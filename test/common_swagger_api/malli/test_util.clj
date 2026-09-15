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
  "Asserts that every public schema var in the namespace, and every response body in its public response maps,
   transforms to JSON schema. Returns the number of schemas checked."
  [ns-sym]
  (require ns-sym)
  (let [checked (atom 0)]
    (doseq [[sym v] (ns-publics ns-sym)]
      (cond
        (schema-var? v) (do (swap! checked inc)
                            (is (map? (js/transform @v)) (str sym)))
        (map? @v)       (doseq [[status response] @v :when (and (map? response) (:body response))]
                          (swap! checked inc)
                          (is (map? (js/transform (:body response))) (str sym " " status)))))
    (is (pos? @checked) (str "no schemas found in " ns-sym))
    @checked))

;; Schema types whose only child still holds map entries worth walking into.
(def ^:private wrapper-types #{:vector :sequential :set :maybe})

(defn- deref-schema
  [x]
  (try (m/deref-all (m/schema x)) (catch Exception _ nil)))

(defn- schema-examples
  "Returns a [path schema example] triple for every :json-schema/example reachable from schema, walking map entries
   and single-child collection wrappers. The values of :or, :multi and :map-of schemas are not walked into; their own
   properties are still checked. `seen` guards against recursive schemas."
  [path x seen]
  (when-let [schema (deref-schema x)]
    (let [form (m/form schema)]
      (when-not (contains? seen form)
        (let [seen  (conj seen form)
              props (m/properties schema)
              own   (when (contains? props :json-schema/example)
                      [[path schema (:json-schema/example props)]])
              kids  (cond
                      (= :map (m/type schema))
                      (mapcat (fn [[k entry-props child]]
                                (let [path (conj path k)]
                                  (concat (when (contains? entry-props :json-schema/example)
                                            [[path child (:json-schema/example entry-props)]])
                                          (schema-examples path child seen))))
                              (m/children schema))

                      (contains? wrapper-types (m/type schema))
                      (schema-examples path (first (m/children schema)) seen))]
          (concat own kids))))))

(defn examples-valid
  "Asserts that every :json-schema/example in the namespace's public map schemas validates against the schema it
   documents. Returns the number of examples checked."
  [ns-sym]
  (require ns-sym)
  (let [examples (for [[sym v] (ns-publics ns-sym)
                       :when   (schema-var? v)
                       :let    [schema (deref-schema @v)]
                       :when   (and schema (= :map (m/type schema)))
                       triple  (schema-examples [] schema #{})]
                   (cons sym triple))]
    (doseq [[sym path schema example] examples]
      (is (m/validate schema example)
          (str sym " " path " example " (pr-str example) " is not valid for " (pr-str (m/form schema)))))
    (count examples)))
