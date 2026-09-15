(ns common-swagger-api.malli.test-util
  (:require
   [clojure.test :refer [is]]
   [malli.core :as m]
   [malli.error :as me]
   [malli.json-schema :as js]
   [malli.transform :as mt]))

(defn valid
  "Asserts that every value validates against schema."
  [schema & values]
  (doseq [v values]
    (is (m/validate schema v)
        (str "expected valid: " (pr-str v) "\n" (pr-str (me/humanize (m/explain schema v)))))))

(defn invalid
  "Asserts that no value validates against schema."
  [schema & values]
  (doseq [v values]
    (is (not (m/validate schema v)) (str "expected invalid: " (pr-str v)))))

(defn decodes
  "Asserts that string-transformer decoding turns in into out."
  [schema in out]
  (is (= out (m/decode schema in mt/string-transformer))))

(defn- schema-var?
  [v]
  (let [x @v]
    (and (not (string? x))
         (not (fn? x))
         (try (m/schema x) true (catch Exception _ false)))))

(defn json-schema-ok
  "Asserts that every public schema var in the namespace transforms to JSON schema."
  [ns-sym]
  (require ns-sym)
  (doseq [[sym v] (ns-publics ns-sym) :when (schema-var? v)]
    (is (map? (js/transform @v)) (str sym))))
