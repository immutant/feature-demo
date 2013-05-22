(ns demo.messaging
  (:require [immutant.messaging :as msg]))

;;; create queue, if not already
(def q "queue")
(msg/start q :durable false)

;;; publish some messages
(msg/publish q :ping)
(msg/publish q {:a 1, :b [1 2 3]})
(msg/publish q 42, :priority :high, :ttl 30000)

;;; consume them
(let [messages (msg/message-seq q)]
  (if (= 42 (msg/receive q))
    (take 2 messages)))

;;; filter them
(def results (atom []))
(msg/listen q #(swap! results conj (:time %)), :selector "type='date'")
(msg/publish q (with-meta {:time (java.util.Date.)} {:type "date"}))
@results

;;; create topic
(def t "topic")
(msg/start t :durable false)

;;; durable topic subscriber
(msg/receive t, :client-id "jim", :timeout 1)
(msg/publish t "just call my name, i'll be there")
(swap! results conj (msg/receive t, :client-id "jim"))
@results

;;; request/response
(msg/respond q (partial apply +), :selector "op='sum'")
(swap! results conj @(msg/request q (with-meta [1 2 3 4 5] {:op "sum"})))
@results
