(ns common-swagger-api.malli.stats-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common-swagger-api.malli.stats :as stats]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(def invalid? (complement valid?))

(deftest test-DataTypeEnum
  (testing "DataTypeEnum validation"
    (testing "valid data types"
      (is (valid? stats/DataTypeEnum :file))
      (is (valid? stats/DataTypeEnum :dir)))

    (testing "invalid data types"
      ;; Only :file and :dir are valid
      (is (invalid? stats/DataTypeEnum :folder))
      (is (invalid? stats/DataTypeEnum :directory))
      (is (invalid? stats/DataTypeEnum "file"))
      (is (invalid? stats/DataTypeEnum "dir"))
      (is (invalid? stats/DataTypeEnum nil))
      (is (invalid? stats/DataTypeEnum :unknown)))))

(deftest test-DataItemIdParam
  (testing "DataItemIdParam validation"
    (testing "valid UUIDs"
      (is (valid? stats/DataItemIdParam #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"))
      (is (valid? stats/DataItemIdParam #uuid "8a950a63-f999-403c-912e-97e11109e68e"))
      (is (valid? stats/DataItemIdParam #uuid "00000000-0000-0000-0000-000000000000")))

    (testing "invalid data item IDs"
      ;; Must be a UUID type, not a string
      (is (invalid? stats/DataItemIdParam "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"))
      (is (invalid? stats/DataItemIdParam "not-a-uuid"))
      (is (invalid? stats/DataItemIdParam nil))
      (is (invalid? stats/DataItemIdParam 123))
      (is (invalid? stats/DataItemIdParam {})))))

(deftest test-DataItemPathParam
  (testing "DataItemPathParam validation"
    (testing "valid paths"
      (is (valid? stats/DataItemPathParam "/example/home/janedoe/example.txt"))
      (is (valid? stats/DataItemPathParam "/iplant/home/user/file.csv"))
      (is (valid? stats/DataItemPathParam "/a/b/c"))
      (is (valid? stats/DataItemPathParam "relative/path.txt"))
      (is (valid? stats/DataItemPathParam "a")))

    (testing "invalid paths"
      ;; NonBlankString validator should reject empty and whitespace-only strings
      (is (invalid? stats/DataItemPathParam ""))
      (is (invalid? stats/DataItemPathParam "   "))
      (is (invalid? stats/DataItemPathParam nil))
      (is (invalid? stats/DataItemPathParam 123))
      (is (invalid? stats/DataItemPathParam {})))))

(deftest test-StatQueryParams
  (testing "StatQueryParams validation"
    (testing "valid query params"
      ;; Empty map is valid since validation-behavior is optional
      (is (valid? stats/StatQueryParams {}))

      (is (valid? stats/StatQueryParams
                  {:validation-behavior :read}))
      (is (valid? stats/StatQueryParams
                  {:validation-behavior :write}))
      (is (valid? stats/StatQueryParams
                  {:validation-behavior :own})))

    (testing "invalid query params"
      ;; Extra fields not allowed due to :closed true
      (is (invalid? stats/StatQueryParams
                    {:validation-behavior :read
                     :extra-field "not allowed"}))

      ;; Invalid permission value
      (is (invalid? stats/StatQueryParams
                    {:validation-behavior :admin}))
      (is (invalid? stats/StatQueryParams
                    {:validation-behavior "read"}))
      (is (invalid? stats/StatQueryParams
                    {:validation-behavior nil})))))

(deftest test-FilteredStatQueryParams
  (testing "FilteredStatQueryParams validation"
    (testing "valid filtered query params"
      ;; Empty map is valid since validation-behavior is optional
      (is (valid? stats/FilteredStatQueryParams {}))

      ;; Just validation-behavior
      (is (valid? stats/FilteredStatQueryParams
                  {:validation-behavior :read}))

      ;; With filter-include
      (is (valid? stats/FilteredStatQueryParams
                  {:validation-behavior :read
                   :filter-include "infoType,path"}))

      ;; With filter-exclude
      (is (valid? stats/FilteredStatQueryParams
                  {:validation-behavior :write
                   :filter-exclude "file-size"}))

      ;; With both filters
      (is (valid? stats/FilteredStatQueryParams
                  {:validation-behavior :own
                   :filter-include "infoType,path"
                   :filter-exclude "file-size"}))

      ;; Empty filter strings are valid
      (is (valid? stats/FilteredStatQueryParams
                  {:validation-behavior :read
                   :filter-include ""}))

      ;; Single field filter
      (is (valid? stats/FilteredStatQueryParams
                  {:validation-behavior :read
                   :filter-include "path"}))

      ;; Just filter fields without validation-behavior
      (is (valid? stats/FilteredStatQueryParams
                  {:filter-include "path"}))

      (is (valid? stats/FilteredStatQueryParams
                  {:filter-exclude "file-size"})))

    (testing "invalid filtered query params"
      ;; Extra fields not allowed due to :closed true
      (is (invalid? stats/FilteredStatQueryParams
                    {:validation-behavior :read
                     :extra-field "not allowed"}))

      ;; Wrong type for filter-include
      (is (invalid? stats/FilteredStatQueryParams
                    {:validation-behavior :read
                     :filter-include 123}))

      ;; Wrong type for filter-exclude
      (is (invalid? stats/FilteredStatQueryParams
                    {:validation-behavior :read
                     :filter-exclude [:path :size]})))))

(deftest test-DataStatInfo
  (testing "DataStatInfo validation"
    (testing "valid data stat info"
      ;; Minimal valid data stat (without optional share-count)
      (is (valid? stats/DataStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/example/home/janedoe/example.txt"
                   :type :file
                   :label "example.pl"
                   :date-created 1763771841123
                   :date-modified 1763772014456
                   :permission :read}))

      ;; With optional share-count
      (is (valid? stats/DataStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/example/home/janedoe/example.txt"
                   :type :dir
                   :label "example.pl"
                   :date-created 1763771841123
                   :date-modified 1763772014456
                   :permission :own
                   :share-count 27}))

      ;; Zero share-count is valid
      (is (valid? stats/DataStatInfo
                  {:id #uuid "00000000-0000-0000-0000-000000000000"
                   :path "/path"
                   :type :file
                   :label "label"
                   :date-created 0
                   :date-modified 0
                   :permission :write
                   :share-count 0}))

      ;; Different permission levels
      (is (valid? stats/DataStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/path"
                   :type :file
                   :label "test"
                   :date-created 1000
                   :date-modified 2000
                   :permission :write})))

    (testing "invalid data stat info"
      ;; Missing required id
      (is (invalid? stats/DataStatInfo
                    {:path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read}))

      ;; Missing required path
      (is (invalid? stats/DataStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read}))

      ;; Missing required type
      (is (invalid? stats/DataStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read}))

      ;; Missing required label
      (is (invalid? stats/DataStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :date-created 1000
                     :date-modified 2000
                     :permission :read}))

      ;; Missing required date-created
      (is (invalid? stats/DataStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-modified 2000
                     :permission :read}))

      ;; Missing required date-modified
      (is (invalid? stats/DataStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :permission :read}))

      ;; Missing required permission
      (is (invalid? stats/DataStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000}))

      ;; Extra fields not allowed due to :closed true
      (is (invalid? stats/DataStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :extra-field "not allowed"}))

      ;; Wrong type for id
      (is (invalid? stats/DataStatInfo
                    {:id "not-a-uuid"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read}))

      ;; Wrong type for path
      (is (invalid? stats/DataStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path 123
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read}))

      ;; Empty string for path (NonBlankString)
      (is (invalid? stats/DataStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path ""
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read}))

      ;; Wrong type enum value
      (is (invalid? stats/DataStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :folder
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read}))

      ;; Wrong permission enum value
      (is (invalid? stats/DataStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :admin}))

      ;; Wrong type for date-created (string instead of int)
      (is (invalid? stats/DataStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created "1000"
                     :date-modified 2000
                     :permission :read}))

      ;; Wrong type for share-count
      (is (invalid? stats/DataStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :share-count "27"})))))

(deftest test-DirStatInfo
  (testing "DirStatInfo validation"
    (testing "valid directory stat info"
      ;; Minimal valid directory stat
      (is (valid? stats/DirStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/example/home/janedoe/mydir"
                   :type :dir
                   :label "mydir"
                   :date-created 1763771841123
                   :date-modified 1763772014456
                   :permission :read
                   :file-count 42
                   :dir-count 27}))

      ;; With optional share-count
      (is (valid? stats/DirStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/example/home/janedoe/mydir"
                   :type :dir
                   :label "mydir"
                   :date-created 1763771841123
                   :date-modified 1763772014456
                   :permission :own
                   :share-count 5
                   :file-count 42
                   :dir-count 27}))

      ;; Zero counts are valid
      (is (valid? stats/DirStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/empty/dir"
                   :type :dir
                   :label "dir"
                   :date-created 0
                   :date-modified 0
                   :permission :write
                   :file-count 0
                   :dir-count 0})))

    (testing "invalid directory stat info"
      ;; Missing required file-count
      (is (invalid? stats/DirStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :dir
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :dir-count 10}))

      ;; Missing required dir-count
      (is (invalid? stats/DirStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :dir
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :file-count 10}))

      ;; Missing base DataStatInfo fields
      (is (invalid? stats/DirStatInfo
                    {:file-count 42
                     :dir-count 27}))

      ;; Extra fields not allowed due to :closed true
      (is (invalid? stats/DirStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :dir
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :file-count 42
                     :dir-count 27
                     :extra-field "not allowed"}))

      ;; Wrong type for file-count
      (is (invalid? stats/DirStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :dir
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :file-count "42"
                     :dir-count 27}))

      ;; Wrong type for dir-count
      (is (invalid? stats/DirStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :dir
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :file-count 42
                     :dir-count "27"})))))

