(ns common-swagger-api.malli.apps.elements-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.apps.elements :as e]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AppParameterTypeParams
  (testing "AppParameterTypeParams validation"
    (is (valid? e/AppParameterTypeParams {}))
    (is (valid? e/AppParameterTypeParams {:tool-type "executable"}))
    (is (valid? e/AppParameterTypeParams {:tool-id #uuid "123e4567-e89b-12d3-a456-426614174000"}))
    (is (valid? e/AppParameterTypeParams {:tool-type "executable"
                                          :tool-id #uuid "123e4567-e89b-12d3-a456-426614174000"}))
    (is (not (valid? e/AppParameterTypeParams {:tool-type 123})))
    (is (not (valid? e/AppParameterTypeParams {:tool-id "not-a-uuid"})))
    (is (not (valid? e/AppParameterTypeParams {:extra-field "not-allowed"})))))

(deftest test-DataSource
  (testing "DataSource validation"
    (is (valid? e/DataSource
                {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                 :name "file"
                 :description "A plain file"
                 :label "File"}))
    (is (not (valid? e/DataSource {})))
    (is (not (valid? e/DataSource {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                    :name "file"
                                    :description "A plain file"})))))

(deftest test-FileFormat
  (testing "FileFormat validation"
    (is (valid? e/FileFormat
                {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                 :name "FASTA"}))
    (is (valid? e/FileFormat
                {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                 :name "FASTA"
                 :label "FASTA sequence file"}))
    (is (not (valid? e/FileFormat {})))
    (is (not (valid? e/FileFormat {:id #uuid "456e7890-b12c-34d5-e678-901234567890"})))))

(deftest test-InfoType
  (testing "InfoType validation"
    (is (valid? e/InfoType
                {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                 :name "BarCode"}))
    (is (valid? e/InfoType
                {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                 :name "BarCode"
                 :label "Barcode sequence"}))
    (is (not (valid? e/InfoType {})))
    (is (not (valid? e/InfoType {:name "BarCode"})))))

(deftest test-ParameterType
  (testing "ParameterType validation"
    (is (valid? e/ParameterType
                {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                 :name "Text"
                 :value_type "String"}))
    (is (valid? e/ParameterType
                {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                 :name "Text"
                 :description "Free-form text input"
                 :value_type "String"}))
    (is (not (valid? e/ParameterType {})))
    (is (not (valid? e/ParameterType {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                                       :name "Text"})))))

(deftest test-RuleType
  (testing "RuleType validation"
    (is (valid? e/RuleType
                {:id #uuid "567e8901-c34d-56e7-f890-123456789012"
                 :name "IntRange"
                 :rule_description_format "Enter a number between {min} and {max}"
                 :subtype "Number"
                 :value_types ["Integer" "Number"]}))
    (is (valid? e/RuleType
                {:id #uuid "567e8901-c34d-56e7-f890-123456789012"
                 :name "IntRange"
                 :description "Integer value within a specified range"
                 :rule_description_format "Enter a number between {min} and {max}"
                 :subtype "Number"
                 :value_types ["Integer"]}))
    (is (not (valid? e/RuleType {})))
    (is (not (valid? e/RuleType {:id #uuid "567e8901-c34d-56e7-f890-123456789012"
                                  :name "IntRange"})))))

(deftest test-ToolType
  (testing "ToolType validation"
    (is (valid? e/ToolType
                {:id #uuid "abc12345-def6-7890-1234-567890abcdef"
                 :name "executable"
                 :label "Executable"}))
    (is (valid? e/ToolType
                {:id #uuid "abc12345-def6-7890-1234-567890abcdef"
                 :name "executable"
                 :description "Command-line executable"
                 :label "Executable"}))
    (is (not (valid? e/ToolType {})))
    (is (not (valid? e/ToolType {:id #uuid "abc12345-def6-7890-1234-567890abcdef"
                                  :name "executable"})))))

(deftest test-ValueType
  (testing "ValueType validation"
    (is (valid? e/ValueType
                {:id #uuid "def67890-abc1-2345-6789-0abcdef12345"
                 :name "String"
                 :description "Character string value"}))
    (is (not (valid? e/ValueType {})))
    (is (not (valid? e/ValueType {:id #uuid "def67890-abc1-2345-6789-0abcdef12345"
                                   :name "String"})))))

(deftest test-listing-schemas
  (testing "Listing schema validation"
    (is (valid? e/DataSourceListing {:data_sources []}))
    (is (valid? e/DataSourceListing
                {:data_sources [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                 :name "file"
                                 :description "A plain file"
                                 :label "File"}]}))

    (is (valid? e/FileFormatListing {:formats []}))
    (is (valid? e/InfoTypeListing {:info_types []}))
    (is (valid? e/ParameterTypeListing {:parameter_types []}))
    (is (valid? e/RuleTypeListing {:rule_types []}))
    (is (valid? e/ToolTypeListing {:tool_types []}))
    (is (valid? e/ValueTypeListing {:value_types []}))

    (is (not (valid? e/DataSourceListing {})))
    (is (not (valid? e/FileFormatListing {})))
    (is (not (valid? e/InfoTypeListing {})))
    (is (not (valid? e/ParameterTypeListing {})))
    (is (not (valid? e/RuleTypeListing {})))
    (is (not (valid? e/ToolTypeListing {})))
    (is (not (valid? e/ValueTypeListing {})))))
