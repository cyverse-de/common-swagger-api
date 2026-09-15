(ns common-swagger-api.malli.filetypes-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.filetypes :as filetypes]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(deftest ValidInfoTypes
  (is (every? (set filetypes/ValidInfoTypes) ["fasta" "csv" "unknown"]))
  (is (not (contains? (set filetypes/ValidInfoTypes) ""))))

(deftest ValidInfoTypesEnum
  (valid filetypes/ValidInfoTypesEnum "fasta" "csv" "unknown")
  (invalid filetypes/ValidInfoTypesEnum "" "invalid-type" nil))

(deftest ValidInfoTypesEnumPlusBlank
  (valid filetypes/ValidInfoTypesEnumPlusBlank "fasta" "unknown" "")
  (invalid filetypes/ValidInfoTypesEnumPlusBlank "invalid-type" nil :fasta))

(deftest TypesList
  (valid filetypes/TypesList {:types []} {:types ["fasta" "csv"]})
  (invalid filetypes/TypesList {} {:types "fasta"} {:types ["fasta" 1]} {:types [] :extra 1}))

(deftest FileType
  (valid filetypes/FileType {:type "fasta"} {:type ""})
  (invalid filetypes/FileType {} {:type "invalid-type"} {:type "fasta" :extra 1}))

(deftest FileTypeReturn
  (valid filetypes/FileTypeReturn {:type "fasta" :user "ipctest" :path "/iplant/home/ipctest/f.fa"})
  (invalid filetypes/FileTypeReturn
           {:type "fasta"}
           {:type "fasta" :user " " :path "/iplant/home/ipctest/f.fa"}
           {:type "fasta" :user "ipctest" :path "/f.fa" :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.filetypes))
