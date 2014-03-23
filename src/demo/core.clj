(ns demo.core
  (:require demo.web
            demo.scheduling))

(defn -main [& args]
  (apply demo.web/-main args)
  (apply demo.scheduling/-main args))
