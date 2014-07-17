(ns demo.web
  (:require [clojure.pprint     :refer [pprint]]
            [immutant.web       :as web]
            [ring.util.response :as ring-util]))

(defn echo-request
  "Echoes the request back as a string."
  [request]
  (ring-util/response (with-out-str (pprint request))))

(defn -main
  [& {:as args}]
  (web/run echo-request args))
