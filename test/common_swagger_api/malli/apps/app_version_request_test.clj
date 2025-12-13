(ns common-swagger-api.malli.apps.app-version-request-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common-swagger-api.malli.apps :as apps]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AppVersionRequest
  (testing "AppVersionRequest validation"
    (testing "valid version request - minimal required fields"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :version "2.14.0"})))

    (testing "valid version request - with id field (optional)"
      (is (valid? apps/AppVersionRequest
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :version "2.14.0"})))

    (testing "valid version request - without id field"
      (is (valid? apps/AppVersionRequest
                  {:name "Trinity"
                   :description "De novo transcript assembly"
                   :version "2.13.2"})))

    (testing "valid version request - with all AppBase fields"
      (is (valid? apps/AppVersionRequest
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :version "2.14.0"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                   :system_id "de"})))

    (testing "valid version request - with empty tools vector"
      (is (valid? apps/AppVersionRequest
                  {:name "Simple App"
                   :description "An app with no tools"
                   :version "1.0.0"
                   :tools []})))

    (testing "valid version request - with single tool (minimal)"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST App"
                   :description "BLAST analysis"
                   :version "2.14.0"
                   :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                            :name "blast"
                            :version "2.14.0"
                            :type "docker"}]})))

    (testing "valid version request - with single tool (complete)"
      (is (valid? apps/AppVersionRequest
                  {:name "SAMtools App"
                   :description "Sequence alignment processing"
                   :version "1.15.1"
                   :tools [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                            :name "samtools"
                            :description "SAM/BAM file manipulation"
                            :attribution "Heng Li et al."
                            :location "/usr/local/bin"
                            :version "1.15.1"
                            :type "executable"
                            :restricted false
                            :time_limit_seconds 3600
                            :interactive false
                            :is_public true
                            :permission "own"
                            :deprecated false}]})))

    (testing "valid version request - with multiple tools"
      (is (valid? apps/AppVersionRequest
                  {:name "Pipeline App"
                   :description "Multi-tool pipeline"
                   :version "1.0.0"
                   :tools [{:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                            :name "samtools"
                            :version "1.15.1"
                            :type "executable"}
                           {:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                            :name "blast"
                            :version "2.14.0"
                            :type "docker"}
                           {:id #uuid "333a0123-c45d-67e8-f901-234567890abc"
                            :name "bowtie2"
                            :version "2.5.0"
                            :type "executable"}]})))

    (testing "valid version request - with references field"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :version "2.14.0"
                   :references ["https://doi.org/10.1093/nar/gkv416" "PMID: 25916842"]})))

    (testing "valid version request - with empty references vector"
      (is (valid? apps/AppVersionRequest
                  {:name "Simple App"
                   :description "App with no references"
                   :version "1.0.0"
                   :references []})))

    (testing "valid version request - with empty groups vector"
      (is (valid? apps/AppVersionRequest
                  {:name "Minimal App"
                   :description "App with no parameter groups"
                   :version "1.0.0"
                   :groups []})))

    (testing "valid version request - with single group (minimal)"
      (is (valid? apps/AppVersionRequest
                  {:name "Simple App"
                   :description "App with one parameter group"
                   :version "1.0.0"
                   :groups [{:label "Input Parameters"}]})))

    (testing "valid version request - with single group (with parameters)"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST App"
                   :description "BLAST analysis"
                   :version "2.14.0"
                   :groups [{:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                             :name "input_parameters"
                             :description "Input file parameters"
                             :label "Input Parameters"
                             :isVisible true
                             :parameters [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                                           :name "--input"
                                           :label "Input File"
                                           :description "Query sequence file"
                                           :type "FileInput"
                                           :required true
                                           :isVisible true
                                           :order 1
                                           :file_parameters {:format "fasta"
                                                             :file_info_type "SequenceAlignment"
                                                             :retain true}}]}]})))

    (testing "valid version request - with multiple groups"
      (is (valid? apps/AppVersionRequest
                  {:name "Complex App"
                   :description "App with multiple parameter groups"
                   :version "1.0.0"
                   :groups [{:label "Input Parameters"
                             :parameters [{:type "FileInput"
                                           :name "--input"
                                           :label "Input File"}]}
                            {:label "Configuration"
                             :parameters [{:type "Integer"
                                           :name "--threads"
                                           :label "Threads"}]}
                            {:label "Output Options"
                             :parameters [{:type "FileOutput"
                                           :name "--output"
                                           :label "Output File"}]}]})))

    (testing "valid version request - complete single-step app"
      (is (valid? apps/AppVersionRequest
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "BLAST Analysis"
                   :description "Perform sequence similarity search using BLAST"
                   :version "2.14.0"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                   :system_id "de"
                   :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                            :name "blast"
                            :description "BLAST sequence alignment tool"
                            :attribution "NCBI"
                            :version "2.14.0"
                            :type "docker"
                            :restricted false
                            :time_limit_seconds 7200
                            :interactive false
                            :is_public true
                            :permission "read"
                            :deprecated false}]
                   :references ["https://doi.org/10.1093/nar/gkv416" "PMID: 25916842"]
                   :groups [{:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                             :name "input_parameters"
                             :description "Input file and configuration parameters"
                             :label "Input Parameters"
                             :isVisible true
                             :parameters [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                                           :name "--query"
                                           :label "Query Sequence"
                                           :description "Input sequence file in FASTA format"
                                           :type "FileInput"
                                           :required true
                                           :isVisible true
                                           :order 1
                                           :file_parameters {:format "fasta"
                                                             :file_info_type "SequenceAlignment"
                                                             :retain true}}
                                          {:id #uuid "def67890-abc1-2345-6789-0abcdef12345"
                                           :name "--database"
                                           :label "Database"
                                           :description "Database to search against"
                                           :type "TextSelection"
                                           :required true
                                           :isVisible true
                                           :order 2
                                           :arguments [{:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                                                        :name "nr"
                                                        :value "nr"
                                                        :display "Non-redundant protein"
                                                        :description "NCBI nr database"
                                                        :isDefault true}
                                                       {:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                                                        :name "nt"
                                                        :value "nt"
                                                        :display "Non-redundant nucleotide"
                                                        :description "NCBI nt database"
                                                        :isDefault false}]}]}
                            {:id #uuid "333a0123-c45d-67e8-f901-234567890abc"
                             :name "config_parameters"
                             :description "Algorithm configuration"
                             :label "Configuration"
                             :isVisible true
                             :parameters [{:id #uuid "444a0123-c45d-67e8-f901-234567890abc"
                                           :name "--evalue"
                                           :label "E-value Threshold"
                                           :description "Expectation value threshold"
                                           :type "Number"
                                           :defaultValue 0.001
                                           :required false
                                           :isVisible true
                                           :order 1
                                           :validators [{:type "NumberAbove"
                                                         :params [0]}]}
                                          {:id #uuid "555a0123-c45d-67e8-f901-234567890abc"
                                           :name "--threads"
                                           :label "Number of Threads"
                                           :description "Number of CPU threads to use"
                                           :type "Integer"
                                           :defaultValue 4
                                           :required false
                                           :isVisible true
                                           :order 2
                                           :validators [{:type "IntAbove"
                                                         :params [0]}
                                                        {:type "IntBelow"
                                                         :params [64]}]}]}
                            {:id #uuid "666a0123-c45d-67e8-f901-234567890abc"
                             :name "output_parameters"
                             :description "Output file configuration"
                             :label "Output Options"
                             :isVisible true
                             :parameters [{:id #uuid "777a0123-c45d-67e8-f901-234567890abc"
                                           :name "--output"
                                           :label "Output File"
                                           :description "BLAST results file"
                                           :type "FileOutput"
                                           :required false
                                           :isVisible true
                                           :order 1
                                           :file_parameters {:format "xml"
                                                             :file_info_type "BlastOutput"
                                                             :is_implicit false
                                                             :data_source "stdout"}}]}]})))

    (testing "invalid version request - missing required field: name"
      (is (not (valid? apps/AppVersionRequest
                       {:description "Missing name field"
                        :version "1.0.0"}))))

    (testing "invalid version request - missing required field: description"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :version "1.0.0"}))))

    (testing "invalid version request - missing required field: version"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"}))))

    (testing "invalid version request - version_id field should not be present (dissoc'd from AppRequest)"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"}))))

    (testing "invalid version request - versions field should not be present (dissoc'd from App)"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :versions [{:version "2.14.0"
                                    :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"}]}))))

    (testing "invalid version request - wrong type for id field"
      (is (not (valid? apps/AppVersionRequest
                       {:id "not-a-uuid"
                        :name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"}))))

    (testing "invalid version request - wrong type for name field"
      (is (not (valid? apps/AppVersionRequest
                       {:name 12345
                        :description "BLAST tool"
                        :version "2.14.0"}))))

    (testing "invalid version request - wrong type for description field"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description 12345
                        :version "2.14.0"}))))

    (testing "invalid version request - wrong type for version field"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version 2.14}))))

    (testing "invalid version request - wrong type for integration_date field"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :integration_date "2024-01-15"}))))

    (testing "invalid version request - wrong type for edited_date field"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :edited_date "2024-10-20"}))))

    (testing "invalid version request - wrong type for system_id field"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :system_id 123}))))

    (testing "invalid version request - empty system_id string"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :system_id ""}))))

    (testing "invalid version request - wrong type for tools field"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :tools "not-a-vector"}))))

    (testing "invalid version request - invalid tool in tools vector (missing required fields)"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                 :name "blast"}]}))))

    (testing "invalid version request - invalid tool in tools vector (wrong id type)"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :tools [{:id "not-a-uuid"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "docker"}]}))))

    (testing "invalid version request - wrong type for references field"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :references "not-a-vector"}))))

    (testing "invalid version request - invalid reference in references vector"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :references [123]}))))

    (testing "invalid version request - wrong type for groups field"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :groups "not-a-vector"}))))

    (testing "invalid version request - invalid group in groups vector (missing required field: label)"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :groups [{:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                                  :name "input_parameters"}]}))))

    (testing "invalid version request - invalid group in groups vector (wrong label type)"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :groups [{:label 12345}]}))))

    (testing "invalid version request - invalid parameter in group (missing required field: type)"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :groups [{:label "Input Parameters"
                                  :parameters [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                                                :name "--input"}]}]}))))

    (testing "invalid version request - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :extra-field "not-allowed"}))))

    (testing "edge case - empty name string"
      (is (valid? apps/AppVersionRequest
                  {:name ""
                   :description "App with empty name"
                   :version "1.0.0"})))

    (testing "edge case - empty description string"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST"
                   :description ""
                   :version "1.0.0"})))

    (testing "edge case - empty version string"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :version ""})))

    (testing "edge case - system_id with single character"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :version "2.14.0"
                   :system_id "d"})))

    (testing "edge case - version with semantic versioning format"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :version "2.14.0-beta.1+build.123"})))

    (testing "edge case - version with date-based format"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :version "2024.10.28"})))

    (testing "edge case - version with simple number"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :version "1"})))

    (testing "edge case - all optional AppBase fields omitted"
      (is (valid? apps/AppVersionRequest
                  {:name "Minimal App"
                   :description "Only required fields"
                   :version "1.0.0"})))

    (testing "edge case - tools vector with mix of simple and complex tools"
      (is (valid? apps/AppVersionRequest
                  {:name "Mixed App"
                   :description "App with various tool configurations"
                   :version "1.0.0"
                   :tools [{:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                            :name "simple-tool"
                            :version "1.0.0"
                            :type "executable"}
                           {:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                            :name "complex-tool"
                            :description "A complex tool"
                            :attribution "Authors"
                            :location "/usr/bin"
                            :version "2.0.0"
                            :type "docker"
                            :restricted true
                            :time_limit_seconds 3600
                            :interactive false
                            :is_public true
                            :permission "own"
                            :deprecated false}]})))

    (testing "edge case - groups with mix of empty and populated parameter lists"
      (is (valid? apps/AppVersionRequest
                  {:name "Mixed Groups App"
                   :description "App with mixed parameter groups"
                   :version "1.0.0"
                   :groups [{:label "Empty Group"
                             :parameters []}
                            {:label "Populated Group"
                             :parameters [{:type "FileInput"
                                           :name "--input"
                                           :label "Input"}]}]})))

    (testing "edge case - single reference in references vector"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :version "2.14.0"
                   :references ["https://doi.org/10.1093/nar/gkv416"]})))

    (testing "edge case - many references in references vector"
      (is (valid? apps/AppVersionRequest
                  {:name "Well-Documented App"
                   :description "App with many references"
                   :version "1.0.0"
                   :references ["https://doi.org/10.1093/nar/gkv416"
                                "PMID: 25916842"
                                "https://example.org/paper1"
                                "https://example.org/paper2"
                                "ISBN: 978-0-123456-78-9"]})))

    (testing "edge case - deeply nested TreeSelector in parameter"
      (is (valid? apps/AppVersionRequest
                  {:name "TreeSelector App"
                   :description "App with deeply nested TreeSelector"
                   :version "1.0.0"
                   :groups [{:label "Advanced Options"
                             :parameters [{:type "TreeSelector"
                                           :name "--options"
                                           :label "Options"
                                           :arguments
                                           [{:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                                             :groups
                                             [{:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                                               :groups
                                               [{:id #uuid "333a0123-c45d-67e8-f901-234567890abc"
                                                 :arguments
                                                 [{:id #uuid "444a0123-c45d-67e8-f901-234567890abc"}]}]}]}]}]}]})))

    (testing "edge case - parameter with multiple validators"
      (is (valid? apps/AppVersionRequest
                  {:name "Validated App"
                   :description "App with validated parameters"
                   :version "1.0.0"
                   :groups [{:label "Configuration"
                             :parameters [{:type "Integer"
                                           :name "--value"
                                           :label "Value"
                                           :validators [{:type "IntAbove" :params [0]}
                                                        {:type "IntBelow" :params [100]}
                                                        {:type "Required" :params []}
                                                        {:type "IntRange" :params [1 50]}]}]}]})))

    (testing "edge case - integration_date and edited_date with same timestamp"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :version "2.14.0"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-01-15T10:30:00.000-00:00"})))

    (testing "edge case - edited_date before integration_date (no validation constraint)"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :version "2.14.0"
                   :integration_date #inst "2024-10-20T14:45:00.000-00:00"
                   :edited_date #inst "2024-01-15T10:30:00.000-00:00"})))

    (testing "boundary case - very long string values"
      (is (valid? apps/AppVersionRequest
                  {:name (apply str (repeat 1000 "a"))
                   :description (apply str (repeat 1000 "b"))
                   :version (apply str (repeat 1000 "c"))
                   :system_id (apply str (repeat 1000 "d"))})))

    (testing "boundary case - many tools in tools vector"
      (is (valid? apps/AppVersionRequest
                  {:name "Multi-Tool App"
                   :description "App with many tools"
                   :version "1.0.0"
                   :tools (vec (for [i (range 10)]
                                 {:id (java.util.UUID/randomUUID)
                                  :name (str "tool-" i)
                                  :version "1.0.0"
                                  :type "executable"}))})))

    (testing "boundary case - many groups in groups vector"
      (is (valid? apps/AppVersionRequest
                  {:name "Multi-Group App"
                   :description "App with many parameter groups"
                   :version "1.0.0"
                   :groups (vec (for [i (range 10)]
                                  {:label (str "Group " i)
                                   :parameters [{:type "FileInput"
                                                 :name (str "--input-" i)
                                                 :label (str "Input " i)}]}))})))

    (testing "boundary case - many parameters in single group"
      (is (valid? apps/AppVersionRequest
                  {:name "Many-Param App"
                   :description "App with many parameters in one group"
                   :version "1.0.0"
                   :groups [{:label "Parameters"
                             :parameters (vec (for [i (range 20)]
                                                {:type "Integer"
                                                 :name (str "--param-" i)
                                                 :label (str "Parameter " i)}))}]})))

    (testing "real-world example - creating initial version of new app"
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST Analysis"
                   :description "Perform sequence similarity search using BLAST"
                   :version "1.0.0"
                   :system_id "de"
                   :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                            :name "blast"
                            :version "2.14.0"
                            :type "docker"}]
                   :groups [{:label "Input"
                             :parameters [{:type "FileInput"
                                           :name "--input"
                                           :label "Input File"
                                           :required true
                                           :file_parameters {:format "fasta"}}]}]})))

    (testing "real-world example - creating new version for existing app"
      (is (valid? apps/AppVersionRequest
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "BLAST Analysis"
                   :description "Updated BLAST with performance improvements"
                   :version "2.0.0"
                   :system_id "de"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                   :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                            :name "blast"
                            :version "2.15.0"
                            :type "docker"}]
                   :references ["https://doi.org/10.1093/nar/gkv416"]
                   :groups [{:label "Input Parameters"
                             :parameters [{:type "FileInput"
                                           :name "--query"
                                           :label "Query Sequence"
                                           :required true
                                           :file_parameters {:format "fasta"}}]}
                            {:label "Output Options"
                             :parameters [{:type "FileOutput"
                                           :name "--output"
                                           :label "Output File"
                                           :file_parameters {:format "xml"}}]}]})))

    (testing "real-world example - patch version with bug fix"
      (is (valid? apps/AppVersionRequest
                  {:name "Data Processing Tool"
                   :description "Process and analyze experimental data"
                   :version "1.2.3"
                   :system_id "de"
                   :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                            :name "processor"
                            :version "1.2.3"
                            :type "executable"}]
                   :groups [{:label "Configuration"
                             :parameters [{:type "Integer"
                                           :name "--threads"
                                           :label "Threads"
                                           :defaultValue 4}]}]})))

    (testing "real-world example - beta version"
      (is (valid? apps/AppVersionRequest
                  {:name "Experimental Analysis Tool"
                   :description "New experimental features for analysis"
                   :version "2.0.0-beta.1"
                   :system_id "de"
                   :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                            :name "experimental"
                            :version "2.0.0-beta.1"
                            :type "docker"}]
                   :groups [{:label "Parameters"}]})))

    (testing "real-world example - minimal version for simple wrapper"
      (is (valid? apps/AppVersionRequest
                  {:name "Echo"
                   :description "Simple echo command wrapper"
                   :version "1.0"
                   :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                            :name "echo"
                            :version "1.0"
                            :type "executable"}]
                   :groups [{:label "Parameters"
                             :parameters [{:type "Text"
                                           :name "message"
                                           :label "Message"
                                           :required true}]}]})))

    (testing "key transformation - version is required (different from AppRequest)"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"})))
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :version "2.14.0"})))

    (testing "key transformation - version_id is removed (different from AppRequest)"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"})))
      (is (valid? apps/AppVersionRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :version "2.14.0"})))

    (testing "key transformation - versions field is also not allowed"
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :versions [{:version "2.14.0"
                                    :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"}]}))))

    (testing "comparison with AppRequest - version is optional in AppRequest but required here"
      (is (valid? apps/AppRequest
                  {:name "BLAST"
                   :description "BLAST tool"}))
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"}))))

    (testing "comparison with AppRequest - version_id is allowed in AppRequest but not here"
      (is (valid? apps/AppRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"}))
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"}))))

    (testing "comparison with AppRequest - both version and version_id in AppRequest"
      (is (valid? apps/AppRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :version "2.14.0"
                   :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"}))
      (is (not (valid? apps/AppVersionRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version "2.14.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"}))))))
