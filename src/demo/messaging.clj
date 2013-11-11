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
  (if (= 42 (msg/receive q, :timeout 1000))
    (doall (take 2 messages))))

;;; filter them
(def results (atom []))
(msg/listen q #(swap! results conj (:time %)), :selector "type='date'")
(msg/publish q {:time "now"}, :properties {:type "date"})
(msg/publish q (with-meta {:time (java.util.Date.)} {:type "date"}))
@results

;;; create topic
(def t "topic")
(msg/start t :durable false)

;;; durable topic subscriber
(msg/receive t, :client-id "jim", :timeout 1)
(msg/publish t "just call my name, i'll be there")
(msg/receive t, :client-id "jim")

;;; request/response
(msg/respond q (partial apply +), :selector "op='sum'")
@(msg/request q (with-meta [1 2 16 23] {:op "sum"}))
