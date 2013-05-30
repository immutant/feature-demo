(ns demo.cache
  (:require [immutant.cache :as cache]))


;;; Writing

(def foo (cache/create "foo", :ttl 5, :idle 1, :units :minutes))

;;; Put an entry in the cache
(cache/put foo :a 1)

;;; Override its time-to-live
(cache/put foo :a 1 {:ttl [1 :hour]})

;;; Add all the entries in the map to the cache
(cache/put-all foo {:b 2, :c 3})

;;; Put it in only if key is not already present
(cache/put-if-absent foo :b 6)                  ;=> 2
(cache/put-if-absent foo :d 4)                  ;=> nil

;;; Put it in only if key is already present
(cache/put-if-present foo :e 5)                 ;=> nil
(cache/put-if-present foo :b 6)                 ;=> 2

;;; Put it in only if key is there and current matches old
(cache/put-if-replace foo :b 2 0)               ;=> false
(cache/put-if-replace foo :b 6 0)               ;=> true
(:b foo)                                        ;=> 0


;;; Reading

(def bar (cache/create "bar" :seed {:a 1, :b {:c 3, :d 4}}))

;;; Use get to obtain associated values
(get bar :a)                              ;=> 1
(get bar :x)                              ;=> nil
(get bar :x 42)                           ;=> 42

;;; Symbols look up their value
(:a bar)                                  ;=> 1
(:x bar 42)                               ;=> 42

;;; Nested structures work as you would expect
(get-in bar [:b :c])                      ;=> 3

;;; Use find to return entries
(find bar :a)                             ;=> [:a 1]

;;; Use contains? to check membership
(contains? bar :a)                        ;=> true
(contains? bar :x)                        ;=> false


;;; Deleting

(def baz (cache/create "baz" :seed {:a 1 :b 2}))

;;; Deleting a missing key is harmless
(cache/delete baz :missing)                     ;=> nil

;;; Deleting an existing key returns its value
(cache/delete baz :b)                           ;=> 2

;;; If value is passed, both must match for delete to succeed
(cache/delete baz :a 2)                         ;=> false
(cache/delete baz :a 1)                         ;=> true

;;; Clear all keys, returning the empty cache
(cache/delete-all baz)                          ;=> baz


;;; Memoization

(defn slow-fn [& _]
  (Thread/sleep 5000)
  42)

;;; Other than the function to be memoized, arguments are the same as
;;; for the cache function.
(def memoized-fn (cache/memo slow-fn "memo", :ttl [5 :minutes]))

;;; Invoking the memoized function fills the cache with the result
;;; from the slow function the first time it is called.
(memoized-fn 1 2 3)                     ;=> 42

;;; Subsequent invocations with the same parameters return the result
;;; from the cache, avoiding the overhead of the slow function
(memoized-fn 1 2 3)                     ;=> 42

;;; It's possible to manipulate the cache backing the memoized
;;; function by referring to its name
(def c (cache/lookup "memo"))
(get c [1 2 3])                         ;=> 42


[foo bar baz]
