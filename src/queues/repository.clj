(ns queues.repository
  (:refer-clojure :exclude [sort find])
  (:require [queues.exceptions :as ex]
            [queues.utils :refer :all]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :refer :all]
            [monger.operators :refer :all]))

(def conn (mg/connect))
(def dbname "queues")
(def db (mg/get-db conn dbname))

(defn to-document [object]
  (dissoc (conj object {:_id (object :id)}) :id))

(defn to-object [document]
  (dissoc (conj document {:id (document :_id)}) :_id))

(defn get-agents []
  (map to-object (mc/find-maps db "agents")))

(defn get-agent [id]
  (some->> (mc/find-one-as-map db "agents" {:_id id})
    to-object))

(defn save-agent [agent]
  (mc/update db "agents" {:_id (agent :id)} {$set (to-document agent)} {:upsert true}))

(defn get-jobs []
  (map to-object (mc/find-maps db "jobs")))

(defn get-highest-priority-unassigned-job-by-skillset [skillset]
  (some->> (with-collection db "jobs" (find {:agent_id nil :type {$in skillset}}) (sort (array-map :urgent -1)) (limit 1))
    first
    to-object))

(defn get-highest-priority-unassigned-job-by-agent [agent]
  (or (get-highest-priority-unassigned-job-by-skillset (agent :primary_skillset))
      (get-highest-priority-unassigned-job-by-skillset (agent :secondary_skillset))))

(defn save-job [job]
  (mc/update db "jobs" {:_id (job :id)} {$set (to-document job)} {:upsert true}))

(defn get-assignments []
  (->> (mc/find-maps db "assignments")
    (map #(dissoc % :_id))))

(defn save-assignment [job agent]
  (mc/insert db "assignments" {:job_id (job :id) :agent_id (agent :id)}))

(defn save-job-request [job-request]
  (let [agent (get-agent (job-request :agent_id))]
    (if (some? agent)
      (let [job (get-highest-priority-unassigned-job-by-agent agent)]
        (if (some? job)
          (do
            (save-job (conj job {:agent_id (agent :id)}))
            (save-assignment job agent))))
      (ex/no-agent-found (job-request :agent_id)))))

(defn drop-db []
  (mg/drop-db conn dbname))