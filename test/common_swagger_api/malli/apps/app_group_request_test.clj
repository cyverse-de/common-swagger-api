(ns common-swagger-api.malli.apps.app-group-request-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common-swagger-api.malli.apps :as apps]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AppGroupRequest
  (testing "AppGroupRequest validation"
    (testing "valid group request - minimal required fields only"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"})))

    (testing "valid group request - with id field"
      (is (valid? apps/AppGroupRequest
                  {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                   :label "Input Parameters"})))

    (testing "valid group request - without id field (optional)"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :name "input_parameters"})))

    (testing "valid group request - with name field"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :name "input_parameters"})))

    (testing "valid group request - with description field"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :description "Input file and configuration parameters"})))

    (testing "valid group request - with isVisible field"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :isVisible true})))

    (testing "valid group request - with empty parameters vector"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :parameters []})))

    (testing "valid group request - with single parameter (minimal)"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :parameters [{:type "FileInput"}]})))

    (testing "valid group request - with single parameter (with id)"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :parameters [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                                 :type "FileInput"}]})))

    (testing "valid group request - with single parameter (without id)"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :parameters [{:type "FileInput"
                                 :name "--input"
                                 :label "Input File"}]})))

    (testing "valid group request - with multiple parameters"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :parameters [{:type "FileInput"
                                 :name "--input"
                                 :label "Input File"}
                                {:type "Integer"
                                 :name "--threads"
                                 :label "Number of Threads"}
                                {:type "TextSelection"
                                 :name "--mode"
                                 :label "Processing Mode"}]})))

    (testing "valid group request - with complex parameter including validators"
      (is (valid? apps/AppGroupRequest
                  {:label "Configuration"
                   :parameters [{:type "Integer"
                                 :name "--threads"
                                 :label "Number of Threads"
                                 :validators [{:type "IntAbove"
                                               :params [0]}
                                              {:type "IntBelow"
                                               :params [100]}]}]})))

    (testing "valid group request - with parameter containing file_parameters"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :parameters [{:type "FileInput"
                                 :name "--input"
                                 :label "Input File"
                                 :file_parameters {:format "fasta"
                                                   :file_info_type "SequenceAlignment"
                                                   :retain true}}]})))

    (testing "valid group request - with parameter containing arguments"
      (is (valid? apps/AppGroupRequest
                  {:label "Configuration"
                   :parameters [{:type "TextSelection"
                                 :name "--genome-size"
                                 :label "Genome Size"
                                 :arguments [{:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                                              :name "small"
                                              :value "small"
                                              :display "Small (< 2 Mb)"
                                              :isDefault true}
                                             {:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                                              :name "medium"
                                              :value "medium"
                                              :display "Medium (2-100 Mb)"}]}]})))

    (testing "valid group request - with parameter containing TreeSelector arguments"
      (is (valid? apps/AppGroupRequest
                  {:label "Advanced Options"
                   :parameters [{:type "TreeSelector"
                                 :name "--options"
                                 :label "Options"
                                 :arguments [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                                              :isSingleSelect true
                                              :selectionCascade "up"
                                              :arguments [{:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                                                           :name "option1"}]
                                              :groups [{:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                                                        :name "group1"}]}]}]})))

    (testing "valid group request - with all fields populated"
      (is (valid? apps/AppGroupRequest
                  {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                   :name "input_parameters"
                   :description "Input file and configuration parameters"
                   :label "Input Parameters"
                   :isVisible true
                   :parameters [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                                 :name "--input"
                                 :defaultValue "default_value"
                                 :value "example_value"
                                 :label "Input File"
                                 :description "Specify the input file for processing"
                                 :order 1
                                 :required true
                                 :isVisible true
                                 :omit_if_blank false
                                 :type "FileInput"
                                 :file_parameters {:format "fasta"
                                                   :file_info_type "SequenceAlignment"
                                                   :is_implicit false
                                                   :repeat_option_flag false
                                                   :data_source "stdout"
                                                   :retain true}
                                 :validators [{:type "Required"
                                               :params []}]}
                                {:id #uuid "def67890-abc1-2345-6789-0abcdef12345"
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
                                               :params [64]}]}]})))

    (testing "invalid group request - missing required field: label"
      (is (not (valid? apps/AppGroupRequest
                       {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                        :name "input_parameters"}))))

    (testing "invalid group request - wrong type for id field"
      (is (not (valid? apps/AppGroupRequest
                       {:id "not-a-uuid"
                        :label "Input Parameters"}))))

    (testing "invalid group request - wrong type for name field"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :name 12345}))))

    (testing "invalid group request - wrong type for description field"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :description 12345}))))

    (testing "invalid group request - wrong type for label field"
      (is (not (valid? apps/AppGroupRequest
                       {:label 12345}))))

    (testing "invalid group request - wrong type for isVisible field"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :isVisible "not-a-boolean"}))))

    (testing "invalid group request - wrong type for parameters field"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :parameters "not-a-vector"}))))

    (testing "invalid group request - invalid parameter in parameters vector"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :parameters [{:id "not-a-uuid"
                                      :type "FileInput"}]}))))

    (testing "invalid group request - parameter missing required type field"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :parameters [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                                      :name "--input"}]}))))

    (testing "invalid group request - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :extra-field "not-allowed"}))))

    (testing "invalid group request - parameter with extra fields"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :parameters [{:type "FileInput"
                                      :extra-field "not-allowed"}]}))))

    (testing "invalid group request - parameter with wrong type for name"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :parameters [{:type "FileInput"
                                      :name 12345}]}))))

    (testing "invalid group request - parameter with wrong type for required"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :parameters [{:type "FileInput"
                                      :required "not-a-boolean"}]}))))

    (testing "invalid group request - parameter with invalid validators structure"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :parameters [{:type "Integer"
                                      :validators "not-a-vector"}]}))))

    (testing "invalid group request - parameter with invalid validator missing type"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :parameters [{:type "Integer"
                                      :validators [{:params [0]}]}]}))))

    (testing "invalid group request - parameter with invalid file_parameters structure"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :parameters [{:type "FileInput"
                                      :file_parameters "not-a-map"}]}))))

    (testing "invalid group request - parameter with invalid arguments structure"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :parameters [{:type "TextSelection"
                                      :arguments "not-a-vector"}]}))))

    (testing "invalid group request - parameter with invalid argument structure"
      (is (not (valid? apps/AppGroupRequest
                       {:label "Input Parameters"
                        :parameters [{:type "TextSelection"
                                      :arguments [{:id "not-a-uuid"}]}]}))))

    (testing "edge case - label as empty string"
      (is (valid? apps/AppGroupRequest
                  {:label ""})))

    (testing "edge case - name as empty string"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :name ""})))

    (testing "edge case - description as empty string"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :description ""})))

    (testing "edge case - isVisible set to false"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :isVisible false})))

    (testing "edge case - multiple parameters with mix of with and without ids"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :parameters [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                                 :type "FileInput"}
                                {:type "Integer"
                                 :name "--threads"}
                                {:id #uuid "def67890-abc1-2345-6789-0abcdef12345"
                                 :type "TextSelection"}]})))

    (testing "edge case - parameter with empty file_parameters map"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :parameters [{:type "FileInput"
                                 :file_parameters {}}]})))

    (testing "edge case - parameter with empty arguments vector"
      (is (valid? apps/AppGroupRequest
                  {:label "Configuration"
                   :parameters [{:type "TextSelection"
                                 :arguments []}]})))

    (testing "edge case - parameter with empty validators vector"
      (is (valid? apps/AppGroupRequest
                  {:label "Configuration"
                   :parameters [{:type "Integer"
                                 :validators []}]})))

    (testing "edge case - parameter with negative order value"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :parameters [{:type "FileInput"
                                 :order -1}]})))

    (testing "edge case - parameter with zero order value"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :parameters [{:type "FileInput"
                                 :order 0}]})))

    (testing "edge case - parameter with defaultValue as nil"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :parameters [{:type "FileInput"
                                 :defaultValue nil}]})))

    (testing "edge case - parameter with value as complex type"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :parameters [{:type "FileInput"
                                 :value {:complex "object"}}]})))

    (testing "edge case - parameter with deeply nested TreeSelector groups"
      (is (valid? apps/AppGroupRequest
                  {:label "Advanced Options"
                   :parameters [{:type "TreeSelector"
                                 :arguments [{:groups [{:groups [{:groups [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"}]}]}]}]}]})))

    (testing "boundary case - very long string values"
      (is (valid? apps/AppGroupRequest
                  {:label (apply str (repeat 1000 "a"))
                   :name (apply str (repeat 1000 "b"))
                   :description (apply str (repeat 1000 "c"))})))

    (testing "boundary case - multiple parameters in one group"
      (is (valid? apps/AppGroupRequest
                  {:label "Input Parameters"
                   :parameters [{:type "FileInput" :name "--input1"}
                                {:type "FileInput" :name "--input2"}
                                {:type "Integer" :name "--threads"}
                                {:type "TextSelection" :name "--mode"}
                                {:type "FileOutput" :name "--output"}]})))

    (testing "boundary case - parameter with multiple argument items"
      (is (valid? apps/AppGroupRequest
                  {:label "Configuration"
                   :parameters [{:type "TextSelection"
                                 :arguments [{:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                                              :name "option1"}
                                             {:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                                              :name "option2"}
                                             {:id #uuid "333a0123-c45d-67e8-f901-234567890abc"
                                              :name "option3"}
                                             {:id #uuid "444a0123-c45d-67e8-f901-234567890abc"
                                              :name "option4"}]}]})))

    (testing "boundary case - parameter with multiple validators"
      (is (valid? apps/AppGroupRequest
                  {:label "Configuration"
                   :parameters [{:type "Integer"
                                 :validators [{:type "IntAbove" :params [0]}
                                              {:type "IntBelow" :params [100]}
                                              {:type "Required" :params []}
                                              {:type "IntRange" :params [1 50]}]}]})))

    (testing "real-world example - input file group"
      (is (valid? apps/AppGroupRequest
                  {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                   :name "input_group"
                   :label "Input Files"
                   :description "Specify input files for the analysis"
                   :isVisible true
                   :parameters [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                                 :name "--query"
                                 :label "Query Sequence"
                                 :description "The sequence to search with"
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
                                 :description "The database to search against"
                                 :type "FileInput"
                                 :required true
                                 :isVisible true
                                 :order 2
                                 :file_parameters {:format "fasta"
                                                   :file_info_type "SequenceAlignment"
                                                   :retain false}}]})))

    (testing "real-world example - configuration group"
      (is (valid? apps/AppGroupRequest
                  {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                   :name "config_group"
                   :label "Configuration"
                   :description "Configure analysis parameters"
                   :isVisible true
                   :parameters [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                                 :name "--evalue"
                                 :label "E-value Threshold"
                                 :description "Statistical significance threshold"
                                 :type "Number"
                                 :defaultValue 0.001
                                 :required false
                                 :isVisible true
                                 :order 1
                                 :validators [{:type "NumberAbove"
                                               :params [0]}]}
                                {:id #uuid "def67890-abc1-2345-6789-0abcdef12345"
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
                                               :params [64]}]}
                                {:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                                 :name "--output-format"
                                 :label "Output Format"
                                 :description "Select the output format"
                                 :type "TextSelection"
                                 :required false
                                 :isVisible true
                                 :order 3
                                 :arguments [{:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                                              :name "xml"
                                              :value "5"
                                              :display "XML"
                                              :description "XML format"
                                              :isDefault false}
                                             {:id #uuid "333a0123-c45d-67e8-f901-234567890abc"
                                              :name "tabular"
                                              :value "6"
                                              :display "Tabular"
                                              :description "Tab-delimited format"
                                              :isDefault true}
                                             {:id #uuid "444a0123-c45d-67e8-f901-234567890abc"
                                              :name "json"
                                              :value "15"
                                              :display "JSON"
                                              :description "JSON format"
                                              :isDefault false}]}]})))

    (testing "real-world example - output group"
      (is (valid? apps/AppGroupRequest
                  {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                   :name "output_group"
                   :label "Output Files"
                   :description "Specify output file names and formats"
                   :isVisible true
                   :parameters [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                                 :name "--output"
                                 :label "Output File"
                                 :description "The output alignment file"
                                 :type "FileOutput"
                                 :required false
                                 :isVisible true
                                 :order 1
                                 :file_parameters {:format "xml"
                                                   :file_info_type "BlastOutput"
                                                   :is_implicit false
                                                   :data_source "stdout"}}]})))

    (testing "real-world example - advanced options group with TreeSelector"
      (is (valid? apps/AppGroupRequest
                  {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                   :name "advanced_group"
                   :label "Advanced Options"
                   :description "Advanced configuration options"
                   :isVisible false
                   :parameters [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                                 :name "--advanced"
                                 :label "Advanced Settings"
                                 :description "Select advanced settings"
                                 :type "TreeSelector"
                                 :required false
                                 :isVisible false
                                 :order 1
                                 :arguments [{:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                                              :name "root"
                                              :isSingleSelect false
                                              :selectionCascade "up"
                                              :arguments [{:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                                                           :name "option1"
                                                           :value "opt1"
                                                           :display "Option 1"}
                                                          {:id #uuid "333a0123-c45d-67e8-f901-234567890abc"
                                                           :name "option2"
                                                           :value "opt2"
                                                           :display "Option 2"}]
                                              :groups [{:id #uuid "444a0123-c45d-67e8-f901-234567890abc"
                                                        :name "sub_group"
                                                        :display "Sub Group"
                                                        :arguments [{:id #uuid "555a0123-c45d-67e8-f901-234567890abc"
                                                                     :name "sub_option1"
                                                                     :value "sub_opt1"
                                                                     :display "Sub Option 1"}]}]}]}]})))

    (testing "real-world example - minimal group for simple app"
      (is (valid? apps/AppGroupRequest
                  {:label "Parameters"
                   :parameters [{:type "FileInput"
                                 :name "--input"
                                 :label "Input File"
                                 :required true}
                                {:type "FileOutput"
                                 :name "--output"
                                 :label "Output File"}]})))

    (testing "real-world example - empty group with no parameters"
      (is (valid? apps/AppGroupRequest
                  {:label "Optional Parameters"
                   :description "Optional configuration parameters"
                   :isVisible false
                   :parameters []})))))
