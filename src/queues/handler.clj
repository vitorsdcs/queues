(ns queues.handler
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