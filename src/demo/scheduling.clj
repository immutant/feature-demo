(ns demo.scheduling
  (:require [immutant.scheduling :refer [schedule stop at in
                                         limit every until cron]])
  (:import java.util.Date))

(defn print-time [] (println (Date.)))

(def every-5s
  (or {:every [5 :seconds]}
    (every 5 :seconds)))

(def daily
  (-> (at (or (Date.) "1830" 1395439646983))
    (every :day)))

(def in-5m-until-5pm
  (-> (in 5 :minutes)
    (every 2 :hours, 30 :minutes)
    (until "1700")))

(def nine-am
  (cron "0 0 9 * * ?"))

(def every-5s-cron
  (cron "*/5 * * * * ?"))

(def every-10ms-in-500ms-4-times
  (-> (in 500)
    (every 10)
    (limit 4)))

(defn -main [& args]
  (let [timer (schedule print-time every-5s)]
    (schedule #(stop timer) (in 20 :seconds))))
