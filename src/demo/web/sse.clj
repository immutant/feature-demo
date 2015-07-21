(ns demo.web.sse
  "Demonstrate Server Sent Events"
  (:require [immutant.web.sse :as sse]))

(defn countdown
  "Countdown from 5 every half-second"
  [request]
  (sse/as-channel request
    {:on-open (fn [ch]
                (doseq [x (range 5 0 -1)]
                  (sse/send! ch x)
                  (Thread/sleep 500))
                ;; Signal the client to call EventSource.close()
                (sse/send! ch {:event "close", :data "bye!"}))}))

