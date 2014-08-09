(ns demo.web
  (:require [immutant.web             :as web]
            [immutant.web.middleware  :as immutant]
            [ring.middleware.session-timeout :refer [wrap-idle-session-timeout]]
            [ring.util.response              :refer [response redirect]]
            [clojure.pprint                  :refer (pprint)]))

(defn echo-request
  "Echoes the request back as a string."
  [request]
  (response (with-out-str (pprint request))))

;;; The Ring Session example: https://github.com/ring-clojure/ring/wiki/Sessions
(defn counter [{session :session}]
  (let [count (:count session 0)
        session (assoc session :count (inc count))]
    (println "counter =>" count)
    (-> (response (str "You accessed this page " count " times\n"))
        (assoc :session session))))

(defn -main
  [& {:as args}]

  (web/run echo-request)

  ;; Run counter in "development mode" using Immutant session middleware
  (web/run-dmc (-> counter
                 (wrap-idle-session-timeout
                   {:timeout 20 :timeout-response (redirect "")})
                 immutant/wrap-session)
    :path "/counter"))
