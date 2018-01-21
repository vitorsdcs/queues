(ns queues.core
  (:require [cheshire.core :refer :all])
  (:require [queues.repository :refer :all]
            [queues.exceptions :as ex]))

(defn find-job [agent]
  (or (get-highest-priority-job-by-skillset (agent :primary_skillset))
      (get-highest-priority-job-by-skillset (agent :secondary_skillset))))

(defn assign-job [job agent]
  (save-job (conj job {:agent_id (agent :id)}))
  (save-assignment job agent))

(defn handle-job-request [job-request]
  (let [agent (get-agent (job-request :agent_id))]
    (if (some? agent)
      (let [job (find-job agent)]
        (if (some? job)
          (assign-job job agent)))
      (ex/no-agent-found (job-request :agent_id)))))

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