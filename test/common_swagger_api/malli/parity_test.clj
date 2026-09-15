(ns common-swagger-api.malli.parity-test
  (:require
   [clojure.java.io :as io]
   [clojure.set :as set]
   [clojure.string :as string]
   [clojure.test :refer [deftest is]]
   [malli.core :as m]
   [schema.core :as s]))

;; compojure-api re-exports and plumatic-only helpers that have no Malli twin. These live in the core namespace, so
;; they are the exclusions for that namespace alone.
(def core-exclusions
  '#{api defapi describe swagger-routes routes defroutes undocumented middleware context
     GET ANY HEAD PATCH DELETE OPTIONS POST PUT
     ->optional-param copy-json-schema-meta optional-key->keyword SortFieldOptionalKey ->DocOnly map->DocOnly})

;; Per-namespace exclusions: coerce-* middleware, plumatic optional-key vars, and key filters with no Malli twin.
;; Everything else is derived from the schema source files, so a new schema namespace without a Malli twin fails.
(def exclusions
  '{common-swagger-api.schema.analyses
    #{coerce-analysis-submission-requirements}

    common-swagger-api.schema.analyses.listing
    #{OptionalKeyFilter}

    common-swagger-api.schema.apps
    #{OptionalDebugKey OptionalDeprecatedKey OptionalGroupsKey OptionalParameterArgumentsKey
      OptionalParametersKey OptionalToolsKey}

    ;; AppSubsetOptionalKey is a plumatic optional-key var; the Malli twin writes the entry inline.
    common-swagger-api.schema.apps.admin.apps
    #{AppSubsetOptionalKey}

    common-swagger-api.schema.containers
    #{coerce-settings-long-values DevicesParamOptional PortsParamOptional ProxySettingsParamOptional
      VolumesFromParamOptional VolumesParamOptional}

    ;; ModeParamOptionalKey is a plumatic optional-key var; the Malli twin writes the entry inline.
    common-swagger-api.schema.data.tickets
    #{ModeParamOptionalKey}

    common-swagger-api.schema.tools
    #{coerce-tool-import-requests coerce-tool-list-import-request}})

(defn- path->ns
  "The namespace symbol for a source path relative to the source root."
  [path]
  (-> path
      (string/replace #"\.clj$" "")
      (string/replace "/" ".")
      (string/replace "_" "-")
      symbol))

(defn- ns->path
  [ns-sym]
  (str (string/replace (string/replace (str ns-sym) "-" "_") "." "/") ".clj"))

(defn- schema-namespaces
  "Every namespace under the plumatic schema directory, read off the source tree."
  []
  (->> (file-seq (io/file "src" "common_swagger_api" "schema"))
       (filter #(.isFile ^java.io.File %))
       (map #(.getPath ^java.io.File %))
       (filter #(string/ends-with? % ".clj"))
       (map #(path->ns (string/replace % #"^src/" "")))
       sort))

(defn- malli-twin
  [ns-sym]
  (symbol (string/replace (str ns-sym) #"^common-swagger-api\.schema\." "common-swagger-api.malli.")))

(def rows
  (into [['common-swagger-api.schema 'common-swagger-api.malli core-exclusions]]
        (map (fn [ns-sym] [ns-sym (malli-twin ns-sym) (get exclusions ns-sym #{})]))
        (schema-namespaces)))

(defn- public-names
  "The public var names in a namespace, or nil if the namespace has no source file."
  [ns-sym]
  (when (io/resource (ns->path ns-sym))
    (require ns-sym)
    (set (keys (ns-publics ns-sym)))))

(deftest every-schema-def-has-a-malli-twin
  (is (< 1 (count rows)) "no plumatic schema namespaces were found")
  (doseq [[schema-ns malli-ns excluded] rows
          :let [malli-names (public-names malli-ns)]]
    (is (some? malli-names) (str malli-ns " does not exist"))
    (let [missing (set/difference (public-names schema-ns) excluded (or malli-names #{}))]
      (is (empty? missing) (str malli-ns " is missing " (sort missing))))))

(defn- plumatic-map
  "Unwraps plumatic wrappers down to a specific-key map, returning it with the recursion guard it was reached under.
   Returns nil for map-of schemas, for the wrappers that hold more than one schema, and for anything else."
  [x seen]
  (let [r (when (record? x) x)]
    (cond
      (and (map? x) (nil? r))           (when (every? s/specific-key? (keys x)) [x seen])
      (and (vector? x) (= 1 (count x))) (recur (first x) seen)
      (:schema-real r)                  (recur (:schema-real r) seen)
      (:derefable r)                    (when-not (seen (:derefable r))
                                          (recur @(:derefable r) (conj seen (:derefable r))))
      (:schema r)                       (recur (:schema r) seen))))

(defn- malli-map
  "Unwraps Malli wrappers down to a :map schema, or nil for anything else."
  [x]
  (when-let [schema (try (m/deref-all (m/schema x)) (catch Exception _ nil))]
    (case (m/type schema)
      :map                             schema
      (:maybe :vector :sequential :set) (recur (first (m/children schema)))
      nil)))

(defn- plumatic-keys [x kind?]
  (set (map s/explicit-schema-key (filter kind? (keys x)))))

(defn- malli-keys [schema optional?]
  (set (for [[k props] (m/children schema) :when (= optional? (boolean (:optional props)))] k)))

(defn- compare-map-keys
  "Compares the required and optional key sets of a plumatic/Malli pair, then recurses into the entries that are
   maps on both sides. Entry pairs that are not both maps are left alone."
  [label path p malli-schema seen]
  (when-let [[pm seen] (plumatic-map p seen)]
    (when-let [mm (malli-map malli-schema)]
      (is (= (plumatic-keys pm s/required-key?) (malli-keys mm false)) (str label " " path " required keys"))
      (is (= (plumatic-keys pm s/optional-key?) (malli-keys mm true)) (str label " " path " optional keys"))
      (let [entries (into {} (map (fn [[k _ child]] [k child])) (m/children mm))]
        (doseq [[k pc] pm :let [k (s/explicit-schema-key k)] :when (contains? entries k)]
          (compare-map-keys label (conj path k) pc (entries k) seen))))))

(deftest map-schemas-have-the-same-keys
  (doseq [[schema-ns malli-ns excluded] rows
          :let [shared (set/difference (set/intersection (public-names schema-ns)
                                                         (or (public-names malli-ns) #{}))
                                       excluded)]
          sym (sort shared)]
    (compare-map-keys (str malli-ns "/" sym) [] @(ns-resolve schema-ns sym) @(ns-resolve malli-ns sym) #{})))
