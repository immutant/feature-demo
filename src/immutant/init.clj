(ns immutant.init)

(defn load-all []
  (require 'demo.web
           'demo.cache
           'demo.daemons
           'demo.jobs
           'demo.messaging
           'demo.xa))

