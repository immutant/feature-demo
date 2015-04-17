(defproject demo "0.2.0-SNAPSHOT"
  :description "Demo of Immutant 2.x libraries"
  :url "http://github.com/immutant/feature-demo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.immutant/immutant "2.0.0"]
                 [compojure "1.3.1"]
                 [ring/ring-devel "1.3.1"]
                 [org.clojure/core.memoize "0.5.6"]
                 [clj-time "0.9.0"]
                 [cheshire "5.4.0"]
                 [environ "1.0.0"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [com.h2database/h2 "1.3.176"]]
  :repositories [["Immutant incremental builds"
                  "http://downloads.immutant.org/incremental/"]]
  :plugins [[lein-immutant "2.0.0"]]
  :main demo.core
  :uberjar-name "demo-standalone.jar"
  :profiles {:uberjar {:aot [demo.core]}}
  :min-lein-version "2.4.0"
  :jvm-opts ["-Dhornetq.data.dir=target/hornetq-data"
             "-Dcom.arjuna.ats.arjuna.objectstore.objectStoreDir=target/ObjectStore"]
  :aliases {"msg-client" ["run" "-m" "demo.remote-messaging-client"]})
