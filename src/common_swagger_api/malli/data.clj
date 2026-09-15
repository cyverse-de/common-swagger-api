(ns common-swagger-api.malli.data
  (:require
   [clojure-commons.error-codes :refer [ERR_SCHEMA_VALIDATION
                                        ERR_UNCHECKED_EXCEPTION]]
   [common-swagger-api.malli :refer [NonBlankString PagingParams SortFieldDocs]]
   [common-swagger-api.malli.filetypes :refer [ValidInfoTypesEnum]]
   [malli.util :as mu]))

(def CommonErrorCodeResponses [ERR_UNCHECKED_EXCEPTION ERR_SCHEMA_VALIDATION])
(def CommonErrorCodeDocs "Potential Error Codes returned by this endpoint.")

(def DataIdPathParam [:uuid {:description "The UUID assigned to the file or folder"}])

(def PermissionEnum [:enum :read :write :own])

(def Paths
  [:map {:closed true}
   [:paths
    {:description         "A list of iRODS paths"
     :json-schema/example ["/example/home/username/foo.txt" "/example/home/username/bar.txt"]}
    [:vector {:min 1} NonBlankString]]])

(def OptionalPaths
  [:map {:closed true}
   [:paths
    {:description         "A list of iRODS paths"
     :json-schema/example ["/example/home/username/foo.txt" "/example/home/username/bar.txt"]
     :optional            true}
    [:vector NonBlankString]]])

(def DataIds
  [:map {:closed true}
   [:ids
    {:description "A list of iRODS data-object UUIDs"
     :json-schema/example [#uuid "8a950a63-f999-403c-912e-97e11109e68e"
                           #uuid "87f902c1-868b-4a36-86c1-7d5d268171bf"]}
    [:vector :uuid]]])

(def OptionalPathsOrDataIds
  (-> (mu/merge DataIds OptionalPaths)
      mu/optional-keys
      (mu/update-properties assoc
                            :description
                            "The path or data ids of the data objects to gather status information on.")))

(def ValidFolderListingSortFields
  #{:datecreated
    :datemodified
    :name
    :path
    :size})

(def FolderListingPagingParams
  (mu/merge
   PagingParams
   [:map {:closed true}
    [:sort-field
     {:optional            true
      :description         SortFieldDocs
      :json-schema/example :datecreated}
     (into [:enum] ValidFolderListingSortFields)]]))

(def FolderListingParams
  (mu/merge
   FolderListingPagingParams
   [:map {:closed true}
    [:entity-type
     {:optional            true
      :description         "The type of folder items to include in the response."
      :json-schema/example :file}
     [:enum :any :file :folder]]

    [:info-type
     {:optional            true
      :description         "A list of info-types with which to filter a folder's result items."
      :json-schema/example "fasta"}
     [:or ValidInfoTypesEnum [:vector ValidInfoTypesEnum]]]]))
