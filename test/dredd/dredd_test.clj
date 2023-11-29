(ns dredd.dredd-test
  {:clj-kondo/config '{:linters {:unresolved-symbol {:level :off}}}}
  (:require
   [clojure.test :refer [deftest is]]
   [clojure.walk :as walk]
   [dredd.dredd :as dredd :refer [j]]))

(deftest basic-judge-disabled-test
  (dredd/disable!)
  (is (= false @@#'dredd/*enabled?))
  (is (= nil (j (+ 1 2))) "no output")
  (is (= nil (j (+ 1 2) 3)) "no output")
  (is (= nil (j (+ 1 2) 4)) "no output"))

(deftest basic-judge-enabled-test
  (dredd/enable!)
  (is (= true @@#'dredd/*enabled?))
  (is (= :fail (j (+ 1 2)))
      "fails when given only an expression")
  (is (= :pass (j (+ 1 2) 3))
      "passed with expected and actual")
  (is (= :fail (j (+ 1 2) 4))
      "failed with expected and actual"))

;; correct me
(j (* 10 10) 100)
(j (* 2 3) 6)
(j (* 10 -4) -40)

(comment
  (dredd/enable!)
  (dredd/ask)

  )
