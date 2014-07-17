(ns demo.caching
  (:require [immutant.caching              :as c]
            [immutant.caching.core-memoize :as cmemo]
            [immutant.scheduling           :as sch])
  (:import java.util.concurrent.TimeUnit))

;; Caches implement org.infinispan.Cache and
;; java.util.concurrent.ConcurrentMap

(comment writing
  "Various ways of putting entries in a cache"

  (def foo (c/cache "foo"))

  ;; The swap! function atomically updates cache entries by applying a
  ;; function to the current value or nil, if the key is missing. The
  ;; function should be side-effect free.

  (c/swap! foo :a (fnil inc 0))         ;=> 1
  (c/swap! foo :b (constantly "foo"))   ;=> "foo"
  (c/swap! foo :a inc)                  ;=> 2

  ;; Internally, swap! uses the ConcurrentMap methods, replace (CAS)
  ;; and putIfAbsent, to provide a consistent view of the cache to
  ;; callers. Of course, you can invoke these and other methods
  ;; directly using plain ol' Java interop...

  ;; Put an entry in the cache
  (.put foo :a 1)

  ;; Override default time-to-live...
  (.put foo :a 1, 1 TimeUnit/HOURS)
  ;; ...and max idle time...
  (.put foo :a 1, 1 TimeUnit/HOURS, 20 TimeUnit/MINUTES)
  ;; ...also works for putIfAbsent, putAll, and replace

  ;; Add all the entries in the map to the cache
  (.putAll foo {:b 2, :c 3})

  ;; Put it in only if key is not already present
  (.putIfAbsent foo :b 6)               ;=> 2
  (.putIfAbsent foo :d 4)               ;=> nil

  ;; Put it in only if key is already present
  (.replace foo :e 5)                   ;=> nil
  (.replace foo :b 6)                   ;=> 2

  ;; Replace for specific key and value (compare-and-set)
  (.replace foo :b 2 0)                 ;=> false
  (.replace foo :b 6 0)                 ;=> true
  (:b foo)                              ;=> 0
  )

(comment reading
  "Caches are just Maps, so core clojure functions work fine"

  (def bar (c/cache "bar"))
  (.putAll bar {:a 1, :b {:c 3, :d 4}})

  ;; Use get to obtain associated values
  (get bar :a)                            ;=> 1
  (get bar :x)                            ;=> nil
  (get bar :x 42)                         ;=> 42

  ;; Symbols look up their value
  (:a bar)                                ;=> 1
  (:x bar 42)                             ;=> 42

  ;; Nested structures work as you would expect
  (get-in bar [:b :c])                    ;=> 3

  ;; Use find to return entries
  (find bar :a)                           ;=> [:a 1]

  ;; Use contains? to check membership
  (contains? bar :a)                      ;=> true
  (contains? bar :x)                      ;=> false
  )

(comment removing
  "Expiration, eviction, and explicit removal"

  (def baz (c/cache "baz",
             :ttl [5 :minutes], :idle [1 :minute] ; expiration
             :max-entries 3, :eviction :lru))     ; eviction
  (.putAll baz {:a 1 :b 2 :c 3})

  ;; Eviction
  (:a baz)                              ;=> 1
  (select-keys baz [:b :c])             ;=> {:c 3, :b 2}
  (.put baz :d 4)
  (:a baz)                              ;=> nil

  ;; Removing a missing key is harmless
  (.remove baz :missing)                  ;=> nil

  ;; Removing an existing key returns its value
  (.remove baz :b)                        ;=> 2

  ;; If value is passed, both must match for remove to succeed
  (.remove baz :a 2)                      ;=> false
  (.remove baz :a 1)                      ;=> true

  ;; Clear all entries
  (.clear baz))

(comment encoding
  "Cache entries are not encoded by default, but may be decorated with
  a codec. Provided codecs include :edn, :json, and :fressian. The
  latter two require additional dependencies: 'cheshire' and
  'org.clojure/data.fressian', respectively."

  (def encoded (c/with-codec baz :edn))

  (.put encoded :a {:b 42})
  (:a encoded)                          ;=> {:b 42}

  ;; Access via non-encoded caches still possible
  (get baz :a)                          ;=> nil
  (get baz ":a")                        ;=> "{:b 42}"

  ;; Infinispan caches don't allow null keys or values
  (try
    (.put baz nil :a)               ;=> Null keys are not supported!
    (.put baz :b nil)               ;=> Null values are not supported!
    (catch NullPointerException _ "ERROR!"))

  ;; But nil keys and values are fine in an encoded cache
  (.put encoded nil :a)
  (.put encoded :b nil)
  (get encoded nil)                     ;=> :a
  (:b encoded)                          ;=> nil
  (contains? encoded :b)                ;=> true
  (contains? baz "nil")                 ;=> true
  )

(comment memoization
  "Caches will implement clojure.core.memoize/PluggableMemoization
  when you require immutant.caching.core-memoize, but it's up to you
  to ensure core.memoize is on the classpath"

  (defn slow-fn [& _]
    (Thread/sleep 5000)
    42)

  ;; Other than the function to be memoized, arguments are the same as
  ;; for the cache function.
  (def memoized-fn (cmemo/memo slow-fn "memo", :ttl [5 :minutes]))

  ;; Invoking the memoized function fills the cache with the result
  ;; from the slow function the first time it is called.
  (memoized-fn 1 2 3)                     ;=> 42

  ;; Subsequent invocations with the same parameters return the result
  ;; from the cache, avoiding the overhead of the slow function
  (memoized-fn 1 2 3)                     ;=> 42

  ;; It's possible to manipulate the cache backing the memoized
  ;; function by referring to its name
  (def c (c/cache "memo"))
  (get c [1 2 3])                         ;=> 42
  )


(defn -main [& args]
  "Schedule a counter"
  (let [c (c/cache "counter")
        f #(println "Updating count to"
             (c/swap! c :count (fnil inc 0)))]
    (sch/schedule f
      :id "counter"
      :every [10 :seconds]
      :singleton false)))
