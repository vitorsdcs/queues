(ns queues.exceptions)

(defn unsupported-event [event-name]
  (throw (ex-info "Unsupported event" {:event-name event-name})))

(defn no-agent-found [agent-id]
  (throw (ex-info "No agent found" {:agent-id agent-id})))