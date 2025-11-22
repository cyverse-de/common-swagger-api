(ns common-swagger-api.malli.data-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common-swagger-api.malli.data :as data]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-Paths
  (testing "Paths validation"
    (testing "valid paths"
      (is (valid? data/Paths
                  {:paths ["/example/home/username/foo.txt"]}))
      (is (valid? data/Paths
                  {:paths ["/example/home/username/foo.txt" "/example/home/username/bar.txt"]}))
      (is (valid? data/Paths
                  {:paths ["/iplant/home/user/file.txt"
                           "/iplant/home/user/dir1/file2.csv"
                           "/iplant/home/user/dir2/file3.json"]}))
      (is (valid? data/Paths
                  {:paths ["a"]})))

    (testing "invalid paths - empty vector"
      ;; The :min 1 constraint requires at least one element
      (is (not (valid? data/Paths
                       {:paths []}))))

    (testing "invalid paths - missing paths key"
      ;; The :paths key is required in a :map
      (is (not (valid? data/Paths {}))))

    (testing "invalid paths - blank strings"
      ;; NonBlankString validator should reject empty and whitespace-only strings
      (is (not (valid? data/Paths
                       {:paths [""]})))
      (is (not (valid? data/Paths
                       {:paths ["   "]})))
      (is (not (valid? data/Paths
                       {:paths ["/valid/path" ""]})))
      (is (not (valid? data/Paths
                       {:paths ["/valid/path" "   "]}))))

    (testing "invalid paths - nil values"
      ;; nil is not a valid NonBlankString
      (is (not (valid? data/Paths
                       {:paths [nil]})))
      (is (not (valid? data/Paths
                       {:paths ["/valid/path" nil]}))))

    (testing "invalid paths - wrong type"
      ;; paths must be a vector, not other types
      (is (not (valid? data/Paths
                       {:paths "/not/a/vector"})))
      (is (not (valid? data/Paths
                       {:paths 123})))
      (is (not (valid? data/Paths
                       {:paths nil}))))

    (testing "invalid paths - vector contains non-strings"
      ;; Elements must be strings
      (is (not (valid? data/Paths
                       {:paths [123]})))
      (is (not (valid? data/Paths
                       {:paths [true]})))
      (is (not (valid? data/Paths
                       {:paths [{:path "/test"}]}))))))

(deftest test-OptionalPaths
  (testing "OptionalPaths validation"
    (testing "valid paths with paths key present"
      (is (valid? data/OptionalPaths
                  {:paths ["/example/home/username/foo.txt"]}))
      (is (valid? data/OptionalPaths
                  {:paths ["/example/home/username/foo.txt" "/example/home/username/bar.txt"]}))
      (is (valid? data/OptionalPaths
                  {:paths ["/iplant/home/user/file.txt"
                           "/iplant/home/user/dir1/file2.csv"
                           "/iplant/home/user/dir2/file3.json"]})))

    (testing "valid paths - empty vector is allowed"
      ;; Unlike Paths, OptionalPaths doesn't have :min 1 constraint
      (is (valid? data/OptionalPaths
                  {:paths []})))

    (testing "valid paths - paths key is optional"
      ;; The :optional true property means the :paths key can be omitted
      (is (valid? data/OptionalPaths {})))

    (testing "invalid paths - blank strings"
      ;; NonBlankString validator should still reject empty and whitespace-only strings
      (is (not (valid? data/OptionalPaths
                       {:paths [""]})))
      (is (not (valid? data/OptionalPaths
                       {:paths ["   "]})))
      (is (not (valid? data/OptionalPaths
                       {:paths ["/valid/path" ""]})))
      (is (not (valid? data/OptionalPaths
                       {:paths ["/valid/path" "   "]}))))

    (testing "invalid paths - nil values"
      ;; nil is not a valid NonBlankString
      (is (not (valid? data/OptionalPaths
                       {:paths [nil]})))
      (is (not (valid? data/OptionalPaths
                       {:paths ["/valid/path" nil]}))))

    (testing "invalid paths - wrong type"
      ;; paths must be a vector when present, not other types
      (is (not (valid? data/OptionalPaths
                       {:paths "/not/a/vector"})))
      (is (not (valid? data/OptionalPaths
                       {:paths 123})))
      (is (not (valid? data/OptionalPaths
                       {:paths nil}))))

    (testing "invalid paths - vector contains non-strings"
      ;; Elements must be strings
      (is (not (valid? data/OptionalPaths
                       {:paths [123]})))
      (is (not (valid? data/OptionalPaths
                       {:paths [true]})))
      (is (not (valid? data/OptionalPaths
                       {:paths [{:path "/test"}]}))))))

