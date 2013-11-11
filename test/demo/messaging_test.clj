(ns demo.messaging-test
  (:use clojure.test
        immutant.messaging))

(if (immutant.util/in-immutant?)
  (deftest ^:integration listening
    (start "test.topic")
    (let [expected (promise)
          handler (listen "test.topic" #(deliver expected %))]
      (try
        (publish "test.topic" :success)
        (is (= :success (deref expected 500 :fail)))
        (finally
          @(unlisten handler)
          (stop "test.topic"))))))
