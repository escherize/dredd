(ns dredd.dredd
  (:require
   [clojure.test :as test]
   [dredd.colors :as c]
   [rewrite-clj.zip :as z]
   [clojure.string :as str]))

(defmacro judge
  "Macro to define a judgemental statement.

  It evaluates down to a simple deftest with is, and when compiled returns a tuple of
  [judge/status correct-value], which is useful when calling it from the repl"
  ([name actual] `(judge ~name ~actual :judge/fixme))
  ([name actual expected]
   (let [{:keys [line]} (meta &form)]
     `(do (test/deftest
            ~(vary-meta name assoc
                        :private true
                        :line line
                        :judge true)
            (test/is (= ~expected ~actual)))
          ~(let [correct-val (eval actual)]
             (cond
               (= correct-val expected)  [:judge/passed correct-val]
               (= expected :judge/fixme) [:judge/filled correct-val]
               :else                     [:judge/failed correct-val]))))))

(defn- run-failed-ask [actual expected correct-val zipper']
  (let [response (atom "__uninitialized")]
                           (println "Incorrect value for: " (pr-str actual))
                           (println "Found:  " (c/red (pr-str expected)))
                           (println "Correct:"(c/green (pr-str correct-val)))
                           (while (not (contains? #{"" "y" "n"} (str/lower-case @response)))
                             (println "Fix it? [Y/n]")
                             (reset! response (read-line)))
                           (if (#{"" "y"} (str/lower-case @response))
                             (do (println "fixing...")
                                 (-> zipper' z/down z/right z/right z/right (z/replace correct-val) z/up))
                             (do (println "not fixing...")
                                 zipper'))))

(defn- run-filled-ask [actual correct-val zipper']
  (let [response (atom "__uninitialized")]
                           (println "Missing value for: " (pr-str actual))
                           (println "Correct:" (c/green (pr-str correct-val)))
                           (while (not (contains? #{"" "y" "n"} (str/lower-case @response)))
                             (println "Fill it? [Y/n]")
                             (reset! response (read-line)))
                           (if (#{"" "y"} (str/lower-case @response))
                             (do (println "fixing...")
                                 (-> zipper' z/down z/right z/right (z/insert-right correct-val) z/up))
                             (do (println "not fixing...")
                                 zipper'))))

(defn- judge-run [in {:keys [mode] :as opts}]
  (loop [zipper      (z/of-string in {:track-position? true})
         report {:judge/passed []
                 :judge/failed []
                 :judge/filled []}]
    (let [zipper' (z/find-next zipper #(-> % z/down z/sexpr
                                           ;; TODO: how to find this when aliased?
                                           (#{'judge 'dredd/judge})))]
      (if-not zipper'
        {:zipper zipper :report report}
        (let [judge-form (z/sexpr zipper')
              [_ name actual expected] judge-form
              [status correct-val] (eval judge-form)
              zipper'' (case [mode status]
                         [:fix :judge/passed] zipper'
                         [:fix :judge/failed] (-> zipper' z/down z/right z/right
                                                  z/right (z/replace correct-val) z/up)
                         [:fix :judge/filled] (-> zipper' z/down z/right z/right
                                                  (z/insert-right correct-val) z/up)

                         [:ask :judge/passed] zipper'
                         [:ask :judge/failed] (run-failed-ask actual expected correct-val zipper')
                         [:ask :judge/filled] (run-filled-ask actual correct-val zipper'))]
          (recur zipper'' (update report status conj name)))))))

(defn- ns->path [ns]
  (:file (meta (first (vals (ns-publics ns))))))

(defn- check-ns [ns]
  (if-let [file-path (ns->path ns)]
    (:report (judge-run (slurp file-path) {:mode :fix}))
    (throw (ex-info "Cannot find file for namespace." {:ns ns}))))

(defn- fix-ns!
  ([ns] (fix-ns! ns {}))
  ([ns {:keys [mode] :or {mode :fix} :as opts}]
   (if-let [file-path (ns->path ns)]
     (let [{:keys [zipper report]} (judge-run (slurp file-path) opts)]
       (spit file-path (z/root-string zipper))
       report)
     (throw (ex-info "Cannot find file for namespace." {:ns ns})))))

(defn fix!
  "Call to automatically, or interactively fix `judge` forms.
  By default automatically fixes them. To interactively fix them, call via:

    (fix! :ask)
  "
  ([] (fix-ns! *ns* {:mode :fix}))
  ([mode] (fix-ns! *ns* {:mode mode})))
