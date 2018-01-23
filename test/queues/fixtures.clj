(ns queues.fixtures
  (:require [clojure.test :refer :all]
            [queues.repository :refer :all]))

(defn fixture [f]
    (drop-db)
    (f)
    (drop-db))