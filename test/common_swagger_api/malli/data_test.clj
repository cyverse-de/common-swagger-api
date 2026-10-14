(ns common-swagger-api.malli.data-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.data :as data]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]))

(def data-id #uuid "8a950a63-f999-403c-912e-97e11109e68e")

(def paths {:paths ["/example/home/username/foo.txt" "/example/home/username/bar.txt"]})

(def ids {:ids [data-id]})

(deftest CommonErrorCodes
  (is (= ["ERR_UNCHECKED_EXCEPTION" "ERR_SCHEMA_VALIDATION"] data/CommonErrorCodeResponses))
  (is (string? data/CommonErrorCodeDocs)))

(deftest DataIdPathParam
  (valid data/DataIdPathParam data-id)
  (invalid data/DataIdPathParam (str data-id) nil))

(deftest PermissionEnum
  (valid data/PermissionEnum :read :write :own)
  (invalid data/PermissionEnum "read" :none nil))

(deftest Paths
  (valid data/Paths paths {:paths ["/example/home/username/foo.txt"]})
  (invalid data/Paths {} {:paths "x"} {:paths []} {:paths [" "]} (assoc paths :extra 1)))

(deftest OptionalPaths
  (valid data/OptionalPaths {} paths {:paths []})
  (invalid data/OptionalPaths {:paths " "} {:paths [" "]} (assoc paths :extra 1)))

(deftest DataIds
  (valid data/DataIds ids {:ids []})
  (invalid data/DataIds {} {:ids "x"} {:ids [(str data-id)]} (assoc ids :extra 1)))

(deftest OptionalPathsOrDataIds
  (valid data/OptionalPathsOrDataIds {} paths ids (merge paths ids))
  (invalid data/OptionalPathsOrDataIds {:ids "x"} {:ids [(str data-id)]} {:paths [" "]} (assoc ids :extra 1)))

(deftest ValidFolderListingSortFields
  (is (= #{:datecreated :datemodified :name :path :size} data/ValidFolderListingSortFields)))

(deftest FolderListingPagingParams
  (valid data/FolderListingPagingParams {} {:limit 50 :offset 0 :sort-dir "ASC"} {:sort-field :datemodified})
  (invalid data/FolderListingPagingParams
           {:sort-field "datemodified"}
           {:sort-field :nope}
           {:limit 0}
           {:extra 1}))

(deftest FolderListingParams
  (valid data/FolderListingParams
         {}
         {:sort-field :name :entity-type :file}
         {:info-type "fasta" :entity-type :any}
         {:info-type ["fasta" "csv"] :entity-type :folder})
  (invalid data/FolderListingParams
           {:entity-type "file"}
           {:entity-type :nope}
           {:info-type "nope"}
           {:info-type [1]}
           {:extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.data))

(deftest examples
  (examples-valid 'common-swagger-api.malli.data))
