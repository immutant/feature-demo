(ns demo.messaging
  (:require [clojure.pprint     :refer [pprint]]
            [immutant.messaging :as msg]))

(defn listener
  "A simple message listener"
  [m]
  (println "listener received" m))

(defn -main [& {:as args}]
  ;; using a listener
  (let [a-queue (msg/queue "my-queue")]
    (msg/listen a-queue listener)
    (dotimes [n 10]
      (msg/publish a-queue {:message n})))

  ;; using synchronous messaging (request/respond)
  (let [sync-queue (msg/queue "sync-queue")]
    (msg/respond sync-queue inc)
    (loop [n 0]
      (let [response @(msg/request sync-queue n)]
        (println "response is:" response)
        (when (< response 10)
          (recur response))))))
