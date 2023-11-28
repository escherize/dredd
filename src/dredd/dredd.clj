(ns dredd.dredd
  (:require
   [dredd.colors :as c]
   [rewrite-clj.zip :as z]
   [clojure.string :as str]))

(def ^:dynamic *verbosity-level* 0)

(defmacro judge
  "Macro to define a judge-y statement. It is evaluatable, and rewritable via
  [[fix-ns!]].

  This does a number of things. When evaluated it returns a tuple of
  [judge/status correct-value], which is useful when calling it from the repl

  The most surprising thing is that it is a marker used to rewrite the contents
  when calling [[fix]] or [[ask]]."
  ([actual] `(judge ~actual :judge/fixme))
  ([actual expected]
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
               (= correct-val expected)  :pass
               (= expected :judge/fixme) :fail
               :else                     :fail))))))

(defonce ^:private *enabled? (atom false))
(defn enable! [] (reset! *enabled? true))
(defn disable! [] (reset! *enabled? false))

(defmacro j [& args]
  (when @*enabled? `(judge ~@args)))

(defn- zipper-fix [filled-zipper correct-val name]
  (println "Dredd is fixing" (c/red (pr-str name)) "to be" (c/green (pr-str correct-val)))
  (-> filled-zipper
      z/down
      z/right
      z/right
      (z/replace correct-val)
      (z/insert-newline-left)
      (z/insert-space-left 3)
      z/up))

(defn- run-failed-ask [name actual expected correct-val zipper noop-zipper arg-count [line col] ns-str]
  (binding [*verbosity-level* 0]
    (let [response (atom "__uninitialized")]
      (println "\n*==================================================")
      (println "*       Fixing judge at:" (str ns-str ":" line (when (not= 1 col) (str ":" col))))
      (println "*       When evaluating:" (pr-str actual))
      (println "*              Expected:" (if (= expected :dredd.dredd/fixme)
                                            "No value was provided"
                                            (c/red (pr-str expected))))
      (println "*      But it should be:" (c/green (pr-str correct-val)))
      (println "*                    ---------")
      (while (not (contains? #{"" "y" "n"} (str/lower-case (or @response "GOTO WHILE"))))
       (println "* Do you want me to fix it? [Y/n]")
       (reset! response (read-line)))
      (if (#{"" "y"} (str/lower-case @response))
        (zipper-fix zipper correct-val name)
        noop-zipper))))

(defn fill-zipper [zipper expected]
  (-> zipper z/down z/right (z/insert-right expected) z/up))

(defn- judge-run
  "Takes a clojure source code file's contents as a string.

  Returns a zipper representing the updated "
  [in {:keys [mode file-path to-find ns-str]
       :or {to-find #{'dredd/j 'd/j 'j}}
       :as _opts}]
  (loop [zipper (z/of-string in {:track-position? true})
         report {:pass [] :fail []}]
    (let [zipper' (z/find-next zipper #(->> % z/down z/sexpr
                                            ;; TODO: figure out how to find this when aliased
                                            (contains? to-find)))]
      (if-not zipper'
        {:zipper zipper :report report}
        ;; - Once we find the next judge form, we are going to shuffle around
        ;;   things so that 1 and 2 arg versions map onto 3 arg versions:
        ;;   *  (judge (+ 1 1)) => (judge autogen-name (+ 1 1) :dredd.dredd/fixme)
        ;;   *  (judge my-name (+ 1 1)) => (judge my-name (+ 1 1) :dredd.dredd/fixme)
        ;;   *  (judge my-name (+ 1 1) :dredd.dredd/fixme) <=> unchanged
        (let [judge-form (z/sexpr zipper')
              arg-count (dec (count judge-form))
              [_judge actual maybe-expected] judge-form
              _ (when (< 2 arg-count) (throw (ex-info "judge can only accept 2 args for now." {})))
              #_#_blank? (= 1 arg-count)
              ;; TODO: get this from zipper.
              [line column] (z/position zipper')
              name (symbol (str "j_" line (when (not= 1 column) (str "_" column))))
              expected (if (= 2 arg-count) maybe-expected :dredd.dredd/fixme)
              #_#_judge-form (if (not blank?) (list _judge actual expected) judge-form)
              filled-zipper (fill-zipper zipper' expected)
              correct-val (eval actual)
              passed? (= correct-val expected)
              zipper''' (case [mode passed?]
                          [:check true] zipper' ;; noop
                          [:check false] zipper' ;; noop

                          [:fix true] zipper' ;; noop
                          [:fix false] (zipper-fix filled-zipper correct-val name)

                          [:ask true] zipper' ;; noop
                          [:ask false] (run-failed-ask
                                         name actual expected
                                         correct-val filled-zipper
                                         zipper' arg-count
                                         [line column]
                                         ns-str))]
          (spit file-path (z/root-string zipper'''))
          (recur zipper''' (update report (if passed? :pass :fail) conj name)))))))

(defn- ns->path [nnamespace]
  (-> nnamespace
      ns-publics ;; symbol -> ns'd symbol
      vals ;; ns'd symbols
      first ;; an ns'd symbol
      meta
      :file))

(defn fix-ns!
  [nnamespace opts]
  (if-let [file-path (ns->path nnamespace)]
    (let [file-contents (first (keep #(try (slurp %) (catch Exception _ nil))
                                     ;; TODO use source-paths
                                     [file-path (str "test/" file-path)]))
          {:keys [zipper report]} (judge-run file-contents (assoc opts :file-path file-path :ns-str (str nnamespace)))]
      (spit file-path (z/root-string zipper))
      report)
    (throw (ex-info "Cannot find file for namespace, you may need to def a public var."
                    {:ns-publix (ns-publics nnamespace)
                     :ns nnamespace}))))

(defmacro check
  "Call to check the truthfullness of `judge` forms in the top level of the current namespace."
  []
  `(do
     ;; TODO this is a hack:
     ;; deffed a harmless thing to be able to find the namespace
     (def sledd# 1)
     (#'fix-ns! *ns* {:mode :check})))

(defmacro fix
  "Call to automatically fix `judge` forms in the top level of the current namespace."
  []
  `(do
     ;; TODO this is a hack:
     ;; deffed a harmless thing to be able to find the namespace
     (def sledd# 1)
     (#'fix-ns! *ns* {:mode :fix})))

(defmacro ask
  "Call to interactively fix `judge` forms in the top level of the current namespace."
  []
  `(do
     ;; TODO this is a hack:
     ;; deffed a harmless thing to be able to find the namespace
     (def sledd# 1)
     (#'fix-ns! *ns* {:mode :ask})))
