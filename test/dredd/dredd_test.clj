(ns dredd.dredd-test
  (:require [clojure.test :refer :all]
            [dredd.dredd :as dredd]))

(deftest test-fix-results
  (dredd/judge one (vec (range 3)) [0 1 2])
  (is (= #:judge{:passed [], :failed [], :filled []}
         (dredd/fix!))))

