(ns demo.core-test
  (:use clojure.test
        immutant.init))

(deftest load-everything
  (load-all)
  (is true))

