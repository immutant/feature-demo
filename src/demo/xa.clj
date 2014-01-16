(ns demo.xa
  (:require [immutant.xa :as xa]
            [immutant.xa.transaction :as tx]
            [immutant.messaging :as msg]
            [immutant.cache :as cache]
            [clojure.java.jdbc :as sql]))

;;; Create a JMS queue
(msg/start "/queue/xa")

;;; And an Infinispan cache
(def cache (cache/lookup-or-create "xa"))

;;; And a transactional datasource
(defonce h2 (xa/datasource "h2" {:adapter "h2" :database "mem:foo"}))
(def spec {:datasource h2})

;;; Create the database schema
(sql/with-connection spec
  (try (sql/drop-table :things) (catch Exception _))
  (sql/create-table :things [:name "varchar(50)"]))

;;; Write a thing
(defn write-thing [name]
  (sql/insert! spec :things {:name name}))

;;; Delete all the things
(defn delete-all-the-things []
  (sql/delete! spec :things [true]))

;;; Count all the things
(defn count-all-the-things []
  (-> (sql/query spec ["select count(*) c from things"])
      first
      :c))

;;; Attempt multi-resource transaction
(defn attempt-transaction [name & [f]]
  (try
    (xa/transaction
     (write-thing name)
     (msg/publish "/queue/xa" name)
     (cache/put cache :name name)
     (tx/not-supported
      (cache/put cache :attempts (inc (or (:attempts cache) 0))))
     (if f (f)))
    (catch Exception e
      (if (not= "testing rollback" (.getMessage e))
        (throw e)))))


(attempt-transaction "foo")
(attempt-transaction "bar" #(throw (Exception. "testing rollback")))

[(msg/receive "/queue/xa" :timeout 1000)
 (count-all-the-things)
 cache]


(comment
  ;;; Examples for Oracle, MySQL, PostgreSQL, and SQL Server
  
  ;;; rds-create-db-instance oracle -s 10 -c db.m1.small -e oracle-se -u myuser -p mypassword --db-name mydb
  (xa/datasource "oracle" {:adapter  "oracle"
                           :url      "jdbc:oracle:thin:@//oracle.cpct4icp7nye.us-east-1.rds.amazonaws.com:1521/mydb"
                           :username "myuser"
                           :password "mypassword"})

  ;;; rds-create-db-instance mysql -s 10 -c db.m1.small -e mysql -u myuser -p mypassword --db-name mydb
  (xa/datasource "mysql" {:adapter "mysql"
                          :url     "jdbc:mysql://mysql.cpct4icp7nye.us-east-1.rds.amazonaws.com/mydb?user=myuser&password=mypassword"})

  ;;; configured locally
  (xa/datasource "postgres" {:adapter  "postgresql"
                             :username "myuser"
                             :password "mypassword"
                             :database "mydb"})

  ;;; nfi since --db-name isn't supported for RDS sqlserver-se instances
  (xa/datasource "mssql" {:adapter  "mssql"
                          :host     "mssql.cpct4icp7nye.us-east-1.rds.amazonaws.com"
                          :username "myuser"
                          :password "mypassword"
                          :database "mydb"}))
