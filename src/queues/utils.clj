(ns queues.utils)

(defn in-array [val array]
  (some #(= val %) array))