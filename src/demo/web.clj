(ns demo.web
  (:use [clojure.pprint :only [pprint]])
  (:require [immutant.web             :as web]
            [immutant.web.session     :as immutant-session]
            [immutant.util            :as util]
            [ring.middleware.session  :as ring-session]
            [ring.middleware.resource :as ring-resource]
            [ring.util.response       :as ring-util]))

(defn request-dumper
  "A very simple ring handler"
  [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (with-out-str (pprint request))})
;;; Mount the handler relative to the app's context path [/demo]
(web/start #'request-dumper)



(defn world-greeter
  "An even simpler ring handler"
  [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "<h1>Hello World</h1>"})
;;; Mount it at a "sub context path" [/demo/hello]
(web/start "/hello" #'world-greeter)



(defn counter [{session :session}]
  (let [count (:count session 0)
        session (assoc session :count (inc count))]
    (-> (ring-util/response (str "You accessed this page " count " times."))
        (assoc :session session))))
;;; Use Immutant's session store for automatic replication
(web/start "/counter"
 (ring-session/wrap-session
  #'counter
  {:store (immutant-session/servlet-store)}))



;;; Static resource middleware
;;; Use immutant.web/wrap-resource unless >=1.2 ring
(web/start (ring-resource/wrap-resource #'request-dumper "public"))



;;; Handy utilities
(util/in-immutant?)
(util/app-relative "src")
(util/http-port)
(util/context-path)
(util/app-uri)
