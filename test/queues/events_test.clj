(ns queues.events-test
  (:require [clojure.test :refer :all]
            [queues.events :refer :all]
            [queues.fixtures :refer :all]
            [cheshire.core :refer :all]))

(use-fixtures :each fixture)

(deftest events-loaded
  (is (seq? (load-events "sample-input.json"))))

(deftest invalid-events-file
  (is (thrown? Exception (load-events "i-dont-exist.json"))))