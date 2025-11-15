(ns common-swagger-api.malli.analyses-test
  (:require
   [clojure.test :refer [are deftest is testing]]
   [common-swagger-api.malli.analyses :as analyses]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-ParameterValue
  (testing "ParameterValue validation"
    (testing "valid params"
      ;; Map with string value
      (is (valid? analyses/ParameterValue
                  {:value "param_value"}))

      ;; Map with numeric value
      (is (valid? analyses/ParameterValue
                  {:value 123}))

      (is (valid? analyses/ParameterValue
                  {:value 123.45}))

      ;; Map with boolean value
      (is (valid? analyses/ParameterValue
                  {:value true}))

      (is (valid? analyses/ParameterValue
                  {:value false}))

      ;; Map with nil value (any type allows nil)
      (is (valid? analyses/ParameterValue
                  {:value nil}))

      ;; Map with empty string value
      (is (valid? analyses/ParameterValue
                  {:value ""}))

      ;; Map with whitespace-only string value
      (is (valid? analyses/ParameterValue
                  {:value "   "}))

      ;; Map with vector value
      (is (valid? analyses/ParameterValue
                  {:value [1 2 3]}))

      (is (valid? analyses/ParameterValue
                  {:value ["a" "b" "c"]}))

      ;; Map with nested map value
      (is (valid? analyses/ParameterValue
                  {:value {:nested "value"}}))

      ;; Map with keyword value
      (is (valid? analyses/ParameterValue
                  {:value :keyword}))

      ;; Map with UUID value
      (is (valid? analyses/ParameterValue
                  {:value #uuid "2b912405-5ae8-4db9-9023-db745b5a7c83"}))

      ;; Map with zero
      (is (valid? analyses/ParameterValue
                  {:value 0}))

      ;; Map with negative number
      (is (valid? analyses/ParameterValue
                  {:value -123}))

      ;; Map with large number
      (is (valid? analyses/ParameterValue
                  {:value 9999999999999}))

      ;; Map with special characters in string
      (is (valid? analyses/ParameterValue
                  {:value "special!@#$%^&*()chars"}))

      ;; Map with multiline string
      (is (valid? analyses/ParameterValue
                  {:value "line1\nline2\nline3"}))

      ;; Map with JSON-like string
      (is (valid? analyses/ParameterValue
                  {:value "{\"key\": \"value\"}"}))

      ;; Map with empty vector
      (is (valid? analyses/ParameterValue
                  {:value []}))

      ;; Map with empty map
      (is (valid? analyses/ParameterValue
                  {:value {}})))

    (testing "invalid params"
      ;; Empty map is invalid (missing required :value field)
      (is (not (valid? analyses/ParameterValue {})))

      ;; Map with wrong field name
      (is (not (valid? analyses/ParameterValue
                       {:val "param_value"})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? analyses/ParameterValue
                       {:value "param_value"
                        :extra-field "not allowed"})))

      (is (not (valid? analyses/ParameterValue
                       {:value "param_value"
                        :unknown-field "value"})))

      (is (not (valid? analyses/ParameterValue
                       {:value 123
                        :another "field"})))

      ;; Map with only extra fields (missing required :value)
      (is (not (valid? analyses/ParameterValue
                       {:extra-field "value"}))))))

(deftest test-AnalysisParameter
  (testing "AnalysisParameter validation"
    (testing "valid params"
      ;; Map with all required fields only
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "FileInput"}))

      ;; Map with all fields (required + all optional)
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_name "Input File"
                   :param_value {:value "param_value"}
                   :param_type "FileInput"
                   :info_type "SequenceAlignment"
                   :data_format "FASTA"
                   :is_default_value false
                   :is_visible true}))

      ;; Map with required fields + :param_name
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_name "Input File"
                   :param_type "FileInput"}))

      ;; Map with required fields + :param_value with string value
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_value {:value "some_value"}
                   :param_type "FileInput"}))

      ;; Map with required fields + :param_value with numeric value
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_value {:value 123}
                   :param_type "Number"}))

      ;; Map with required fields + :param_value with boolean value
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_value {:value true}
                   :param_type "Flag"}))

      ;; Map with required fields + :param_value with nil value
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_value {:value nil}
                   :param_type "FileInput"}))

      ;; Map with required fields + :info_type
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "FileInput"
                   :info_type "SequenceAlignment"}))

      ;; Map with required fields + :data_format
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "FileInput"
                   :data_format "FASTA"}))

      ;; Map with required fields + :is_default_value true
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "FileInput"
                   :is_default_value true}))

      ;; Map with required fields + :is_default_value false
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "FileInput"
                   :is_default_value false}))

      ;; Map with required fields + :is_visible true
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "FileInput"
                   :is_visible true}))

      ;; Map with required fields + :is_visible false
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "FileInput"
                   :is_visible false}))

      ;; Map with different valid param_type values
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "TextInput"}))

      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "Number"}))

      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "Flag"}))

      ;; Map with different valid info_type values
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "FileInput"
                   :info_type "PlainText"}))

      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "FileInput"
                   :info_type "GenomeAnnotation"}))

      ;; Map with different valid data_format values
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "FileInput"
                   :data_format "FASTQ"}))

      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_type "FileInput"
                   :data_format "BAM"}))

      ;; Map with empty strings (valid as :string is used)
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id ""
                   :param_id ""
                   :param_type ""}))

      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_name ""
                   :param_type "FileInput"
                   :info_type ""
                   :data_format ""}))

      ;; Map with whitespace-only strings
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "   "
                   :param_id "   "
                   :param_type "   "}))

      ;; Map with combination of optional fields
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_name "Output File"
                   :param_type "FileOutput"
                   :info_type "GenomeAnnotation"
                   :data_format "GFF3"}))

      ;; Map with boolean flags and param_value
      (is (valid? analyses/AnalysisParameter
                  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                   :param_value {:value "default"}
                   :param_type "TextInput"
                   :is_default_value true
                   :is_visible false})))

    (testing "invalid params"
      ;; Empty map is invalid (missing all required fields)
      (is (not (valid? analyses/AnalysisParameter {})))

      ;; Map missing :full_param_id (required field)
      (is (not (valid? analyses/AnalysisParameter
                       {:param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"})))

      ;; Map missing :param_id (required field)
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"})))

      ;; Map missing :param_type (required field)
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"})))

      ;; Map with only one required field
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"})))

      (is (not (valid? analyses/AnalysisParameter
                       {:param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"})))

      (is (not (valid? analyses/AnalysisParameter
                       {:param_type "FileInput"})))

      ;; Map with wrong type for :full_param_id
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id 123
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id nil
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id true
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"})))

      ;; Map with wrong type for :param_id
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id 123
                        :param_type "FileInput"})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id nil
                        :param_type "FileInput"})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id :keyword
                        :param_type "FileInput"})))

      ;; Map with wrong type for :param_type
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type 123})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type nil})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type ["FileInput"]})))

      ;; Map with wrong type for optional :param_name
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_name 123
                        :param_type "FileInput"})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_name nil
                        :param_type "FileInput"})))

      ;; Map with wrong type for optional :param_value (must be ParameterValue schema)
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_value "not a ParameterValue schema"
                        :param_type "FileInput"})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_value 123
                        :param_type "FileInput"})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_value nil
                        :param_type "FileInput"})))

      ;; Map with :param_value missing required :value field
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_value {}
                        :param_type "FileInput"})))

      ;; Map with :param_value having extra fields
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_value {:value "param_value"
                                      :extra "field"}
                        :param_type "FileInput"})))

      ;; Map with wrong type for optional :info_type
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"
                        :info_type 123})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"
                        :info_type nil})))

      ;; Map with wrong type for optional :data_format
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"
                        :data_format 123})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"
                        :data_format nil})))

      ;; Map with wrong type for optional :is_default_value
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"
                        :is_default_value "not a boolean"})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"
                        :is_default_value 1})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"
                        :is_default_value nil})))

      ;; Map with wrong type for optional :is_visible
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"
                        :is_visible "not a boolean"})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"
                        :is_visible 0})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"
                        :is_visible nil})))

      ;; Map with multiple wrong types
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id 123
                        :param_id nil
                        :param_type false})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"
                        :extra-field "not allowed"})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_type "FileInput"
                        :unknown-field "value"})))

      (is (not (valid? analyses/AnalysisParameter
                       {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                        :param_name "Input File"
                        :param_value {:value "param_value"}
                        :param_type "FileInput"
                        :info_type "SequenceAlignment"
                        :data_format "FASTA"
                        :is_default_value false
                        :is_visible true
                        :extra "field"}))))))

