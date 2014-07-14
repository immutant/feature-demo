(ns demo.remote-messaging-client
  (:require [immutant.messaging :as msg]
            [clojure.string     :as str])
  (:gen-class))

(defn -main
  "Connects to a 'remote' HornetQ running on localhost:5445 and delivers
  the message to the given destination. The destination name must be
  prefixed with the type (either 'queue' or 'topic') and a colon. The
  rest of the arguments are considered the message. Example:

    lein msg-client queue:foo hi there friends"
  [dest-type:name & message]
  (with-open [connection (msg/connection :host "localhost" :port 5445)]
    (let [[dest-type dest-name] (str/split dest-type:name #":")
          destination ((if (= dest-type "queue")
                         msg/queue msg/topic)
                       dest-name
                       :connection connection)
          message (str/join " " message)]
      (println (format "Sending '%s' to %s %s"
                 message dest-type dest-name))
      (msg/publish destination  message))))
