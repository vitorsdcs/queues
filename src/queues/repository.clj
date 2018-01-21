(ns queues.repository
  (:require [queues.utils :refer :all]))

(defonce agents (atom ()))
(defonce jobs (atom ()))
(defonce assignments (atom ()))

(defn get-agent [id]
  (first (filter #(= (% :id) id) @agents)))

(defn save-agent [agent]
  (swap! agents conj agent))

(defn get-highest-priority-job-by-skillset [skillset]
  (->> @jobs
    (filter #(nil? (% :agent_id)))
    (filter #(in-array (% :type) skillset))
    (sort-by :urgent #(compare %2 %1))
    (first)))

(defn save-job [job]
  (swap! jobs conj job))

(defn get-assignments []
  (-> @assignments reverse))

(defn save-assignment [job agent]
  (swap! assignments conj {:job_id (job :id) :agent_id (agent :id)}))