(deftest test-AnalysisParameters
  (testing "AnalysisParameters validation"
    (testing "valid params"
      ;; Map with all required fields and empty parameters vector
      (is (valid? analyses/AnalysisParameters
                  {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                   :system_id "de"
                   :parameters []}))

      ;; Map with all required fields and single parameter
      (is (valid? analyses/AnalysisParameters
                  {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                   :system_id "de"
                   :parameters [{:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                                 :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                                 :param_type "FileInput"}]}))

      ;; Map with all fields (required + optional :app_version_id)
      (is (valid? analyses/AnalysisParameters
                  {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                   :app_version_id "763a0d0c-d2bd-46fc-b3f6-747ba9c82cfa"
                   :system_id "de"
                   :parameters []}))

      ;; Map with multiple parameters
      (is (valid? analyses/AnalysisParameters
                  {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                   :system_id "de"
                   :parameters [{:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                                 :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                                 :param_type "FileInput"}
                                {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                                 :param_id "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                                 :param_type "TextInput"}]}))

      ;; Map with parameter containing all fields
      (is (valid? analyses/AnalysisParameters
                  {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                   :app_version_id "763a0d0c-d2bd-46fc-b3f6-747ba9c82cfa"
                   :system_id "de"
                   :parameters [{:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                                 :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                                 :param_name "Input File"
                                 :param_value {:value "param_value"}
                                 :param_type "FileInput"
                                 :info_type "SequenceAlignment"
                                 :data_format "FASTA"
                                 :is_default_value false
                                 :is_visible true}]}))

      ;; Map with different valid system_id values
      (is (valid? analyses/AnalysisParameters
                  {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                   :system_id "agave"
                   :parameters []}))

      (is (valid? analyses/AnalysisParameters
                  {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                   :system_id "osg"
                   :parameters []}))

      ;; Map with different app_id values
      (is (valid? analyses/AnalysisParameters
                  {:app_id "app-12345"
                   :system_id "de"
                   :parameters []}))

      (is (valid? analyses/AnalysisParameters
                  {:app_id "a"
                   :system_id "de"
                   :parameters []}))

      ;; Map with different app_version_id values
      (is (valid? analyses/AnalysisParameters
                  {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                   :app_version_id "version-12345"
                   :system_id "de"
                   :parameters []}))

      ;; Map with parameters containing various param_value types
      (is (valid? analyses/AnalysisParameters
                  {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                   :system_id "de"
                   :parameters [{:full_param_id "id1"
                                 :param_id "param1"
                                 :param_value {:value "string_value"}
                                 :param_type "TextInput"}
                                {:full_param_id "id2"
                                 :param_id "param2"
                                 :param_value {:value 123}
                                 :param_type "Number"}
                                {:full_param_id "id3"
                                 :param_id "param3"
                                 :param_value {:value true}
                                 :param_type "Flag"}]}))

      ;; Map with many parameters
      (is (valid? analyses/AnalysisParameters
                  {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                   :system_id "de"
                   :parameters (vec (repeat 10 {:full_param_id "id"
                                                :param_id "param"
                                                :param_type "TextInput"}))})))

    (testing "invalid params"
      ;; Empty map is invalid (missing all required fields)
      (is (not (valid? analyses/AnalysisParameters {})))

      ;; Map missing :app_id (required field)
      (is (not (valid? analyses/AnalysisParameters
                       {:system_id "de"
                        :parameters []})))

      ;; Map missing :system_id (required field)
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :parameters []})))

      ;; Map missing :parameters (required field)
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"})))

      ;; Map with only one required field
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"})))

      (is (not (valid? analyses/AnalysisParameters
                       {:system_id "de"})))

      (is (not (valid? analyses/AnalysisParameters
                       {:parameters []})))

      ;; Map with wrong type for :app_id
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id 123
                        :system_id "de"
                        :parameters []})))

      (is (not (valid? analyses/AnalysisParameters
                       {:app_id nil
                        :system_id "de"
                        :parameters []})))

      (is (not (valid? analyses/AnalysisParameters
                       {:app_id true
                        :system_id "de"
                        :parameters []})))

      ;; Map with empty string for :app_id (should be valid - no min constraint on app_id)
      (is (valid? analyses/AnalysisParameters
                  {:app_id ""
                   :system_id "de"
                   :parameters []}))

      ;; Map with wrong type for :system_id
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id 123
                        :parameters []})))

      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id nil
                        :parameters []})))

      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id :de
                        :parameters []})))

      ;; Map with empty string for :system_id (invalid - SystemId has min 1)
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id ""
                        :parameters []})))

      ;; Map with wrong type for :parameters (not a vector)
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters "not a vector"})))

      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters nil})))

      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters {:param "value"}})))

      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters 123})))

      ;; Map with wrong type for optional :app_version_id
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :app_version_id 123
                        :system_id "de"
                        :parameters []})))

      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :app_version_id nil
                        :system_id "de"
                        :parameters []})))

      ;; Map with invalid parameter entry (missing required field)
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters [{:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                                      :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"}]})))

      ;; Map with invalid parameter entry (wrong type)
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters [{:full_param_id 123
                                      :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                                      :param_type "FileInput"}]})))

      ;; Map with parameter entry with invalid param_value
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters [{:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                                      :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                                      :param_value {}
                                      :param_type "FileInput"}]})))

      ;; Map with parameter entry with extra field
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters [{:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                                      :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                                      :param_type "FileInput"
                                      :extra "field"}]})))

      ;; Map with non-map elements in parameters vector
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters ["not a parameter"]})))

      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters [123]})))

      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters [nil]})))

      ;; Map with mix of valid and invalid parameters
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters [{:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
                                      :param_id "17826e1e-9132-47e3-a2e0-0399f212bb92"
                                      :param_type "FileInput"}
                                     {:full_param_id 123
                                      :param_id "invalid"
                                      :param_type "TextInput"}]})))

      ;; Map with multiple wrong types
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id 123
                        :system_id nil
                        :parameters "not a vector"})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters []
                        :extra-field "not allowed"})))

      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :system_id "de"
                        :parameters []
                        :unknown-field "value"})))

      (is (not (valid? analyses/AnalysisParameters
                       {:app_id "0f32febd-13fb-49b3-93f9-f6e39d46d817"
                        :app_version_id "763a0d0c-d2bd-46fc-b3f6-747ba9c82cfa"
                        :system_id "de"
                        :parameters []
                        :extra "field"}))))))

(deftest test-AnalysisRelauncherRequest
  (testing "AnalysisRelauncherRequest validation"
    (testing "valid params"
      ;; Map with empty analyses vector
      (is (valid? analyses/AnalysisRelauncherRequest
                  {:analyses []}))

      ;; Map with single analysis UUID
      (is (valid? analyses/AnalysisRelauncherRequest
                  {:analyses [#uuid "cca90887-fb54-4504-b882-e8f631f9613e"]}))

      ;; Map with two analysis UUIDs (from schema example)
      (is (valid? analyses/AnalysisRelauncherRequest
                  {:analyses [#uuid "cca90887-fb54-4504-b882-e8f631f9613e"
                              #uuid "4658e4f9-0297-4493-ab56-ba06960a9bd0"]}))

      ;; Map with multiple analysis UUIDs
      (is (valid? analyses/AnalysisRelauncherRequest
                  {:analyses [#uuid "cca90887-fb54-4504-b882-e8f631f9613e"
                              #uuid "4658e4f9-0297-4493-ab56-ba06960a9bd0"
                              #uuid "2b912405-5ae8-4db9-9023-db745b5a7c83"]}))

      ;; Map with different valid UUIDs
      (is (valid? analyses/AnalysisRelauncherRequest
                  {:analyses [#uuid "00000000-0000-0000-0000-000000000000"]}))

      (is (valid? analyses/AnalysisRelauncherRequest
                  {:analyses [#uuid "ffffffff-ffff-ffff-ffff-ffffffffffff"]}))

      (is (valid? analyses/AnalysisRelauncherRequest
                  {:analyses [#uuid "12345678-1234-1234-1234-123456789abc"]}))

      ;; Map with many UUIDs
      (is (valid? analyses/AnalysisRelauncherRequest
                  {:analyses [#uuid "00000000-0000-0000-0000-000000000001"
                              #uuid "00000000-0000-0000-0000-000000000002"
                              #uuid "00000000-0000-0000-0000-000000000003"
                              #uuid "00000000-0000-0000-0000-000000000004"
                              #uuid "00000000-0000-0000-0000-000000000005"]})))

    (testing "invalid params"
      ;; Empty map is invalid (missing required field)
      (is (not (valid? analyses/AnalysisRelauncherRequest {})))

      ;; Map with wrong type for :analyses (not a vector)
      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses "not a vector"})))

      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses nil})))

      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses #uuid "cca90887-fb54-4504-b882-e8f631f9613e"})))

      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses {:uuid #uuid "cca90887-fb54-4504-b882-e8f631f9613e"}})))

      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses 123})))

      ;; Map with vector containing non-UUID elements
      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses ["cca90887-fb54-4504-b882-e8f631f9613e"]})))

      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses [123]})))

      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses [nil]})))

      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses [true]})))

      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses [:uuid]})))

      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses [{}]})))

      ;; Map with mix of valid and invalid elements
      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses [#uuid "cca90887-fb54-4504-b882-e8f631f9613e"
                                   "not a uuid"]})))

      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses [#uuid "cca90887-fb54-4504-b882-e8f631f9613e"
                                   123]})))

      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses [#uuid "cca90887-fb54-4504-b882-e8f631f9613e"
                                   nil
                                   #uuid "4658e4f9-0297-4493-ab56-ba06960a9bd0"]})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses []
                        :extra-field "not allowed"})))

      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses []
                        :unknown-field "value"})))

      (is (not (valid? analyses/AnalysisRelauncherRequest
                       {:analyses [#uuid "cca90887-fb54-4504-b882-e8f631f9613e"
                                   #uuid "4658e4f9-0297-4493-ab56-ba06960a9bd0"]
                        :extra "field"}))))))

