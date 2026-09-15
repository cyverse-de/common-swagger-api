(ns common-swagger-api.malli.parity-test
  (:require
   [clojure.set :as set]
   [clojure.test :refer [deftest is]]
   [malli.core :as m]
   [schema.core :as s]))

;; compojure-api re-exports and plumatic-only helpers that have no Malli twin.
(def core-exclusions
  '#{api defapi describe swagger-routes routes defroutes undocumented middleware context
     GET ANY HEAD PATCH DELETE OPTIONS POST PUT
     ->optional-param copy-json-schema-meta optional-key->keyword SortFieldOptionalKey ->DocOnly map->DocOnly})

;; Per-namespace exclusions. "pending" entries are removed as tasks land.
(def rows
  '[[common-swagger-api.schema common-swagger-api.malli #{}]
    [common-swagger-api.schema.analyses common-swagger-api.malli.analyses
     #{coerce-analysis-submission-requirements}]
    [common-swagger-api.schema.analyses.listing common-swagger-api.malli.analyses.listing
     #{OptionalKeyFilter}]
    [common-swagger-api.schema.apps common-swagger-api.malli.apps
     #{OptionalDebugKey OptionalDeprecatedKey OptionalGroupsKey OptionalParameterArgumentsKey
       OptionalParametersKey OptionalToolsKey}]
    [common-swagger-api.schema.apps.admin.categories common-swagger-api.malli.apps.admin.categories #{}]
    [common-swagger-api.schema.apps.admin.reference-genomes common-swagger-api.malli.apps.admin.reference-genomes
     #{}]
    [common-swagger-api.schema.apps.bootstrap common-swagger-api.malli.apps.bootstrap #{}]
    [common-swagger-api.schema.apps.categories common-swagger-api.malli.apps.categories #{}]
    [common-swagger-api.schema.apps.communities common-swagger-api.malli.apps.communities #{}]
    [common-swagger-api.schema.apps.elements common-swagger-api.malli.apps.elements #{}]
    [common-swagger-api.schema.apps.metadata common-swagger-api.malli.apps.metadata #{}]
    [common-swagger-api.schema.apps.permission common-swagger-api.malli.apps.permission #{}]
    [common-swagger-api.schema.apps.rating common-swagger-api.malli.apps.rating #{}]
    [common-swagger-api.schema.apps.reference-genomes common-swagger-api.malli.apps.reference-genomes #{}]
    [common-swagger-api.schema.apps.workspace common-swagger-api.malli.apps.workspace #{}]
    [common-swagger-api.schema.callbacks common-swagger-api.malli.callbacks #{}]
    [common-swagger-api.schema.common common-swagger-api.malli.common #{}]
    [common-swagger-api.schema.containers common-swagger-api.malli.containers
     #{coerce-settings-long-values DevicesParamOptional PortsParamOptional ProxySettingsParamOptional
       VolumesFromParamOptional VolumesParamOptional}]
    [common-swagger-api.schema.data common-swagger-api.malli.data #{}]
    [common-swagger-api.schema.filetypes common-swagger-api.malli.filetypes #{}]
    [common-swagger-api.schema.groups common-swagger-api.malli.groups #{}]
    [common-swagger-api.schema.integration-data common-swagger-api.malli.integration-data #{}]
    [common-swagger-api.schema.metadata common-swagger-api.malli.metadata #{}]
    [common-swagger-api.schema.oauth common-swagger-api.malli.oauth #{}]
    [common-swagger-api.schema.ontologies common-swagger-api.malli.ontologies #{}]
    [common-swagger-api.schema.permanent-id-requests common-swagger-api.malli.permanent-id-requests #{}]
    [common-swagger-api.schema.quicklaunches common-swagger-api.malli.quicklaunches #{}]
    [common-swagger-api.schema.sessions common-swagger-api.malli.sessions #{}]
    [common-swagger-api.schema.stats common-swagger-api.malli.stats #{}]
    [common-swagger-api.schema.subjects common-swagger-api.malli.subjects #{}]
    [common-swagger-api.schema.tools common-swagger-api.malli.tools
     #{coerce-tool-import-requests coerce-tool-list-import-request}]
    [common-swagger-api.schema.webhooks common-swagger-api.malli.webhooks #{}]])

(defn- public-names [ns-sym]
  (require ns-sym)
  (set (keys (ns-publics ns-sym))))

(deftest every-schema-def-has-a-malli-twin
  (doseq [[schema-ns malli-ns excluded] rows]
    (let [missing (set/difference (public-names schema-ns) core-exclusions excluded (public-names malli-ns))]
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
          :let [shared (set/difference (set/intersection (public-names schema-ns) (public-names malli-ns))
                                       core-exclusions excluded)]
          sym (sort shared)]
    (compare-map-keys (str malli-ns "/" sym) [] @(ns-resolve schema-ns sym) @(ns-resolve malli-ns sym) #{})))
