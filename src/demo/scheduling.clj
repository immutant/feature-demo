(ns demo.scheduling
  (:require [immutant.scheduling :refer [schedule stop at in
                                         limit every until cron]]
            [immutant.scheduling.joda :as j]
            [clj-time.core :as t]
            [clj-time.periodic :as p])
  (:import java.util.Date))

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

(defn every-3s-lazy-seq []
  (let [at (t/plus (t/now) (t/seconds 1))
        every (t/seconds 3)]
    (p/periodic-seq at every)))

(defn -main [& args]
  (let [beep (schedule #(prn "beep") every-5s)
        boop (j/schedule #(prn "boop") (every-3s-lazy-seq))]
    (schedule #(doall (map stop [beep boop])) (in 20 :seconds))))
