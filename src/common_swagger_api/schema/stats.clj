(ns common-swagger-api.schema.stats
  (:require [common-swagger-api.schema
             :refer [->optional-param
                     CommonResponses
                     describe
                     doc-only
                     ErrorResponseUnchecked
                     NonBlankString
                     optional-key->keyword]]
            [clojure-commons.error-codes :as ce]
            [common-swagger-api.schema.data :as data-schema]
            [schema.core :as s])
  (:import [java.util UUID]))

(def StatSummary "File and Folder Status Information")
(def StatDocs
  "This endpoint allows the caller to get information about many files and folders at once.")

(def DataTypeEnum (s/enum :file :dir))
(def DataItemIdParam (describe UUID "The UUID of this data item"))
(def DataItemPathParam (describe NonBlankString "The IRODS paths to this data item"))

(s/defschema StatQueryParams
  {(s/optional-key :validation-behavior)
   (describe data-schema/PermissionEnum "What level of permissions on the queried files should be validated?")})

(s/defschema FilteredStatQueryParams
  (merge StatQueryParams
         {(s/optional-key :filter-include)
          (describe String (str "Comma-separated list of keys to generate and return in each stat object. "
                                "Defaults to all keys. If both this and filter-exclude are provided, "
                                "includes are processed first, then excludes."))

          (s/optional-key :filter-exclude)
          (describe String (str "Comma-separated list of keys to exclude from each stat object. "
                                "Defaults to no keys. If both this and filter-include are provided, "
                                "includes are processed first, then excludes."))}))

(s/defschema DataStatInfo
  {:id
   DataItemIdParam

   :path
   DataItemPathParam

   :type
   (describe DataTypeEnum "The data item's type")

   :label
   (describe String "The descriptive label for this item.")

   :date-created
   (describe Long "The date this data item was created")

   :date-modified
   (describe Long "The date this data item was last modified")

   :permission
   (describe data-schema/PermissionEnum "The requesting user's permissions on this data item")

   (s/optional-key :share-count)
   (describe Long (str "The number of other users this data item is shared with (only displayed to users with 'own' "
                       "permissions)"))})

(s/defschema DirStatInfo
  (merge DataStatInfo
         {:file-count (describe Long "The number of files under this directory")
          :dir-count  (describe Long "The number of subdirectories under this directory")}))

(s/defschema FileStatInfo
  (merge DataStatInfo
         {:file-size
          (describe Long "The size in bytes of this file")

          :content-type
          (describe NonBlankString "The detected media type of the data contained in this file")

          :infoType
          (describe String "The type of contents in this file")

          :md5
          (describe String "The md5 hash of this file's contents, as calculated and saved by IRODS")}))

(s/defschema FilteredStatInfo
  (let [combined-stat (merge DirStatInfo FileStatInfo)]
    (reduce ->optional-param combined-stat (remove s/optional-key? (keys combined-stat)))))

(def AvailableStatFields (vec (map optional-key->keyword (keys FilteredStatInfo))))

(s/defschema FileStat
  {:file (describe FileStatInfo "File info")})

(s/defschema PathsMap
  {(describe s/Keyword "The iRODS data item's path")
   (describe (s/conditional #(contains? % :file-size) FileStatInfo :else DirStatInfo) "The data item's info")})

(s/defschema FilteredPathsMap
  {(describe s/Keyword "The iRODS data item's path")
   (describe FilteredStatInfo "The data item's info")})

(s/defschema DataIdsMap
  {(describe s/Keyword "The iRODS data item's ID")
   (describe (s/conditional #(contains? % :file-size) FileStatInfo :else DirStatInfo) "The data item's info")})

(s/defschema FilteredDataIdsMap
  {(describe s/Keyword "The iRODS data item's ID")
   (describe FilteredStatInfo "The data item's info")})

(s/defschema StatusInfo
  {(s/optional-key :paths) (describe PathsMap "Paths info")
   (s/optional-key :ids) (describe DataIdsMap "IDs info")})

(s/defschema FilteredStatusInfo
  {(s/optional-key :paths) (describe FilteredPathsMap "Paths info")
   (s/optional-key :ids) (describe FilteredDataIdsMap "IDs info")})

;; Used only for display as documentation in Swagger UI
(s/defschema StatResponsePathsMap
  {(keyword ":/path/from/request/to/a/folder") (describe DirStatInfo "A folder's info")
   (keyword ":/path/from/request/to/a/file")   (describe FileStatInfo "A file's info")})

;; Used only for display as documentation in Swagger UI
(s/defschema StatResponseIdsMap
  {:some-folder-uuid (describe DirStatInfo "A folder's info")
   :some-file-uuid   (describe FileStatInfo "A file's info")})

;; Used only for display as documentation in Swagger UI
(s/defschema StatResponse
  {(s/optional-key :paths) (describe StatResponsePathsMap "A map of paths from the request to their status info")
   (s/optional-key :ids) (describe StatResponseIdsMap "A map of ids from the request to their status info")})

(s/defschema StatErrorResponses
  (merge ErrorResponseUnchecked
         {:error_code (apply s/enum (conj data-schema/CommonErrorCodeResponses
                                          ce/ERR_DOES_NOT_EXIST
                                          ce/ERR_NOT_READABLE
                                          ce/ERR_NOT_WRITEABLE
                                          ce/ERR_NOT_OWNER
                                          ce/ERR_NOT_A_USER
                                          ce/ERR_TOO_MANY_RESULTS))}))

(s/defschema StatResponses
  (merge CommonResponses
         {200 {:schema      (doc-only StatusInfo StatResponse)
               :description "File and Folder Status Response."}
          500 {:schema      StatErrorResponses
               :description data-schema/CommonErrorCodeDocs}}))
