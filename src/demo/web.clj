(ns demo.web
  (:require [clojure.pprint           :refer [pprint]]
            [immutant.web             :refer [run]]
            [immutant.web.javax       :refer [create-servlet]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response       :refer [response]]))

(defn request-dumper
  "Dump the request"
  [request]
  (response (with-out-str (pprint request))))

(defn world-greeter
  "Say hello"
  [_]
  (response "Hello World"))

(def assets
  "Serve static assets from resources/public/"
  (wrap-resource #'request-dumper "public"))

;;; The Ring Session example: https://github.com/ring-clojure/ring/wiki/Sessions
(defn counter [{session :session}]
  (let [count (:count session 0)
        session (assoc session :count (inc count))]
    (-> (response (str "You accessed this page " count " times\n"))
        (assoc :session session))))

(defn -main
  [& {:as args}]
  (run request-dumper args)
  (run (create-servlet #'counter) :path "/counter"))
