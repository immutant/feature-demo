(defproject demo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [ring/ring-core "1.2.0-beta1"]
                 [com.h2database/h2 "1.3.160"]]
  :profiles {:openshift {:immutant {:init immutant.init/load-all}}})
