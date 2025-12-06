(ns common-swagger-api.malli.filetypes-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.filetypes :as ft]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-ValidInfoTypes
  (testing "ValidInfoTypes contains expected values"
    (is (some #{"fasta"} ft/ValidInfoTypes))
    (is (some #{"unknown"} ft/ValidInfoTypes))
    (is (some #{"csv"} ft/ValidInfoTypes))
    (is (some #{"pdf"} ft/ValidInfoTypes))))

(deftest test-ValidInfoTypesEnum
  (testing "ValidInfoTypesEnum validation"
    (is (valid? ft/ValidInfoTypesEnum "fasta"))
    (is (valid? ft/ValidInfoTypesEnum "unknown"))
    (is (valid? ft/ValidInfoTypesEnum "csv"))
    (is (valid? ft/ValidInfoTypesEnum "pdf"))
    (is (not (valid? ft/ValidInfoTypesEnum "invalid-type")))
    (is (not (valid? ft/ValidInfoTypesEnum "")))
    (is (not (valid? ft/ValidInfoTypesEnum nil)))))

(deftest test-ValidInfoTypesEnumPlusBlank
  (testing "ValidInfoTypesEnumPlusBlank validation"
    (is (valid? ft/ValidInfoTypesEnumPlusBlank "fasta"))
    (is (valid? ft/ValidInfoTypesEnumPlusBlank "unknown"))
    (is (valid? ft/ValidInfoTypesEnumPlusBlank "csv"))
    (is (valid? ft/ValidInfoTypesEnumPlusBlank ""))  ; Blank is allowed
    (is (not (valid? ft/ValidInfoTypesEnumPlusBlank "invalid-type")))
    (is (not (valid? ft/ValidInfoTypesEnumPlusBlank nil)))))

(deftest test-TypesList
  (testing "TypesList validation"
    (testing "valid types list"
      (is (valid? ft/TypesList
                  {:types ["fasta" "csv" "pdf"]}))
      (is (valid? ft/TypesList
                  {:types []}))  ; Empty list is valid
      (is (valid? ft/TypesList
                  {:types ["unknown"]})))

    (testing "invalid types list"
      ;; Extra fields not allowed due to :closed true
      (is (not (valid? ft/TypesList
                       {:types ["fasta"]
                        :extra "field"})))
      ;; Missing required field
      (is (not (valid? ft/TypesList {})))
      ;; Wrong type for types field
      (is (not (valid? ft/TypesList
                       {:types "not-a-vector"})))
      (is (not (valid? ft/TypesList
                       {:types ["fasta" 123]}))))))  ; Non-string in vector

(deftest test-FileType
  (testing "FileType validation"
    (testing "valid file type"
      (is (valid? ft/FileType
                  {:type "fasta"}))
      (is (valid? ft/FileType
                  {:type "unknown"}))
      (is (valid? ft/FileType
                  {:type ""}))  ; Blank is allowed
      (is (valid? ft/FileType
                  {:type "csv"})))

    (testing "invalid file type"
      ;; Extra fields not allowed due to :closed true
      (is (not (valid? ft/FileType
                       {:type "fasta"
                        :extra "field"})))
      ;; Missing required field
      (is (not (valid? ft/FileType {})))
      ;; Invalid type value
      (is (not (valid? ft/FileType
                       {:type "invalid-type"})))
      ;; Wrong type for type field
      (is (not (valid? ft/FileType
                       {:type 123})))
      (is (not (valid? ft/FileType
                       {:type nil}))))))

(deftest test-FileTypeReturn
  (testing "FileTypeReturn validation"
    (testing "valid file type return"
      (is (valid? ft/FileTypeReturn
                  {:type "fasta"
                   :user "testuser"
                   :path "/iplant/home/user/file.fasta"}))
      (is (valid? ft/FileTypeReturn
                  {:type ""
                   :user "testuser"
                   :path "/iplant/home/user/file.txt"}))
      (is (valid? ft/FileTypeReturn
                  {:type "csv"
                   :user "anotheruser"
                   :path "/iplant/home/another/data.csv"})))

    (testing "invalid file type return"
      ;; Missing required fields
      (is (not (valid? ft/FileTypeReturn
                       {:type "fasta"})))
      (is (not (valid? ft/FileTypeReturn
                       {:user "testuser"
                        :path "/path"})))
      (is (not (valid? ft/FileTypeReturn
                       {:type "fasta"
                        :user "testuser"})))

      ;; Invalid type values
      (is (not (valid? ft/FileTypeReturn
                       {:type "invalid-type"
                        :user "testuser"
                        :path "/path"})))

      ;; Blank/empty user (NonBlankString validation)
      (is (not (valid? ft/FileTypeReturn
                       {:type "fasta"
                        :user ""
                        :path "/path"})))
      (is (not (valid? ft/FileTypeReturn
                       {:type "fasta"
                        :user "   "
                        :path "/path"})))

      ;; Blank/empty path (NonBlankString validation)
      (is (not (valid? ft/FileTypeReturn
                       {:type "fasta"
                        :user "testuser"
                        :path ""})))
      (is (not (valid? ft/FileTypeReturn
                       {:type "fasta"
                        :user "testuser"
                        :path "   "})))

      ;; Extra fields not allowed due to closed map
      (is (not (valid? ft/FileTypeReturn
                       {:type "fasta"
                        :user "testuser"
                        :path "/path"
                        :extra "field"}))))))

(deftest test-schema-relationships
  (testing "Schema inheritance and merging"
    ;; FileTypeReturn should contain all fields from FileType plus user and path
    (let [file-type-data {:type "fasta"}
          file-type-return-data (assoc file-type-data
                                       :user "testuser"
                                       :path "/path")]
      (is (valid? ft/FileType file-type-data))
      (is (valid? ft/FileTypeReturn file-type-return-data))
      ;; FileType data alone should not be valid for FileTypeReturn
      (is (not (valid? ft/FileTypeReturn file-type-data))))))
