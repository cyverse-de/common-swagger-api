(ns common-swagger-api.malli.apps.app-parameter-request-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common-swagger-api.malli.apps :as apps]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AppParameterRequest
  (testing "AppParameterRequest validation"
    (testing "valid parameter request - minimal required fields only"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"})))

    (testing "valid parameter request - with id field"
      (is (valid? apps/AppParameterRequest
                  {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                   :type "FileInput"})))

    (testing "valid parameter request - without id field (optional)"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :name "--input"
                   :label "Input File"})))

    (testing "valid parameter request - with name field"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :name "--input"})))

    (testing "valid parameter request - with defaultValue field"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :defaultValue "default_value"})))

    (testing "valid parameter request - with value field"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :value "example_value"})))

    (testing "valid parameter request - with label field"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :label "Input File"})))

    (testing "valid parameter request - with description field"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :description "Specify the input file for processing"})))

    (testing "valid parameter request - with order field"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :order 1})))

    (testing "valid parameter request - with required field"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :required true})))

    (testing "valid parameter request - with isVisible field"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :isVisible true})))

    (testing "valid parameter request - with omit_if_blank field"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :omit_if_blank false})))

    (testing "valid parameter request - with file_parameters field"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :file_parameters {:format "fasta"
                                     :file_info_type "SequenceAlignment"
                                     :is_implicit false
                                     :repeat_option_flag false
                                     :data_source "stdout"
                                     :retain true}})))

    (testing "valid parameter request - with file_parameters partial fields"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :file_parameters {:format "fasta"}})))

    (testing "valid parameter request - with arguments field"
      (is (valid? apps/AppParameterRequest
                  {:type "TextSelection"
                   :arguments [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                                :name "small_genome"
                                :value "1"
                                :description "Small genome (< 2 Mb)"
                                :display "Small Genome"
                                :isDefault false}]})))

    (testing "valid parameter request - with arguments field without id (optional)"
      (is (valid? apps/AppParameterRequest
                  {:type "TextSelection"
                   :arguments [{:name "small_genome"
                                :value "1"
                                :description "Small genome (< 2 Mb)"
                                :display "Small Genome"
                                :isDefault false}]})))

    (testing "valid parameter request - with arguments field minimal"
      (is (valid? apps/AppParameterRequest
                  {:type "TextSelection"
                   :arguments [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"}]})))

    (testing "valid parameter request - with TreeSelector arguments"
      (is (valid? apps/AppParameterRequest
                  {:type "TreeSelector"
                   :arguments [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                                :name "root"
                                :isSingleSelect true
                                :selectionCascade "up"
                                :arguments [{:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                                             :name "child1"
                                             :value "val1"}]
                                :groups [{:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                                          :name "genome_size_group"
                                          :value "size_group"
                                          :description "Genome size selection group"
                                          :display "Genome Size"
                                          :isDefault false}]}]})))

    (testing "valid parameter request - with nested TreeSelector groups"
      (is (valid? apps/AppParameterRequest
                  {:type "TreeSelector"
                   :arguments [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                                :groups [{:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                                          :arguments [{:name "arg1"}]
                                          :groups [{:id #uuid "333a0123-c45d-67e8-f901-234567890abc"
                                                    :arguments [{:name "nested_arg"}]}]}]}]})))

    (testing "valid parameter request - with validators field"
      (is (valid? apps/AppParameterRequest
                  {:type "Integer"
                   :validators [{:type "IntAbove"
                                 :params [0]}]})))

    (testing "valid parameter request - with multiple validators"
      (is (valid? apps/AppParameterRequest
                  {:type "Integer"
                   :validators [{:type "IntAbove"
                                 :params [0]}
                                {:type "IntBelow"
                                 :params [100]}]})))

    (testing "valid parameter request - with all fields populated"
      (is (valid? apps/AppParameterRequest
                  {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
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
                   :arguments [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                                :name "small_genome"
                                :value "1"
                                :description "Small genome (< 2 Mb)"
                                :display "Small Genome"
                                :isDefault false}]
                   :validators [{:type "IntAbove"
                                 :params [0]}]})))

    (testing "invalid parameter request - missing required field: type"
      (is (not (valid? apps/AppParameterRequest
                       {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                        :name "--input"}))))

    (testing "invalid parameter request - wrong type for id field"
      (is (not (valid? apps/AppParameterRequest
                       {:id "not-a-uuid"
                        :type "FileInput"}))))

    (testing "invalid parameter request - wrong type for name field"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :name 12345}))))

    (testing "invalid parameter request - wrong type for defaultValue field (can be any)"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :defaultValue nil})))

    (testing "invalid parameter request - wrong type for value field (can be any)"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :value nil})))

    (testing "invalid parameter request - wrong type for label field"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :label 12345}))))

    (testing "invalid parameter request - wrong type for description field"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :description 12345}))))

    (testing "invalid parameter request - wrong type for order field"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :order "not-an-int"}))))

    (testing "invalid parameter request - wrong type for required field"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :required "not-a-boolean"}))))

    (testing "invalid parameter request - wrong type for isVisible field"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :isVisible "not-a-boolean"}))))

    (testing "invalid parameter request - wrong type for omit_if_blank field"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :omit_if_blank "not-a-boolean"}))))

    (testing "invalid parameter request - wrong type for type field"
      (is (not (valid? apps/AppParameterRequest
                       {:type 12345}))))

    (testing "invalid parameter request - wrong type for file_parameters field"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :file_parameters "not-a-map"}))))

    (testing "invalid parameter request - invalid file_parameters structure"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :file_parameters {:format 12345}}))))

    (testing "invalid parameter request - wrong type for arguments field"
      (is (not (valid? apps/AppParameterRequest
                       {:type "TextSelection"
                        :arguments "not-a-vector"}))))

    (testing "invalid parameter request - invalid argument in arguments vector"
      (is (not (valid? apps/AppParameterRequest
                       {:type "TextSelection"
                        :arguments [{:id "not-a-uuid"}]}))))

    (testing "invalid parameter request - wrong type for validators field"
      (is (not (valid? apps/AppParameterRequest
                       {:type "Integer"
                        :validators "not-a-vector"}))))

    (testing "invalid parameter request - invalid validator in validators vector"
      (is (not (valid? apps/AppParameterRequest
                       {:type "Integer"
                        :validators [{:type 12345}]}))))

    (testing "invalid parameter request - missing type in validator"
      (is (not (valid? apps/AppParameterRequest
                       {:type "Integer"
                        :validators [{:params [0]}]}))))

    (testing "invalid parameter request - missing params in validator"
      (is (not (valid? apps/AppParameterRequest
                       {:type "Integer"
                        :validators [{:type "IntAbove"}]}))))

    (testing "invalid parameter request - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :extra-field "not-allowed"}))))

    (testing "invalid parameter request - wrong type for file_parameters.format"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :file_parameters {:format 12345}}))))

    (testing "invalid parameter request - wrong type for file_parameters.file_info_type"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :file_parameters {:file_info_type 12345}}))))

    (testing "invalid parameter request - wrong type for file_parameters.is_implicit"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :file_parameters {:is_implicit "not-a-boolean"}}))))

    (testing "invalid parameter request - wrong type for file_parameters.repeat_option_flag"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :file_parameters {:repeat_option_flag "not-a-boolean"}}))))

    (testing "invalid parameter request - wrong type for file_parameters.data_source"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :file_parameters {:data_source 12345}}))))

    (testing "invalid parameter request - wrong type for file_parameters.retain"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :file_parameters {:retain "not-a-boolean"}}))))

    (testing "invalid parameter request - extra fields in file_parameters not allowed"
      (is (not (valid? apps/AppParameterRequest
                       {:type "FileInput"
                        :file_parameters {:format "fasta"
                                          :extra-field "not-allowed"}}))))

    (testing "invalid parameter request - wrong type for arguments item name"
      (is (not (valid? apps/AppParameterRequest
                       {:type "TextSelection"
                        :arguments [{:name 12345}]}))))

    (testing "invalid parameter request - wrong type for arguments item value"
      (is (not (valid? apps/AppParameterRequest
                       {:type "TextSelection"
                        :arguments [{:value 12345}]}))))

    (testing "invalid parameter request - wrong type for arguments item description"
      (is (not (valid? apps/AppParameterRequest
                       {:type "TextSelection"
                        :arguments [{:description 12345}]}))))

    (testing "invalid parameter request - wrong type for arguments item display"
      (is (not (valid? apps/AppParameterRequest
                       {:type "TextSelection"
                        :arguments [{:display 12345}]}))))

    (testing "invalid parameter request - wrong type for arguments item isDefault"
      (is (not (valid? apps/AppParameterRequest
                       {:type "TextSelection"
                        :arguments [{:isDefault "not-a-boolean"}]}))))

    (testing "invalid parameter request - wrong type for TreeSelector isSingleSelect"
      (is (not (valid? apps/AppParameterRequest
                       {:type "TreeSelector"
                        :arguments [{:isSingleSelect "not-a-boolean"}]}))))

    (testing "invalid parameter request - wrong type for TreeSelector selectionCascade"
      (is (not (valid? apps/AppParameterRequest
                       {:type "TreeSelector"
                        :arguments [{:selectionCascade 12345}]}))))

    (testing "invalid parameter request - wrong type for TreeSelector nested arguments"
      (is (not (valid? apps/AppParameterRequest
                       {:type "TreeSelector"
                        :arguments [{:arguments "not-a-vector"}]}))))

    (testing "invalid parameter request - wrong type for TreeSelector nested groups"
      (is (not (valid? apps/AppParameterRequest
                       {:type "TreeSelector"
                        :arguments [{:groups "not-a-vector"}]}))))

    (testing "invalid parameter request - extra fields in arguments not allowed"
      (is (not (valid? apps/AppParameterRequest
                       {:type "TextSelection"
                        :arguments [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                                     :extra-field "not-allowed"}]}))))

    (testing "edge case - defaultValue as complex type"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :defaultValue {:complex "object"}})))

    (testing "edge case - value as complex type"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :value ["array" "of" "values"]})))

    (testing "edge case - empty validators vector"
      (is (valid? apps/AppParameterRequest
                  {:type "Integer"
                   :validators []})))

    (testing "edge case - empty arguments vector"
      (is (valid? apps/AppParameterRequest
                  {:type "TextSelection"
                   :arguments []})))

    (testing "edge case - validator params as empty vector"
      (is (valid? apps/AppParameterRequest
                  {:type "Integer"
                   :validators [{:type "IntAbove"
                                 :params []}]})))

    (testing "edge case - validator params with mixed types"
      (is (valid? apps/AppParameterRequest
                  {:type "Integer"
                   :validators [{:type "IntAbove"
                                 :params [0 "string" true {:key "value"}]}]})))

    (testing "edge case - order with negative value"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :order -1})))

    (testing "edge case - order with zero"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :order 0})))

    (testing "edge case - deeply nested TreeSelector groups"
      (is (valid? apps/AppParameterRequest
                  {:type "TreeSelector"
                   :arguments [{:groups [{:groups [{:groups [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"}]}]}]}]})))

    (testing "edge case - TreeSelector with both arguments and groups"
      (is (valid? apps/AppParameterRequest
                  {:type "TreeSelector"
                   :arguments [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                                :arguments [{:id #uuid "111a0123-c45d-67e8-f901-234567890abc"}]
                                :groups [{:id #uuid "222a0123-c45d-67e8-f901-234567890abc"}]}]})))

    (testing "edge case - file_parameters with all optional fields omitted"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :file_parameters {}})))

    (testing "boundary case - very long string values"
      (is (valid? apps/AppParameterRequest
                  {:type "FileInput"
                   :name (apply str (repeat 1000 "a"))
                   :label (apply str (repeat 1000 "b"))
                   :description (apply str (repeat 1000 "c"))})))

    (testing "boundary case - multiple arguments items"
      (is (valid? apps/AppParameterRequest
                  {:type "TextSelection"
                   :arguments [{:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                                :name "option1"}
                               {:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                                :name "option2"}
                               {:id #uuid "333a0123-c45d-67e8-f901-234567890abc"
                                :name "option3"}]})))

    (testing "boundary case - multiple validators"
      (is (valid? apps/AppParameterRequest
                  {:type "Integer"
                   :validators [{:type "IntAbove" :params [0]}
                                {:type "IntBelow" :params [100]}
                                {:type "Required" :params []}]})))

    (testing "real-world example - complete FileInput parameter"
      (is (valid? apps/AppParameterRequest
                  {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                   :name "--input-file"
                   :label "Input FASTA File"
                   :description "The input sequence file in FASTA format"
                   :type "FileInput"
                   :required true
                   :isVisible true
                   :order 1
                   :file_parameters {:format "fasta"
                                     :file_info_type "SequenceAlignment"
                                     :retain true}})))

    (testing "real-world example - complete TextSelection parameter"
      (is (valid? apps/AppParameterRequest
                  {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                   :name "--genome-size"
                   :label "Genome Size"
                   :description "Select the approximate genome size for optimization"
                   :type "TextSelection"
                   :required false
                   :isVisible true
                   :order 2
                   :arguments [{:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                                :name "small"
                                :value "small"
                                :display "Small (< 2 Mb)"
                                :description "For bacterial genomes"
                                :isDefault true}
                               {:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                                :name "medium"
                                :value "medium"
                                :display "Medium (2-100 Mb)"
                                :description "For fungal genomes"}
                               {:id #uuid "333a0123-c45d-67e8-f901-234567890abc"
                                :name "large"
                                :value "large"
                                :display "Large (> 100 Mb)"
                                :description "For plant/animal genomes"}]})))

    (testing "real-world example - Integer parameter with validation"
      (is (valid? apps/AppParameterRequest
                  {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                   :name "--threads"
                   :label "Number of Threads"
                   :description "Number of CPU threads to use"
                   :type "Integer"
                   :defaultValue 4
                   :required false
                   :isVisible true
                   :order 3
                   :validators [{:type "IntAbove" :params [0]}
                                {:type "IntBelow" :params [64]}]})))

    (testing "real-world example - Output parameter"
      (is (valid? apps/AppParameterRequest
                  {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                   :name "output.bam"
                   :label "Output BAM File"
                   :description "The aligned sequences in BAM format"
                   :type "FileOutput"
                   :required false
                   :isVisible true
                   :order 10
                   :file_parameters {:format "bam"
                                     :file_info_type "Alignment"
                                     :is_implicit false
                                     :data_source "stdout"}})))))
