(ns demo.core
  (:require demo.web
            demo.scheduling
            demo.messaging))

(defn -main [& args]
  (apply demo.web/-main args)
  (apply demo.scheduling/-main args)
  (apply demo.messaging/-main args))
