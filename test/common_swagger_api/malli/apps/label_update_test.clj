(ns common-swagger-api.malli.apps.label-update-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common-swagger-api.malli.apps :as apps]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AppLabelUpdateRequest
  (testing "AppLabelUpdateRequest validation"
    (testing "minimal valid request"
      (is (valid? apps/AppLabelUpdateRequest
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                            :name "blast"
                            :version "2.14.0"
                            :type "executable"}]})))

    (testing "valid request with all AppBase fields"
      (is (valid? apps/AppLabelUpdateRequest
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "FastQC"
                   :description "Quality control tool for high throughput sequence data"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                   :system_id "de"
                   :version "1.2.0"
                   :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :tools [{:id #uuid "555e5555-e55e-55e5-e555-555555555555"
                            :name "fastqc"
                            :version "0.11.9"
                            :type "executable"
                            :deprecated false}]})))

    (testing "valid request with references"
      (is (valid? apps/AppLabelUpdateRequest
                  {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                   :name "BWA"
                   :description "Burrows-Wheeler Aligner"
                   :tools [{:id #uuid "666e6666-f66f-66f6-f666-666666666666"
                            :name "bwa"
                            :version "0.7.17"
                            :type "executable"}]
                   :references ["https://doi.org/10.1093/bioinformatics/btp324"
                                "PMID: 19451168"]})))

    (testing "valid request with parameter groups"
      (is (valid? apps/AppLabelUpdateRequest
                  {:id #uuid "111e1111-a11a-11a1-a111-111111111111"
                   :name "Samtools"
                   :description "Tools for manipulating SAM/BAM files"
                   :tools [{:id #uuid "777e7777-a77a-77a7-a777-777777777777"
                            :name "samtools"
                            :version "1.15.1"
                            :type "executable"}]
                   :groups [{:id #uuid "888e8888-a88a-88a8-a888-888888888888"
                             :label "Input Parameters"
                             :name "input_params"
                             :description "Input file parameters"
                             :isVisible true
                             :parameters [{:id #uuid "999e9999-b99b-99b9-b999-999999999999"
                                           :type "FileInput"
                                           :label "Input File"
                                           :required true}]}]})))

    (testing "valid request with complex app structure"
      (is (valid? apps/AppLabelUpdateRequest
                  {:id #uuid "222e2222-b22b-22b2-b222-222222222222"
                   :name "Complete App"
                   :description "An app with all fields"
                   :integration_date #inst "2024-01-01T00:00:00.000-00:00"
                   :edited_date #inst "2024-12-31T23:59:59.000-00:00"
                   :system_id "condor"
                   :version "2.0.0"
                   :version_id #uuid "333e3333-c33c-33c3-c333-333333333333"
                   :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                            :name "tool1"
                            :version "1.0"
                            :type "executable"
                            :deprecated false}
                           {:id #uuid "555e5555-e55e-55e5-e555-555555555555"
                            :name "tool2"
                            :version "2.0"
                            :type "executable"
                            :deprecated false}]
                   :references ["https://doi.org/10.1093/nar/gkv416"
                                "PMID: 25916842"
                                "https://example.com/docs"]
                   :groups [{:id #uuid "666e6666-f66f-66f6-f666-666666666666"
                             :label "Input Parameters"
                             :name "input_params"
                             :description "Input file parameters"
                             :isVisible true
                             :parameters [{:id #uuid "777e7777-a77a-77a7-a777-777777777777"
                                           :type "FileInput"
                                           :name "--input"
                                           :label "Input File"
                                           :description "Select input file"
                                           :required true
                                           :isVisible true
                                           :file_parameters {:format "fasta"
                                                             :file_info_type "SequenceAlignment"
                                                             :retain true}}
                                          {:id #uuid "888e8888-a88a-88a8-a888-888888888888"
                                           :type "IntegerInput"
                                           :name "--threads"
                                           :label "Number of Threads"
                                           :defaultValue 4
                                           :order 1
                                           :required false
                                           :validators [{:type "IntAbove"
                                                         :params [0]}
                                                        {:type "IntBelow"
                                                         :params [100]}]}]}
                            {:id #uuid "999e9999-b99b-99b9-b999-999999999999"
                             :label "Output Parameters"
                             :isVisible true
                             :parameters [{:id #uuid "aaaeaaaa-caaa-aaa1-aaaa-aaaaaaaaaaaa"
                                           :type "FileOutput"
                                           :label "Output File"}]}]})))

    (testing "versions field is properly removed"
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]
                        :versions [{:version "1.0.0"
                                    :version_id #uuid "555e5555-e55e-55e5-e555-555555555555"}]}))))

    (testing "invalid requests - missing required AppBase fields"
      (is (not (valid? apps/AppLabelUpdateRequest {})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :description "BLAST tool"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]}))))

    (testing "invalid requests - missing tools field"
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"}))))

    (testing "invalid requests - wrong types for AppBase fields"
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id "not-a-uuid"
                        :name "BLAST"
                        :description "BLAST tool"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name 123
                        :description "BLAST tool"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description true
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :integration_date "not-a-date"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :edited_date 12345
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :system_id 123
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :version 123
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :version_id "not-a-uuid"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]}))))

    (testing "invalid requests - wrong types for tools fields"
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :tools "not-a-vector"})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :tools []})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"
                                 :deprecated "not-a-boolean"}]}))))

    (testing "invalid requests - wrong types for references field"
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]
                        :references "not-a-vector"})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]
                        :references [123]}))))

    (testing "invalid requests - wrong types for groups field"
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]
                        :groups "not-a-vector"})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]
                        :groups [{}]})))
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]
                        :groups [{:id #uuid "999e9999-b99b-99b9-b999-999999999999"}]}))))

    (testing "invalid requests - extra fields not allowed"
      (is (not (valid? apps/AppLabelUpdateRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "executable"}]
                        :extra-field "not-allowed"}))))))
