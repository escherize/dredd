(ns dredd.dredd-test
  {:clj-kondo/config '{:linters {:unresolved-symbol {:level :off}}}}
  (:require
   [clojure.test :refer [deftest is]]
   [clojure.walk :as walk]
   [dredd.dredd :as dredd :refer [j]]))

(clojure.test/use-fixtures
  :each
  (fn [f] (dredd/enable!) (f)))

(deftest basic-judge-one-argument-test
  (is (= :fail (dredd/j (+ 1 2)))
      "fails when given only an expression"))

(deftest basic-judge-two-argument-test
  (is (= :pass (j (+ 1 2) 3))
      "passed with name expected and actual")
  (is (= :fail (j (+ 1 2) 4))
      "failed with name expected and actual"))

;; one arg
(j (* 10 10) 
   100)
(j (* 2 3) 
   6 "wrong")
(j (* 10 -4) -40)

(comment
  (dredd/ask)

  )