(deftest test-FileStatInfo
  (testing "FileStatInfo validation"
    (testing "valid file stat info"
      ;; Complete valid file stat
      (is (valid? stats/FileStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/example/home/janedoe/example.txt"
                   :type :file
                   :label "example.pl"
                   :date-created 1763771841123
                   :date-modified 1763772014456
                   :permission :read
                   :file-size 57
                   :content-type "text/plain"
                   :infoType "perl"
                   :md5 "d5a0bfa9677508d7b379c3e07284a493"}))

      ;; With optional share-count
      (is (valid? stats/FileStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/example/home/janedoe/example.txt"
                   :type :file
                   :label "example.pl"
                   :date-created 1763771841123
                   :date-modified 1763772014456
                   :permission :own
                   :share-count 10
                   :file-size 57
                   :content-type "text/plain"
                   :infoType "perl"
                   :md5 "d5a0bfa9677508d7b379c3e07284a493"}))

      ;; Zero file-size is valid
      (is (valid? stats/FileStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/empty/file"
                   :type :file
                   :label "file"
                   :date-created 0
                   :date-modified 0
                   :permission :write
                   :file-size 0
                   :content-type "text/plain"
                   :infoType ""
                   :md5 ""}))

      ;; Empty strings for string fields are valid (not NonBlankString)
      (is (valid? stats/FileStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/path"
                   :type :file
                   :label "file"
                   :date-created 1000
                   :date-modified 2000
                   :permission :read
                   :file-size 100
                   :content-type "a"
                   :infoType ""
                   :md5 ""})))

    (testing "invalid file stat info"
      ;; Missing required file-size
      (is (invalid? stats/FileStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :content-type "text/plain"
                     :infoType "text"
                     :md5 "abc123"}))

      ;; Missing required content-type
      (is (invalid? stats/FileStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :file-size 100
                     :infoType "text"
                     :md5 "abc123"}))

      ;; Missing required infoType
      (is (invalid? stats/FileStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :file-size 100
                     :content-type "text/plain"
                     :md5 "abc123"}))

      ;; Missing required md5
      (is (invalid? stats/FileStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :file-size 100
                     :content-type "text/plain"
                     :infoType "text"}))

      ;; Missing base DataStatInfo fields
      (is (invalid? stats/FileStatInfo
                    {:file-size 100
                     :content-type "text/plain"
                     :infoType "text"
                     :md5 "abc123"}))

      ;; Extra fields not allowed due to :closed true
      (is (invalid? stats/FileStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :file-size 57
                     :content-type "text/plain"
                     :infoType "perl"
                     :md5 "abc123"
                     :extra-field "not allowed"}))

      ;; Wrong type for file-size
      (is (invalid? stats/FileStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :file-size "100"
                     :content-type "text/plain"
                     :infoType "text"
                     :md5 "abc123"}))

      ;; Empty string for content-type (NonBlankString)
      (is (invalid? stats/FileStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :file-size 100
                     :content-type ""
                     :infoType "text"
                     :md5 "abc123"}))

      ;; Whitespace-only string for content-type (NonBlankString)
      (is (invalid? stats/FileStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :file-size 100
                     :content-type "   "
                     :infoType "text"
                     :md5 "abc123"}))

      ;; Wrong type for infoType
      (is (invalid? stats/FileStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :file-size 100
                     :content-type "text/plain"
                     :infoType 123
                     :md5 "abc123"}))

      ;; Wrong type for md5
      (is (invalid? stats/FileStatInfo
                    {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     :path "/path"
                     :type :file
                     :label "label"
                     :date-created 1000
                     :date-modified 2000
                     :permission :read
                     :file-size 100
                     :content-type "text/plain"
                     :infoType "text"
                     :md5 123})))))

(deftest test-FilteredStatInfo
  (testing "FilteredStatInfo validation"
    (testing "valid filtered stat info"
      ;; Can be empty map since all keys are optional
      (is (valid? stats/FilteredStatInfo {}))

      ;; Can have any combination of DirStatInfo fields
      (is (valid? stats/FilteredStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/path"
                   :type :dir
                   :file-count 10
                   :dir-count 5}))

      ;; Can have any combination of FileStatInfo fields
      (is (valid? stats/FilteredStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/path"
                   :type :file
                   :file-size 100
                   :content-type "text/plain"}))

      ;; Can have mix of both dir and file fields (even though semantically odd)
      (is (valid? stats/FilteredStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/path"
                   :file-count 10
                   :file-size 100}))

      ;; Can have just a subset of fields
      (is (valid? stats/FilteredStatInfo
                  {:path "/path"
                   :type :file}))

      ;; Can have a single field
      (is (valid? stats/FilteredStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"})))

    (testing "invalid filtered stat info"
      ;; Wrong type for any field still causes validation failure
      (is (invalid? stats/FilteredStatInfo
                    {:id "not-a-uuid"}))

      (is (invalid? stats/FilteredStatInfo
                    {:path 123}))

      (is (invalid? stats/FilteredStatInfo
                    {:type :folder}))

      (is (invalid? stats/FilteredStatInfo
                    {:file-count "10"}))

      (is (invalid? stats/FilteredStatInfo
                    {:file-size "100"}))

      (is (invalid? stats/FilteredStatInfo
                    {:content-type 123}))

      ;; Empty string for NonBlankString fields
      (is (invalid? stats/FilteredStatInfo
                    {:path ""}))

      (is (invalid? stats/FilteredStatInfo
                    {:content-type ""})))))

(deftest test-FileStat
  (testing "FileStat validation"
    (testing "valid file stat"
      ;; Complete file stat
      (is (valid? stats/FileStat
                  {:file {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/example/home/janedoe/example.txt"
                          :type :file
                          :label "example.pl"
                          :date-created 1763771841123
                          :date-modified 1763772014456
                          :permission :read
                          :file-size 57
                          :content-type "text/plain"
                          :infoType "perl"
                          :md5 "d5a0bfa9677508d7b379c3e07284a493"}}))

      ;; With optional share-count
      (is (valid? stats/FileStat
                  {:file {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/path"
                          :type :file
                          :label "file"
                          :date-created 1000
                          :date-modified 2000
                          :permission :own
                          :share-count 5
                          :file-size 100
                          :content-type "text/plain"
                          :infoType "text"
                          :md5 "abc123"}})))

    (testing "invalid file stat"
      ;; Missing required :file key
      (is (invalid? stats/FileStat {}))

      ;; Extra fields not allowed due to :closed true
      (is (invalid? stats/FileStat
                    {:file {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/path"
                            :type :file
                            :label "file"
                            :date-created 1000
                            :date-modified 2000
                            :permission :read
                            :file-size 100
                            :content-type "text/plain"
                            :infoType "text"
                            :md5 "abc123"}
                     :extra-field "not allowed"}))

      ;; Invalid FileStatInfo
      (is (invalid? stats/FileStat
                    {:file {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/path"
                            :type :file
                            :label "file"
                            :date-created 1000
                            :date-modified 2000
                            :permission :read}}))

      ;; Wrong type for :file value
      (is (invalid? stats/FileStat
                    {:file "not a map"}))

      (is (invalid? stats/FileStat
                    {:file nil})))))

