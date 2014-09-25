(defproject demo "0.2.0-SNAPSHOT"
  :description "Demo of Immutant 2.x libraries"
  :url "http://github.com/immutant/feature-demo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.immutant/immutant "2.x.incremental.307"]

                 ;; or bring the artifacts in piecemeal:
                 ;; [org.immutant/caching "2.x.incremental.307"]
                 ;; [org.immutant/messaging "2.x.incremental.307"]
                 ;; [org.immutant/scheduling "2.x.incremental.307"]
                 ;; [org.immutant/web "2.x.incremental.307"]

                 [compojure "1.1.8"]
                 [ring/ring-core "1.3.0"]
                 [ring/ring-devel "1.3.0"]
                 [org.clojure/core.memoize "0.5.6"]
                 [clj-time "0.7.0"]
                 [cheshire "5.3.1"]]
  :repositories [["Immutant incremental builds"
                  "http://downloads.immutant.org/incremental/"]]
  :plugins [[lein-immutant "2.0.0-alpha1"]]
  :main demo.core
  :uberjar-name "demo-standalone.jar"
  :profiles {:uberjar {:aot [demo.core]}}
  :min-lein-version "2.4.0"
  :aliases {"msg-client" ["run" "-m" "demo.remote-messaging-client"]})
