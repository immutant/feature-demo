(ns demo.caching
  (:require [immutant.caching :as c]
            [immutant.caching.core-memoize :refer [memo]])
  (:import java.util.concurrent.TimeUnit))

;;; Caches implement org.infinispan.Cache and
;;; java.util.concurrent.ConcurrentMap. They are mutable maps, so
;;; changing them requires Java interop, as would any mutable Java
;;; data structure. Of course, reading entries can be done with core
;;; Clojure functions.

;;; Writing

(def foo (c/cache "foo", :ttl [5 :minutes], :idle [1 :minute]))

;;; Put an entry in the cache
(.put foo :a 1)

;;; Override its time-to-live...
(.put foo :a 1, 1 TimeUnit/HOURS)
;;; ...and its max idle time...
(.put foo :a 1, 1 TimeUnit/HOURS, 20 TimeUnit/MINUTES)
;;; ...also works for putIfAbsent, putAll, and replace

;;; Add all the entries in the map to the cache
(.putAll foo {:b 2, :c 3})

;;; Put it in only if key is not already present
(.putIfAbsent foo :b 6)                 ;=> 2
(.putIfAbsent foo :d 4)                 ;=> nil

;;; Put it in only if key is already present
(.replace foo :e 5)                     ;=> nil
(.replace foo :b 6)                     ;=> 2

;;; Put it in only if key is there and current matches old
(.replace foo :b 2 0)                   ;=> false
(.replace foo :b 6 0)                   ;=> true
(:b foo)                                ;=> 0


;;; Reading

(def bar (c/cache "bar"))
(.putAll bar {:a 1, :b {:c 3, :d 4}})

;;; Use get to obtain associated values
(get bar :a)                            ;=> 1
(get bar :x)                            ;=> nil
(get bar :x 42)                         ;=> 42

;;; Symbols look up their value
(:a bar)                                ;=> 1
(:x bar 42)                             ;=> 42

;;; Nested structures work as you would expect
(get-in bar [:b :c])                    ;=> 3

;;; Use find to return entries
(find bar :a)                           ;=> [:a 1]

;;; Use contains? to check membership
(contains? bar :a)                      ;=> true
(contains? bar :x)                      ;=> false


;;; Removing

(def baz (c/cache "baz"))
(.putAll baz {:a 1 :b 2})

;;; Removing a missing key is harmless
(.remove baz :missing)                  ;=> nil

;;; Removing an existing key returns its value
(.remove baz :b)                        ;=> 2

;;; If value is passed, both must match for remove to succeed
(.remove baz :a 2)                      ;=> false
(.remove baz :a 1)                      ;=> true

;;; Clear all entries
(.clear baz)


;;; Memoization

(defn slow-fn [& _]
  (Thread/sleep 5000)
  42)

;;; Other than the function to be memoized, arguments are the same as
;;; for the cache function.
(def memoized-fn (memo slow-fn "memo", :ttl [5 :minutes]))

;;; Invoking the memoized function fills the cache with the result
;;; from the slow function the first time it is called.
(memoized-fn 1 2 3)                     ;=> 42

;;; Subsequent invocations with the same parameters return the result
;;; from the cache, avoiding the overhead of the slow function
(memoized-fn 1 2 3)                     ;=> 42

;;; It's possible to manipulate the cache backing the memoized
;;; function by referring to its name
(def c (c/cache "memo"))
(get c [1 2 3])                         ;=> 42


[foo bar baz]