(deftest test-PathsMap
  (testing "PathsMap validation"
    (testing "valid paths maps"
      ;; Empty map is valid
      (is (valid? stats/PathsMap {}))

      ;; Single entry with FileStatInfo
      (is (valid? stats/PathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/file.txt"
                    :type :file
                    :label "file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-size 57
                    :content-type "text/plain"
                    :infoType "perl"
                    :md5 "d5a0bfa9677508d7b379c3e07284a493"}}))

      ;; Single entry with DirStatInfo
      (is (valid? stats/PathsMap
                  {(keyword ":/iplant/home/janedoe/mydir")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/mydir"
                    :type :dir
                    :label "mydir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-count 42
                    :dir-count 27}}))

      ;; Multiple entries with FileStatInfo
      (is (valid? stats/PathsMap
                  {(keyword ":/iplant/home/janedoe/file1.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/file1.txt"
                    :type :file
                    :label "file1.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   (keyword ":/iplant/home/janedoe/file2.txt")
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/iplant/home/janedoe/file2.txt"
                    :type :file
                    :label "file2.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :write
                    :file-size 200
                    :content-type "application/json"
                    :infoType "json"
                    :md5 "def456"}}))

      ;; Multiple entries with DirStatInfo
      (is (valid? stats/PathsMap
                  {(keyword ":/iplant/home/janedoe/dir1")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/dir1"
                    :type :dir
                    :label "dir1"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-count 10
                    :dir-count 5}
                   (keyword ":/iplant/home/janedoe/dir2")
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/iplant/home/janedoe/dir2"
                    :type :dir
                    :label "dir2"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :file-count 20
                    :dir-count 15}}))

      ;; Mixed FileStatInfo and DirStatInfo entries
      (is (valid? stats/PathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/file.txt"
                    :type :file
                    :label "file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   (keyword ":/iplant/home/janedoe/mydir")
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/iplant/home/janedoe/mydir"
                    :type :dir
                    :label "mydir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :write
                    :file-count 42
                    :dir-count 27}}))

      ;; With optional share-count in FileStatInfo
      (is (valid? stats/PathsMap
                  {(keyword ":/iplant/home/janedoe/shared-file.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/shared-file.txt"
                    :type :file
                    :label "shared-file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :share-count 10
                    :file-size 57
                    :content-type "text/plain"
                    :infoType "perl"
                    :md5 "d5a0bfa9677508d7b379c3e07284a493"}}))

      ;; With optional share-count in DirStatInfo
      (is (valid? stats/PathsMap
                  {(keyword ":/iplant/home/janedoe/shared-dir")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/shared-dir"
                    :type :dir
                    :label "shared-dir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :share-count 5
                    :file-count 42
                    :dir-count 27}}))

      ;; Various keyword path formats
      (is (valid? stats/PathsMap
                  {(keyword ":/example/home/user/test.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/example/home/user/test.txt"
                    :type :file
                    :label "test.txt"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   (keyword ":/a/b/c")
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/a/b/c"
                    :type :dir
                    :label "c"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-count 0
                    :dir-count 0}})))

    (testing "invalid paths maps"
      ;; String keys instead of keywords
      (is (invalid? stats/PathsMap
                    {"/iplant/home/janedoe/file.txt"
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Integer keys instead of keywords
      (is (invalid? stats/PathsMap
                    {123
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/path"
                      :type :file
                      :label "file"
                      :date-created 1000
                      :date-modified 2000
                      :permission :read
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Invalid FileStatInfo value - missing required field
      (is (invalid? stats/PathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      ;; Missing file-size
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Invalid DirStatInfo value - missing required field
      (is (invalid? stats/PathsMap
                    {(keyword ":/iplant/home/janedoe/mydir")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/mydir"
                      :type :dir
                      :label "mydir"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-count 42
                      ;; Missing dir-count
                      }}))

      ;; Extra fields in value not allowed due to :closed true
      (is (invalid? stats/PathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"
                      :extra-field "not allowed"}}))

      ;; Wrong type for value - string instead of map
      (is (invalid? stats/PathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt") "not a map"}))

      ;; Wrong type for value - nil
      (is (invalid? stats/PathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt") nil}))

      ;; Value doesn't match FileStatInfo or DirStatInfo
      (is (invalid? stats/PathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read}}))

      ;; Mixed valid and invalid entries
      (is (invalid? stats/PathsMap
                    {(keyword ":/iplant/home/janedoe/valid-file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/valid-file.txt"
                      :type :file
                      :label "valid-file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}
                     (keyword ":/iplant/home/janedoe/invalid-file.txt")
                     {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                      :path "/iplant/home/janedoe/invalid-file.txt"
                      :type :file
                      :label "invalid-file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      ;; Missing required fields
                      }}))

      ;; Wrong type in nested FileStatInfo field
      (is (invalid? stats/PathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-size "not-a-number"
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Wrong type in nested DirStatInfo field
      (is (invalid? stats/PathsMap
                    {(keyword ":/iplant/home/janedoe/mydir")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/mydir"
                      :type :dir
                      :label "mydir"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-count "not-a-number"
                      :dir-count 27}}))

      ;; Invalid enum value in nested stat info
      (is (invalid? stats/PathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :folder  ;; Invalid type
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Empty string for NonBlankString field in nested stat info
      (is (invalid? stats/PathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-size 100
                      :content-type ""  ;; Empty string for NonBlankString
                      :infoType "text"
                      :md5 "abc123"}})))))

(deftest test-edge-cases
  (testing "Edge cases and boundary conditions"
    (testing "very large timestamps and counts"
      (is (valid? stats/DataStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/path"
                   :type :file
                   :label "label"
                   :date-created 999999999999999
                   :date-modified 999999999999999
                   :permission :read}))

      (is (valid? stats/DirStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/path"
                   :type :dir
                   :label "label"
                   :date-created 1000
                   :date-modified 2000
                   :permission :read
                   :file-count 999999999
                   :dir-count 999999999}))

      (is (valid? stats/FileStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/path"
                   :type :file
                   :label "label"
                   :date-created 1000
                   :date-modified 2000
                   :permission :read
                   :file-size 999999999999999
                   :content-type "text/plain"
                   :infoType "text"
                   :md5 "abc123"})))

    (testing "negative timestamps are valid for :int"
      (is (valid? stats/DataStatInfo
                  {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                   :path "/path"
                   :type :file
                   :label "label"
                   :date-created -1
                   :date-modified -1
                   :permission :read})))

    (testing "special UUID values"
      ;; All zeros UUID
      (is (valid? stats/DataItemIdParam #uuid "00000000-0000-0000-0000-000000000000"))

      ;; All fs UUID
      (is (valid? stats/DataItemIdParam #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff")))

    (testing "very long paths"
      (is (valid? stats/DataItemPathParam
                  (apply str (repeat 1000 "a")))))

    (testing "paths with special characters"
      (is (valid? stats/DataItemPathParam "/path/with spaces/file.txt"))
      (is (valid? stats/DataItemPathParam "/path/with-dashes/file.txt"))
      (is (valid? stats/DataItemPathParam "/path/with_underscores/file.txt"))
      (is (valid? stats/DataItemPathParam "/path/with.dots/file.txt"))
      (is (valid? stats/DataItemPathParam "/path/with@symbols/file.txt")))

    (testing "filter strings with various formats"
      (is (valid? stats/FilteredStatQueryParams
                  {:validation-behavior :read
                   :filter-include "a,b,c,d,e"}))

      (is (valid? stats/FilteredStatQueryParams
                  {:validation-behavior :read
                   :filter-include "single-field"}))

      (is (valid? stats/FilteredStatQueryParams
                  {:validation-behavior :read
                   :filter-include "field-with-dashes,another-field"}))

      ;; Very long filter string
      (is (valid? stats/FilteredStatQueryParams
                  {:validation-behavior :read
                   :filter-include (apply str (repeat 100 "field,"))})))))

(deftest test-FilteredPathsMap
  (testing "FilteredPathsMap validation"
    (testing "valid filtered paths maps"
      ;; Empty map is valid
      (is (valid? stats/FilteredPathsMap {}))

      ;; Single entry with all FileStatInfo fields
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/file.txt"
                    :type :file
                    :label "file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-size 57
                    :content-type "text/plain"
                    :infoType "perl"
                    :md5 "d5a0bfa9677508d7b379c3e07284a493"}}))

      ;; Single entry with all DirStatInfo fields
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/mydir")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/mydir"
                    :type :dir
                    :label "mydir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-count 42
                    :dir-count 27}}))

      ;; Single entry with optional share-count in file stat
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/shared-file.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/shared-file.txt"
                    :type :file
                    :label "shared-file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :share-count 10
                    :file-size 57
                    :content-type "text/plain"
                    :infoType "perl"
                    :md5 "d5a0bfa9677508d7b379c3e07284a493"}}))

      ;; Single entry with optional share-count in dir stat
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/shared-dir")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/shared-dir"
                    :type :dir
                    :label "shared-dir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :share-count 5
                    :file-count 42
                    :dir-count 27}}))

      ;; Multiple entries with FileStatInfo
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file1.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/file1.txt"
                    :type :file
                    :label "file1.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   (keyword ":/iplant/home/janedoe/file2.txt")
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/iplant/home/janedoe/file2.txt"
                    :type :file
                    :label "file2.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :write
                    :file-size 200
                    :content-type "application/json"
                    :infoType "json"
                    :md5 "def456"}}))

      ;; Multiple entries with DirStatInfo
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/dir1")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/dir1"
                    :type :dir
                    :label "dir1"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-count 10
                    :dir-count 5}
                   (keyword ":/iplant/home/janedoe/dir2")
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/iplant/home/janedoe/dir2"
                    :type :dir
                    :label "dir2"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :file-count 20
                    :dir-count 15}}))

      ;; Mixed FileStatInfo and DirStatInfo entries
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/file.txt"
                    :type :file
                    :label "file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   (keyword ":/iplant/home/janedoe/mydir")
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/iplant/home/janedoe/mydir"
                    :type :dir
                    :label "mydir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :write
                    :file-count 42
                    :dir-count 27}}))

      ;; Entry with empty map value (all fields optional in FilteredStatInfo)
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/filtered.txt") {}}))

      ;; Entry with only id field
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}}))

      ;; Entry with only path field
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:path "/iplant/home/janedoe/file.txt"}}))

      ;; Entry with only type field
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:type :file}}))

      ;; Entry with subset of fields - path and type only
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:path "/iplant/home/janedoe/file.txt"
                    :type :file}}))

      ;; Entry with subset of fields - id, path, and type
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/file.txt"
                    :type :file}}))

      ;; Entry with file-specific fields only
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:file-size 100
                    :content-type "text/plain"}}))

      ;; Entry with dir-specific fields only
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/mydir")
                   {:file-count 42
                    :dir-count 27}}))

      ;; Entry with mix of file and dir fields (semantically odd but valid for filtering)
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/mixed")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/mixed"
                    :file-count 10
                    :file-size 100}}))

      ;; Entry with just permission field
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:permission :read}}))

      ;; Entry with just timestamps
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:date-created 1763771841123
                    :date-modified 1763772014456}}))

      ;; Entry with various field combinations
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:infoType "perl"
                    :md5 "d5a0bfa9677508d7b379c3e07284a493"}}))

      ;; Multiple entries with different field subsets
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file1.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/file1.txt"}
                   (keyword ":/iplant/home/janedoe/file2.txt")
                   {:type :file
                    :file-size 100}
                   (keyword ":/iplant/home/janedoe/dir1")
                   {:file-count 10
                    :dir-count 5}
                   (keyword ":/iplant/home/janedoe/file3.txt")
                   {}}))

      ;; Entry with zero values
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/empty-file.txt")
                   {:file-size 0
                    :share-count 0
                    :date-created 0
                    :date-modified 0}}))

      ;; Entry with all permission levels
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/read-file.txt")
                   {:permission :read}
                   (keyword ":/iplant/home/janedoe/write-file.txt")
                   {:permission :write}
                   (keyword ":/iplant/home/janedoe/own-file.txt")
                   {:permission :own}}))

      ;; Entry with both data types
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:type :file}
                   (keyword ":/iplant/home/janedoe/mydir")
                   {:type :dir}}))

      ;; Various keyword path formats
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/example/home/user/test.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}
                   (keyword ":/a/b/c")
                   {:path "/a/b/c"}
                   (keyword ":/very/long/path/to/some/deeply/nested/file.txt")
                   {:type :file}}))

      ;; Entry with empty string fields (valid for non-NonBlankString fields)
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:infoType ""
                    :md5 ""
                    :label ""}}))

      ;; Entry with complete file stat info (all file fields present)
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/complete-file.txt")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/complete-file.txt"
                    :type :file
                    :label "complete-file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :share-count 10
                    :file-size 57
                    :content-type "text/plain"
                    :infoType "perl"
                    :md5 "d5a0bfa9677508d7b379c3e07284a493"}}))

      ;; Entry with complete dir stat info (all dir fields present)
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/complete-dir")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/complete-dir"
                    :type :dir
                    :label "complete-dir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :share-count 5
                    :file-count 42
                    :dir-count 27}})))

    (testing "invalid filtered paths maps"
      ;; String keys instead of keywords
      (is (invalid? stats/FilteredPathsMap
                    {"/iplant/home/janedoe/file.txt"
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}}))

      ;; Integer keys instead of keywords
      (is (invalid? stats/FilteredPathsMap
                    {123
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}}))

      ;; Symbol keys instead of keywords
      (is (invalid? stats/FilteredPathsMap
                    {'path
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}}))

      ;; Wrong type for id field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:id "not-a-uuid"}}))

      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:id 123}}))

      ;; Wrong type for path field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:path 123}}))

      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:path nil}}))

      ;; Empty string for path (NonBlankString field)
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:path ""}}))

      ;; Whitespace-only string for path (NonBlankString field)
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:path "   "}}))

      ;; Wrong type for type field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:type :folder}}))

      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:type "file"}}))

      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:type :directory}}))

      ;; Wrong type for label field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:label 123}}))

      ;; Wrong type for date-created field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:date-created "1763771841123"}}))

      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:date-created 1763771841123.5}}))

      ;; Wrong type for date-modified field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:date-modified "1763772014456"}}))

      ;; Wrong type for permission field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:permission :admin}}))

      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:permission "read"}}))

      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:permission nil}}))

      ;; Wrong type for share-count field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:share-count "27"}}))

      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:share-count 27.5}}))

      ;; Wrong type for file-count field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/mydir")
                     {:file-count "42"}}))

      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/mydir")
                     {:file-count nil}}))

      ;; Wrong type for dir-count field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/mydir")
                     {:dir-count "27"}}))

      ;; Wrong type for file-size field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:file-size "100"}}))

      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:file-size nil}}))

      ;; Wrong type for content-type field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:content-type 123}}))

      ;; Empty string for content-type (NonBlankString field)
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:content-type ""}}))

      ;; Whitespace-only string for content-type (NonBlankString field)
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:content-type "   "}}))

      ;; Wrong type for infoType field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:infoType 123}}))

      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:infoType nil}}))

      ;; Wrong type for md5 field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:md5 123}}))

      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:md5 nil}}))

      ;; Extra fields not allowed due to :closed true from DirStatInfo and FileStatInfo
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/file.txt"
                      :extra-field "not allowed"}}))

      ;; Wrong type for value - string instead of map
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt") "not a map"}))

      ;; Wrong type for value - nil
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt") nil}))

      ;; Wrong type for value - vector
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt") []}))

      ;; Mixed valid and invalid entries - one entry has wrong type
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/valid-file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/valid-file.txt"
                      :type :file}
                     (keyword ":/iplant/home/janedoe/invalid-file.txt")
                     {:id "not-a-uuid"
                      :path "/iplant/home/janedoe/invalid-file.txt"
                      :type :file}}))

      ;; Mixed valid and invalid entries - one entry has invalid enum value
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/valid-file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :type :file}
                     (keyword ":/iplant/home/janedoe/invalid-file.txt")
                     {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                      :type :folder}}))

      ;; Mixed valid and invalid entries - one entry has extra field
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/valid-file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}
                     (keyword ":/iplant/home/janedoe/invalid-file.txt")
                     {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                      :extra-field "not allowed"}}))

      ;; Wrong field type in nested value
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :file-size "not-a-number"}}))

      ;; Invalid enum value in nested value
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :permission :admin}}))

      ;; Empty string for NonBlankString field in nested value
      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path ""}}))

      (is (invalid? stats/FilteredPathsMap
                    {(keyword ":/iplant/home/janedoe/file.txt")
                     {:content-type ""}})))

    (testing "edge cases"
      ;; Very large integers
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:date-created 999999999999999
                    :date-modified 999999999999999
                    :file-size 999999999999999
                    :file-count 999999999
                    :dir-count 999999999
                    :share-count 999999999}}))

      ;; Negative integers (valid for :int type)
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:date-created -1
                    :date-modified -1}}))

      ;; Special UUID values
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file1.txt")
                   {:id #uuid "00000000-0000-0000-0000-000000000000"}
                   (keyword ":/iplant/home/janedoe/file2.txt")
                   {:id #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff"}}))

      ;; Very long paths
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:path (apply str (repeat 1000 "a"))}}))

      ;; Paths with special characters
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file with spaces.txt")
                   {:path "/path/with spaces/file.txt"}
                   (keyword ":/iplant/home/janedoe/file-with-dashes.txt")
                   {:path "/path/with-dashes/file.txt"}
                   (keyword ":/iplant/home/janedoe/file_with_underscores.txt")
                   {:path "/path/with_underscores/file.txt"}
                   (keyword ":/iplant/home/janedoe/file.with.dots.txt")
                   {:path "/path/with.dots/file.txt"}
                   (keyword ":/iplant/home/janedoe/file@with@symbols.txt")
                   {:path "/path/with@symbols/file.txt"}}))

      ;; Large number of entries
      (is (valid? stats/FilteredPathsMap
                  (into {}
                        (for [i (range 100)]
                          [(keyword (str ":/" i))
                           {:id (java.util.UUID/randomUUID)
                            :path (str "/" i)}]))))

      ;; Entry with single character strings
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/f")
                   {:path "a"
                    :label "b"
                    :content-type "c"
                    :infoType "d"
                    :md5 "e"}}))

      ;; Entry with very long string fields
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:label (apply str (repeat 1000 "a"))
                    :content-type (apply str (repeat 1000 "b"))
                    :infoType (apply str (repeat 1000 "c"))
                    :md5 (apply str (repeat 1000 "d"))}}))

      ;; Entry with all permission levels tested separately
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/read.txt")
                   {:permission :read}}))

      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/write.txt")
                   {:permission :write}}))

      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/own.txt")
                   {:permission :own}}))

      ;; Entry with all data types tested separately
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/file.txt")
                   {:type :file}}))

      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/iplant/home/janedoe/mydir")
                   {:type :dir}}))

      ;; Multiple entries where each has different single field
      (is (valid? stats/FilteredPathsMap
                  {(keyword ":/path1")
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}
                   (keyword ":/path2")
                   {:path "/path2"}
                   (keyword ":/path3")
                   {:type :file}
                   (keyword ":/path4")
                   {:label "label"}
                   (keyword ":/path5")
                   {:date-created 1000}
                   (keyword ":/path6")
                   {:date-modified 2000}
                   (keyword ":/path7")
                   {:permission :read}
                   (keyword ":/path8")
                   {:share-count 10}
                   (keyword ":/path9")
                   {:file-count 5}
                   (keyword ":/path10")
                   {:dir-count 3}
                   (keyword ":/path11")
                   {:file-size 100}
                   (keyword ":/path12")
                   {:content-type "text/plain"}
                   (keyword ":/path13")
                   {:infoType "text"}
                   (keyword ":/path14")
                   {:md5 "abc123"}})))))

