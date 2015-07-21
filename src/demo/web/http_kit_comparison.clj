(ns demo.web.http-kit-comparison
  "Show API differences between Immutant and HTTP Kit"
  (:require [immutant.web             :as web]
            [immutant.web.async       :as async]))

#_(defn async-handler
  "For comparison, the actual http-kit example"
  [ring-request]
  ;; unified API for WebSocket and HTTP long polling/streaming
  (with-channel ring-request channel    ; get the channel
    (if (websocket? channel)            ; if you want to distinguish them
      (on-receive channel (fn [data]     ; two way communication
                            (send! channel data)))
      (send! channel {:status 200
                      :headers {"Content-Type" "text/plain"}
                      :body    "Long polling?"}))))

(defn async-handler
  "Immutant version of http-kit example showing websocket degrade

  Differences:
  - Channel passed as callback param instead of macro param
  - send! provides :on-success and :on-error callbacks
  - Channel not closed after send, by default
  - All ring body types supported
  - Automatic chunking of large (>16K) content"
  [ring-request]
  (async/as-channel ring-request
    {:on-message (fn [channel data]
                   (async/send! channel data))
     :on-open    (fn [channel]
                   (when-not (:websocket? ring-request)
                     (async/send! channel {:status 200
                                           :headers {"Content-Type" "text/plain"}
                                           :body    "Long polling?"}
                       :close? true)))}))

