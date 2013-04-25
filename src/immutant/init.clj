(ns immutant.init
  (:require demo.web))

(defn load-all []
  (require 'demo.cache
           'demo.daemons
           'demo.jobs
           'demo.messaging
           'demo.xa))
