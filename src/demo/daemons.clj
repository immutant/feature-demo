(ns demo.daemons
  (:require [immutant.daemons :as d]
            [clojure.tools.logging :as log]))

(def service (let [start (fn [] (log/info "Starting daemon"))
                   stop  (fn [] (log/info "Stopped daemon"))]
               (d/daemonize "demo service" start stop)))

(.stop service)