(deftest test-AnalysisShredderRequest
  (testing "AnalysisShredderRequest validation"
    (testing "valid params"
      ;; Map with empty analyses vector
      (is (valid? analyses/AnalysisShredderRequest
                  {:analyses []}))

      ;; Map with single analysis UUID
      (is (valid? analyses/AnalysisShredderRequest
                  {:analyses [#uuid "2506f0bf-9664-4fa2-8465-41e3a0a487de"]}))

      ;; Map with two analysis UUIDs (from schema example)
      (is (valid? analyses/AnalysisShredderRequest
                  {:analyses [#uuid "2506f0bf-9664-4fa2-8465-41e3a0a487de"
                              #uuid "88e81af2-4f22-4f73-aaec-5a4a81c62256"]}))

      ;; Map with multiple analysis UUIDs
      (is (valid? analyses/AnalysisShredderRequest
                  {:analyses [#uuid "2506f0bf-9664-4fa2-8465-41e3a0a487de"
                              #uuid "88e81af2-4f22-4f73-aaec-5a4a81c62256"
                              #uuid "2b912405-5ae8-4db9-9023-db745b5a7c83"]}))

      ;; Map with different valid UUIDs
      (is (valid? analyses/AnalysisShredderRequest
                  {:analyses [#uuid "00000000-0000-0000-0000-000000000000"]}))

      (is (valid? analyses/AnalysisShredderRequest
                  {:analyses [#uuid "ffffffff-ffff-ffff-ffff-ffffffffffff"]}))

      (is (valid? analyses/AnalysisShredderRequest
                  {:analyses [#uuid "12345678-1234-1234-1234-123456789abc"]}))

      ;; Map with many UUIDs
      (is (valid? analyses/AnalysisShredderRequest
                  {:analyses [#uuid "00000000-0000-0000-0000-000000000001"
                              #uuid "00000000-0000-0000-0000-000000000002"
                              #uuid "00000000-0000-0000-0000-000000000003"
                              #uuid "00000000-0000-0000-0000-000000000004"
                              #uuid "00000000-0000-0000-0000-000000000005"]})))

    (testing "invalid params"
      ;; Empty map is invalid (missing required field)
      (is (not (valid? analyses/AnalysisShredderRequest {})))

      ;; Map with wrong type for :analyses (not a vector)
      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses "not a vector"})))

      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses nil})))

      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses #uuid "2506f0bf-9664-4fa2-8465-41e3a0a487de"})))

      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses {:uuid #uuid "2506f0bf-9664-4fa2-8465-41e3a0a487de"}})))

      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses 123})))

      ;; Map with vector containing non-UUID elements
      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses ["2506f0bf-9664-4fa2-8465-41e3a0a487de"]})))

      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses [123]})))

      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses [nil]})))

      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses [true]})))

      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses [:uuid]})))

      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses [{}]})))

      ;; Map with mix of valid and invalid elements
      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses [#uuid "2506f0bf-9664-4fa2-8465-41e3a0a487de"
                                   "not a uuid"]})))

      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses [#uuid "2506f0bf-9664-4fa2-8465-41e3a0a487de"
                                   123]})))

      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses [#uuid "2506f0bf-9664-4fa2-8465-41e3a0a487de"
                                   nil
                                   #uuid "88e81af2-4f22-4f73-aaec-5a4a81c62256"]})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses []
                        :extra-field "not allowed"})))

      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses []
                        :unknown-field "value"})))

      (is (not (valid? analyses/AnalysisShredderRequest
                       {:analyses [#uuid "2506f0bf-9664-4fa2-8465-41e3a0a487de"
                                   #uuid "88e81af2-4f22-4f73-aaec-5a4a81c62256"]
                        :extra "field"}))))))

(deftest test-StopAnalysisRequest
  (testing "StopAnalysisRequest validation"
    (testing "valid params"
      ;; Empty map is valid (all fields are optional)
      (is (valid? analyses/StopAnalysisRequest {}))

      ;; Map with :job_status "Canceled" (from schema example)
      (is (valid? analyses/StopAnalysisRequest
                  {:job_status "Canceled"}))

      ;; Map with :job_status "Completed"
      (is (valid? analyses/StopAnalysisRequest
                  {:job_status "Completed"}))

      ;; Map with :job_status "Failed"
      (is (valid? analyses/StopAnalysisRequest
                  {:job_status "Failed"})))

    (testing "invalid params"
      ;; Map with invalid :job_status value (not in enum)
      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status "Running"})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status "Submitted"})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status "Pending"})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status "Invalid"})))

      ;; Map with lowercase enum values (case-sensitive)
      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status "canceled"})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status "completed"})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status "failed"})))

      ;; Map with uppercase enum values
      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status "CANCELED"})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status "COMPLETED"})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status "FAILED"})))

      ;; Map with empty string
      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status ""})))

      ;; Map with wrong type for :job_status
      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status 123})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status nil})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status true})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status :Canceled})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status ["Canceled"]})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? analyses/StopAnalysisRequest
                       {:extra-field "not allowed"})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:unknown-field "value"})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status "Canceled"
                        :extra-field "not allowed"})))

      (is (not (valid? analyses/StopAnalysisRequest
                       {:job_status "Canceled"
                        :reason "user requested"}))))))

