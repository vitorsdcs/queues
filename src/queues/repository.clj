(ns queues.repository
  (:require [queues.exceptions :as ex]
            [queues.utils :refer :all]))

(defonce agents (atom (array-map)))
(defonce jobs (atom (array-map)))
(defonce assignments (atom (array-map)))
(defonce id-assignments (atom 0))

(defn get-agent [id]
  (@agents id))

(defn save-agent! [agent]
  (swap! agents assoc (agent :id) agent))

(defn get-highest-priority-unassigned-job-by-skillset [skillset]
  (->> @jobs
    (vals)
    (filter #(nil? (% :agent_id)))
    (filter #(in-array (% :type) skillset))
    (sort-by :urgent #(compare %2 %1))
    (first)))

(defn get-highest-priority-unassigned-job-by-agent [agent]
  (or (get-highest-priority-unassigned-job-by-skillset (agent :primary_skillset))
      (get-highest-priority-unassigned-job-by-skillset (agent :secondary_skillset))))

(defn save-job! [job]
  (swap! jobs assoc (job :id) job))

(defn get-assignments []
  (-> @assignments vals))

(defn save-assignment! [job agent]
  (let [id (swap! id-assignments inc)]
    (swap! assignments assoc id {:job_id (job :id) :agent_id (agent :id)})))

(defn save-job-request [job-request]
  (let [agent (get-agent (job-request :agent_id))]
    (if (some? agent)
      (let [job (get-highest-priority-unassigned-job-by-agent agent)]
        (if (some? job)
          (do
            (save-job! (conj job {:agent_id (agent :id)}))
            (save-assignment! job agent))))
      (ex/no-agent-found (job-request :agent_id)))))