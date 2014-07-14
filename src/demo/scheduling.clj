(ns demo.scheduling
  (:require [immutant.scheduling      :as is]
            [immutant.scheduling.joda :as ijoda]
            [clj-time.core            :as time]
            [clj-time.periodic        :as periodic])
  (:import java.util.Date))

(def every-5s
  (or {:every [5 :seconds]}
    (is/every 5 :seconds)))

(def daily
  (-> (is/at (or (Date.) "1830" 1395439646983))
    (is/every :day)))

(def in-5m-until-5pm
  (-> (is/in 5 :minutes)
    (is/every 2 :hours, 30 :minutes)
    (is/until "1700")))

(def nine-am
  (is/cron "0 0 9 * * ?"))

(def every-5s-cron
  (is/cron "*/5 * * * * ?"))

(def every-10ms-in-500ms-4-times
  (-> (is/in 500)
    (is/every 10)
    (is/limit 4)))

(defn every-3s-lazy-seq []
  (let [at (time/plus (time/now) (time/seconds 1))
        every (time/seconds 3)]
    (periodic/periodic-seq at every)))

(defn -main [& args]
  (let [beep (is/schedule #(prn "beep") every-5s)
        boop (ijoda/schedule-seq #(prn "boop") (every-3s-lazy-seq))]
    (is/schedule #(doall (map is/stop [beep boop])) (is/in 20 :seconds))))