(deftest test-StopAnalysisResponse
  (testing "StopAnalysisResponse validation"
    (testing "valid params"
      ;; Map with required :id field (from schema example)
      (is (valid? analyses/StopAnalysisResponse
                  {:id #uuid "b2a1790c-fe7d-4e4c-8a2e-4734b69bbba6"}))

      ;; Map with different valid UUIDs
      (is (valid? analyses/StopAnalysisResponse
                  {:id #uuid "00000000-0000-0000-0000-000000000000"}))

      (is (valid? analyses/StopAnalysisResponse
                  {:id #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff"}))

      (is (valid? analyses/StopAnalysisResponse
                  {:id #uuid "12345678-1234-1234-1234-123456789abc"}))

      (is (valid? analyses/StopAnalysisResponse
                  {:id #uuid "2b912405-5ae8-4db9-9023-db745b5a7c83"})))

    (testing "invalid params"
      ;; Empty map is invalid (missing required field)
      (is (not (valid? analyses/StopAnalysisResponse {})))

      ;; Map with wrong type for :id
      (is (not (valid? analyses/StopAnalysisResponse
                       {:id "b2a1790c-fe7d-4e4c-8a2e-4734b69bbba6"})))

      (is (not (valid? analyses/StopAnalysisResponse
                       {:id 123})))

      (is (not (valid? analyses/StopAnalysisResponse
                       {:id nil})))

      (is (not (valid? analyses/StopAnalysisResponse
                       {:id true})))

      (is (not (valid? analyses/StopAnalysisResponse
                       {:id :uuid})))

      (is (not (valid? analyses/StopAnalysisResponse
                       {:id ["b2a1790c-fe7d-4e4c-8a2e-4734b69bbba6"]})))

      (is (not (valid? analyses/StopAnalysisResponse
                       {:id {:uuid #uuid "b2a1790c-fe7d-4e4c-8a2e-4734b69bbba6"}})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? analyses/StopAnalysisResponse
                       {:id #uuid "b2a1790c-fe7d-4e4c-8a2e-4734b69bbba6"
                        :extra-field "not allowed"})))

      (is (not (valid? analyses/StopAnalysisResponse
                       {:id #uuid "b2a1790c-fe7d-4e4c-8a2e-4734b69bbba6"
                        :unknown-field "value"})))

      (is (not (valid? analyses/StopAnalysisResponse
                       {:id #uuid "b2a1790c-fe7d-4e4c-8a2e-4734b69bbba6"
                        :status "Canceled"}))))))

(deftest test-FileMetadata
  (testing "valid file metadata"
    (are [obj]
        (valid? analyses/FileMetadata obj)

      {:attr "foo" :value "bar" :unit "baz"}
      {:attr "empty-unit" :value "the-value" :unit ""}
      {:attr "empty-value" :value "" :unit "something"}
      {:attr "" :value "empty-attribute" :unit "something-else"}))

  (testing "invalid file metadata"
    (are [obj]
        (not (valid? analyses/FileMetadata obj))

      {:attr "missing-unit" :value "some-value"}
      {:attr "missing-value" :unit "some-unit"}
      {:value "missing-attr" :unit "some-unit"}
      {:attr "unrecognized" :value "field" :unit "quux" :extra-field "blrfl"})))

(deftest test-AnalysisSubmissionConfig
  (testing "valid analysis submission config"
    (are [obj]
        (valid? analyses/AnalysisSubmissionConfig obj)

      ;; Empty map
      {}

      ;; Single keyword-value pair with string value
      {:step1_param1 "value1"}

      ;; Single keyword-value pair with number value
      {:step1_param1 123}

      ;; Single keyword-value pair with float value
      {:step1_param1 123.45}

      ;; Single keyword-value pair with boolean value
      {:step1_param1 true}
      {:step1_param1 false}

      ;; Single keyword-value pair with nil value
      {:step1_param1 nil}

      ;; Single keyword-value pair with vector value
      {:step1_param1 ["value1" "value2"]}

      ;; Single keyword-value pair with map value
      {:step1_param1 {:nested "value"}}

      ;; Multiple keyword-value pairs
      {:step1_param1 "value1"
       :step1_param2 "value2"}

      ;; Multiple keyword-value pairs with various types
      {:step1_param1 "string"
       :step1_param2 123
       :step1_param3 true
       :step1_param4 nil
       :step1_param5 ["vector"]}

      ;; Keyword keys with underscores (typical format)
      {:50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92 "/path/to/file"}

      ;; Keyword keys with hyphens
      {:step-1_param-1 "value"}

      ;; Complex nested values
      {:step1_param1 {:nested {:deeply "value"}}}

      ;; UUID as value
      {:step1_param1 #uuid "2b912405-5ae8-4db9-9023-db745b5a7c83"}

      ;; Empty string as value
      {:step1_param1 ""}

      ;; Zero as value
      {:step1_param1 0}

      ;; Negative number as value
      {:step1_param1 -123}))

  (testing "invalid analysis submission config"
    (are [obj]
        (not (valid? analyses/AnalysisSubmissionConfig obj))

      ;; String keys (not keywords)
      {"step1_param1" "value1"}

      ;; Number keys
      {123 "value1"}

      ;; Nil as a key
      {nil "value1"}

      ;; Not a map - string
      "not a map"

      ;; Not a map - number
      123

      ;; Not a map - vector
      [:step1_param1 "value1"]

      ;; Not a map - nil
      nil

      ;; Mixed key types (keyword and string)
      {:step1_param1 "value1"
       "step2_param2" "value2"}

      ;; Mixed key types (keyword and number)
      {:step1_param1 "value1"
       123 "value2"})))

(deftest test-AnalysisStepResourceRequirements
  (testing "valid analysis step resource requirements"
    (are [obj]
        (valid? analyses/AnalysisStepResourceRequirements obj)

      ;; Only required field :step_number
      {:step_number 1}

      ;; With :min_memory_limit
      {:step_number 1
       :min_memory_limit 536870912}

      ;; With :min_cpu_cores
      {:step_number 1
       :min_cpu_cores 1.0}

      ;; With :max_cpu_cores
      {:step_number 1
       :max_cpu_cores 4.0}

      ;; With :min_gpus
      {:step_number 1
       :min_gpus 1}

      ;; With :max_gpus
      {:step_number 1
       :max_gpus 4}

      ;; With :min_disk_space
      {:step_number 1
       :min_disk_space 10737418240}

      ;; With all fields
      {:step_number 1
       :min_memory_limit 536870912
       :min_cpu_cores 1.0
       :max_cpu_cores 4.0
       :min_gpus 1
       :max_gpus 4
       :min_disk_space 10737418240}

      ;; With some optional fields
      {:step_number 2
       :min_cpu_cores 2.0
       :max_cpu_cores 8.0}

      ;; Different step numbers
      {:step_number 0}
      {:step_number 10}
      {:step_number 999}

      ;; Zero values for optional fields
      {:step_number 1
       :min_memory_limit 0}

      {:step_number 1
       :min_cpu_cores 0.0}

      {:step_number 1
       :min_gpus 0}

      {:step_number 1
       :min_disk_space 0}

      ;; Large values
      {:step_number 1
       :min_memory_limit 1099511627776}

      {:step_number 1
       :max_cpu_cores 128.0}

      ;; Fractional CPU cores
      {:step_number 1
       :min_cpu_cores 0.5
       :max_cpu_cores 2.5}))

  (testing "invalid analysis step resource requirements"
    (are [obj]
        (not (valid? analyses/AnalysisStepResourceRequirements obj))

      ;; Empty map (missing required :step_number)
      {}

      ;; Missing :step_number
      {:min_memory_limit 536870912}

      {:min_cpu_cores 1.0
       :max_cpu_cores 4.0}

      ;; Wrong type for :step_number
      {:step_number "1"}

      {:step_number 1.5}

      {:step_number nil}

      {:step_number true}

      ;; Wrong type for :min_memory_limit (should be int)
      {:step_number 1
       :min_memory_limit "536870912"}

      {:step_number 1
       :min_memory_limit 536870912.5}

      {:step_number 1
       :min_memory_limit nil}

      ;; Wrong type for :min_cpu_cores (should be double)
      {:step_number 1
       :min_cpu_cores "1.0"}

      {:step_number 1
       :min_cpu_cores nil}

      ;; Wrong type for :max_cpu_cores (should be double)
      {:step_number 1
       :max_cpu_cores "4.0"}

      {:step_number 1
       :max_cpu_cores nil}

      ;; Wrong type for :min_gpus (should be int)
      {:step_number 1
       :min_gpus 1.5}

      {:step_number 1
       :min_gpus "1"}

      {:step_number 1
       :min_gpus nil}

      ;; Wrong type for :max_gpus (should be int)
      {:step_number 1
       :max_gpus 4.0}

      {:step_number 1
       :max_gpus "4"}

      {:step_number 1
       :max_gpus nil}

      ;; Wrong type for :min_disk_space (should be int)
      {:step_number 1
       :min_disk_space 10737418240.5}

      {:step_number 1
       :min_disk_space "10737418240"}

      {:step_number 1
       :min_disk_space nil}

      ;; Extra fields not allowed (closed schema)
      {:step_number 1
       :extra_field "not allowed"}

      {:step_number 1
       :min_memory_limit 536870912
       :unknown_field "value"}

      ;; Fields not in the select-keys list
      {:step_number 1
       :memory_limit 1073741824}

      {:step_number 1
       :default_cpu_cores 1.0}

      {:step_number 1
       :default_memory 536870912}

      ;; Not a map
      "not a map"

      123

      nil

      [:step_number 1])))

(deftest test-AnalysisSubmission
  (testing "valid analysis submission"
    (are [obj]
        (valid? analyses/AnalysisSubmission obj)

      ;; Minimal valid submission with only required fields
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; With all required fields plus some optional fields
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :app_version_id "02ff8f75-a4fc-4d8c-9d76-ef91f764cec4"
       :config {:step1_param1 "value1"}
       :debug true
       :name "sequence-alignment"
       :notify true
       :output_dir "/zone/home/username/outputs"}

      ;; With :job_id
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :job_id #uuid "1738fd3b-702d-4022-9c89-0337ef726cfb"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; With :callback
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :callback "https://example.com/callback"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; With :requirements
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :requirements [{:step_number 1 :min_cpu_cores 2.0}]
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; With multiple requirements
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :requirements [{:step_number 1 :min_cpu_cores 2.0}
                      {:step_number 2 :min_memory_limit 1073741824}]
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; With :create_output_subdir
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :create_output_subdir true
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; With :description
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :description "Sequence alignment for Zea Mays."
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; With :notify_periodic and :periodic_period
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify true
       :notify_periodic true
       :periodic_period 3600
       :output_dir "/zone/home/username/outputs"}

      ;; With :starting_step
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :starting_step 2
       :output_dir "/zone/home/username/outputs"}

      ;; With :uuid
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :uuid #uuid "df97797c-0f8d-41ae-898d-68dcd79abfc8"
       :output_dir "/zone/home/username/outputs"}

      ;; With :skip-parent-meta
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :skip-parent-meta true
       :output_dir "/zone/home/username/outputs"}

      ;; With :file-metadata (empty vector)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :file-metadata []
       :output_dir "/zone/home/username/outputs"}

      ;; With :file-metadata (single item)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :file-metadata [{:attr "sample_weight" :value "2" :unit "kg"}]
       :output_dir "/zone/home/username/outputs"}

      ;; With :file-metadata (multiple items)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :file-metadata [{:attr "sample_weight" :value "2" :unit "kg"}
                       {:attr "temperature" :value "25" :unit "C"}
                       {:attr "experiment_id" :value "EXP-123" :unit ""}]
       :output_dir "/zone/home/username/outputs"}

      ;; With :archive_logs
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :archive_logs true
       :output_dir "/zone/home/username/outputs"}

      ;; With :mount_data_store
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :mount_data_store false
       :output_dir "/zone/home/username/outputs"}

      ;; With all fields populated
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :app_version_id "02ff8f75-a4fc-4d8c-9d76-ef91f764cec4"
       :job_id #uuid "1738fd3b-702d-4022-9c89-0337ef726cfb"
       :callback "https://example.com/callback"
       :config {:step1_param1 "value1" :step1_param2 123}
       :requirements [{:step_number 1 :min_cpu_cores 2.0 :min_memory_limit 1073741824}]
       :create_output_subdir true
       :debug true
       :description "Full analysis submission"
       :name "comprehensive-test"
       :notify true
       :notify_periodic false
       :periodic_period 1800
       :output_dir "/zone/home/username/outputs"
       :starting_step 1
       :uuid #uuid "df97797c-0f8d-41ae-898d-68dcd79abfc8"
       :skip-parent-meta false
       :file-metadata [{:attr "experiment_id" :value "EXP-123" :unit ""}]
       :archive_logs true
       :mount_data_store true}

      ;; Different valid system_id values
      {:system_id "agave"
       :app_id "app-123"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/output"}

      {:system_id "osg"
       :app_id "app-456"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/output"}

      ;; Empty config
      {:system_id "de"
       :app_id "app-id"
       :config {}
       :debug false
       :name "test"
       :notify false
       :output_dir "/out"}

      ;; Empty requirements vector
      {:system_id "de"
       :app_id "app-id"
       :config {}
       :requirements []
       :debug false
       :name "test"
       :notify false
       :output_dir "/out"}))

  (testing "invalid analysis submission"
    (are [obj]
        (not (valid? analyses/AnalysisSubmission obj))

      ;; Empty map
      {}

      ;; Missing :system_id
      {:app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Missing :app_id
      {:system_id "de"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Missing :config
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Missing :debug
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Missing :name
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Missing :notify
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :output_dir "/zone/home/username/outputs"}

      ;; Missing :output_dir
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false}

      ;; Wrong type for :system_id
      {:system_id 123
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      {:system_id ""
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :app_id
      {:system_id "de"
       :app_id 123
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      {:system_id "de"
       :app_id nil
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :app_version_id
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :app_version_id 123
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :job_id
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :job_id "not-a-uuid"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :callback
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :callback 123
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :config
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config "not a map"
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config nil
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :requirements (not a vector)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :requirements {:step_number 1}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Invalid requirement in vector
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :requirements [{:min_cpu_cores 2.0}]
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :create_output_subdir
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :create_output_subdir "true"
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :debug
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug "false"
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug nil
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :description
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :description 123
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :name
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name 123
       :notify false
       :output_dir "/zone/home/username/outputs"}

      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name nil
       :notify false
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :notify
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify "true"
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :notify_periodic
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :notify_periodic 1
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :periodic_period
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :periodic_period "3600"
       :output_dir "/zone/home/username/outputs"}

      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :periodic_period 3600.5
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :output_dir
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir 123}

      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir nil}

      ;; Wrong type for :starting_step
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :starting_step "2"
       :output_dir "/zone/home/username/outputs"}

      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :starting_step 2.5
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :uuid
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :uuid "not-a-uuid"
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :skip-parent-meta
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :skip-parent-meta "true"
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :file-metadata (not a vector - string)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :file-metadata "not a vector"
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :file-metadata (not a vector - single object)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :file-metadata {:attr "sample_weight" :value "2" :unit "kg"}
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :file-metadata (not a vector - number)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :file-metadata 123
       :output_dir "/zone/home/username/outputs"}

      ;; Invalid :file-metadata (vector with invalid FileMetadata - missing :unit)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :file-metadata [{:attr "sample_weight" :value "2"}]
       :output_dir "/zone/home/username/outputs"}

      ;; Invalid :file-metadata (vector with invalid FileMetadata - missing :value)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :file-metadata [{:attr "sample_weight" :unit "kg"}]
       :output_dir "/zone/home/username/outputs"}

      ;; Invalid :file-metadata (vector with invalid FileMetadata - missing :attr)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :file-metadata [{:value "2" :unit "kg"}]
       :output_dir "/zone/home/username/outputs"}

      ;; Invalid :file-metadata (vector with extra field)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :file-metadata [{:attr "sample_weight" :value "2" :unit "kg" :extra "field"}]
       :output_dir "/zone/home/username/outputs"}

      ;; Invalid :file-metadata (vector containing non-map)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :file-metadata ["not a map"]
       :output_dir "/zone/home/username/outputs"}

      ;; Invalid :file-metadata (mixed valid and invalid items)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :file-metadata [{:attr "sample_weight" :value "2" :unit "kg"}
                       {:attr "missing-value" :unit "C"}]
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :archive_logs
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :archive_logs 1
       :output_dir "/zone/home/username/outputs"}

      ;; Wrong type for :mount_data_store
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :mount_data_store "false"
       :output_dir "/zone/home/username/outputs"}

      ;; Extra field not allowed (closed schema)
      {:system_id "de"
       :app_id "007a8434-1b84-42e8-b647-4073a62b4b3b"
       :config {}
       :debug false
       :name "my-analysis"
       :notify false
       :output_dir "/zone/home/username/outputs"
       :extra_field "not allowed"}

      ;; Not a map
      "not a map"

      123

      nil

      [:system_id "de"])))

(deftest test-AnalysisResponse
  (testing "valid analysis response"
    (are [obj]
        (valid? analyses/AnalysisResponse obj)

      ;; Minimal valid response with only required fields
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "An arabidopsis thaliana sequence alignment"
       :status "Submitted"
       :start-date "1763083385000"}

      ;; With :missing-paths
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "An arabidopsis thaliana sequence alignment"
       :status "Submitted"
       :start-date "1763083385000"
       :missing-paths ["/foo/bar/baz" "/foo/bar/quux"]}

      ;; With empty :missing-paths vector
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis name"
       :status "Running"
       :start-date "1763083385000"
       :missing-paths []}

      ;; With single missing path
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Test analysis"
       :status "Completed"
       :start-date "1763083385000"
       :missing-paths ["/path/to/missing/file"]}

      ;; Different valid UUIDs
      {:id #uuid "00000000-0000-0000-0000-000000000000"
       :name "Test"
       :status "Failed"
       :start-date "0"}

      {:id #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff"
       :name "Test"
       :status "Canceled"
       :start-date "9999999999999"}

      ;; Different status values
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Submitted"
       :start-date "1763083385000"}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Running"
       :start-date "1763083385000"}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Completed"
       :start-date "1763083385000"}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Failed"
       :start-date "1763083385000"}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Canceled"
       :start-date "1763083385000"}

      ;; Empty strings
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name ""
       :status ""
       :start-date ""}

      ;; Multiple missing paths
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Completed"
       :start-date "1763083385000"
       :missing-paths ["/path1" "/path2" "/path3" "/path4"]}))

  (testing "invalid analysis response"
    (are [obj]
        (not (valid? analyses/AnalysisResponse obj))

      ;; Empty map
      {}

      ;; Missing :id
      {:name "An arabidopsis thaliana sequence alignment"
       :status "Submitted"
       :start-date "1763083385000"}

      ;; Missing :name
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :status "Submitted"
       :start-date "1763083385000"}

      ;; Missing :status
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "An arabidopsis thaliana sequence alignment"
       :start-date "1763083385000"}

      ;; Missing :start-date
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "An arabidopsis thaliana sequence alignment"
       :status "Submitted"}

      ;; Wrong type for :id
      {:id "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Submitted"
       :start-date "1763083385000"}

      {:id 123
       :name "Analysis"
       :status "Submitted"
       :start-date "1763083385000"}

      {:id nil
       :name "Analysis"
       :status "Submitted"
       :start-date "1763083385000"}

      ;; Wrong type for :name
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name 123
       :status "Submitted"
       :start-date "1763083385000"}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name nil
       :status "Submitted"
       :start-date "1763083385000"}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name true
       :status "Submitted"
       :start-date "1763083385000"}

      ;; Wrong type for :status
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status 123
       :start-date "1763083385000"}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status nil
       :start-date "1763083385000"}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status :Submitted
       :start-date "1763083385000"}

      ;; Wrong type for :start-date
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Submitted"
       :start-date 1763083385000}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Submitted"
       :start-date nil}

      ;; Wrong type for :missing-paths (not a vector)
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Submitted"
       :start-date "1763083385000"
       :missing-paths "/foo/bar/baz"}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Submitted"
       :start-date "1763083385000"
       :missing-paths nil}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Submitted"
       :start-date "1763083385000"
       :missing-paths {:path "/foo"}}

      ;; Wrong element type in :missing-paths vector
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Submitted"
       :start-date "1763083385000"
       :missing-paths [123]}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Submitted"
       :start-date "1763083385000"
       :missing-paths [nil]}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Submitted"
       :start-date "1763083385000"
       :missing-paths ["/valid/path" 123 "/another/path"]}

      ;; Extra field not allowed (closed schema)
      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Submitted"
       :start-date "1763083385000"
       :extra_field "not allowed"}

      {:id #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
       :name "Analysis"
       :status "Submitted"
       :start-date "1763083385000"
       :missing-paths ["/foo"]
       :unknown "field"}

      ;; Not a map
      "not a map"

      123

      nil

      [#uuid "570e5ca1-194d-400a-bc91-fc64324f7367"])))

(deftest test-AnalysisPod
  (testing "valid analysis pod"
    (are [obj]
        (valid? analyses/AnalysisPod obj)

      ;; Minimal valid pod with required fields
      {:name "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05-79c44695b5-v8s4w"
       :external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"}

      ;; Pod with short name
      {:name "pod-abc"
       :external_id #uuid "123e4567-e89b-12d3-a456-426614174000"}

      ;; Pod with long name
      {:name "very-long-pod-name-with-many-segments-12345678-90ab-cdef-1234-567890abcdef-79c44695b5-v8s4w"
       :external_id #uuid "12345678-90ab-cdef-1234-567890abcdef"}

      ;; Pod with hyphenated name
      {:name "analysis-pod-12345-abcde"
       :external_id #uuid "abcdef12-3456-7890-abcd-ef1234567890"}

      ;; Pod with alphanumeric name
      {:name "pod123abc456def"
       :external_id #uuid "98765432-1234-5678-9abc-def012345678"}

      ;; Pod with empty string name (strings allow empty)
      {:name ""
       :external_id #uuid "11111111-2222-3333-4444-555555555555"}))

  (testing "invalid analysis pod"
    (are [obj]
        (not (valid? analyses/AnalysisPod obj))

      ;; Empty map (missing required fields)
      {}

      ;; Missing :name
      {:external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"}

      ;; Missing :external_id
      {:name "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05-79c44695b5-v8s4w"}

      ;; Wrong type for :name (number instead of string)
      {:name 123
       :external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"}

      ;; Wrong type for :name (nil)
      {:name nil
       :external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"}

      ;; Wrong type for :name (boolean)
      {:name true
       :external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"}

      ;; Wrong type for :name (keyword)
      {:name :pod-name
       :external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"}

      ;; Wrong type for :external_id (string instead of uuid)
      {:name "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05-79c44695b5-v8s4w"
       :external_id "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"}

      ;; Wrong type for :external_id (number)
      {:name "pod-name"
       :external_id 123}

      ;; Wrong type for :external_id (nil)
      {:name "pod-name"
       :external_id nil}

      ;; Extra field not allowed (closed schema)
      {:name "pod-name"
       :external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"
       :extra_field "not allowed"}

      {:name "pod-name"
       :external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"
       :status "Running"}

      ;; Not a map
      "not a map"

      123

      nil

      [#uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"])))

(deftest test-AnalysisPodList
  (testing "valid analysis pod list"
    (are [obj]
        (valid? analyses/AnalysisPodList obj)

      ;; Empty pod list
      {:pods []}

      ;; Single pod
      {:pods [{:name "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05-79c44695b5-v8s4w"
               :external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"}]}

      ;; Multiple pods
      {:pods [{:name "pod-1-abc123-xyz"
               :external_id #uuid "11111111-1111-1111-1111-111111111111"}
              {:name "pod-2-def456-uvw"
               :external_id #uuid "22222222-2222-2222-2222-222222222222"}
              {:name "pod-3-ghi789-rst"
               :external_id #uuid "33333333-3333-3333-3333-333333333333"}]}

      ;; Multiple pods with various name formats
      {:pods [{:name "short"
               :external_id #uuid "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"}
              {:name "very-long-pod-name-with-many-hyphens-12345678-90ab-cdef-1234-567890abcdef"
               :external_id #uuid "bbbbbbbb-cccc-dddd-eeee-ffffffffffff"}
              {:name ""
               :external_id #uuid "cccccccc-dddd-eeee-ffff-000000000000"}]}))

  (testing "invalid analysis pod list"
    (are [obj]
        (not (valid? analyses/AnalysisPodList obj))

      ;; Empty map (missing required :pods field)
      {}

      ;; Wrong field name
      {:pod-list []}

      ;; :pods is not a vector (string instead)
      {:pods "not a vector"}

      ;; :pods is not a vector (single pod instead)
      {:pods {:name "pod-name"
              :external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"}}

      ;; :pods is nil
      {:pods nil}

      ;; Invalid pod in list (missing :name)
      {:pods [{:external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"}]}

      ;; Invalid pod in list (missing :external_id)
      {:pods [{:name "pod-name"}]}

      ;; Invalid pod in list (wrong type for :name)
      {:pods [{:name 123
               :external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"}]}

      ;; Invalid pod in list (wrong type for :external_id)
      {:pods [{:name "pod-name"
               :external_id "not-a-uuid"}]}

      ;; Mix of valid and invalid pods
      {:pods [{:name "valid-pod"
               :external_id #uuid "11111111-1111-1111-1111-111111111111"}
              {:name "invalid-pod"}]}

      ;; Pod with extra field (closed schema)
      {:pods [{:name "pod-name"
               :external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"
               :extra_field "not allowed"}]}

      ;; Extra field in pod list (closed schema)
      {:pods []
       :extra_field "not allowed"}

      ;; Not a map
      "not a map"

      123

      nil

      [{:name "pod-name"
        :external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"}])))

(deftest test-AnalysisPodLogParameters
  (testing "valid pod log parameters"
    (are [obj]
        (valid? analyses/AnalysisPodLogParameters obj)

      ;; Empty map (all fields optional)
      {}

      ;; Individual fields
      {:previous true}
      {:previous false}
      {:since 3600}
      {:since 0}
      {:since 86400}
      {:since-time "1763156075"}
      {:since-time "0"}
      {:tail-lines 100}
      {:tail-lines 1}
      {:tail-lines 0}
      {:timestamps true}
      {:timestamps false}
      {:container "analysis"}
      {:container "init"}
      {:container ""}

      ;; Various combinations of fields
      {:previous true :timestamps true}
      {:since 3600 :tail-lines 100}
      {:since-time "1763156075" :timestamps true}
      {:tail-lines 100 :container "analysis"}
      {:previous true :since 3600 :timestamps true}
      {:since 3600 :tail-lines 100 :container "analysis"}
      {:previous false :timestamps false :container "init"}

      ;; All fields present
      {:previous true
       :since 3600
       :since-time "1763156075"
       :tail-lines 100
       :timestamps true
       :container "analysis"}

      ;; All fields with different values
      {:previous false
       :since 7200
       :since-time "1763156000"
       :tail-lines 50
       :timestamps false
       :container "init"}

      ;; Edge case: large values
      {:since 999999999}
      {:tail-lines 999999999}
      {:since-time "9999999999"}

      ;; Empty string for container
      {:container ""}

      ;; Complex container names
      {:container "analysis-sidecar"}
      {:container "log-collector"}))

  (testing "invalid pod log parameters"
    (are [obj]
        (not (valid? analyses/AnalysisPodLogParameters obj))

      ;; Wrong types for boolean fields
      {:previous "true"}
      {:previous 1}
      {:previous nil}
      {:timestamps "false"}
      {:timestamps 0}

      ;; Wrong types for integer fields
      {:since "3600"}
      {:since 3600.5}
      {:since true}
      {:since nil}
      {:tail-lines "100"}
      {:tail-lines 100.5}
      {:tail-lines false}

      ;; Wrong types for string fields
      {:since-time 1763156075}
      {:since-time true}
      {:since-time nil}
      {:container 123}
      {:container true}
      {:container nil}

      ;; Extra/unknown fields (closed schema)
      {:previous true
       :extra-field "not allowed"}
      {:since 3600
       :unknown-field "value"}
      {:timestamps true
       :additional "field"}
      {:container "analysis"
       :pod-name "some-pod"}

      ;; Not a map
      "not a map"
      123
      true
      nil
      []
      [:previous true])))

(deftest test-AnalysisPodLogEntry
  (testing "valid analysis pod log entry"
    (are [obj]
        (valid? analyses/AnalysisPodLogEntry obj)

      ;; Minimal valid log entry with required fields and empty lines
      {:since_time "1763156649"
       :lines []}

      ;; Single line
      {:since_time "1763156649"
       :lines ["[I 2025-11-14 18:24:35.960 ServerApp] Jupyter Server 2.15.0 is running at:"]}

      ;; Multiple lines (from schema example)
      {:since_time "1763156649"
       :lines ["[I 2025-11-14 18:24:35.960 ServerApp] Jupyter Server 2.15.0 is running at:"
               "[I 2025-11-14 18:24:35.960 ServerApp] http://a9e6f09ad:8888/lab"
               "[I 2025-11-14 18:24:35.960 ServerApp]     http://127.0.0.1:8888/lab"]}

      ;; Many lines
      {:since_time "1763156649"
       :lines ["Line 1" "Line 2" "Line 3" "Line 4" "Line 5" "Line 6" "Line 7" "Line 8" "Line 9" "Line 10"]}

      ;; Empty string line
      {:since_time "1763156649"
       :lines [""]}

      ;; Multiple empty string lines
      {:since_time "1763156649"
       :lines ["" "" ""]}

      ;; Whitespace-only lines
      {:since_time "1763156649"
       :lines ["   " "\t" "  \n  "]}

      ;; Long line
      {:since_time "1763156649"
       :lines ["This is a very long log line that contains a lot of text and goes on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on"]}

      ;; Special characters in lines
      {:since_time "1763156649"
       :lines ["!@#$%^&*()_+-=[]{}|;':\",./<>?" "Special chars: \n\t\r" "Unicode: \u00e9\u00f1\u00fc"]}

      ;; JSON-like content in lines
      {:since_time "1763156649"
       :lines ["{\"key\": \"value\", \"number\": 123}" "[1, 2, 3, 4, 5]"]}

      ;; XML-like content in lines
      {:since_time "1763156649"
       :lines ["<tag>content</tag>" "<root><child>value</child></root>"]}

      ;; Stack trace-like lines
      {:since_time "1763156649"
       :lines ["Exception in thread \"main\" java.lang.NullPointerException"
               "  at com.example.MyClass.myMethod(MyClass.java:42)"
               "  at com.example.Main.main(Main.java:10)"]}

      ;; Mixed content
      {:since_time "1763156649"
       :lines ["Normal log line"
               ""
               "Another line with !@#$ special chars"
               "Line with numbers: 12345"]}

      ;; Different since_time formats
      {:since_time "0"
       :lines ["Line 1"]}

      {:since_time "9999999999"
       :lines ["Line 1"]}

      ;; Empty string since_time (strings allow empty)
      {:since_time ""
       :lines ["Line 1"]}))

  (testing "invalid analysis pod log entry"
    (are [obj]
        (not (valid? analyses/AnalysisPodLogEntry obj))

      ;; Empty map (missing required fields)
      {}

      ;; Missing :since_time
      {:lines ["Line 1"]}

      ;; Missing :lines
      {:since_time "1763156649"}

      ;; Wrong type for :since_time (number instead of string)
      {:since_time 1763156649
       :lines ["Line 1"]}

      ;; Wrong type for :since_time (nil)
      {:since_time nil
       :lines ["Line 1"]}

      ;; Wrong type for :since_time (boolean)
      {:since_time true
       :lines ["Line 1"]}

      ;; Wrong type for :since_time (keyword)
      {:since_time :timestamp
       :lines ["Line 1"]}

      ;; Wrong type for :lines (not a vector)
      {:since_time "1763156649"
       :lines "not a vector"}

      ;; Wrong type for :lines (string instead of vector)
      {:since_time "1763156649"
       :lines "Line 1, Line 2"}

      ;; Wrong type for :lines (map)
      {:since_time "1763156649"
       :lines {:line "value"}}

      ;; Wrong type for :lines (number)
      {:since_time "1763156649"
       :lines 123}

      ;; Wrong type for :lines (nil)
      {:since_time "1763156649"
       :lines nil}

      ;; Vector containing non-strings (numbers)
      {:since_time "1763156649"
       :lines [123 456]}

      ;; Vector containing non-strings (booleans)
      {:since_time "1763156649"
       :lines [true false]}

      ;; Vector containing non-strings (nil)
      {:since_time "1763156649"
       :lines [nil]}

      ;; Vector containing non-strings (keywords)
      {:since_time "1763156649"
       :lines [:keyword]}

      ;; Vector containing non-strings (maps)
      {:since_time "1763156649"
       :lines [{:key "value"}]}

      ;; Vector containing non-strings (vectors)
      {:since_time "1763156649"
       :lines [["nested"]]}

      ;; Mixed types in vector
      {:since_time "1763156649"
       :lines ["valid string" 123 "another string"]}

      {:since_time "1763156649"
       :lines ["valid" nil "strings"]}

      {:since_time "1763156649"
       :lines ["string" true false]}

      ;; Extra field not allowed (closed schema)
      {:since_time "1763156649"
       :lines ["Line 1"]
       :extra_field "not allowed"}

      {:since_time "1763156649"
       :lines ["Line 1"]
       :pod_name "some-pod"}

      {:since_time "1763156649"
       :lines ["Line 1"]
       :timestamp 1763156649}

      ;; Not a map
      "not a map"

      123

      true

      nil

      []

      ["Line 1" "Line 2"])))

(deftest test-AnalysisTimeLimit
  (testing "valid analysis time limit"
    (are [obj]
        (valid? analyses/AnalysisTimeLimit obj)

      ;; Example from schema
      {:time_limit "1763157634"}

      ;; String "null" when time limit isn't set
      {:time_limit "null"}

      ;; Zero timestamp
      {:time_limit "0"}

      ;; Large timestamp
      {:time_limit "9999999999"}

      ;; Very large timestamp
      {:time_limit "99999999999999"}

      ;; String with leading zeros
      {:time_limit "0001763157634"}

      ;; Empty string (strings allow empty)
      {:time_limit ""}

      ;; Whitespace-only string
      {:time_limit "   "}

      ;; Arbitrary string values
      {:time_limit "not a number"}

      {:time_limit "some random text"}

      {:time_limit "NULL"}

      {:time_limit "Null"}

      ;; String with special characters
      {:time_limit "time-limit-value!@#$"}))

  (testing "invalid analysis time limit"
    (are [obj]
        (not (valid? analyses/AnalysisTimeLimit obj))

      ;; Empty map (missing required :time_limit)
      {}

      ;; Missing :time_limit field
      {:something_else "1763157634"}

      ;; Wrong type for :time_limit (number instead of string)
      {:time_limit 1763157634}

      {:time_limit 0}

      {:time_limit 123456789}

      ;; Wrong type for :time_limit (nil)
      {:time_limit nil}

      ;; Wrong type for :time_limit (boolean)
      {:time_limit true}

      {:time_limit false}

      ;; Wrong type for :time_limit (keyword)
      {:time_limit :null}

      {:time_limit :timestamp}

      ;; Wrong type for :time_limit (vector)
      {:time_limit ["1763157634"]}

      ;; Wrong type for :time_limit (map)
      {:time_limit {:value "1763157634"}}

      ;; Extra field not allowed (closed schema)
      {:time_limit "1763157634"
       :extra_field "not allowed"}

      {:time_limit "1763157634"
       :timestamp 1763157634}

      {:time_limit "null"
       :limit_type "soft"}

      ;; Not a map
      "not a map"

      123

      true

      false

      nil

      []

      ["1763157634"])))

(deftest test-ConcurrentJobLimitListItem
  (testing "valid concurrent job limit list item"
    (are [obj]
        (valid? analyses/ConcurrentJobLimitListItem obj)

      ;; Minimal valid item with only required fields (:concurrent_jobs and :is_default)
      {:concurrent_jobs 2
       :is_default false}

      {:concurrent_jobs 1
       :is_default true}

      ;; With username
      {:username "janedoe"
       :concurrent_jobs 2
       :is_default false}

      {:username "johndoe"
       :concurrent_jobs 5
       :is_default false}

      ;; Default setting (no username, is_default true)
      {:concurrent_jobs 3
       :is_default true}

      ;; Edge case: 0 concurrent jobs
      {:concurrent_jobs 0
       :is_default false}

      {:username "testuser"
       :concurrent_jobs 0
       :is_default false}

      ;; Edge case: very large job count
      {:concurrent_jobs 999999
       :is_default false}

      {:username "poweruser"
       :concurrent_jobs 100
       :is_default false}

      ;; Edge case: empty username string
      {:username ""
       :concurrent_jobs 2
       :is_default false}

      ;; Various usernames
      {:username "user.with.dots"
       :concurrent_jobs 3
       :is_default false}

      {:username "user_with_underscores"
       :concurrent_jobs 4
       :is_default false}

      {:username "user-with-dashes"
       :concurrent_jobs 5
       :is_default false}

      {:username "a"
       :concurrent_jobs 1
       :is_default false}

      ;; Various boolean values for is_default
      {:username "user1"
       :concurrent_jobs 2
       :is_default true}

      {:username "user2"
       :concurrent_jobs 3
       :is_default false}

      ;; Different job counts
      {:concurrent_jobs 1
       :is_default false}

      {:concurrent_jobs 10
       :is_default true}

      {:concurrent_jobs 50
       :is_default false}

      {:concurrent_jobs 1000
       :is_default true}

      ;; Admin user with high limit
      {:username "admin"
       :concurrent_jobs 100
       :is_default false}

      ;; Default limit scenarios
      {:concurrent_jobs 5
       :is_default true}))

  (testing "invalid concurrent job limit list item"
    (are [obj]
        (not (valid? analyses/ConcurrentJobLimitListItem obj))

      ;; Empty map (missing required :concurrent_jobs and :is_default)
      {}

      ;; Only username (missing required :concurrent_jobs and :is_default)
      {:username "janedoe"}

      ;; Only concurrent_jobs (missing required :is_default)
      {:concurrent_jobs 2}

      {:concurrent_jobs 5}

      ;; Only is_default (missing required :concurrent_jobs)
      {:is_default true}

      {:is_default false}

      ;; Username and is_default without concurrent_jobs
      {:username "janedoe"
       :is_default false}

      ;; Username and concurrent_jobs without is_default
      {:username "janedoe"
       :concurrent_jobs 2}

      {:username "johndoe"
       :concurrent_jobs 5}

      ;; Wrong type for :concurrent_jobs (string)
      {:concurrent_jobs "2"
       :is_default false}

      {:username "janedoe"
       :concurrent_jobs "5"
       :is_default false}

      ;; Wrong type for :concurrent_jobs (float)
      {:concurrent_jobs 2.5
       :is_default false}

      {:concurrent_jobs 3.14159
       :is_default true}

      ;; Wrong type for :concurrent_jobs (boolean)
      {:concurrent_jobs true
       :is_default false}

      {:concurrent_jobs false
       :is_default true}

      ;; Wrong type for :concurrent_jobs (nil)
      {:concurrent_jobs nil
       :is_default false}

      ;; Wrong type for :concurrent_jobs (keyword)
      {:concurrent_jobs :two
       :is_default false}

      ;; Wrong type for :concurrent_jobs (vector)
      {:concurrent_jobs [2]
       :is_default false}

      ;; Wrong type for :concurrent_jobs (map)
      {:concurrent_jobs {:value 2}
       :is_default false}

      ;; Wrong type for :username (number)
      {:username 123
       :concurrent_jobs 2
       :is_default false}

      ;; Wrong type for :username (boolean)
      {:username true
       :concurrent_jobs 2
       :is_default false}

      {:username false
       :concurrent_jobs 2
       :is_default true}

      ;; Wrong type for :username (nil)
      {:username nil
       :concurrent_jobs 2
       :is_default false}

      ;; Wrong type for :username (keyword)
      {:username :janedoe
       :concurrent_jobs 2
       :is_default false}

      ;; Wrong type for :username (vector)
      {:username ["janedoe"]
       :concurrent_jobs 2
       :is_default false}

      ;; Wrong type for :username (map)
      {:username {:name "janedoe"}
       :concurrent_jobs 2
       :is_default false}

      ;; Wrong type for :is_default (string)
      {:concurrent_jobs 2
       :is_default "true"}

      {:concurrent_jobs 2
       :is_default "false"}

      ;; Wrong type for :is_default (number)
      {:concurrent_jobs 2
       :is_default 1}

      {:concurrent_jobs 2
       :is_default 0}

      ;; Wrong type for :is_default (nil)
      {:concurrent_jobs 2
       :is_default nil}

      ;; Wrong type for :is_default (keyword)
      {:concurrent_jobs 2
       :is_default :true}

      {:concurrent_jobs 2
       :is_default :false}

      ;; Wrong type for :is_default (vector)
      {:concurrent_jobs 2
       :is_default [true]}

      ;; Wrong type for :is_default (map)
      {:concurrent_jobs 2
       :is_default {:value true}}

      ;; Extra field not allowed (closed schema)
      {:concurrent_jobs 2
       :is_default false
       :extra_field "not allowed"}

      {:username "janedoe"
       :concurrent_jobs 2
       :is_default false
       :extra_field "not allowed"}

      {:username "janedoe"
       :concurrent_jobs 2
       :is_default false
       :limit_type "user"}

      {:concurrent_jobs 2
       :is_default false
       :user "janedoe"}

      {:concurrent_jobs 2
       :is_default false
       :default true}

      ;; Not a map
      "not a map"

      123

      true

      false

      nil

      []

      ["janedoe" 2 false])))

(deftest test-ConcurrentJobLimits
  (testing "valid concurrent job limits"
    (are [obj]
        (valid? analyses/ConcurrentJobLimits obj)

      ;; Empty list
      {:limits []}

      ;; Single item - default setting
      {:limits [{:concurrent_jobs 5
                 :is_default true}]}

      ;; Single item - user setting
      {:limits [{:username "janedoe"
                 :concurrent_jobs 3
                 :is_default false}]}

      ;; Multiple items - mix of default and user settings
      {:limits [{:concurrent_jobs 5
                 :is_default true}
                {:username "janedoe"
                 :concurrent_jobs 3
                 :is_default false}]}

      {:limits [{:username "alice"
                 :concurrent_jobs 2
                 :is_default false}
                {:username "bob"
                 :concurrent_jobs 4
                 :is_default false}
                {:concurrent_jobs 3
                 :is_default true}]}

      ;; Multiple items - all user settings
      {:limits [{:username "alice"
                 :concurrent_jobs 2
                 :is_default false}
                {:username "bob"
                 :concurrent_jobs 4
                 :is_default false}
                {:username "charlie"
                 :concurrent_jobs 6
                 :is_default false}]}

      ;; Multiple items - various usernames
      {:limits [{:username "user.with.dots"
                 :concurrent_jobs 3
                 :is_default false}
                {:username "user_with_underscores"
                 :concurrent_jobs 4
                 :is_default false}
                {:username "user-with-dashes"
                 :concurrent_jobs 5
                 :is_default false}]}

      ;; Multiple items - edge case values
      {:limits [{:username "poweruser"
                 :concurrent_jobs 100
                 :is_default false}
                {:username "restricteduser"
                 :concurrent_jobs 0
                 :is_default false}
                {:concurrent_jobs 5
                 :is_default true}]}

      ;; Items with and without usernames
      {:limits [{:concurrent_jobs 10
                 :is_default true}
                {:username "user1"
                 :concurrent_jobs 5
                 :is_default false}
                {:username "user2"
                 :concurrent_jobs 8
                 :is_default false}]}

      ;; Large list
      {:limits [{:concurrent_jobs 5
                 :is_default true}
                {:username "user1"
                 :concurrent_jobs 1
                 :is_default false}
                {:username "user2"
                 :concurrent_jobs 2
                 :is_default false}
                {:username "user3"
                 :concurrent_jobs 3
                 :is_default false}
                {:username "user4"
                 :concurrent_jobs 4
                 :is_default false}
                {:username "user5"
                 :concurrent_jobs 5
                 :is_default false}]}

      ;; Single default item
      {:limits [{:is_default true
                 :concurrent_jobs 10}]}))

  (testing "invalid concurrent job limits"
    (are [obj]
        (not (valid? analyses/ConcurrentJobLimits obj))

      ;; Empty map (missing required :limits field)
      {}

      ;; Wrong type for :limits (not a vector) - string
      {:limits "not a vector"}

      ;; Wrong type for :limits - number
      {:limits 5}

      ;; Wrong type for :limits - boolean
      {:limits true}

      {:limits false}

      ;; Wrong type for :limits - nil
      {:limits nil}

      ;; Wrong type for :limits - keyword
      {:limits :limits}

      ;; Wrong type for :limits - map
      {:limits {:username "janedoe"
                :concurrent_jobs 2
                :is_default false}}

      ;; Vector containing invalid items - not maps
      {:limits ["not a map"]}

      {:limits [123]}

      {:limits [true]}

      {:limits [nil]}

      {:limits [:keyword]}

      ;; Vector containing invalid item structures
      {:limits [{:concurrent_jobs 2}]}

      {:limits [{:is_default true}]}

      {:limits [{:username "janedoe"}]}

      {:limits [{:username "janedoe"
                 :concurrent_jobs 2}]}

      {:limits [{:username "janedoe"
                 :is_default false}]}

      ;; Vector containing mix of valid and invalid items
      {:limits [{:concurrent_jobs 5
                 :is_default true}
                {:concurrent_jobs 3}]}

      {:limits [{:concurrent_jobs 5
                 :is_default true}
                "not a map"]}

      {:limits [{:username "alice"
                 :concurrent_jobs 2
                 :is_default false}
                {:username "bob"
                 :concurrent_jobs "not an int"
                 :is_default false}]}

      ;; Vector containing items with wrong types
      {:limits [{:concurrent_jobs "5"
                 :is_default true}]}

      {:limits [{:username 123
                 :concurrent_jobs 5
                 :is_default false}]}

      {:limits [{:concurrent_jobs 5
                 :is_default "true"}]}

      {:limits [{:concurrent_jobs 5.5
                 :is_default true}]}

      ;; Extra fields (closed schema)
      {:limits []
       :extra_field "not allowed"}

      {:limits [{:concurrent_jobs 5
                 :is_default true}]
       :total_count 1}

      {:limits [{:concurrent_jobs 5
                 :is_default true}]
       :metadata {:foo "bar"}}

      ;; Vector containing items with extra fields
      {:limits [{:concurrent_jobs 5
                 :is_default true
                 :extra_field "not allowed"}]}

      {:limits [{:username "janedoe"
                 :concurrent_jobs 2
                 :is_default false
                 :limit_type "user"}]}

      ;; Not a map
      "not a map"

      123

      true

      false

      nil

      []

      [{:concurrent_jobs 5
        :is_default true}])))

(deftest test-AnalysisCount
  (testing "valid analysis counts"
    (are [obj]
        (valid? analyses/AnalysisCount obj)

      ;; Valid count with typical status
      {:count 27
       :status "Completed"}

      ;; Zero count
      {:count 0
       :status "Running"}

      ;; Count of 1
      {:count 1
       :status "Failed"}

      ;; Large count
      {:count 999999
       :status "Submitted"}

      ;; Negative count (valid as int type allows negatives)
      {:count -1
       :status "Canceled"}

      ;; Different status values
      {:count 5
       :status "Running"}

      {:count 10
       :status "Failed"}

      {:count 3
       :status "Submitted"}

      {:count 42
       :status "Canceled"}

      {:count 7
       :status "Pending"}

      {:count 15
       :status "Held"}

      {:count 2
       :status "Removed"}

      ;; Empty string status (string type allows this)
      {:count 8
       :status ""}

      ;; Status with special characters
      {:count 3
       :status "Status-With-Dashes"}

      {:count 4
       :status "Status_With_Underscores"}

      {:count 5
       :status "Status With Spaces"}))

  (testing "invalid analysis counts"
    (are [obj]
        (not (valid? analyses/AnalysisCount obj))

      ;; Empty map (missing required fields)
      {}

      ;; Missing :count
      {:status "Completed"}

      ;; Missing :status
      {:count 27}

      ;; Wrong type for :count (string instead of int)
      {:count "27"
       :status "Completed"}

      ;; Wrong type for :count (double instead of int)
      {:count 27.5
       :status "Completed"}

      ;; Wrong type for :count (nil)
      {:count nil
       :status "Completed"}

      ;; Wrong type for :status (int instead of string)
      {:count 27
       :status 123}

      ;; Wrong type for :status (boolean)
      {:count 27
       :status true}

      ;; Wrong type for :status (nil)
      {:count 27
       :status nil}

      ;; Wrong type for :status (keyword)
      {:count 27
       :status :Completed}

      ;; Extra fields (closed schema)
      {:count 27
       :status "Completed"
       :extra-field "not allowed"}

      {:count 27
       :status "Completed"
       :description "Should not be here"}

      ;; Not a map
      "not a map"

      123

      true

      false

      nil

      []

      [{:count 27
        :status "Completed"}])))

(deftest test-AnalysisStats
  (testing "valid analysis stats"
    (are [obj]
        (valid? analyses/AnalysisStats obj)

      ;; Empty list
      {:status-count []}

      ;; Single status count
      {:status-count [{:count 27
                       :status "Completed"}]}

      ;; Multiple status counts (different job statuses)
      {:status-count [{:count 27
                       :status "Completed"}
                      {:count 5
                       :status "Running"}]}

      ;; Multiple status counts with various statuses
      {:status-count [{:count 27
                       :status "Completed"}
                      {:count 5
                       :status "Running"}
                      {:count 10
                       :status "Failed"}
                      {:count 3
                       :status "Submitted"}]}

      ;; All common job statuses
      {:status-count [{:count 100
                       :status "Completed"}
                      {:count 15
                       :status "Running"}
                      {:count 8
                       :status "Failed"}
                      {:count 25
                       :status "Submitted"}
                      {:count 12
                       :status "Canceled"}
                      {:count 7
                       :status "Pending"}
                      {:count 5
                       :status "Held"}
                      {:count 2
                       :status "Removed"}]}

      ;; Zero counts
      {:status-count [{:count 0
                       :status "Running"}
                      {:count 0
                       :status "Failed"}]}

      ;; Large count values
      {:status-count [{:count 999999
                       :status "Completed"}
                      {:count 123456
                       :status "Running"}]}

      ;; Single item with zero count
      {:status-count [{:count 0
                       :status "Submitted"}]}))

  (testing "invalid analysis stats"
    (are [obj]
        (not (valid? analyses/AnalysisStats obj))

      ;; Empty map (missing required :status-count field)
      {}

      ;; Missing :status-count field
      {:other-field "value"}

      ;; Wrong type for :status-count (not a vector)
      {:status-count {:count 27
                      :status "Completed"}}

      {:status-count "not a vector"}

      {:status-count 123}

      {:status-count true}

      {:status-count nil}

      ;; Vector containing invalid AnalysisCount items
      ;; Missing :count
      {:status-count [{:status "Completed"}]}

      ;; Missing :status
      {:status-count [{:count 27}]}

      ;; Wrong type for :count
      {:status-count [{:count "27"
                       :status "Completed"}]}

      ;; Wrong type for :status
      {:status-count [{:count 27
                       :status 123}]}

      ;; Extra fields in AnalysisCount (closed schema)
      {:status-count [{:count 27
                       :status "Completed"
                       :extra-field "not allowed"}]}

      ;; Mix of valid and invalid items
      {:status-count [{:count 27
                       :status "Completed"}
                      {:count "invalid"
                       :status "Running"}]}

      {:status-count [{:count 27
                       :status "Completed"}
                      {:status "Missing count"}]}

      ;; Vector containing non-map values
      {:status-count ["not a map"]}

      {:status-count [123]}

      {:status-count [true]}

      {:status-count [nil]}

      {:status-count [{:count 27
                       :status "Completed"}
                      "invalid item"]}

      ;; Extra fields at top level (closed schema)
      {:status-count []
       :extra-field "not allowed"}

      {:status-count [{:count 27
                       :status "Completed"}]
       :description "Should not be here"}

      ;; Not a map
      "not a map"

      123

      true

      false

      nil

      []

      [{:status-count [{:count 27
                        :status "Completed"}]}])))

(deftest test-AnalysisStatParams
  (testing "valid analysis stat params"
    (are [obj]
        (valid? analyses/AnalysisStatParams obj)

      ;; Empty map (all fields optional)
      {}

      ;; Individual fields
      {:include-hidden true}

      {:include-hidden false}

      {:include-deleted true}

      {:include-deleted false}

      {:filter "[{\"field\":\"ownership\",\"value\":\"all\"}]"}

      ;; Combinations of fields
      {:include-hidden true
       :include-deleted false}

      {:include-hidden false
       :include-deleted true}

      {:include-hidden true
       :filter "[{\"field\":\"ownership\",\"value\":\"all\"}]"}

      {:include-deleted true
       :filter "[{\"field\":\"status\",\"value\":\"Running\"}]"}

      {:include-hidden true
       :include-deleted true
       :filter "[{\"field\":\"ownership\",\"value\":\"mine\"}]"}

      {:include-hidden false
       :include-deleted false
       :filter "[{\"field\":\"app_id\",\"value\":\"12345\"}]"}

      ;; Different filter values
      {:filter ""}

      {:filter "[]"}

      {:filter "[{\"field\":\"name\",\"value\":\"test\"}]"}

      {:filter "[{\"field\":\"ownership\",\"value\":\"all\"},{\"field\":\"status\",\"value\":\"Completed\"}]"}

      ;; Complex filter JSON string
      {:filter "[{\"field\":\"app_name\",\"value\":\"My Analysis App\"}]"}))

  (testing "invalid analysis stat params"
    (are [obj]
        (not (valid? analyses/AnalysisStatParams obj))

      ;; Wrong type for :include-hidden
      {:include-hidden "true"}

      {:include-hidden 1}

      {:include-hidden nil}

      {:include-hidden "false"}

      ;; Wrong type for :include-deleted
      {:include-deleted "true"}

      {:include-deleted 1}

      {:include-deleted nil}

      {:include-deleted "false"}

      ;; Wrong type for :filter
      {:filter 123}

      {:filter true}

      {:filter false}

      {:filter nil}

      {:filter []}

      {:filter {}}

      {:filter ["field" "value"]}

      ;; Extra fields (closed schema)
      {:extra-field "not allowed"}

      {:include-hidden true
       :extra-field "not allowed"}

      {:include-deleted false
       :unknown-field "value"}

      {:filter "[]"
       :additional-param true}

      {:include-hidden true
       :include-deleted false
       :filter "[]"
       :extra "field"}

      ;; Not a map
      "not a map"

      123

      true

      false

      nil

      []

      [{:include-hidden true}])))
