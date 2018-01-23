(ns queues.fixtures
  (:require [clojure.test :refer :all]
            [queues.repository :refer :all]))

(defn fixture [f]
    (reset! agents {})
    (reset! jobs {})
    (reset! assignments {})
    (reset! id-assignments 0)
    (f)
    (reset! agents {})
    (reset! jobs {})
    (reset! assignments {})
    (reset! id-assignments 0))