(ns demo.scheduling
  (:refer-clojure :exclude (repeat))
  (:require [immutant.scheduling :refer [schedule unschedule repeat
                                         at in every until cron]])
  (:import java.util.Date))

(defn foo [] (println (Date.) "foo"))

;;; A period spec is a vector of a number and keyword
(schedule "every-5s-forever" #'foo
  (every 5 :seconds))

;;; Dates or strings or ms-since-epoch
(schedule "run-reports" #(println "reporting")
  (-> (at (or (Date.) "0400" 1395439646983))
    (every :day)))

;;; Fancy cron spec feature
(schedule "every-5s-forever" #(println (Date.))
  (cron "*/5 * * * * ?"))

;;; Remember the 0's!
(schedule "nothing-at-9am" #(println "nothing")
  (cron "0 0 9 * * ?"))

;;; Numbers are milliseconds
(schedule "every-10ms-in-500ms-4-times"
  #(println (Date.) "test")
  (-> (in 500)
    (every 10)
    (repeat 3)))

(unschedule "every-5s-forever")
