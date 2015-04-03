(ns demo.transactions
  (:require [immutant.transactions.scope :as tx]
            [immutant.messaging          :as msg]
            [immutant.caching            :as csh]
            [clojure.java.jdbc           :as sql]))

(def cache (delay (csh/cache (str *ns*) :transactional? true)))
(def queue (delay (msg/queue (str *ns*))))
(def db {:connection-uri "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"})

(defn unit-of-work [m]
  (tx/not-supported
    (csh/swap-in! @cache :attempts (fnil inc 0)))
  (csh/swap-in! @cache :count (fnil inc 0))
  (msg/publish @queue m)
  (sql/insert! db :things m))

(defn dump []
  (println "tx:queue =>" (msg/receive @queue :timeout -1))
  (println "tx:cache =>" (into {} @cache))
  (println "tx:db    =>" (sql/query db ["select * from things"])))

(defn -main [& _]
  (sql/db-do-commands db
    (sql/create-table-ddl :things [:name "varchar(50)"]))

  (tx/required (unit-of-work {:name "t-swizzle"}))
  (dump)

  (try
    (tx/required (unit-of-work {:kanye "this should fail"}))
    (catch Throwable e (println e)))
  (dump))
