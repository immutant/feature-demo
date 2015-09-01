(ns demo.scheduling
  (:require [immutant.scheduling :refer (every at in until limit cron singleton) :as sch]
            [immutant.util       :as util]
            immutant.scheduling.joda
            clj-time.core
            clj-time.periodic)
  (:import java.util.Date))

;; scheduling specs are just maps. we provide optional helpers for
;; generating thoes maps
(def every-5s
  (or
    {:every (or 5000 [5 :seconds])}
    (every 5 :seconds)))

;; :at can be a Date, a time as a string, or millis since epoch
(def daily
  (-> (at (or (Date.) "1830" 1395439646983))
    (every :day)))

;; helpers optionally take a map as the first arg, so compose
(def in-5m-until-5pm
  (-> (in 5 :minutes)
    (every 2 :hours, 30 :minutes)
    (until "1700")))

;; cron-style strings are also supported
(def nine-am
  (cron "0 0 9 * * ?"))

(def every-5s-cron
  (cron "*/5 * * * * ?"))

(def every-10ms-in-500ms-4-times
  (-> (in 500)
    (every 10)
    (limit 4)))

;; you can use clj-time periods if using clj-time
(defn every-3s-lazy-seq []
  (let [at (clj-time.core/plus (clj-time.core/now) (clj-time.core/seconds 1))
        every (clj-time.core/seconds 3)]
    (clj-time.periodic/periodic-seq at every)))

(comment

  ;; scheduling a simple job, using bound-fn to capture *out* for the repl
  (def every-2-job
    (sch/schedule (bound-fn []
                    (println "called"))
      (every 2 :seconds)))

  ;; the return value of the schedule call can be used to stop it
  (sch/stop every-2-job)

  ;; schedule a job from a clj-time sequence
  (def seq-job
    (immutant.scheduling.joda/schedule-seq #(println "a sequence")
      (every-3s-lazy-seq)))

  (sch/stop seq-job)

  )

(defn -main [& _]

  ;; start a couple of jobs, along with a job to stop them in 20 seconds
  (let [beep (sch/schedule #(println "beep") every-5s)
        ;; schedule a clj-time sequence
        boop (immutant.scheduling.joda/schedule-seq #(println "boop") (every-3s-lazy-seq))]
    (sch/schedule
      (fn []
        (println "unscheduling beep & boop")
        (sch/stop beep)
        (sch/stop boop))
      (in 20 :seconds)))

  ;; start singleton and non-singleton jobs to demo cluster failover
  (when (util/in-container?)

    ;; singleton jobs require an id
    (sch/schedule #(println "I run on ONE node")
      (-> (singleton :a-unique-id-for-the-singleton)
        (every 10 :seconds)))

    (sch/schedule #(println "I run on EVERY node")
      (every 9 :seconds))))