(deftest test-DataIdsMap
  (testing "DataIdsMap validation"
    (testing "valid data ids maps"
      ;; Empty map is valid
      (is (valid? stats/DataIdsMap {}))

      ;; Single entry with FileStatInfo
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/iplant/home/janedoe/file.txt"
                    :type :file
                    :label "file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-size 57
                    :content-type "text/plain"
                    :infoType "perl"
                    :md5 "d5a0bfa9677508d7b379c3e07284a493"}}))

      ;; Single entry with DirStatInfo
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/iplant/home/janedoe/mydir"
                    :type :dir
                    :label "mydir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-count 42
                    :dir-count 27}}))

      ;; Multiple entries with FileStatInfo
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/iplant/home/janedoe/file1.txt"
                    :type :file
                    :label "file1.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/iplant/home/janedoe/file2.txt"
                    :type :file
                    :label "file2.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :write
                    :file-size 200
                    :content-type "application/json"
                    :infoType "json"
                    :md5 "def456"}}))

      ;; Multiple entries with DirStatInfo
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/iplant/home/janedoe/dir1"
                    :type :dir
                    :label "dir1"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-count 10
                    :dir-count 5}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/iplant/home/janedoe/dir2"
                    :type :dir
                    :label "dir2"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :file-count 20
                    :dir-count 15}}))

      ;; Mixed FileStatInfo and DirStatInfo entries
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/iplant/home/janedoe/file.txt"
                    :type :file
                    :label "file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/iplant/home/janedoe/mydir"
                    :type :dir
                    :label "mydir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :write
                    :file-count 42
                    :dir-count 27}}))

      ;; With optional share-count in FileStatInfo
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/iplant/home/janedoe/shared-file.txt"
                    :type :file
                    :label "shared-file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :share-count 10
                    :file-size 57
                    :content-type "text/plain"
                    :infoType "perl"
                    :md5 "d5a0bfa9677508d7b379c3e07284a493"}}))

      ;; With optional share-count in DirStatInfo
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/iplant/home/janedoe/shared-dir"
                    :type :dir
                    :label "shared-dir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :share-count 5
                    :file-count 42
                    :dir-count 27}}))

      ;; Various keyword ID formats
      (is (valid? stats/DataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/example/home/user/test.txt"
                    :type :file
                    :label "test.txt"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/a/b/c"
                    :type :dir
                    :label "c"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-count 0
                    :dir-count 0}}))

      ;; Zero counts and timestamps are valid
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/empty/file"
                    :type :file
                    :label "empty"
                    :date-created 0
                    :date-modified 0
                    :permission :read
                    :file-size 0
                    :content-type "text/plain"
                    :infoType ""
                    :md5 ""}}))

      ;; Different permission levels
      (is (valid? stats/DataIdsMap
                  {:00000000-0000-0000-0000-000000000000
                   {:id #uuid "00000000-0000-0000-0000-000000000000"
                    :path "/read-file.txt"
                    :type :file
                    :label "read-file.txt"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   :11111111-1111-1111-1111-111111111111
                   {:id #uuid "11111111-1111-1111-1111-111111111111"
                    :path "/write-file.txt"
                    :type :file
                    :label "write-file.txt"
                    :date-created 1000
                    :date-modified 2000
                    :permission :write
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "def456"}
                   :22222222-2222-2222-2222-222222222222
                   {:id #uuid "22222222-2222-2222-2222-222222222222"
                    :path "/own-file.txt"
                    :type :file
                    :label "own-file.txt"
                    :date-created 1000
                    :date-modified 2000
                    :permission :own
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "ghi789"}})))

    (testing "invalid data ids maps"
      ;; String keys instead of keywords
      (is (invalid? stats/DataIdsMap
                    {"3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Integer keys instead of keywords
      (is (invalid? stats/DataIdsMap
                    {123
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "/path"
                      :type :file
                      :label "file"
                      :date-created 1000
                      :date-modified 2000
                      :permission :read
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Invalid FileStatInfo value - missing required field
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      ;; Missing file-size
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Invalid DirStatInfo value - missing required field
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "/iplant/home/janedoe/mydir"
                      :type :dir
                      :label "mydir"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-count 42
                      ;; Missing dir-count
                      }}))

      ;; Extra fields in value not allowed due to :closed true
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"
                      :extra-field "not allowed"}}))

      ;; Wrong type for value - string instead of map
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48 "not a map"}))

      ;; Wrong type for value - nil
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48 nil}))

      ;; Value doesn't match FileStatInfo or DirStatInfo - missing required file-specific fields
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read}}))

      ;; Mixed valid and invalid entries
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "/iplant/home/janedoe/valid-file.txt"
                      :type :file
                      :label "valid-file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}
                     :8a950a63-f999-403c-912e-97e11109e68e
                     {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                      :path "/iplant/home/janedoe/invalid-file.txt"
                      :type :file
                      :label "invalid-file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      ;; Missing required fields
                      }}))

      ;; Wrong type in nested FileStatInfo field
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-size "not-a-number"
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Wrong type in nested DirStatInfo field
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "/iplant/home/janedoe/mydir"
                      :type :dir
                      :label "mydir"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-count "not-a-number"
                      :dir-count 27}}))

      ;; Invalid enum value in nested stat info
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :folder  ;; Invalid type
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Empty string for NonBlankString field in nested stat info
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-size 100
                      :content-type ""  ;; Empty string for NonBlankString
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Whitespace-only string for NonBlankString field
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "   "  ;; Whitespace-only string for path
                      :type :file
                      :label "file.txt"
                      :date-created 1763771841123
                      :date-modified 1763772014456
                      :permission :read
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Invalid permission enum value
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "/path"
                      :type :file
                      :label "file"
                      :date-created 1000
                      :date-modified 2000
                      :permission :admin  ;; Invalid permission
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Missing required id field in nested stat info
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:path "/path"
                      :type :file
                      :label "file"
                      :date-created 1000
                      :date-modified 2000
                      :permission :read
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Missing required path field in nested stat info
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :type :file
                      :label "file"
                      :date-created 1000
                      :date-modified 2000
                      :permission :read
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}}))

      ;; Wrong type for share-count
      (is (invalid? stats/DataIdsMap
                    {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                     {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                      :path "/path"
                      :type :file
                      :label "file"
                      :date-created 1000
                      :date-modified 2000
                      :permission :read
                      :share-count "not-a-number"
                      :file-size 100
                      :content-type "text/plain"
                      :infoType "text"
                      :md5 "abc123"}})))

    (testing "edge cases and boundary conditions"
      ;; Very large timestamps, counts, and file sizes
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/path"
                    :type :file
                    :label "file"
                    :date-created 999999999999999
                    :date-modified 999999999999999
                    :permission :read
                    :file-size 999999999999999
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}}))

      ;; Negative timestamps are valid for :int
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/path"
                    :type :file
                    :label "file"
                    :date-created -1
                    :date-modified -1
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}}))

      ;; Special UUID values - all zeros
      (is (valid? stats/DataIdsMap
                  {:00000000-0000-0000-0000-000000000000
                   {:id #uuid "00000000-0000-0000-0000-000000000000"
                    :path "/path"
                    :type :file
                    :label "file"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}}))

      ;; Special UUID values - all fs
      (is (valid? stats/DataIdsMap
                  {:ffffffff-ffff-ffff-ffff-ffffffffffff
                   {:id #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff"
                    :path "/path"
                    :type :file
                    :label "file"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}}))

      ;; Very long paths and strings
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path (apply str (repeat 1000 "a"))
                    :type :file
                    :label (apply str (repeat 1000 "b"))
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-size 100
                    :content-type (apply str (repeat 1000 "c"))
                    :infoType (apply str (repeat 1000 "d"))
                    :md5 (apply str (repeat 1000 "e"))}}))

      ;; Paths with special characters
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/path/with spaces/file.txt"
                    :type :file
                    :label "file with spaces.txt"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/path/with-dashes/file-name.txt"
                    :type :file
                    :label "file-name.txt"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "def456"}
                   :ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/path/with_underscores/file_name.txt"
                    :type :file
                    :label "file_name.txt"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "ghi789"}
                   :12345678-1234-1234-1234-123456789abc
                   {:id #uuid "12345678-1234-1234-1234-123456789abc"
                    :path "/path/with@symbols/file@name.txt"
                    :type :file
                    :label "file@name.txt"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "jkl012"}}))

      ;; Large number of entries
      (is (valid? stats/DataIdsMap
                  (into {}
                        (for [i (range 50)]
                          (let [uuid (java.util.UUID/randomUUID)]
                            [(keyword (str uuid))
                             {:id uuid
                              :path (str "/" i)
                              :type :file
                              :label (str "file-" i)
                              :date-created i
                              :date-modified (* i 2)
                              :permission :read
                              :file-size (* i 100)
                              :content-type "text/plain"
                              :infoType "text"
                              :md5 (str "hash-" i)}])))))

      ;; Single character strings where allowed
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "a"
                    :type :file
                    :label "b"
                    :date-created 1
                    :date-modified 2
                    :permission :read
                    :file-size 1
                    :content-type "c"
                    :infoType "d"
                    :md5 "e"}}))

      ;; All three permission levels in different entries
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/read"
                    :type :file
                    :label "read"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/write"
                    :type :file
                    :label "write"
                    :date-created 1000
                    :date-modified 2000
                    :permission :write
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "def456"}
                   :ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/own"
                    :type :file
                    :label "own"
                    :date-created 1000
                    :date-modified 2000
                    :permission :own
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "ghi789"}}))

      ;; Both data types in different entries
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/file"
                    :type :file
                    :label "file"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/dir"
                    :type :dir
                    :label "dir"
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-count 10
                    :dir-count 5}}))

      ;; Empty strings for non-NonBlankString fields
      (is (valid? stats/DataIdsMap
                  {:3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                    :path "/path"
                    :type :file
                    :label ""
                    :date-created 1000
                    :date-modified 2000
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType ""
                    :md5 ""}})))))

