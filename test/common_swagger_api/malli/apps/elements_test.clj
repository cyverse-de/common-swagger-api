(ns common-swagger-api.malli.apps.elements-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.apps.elements :as elements]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def data-source
  {:id          #uuid "123e4567-e89b-12d3-a456-426614174000"
   :name        "file"
   :description "A plain file"
   :label       "File"})

(def file-format
  {:id   #uuid "456e7890-b12c-34d5-e678-901234567890"
   :name "FASTA"})

(def info-type
  {:id   #uuid "789a0123-c45d-67e8-f901-234567890abc"
   :name "BarCode"})

(def parameter-type
  {:id         #uuid "987e6543-e21b-32c1-b456-426614174000"
   :name       "Text"
   :value_type "String"})

(def rule-type
  {:id                      #uuid "567e8901-c34d-56e7-f890-123456789012"
   :name                    "IntRange"
   :rule_description_format "Enter a number between {min} and {max}"
   :subtype                 "Number"
   :value_types             ["Integer" "Number"]})

(def tool-type
  {:id    #uuid "abc12345-def6-7890-1234-567890abcdef"
   :name  "executable"
   :label "Executable"})

(def value-type
  {:id          #uuid "def67890-abc1-2345-6789-0abcdef12345"
   :name        "String"
   :description "Character string value"})

(deftest AppParameterTypeParams
  (valid elements/AppParameterTypeParams
         {}
         {:tool-type "executable"}
         {:tool-id #uuid "123e4567-e89b-12d3-a456-426614174000"})
  (invalid elements/AppParameterTypeParams {:tool-type 1} {:tool-id "123e4567"} {:extra 1}))

(deftest DataSource
  (valid elements/DataSource data-source)
  (invalid elements/DataSource
           {}
           (dissoc data-source :label)
           (assoc data-source :id (str (:id data-source)))
           (assoc data-source :extra 1)))

(deftest FileFormat
  (valid elements/FileFormat file-format (assoc file-format :label "FASTA sequence file"))
  (invalid elements/FileFormat
           {}
           (dissoc file-format :name)
           (assoc file-format :label 1)
           (assoc file-format :extra 1)))

(deftest InfoType
  (valid elements/InfoType info-type (assoc info-type :label "Barcode sequence"))
  (invalid elements/InfoType
           {}
           (dissoc info-type :id)
           (assoc info-type :name :BarCode)
           (assoc info-type :extra 1)))

(deftest ParameterType
  (valid elements/ParameterType parameter-type (assoc parameter-type :description "Free-form text input"))
  (invalid elements/ParameterType
           {}
           (dissoc parameter-type :value_type)
           (assoc parameter-type :description 1)
           (assoc parameter-type :extra 1)))

(deftest RuleType
  (valid elements/RuleType rule-type (assoc rule-type :description "Integer within a range" :value_types []))
  (invalid elements/RuleType
           {}
           (dissoc rule-type :subtype)
           (assoc rule-type :value_types "Integer")
           (assoc rule-type :extra 1)))

(deftest ToolType
  (valid elements/ToolType tool-type (assoc tool-type :description "Command-line executable"))
  (invalid elements/ToolType
           {}
           (dissoc tool-type :label)
           (assoc tool-type :id (str (:id tool-type)))
           (assoc tool-type :extra 1)))

(deftest ValueType
  (valid elements/ValueType value-type)
  (invalid elements/ValueType
           {}
           (dissoc value-type :description)
           (assoc value-type :name 1)
           (assoc value-type :extra 1)))

(deftest DataSourceListing
  (valid elements/DataSourceListing {:data_sources []} {:data_sources [data-source]})
  (invalid elements/DataSourceListing {} {:data_sources [{}]} {:data_sources [] :extra 1}))

(deftest FileFormatListing
  (valid elements/FileFormatListing {:formats []} {:formats [file-format]})
  (invalid elements/FileFormatListing {} {:formats [{}]} {:formats [] :extra 1}))

(deftest InfoTypeListing
  (valid elements/InfoTypeListing {:info_types []} {:info_types [info-type]})
  (invalid elements/InfoTypeListing {} {:info_types [{}]} {:info_types [] :extra 1}))

(deftest ParameterTypeListing
  (valid elements/ParameterTypeListing {:parameter_types []} {:parameter_types [parameter-type]})
  (invalid elements/ParameterTypeListing {} {:parameter_types [{}]} {:parameter_types [] :extra 1}))

(deftest RuleTypeListing
  (valid elements/RuleTypeListing {:rule_types []} {:rule_types [rule-type]})
  (invalid elements/RuleTypeListing {} {:rule_types [{}]} {:rule_types [] :extra 1}))

(deftest ToolTypeListing
  (valid elements/ToolTypeListing {:tool_types []} {:tool_types [tool-type]})
  (invalid elements/ToolTypeListing {} {:tool_types [{}]} {:tool_types [] :extra 1}))

(deftest ValueTypeListing
  (valid elements/ValueTypeListing {:value_types []} {:value_types [value-type]})
  (invalid elements/ValueTypeListing {} {:value_types [{}]} {:value_types [] :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps.elements))
