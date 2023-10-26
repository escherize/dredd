(ns dredd.dredd-test
  (:require
   [clojure.test :refer [deftest is]]
   [dredd.dredd :as dredd]))

(deftest basic-judge-one-argument-test
  (is (= [:judge/filled 3] (dredd/judge (+ 1 2)))
      "filled when no name and no expected"))

(deftest basic-judge-two-argument-test
  (is (= [:judge/filled 3] (dredd/judge one (+ 1 2)))
      "filled when name and no expected"))

(deftest basic-judge-three-argument-test
  (is (= [:judge/passed 3] (dredd/judge one (+ 1 2) 3))
      "passed with name expected and actual")
  (is (= [:judge/failed 3] (dredd/judge one (+ 1 2) 4))
      "failed with name expected and actual"))


;; one arg
(dredd/judge copper-mangosteen (* 10 10) 100)
;;           ^ expression to check

;; two args
(dredd/judge hard-math (* 234 593 -3) -416286)
;;           ^ added name

;; three args
(dredd/judge two-plus-2 (+ 2 2) 4)
;;         added expected value ^

(comment
  (dredd/fix)

  ;; 1 arg
  ;; [expected]
  ;; 
  ;; This will always need fixing, and fixing it will fill in so we get to 3
  ;; args.

  ;; 2 args
  ;; [name expected]

  ;; 3 args
  ;; [name actual expected]


  ;; matrix is:
  (vec (for [mode [:fix :ask]
             args [:1-arg :2-args :3-args]
             right? [:correct :incorrect]]
         [mode args right?]))
  [[:fix :1-arg :incorrect]
   [:fix :2-args :incorrect]
   [:fix :3-args :correct]
   [:fix :3-args :incorrect]
   [:ask :1-arg :incorrect]
   [:ask :2-args :incorrect]
   [:ask :3-args :correct]
   [:ask :3-args :incorrect]]


  )