(deftest test-FilteredDataIdsMap
  (testing "FilteredDataIdsMap validation"
    (testing "valid filtered data ids maps"
      ;; Empty map is valid
      (is (valid? stats/FilteredDataIdsMap {}))

      ;; Single entry with all FileStatInfo fields
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/file.txt"
                    :type :file
                    :label "file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-size 57
                    :content-type "text/plain"
                    :infoType "perl"
                    :md5 "d5a0bfa9677508d7b379c3e07284a493"}}))

      ;; Single entry with all DirStatInfo fields
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/mydir"
                    :type :dir
                    :label "mydir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-count 42
                    :dir-count 27}}))

      ;; Single entry with optional share-count in file stat
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/shared-file.txt"
                    :type :file
                    :label "shared-file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :share-count 10
                    :file-size 57
                    :content-type "text/plain"
                    :infoType "perl"
                    :md5 "d5a0bfa9677508d7b379c3e07284a493"}}))

      ;; Single entry with optional share-count in dir stat
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/shared-dir"
                    :type :dir
                    :label "shared-dir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :share-count 5
                    :file-count 42
                    :dir-count 27}}))

      ;; Multiple entries with FileStatInfo
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/file1.txt"
                    :type :file
                    :label "file1.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/iplant/home/janedoe/file2.txt"
                    :type :file
                    :label "file2.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :write
                    :file-size 200
                    :content-type "application/json"
                    :infoType "json"
                    :md5 "def456"}}))

      ;; Multiple entries with DirStatInfo
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/dir1"
                    :type :dir
                    :label "dir1"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-count 10
                    :dir-count 5}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/iplant/home/janedoe/dir2"
                    :type :dir
                    :label "dir2"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :file-count 20
                    :dir-count 15}}))

      ;; Mixed FileStatInfo and DirStatInfo entries
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/file.txt"
                    :type :file
                    :label "file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :read
                    :file-size 100
                    :content-type "text/plain"
                    :infoType "text"
                    :md5 "abc123"}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                    :path "/iplant/home/janedoe/mydir"
                    :type :dir
                    :label "mydir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :write
                    :file-count 42
                    :dir-count 27}}))

      ;; Entry with empty map value (all fields optional in FilteredStatInfo)
      (is (valid? stats/FilteredDataIdsMap
                  {:5bab6167-df4a-4bff-a4cd-96dbe484755b {}}))

      ;; Entry with only id field
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}}))

      ;; Entry with only path field
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:path "/iplant/home/janedoe/file.txt"}}))

      ;; Entry with only type field
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:type :file}}))

      ;; Entry with subset of fields - path and type only
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:path "/iplant/home/janedoe/file.txt"
                    :type :file}}))

      ;; Entry with subset of fields - id, path, and type
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/file.txt"
                    :type :file}}))

      ;; Entry with file-specific fields only
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:file-size 100
                    :content-type "text/plain"}}))

      ;; Entry with dir-specific fields only
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:file-count 42
                    :dir-count 27}}))

      ;; Entry with mix of file and dir fields (semantically odd but valid for filtering)
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/mixed"
                    :file-count 10
                    :file-size 100}}))

      ;; Entry with just permission field
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:permission :read}}))

      ;; Entry with just timestamps
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:date-created 1763771841123
                    :date-modified 1763772014456}}))

      ;; Entry with various field combinations
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:infoType "perl"
                    :md5 "d5a0bfa9677508d7b379c3e07284a493"}}))

      ;; Multiple entries with different field subsets
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/file1.txt"}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:type :file
                    :file-size 100}
                   :12345678-1234-1234-1234-123456789abc
                   {:file-count 10
                    :dir-count 5}
                   :3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {}}))

      ;; Entry with zero values
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:file-size 0
                    :share-count 0
                    :date-created 0
                    :date-modified 0}}))

      ;; Entry with all permission levels
      (is (valid? stats/FilteredDataIdsMap
                  {:00000000-0000-0000-0000-000000000000
                   {:permission :read}
                   :11111111-1111-1111-1111-111111111111
                   {:permission :write}
                   :22222222-2222-2222-2222-222222222222
                   {:permission :own}}))

      ;; Entry with both data types
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:type :file}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:type :dir}}))

      ;; Various keyword ID formats
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:path "/a/b/c"}
                   :3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:type :file}}))

      ;; Entry with empty string fields (valid for non-NonBlankString fields)
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:infoType ""
                    :md5 ""
                    :label ""}}))

      ;; Entry with complete file stat info (all file fields present)
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/complete-file.txt"
                    :type :file
                    :label "complete-file.txt"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :share-count 10
                    :file-size 57
                    :content-type "text/plain"
                    :infoType "perl"
                    :md5 "d5a0bfa9677508d7b379c3e07284a493"}}))

      ;; Entry with complete dir stat info (all dir fields present)
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                    :path "/iplant/home/janedoe/complete-dir"
                    :type :dir
                    :label "complete-dir"
                    :date-created 1763771841123
                    :date-modified 1763772014456
                    :permission :own
                    :share-count 5
                    :file-count 42
                    :dir-count 27}})))

    (testing "invalid filtered data ids maps"
      ;; String keys instead of keywords
      (is (invalid? stats/FilteredDataIdsMap
                    {"ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}}))

      ;; Integer keys instead of keywords
      (is (invalid? stats/FilteredDataIdsMap
                    {123
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}}))

      ;; Symbol keys instead of keywords
      (is (invalid? stats/FilteredDataIdsMap
                    {'some-id
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}}))

      ;; Wrong type for id field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:id "not-a-uuid"}}))

      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:id 123}}))

      ;; Wrong type for path field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:path 123}}))

      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:path nil}}))

      ;; Empty string for path (NonBlankString field)
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:path ""}}))

      ;; Whitespace-only string for path (NonBlankString field)
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:path "   "}}))

      ;; Wrong type for type field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:type :folder}}))

      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:type "file"}}))

      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:type :directory}}))

      ;; Wrong type for label field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:label 123}}))

      ;; Wrong type for date-created field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:date-created "1763771841123"}}))

      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:date-created 1763771841123.5}}))

      ;; Wrong type for date-modified field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:date-modified "1763772014456"}}))

      ;; Wrong type for permission field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:permission :admin}}))

      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:permission "read"}}))

      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:permission nil}}))

      ;; Wrong type for share-count field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:share-count "27"}}))

      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:share-count 27.5}}))

      ;; Wrong type for file-count field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:file-count "42"}}))

      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:file-count nil}}))

      ;; Wrong type for dir-count field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:dir-count "27"}}))

      ;; Wrong type for file-size field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:file-size "100"}}))

      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:file-size nil}}))

      ;; Wrong type for content-type field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:content-type 123}}))

      ;; Empty string for content-type (NonBlankString field)
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:content-type ""}}))

      ;; Whitespace-only string for content-type (NonBlankString field)
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:content-type "   "}}))

      ;; Wrong type for infoType field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:infoType 123}}))

      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:infoType nil}}))

      ;; Wrong type for md5 field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:md5 123}}))

      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:md5 nil}}))

      ;; Extra fields not allowed due to :closed true from DirStatInfo and FileStatInfo
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/file.txt"
                      :extra-field "not allowed"}}))

      ;; Wrong type for value - string instead of map
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c "not a map"}))

      ;; Wrong type for value - nil
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c nil}))

      ;; Wrong type for value - vector
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c []}))

      ;; Mixed valid and invalid entries - one entry has wrong type
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/valid-file.txt"
                      :type :file}
                     :8a950a63-f999-403c-912e-97e11109e68e
                     {:id "not-a-uuid"
                      :path "/iplant/home/janedoe/invalid-file.txt"
                      :type :file}}))

      ;; Mixed valid and invalid entries - one entry has invalid enum value
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :type :file}
                     :8a950a63-f999-403c-912e-97e11109e68e
                     {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                      :type :folder}}))

      ;; Mixed valid and invalid entries - one entry has extra field
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}
                     :8a950a63-f999-403c-912e-97e11109e68e
                     {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                      :extra-field "not allowed"}}))

      ;; Wrong field type in nested value
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :file-size "not-a-number"}}))

      ;; Invalid enum value in nested value
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path "/iplant/home/janedoe/file.txt"
                      :type :file
                      :permission :admin}}))

      ;; Empty string for NonBlankString field in nested value
      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                      :path ""}}))

      (is (invalid? stats/FilteredDataIdsMap
                    {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                     {:content-type ""}})))

    (testing "edge cases"
      ;; Very large integers
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:date-created 999999999999999
                    :date-modified 999999999999999
                    :file-size 999999999999999
                    :file-count 999999999
                    :dir-count 999999999
                    :share-count 999999999}}))

      ;; Negative integers (valid for :int type)
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:date-created -1
                    :date-modified -1}}))

      ;; Special UUID values
      (is (valid? stats/FilteredDataIdsMap
                  {:00000000-0000-0000-0000-000000000000
                   {:id #uuid "00000000-0000-0000-0000-000000000000"}
                   :ffffffff-ffff-ffff-ffff-ffffffffffff
                   {:id #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff"}}))

      ;; Very long paths
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:path (apply str (repeat 1000 "a"))}}))

      ;; Paths with special characters
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:path "/path/with spaces/file.txt"}
                   :8a950a63-f999-403c-912e-97e11109e68e
                   {:path "/path/with-dashes/file.txt"}
                   :3dded710-1bd0-4541-9b1f-6b8c87e31f48
                   {:path "/path/with_underscores/file.txt"}
                   :12345678-1234-1234-1234-123456789abc
                   {:path "/path/with.dots/file.txt"}
                   :87654321-4321-4321-4321-cba987654321
                   {:path "/path/with@symbols/file.txt"}}))

      ;; Large number of entries
      (is (valid? stats/FilteredDataIdsMap
                  (into {}
                        (for [i (range 100)]
                          [(keyword (str (java.util.UUID/randomUUID)))
                           {:id (java.util.UUID/randomUUID)
                            :path (str "/" i)}]))))

      ;; Entry with single character strings
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:path "a"
                    :label "b"
                    :content-type "c"
                    :infoType "d"
                    :md5 "e"}}))

      ;; Entry with very long string fields
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:label (apply str (repeat 1000 "a"))
                    :content-type (apply str (repeat 1000 "b"))
                    :infoType (apply str (repeat 1000 "c"))
                    :md5 (apply str (repeat 1000 "d"))}}))

      ;; Entry with all permission levels tested separately
      (is (valid? stats/FilteredDataIdsMap
                  {:00000000-0000-0000-0000-000000000000
                   {:permission :read}}))

      (is (valid? stats/FilteredDataIdsMap
                  {:11111111-1111-1111-1111-111111111111
                   {:permission :write}}))

      (is (valid? stats/FilteredDataIdsMap
                  {:22222222-2222-2222-2222-222222222222
                   {:permission :own}}))

      ;; Entry with all data types tested separately
      (is (valid? stats/FilteredDataIdsMap
                  {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                   {:type :file}}))

      (is (valid? stats/FilteredDataIdsMap
                  {:8a950a63-f999-403c-912e-97e11109e68e
                   {:type :dir}}))

      ;; Multiple entries where each has different single field
      (is (valid? stats/FilteredDataIdsMap
                  {:00000000-0000-0000-0000-000000000000
                   {:id #uuid "00000000-0000-0000-0000-000000000000"}
                   :11111111-1111-1111-1111-111111111111
                   {:path "/path2"}
                   :22222222-2222-2222-2222-222222222222
                   {:type :file}
                   :33333333-3333-3333-3333-333333333333
                   {:label "label"}
                   :44444444-4444-4444-4444-444444444444
                   {:date-created 1000}
                   :55555555-5555-5555-5555-555555555555
                   {:date-modified 2000}
                   :66666666-6666-6666-6666-666666666666
                   {:permission :read}
                   :77777777-7777-7777-7777-777777777777
                   {:share-count 10}
                   :88888888-8888-8888-8888-888888888888
                   {:file-count 5}
                   :99999999-9999-9999-9999-999999999999
                   {:dir-count 3}
                   :aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa
                   {:file-size 100}
                   :bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb
                   {:content-type "text/plain"}
                   :cccccccc-cccc-cccc-cccc-cccccccccccc
                   {:infoType "text"}
                   :dddddddd-dddd-dddd-dddd-dddddddddddd
                   {:md5 "abc123"}})))))

