(ns demo.jobs
  (:require [immutant.jobs :as jobs])
  (:import java.util.Date))

;;; Strings are military time
(jobs/schedule "run-reports"
               #(println "reporting")
               :at "0400" :every :day)

;;; java.util.Date, possibly returned by clj-time
(jobs/schedule "run-reports"
               #(println "reporting")
               :at (Date.) :every :day)

;;; Remember the 0's!
(jobs/schedule "nothing-at-9am"  
               #(println "nothing")
               "0 0 9 * * ?"
               :singleton false)

;;; Fancy cron spec feature
(jobs/schedule "every-5s-forever"  
               #(println (Date.))
               "*/5 * * * * ?")

(defn foo [] (println (Date.) "foo"))

;;; A period spec is a vector of a number and keyword
(jobs/schedule "every-5s-forever"  
               #'foo
               :every [5 :seconds])

;;; Numbers are milliseconds
(jobs/schedule "every-10ms-in-500ms-4-times"  
               #(println (Date.) "test")
               :in     500
               :every  10
               :repeat 3)

(jobs/unschedule "every-5s-forever")
