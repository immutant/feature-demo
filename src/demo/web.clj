(ns demo.web
  (:require [clojure.pprint           :refer [pprint]]
            [immutant.web             :as web]
            [immutant.web.javax       :as javax]
            [ring.middleware.resource :as ring-resource]
            [ring.util.response       :as ring-util])
  (:import [javax.servlet.http HttpServlet HttpServletRequest HttpServletResponse]))

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

(def servlet
  (proxy [HttpServlet] []
    (service [^HttpServletRequest request ^HttpServletResponse response]
      (let [session (.getSession request)
            count (or (.getAttribute session "count") 0)]
        (-> response .getWriter (.write (str "You've visited " count " times\n")))
        (.setAttribute session "count" (inc count))))))

(defn -main
  [& {:as args}]

  ;; this uses an Undertow handler by default, which is faster than a servlet
  (web/run echo-request)

  ;; using a servlet allows the session to be shared in a WildFly
  ;; cluster and allow access to the session from websockets
  (web/run
    (javax/create-servlet #'counter)
    :path "/counter")

  (web/run servlet :path "/session")
  )
