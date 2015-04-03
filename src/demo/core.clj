(ns demo.core
  (:require demo.web
            demo.scheduling
            demo.messaging
            demo.caching
            demo.transactions)
  (:gen-class))

(defn -main [& args]
  (apply demo.web/-main args)
  (apply demo.messaging/-main args)
  (apply demo.scheduling/-main args)
  (apply demo.caching/-main args)
  (apply demo.transactions/-main args))
