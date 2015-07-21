(ns demo.web
  (:require [immutant.web                 :as web]
            [immutant.web.async           :as async]
            [immutant.web.middleware      :as mw]
            [demo.web.sse                 :as sse]
            [demo.web.http-kit-comparison :as hk]
            [compojure.route              :as route]
            [compojure.core     :refer (ANY GET defroutes)]
            [ring.util.response :refer (response redirect content-type)]
            [clojure.pprint     :refer (pprint)]
            [environ.core       :refer (env)]))

(defn echo
  "Echos the request back as a string."
  [request]
  (-> (response (with-out-str (pprint request)))
    (content-type "text/plain")))

(defn counter
  "An example manipulating session state from
  https://github.com/ring-clojure/ring/wiki/Sessions"
  [{session :session}]
  (let [count (:count session 0)
        session (assoc session :count (inc count))]
    (println "counter =>" count)
    (-> (response (str "You accessed this page " count " times\n"))
      (assoc :session session))))

(defn reverser
  "An example WebSocket app"
  [request]
  (async/as-channel request
    {:on-open    (fn [channel]
                   (async/send! channel "Ready to reverse your messages!"))
     :on-message (fn [channel m]
                   (async/send! channel (apply str (reverse m))))
     :on-close   (fn [channel {:keys [code reason]}]
                   (println "close code:" code "reason:" reason))}))


(defroutes routes
  (GET "/" {c :context} (redirect (str c "/index.html")))
  (GET "/counter"  [] counter)
  (GET "/reverser" [] reverser)
  (GET "/sse"      [] sse/countdown)
  (GET "/http-kit" [] hk/async-handler)
  (route/resources "/")
  (ANY "*" [] echo))

(defn -main [& {:as args}]
  (web/run
    (-> routes
      (immutant.web.middleware/wrap-session
        {:timeout 20})
      #_(immutant.web.middleware/wrap-websocket
        {:on-open (fn [ch] (println "You opened a websocket!"))}))
    (merge {"host" (env :demo-web-host), "port" (env :demo-web-port)}
      args)))
