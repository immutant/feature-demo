(ns demo.messaging
  (:require [clojure.pprint     :refer [pprint]]
            [immutant.messaging :as msg]))

(defn listener
  "A simple message listener"
  [m]
  (println "listener received" m))

(defn -main [& {:as args}]

  ;; msg/queue creates a queue in HornetQ if it does not already exist
  ;; returns a reference to the queue
  (let [a-queue (msg/queue "my-queue")]

    ;; registers a fn to be called each time a message comes in
    (msg/listen a-queue listener)

    ;; sends 10 messages to the queue
    (dotimes [n 10]
      (msg/publish a-queue {:message n}))

    ;; default encoding is :edn. Other options are: :edn, :fressian, :json, :none
    ;; :json requires cheshire, :fressian requires data.fressian
    (msg/publish a-queue {:message :hi} :encoding :json))

  ;; using synchronous messaging (request/respond)
  (let [sync-queue (msg/queue "sync-queue")]

    ;; registers a fn as a responder - a listener who's return value
    ;; is sent to the requester
    (msg/respond sync-queue inc)

    (dotimes [n 5]
      ;; request returns a j.u.c.Future
      (println "response is:"
        @(msg/request sync-queue n)))))
