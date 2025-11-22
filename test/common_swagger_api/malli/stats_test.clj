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