(deftest test-Paths-vs-OptionalPaths-differences
  (testing "Key differences between Paths and OptionalPaths"
    (testing "Paths requires at least one element, OptionalPaths allows empty vector"
      (is (not (valid? data/Paths {:paths []})))
      (is (valid? data/OptionalPaths {:paths []})))

    (testing "Paths requires the paths key, OptionalPaths makes it optional"
      (is (not (valid? data/Paths {})))
      (is (valid? data/OptionalPaths {})))))

(deftest test-DataIds
  (testing "DataIds validation"
    (testing "valid data ids with single UUID"
      (is (valid? data/DataIds
                  {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e"]})))

    (testing "valid data ids with multiple UUIDs"
      (is (valid? data/DataIds
                  {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e"
                         #uuid "87f902c1-868b-4a36-86c1-7d5d268171bf"]}))
      (is (valid? data/DataIds
                  {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e"
                         #uuid "87f902c1-868b-4a36-86c1-7d5d268171bf"
                         #uuid "12345678-1234-1234-1234-123456789abc"]})))

    (testing "valid data ids - empty vector is allowed"
      ;; Unlike Paths, DataIds doesn't have a :min constraint
      (is (valid? data/DataIds
                  {:ids []})))

    (testing "invalid data ids - missing ids key"
      ;; The :ids key is required in a :map
      (is (not (valid? data/DataIds {}))))

    (testing "invalid data ids - non-UUID strings"
      ;; Strings that aren't valid UUID format should fail
      (is (not (valid? data/DataIds
                       {:ids ["not-a-uuid"]})))
      (is (not (valid? data/DataIds
                       {:ids ["8a950a63-f999-403c"]})))  ;; Incomplete UUID
      (is (not (valid? data/DataIds
                       {:ids ["8a950a63-f999-403c-912e-97e11109e68e-extra"]})))  ;; Extra chars
      (is (not (valid? data/DataIds
                       {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e" "not-a-uuid"]}))))

    (testing "invalid data ids - numbers instead of UUIDs"
      ;; Numbers are not valid UUIDs
      (is (not (valid? data/DataIds
                       {:ids [123]})))
      (is (not (valid? data/DataIds
                       {:ids [123.456]}))))

    (testing "invalid data ids - nil values"
      ;; nil is not a valid UUID
      (is (not (valid? data/DataIds
                       {:ids [nil]})))
      (is (not (valid? data/DataIds
                       {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e" nil]}))))

    (testing "invalid data ids - wrong type for ids field"
      ;; ids must be a vector, not other types
      (is (not (valid? data/DataIds
                       {:ids "8a950a63-f999-403c-912e-97e11109e68e"})))
      (is (not (valid? data/DataIds
                       {:ids #uuid "8a950a63-f999-403c-912e-97e11109e68e"})))
      (is (not (valid? data/DataIds
                       {:ids 123})))
      (is (not (valid? data/DataIds
                       {:ids nil}))))

    (testing "invalid data ids - extra keys"
      ;; The :closed true property means extra keys should fail
      (is (not (valid? data/DataIds
                       {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e"]
                        :extra-key "value"})))
      (is (not (valid? data/DataIds
                       {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e"]
                        :paths ["/some/path"]}))))

    (testing "invalid data ids - vector contains non-UUIDs"
      ;; Elements must be UUIDs
      (is (not (valid? data/DataIds
                       {:ids [true]})))
      (is (not (valid? data/DataIds
                       {:ids [{:id "8a950a63-f999-403c-912e-97e11109e68e"}]}))))))

(deftest test-OptionalPathsOrDataIds
  (testing "OptionalPathsOrDataIds validation"
    (testing "valid data with just ids"
      (is (valid? data/OptionalPathsOrDataIds
                  {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e"]}))
      (is (valid? data/OptionalPathsOrDataIds
                  {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e"
                         #uuid "87f902c1-868b-4a36-86c1-7d5d268171bf"]})))

    (testing "valid data with just paths"
      (is (valid? data/OptionalPathsOrDataIds
                  {:paths ["/example/home/username/foo.txt"]}))
      (is (valid? data/OptionalPathsOrDataIds
                  {:paths ["/example/home/username/foo.txt" "/example/home/username/bar.txt"]})))

    (testing "valid data with both ids and paths"
      (is (valid? data/OptionalPathsOrDataIds
                  {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e"]
                   :paths ["/example/home/username/foo.txt"]}))
      (is (valid? data/OptionalPathsOrDataIds
                  {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e"
                         #uuid "87f902c1-868b-4a36-86c1-7d5d268171bf"]
                   :paths ["/example/home/username/foo.txt"
                           "/example/home/username/bar.txt"]})))

    (testing "valid data - empty map"
      ;; Both fields are optional after mu/optional-keys
      (is (valid? data/OptionalPathsOrDataIds {})))

    (testing "valid data - empty vectors"
      ;; Empty vectors should be allowed
      (is (valid? data/OptionalPathsOrDataIds
                  {:ids []}))
      (is (valid? data/OptionalPathsOrDataIds
                  {:paths []}))
      (is (valid? data/OptionalPathsOrDataIds
                  {:ids []
                   :paths []})))

    (testing "invalid data - invalid ids values"
      ;; Invalid UUID formats should fail
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:ids ["not-a-uuid"]})))
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:ids [123]})))
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:ids [nil]})))
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:ids "not-a-vector"}))))

    (testing "invalid data - invalid paths values"
      ;; Invalid path values should fail
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:paths [""]})))
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:paths ["   "]})))
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:paths [nil]})))
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:paths [123]})))
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:paths "not-a-vector"}))))

    (testing "invalid data - invalid combination"
      ;; One valid, one invalid field should fail overall
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e"]
                        :paths [""]})))
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:ids ["not-a-uuid"]
                        :paths ["/valid/path"]}))))

    (testing "invalid data - extra keys"
      ;; The :closed true property from parent schemas means extra keys should fail
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e"]
                        :extra-key "value"})))
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:paths ["/example/home/username/foo.txt"]
                        :extra-key "value"})))
      (is (not (valid? data/OptionalPathsOrDataIds
                       {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e"]
                        :paths ["/example/home/username/foo.txt"]
                        :extra-key "value"}))))

    (testing "both ids and paths are optional"
      ;; Verify each field can be omitted independently
      (is (valid? data/OptionalPathsOrDataIds
                  {:ids [#uuid "8a950a63-f999-403c-912e-97e11109e68e"]}))
      (is (valid? data/OptionalPathsOrDataIds
                  {:paths ["/example/home/username/foo.txt"]}))
      (is (valid? data/OptionalPathsOrDataIds {})))))

(deftest test-FolderListingPagingParams
  (testing "FolderListingPagingParams validation"
    (testing "valid params with all fields"
      (is (valid? data/FolderListingPagingParams
                  {:limit      50
                   :offset     0
                   :sort-field :datecreated
                   :sort-dir   "ASC"}))
      (is (valid? data/FolderListingPagingParams
                  {:limit      100
                   :offset     25
                   :sort-field :name
                   :sort-dir   "DESC"}))
      (is (valid? data/FolderListingPagingParams
                  {:limit      10
                   :offset     0
                   :sort-field :datemodified
                   :sort-dir   "ASC"})))

    (testing "valid params with only limit and offset"
      (is (valid? data/FolderListingPagingParams
                  {:limit  50
                   :offset 0}))
      (is (valid? data/FolderListingPagingParams
                  {:limit  100
                   :offset 10})))

    (testing "valid params with only sort fields"
      (is (valid? data/FolderListingPagingParams
                  {:sort-field :datecreated
                   :sort-dir   "ASC"}))
      (is (valid? data/FolderListingPagingParams
                  {:sort-field :name
                   :sort-dir   "DESC"})))

    (testing "valid params with only limit"
      (is (valid? data/FolderListingPagingParams
                  {:limit 50}))
      (is (valid? data/FolderListingPagingParams
                  {:limit 1})))

    (testing "valid params with only offset"
      (is (valid? data/FolderListingPagingParams
                  {:offset 0}))
      (is (valid? data/FolderListingPagingParams
                  {:offset 100})))

    (testing "valid params with only sort-field"
      (is (valid? data/FolderListingPagingParams
                  {:sort-field :datecreated}))
      (is (valid? data/FolderListingPagingParams
                  {:sort-field :datemodified}))
      (is (valid? data/FolderListingPagingParams
                  {:sort-field :name}))
      (is (valid? data/FolderListingPagingParams
                  {:sort-field :path}))
      (is (valid? data/FolderListingPagingParams
                  {:sort-field :size})))

    (testing "valid params with only sort-dir"
      (is (valid? data/FolderListingPagingParams
                  {:sort-dir "ASC"}))
      (is (valid? data/FolderListingPagingParams
                  {:sort-dir "DESC"})))

    (testing "valid params - empty map (all fields are optional)"
      (is (valid? data/FolderListingPagingParams {})))

    (testing "invalid params - limit must be positive"
      ;; The :fn pos? constraint requires limit > 0
      (is (not (valid? data/FolderListingPagingParams
                       {:limit 0})))
      (is (not (valid? data/FolderListingPagingParams
                       {:limit -1})))
      (is (not (valid? data/FolderListingPagingParams
                       {:limit -100}))))

    (testing "invalid params - offset must be non-negative"
      ;; The :fn (comp not neg?) constraint requires offset >= 0
      (is (not (valid? data/FolderListingPagingParams
                       {:offset -1})))
      (is (not (valid? data/FolderListingPagingParams
                       {:offset -100}))))

    (testing "invalid params - limit must be an integer"
      (is (not (valid? data/FolderListingPagingParams
                       {:limit 50.5})))
      (is (not (valid? data/FolderListingPagingParams
                       {:limit "50"})))
      (is (not (valid? data/FolderListingPagingParams
                       {:limit nil})))
      (is (not (valid? data/FolderListingPagingParams
                       {:limit true}))))

    (testing "invalid params - offset must be an integer"
      (is (not (valid? data/FolderListingPagingParams
                       {:offset 10.5})))
      (is (not (valid? data/FolderListingPagingParams
                       {:offset "10"})))
      (is (not (valid? data/FolderListingPagingParams
                       {:offset nil})))
      (is (not (valid? data/FolderListingPagingParams
                       {:offset true}))))

    (testing "invalid params - sort-field must be a valid enum value"
      ;; Only :datecreated, :datemodified, :name, :path, :size are valid
      (is (not (valid? data/FolderListingPagingParams
                       {:sort-field :invalid})))
      (is (not (valid? data/FolderListingPagingParams
                       {:sort-field :id})))
      (is (not (valid? data/FolderListingPagingParams
                       {:sort-field "datecreated"})))  ;; Must be keyword, not string
      (is (not (valid? data/FolderListingPagingParams
                       {:sort-field nil})))
      (is (not (valid? data/FolderListingPagingParams
                       {:sort-field 123}))))

    (testing "invalid params - sort-dir must be ASC or DESC"
      (is (not (valid? data/FolderListingPagingParams
                       {:sort-dir "asc"})))  ;; Case sensitive
      (is (not (valid? data/FolderListingPagingParams
                       {:sort-dir "desc"})))  ;; Case sensitive
      (is (not (valid? data/FolderListingPagingParams
                       {:sort-dir "ASCENDING"})))
      (is (not (valid? data/FolderListingPagingParams
                       {:sort-dir :ASC})))  ;; Must be string, not keyword
      (is (not (valid? data/FolderListingPagingParams
                       {:sort-dir nil})))
      (is (not (valid? data/FolderListingPagingParams
                       {:sort-dir 123}))))

    (testing "invalid params - extra keys not allowed"
      ;; The :closed true property means extra keys should fail
      (is (not (valid? data/FolderListingPagingParams
                       {:limit     50
                        :extra-key "value"})))
      (is (not (valid? data/FolderListingPagingParams
                       {:sort-field  :name
                        :unknown-key true}))))

    (testing "valid params - boundary values for offset (0 is valid)"
      (is (valid? data/FolderListingPagingParams
                  {:offset 0}))
      (is (valid? data/FolderListingPagingParams
                  {:offset 1}))
      (is (valid? data/FolderListingPagingParams
                  {:offset 999999})))

    (testing "valid params - boundary values for limit (must be positive)"
      (is (valid? data/FolderListingPagingParams
                  {:limit 1}))
      (is (valid? data/FolderListingPagingParams
                  {:limit 999999})))))

(deftest test-FolderListingParams
  (testing "FolderListingParams validation"
    (testing "valid params with all fields"
      (is (valid? data/FolderListingParams
                  {:limit       50
                   :offset      0
                   :sort-field  :datecreated
                   :sort-dir    "ASC"
                   :entity-type :file
                   :info-type   "fasta"}))
      (is (valid? data/FolderListingParams
                  {:limit       100
                   :offset      25
                   :sort-field  :name
                   :sort-dir    "DESC"
                   :entity-type :folder
                   :info-type   "csv"}))
      (is (valid? data/FolderListingParams
                  {:limit       10
                   :offset      0
                   :sort-field  :size
                   :sort-dir    "ASC"
                   :entity-type :any
                   :info-type   "perl"})))

    (testing "valid params with only paging fields (inherited from FolderListingPagingParams)"
      (is (valid? data/FolderListingParams
                  {:limit      50
                   :offset     0
                   :sort-field :datecreated
                   :sort-dir   "ASC"}))
      (is (valid? data/FolderListingParams
                  {:limit  100
                   :offset 10})))

    (testing "valid params with only entity-type"
      (is (valid? data/FolderListingParams
                  {:entity-type :file}))
      (is (valid? data/FolderListingParams
                  {:entity-type :folder}))
      (is (valid? data/FolderListingParams
                  {:entity-type :any})))

    (testing "valid params with only info-type as single value"
      (is (valid? data/FolderListingParams
                  {:info-type "fasta"}))
      (is (valid? data/FolderListingParams
                  {:info-type "csv"}))
      (is (valid? data/FolderListingParams
                  {:info-type "perl"})))

    (testing "valid params with info-type as vector"
      ;; The :or allows either a single value or a vector of values
      (is (valid? data/FolderListingParams
                  {:info-type ["fasta"]}))
      (is (valid? data/FolderListingParams
                  {:info-type ["fasta" "csv"]}))
      (is (valid? data/FolderListingParams
                  {:info-type ["fasta" "csv" "perl"]})))

    (testing "valid params - empty map (all fields are optional)"
      (is (valid? data/FolderListingParams {})))

    (testing "valid params - combining paging and filtering"
      (is (valid? data/FolderListingParams
                  {:limit       50
                   :entity-type :file}))
      (is (valid? data/FolderListingParams
                  {:offset    10
                   :info-type "fasta"}))
      (is (valid? data/FolderListingParams
                  {:sort-field  :name
                   :entity-type :folder
                   :info-type   ["fasta" "csv"]})))

    (testing "invalid params - limit must be positive (inherited validation)"
      (is (not (valid? data/FolderListingParams
                       {:limit 0})))
      (is (not (valid? data/FolderListingParams
                       {:limit -1}))))

    (testing "invalid params - offset must be non-negative (inherited validation)"
      (is (not (valid? data/FolderListingParams
                       {:offset -1})))
      (is (not (valid? data/FolderListingParams
                       {:offset -100}))))

    (testing "invalid params - sort-field must be valid (inherited validation)"
      (is (not (valid? data/FolderListingParams
                       {:sort-field :invalid})))
      (is (not (valid? data/FolderListingParams
                       {:sort-field :id}))))

    (testing "invalid params - sort-dir must be ASC or DESC (inherited validation)"
      (is (not (valid? data/FolderListingParams
                       {:sort-dir "asc"})))  ;; Case sensitive
      (is (not (valid? data/FolderListingParams
                       {:sort-dir "ASCENDING"}))))

    (testing "invalid params - entity-type must be :any, :file, or :folder"
      (is (not (valid? data/FolderListingParams
                       {:entity-type :invalid})))
      (is (not (valid? data/FolderListingParams
                       {:entity-type :directory})))
      (is (not (valid? data/FolderListingParams
                       {:entity-type "file"})))  ;; Must be keyword, not string
      (is (not (valid? data/FolderListingParams
                       {:entity-type nil})))
      (is (not (valid? data/FolderListingParams
                       {:entity-type 123}))))

    (testing "invalid params - info-type must be valid enum value or vector of enum values"
      ;; Invalid single value
      (is (not (valid? data/FolderListingParams
                       {:info-type "invalid-type"})))
      (is (not (valid? data/FolderListingParams
                       {:info-type :fasta})))
      (is (not (valid? data/FolderListingParams
                       {:info-type nil})))
      (is (not (valid? data/FolderListingParams
                       {:info-type 123})))
      ;; Invalid vector
      (is (not (valid? data/FolderListingParams
                       {:info-type ["invalid-type"]})))
      (is (not (valid? data/FolderListingParams
                       {:info-type [:fasta]})))
      (is (not (valid? data/FolderListingParams
                       {:info-type ["fasta" "invalid-type"]})))
      ;; Mixed valid and invalid
      (is (not (valid? data/FolderListingParams
                       {:info-type ["fasta" "csv" "blargh"]}))))

    (testing "invalid params - extra keys not allowed"
      ;; The :closed true property means extra keys should fail
      (is (not (valid? data/FolderListingParams
                       {:limit     50
                        :extra-key "value"})))
      (is (not (valid? data/FolderListingParams
                       {:entity-type :file
                        :unknown-key true}))))

    (testing "invalid params - wrong types for paging fields (inherited validation)"
      (is (not (valid? data/FolderListingParams
                       {:limit "50"})))
      (is (not (valid? data/FolderListingParams
                       {:offset "10"}))))

    (testing "valid params - all combinations of entity-type and info-type"
      ;; Test different valid combinations
      (is (valid? data/FolderListingParams
                  {:entity-type :file
                   :info-type   "fasta"}))
      (is (valid? data/FolderListingParams
                  {:entity-type :folder
                   :info-type   ["csv" "perl"]}))
      (is (valid? data/FolderListingParams
                  {:entity-type :any
                   :info-type   ["fasta" "csv" "perl"]})))

    (testing "valid params - info-type empty vector is allowed by :or validator"
      ;; An empty vector should match the [:vector ValidInfoTypesEnum] branch
      (is (valid? data/FolderListingParams
                  {:info-type []})))

    (testing "valid params - boundary values"
      (is (valid? data/FolderListingParams
                  {:limit  1
                   :offset 0}))
      (is (valid? data/FolderListingParams
                  {:limit  999999
                   :offset 999999})))

    (testing "valid params - all sort fields work with new params"
      (is (valid? data/FolderListingParams
                  {:sort-field  :datecreated
                   :entity-type :file}))
      (is (valid? data/FolderListingParams
                  {:sort-field  :datemodified
                   :entity-type :folder}))
      (is (valid? data/FolderListingParams
                  {:sort-field :name
                   :info-type  "fasta"}))
      (is (valid? data/FolderListingParams
                  {:sort-field :path
                   :info-type  ["csv" "perl"]}))
      (is (valid? data/FolderListingParams
                  {:sort-field :size
                   :info-type  "fasta"})))))
