(ns demo.remote-messaging-client
  (:require [immutant.messaging :as msg]
            [clojure.string     :as str])
  (:gen-class))

(defn -main
  "Connects to a 'remote' HornetQ running on localhost:5445 and delivers
  the message to the given destination. The first argument is the queue
  name, the rest of the arguments are considered the message. Example:

    lein msg-client foo hi there friends"
  [queue-name & message]
  (with-open [connection (msg/connection :host "localhost" :port 5445)]
    (let [queue (msg/queue queue-name :connection connection)
          message (str/join " " message)]
      (println (format "Sending '%s' to queue %s"
                 message queue-name))
      (msg/publish queue message))))
