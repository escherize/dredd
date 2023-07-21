(ns dredd.dredd-test
  (:require
   [clojure.java.io :as io]
   [clojure.test :refer [deftest is]]
   [dredd.dredd :as dredd]))

(deftest judge-output-test
  (let [pass (dredd/judge one (vec (range 3)) [0 1 2])]
    (is (= [:judge/passed [0 1 2]] pass)))

  (let [fail (dredd/judge two (vec (range 3)) [])]
    (is (= [:judge/failed [0 1 2]] fail)))

  (let [fill (dredd/judge three (vec (range 3)))]
    (is (= [:judge/filled [0 1 2]] fill))))

(def test-fill-content
  "(ns dredd.dredd-test-fill
  (:require [dredd.dredd :as dredd]))

(def x 1)

(dredd/judge passing-test (+ 1 1) 2)
(dredd/judge failing-test (+ 1 1) 0)
(dredd/judge filling-test (+ 1 1))
")

(def test-fill-output
  "(ns dredd.dredd-test-fill
  (:require [dredd.dredd :as dredd]))

(def x 1)

(dredd/judge passing-test (+ 1 1) 2)
(dredd/judge failing-test (+ 1 1) 2)
(dredd/judge filling-test (+ 1 1) 2)
")

(deftest fix-ns!-fill-test
  (spit "test/dredd/dredd_test_fill.clj" test-fill-content)
  (require '[dredd.dredd-test-fill :as dtf])
  (is (= {:judge/passed '[passing-test]
          :judge/failed '[failing-test]
          :judge/filled '[filling-test]}
         (dredd/fix-ns! 'dredd.dredd-test-fill {:mode :fix})))
  (is (= test-fill-output (slurp "test/dredd/dredd_test_fill.clj")))
  (io/delete-file "test/dredd/dredd_test_fill.clj"))
