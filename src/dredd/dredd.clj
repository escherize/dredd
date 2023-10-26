(ns dredd.dredd
  (:require
   [clojure.test :as test]
   [dredd.colors :as c]
   [rewrite-clj.zip :as z]
   [clojure.string :as str]))

(def ^:dynamic *verbosity-level* 0)

(defn logger [n & vs]
  (when (>= n *verbosity-level*) (print (str/join " " vs)) (flush)))

(defmacro judge
  "Macro to define a judge-y statement. It is evaluatable, and rewritable via [[fix-ns!]].

  This does a number of things. When evaluated it returns a tuple of
  [judge/status correct-value], which is useful when calling it from the repl

  The most surprising thing is that it is a marker for [[fix-ns!]] which will rewrite its contents."
  {:clj-kondo/lint-as 'clj-kondo.lint-as/def-catch-app}
  ([actual] (let [{:keys [line column] :as j} (meta &form)]
              `(judge ~(symbol (str "judge__auto_" line "_" column)) ~actual :judge/fixme)))
  ([name actual] `(judge ~name ~actual :judge/fixme))
  ([name actual expected]
   (let [{:keys [line column]} (meta &form)]
     `(do (def
            ~(vary-meta (symbol (str "judge__" name)) assoc
                        :private true
                        :line line
                        :column column
                        :judge true)
            (= ~expected ~actual))
          ~(let [correct-val (eval actual)]
             (cond
               (= correct-val expected)  (do #_(logger 1 #_"[" "✅" #_#_#_name "on line: " line #_"]") [:judge/passed correct-val])
               (= expected :judge/fixme) (do #_(logger 1 #_"[" "😶" #_#_#_name "on line: " line #_"]") [:judge/filled correct-val])
               :else                   (do #_(logger 1 #_"[" "❌" #_#_#_name "on line: " line #_"]") [:judge/failed correct-val])))))))

(defn rand-name []
  (c/rand-name (comp #(str/replace % #" " "-") str/lower-case)))

(defn- run-failed-ask [name actual expected correct-val zipper noop-zipper arg-count]
  (binding [*verbosity-level* 0]
    (let [response (atom "__uninitialized")]
      (println "\n*==================================================")
      (println "*      Fixing judge for: " name (if (= 1 arg-count) "(autgen'd name)" ""))
      (println "*       When evaluating: " (pr-str actual))
      (println "*                   Got: " (if (= expected :dredd.dredd/fixme)
                                             "(No value was there)"
                                             (c/red (pr-str expected))))
      (println "*      But it should be: " (c/green (pr-str correct-val)))
      (println "*                    ---------")
      (while (not (contains? #{"" "y" "n"} (str/lower-case (or @response "GOTO WHILE"))))
        (println "* Do you want me to fix it? [Y/n]")
        (reset! response (read-line)))
      (def zipper zipper)
      (def arg-count arg-count)
      (if (#{"" "y"} (str/lower-case @response))
        (-> zipper z/down z/right z/right z/right (z/replace correct-val) z/up)
        noop-zipper))))

(defn fill-zipper [zipper name expected arg-count]
  #_(def zipper zipper)
  #_(def name name)
  #_(def expected expected)
  #_(def actual actual)
  #_(def arg-count arg-count)
  (case arg-count
    1 (-> zipper z/down (z/insert-right name) z/right z/right (z/insert-right expected) z/up)
    2 (-> zipper z/down z/right z/right (z/insert-right expected) z/up)
    3 (-> zipper z/down z/right z/right z/right (z/replace expected) z/up)))

(defn- judge-run [in {:keys [mode file-path] :as _opts}]
  (loop [zipper (z/of-string in {:track-position? true})
         report {:judge/passed []
                 :judge/failed []
                 :judge/filled []}]
    (let [zipper' (z/find-next zipper #(->> % z/down z/sexpr
                                            ;; TODO: figure out how to find this when aliased
                                            (contains? #{'judge 'dredd/judge})))]
      (if-not zipper'
        {:zipper zipper :report report}
        ;; - Once we find the next judge form, we are going to shuffle around
        ;;   things so that 1 and 2 arg versions map onto 3 arg versions:
        ;;   *  (judge (+ 1 1)) => (judge autogen-name (+ 1 1) :dredd.dredd/fixme)
        ;;   *  (judge my-name (+ 1 1)) => (judge my-name (+ 1 1) :dredd.dredd/fixme)
        ;;   *  (judge my-name (+ 1 1) :dredd.dredd/fixme) <=> unchanged
        (let [judge-form (z/sexpr zipper')
              arg-count (dec (count judge-form))
              [_judge name-or-actual actual expected] judge-form
              anon? (= 1 arg-count)
              actual (if anon? name-or-actual actual)
              name (if anon? (symbol (rand-name)) name-or-actual)
              expected (if (= 3 arg-count) expected :dredd.dredd/fixme)
              judge-form (if (not anon?) judge-form (list _judge name actual expected))
              filled-zipper (fill-zipper zipper' name expected arg-count)
              [status correct-val] (eval judge-form)
              zipper''' (case [mode (= correct-val expected)]
                          [:fix true] zipper' ;; noop
                          [:fix false] (do
                                         (println "I'm Fixing" name "to be" correct-val)
                                         (-> filled-zipper z/down z/right z/right z/right (z/replace correct-val) z/up))

                          [:ask true] zipper' ;; noop
                          [:ask false] (run-failed-ask name actual expected correct-val filled-zipper zipper' arg-count))]
          (spit file-path (z/root-string zipper'''))
          (recur zipper''' (update report status conj name)))))))

(defn- ns->path [nnamespace]
  (:file (meta (first (vals (ns-publics nnamespace))))))

(defn fix-ns!
  ([nnamespace] (fix-ns! nnamespace {}))
  ([nnamespace opts]
   (if-let [file-path (ns->path nnamespace)]
     (let [file-contents (first (keep #(try (slurp %) (catch Exception _ nil))
                                      [file-path (str "test/" file-path)]))
           {:keys [zipper report]} (judge-run file-contents (assoc opts :file-path file-path))]
       (spit file-path (z/root-string zipper))
       report)
     (throw (ex-info "Cannot find file for namespace, please def a public var."
                     {:ns-publix (ns-publics nnamespace)
                      :ns nnamespace})))))

(defmacro fix
  "Call to automatically fix `judge` forms.
  (fix :ask)
  "
  []
  `(do
     ;; TODO this is a hack:
     ;; deffed a harmless thing to be able to find the namespace
     (def sledd# 1)
     (#'fix-ns! *ns* {:mode :fix})))

(defmacro ask
  "Call to interactively fix `judge` forms.
  By default automatically fixes them. To interactively fix them, call via:

    (fix! :ask)
  "
  []
  `(do
     ;; TODO this is a hack:
     ;; deffed a harmless thing to be able to find the namespace
     (def sledd# 1)
     (#'fix-ns! *ns* {:mode :ask})))
