(ns demo.xa
  (:require [immutant.xa :as xa]
            [immutant.xa.transaction :as tx]
            [immutant.messaging :as msg]
            [immutant.cache :as cache]
            [clojure.java.jdbc :as sql]))

;;; Create a JMS queue
(msg/start "/queue/xa")

;;; And an Infinispan cache
(def cache (cache/cache "xa"))

;;; And a transactional datasource
(defonce h2 (xa/datasource "h2" {:adapter "h2" :database "mem:foo"}))
(def spec {:datasource h2})

;;; Create the database schema
(sql/with-connection spec
  (try (sql/drop-table :things) (catch Exception _))
  (sql/create-table :things [:name "varchar(50)"]))

;;; Write a thing
(defn write-thing-to-db [name]
  (sql/with-connection spec
    (sql/insert-records :things {:name name})))

;;; Count all the things
(defn count-things-in-db []
  (sql/with-connection spec
    (sql/with-query-results rows ["select count(*) c from things"]
      (int ((first rows) :c)))))

;;; Attempt multi-resource transaction
(defn attempt-transaction [name & [f]]
  (try
    (xa/transaction
     (write-thing-to-db name)
     (msg/publish "/queue/xa" name)
     (cache/put cache :name name)
     (tx/not-supported
      (cache/put cache :attempts (inc (or (:attempts cache) 0))))
     (if f (f)))
    (catch Exception e
      (println "Caught exception:" (.getMessage e)))))


(attempt-transaction "foo")
(attempt-transaction "bar" #(throw (Exception. "rollback")))

[(msg/receive "/queue/xa" :timeout 1000)
 (count-things-in-db)
 cache]