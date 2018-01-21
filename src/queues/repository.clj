(ns queues.repository
  (:refer-clojure :exclude [sort find])
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :refer :all]
            [monger.operators :refer :all]))

(def db
  (mg/get-db (mg/connect {:host "queues-database"}) "queues"))

(defn to-document [object]
  (dissoc (conj object {:_id (object :id)}) :id))

(defn to-object [document]
  (dissoc (conj document {:id (document :_id)}) :_id))

(defn get-agent [id]
  (some->> (mc/find-one-as-map db "agents" {:_id id})
    to-object))

(defn save-agent [agent]
  (mc/update db "agents" {:_id (agent :id)} {$set (to-document agent)} {:upsert true}))

(defn remove-agents []
  (mc/remove db "agents"))

(defn get-highest-priority-job-by-type [type]
  (some->> (with-collection db "jobs" (find {:agent_id nil :type {$in type}}) (sort (array-map :urgent -1)) (limit 1))
    first
    to-object))

(defn save-job [job]
  (mc/update db "jobs" {:_id (job :id)} {$set (to-document job)} {:upsert true}))

(defn remove-jobs []
  (mc/remove db "jobs"))

(defn get-assignments []
  (->> (mc/find-maps db "assignments")
    (map #(dissoc % :_id))))

(defn save-assignment [job agent]
  (mc/insert db "assignments" {:job_id (job :id) :agent_id (agent :id)}))

(defn remove-assignments []
  (mc/remove db "assignments"))