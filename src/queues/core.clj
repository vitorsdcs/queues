(ns queues.core
  (:require [cheshire.core :refer :all])
  (:require [queues.repository :refer :all]
            [queues.handler :refer :all]
            [queues.exceptions :as ex]))

(defn load-events [input]
  (as-> input input
    (clojure.java.io/reader input)
    (parse-stream input true)
    (map first input)))

(defn process-events [events]
  (doseq [event events]
    (let [event-name (first event) object (second event)]
      (cond
        (= event-name :new_agent) (save-agent object)
        (= event-name :new_job) (save-job object)
        (= event-name :job_request) (handle-job-request object)
        :else (ex/unsupported-event event-name)))))

(defn output []
  (->> (get-assignments)
    (map #(conj {} {:job_assigned %}))
    generate-string))

(defn input [file]
  (process-events (load-events file))
  (output))

(defn -main
  ([] (ex/no-input-provided))
  ([file]
    (print (input file))))