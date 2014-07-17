(defproject demo "0.2.0-SNAPSHOT"
  :description "Demo of Immutant 2.x libraries"
  :url "http://github.com/immutant/feature-demo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.immutant/immutant "2.x.incremental.183"
                  ]

                 ;; Or bring in the immutant jars piecemeal
                 ;; [org.immutant/caching    "2.x.incremental.183"]
                 ;; [org.immutant/messaging  "2.x.incremental.183"]
                 ;; [org.immutant/scheduling "2.x.incremental.183"]
                 ;; [org.immutant/web        "2.x.incremental.183"]

                 [org.clojure/core.memoize "0.5.6"]
                 [clj-time "0.7.0"]
                 [cheshire "5.3.1"]]
  :plugins [[lein-immutant "2.0.0-SNAPSHOT"]]
  :repositories [["Immutant 2.x incremental builds"
                  "http://downloads.immutant.org/incremental/"]]
  :main demo.core
  :aliases {"msg-client" ["run" "-m" "demo.remote-messaging-client"]}
  :profiles {:uberjar {:aot :all}})
