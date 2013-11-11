(ns demo.core-test
  (:use clojure.test
        immutant.init
        [immutant.util :only (in-immutant?)]))

(deftest load-everything
  (if (in-immutant?)
    (load-all))
  (is true))