(deftest test-StatusInfo
  (testing "StatusInfo validation"
    (testing "valid status info"
      ;; Both :paths and :ids with complete file stat info
      (is (valid? stats/StatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/file.txt"
                            :type :file
                            :label "file.txt"
                            :date-created 1763771841123
                            :date-modified 1763772014456
                            :permission :read
                            :file-size 57
                            :content-type "text/plain"
                            :infoType "perl"
                            :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/iplant/home/janedoe/file.txt"
                          :type :file
                          :label "file.txt"
                          :date-created 1763771841123
                          :date-modified 1763772014456
                          :permission :read
                          :file-size 57
                          :content-type "text/plain"
                          :infoType "perl"
                          :md5 "d5a0bfa9677508d7b379c3e07284a493"}}}))

      ;; Both :paths and :ids with complete dir stat info
      (is (valid? stats/StatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/mydir")
                           {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                            :path "/iplant/home/janedoe/mydir"
                            :type :dir
                            :label "mydir"
                            :date-created 1763771841123
                            :date-modified 1763772014456
                            :permission :own
                            :file-count 42
                            :dir-count 27}}
                   :ids {:8a950a63-f999-403c-912e-97e11109e68e
                         {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                          :path "/iplant/home/janedoe/mydir"
                          :type :dir
                          :label "mydir"
                          :date-created 1763771841123
                          :date-modified 1763772014456
                          :permission :own
                          :file-count 42
                          :dir-count 27}}}))

      ;; Both :paths and :ids with empty maps
      (is (valid? stats/StatusInfo
                  {:paths {}
                   :ids {}}))

      ;; Both :paths and :ids with mixed file and dir entries
      (is (valid? stats/StatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/file.txt"
                            :type :file
                            :label "file.txt"
                            :date-created 1763771841123
                            :date-modified 1763772014456
                            :permission :read
                            :file-size 100
                            :content-type "text/plain"
                            :infoType "text"
                            :md5 "abc123"}
                           (keyword ":/iplant/home/janedoe/mydir")
                           {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                            :path "/iplant/home/janedoe/mydir"
                            :type :dir
                            :label "mydir"
                            :date-created 1763771841123
                            :date-modified 1763772014456
                            :permission :write
                            :file-count 42
                            :dir-count 27}}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/iplant/home/janedoe/file.txt"
                          :type :file
                          :label "file.txt"
                          :date-created 1763771841123
                          :date-modified 1763772014456
                          :permission :read
                          :file-size 100
                          :content-type "text/plain"
                          :infoType "text"
                          :md5 "abc123"}
                         :8a950a63-f999-403c-912e-97e11109e68e
                         {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                          :path "/iplant/home/janedoe/mydir"
                          :type :dir
                          :label "mydir"
                          :date-created 1763771841123
                          :date-modified 1763772014456
                          :permission :write
                          :file-count 42
                          :dir-count 27}}}))

      ;; :paths populated, :ids empty
      (is (valid? stats/StatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/file.txt"
                            :type :file
                            :label "file.txt"
                            :date-created 1763771841123
                            :date-modified 1763772014456
                            :permission :read
                            :file-size 57
                            :content-type "text/plain"
                            :infoType "perl"
                            :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                   :ids {}}))

      ;; :paths empty, :ids populated
      (is (valid? stats/StatusInfo
                  {:paths {}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/iplant/home/janedoe/file.txt"
                          :type :file
                          :label "file.txt"
                          :date-created 1763771841123
                          :date-modified 1763772014456
                          :permission :read
                          :file-size 57
                          :content-type "text/plain"
                          :infoType "perl"
                          :md5 "d5a0bfa9677508d7b379c3e07284a493"}}}))

      ;; Multiple entries in both :paths and :ids
      (is (valid? stats/StatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file1.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/file1.txt"
                            :type :file
                            :label "file1.txt"
                            :date-created 1763771841123
                            :date-modified 1763772014456
                            :permission :read
                            :file-size 100
                            :content-type "text/plain"
                            :infoType "text"
                            :md5 "abc123"}
                           (keyword ":/iplant/home/janedoe/file2.txt")
                           {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                            :path "/iplant/home/janedoe/file2.txt"
                            :type :file
                            :label "file2.txt"
                            :date-created 1763771841123
                            :date-modified 1763772014456
                            :permission :write
                            :file-size 200
                            :content-type "application/json"
                            :infoType "json"
                            :md5 "def456"}}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/iplant/home/janedoe/file1.txt"
                          :type :file
                          :label "file1.txt"
                          :date-created 1763771841123
                          :date-modified 1763772014456
                          :permission :read
                          :file-size 100
                          :content-type "text/plain"
                          :infoType "text"
                          :md5 "abc123"}
                         :8a950a63-f999-403c-912e-97e11109e68e
                         {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                          :path "/iplant/home/janedoe/file2.txt"
                          :type :file
                          :label "file2.txt"
                          :date-created 1763771841123
                          :date-modified 1763772014456
                          :permission :write
                          :file-size 200
                          :content-type "application/json"
                          :infoType "json"
                          :md5 "def456"}}}))

      ;; With optional share-count
      (is (valid? stats/StatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/shared-file.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/shared-file.txt"
                            :type :file
                            :label "shared-file.txt"
                            :date-created 1763771841123
                            :date-modified 1763772014456
                            :permission :own
                            :share-count 10
                            :file-size 57
                            :content-type "text/plain"
                            :infoType "perl"
                            :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/iplant/home/janedoe/shared-file.txt"
                          :type :file
                          :label "shared-file.txt"
                          :date-created 1763771841123
                          :date-modified 1763772014456
                          :permission :own
                          :share-count 10
                          :file-size 57
                          :content-type "text/plain"
                          :infoType "perl"
                          :md5 "d5a0bfa9677508d7b379c3e07284a493"}}}))

      ;; Zero values are valid
      (is (valid? stats/StatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/file.txt"
                            :type :file
                            :label "file.txt"
                            :date-created 0
                            :date-modified 0
                            :permission :read
                            :file-size 0
                            :content-type "text/plain"
                            :infoType "text"
                            :md5 "abc123"}}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/iplant/home/janedoe/file.txt"
                          :type :file
                          :label "file.txt"
                          :date-created 0
                          :date-modified 0
                          :permission :read
                          :file-size 0
                          :content-type "text/plain"
                          :infoType "text"
                          :md5 "abc123"}}}))

      ;; Only :paths field (no :ids)
      (is (valid? stats/StatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/file.txt"
                            :type :file
                            :label "file.txt"
                            :date-created 1763771841123
                            :date-modified 1763772014456
                            :permission :read
                            :file-size 57
                            :content-type "text/plain"
                            :infoType "perl"
                            :md5 "d5a0bfa9677508d7b379c3e07284a493"}}}))

      ;; Only :ids field (no :paths)
      (is (valid? stats/StatusInfo
                  {:ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/iplant/home/janedoe/file.txt"
                          :type :file
                          :label "file.txt"
                          :date-created 1763771841123
                          :date-modified 1763772014456
                          :permission :read
                          :file-size 57
                          :content-type "text/plain"
                          :infoType "perl"
                          :md5 "d5a0bfa9677508d7b379c3e07284a493"}}}))

      ;; Empty map (both fields optional)
      (is (valid? stats/StatusInfo
                  {}))

      ;; Different permission levels
      (is (valid? stats/StatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file1.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/file1.txt"
                            :type :file
                            :label "file1.txt"
                            :date-created 1000
                            :date-modified 2000
                            :permission :read
                            :file-size 100
                            :content-type "text/plain"
                            :infoType "text"
                            :md5 "abc123"}
                           (keyword ":/iplant/home/janedoe/file2.txt")
                           {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                            :path "/iplant/home/janedoe/file2.txt"
                            :type :file
                            :label "file2.txt"
                            :date-created 1000
                            :date-modified 2000
                            :permission :write
                            :file-size 200
                            :content-type "text/plain"
                            :infoType "text"
                            :md5 "def456"}
                           (keyword ":/iplant/home/janedoe/file3.txt")
                           {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                            :path "/iplant/home/janedoe/file3.txt"
                            :type :file
                            :label "file3.txt"
                            :date-created 1000
                            :date-modified 2000
                            :permission :own
                            :file-size 300
                            :content-type "text/plain"
                            :infoType "text"
                            :md5 "ghi789"}}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/iplant/home/janedoe/file1.txt"
                          :type :file
                          :label "file1.txt"
                          :date-created 1000
                          :date-modified 2000
                          :permission :read
                          :file-size 100
                          :content-type "text/plain"
                          :infoType "text"
                          :md5 "abc123"}
                         :8a950a63-f999-403c-912e-97e11109e68e
                         {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                          :path "/iplant/home/janedoe/file2.txt"
                          :type :file
                          :label "file2.txt"
                          :date-created 1000
                          :date-modified 2000
                          :permission :write
                          :file-size 200
                          :content-type "text/plain"
                          :infoType "text"
                          :md5 "def456"}
                         :3dded710-1bd0-4541-9b1f-6b8c87e31f48
                         {:id #uuid "3dded710-1bd0-4541-9b1f-6b8c87e31f48"
                          :path "/iplant/home/janedoe/file3.txt"
                          :type :file
                          :label "file3.txt"
                          :date-created 1000
                          :date-modified 2000
                          :permission :own
                          :file-size 300
                          :content-type "text/plain"
                          :infoType "text"
                          :md5 "ghi789"}}})))

    (testing "invalid status info"
      ;; Extra fields not allowed
      (is (invalid? stats/StatusInfo
                    {:paths {}
                     :ids {}
                     :extra-field "not allowed"}))

      ;; Invalid :paths value - not a map
      (is (invalid? stats/StatusInfo
                    {:paths "not a map"
                     :ids {}}))

      ;; Invalid :ids value - not a map
      (is (invalid? stats/StatusInfo
                    {:paths {}
                     :ids "not a map"}))

      ;; Invalid :paths value - nil
      (is (invalid? stats/StatusInfo
                    {:paths nil
                     :ids {}}))

      ;; Invalid :ids value - nil
      (is (invalid? stats/StatusInfo
                    {:paths {}
                     :ids nil}))

      ;; Invalid PathsMap - string keys instead of keywords
      (is (invalid? stats/StatusInfo
                    {:paths {"/iplant/home/janedoe/file.txt"
                             {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                              :path "/iplant/home/janedoe/file.txt"
                              :type :file
                              :label "file.txt"
                              :date-created 1763771841123
                              :date-modified 1763772014456
                              :permission :read
                              :file-size 57
                              :content-type "text/plain"
                              :infoType "perl"
                              :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                     :ids {}}))

      ;; Invalid DataIdsMap - string keys instead of keywords
      (is (invalid? stats/StatusInfo
                    {:paths {}
                     :ids {"ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/file.txt"
                            :type :file
                            :label "file.txt"
                            :date-created 1763771841123
                            :date-modified 1763772014456
                            :permission :read
                            :file-size 57
                            :content-type "text/plain"
                            :infoType "perl"
                            :md5 "d5a0bfa9677508d7b379c3e07284a493"}}}))

      ;; Invalid FileStatInfo - missing required field
      (is (invalid? stats/StatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:path "/iplant/home/janedoe/file.txt"
                              :type :file
                              :label "file.txt"
                              :date-created 1763771841123
                              :date-modified 1763772014456
                              :permission :read
                              :file-size 57
                              :content-type "text/plain"
                              :infoType "perl"
                              :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                     :ids {}}))

      ;; Invalid DirStatInfo - missing required field
      (is (invalid? stats/StatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/mydir")
                             {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                              :type :dir
                              :label "mydir"
                              :date-created 1763771841123
                              :date-modified 1763772014456
                              :permission :own
                              :file-count 42
                              :dir-count 27}}
                     :ids {}}))

      ;; Invalid type enum
      (is (invalid? stats/StatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                              :path "/iplant/home/janedoe/file.txt"
                              :type :folder
                              :label "file.txt"
                              :date-created 1763771841123
                              :date-modified 1763772014456
                              :permission :read
                              :file-size 57
                              :content-type "text/plain"
                              :infoType "perl"
                              :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                     :ids {}}))

      ;; Invalid permission enum
      (is (invalid? stats/StatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                              :path "/iplant/home/janedoe/file.txt"
                              :type :file
                              :label "file.txt"
                              :date-created 1763771841123
                              :date-modified 1763772014456
                              :permission :admin
                              :file-size 57
                              :content-type "text/plain"
                              :infoType "perl"
                              :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                     :ids {}}))

      ;; Invalid date type (string instead of int)
      (is (invalid? stats/StatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                              :path "/iplant/home/janedoe/file.txt"
                              :type :file
                              :label "file.txt"
                              :date-created "1763771841123"
                              :date-modified 1763772014456
                              :permission :read
                              :file-size 57
                              :content-type "text/plain"
                              :infoType "perl"
                              :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                     :ids {}}))

      ;; Invalid file-size type (string instead of int)
      (is (invalid? stats/StatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                              :path "/iplant/home/janedoe/file.txt"
                              :type :file
                              :label "file.txt"
                              :date-created 1763771841123
                              :date-modified 1763772014456
                              :permission :read
                              :file-size "57"
                              :content-type "text/plain"
                              :infoType "perl"
                              :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                     :ids {}}))

      ;; Invalid path (empty string - must be NonBlankString)
      (is (invalid? stats/StatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                              :path ""
                              :type :file
                              :label "file.txt"
                              :date-created 1763771841123
                              :date-modified 1763772014456
                              :permission :read
                              :file-size 57
                              :content-type "text/plain"
                              :infoType "perl"
                              :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                     :ids {}}))

      ;; Invalid path (whitespace-only - must be NonBlankString)
      (is (invalid? stats/StatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                              :path "   "
                              :type :file
                              :label "file.txt"
                              :date-created 1763771841123
                              :date-modified 1763772014456
                              :permission :read
                              :file-size 57
                              :content-type "text/plain"
                              :infoType "perl"
                              :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                     :ids {}}))

      ;; Invalid content-type (empty string - must be NonBlankString)
      (is (invalid? stats/StatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                              :path "/iplant/home/janedoe/file.txt"
                              :type :file
                              :label "file.txt"
                              :date-created 1763771841123
                              :date-modified 1763772014456
                              :permission :read
                              :file-size 57
                              :content-type ""
                              :infoType "perl"
                              :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                     :ids {}}))

      ;; Missing required file-specific fields
      (is (invalid? stats/StatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                              :path "/iplant/home/janedoe/file.txt"
                              :type :file
                              :label "file.txt"
                              :date-created 1763771841123
                              :date-modified 1763772014456
                              :permission :read}}
                     :ids {}}))

      ;; Missing required dir-specific fields
      (is (invalid? stats/StatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/mydir")
                             {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                              :path "/iplant/home/janedoe/mydir"
                              :type :dir
                              :label "mydir"
                              :date-created 1763771841123
                              :date-modified 1763772014456
                              :permission :own}}
                     :ids {}}))

      ;; Invalid UUID format in id field
      (is (invalid? stats/StatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:id "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                              :path "/iplant/home/janedoe/file.txt"
                              :type :file
                              :label "file.txt"
                              :date-created 1763771841123
                              :date-modified 1763772014456
                              :permission :read
                              :file-size 57
                              :content-type "text/plain"
                              :infoType "perl"
                              :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                     :ids {}})))))

