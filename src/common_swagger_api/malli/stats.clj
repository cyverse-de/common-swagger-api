(ns common-swagger-api.malli.stats
  (:require
   [clojure-commons.error-codes :as ce]
   [common-swagger-api.malli :refer [add-enum-values ErrorResponseUnchecked
                                     NonBlankString]]
   [common-swagger-api.malli.data :as data-schema]
   [malli.util :as mu]))

(def StatSummary "File and Folder Status Information")
(def StatDocs
  "This endpoint allows the caller to get information about many files and folders at once.")

(def DataTypeEnum [:enum :file :dir])

(def DataItemIdParam
  [:uuid
   {:description         "The UUID of this data item"
    :json-schema/example #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}])

(def DataItemPathParam
  (as-> NonBlankString s
    (mu/update-properties s assoc :description "The IRODS paths to this data item")
    (mu/update-properties s assoc :json-schema/example "/example/home/janedoe/example.txt")))

(def StatQueryParams
  (mu/closed-schema
   [:map
    [:validation-behavior
     {:optional            true
      :description         "What level of permissions on the queried files should be validated?"
      :json-schema/example :read}
     data-schema/PermissionEnum]]))

(def FilteredStatQueryParams
  (mu/closed-schema
   (mu/merge
    StatQueryParams
    [:map
     [:filter-include
      {:optional            true
       :description         (str "Comma-separated list of keys to generate and return in each stat object. Defaults "
                                 "to all keys. If both this and filter-exclude are provided, includes are processed "
                                 "first, then excludes.")
       :json-schema/example "infoType,path"}
      :string]

     [:filter-exclude
      {:optional            true
       :description         (str "Comma-separated list of keys to exclude from each stat object. Defaults to no keys. "
                                 "If both this and filter-include are provided, includes are processed first, then "
                                 "excludes.")
       :json-schema/example "file-size"}
      :string]])))

(def DataStatInfo
  (mu/closed-schema
   [:map
    [:id DataItemIdParam]

    [:path DataItemPathParam]

    [:type
     {:description         "The data item's type"
      :json-schema/example :file}
     DataTypeEnum]

    [:label
     {:description         "The descriptive label for this item."
      :json-schema/example "example.pl"}
     :string]

    [:date-created
     {:description         "The date this data item was created"
      :json-schema/example 1763771841123}
     :int]

    [:date-modified
     {:description         "The date this data item was last modified"
      :json-schema/example 1763772014456}
     :int]

    [:permission
     {:description         "The requesting user's permissions on this data item"
      :json-schema/example :read}
     data-schema/PermissionEnum]

    [:share-count
     {:optional            true
      :description         (str "The number of other users this data item is shared with (only displayed to users with "
                                "'own' permissions)")
      :json-schema/example 27}
     :int]]))

(def DirStatInfo
  (mu/closed-schema
   (mu/merge
    DataStatInfo
    [:map
     {:description "Information about an iRODS collection."}

     [:file-count
      {:description         "The number of files under this directory"
       :json-schema/example 42}
      :int]

     [:dir-count
      {:description         "The number of subdirectories under this directory"
       :json-schema/example 27}
      :int]])))

(def FileStatInfo
  (mu/closed-schema
   (mu/merge
    DataStatInfo
    [:map
     {:description "Information about an iRODS data item."}

     [:file-size
      {:description         "The size in bytes of this file"
       :json-schema/example 57}
      :int]

     [:content-type
      {:description         "The detected media type of the data contained in this file"
       :json-schema/example "text/plain"}
      NonBlankString]

     [:infoType
      {:description         "The type of contents in this file"
       :json-schema/example "perl"}
      :string]

     [:md5
      {:description         "The md5 hash of this file's contents, as calculated and saved by IRODS"
       :json-schema/example "d5a0bfa9677508d7b379c3e07284a493"}
      :string]])))

(def FilteredStatInfo
  (as-> (mu/merge DirStatInfo FileStatInfo) s
    (mu/optional-keys s)
    (mu/update-properties s assoc :description "The data item's info")))

(def AvailableStatFields
  (mu/keys FilteredStatInfo))

(def FileStat
  (mu/closed-schema
   [:map
    [:file
     {:description "File info"}
     FileStatInfo]]))

;; FIXME: Moving the descriptions for FileStatInfo and DirStatInfo into their respective definitions was the only way
;; That I could get Malli to accept this schema definition. I'm not sure how this will work when we're generating
;; OpenAPI docs, though. We'll have to experiment with this when we migrate endpoints that use this schema to Reitit.
(def PathsMap
  (mu/closed-schema
   [:map-of
    [:keyword
     {:description         "The iRODS data item's path"
      :json-schema/example (keyword ":/example/home/janedoe/file.txt")}]

    ;; We could use `:multi` with a dispatch function here, but `:or` will match the first schema that succeeds, which
    ;; should be suitable for this schema.
    [:or FileStatInfo DirStatInfo]]))

;; FIXME: I moved the description for FilteredStatInfo into its definition becuase of the same limitation that was
;; encountered in PathsMap.
(def FilteredPathsMap
  (mu/closed-schema
   [:map-of
    [:keyword
     {:description         "The iRODS data item's path"
      :json-schema/example (keyword ":/example/home/janedoe/file.txt")}]

    FilteredStatInfo]))

;; FIXME: I moved the value descriptions into the schema definitions becuase of the same limitation that was encountered
;; in PathsMap.
(def DataIdsMap
  (mu/closed-schema
   [:map-of
    [:keyword
     {:description         "The iRODS data item's ID"
      :json-schema/example :3dded710-1bd0-4541-9b1f-6b8c87e31f48}]

    ;; We could use `:multi` with a dispatch function here, but `:or` will match the first schema that succeeds, which
    ;; should be suitable for this schema.
    [:or FileStatInfo DirStatInfo]]))

;; FIXME: I moved the description for FilteredStatInfo into its definition becuase of the same limitation that was
;; encountered in PathsMap.
(def FilteredDataIdsMap
  (mu/closed-schema
   [:map-of
    [:keyword
     {:description         "The iRODS data item's ID"
      :json-schema/example :5bab6167-df4a-4bff-a4cd-96dbe484755b}]

    FilteredStatInfo]))

(def StatusInfo
  (mu/closed-schema
   [:map
    [:paths
     {:optional    true
      :description "Paths info"}
     PathsMap]

    [:ids
     {:optional    true
      :description "IDs info"}
     DataIdsMap]]))

(def FilteredStatusInfo
  (mu/closed-schema
   [:map
    [:paths
     {:optional    true
      :description "Paths info"}
     FilteredPathsMap]

    [:ids
     {:optional    true
      :description "IDs info"}
     FilteredDataIdsMap]]))

;; Note: I omitted the StatResponsePathsMap, StatResponseIdsMap, and StatResponse schema definitions here because I
;; believe that the possibility to add example values in Malli makes them unnecessary.

(def StatErrorResponses
(mu/update
  ErrorResponseUnchecked
  :error_code
  add-enum-values
  ce/ERR_DOES_NOT_EXIST
  ce/ERR_NOT_READABLE
  ce/ERR_NOT_WRITEABLE
  ce/ERR_NOT_OWNER
  ce/ERR_NOT_A_USER
  ce/ERR_TOO_MANY_RESULTS))

;; Note: I omitted StatResponses here because I think that Reitit handles schemas for different response codes a little
;; differently. We can add support for that later.
