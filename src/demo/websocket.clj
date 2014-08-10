(ns demo.websocket
  (:require [immutant.web             :as web]
            [immutant.web.websocket   :as ws]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response       :refer [redirect]]
            [clojure.pprint           :refer (pprint)]))

;;; WebSocket callback functions
(def callbacks
  {:on-open    (fn [channel handshake]
                 (ws/send! channel "Ready to reverse your messages!"))
   :on-close   (fn [channel {:keys [code reason]}]
                 (println "close code:" code "reason:" reason))
   :on-message (fn [ch m]
                 (ws/send! ch (apply str (reverse m))))})

(def app (-> (fn [r] (redirect (str (:context r) "/websocket.html")))
           ;;; Static resource middleware
           (wrap-resource "public")
           ;;; WebSocket middleware (must come last!)
           (ws/wrap-websocket callbacks)))

(defn -main [& _]
  (web/run app :path "/ws"))