(deftest test-FilteredStatusInfo
  (testing "FilteredStatusInfo validation"
    (testing "valid filtered status info"
      ;; Both :paths and :ids with complete file stat info
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/file.txt"
                            :type :file
                            :label "file.txt"
                            :date-created 1763771841123
                            :date-modified 1763772014456
                            :permission :read
                            :file-size 57
                            :content-type "text/plain"
                            :infoType "perl"
                            :md5 "d5a0bfa9677508d7b379c3e07284a493"}}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/iplant/home/janedoe/file.txt"
                          :type :file
                          :label "file.txt"
                          :date-created 1763771841123
                          :date-modified 1763772014456
                          :permission :read
                          :file-size 57
                          :content-type "text/plain"
                          :infoType "perl"
                          :md5 "d5a0bfa9677508d7b379c3e07284a493"}}}))

      ;; Both :paths and :ids with complete dir stat info
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/mydir")
                           {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                            :path "/iplant/home/janedoe/mydir"
                            :type :dir
                            :label "mydir"
                            :date-created 1763771841123
                            :date-modified 1763772014456
                            :permission :own
                            :file-count 42
                            :dir-count 27}}
                   :ids {:8a950a63-f999-403c-912e-97e11109e68e
                         {:id #uuid "8a950a63-f999-403c-912e-97e11109e68e"
                          :path "/iplant/home/janedoe/mydir"
                          :type :dir
                          :label "mydir"
                          :date-created 1763771841123
                          :date-modified 1763772014456
                          :permission :own
                          :file-count 42
                          :dir-count 27}}}))

      ;; Both :paths and :ids with empty maps
      (is (valid? stats/FilteredStatusInfo
                  {:paths {}
                   :ids {}}))

      ;; Both :paths and :ids with empty FilteredStatInfo maps
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt") {}}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c {}}}))

      ;; :paths with only id field
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}}
                   :ids {}}))

      ;; :paths with only path field
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:path "/iplant/home/janedoe/file.txt"}}
                   :ids {}}))

      ;; :paths with only type field
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:type :file}}
                   :ids {}}))

      ;; :ids with only id field
      (is (valid? stats/FilteredStatusInfo
                  {:paths {}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}}}))

      ;; :ids with only path field
      (is (valid? stats/FilteredStatusInfo
                  {:paths {}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:path "/iplant/home/janedoe/file.txt"}}}))

      ;; :ids with only type field
      (is (valid? stats/FilteredStatusInfo
                  {:paths {}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:type :file}}}))

      ;; :paths with subset of fields - path and type only
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:path "/iplant/home/janedoe/file.txt"
                            :type :file}}
                   :ids {}}))

      ;; :ids with subset of fields - id, path, and type
      (is (valid? stats/FilteredStatusInfo
                  {:paths {}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/iplant/home/janedoe/file.txt"
                          :type :file}}}))

      ;; :paths with file-specific fields only
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:file-size 100
                            :content-type "text/plain"}}
                   :ids {}}))

      ;; :ids with dir-specific fields only
      (is (valid? stats/FilteredStatusInfo
                  {:paths {}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:file-count 42
                          :dir-count 27}}}))

      ;; :paths with mix of file and dir fields (semantically odd but valid for filtering)
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/mixed")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/mixed"
                            :file-count 10
                            :file-size 100}}
                   :ids {}}))

      ;; :ids with just permission field
      (is (valid? stats/FilteredStatusInfo
                  {:paths {}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:permission :read}}}))

      ;; :paths with just timestamps
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:date-created 1763771841123
                            :date-modified 1763772014456}}
                   :ids {}}))

      ;; :ids with various field combinations
      (is (valid? stats/FilteredStatusInfo
                  {:paths {}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:infoType "perl"
                          :md5 "d5a0bfa9677508d7b379c3e07284a493"}}}))

      ;; Both :paths and :ids with different field subsets
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file1.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/file1.txt"}
                           (keyword ":/iplant/home/janedoe/file2.txt")
                           {:type :file
                            :file-size 100}}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:file-count 10
                          :dir-count 5}
                         :8a950a63-f999-403c-912e-97e11109e68e
                         {}}}))

      ;; :paths with zero values
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:file-size 0
                            :share-count 0
                            :date-created 0
                            :date-modified 0}}
                   :ids {}}))

      ;; :ids with all permission levels
      (is (valid? stats/FilteredStatusInfo
                  {:paths {}
                   :ids {:00000000-0000-0000-0000-000000000000
                         {:permission :read}
                         :11111111-1111-1111-1111-111111111111
                         {:permission :write}
                         :22222222-2222-2222-2222-222222222222
                         {:permission :own}}}))

      ;; Both :paths and :ids with both data types
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:type :file}
                           (keyword ":/iplant/home/janedoe/mydir")
                           {:type :dir}}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:type :file}
                         :8a950a63-f999-403c-912e-97e11109e68e
                         {:type :dir}}}))

      ;; :paths with empty string fields (valid for non-NonBlankString fields)
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:infoType ""
                            :md5 ""
                            :label ""}}
                   :ids {}}))

      ;; :paths populated, :ids empty
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/file.txt"
                            :type :file}}
                   :ids {}}))

      ;; :paths empty, :ids populated
      (is (valid? stats/FilteredStatusInfo
                  {:paths {}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/iplant/home/janedoe/file.txt"
                          :type :file}}}))

      ;; Multiple entries in both :paths and :ids with various field combinations
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file1.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}
                           (keyword ":/iplant/home/janedoe/file2.txt")
                           {:path "/iplant/home/janedoe/file2.txt"}
                           (keyword ":/iplant/home/janedoe/file3.txt")
                           {:type :file}
                           (keyword ":/iplant/home/janedoe/mydir")
                           {:file-count 10
                            :dir-count 5}}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:label "label"}
                         :8a950a63-f999-403c-912e-97e11109e68e
                         {:date-created 1000}
                         :3dded710-1bd0-4541-9b1f-6b8c87e31f48
                         {:permission :read}
                         :12345678-1234-1234-1234-123456789abc
                         {}}}))

      ;; With optional share-count
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:share-count 10}}
                   :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:share-count 5}}}))

      ;; Only :paths field (no :ids)
      (is (valid? stats/FilteredStatusInfo
                  {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                            :path "/iplant/home/janedoe/file.txt"
                            :type :file}}}))

      ;; Only :ids field (no :paths)
      (is (valid? stats/FilteredStatusInfo
                  {:ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                         {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                          :path "/iplant/home/janedoe/file.txt"
                          :type :file}}}))

      ;; Empty map (both fields optional)
      (is (valid? stats/FilteredStatusInfo
                  {})))

    (testing "invalid filtered status info"
      ;; Extra fields not allowed
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {}
                     :extra-field "not allowed"}))

      ;; Invalid :paths value - not a map
      (is (invalid? stats/FilteredStatusInfo
                    {:paths "not a map"
                     :ids {}}))

      ;; Invalid :ids value - not a map
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids "not a map"}))

      ;; Invalid :paths value - nil
      (is (invalid? stats/FilteredStatusInfo
                    {:paths nil
                     :ids {}}))

      ;; Invalid :ids value - nil
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids nil}))

      ;; Invalid FilteredPathsMap - string keys instead of keywords
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {"/iplant/home/janedoe/file.txt"
                             {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}}
                     :ids {}}))

      ;; Invalid FilteredDataIdsMap - string keys instead of keywords
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {"ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
                           {:id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"}}}))

      ;; Wrong type for id field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:id "not-a-uuid"}}
                     :ids {}}))

      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                           {:id 123}}}))

      ;; Wrong type for path field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:path 123}}
                     :ids {}}))

      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                           {:path nil}}}))

      ;; Empty string for path (NonBlankString field)
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:path ""}}
                     :ids {}}))

      ;; Whitespace-only string for path (NonBlankString field)
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                           {:path "   "}}}))

      ;; Wrong type for type field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:type :folder}}
                     :ids {}}))

      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                           {:type "file"}}}))

      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:type :directory}}
                     :ids {}}))

      ;; Wrong type for label field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:label 123}}
                     :ids {}}))

      ;; Wrong type for date-created field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                           {:date-created "1763771841123"}}}))

      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:date-created 1763771841123.5}}
                     :ids {}}))

      ;; Wrong type for date-modified field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                           {:date-modified "1763772014456"}}}))

      ;; Wrong type for permission field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:permission :admin}}
                     :ids {}}))

      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                           {:permission "read"}}}))

      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:permission nil}}
                     :ids {}}))

      ;; Wrong type for share-count field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                           {:share-count "27"}}}))

      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:share-count 27.5}}
                     :ids {}}))

      ;; Wrong type for file-count field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                           {:file-count "42"}}}))

      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:file-count nil}}
                     :ids {}}))

      ;; Wrong type for dir-count field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                           {:dir-count "27"}}}))

      ;; Wrong type for file-size field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:file-size "100"}}
                     :ids {}}))

      ;; Wrong type for content-type field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                           {:content-type 123}}}))

      ;; Empty string for content-type (NonBlankString field)
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:content-type ""}}
                     :ids {}}))

      ;; Whitespace-only string for content-type (NonBlankString field)
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                           {:content-type "   "}}}))

      ;; Wrong type for infoType field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {(keyword ":/iplant/home/janedoe/file.txt")
                             {:infoType 123}}
                     :ids {}}))

      ;; Wrong type for md5 field
      (is (invalid? stats/FilteredStatusInfo
                    {:paths {}
                     :ids {:ca23780a-6acb-47aa-9f9a-eab1ef9a541c
                           {:md5 123}}})))))

