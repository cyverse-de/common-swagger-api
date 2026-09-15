(ns common-swagger-api.malli.data.exists
  (:require
   [clojure-commons.error-codes :as ce]
   [common-swagger-api.malli :refer [add-enum-values CommonResponses doc-only ErrorResponseUnchecked]]
   [common-swagger-api.malli.data :as data-schema]
   [malli.util :as mu]))

(def ExistenceSummary "File and Folder Existence")
(def ExistenceDocs
  "This endpoint allows the caller to check for the existence of a set of files and folders.")

(def ExistenceRequest
  (mu/update-properties data-schema/Paths assoc :description "The paths to check for existence."))

(def PathExistenceMap
  [:map-of
   [:keyword
    {:description         "The iRODS data item's path"
     :json-schema/example (keyword "/example/home/janedoe/file.txt")}]

   [:boolean
    {:description         "Whether this path from the request exists"
     :json-schema/example true}]])

(def ExistenceInfo
  [:map {:closed true}
   [:paths
    {:description "Paths existence mapping"}
    PathExistenceMap]])

;; Used only for display as documentation in Swagger UI
(def ExistenceResponsePathsMap
  [:map {:closed true}
   [(keyword "/path/from/request/to/a/file/or/folder")
    {:description         "Whether this path from the request exists"
     :json-schema/example true}
    :boolean]])

;; Used only for display as documentation in Swagger UI
(def ExistenceResponse
  [:map {:closed true}
   [:paths
    {:description "A map of paths from the request to their existence info"}
    ExistenceResponsePathsMap]])

(def ExistenceErrorCodeResponses
  (conj data-schema/CommonErrorCodeResponses
        ce/ERR_NOT_A_USER
        ce/ERR_TOO_MANY_RESULTS))

(def ExistenceErrorResponses
  (mu/update
   ErrorResponseUnchecked
   :error_code
   add-enum-values
   ce/ERR_NOT_A_USER
   ce/ERR_TOO_MANY_RESULTS))

(def ExistenceResponses
  (merge CommonResponses
         {200 {:body        (doc-only ExistenceInfo ExistenceResponse)
               :description "A map of paths from the request to their existence info"}
          500 {:body        ExistenceErrorResponses
               :description data-schema/CommonErrorCodeDocs}}))
