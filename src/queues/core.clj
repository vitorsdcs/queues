(ns queues.core
  (:require [cheshire.core :refer :all]))

(def input-json
  (map first (parse-stream (clojure.java.io/reader "sample-input.json") true)))

(def agents
  (->> input-json
    (filter (fn [[k v]] (= k :new_agent)))
    (map second)))

(def jobs
  (->> input-json
    (filter (fn [[k v]] (= k :new_job)))
    (map second)
    (sort-by :urgent #(compare %2 %1))))

(def job-requests
  (->> input-json
    (filter (fn [[k v]] (= k :job_request)))
    (map second)))

(defn -main []
  (println "Agents")
  (doseq [agent agents] (println agent))
  (println "Jobs")
  (doseq [job jobs] (println job))
  (println "Job Requests")
  (doseq [job-request job-requests] (println job-request)))