(deftest test-StatErrorResponses
  (testing "StatErrorResponses validation"
    (testing "valid error responses"
      ;; Base error codes from ErrorResponseUnchecked
      (is (valid? stats/StatErrorResponses
                  {:error_code "ERR_UNCHECKED_EXCEPTION"}))
      (is (valid? stats/StatErrorResponses
                  {:error_code "ERR_SCHEMA_VALIDATION"}))

      ;; Additional stat-specific error codes
      (is (valid? stats/StatErrorResponses
                  {:error_code "ERR_DOES_NOT_EXIST"}))
      (is (valid? stats/StatErrorResponses
                  {:error_code "ERR_NOT_READABLE"}))
      (is (valid? stats/StatErrorResponses
                  {:error_code "ERR_NOT_WRITEABLE"}))
      (is (valid? stats/StatErrorResponses
                  {:error_code "ERR_NOT_OWNER"}))
      (is (valid? stats/StatErrorResponses
                  {:error_code "ERR_NOT_A_USER"}))
      (is (valid? stats/StatErrorResponses
                  {:error_code "ERR_TOO_MANY_RESULTS"}))

      ;; With optional reason field
      (is (valid? stats/StatErrorResponses
                  {:error_code "ERR_DOES_NOT_EXIST"
                   :reason "The requested path does not exist"}))

      ;; Reason can be any type (from ErrorResponseUnchecked)
      (is (valid? stats/StatErrorResponses
                  {:error_code "ERR_UNCHECKED_EXCEPTION"
                   :reason {:details "Complex error object"}})))

    (testing "invalid error responses"
      ;; Error codes not in the allowed set
      (is (invalid? stats/StatErrorResponses
                    {:error_code "ERR_NOT_FOUND"}))
      (is (invalid? stats/StatErrorResponses
                    {:error_code "ERR_EXISTS"}))
      (is (invalid? stats/StatErrorResponses
                    {:error_code "INVALID_CODE"}))

      ;; Missing required error_code
      (is (invalid? stats/StatErrorResponses {}))
      (is (invalid? stats/StatErrorResponses
                    {:reason "Some error occurred"}))

      ;; Extra fields not allowed (closed map)
      (is (invalid? stats/StatErrorResponses
                    {:error_code "ERR_DOES_NOT_EXIST"
                     :extra_field "not allowed"})))))
