(ns queues.events
  (:require [cheshire.core :refer :all])
  (:require [queues.repository :refer :all]
            [queues.exceptions :as ex]))

(defn load-events [input]
  (as-> input input
    (clojure.java.io/reader input)
    (parse-stream input true)
    (map first input)))

(defn process-events [events]
  (doseq [event events]
    (let [event-name (key event) object (val event)]
      (cond
        (= event-name :new_agent) (save-agent object)
        (= event-name :new_job) (save-job object)
        (= event-name :job_request) (save-job-request object)
        :else (ex/unsupported-event event-name)))))