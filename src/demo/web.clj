(ns demo.web
  (:require [immutant.web             :as web]
            [ring.middleware.resource :as ring-resource]
            [ring.util.response       :as ring-util]
            [immutant.web.servlet     :refer (create-servlet)]
            [clojure.pprint           :refer (pprint)]))

(defn echo-request
  "Echoes the request back as a string."
  [request]
  (ring-util/response (with-out-str (pprint request))))

;;; The Ring Session example: https://github.com/ring-clojure/ring/wiki/Sessions
(defn counter [{session :session}]
  (let [count (:count session 0)
        session (assoc session :count (inc count))]
    (println "counter =>" count)
    (-> (ring-util/response (str "You accessed this page " count " times\n"))
        (assoc :session session))))

(defn -main
  [& {:as args}]

  ;; this uses an Undertow handler by default, which is faster than a servlet
  (web/run echo-request)

  ;; using a servlet allows the session to be shared in a WildFly
  ;; cluster and allow access to the session from websockets
  (web/run
    (create-servlet #'counter)
    :path "/counter")
  )
