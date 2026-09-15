(ns common-swagger-api.malli.parity-test
  (:require
   [clojure.set :as set]
   [clojure.test :refer [deftest is]]))

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
    [common-swagger-api.schema.apps.bootstrap common-swagger-api.malli.apps.bootstrap #{}]
    [common-swagger-api.schema.apps.categories common-swagger-api.malli.apps.categories #{}]
    [common-swagger-api.schema.apps.communities common-swagger-api.malli.apps.communities #{}]
    [common-swagger-api.schema.apps.elements common-swagger-api.malli.apps.elements #{}]
    [common-swagger-api.schema.apps.metadata common-swagger-api.malli.apps.metadata #{}]
    [common-swagger-api.schema.apps.permission common-swagger-api.malli.apps.permission
     #{ToolPermissionsListingResponses}]                                     ; pending Task 11
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
    [common-swagger-api.schema.permanent-id-requests common-swagger-api.malli.permanent-id-requests
     #{ValidPermanentIDRequestListSortFields}]                               ; pending Task 12
    [common-swagger-api.schema.quicklaunches common-swagger-api.malli.quicklaunches #{}]
    [common-swagger-api.schema.sessions common-swagger-api.malli.sessions #{}]
    [common-swagger-api.schema.stats common-swagger-api.malli.stats
     #{StatResponse StatResponseIdsMap StatResponsePathsMap StatResponses}]  ; pending Task 13
    [common-swagger-api.schema.subjects common-swagger-api.malli.subjects #{}]
    [common-swagger-api.schema.tools common-swagger-api.malli.tools
     #{coerce-tool-import-requests coerce-tool-list-import-request
       PrivateToolImportResponse400 PrivateToolImportResponses ToolDeleteResponses ToolDetailsResponses
       ToolUpdateResponses}]                                                 ; pending Task 14
    [common-swagger-api.schema.webhooks common-swagger-api.malli.webhooks #{}]])

(defn- public-names [ns-sym]
  (require ns-sym)
  (set (keys (ns-publics ns-sym))))

(deftest every-schema-def-has-a-malli-twin
  (doseq [[schema-ns malli-ns excluded] rows]
    (let [missing (set/difference (public-names schema-ns) core-exclusions excluded (public-names malli-ns))]
      (is (empty? missing) (str malli-ns " is missing " (sort missing))))))
