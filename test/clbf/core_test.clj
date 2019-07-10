(ns clbf.core-test
  (:require [clojure.test :refer :all]
            [clbf.core :refer :all]))

(defn eval-string
  "Helper function to test-evaluate BF code"
  [src-code]
  (clbf.core/clbf-eval (clbf.core/clbf-read src-code)))

(deftest reader-test-single-symbol
  (testing "Test Reader '+'"
    (is (= (clbf.core/clbf-read "+") '(:add)))))

(deftest reader-test-mismatched
  (testing "Test Reader '['"
    (is (= (clbf.core/clbf-read "[") '(:add)))))
