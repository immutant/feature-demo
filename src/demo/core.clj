(ns demo.core
  (:require demo.web
            demo.websocket
            demo.scheduling
            demo.messaging
            demo.caching)
  (:gen-class))

(defn -main [& args]
  (apply demo.web/-main args)
  (apply demo.websocket/-main args)
  (apply demo.messaging/-main args)
  (apply demo.scheduling/-main args)
  (apply demo.caching/-main args))
