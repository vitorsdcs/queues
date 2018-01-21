(ns queues.core
  (:refer-clojure :exclude [sort find])
  (:require [cheshire.core :refer :all])
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :refer :all]
            [monger.operators :refer :all]))

(declare clean-db)
(declare load-events)
(declare process-events)
(declare output)
(declare get-agent)
(declare save-agent)
(declare get-fittest-job)
(declare save-job)
(declare get-assignments)
(declare save-assignment)
(declare assign-job)
(declare to-document)
(declare to-object)
(declare to-assignment)

(defn -main []
  (let [conn (mg/connect {:host "queues-database"}), db (mg/get-db conn "queues")]
    (clean-db db)
    (process-events db (load-events))
    (output db)))

(defn clean-db [db]
  (mc/remove db "agents")
  (mc/remove db "jobs")
  (mc/remove db "assignments"))

(defn load-events []
  (as-> "sample-input.json" input
    (clojure.java.io/reader input)
    (parse-stream input true)
    (map first input)))

(defn process-events [db events]
  (doseq [event events]
    (let [event-name (first event) object (second event)]
      (cond
        (= event-name :new_agent) (save-agent db object)
        (= event-name :new_job) (save-job db object)
        (= event-name :job_request) (assign-job db object)))))

(defn output [db]
  (->> (get-assignments db)
    (map #(conj {} {:job_assigned %}))
    (generate-string)
    (println)))

(defn get-agent [db id]
  (let [agent (mc/find-one-as-map db "agents" {:_id id})]
    (if (some? agent)
      (to-object agent))))

(defn save-agent [db agent]
  (println (str "Adding agent " (agent :id)))
  (mc/update db "agents" {:_id (agent :id)} {$set (to-document agent)} {:upsert true}))

(defn get-fittest-job [db agent]
  (let [job (first (with-collection db "jobs" (find {:agent_id nil}) (sort (array-map :urgent -1)) (limit 1)))]
    (if (some? job)
      (to-object job))))

(defn save-job [db job]
  (println (str "Adding job " (job :id)))
  (mc/update db "jobs" {:_id (job :id)} {$set (to-document job)} {:upsert true}))

(defn get-assignments [db]
  (map #(to-assignment %) (mc/find-maps db "assignments")))

(defn save-assignment [db job agent]
  (println (str "Assigning job " (job :id)  " to agent " (agent :id)))
  (mc/insert db "assignments" {:job_id (job :id) :agent_id (agent :id)}))

(defn assign-job [db job-request]
  (let [agent (get-agent db (job-request :agent_id))]
    (if (some? agent)
      (let [job (get-fittest-job db agent)]
        (if (some? job)
          (do
            (save-job db (conj job {:agent_id (agent :id)}))
            (save-assignment db job agent))
          (println (str "No job available for agent " (agent :id)))))
      (println (str "No agent with id " (job-request :agent_id) " found")))))

(defn to-document [object]
  (dissoc (conj object {:_id (object :id)}) :id))

(defn to-object [document]
  (dissoc (conj document {:id (document :_id)}) :_id))

(defn to-assignment [document]
  (dissoc document :_id))