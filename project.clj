(defproject demo "0.2.0-SNAPSHOT"
  :description "Demo of Immutant 2.x libraries"
  :url "http://github.com/immutant/feature-demo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.immutant/web "2.0.0-SNAPSHOT"]
                 [org.immutant/scheduling "2.0.0-SNAPSHOT"]
                 [clj-time "0.7.0"]]
  :plugins [[lein-immutant "2.0.0-SNAPSHOT"]]
  :main demo.core)
