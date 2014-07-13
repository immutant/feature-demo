(ns demo.web
  (:require [clojure.pprint           :refer [pprint]]
            [immutant.web             :refer [run]]
            [immutant.web.javax       :refer [create-servlet]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response       :refer [response]]))

(defn echo-request
  "Dump the request"
  [request]
  (response (with-out-str (pprint request))))

(defn world-greeter
  "Say hello"
  [_]
  (response "Hello World"))

(def assets
  "Serve static assets from resources/public/"
  (wrap-resource #'echo-request "public"))

;;; The Ring Session example: https://github.com/ring-clojure/ring/wiki/Sessions
(defn counter [{session :session}]
  (let [count (:count session 0)
        session (assoc session :count (inc count))]
    (-> (response (str "You accessed this page " count " times\n"))
        (assoc :session session))))

(defn -main
  [& {:as args}]

  ;; this uses an Undertow handler by default, which is faster
  (run echo-request args)

  ;; using a servlet allows the session to be shared in a WildFly
  ;; cluster and allow access to the session from websockets
  (run (create-servlet #'counter) :path "/counter"))
