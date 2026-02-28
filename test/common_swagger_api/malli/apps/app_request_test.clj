(ns common-swagger-api.malli.apps.app-request-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common-swagger-api.malli.apps :as apps]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AppRequest
  (testing "AppRequest validation"
    (testing "valid app request - minimal required fields from AppBase"
      (is (valid? apps/AppRequest
                  {:name "BLAST"
                   :description "Basic Local Alignment Search Tool"})))

    (testing "valid app request - with id field (optional in AppRequest)"
      (is (valid? apps/AppRequest
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool"})))

    (testing "valid app request - without id field (made optional)"
      (is (valid? apps/AppRequest
                  {:name "Trinity"
                   :description "De novo transcript assembly"})))

    (testing "valid app request - with all AppBase fields"
      (is (valid? apps/AppRequest
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                   :system_id "de"})))

    (testing "valid app request - with AppVersionDetails fields"
      (is (valid? apps/AppRequest
                  {:name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :version "2.14.0"
                   :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"})))

    (testing "valid app request - with version field only"
      (is (valid? apps/AppRequest
                  {:name "Trinity"
                   :description "De novo transcript assembly"
                   :version "2.13.2"})))

    (testing "valid app request - with version_id field only"
      (is (valid? apps/AppRequest
                  {:name "Bowtie2"
                   :description "Fast alignment tool"
                   :version_id #uuid "789a0123-c45d-67e8-f901-234567890abc"})))

    (testing "valid app request - with empty tools vector"
      (is (valid? apps/AppRequest
                  {:name "Simple App"
                   :description "An app with no tools"
                   :tools []})))

    (testing "valid app request - with single tool (minimal)"
      (is (valid? apps/AppRequest
                  {:name "BLAST App"
                   :description "BLAST analysis"
                   :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                            :name "blast"
                            :version "2.14.0"
                            :type "docker"}]})))

    (testing "valid app request - with single tool (complete)"
      (is (valid? apps/AppRequest
                  {:name "SAMtools App"
                   :description "Sequence alignment processing"
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

    (testing "valid app request - with multiple tools"
      (is (valid? apps/AppRequest
                  {:name "Pipeline App"
                   :description "Multi-tool pipeline"
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

    (testing "valid app request - with references field"
      (is (valid? apps/AppRequest
                  {:name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :references ["https://doi.org/10.1093/nar/gkv416" "PMID: 25916842"]})))

    (testing "valid app request - with empty references vector"
      (is (valid? apps/AppRequest
                  {:name "Simple App"
                   :description "App with no references"
                   :references []})))

    (testing "valid app request - with empty groups vector"
      (is (valid? apps/AppRequest
                  {:name "Minimal App"
                   :description "App with no parameter groups"
                   :groups []})))

    (testing "valid app request - with single group (minimal)"
      (is (valid? apps/AppRequest
                  {:name "Simple App"
                   :description "App with one parameter group"
                   :groups [{:label "Input Parameters"}]})))

    (testing "valid app request - with single group (with parameters)"
      (is (valid? apps/AppRequest
                  {:name "BLAST App"
                   :description "BLAST analysis"
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

    (testing "valid app request - with multiple groups"
      (is (valid? apps/AppRequest
                  {:name "Complex App"
                   :description "App with multiple parameter groups"
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

    (testing "valid app request - complete single-step app"
      (is (valid? apps/AppRequest
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "BLAST Analysis"
                   :description "Perform sequence similarity search using BLAST"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                   :system_id "de"
                   :version "2.14.0"
                   :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
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

    (testing "invalid app request - missing required field: name"
      (is (not (valid? apps/AppRequest
                       {:description "Missing name field"}))))

    (testing "invalid app request - missing required field: description"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"}))))

    (testing "invalid app request - wrong type for id field"
      (is (not (valid? apps/AppRequest
                       {:id "not-a-uuid"
                        :name "BLAST"
                        :description "BLAST tool"}))))

    (testing "invalid app request - wrong type for name field"
      (is (not (valid? apps/AppRequest
                       {:name 12345
                        :description "BLAST tool"}))))

    (testing "invalid app request - wrong type for description field"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description 12345}))))

    (testing "invalid app request - wrong type for integration_date field"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :integration_date "2024-01-15"}))))

    (testing "invalid app request - wrong type for edited_date field"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :edited_date "2024-10-20"}))))

    (testing "invalid app request - wrong type for system_id field"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :system_id 123}))))

    (testing "invalid app request - empty system_id string"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :system_id ""}))))

    (testing "invalid app request - wrong type for version field"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version 2.14}))))

    (testing "invalid app request - wrong type for version_id field"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :version_id "not-a-uuid"}))))

    (testing "invalid app request - wrong type for tools field"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :tools "not-a-vector"}))))

    (testing "invalid app request - invalid tool in tools vector (missing required fields)"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                 :name "blast"}]}))))

    (testing "invalid app request - invalid tool in tools vector (wrong id type)"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :tools [{:id "not-a-uuid"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "docker"}]}))))

    (testing "invalid app request - wrong type for references field"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :references "not-a-vector"}))))

    (testing "invalid app request - invalid reference in references vector"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :references [123]}))))

    (testing "invalid app request - wrong type for groups field"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :groups "not-a-vector"}))))

    (testing "invalid app request - invalid group in groups vector (missing required field: label)"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :groups [{:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                                  :name "input_parameters"}]}))))

    (testing "invalid app request - invalid group in groups vector (wrong label type)"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :groups [{:label 12345}]}))))

    (testing "invalid app request - invalid parameter in group (missing required field: type)"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :groups [{:label "Input Parameters"
                                  :parameters [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                                                :name "--input"}]}]}))))

    (testing "invalid app request - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :extra-field "not-allowed"}))))

    (testing "invalid app request - versions field should not be present (dissoc'd from App)"
      (is (not (valid? apps/AppRequest
                       {:name "BLAST"
                        :description "BLAST tool"
                        :versions [{:version "2.14.0"
                                    :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"}]}))))

    (testing "edge case - empty name string"
      (is (valid? apps/AppRequest
                  {:name ""
                   :description "App with empty name"})))

    (testing "edge case - empty description string"
      (is (valid? apps/AppRequest
                  {:name "BLAST"
                   :description ""})))

    (testing "edge case - empty version string"
      (is (valid? apps/AppRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :version ""})))

    (testing "edge case - system_id with single character"
      (is (valid? apps/AppRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :system_id "d"})))

    (testing "edge case - all optional fields omitted"
      (is (valid? apps/AppRequest
                  {:name "Minimal App"
                   :description "Only required fields"})))

    (testing "edge case - tools vector with mix of simple and complex tools"
      (is (valid? apps/AppRequest
                  {:name "Mixed App"
                   :description "App with various tool configurations"
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
      (is (valid? apps/AppRequest
                  {:name "Mixed Groups App"
                   :description "App with mixed parameter groups"
                   :groups [{:label "Empty Group"
                             :parameters []}
                            {:label "Populated Group"
                             :parameters [{:type "FileInput"
                                           :name "--input"
                                           :label "Input"}]}]})))

    (testing "edge case - single reference in references vector"
      (is (valid? apps/AppRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :references ["https://doi.org/10.1093/nar/gkv416"]})))

    (testing "edge case - many references in references vector"
      (is (valid? apps/AppRequest
                  {:name "Well-Documented App"
                   :description "App with many references"
                   :references ["https://doi.org/10.1093/nar/gkv416"
                                "PMID: 25916842"
                                "https://example.org/paper1"
                                "https://example.org/paper2"
                                "ISBN: 978-0-123456-78-9"]})))

    (testing "edge case - deeply nested TreeSelector in parameter"
      (is (valid? apps/AppRequest
                  {:name "TreeSelector App"
                   :description "App with deeply nested TreeSelector"
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
      (is (valid? apps/AppRequest
                  {:name "Validated App"
                   :description "App with validated parameters"
                   :groups [{:label "Configuration"
                             :parameters [{:type "Integer"
                                           :name "--value"
                                           :label "Value"
                                           :validators [{:type "IntAbove" :params [0]}
                                                        {:type "IntBelow" :params [100]}
                                                        {:type "Required" :params []}
                                                        {:type "IntRange" :params [1 50]}]}]}]})))

    (testing "edge case - integration_date and edited_date with same timestamp"
      (is (valid? apps/AppRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-01-15T10:30:00.000-00:00"})))

    (testing "edge case - edited_date before integration_date (no validation constraint)"
      (is (valid? apps/AppRequest
                  {:name "BLAST"
                   :description "BLAST tool"
                   :integration_date #inst "2024-10-20T14:45:00.000-00:00"
                   :edited_date #inst "2024-01-15T10:30:00.000-00:00"})))

    (testing "boundary case - very long string values"
      (is (valid? apps/AppRequest
                  {:name (apply str (repeat 1000 "a"))
                   :description (apply str (repeat 1000 "b"))
                   :system_id (apply str (repeat 1000 "c"))
                   :version (apply str (repeat 1000 "d"))})))

    (testing "boundary case - many tools in tools vector"
      (is (valid? apps/AppRequest
                  {:name "Multi-Tool App"
                   :description "App with many tools"
                   :tools (vec (for [i (range 10)]
                                 {:id (java.util.UUID/randomUUID)
                                  :name (str "tool-" i)
                                  :version "1.0.0"
                                  :type "executable"}))})))

    (testing "boundary case - many groups in groups vector"
      (is (valid? apps/AppRequest
                  {:name "Multi-Group App"
                   :description "App with many parameter groups"
                   :groups (vec (for [i (range 10)]
                                  {:label (str "Group " i)
                                   :parameters [{:type "FileInput"
                                                 :name (str "--input-" i)
                                                 :label (str "Input " i)}]}))})))

    (testing "boundary case - many parameters in single group"
      (is (valid? apps/AppRequest
                  {:name "Many-Param App"
                   :description "App with many parameters in one group"
                   :groups [{:label "Parameters"
                             :parameters (vec (for [i (range 20)]
                                                {:type "Integer"
                                                 :name (str "--param-" i)
                                                 :label (str "Parameter " i)}))}]})))

    (testing "real-world example - basic file processing app"
      (is (valid? apps/AppRequest
                  {:name "File Converter"
                   :description "Convert files from one format to another"
                   :system_id "de"
                   :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                            :name "converter"
                            :version "1.0.0"
                            :type "executable"}]
                   :groups [{:label "Input"
                             :parameters [{:type "FileInput"
                                           :name "--input"
                                           :label "Input File"
                                           :required true
                                           :file_parameters {:format "txt"}}]}
                            {:label "Output"
                             :parameters [{:type "FileOutput"
                                           :name "--output"
                                           :label "Output File"
                                           :file_parameters {:format "csv"
                                                             :data_source "stdout"}}]}]})))

    (testing "real-world example - bioinformatics pipeline app"
      (is (valid? apps/AppRequest
                  {:name "RNA-Seq Analysis Pipeline"
                   :description "Complete RNA-Seq analysis from raw reads to differential expression"
                   :system_id "de"
                   :version "1.0.0"
                   :references ["https://doi.org/10.1038/nprot.2016.095"]
                   :tools [{:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                            :name "trimmomatic"
                            :description "Read trimming tool"
                            :version "0.39"
                            :type "executable"}
                           {:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                            :name "hisat2"
                            :description "Alignment tool"
                            :version "2.2.1"
                            :type "executable"}
                           {:id #uuid "333a0123-c45d-67e8-f901-234567890abc"
                            :name "featurecounts"
                            :description "Read counting tool"
                            :version "2.0.1"
                            :type "executable"}]
                   :groups [{:label "Input Files"
                             :parameters [{:type "FileInput"
                                           :name "--reads"
                                           :label "RNA-Seq Reads"
                                           :description "Raw sequencing reads in FASTQ format"
                                           :required true
                                           :file_parameters {:format "fastq"
                                                             :file_info_type "SequenceReads"}}
                                          {:type "FileInput"
                                           :name "--genome"
                                           :label "Reference Genome"
                                           :description "Indexed reference genome"
                                           :required true
                                           :file_parameters {:format "fasta"
                                                             :file_info_type "ReferenceGenome"}}]}
                            {:label "Quality Control"
                             :parameters [{:type "Integer"
                                           :name "--min-quality"
                                           :label "Minimum Quality Score"
                                           :description "Minimum phred quality score"
                                           :defaultValue 20
                                           :validators [{:type "IntAbove" :params [0]}
                                                        {:type "IntBelow" :params [41]}]}]}
                            {:label "Alignment Options"
                             :parameters [{:type "Integer"
                                           :name "--threads"
                                           :label "CPU Threads"
                                           :defaultValue 8
                                           :validators [{:type "IntAbove" :params [0]}
                                                        {:type "IntBelow" :params [64]}]}
                                          {:type "TextSelection"
                                           :name "--strand"
                                           :label "Strand Specificity"
                                           :arguments [{:id #uuid "444a0123-c45d-67e8-f901-234567890abc"
                                                        :name "unstranded"
                                                        :value "0"
                                                        :display "Unstranded"
                                                        :isDefault true}
                                                       {:id #uuid "555a0123-c45d-67e8-f901-234567890abc"
                                                        :name "forward"
                                                        :value "1"
                                                        :display "Forward Strand"}
                                                       {:id #uuid "666a0123-c45d-67e8-f901-234567890abc"
                                                        :name "reverse"
                                                        :value "2"
                                                        :display "Reverse Strand"}]}]}
                            {:label "Output Files"
                             :parameters [{:type "FileOutput"
                                           :name "--counts"
                                           :label "Gene Counts"
                                           :description "Read counts per gene"
                                           :file_parameters {:format "txt"
                                                             :file_info_type "CountMatrix"
                                                             :data_source "file"}}]}]})))

    (testing "real-world example - simple command-line tool wrapper"
      (is (valid? apps/AppRequest
                  {:name "Echo"
                   :description "Simple echo command wrapper"
                   :system_id "de"
                   :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                            :name "echo"
                            :version "1.0"
                            :type "executable"}]
                   :groups [{:label "Parameters"
                             :parameters [{:type "Text"
                                           :name "message"
                                           :label "Message"
                                           :description "Message to echo"
                                           :required true}]}]})))

    (testing "real-world example - docker-based interactive app"
      (is (valid? apps/AppRequest
                  {:name "RStudio Server"
                   :description "Interactive RStudio environment"
                   :system_id "de"
                   :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                            :name "rstudio"
                            :version "4.2.0"
                            :type "docker"
                            :interactive true
                            :time_limit_seconds 28800
                            :container {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                                        :memory_limit 8589934592
                                        :min_memory_limit 4294967296
                                        :min_cpu_cores 2.0
                                        :max_cpu_cores 8.0
                                        :network_mode "bridge"
                                        :working_directory "/workspace"
                                        :image {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                                                :name "rocker/rstudio"
                                                :tag "4.2.0"}}}]
                   :groups [{:label "Configuration"
                             :parameters [{:type "Integer"
                                           :name "--memory"
                                           :label "Memory (GB)"
                                           :description "Memory allocation"
                                           :defaultValue 8
                                           :validators [{:type "IntAbove" :params [0]}]}]}]})))

    (testing "real-world example - minimal app for new integration"
      (is (valid? apps/AppRequest
                  {:name "New Tool"
                   :description "A newly integrated tool"
                   :tools [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                            :name "mytool"
                            :version "1.0.0"
                            :type "executable"}]
                   :groups [{:label "Parameters"}]})))))
