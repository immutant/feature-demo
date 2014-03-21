(ns demo.web
  (:require [clojure.pprint           :refer [pprint]]
            [immutant.web             :refer [run]]
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

(defn -main
  [& {:as args}]
  (run request-dumper args))
