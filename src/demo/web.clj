(ns demo.web
  (:require [immutant.web             :as web]
            [immutant.web.middleware  :as immutant]
            [ring.util.response       :refer [response resource-response]]
            [clojure.pprint           :refer (pprint)]
            demo.websocket))

(defn echo-request
  "Echoes the request back as a string."
  [request]
  (response (with-out-str (pprint request))))

(defn wrap-index
  "Returns index.html if :path-info is '/' or nil"
  [f]
  (fn [{:keys [path-info] :as request}]
    (if (>= 1 (count path-info))
      (resource-response "/public/index.html")
      (f request))))

(defn counter [{session :session}]
  "From https://github.com/ring-clojure/ring/wiki/Sessions"
  (let [count (:count session 0)
        session (assoc session :count (inc count))]
    (println "counter =>" count)
    (-> (response (str "You accessed this page " count " times\n"))
        (assoc :session session))))

(defn -main [& args]
  (web/run
    (-> echo-request wrap-index))
  (web/run
    (-> counter (immutant/wrap-session {:timeout 20}))
    :path "/counter")

  (apply demo.websocket/-main args))
