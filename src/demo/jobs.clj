(ns demo.jobs
  (:require [immutant.jobs :as jobs])
  (:import java.util.Date))

(jobs/schedule "every-5s-forever"  
               #(println (str (Date.)))
               "*/5 * * * * ?"
               :singleton false)

(jobs/schedule "4-times-every-10ms-in-500ms"  
               #(println (Date.))
               :in     500
               :every  10
               :repeat 3)

(jobs/unschedule "every-5s-forever")
(jobs/unschedule "4-times-every-10ms-in-500ms")