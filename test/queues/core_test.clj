(ns queues.core-test
  (:require [clojure.test :refer :all]
            [queues.core :refer :all]
            [cheshire.core :refer :all]))

(deftest sample-input
  (let [sample-output (generate-string (parse-string (slurp "sample-output.json")))]
    (is (= sample-output (input "sample-input.json")))))