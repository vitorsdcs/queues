(ns queues.repository-test
  (:require [clojure.test :refer :all]
            [queues.core :refer :all]
            [queues.exceptions :as ex]
            [queues.fixtures :refer :all]
            [queues.repository :refer :all]
            [cheshire.core :refer :all]))

(use-fixtures :each fixture)

(deftest agent-created
  (save-agent! {:id 1 :name "BoJack Horseman" :primary_skillset ["bills-questions"] :secondary_skillset []})
  (is (= 1 (count @agents)))
  (is (= 1 ((val (first @agents)) :id))))

(deftest job-created
  (save-job! {:id 1 :type "bills-questions" :urgent false})
  (is (= 1 (count @jobs)))
  (is (= 1 ((val (first @jobs)) :id))))

(deftest job-assigned
  (save-agent! {:id 1 :name "BoJack Horseman" :primary_skillset ["bills-questions"] :secondary_skillset []})
  (save-job! {:id 1 :type "bills-questions" :urgent false})
  (save-job-request {:agent_id 1})
  (is (= 1 (count @assignments))))

(deftest job-assigned-first-due-to-urgency
  (save-agent! {:id 1 :name "BoJack Horseman" :primary_skillset ["bills-questions"] :secondary_skillset []})
  (save-job! {:id 1 :type "bills-questions" :urgent false})
  (save-job! {:id 2 :type "bills-questions" :urgent true})
  (save-job-request {:agent_id 1})
  (is (= 2 ((val (first @assignments)) :job_id))))

(deftest job-assigned-according-to-skillset-priority
  (save-agent! {:id 1 :name "Mr. Peanut Butter" :primary_skillset ["rewards-question"] :secondary_skillset ["bills-questions"]})
  (save-job! {:id 1 :type "waiting-list" :urgent false})
  (save-job! {:id 2 :type "bills-questions" :urgent false})
  (save-job-request {:agent_id 1})
  (is (= 2 ((val (first @assignments)) :job_id))))

(deftest job-not-assigned-due-to-agent-skillset
  (save-agent! {:id 1 :name "BoJack Horseman" :primary_skillset ["bills-questions"] :secondary_skillset []})
  (save-job! {:id 1 :type "rewards-question" :urgent false})
  (save-job-request {:agent_id 1})
  (is (= 0 (count @assignments))))

(deftest job-not-assigned-due-to-empty-list
  (save-agent! {:id 1 :name "BoJack Horseman" :primary_skillset ["bills-questions"] :secondary_skillset []})
  (save-job-request {:agent_id 1})
  (is (= 0 (count @assignments))))

(deftest agent-not-found
  (is (thrown-with-msg? Exception #".*No agent found*" (save-job-request {:agent_id 1}))))