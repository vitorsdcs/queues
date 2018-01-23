(ns queues.core
  (:require [cheshire.core :refer :all])
  (:require [queues.events :refer :all]
            [queues.repository :refer :all]
            [queues.exceptions :as ex]))

(defn output []
  (->> (get-assignments)
    (map #(conj {} {:job_assigned %}))
    (generate-string)))

(defn input [file]
  (process-events (load-events file))
  (output))

(defn -main
  ([] (ex/no-input-provided))
  ([file]
    (drop-db)
    (print (input file))))