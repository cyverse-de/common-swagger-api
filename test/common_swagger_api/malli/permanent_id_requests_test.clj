(ns common-swagger-api.malli.permanent-id-requests-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.permanent-id-requests :as pir]
            [malli.core :as malli]
            [malli.error :as me]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-PermanentID
  (testing "PermanentID validation"
    (testing "valid params"
      ;; Empty map is valid since permanent_id is optional
      (is (valid? pir/PermanentID {}))

      ;; Valid permanent_id with DOI example from schema
      (is (valid? pir/PermanentID
                  {:permanent_id "https://doi.org/10.1093/nar/gkv416"}))

      ;; Valid permanent_id with other string values
      (is (valid? pir/PermanentID
                  {:permanent_id "https://doi.org/10.5061/dryad.12345"}))

      (is (valid? pir/PermanentID
                  {:permanent_id "ark:/12345/abcdef"}))

      ;; Single character string is valid
      (is (valid? pir/PermanentID
                  {:permanent_id "a"}))

      ;; Empty string is valid with current schema (uses :string, not NonBlankString)
      (is (valid? pir/PermanentID
                  {:permanent_id ""}))

      ;; Whitespace-only string is valid with current schema
      (is (valid? pir/PermanentID
                  {:permanent_id "   "})))

    (testing "invalid params"
      ;; Non-string values not allowed
      (is (not (valid? pir/PermanentID
                       {:permanent_id 123})))

      (is (not (valid? pir/PermanentID
                       {:permanent_id nil})))

      (is (not (valid? pir/PermanentID
                       {:permanent_id true})))

      (is (not (valid? pir/PermanentID
                       {:permanent_id ["https://doi.org/10.1093/nar/gkv416"]})))

      (is (not (valid? pir/PermanentID
                       {:permanent_id {:url "https://doi.org/10.1093/nar/gkv416"}})))

      ;; Extra fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentID
                       {:permanent_id "https://doi.org/10.1093/nar/gkv416"
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentID
                       {:unknown-field "value"}))))))

(deftest test-PermanentIDRequestOrigPath
  (testing "PermanentIDRequestOrigPath validation"
    (testing "valid params"
      ;; Empty map is valid since original_path is optional
      (is (valid? pir/PermanentIDRequestOrigPath {}))

      ;; Valid original_path with file path examples
      (is (valid? pir/PermanentIDRequestOrigPath
                  {:original_path "/iplant/home/username/analyses/example-analysis/output"}))

      (is (valid? pir/PermanentIDRequestOrigPath
                  {:original_path "/iplant/home/shared/commons_repo/curated/dataset_v1.0"}))

      (is (valid? pir/PermanentIDRequestOrigPath
                  {:original_path "/iplant/home/researcher/data/2023/experiment_results"}))

      ;; Single character string is valid
      (is (valid? pir/PermanentIDRequestOrigPath
                  {:original_path "/"}))

      ;; Empty string is valid with current schema (uses :string, not NonBlankString)
      (is (valid? pir/PermanentIDRequestOrigPath
                  {:original_path ""}))

      ;; Whitespace-only string is valid with current schema
      (is (valid? pir/PermanentIDRequestOrigPath
                  {:original_path "   "})))

    (testing "invalid params"
      ;; Non-string values not allowed
      (is (not (valid? pir/PermanentIDRequestOrigPath
                       {:original_path 123})))

      (is (not (valid? pir/PermanentIDRequestOrigPath
                       {:original_path nil})))

      (is (not (valid? pir/PermanentIDRequestOrigPath
                       {:original_path true})))

      (is (not (valid? pir/PermanentIDRequestOrigPath
                       {:original_path ["/iplant/home/username/data"]})))

      (is (not (valid? pir/PermanentIDRequestOrigPath
                       {:original_path {:path "/iplant/home/username/data"}})))

      ;; Extra fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequestOrigPath
                       {:original_path "/iplant/home/username/data"
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequestOrigPath
                       {:unknown-field "value"}))))))

(deftest test-PermanentIDRequest
  (testing "PermanentIDRequest validation"
    (testing "valid params"
      ;; Valid type with DOI example from schema
      (is (valid? pir/PermanentIDRequest
                  {:type "DOI"}))

      ;; Valid type with other ID types
      (is (valid? pir/PermanentIDRequest
                  {:type "ARK"}))

      (is (valid? pir/PermanentIDRequest
                  {:type "Handle"}))

      ;; Single character string is valid
      (is (valid? pir/PermanentIDRequest
                  {:type "A"}))

      ;; Empty string is valid with current schema (uses :string, not NonBlankString)
      (is (valid? pir/PermanentIDRequest
                  {:type ""}))

      ;; Whitespace-only string is valid with current schema
      (is (valid? pir/PermanentIDRequest
                  {:type "   "})))

    (testing "invalid params"
      ;; Empty map is invalid since type is required
      (is (not (valid? pir/PermanentIDRequest {})))

      ;; Map with missing type field is invalid
      (is (not (valid? pir/PermanentIDRequest
                       {:other-field "value"})))

      ;; Non-string values not allowed
      (is (not (valid? pir/PermanentIDRequest
                       {:type 123})))

      (is (not (valid? pir/PermanentIDRequest
                       {:type nil})))

      (is (not (valid? pir/PermanentIDRequest
                       {:type true})))

      (is (not (valid? pir/PermanentIDRequest
                       {:type ["DOI"]})))

      (is (not (valid? pir/PermanentIDRequest
                       {:type {:name "DOI"}})))

      ;; Extra fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequest
                       {:type "DOI"
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequest
                       {:type "DOI"
                        :unknown-field "value"}))))))

(deftest test-PermanentIDRequestBase
  (testing "PermanentIDRequestBase validation"
    (testing "valid params"
      ;; Map with all required fields only
      (is (valid? pir/PermanentIDRequestBase
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"}))

      ;; Map with all fields (required + both optional fields)
      (is (valid? pir/PermanentIDRequestBase
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :permanent_id "https://doi.org/10.1093/nar/gkv416"
                   :original_path "/iplant/home/username/analyses/example-analysis/output"}))

      ;; Map with required fields + :permanent_id only
      (is (valid? pir/PermanentIDRequestBase
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :permanent_id "https://doi.org/10.1093/nar/gkv416"}))

      ;; Map with required fields + :original_path only
      (is (valid? pir/PermanentIDRequestBase
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :original_path "/iplant/home/username/analyses/example-analysis/output"}))

      ;; Map with different valid type values
      (is (valid? pir/PermanentIDRequestBase
                  {:type "ARK"
                   :id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                   :requested_by "johndoe"}))

      (is (valid? pir/PermanentIDRequestBase
                  {:type "Handle"
                   :id #uuid "00000000-0000-0000-0000-000000000000"
                   :requested_by "researcher"}))

      ;; Map with different valid permanent_id values
      (is (valid? pir/PermanentIDRequestBase
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :permanent_id "https://doi.org/10.5061/dryad.12345"}))

      (is (valid? pir/PermanentIDRequestBase
                  {:type "ARK"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :permanent_id "ark:/12345/abcdef"}))

      ;; Map with different valid original_path values
      (is (valid? pir/PermanentIDRequestBase
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :original_path "/iplant/home/shared/commons_repo/curated/dataset_v1.0"}))

      (is (valid? pir/PermanentIDRequestBase
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :original_path "/iplant/home/researcher/data/2023/experiment_results"}))

      ;; Map with different valid requested_by values
      (is (valid? pir/PermanentIDRequestBase
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "user123"}))

      (is (valid? pir/PermanentIDRequestBase
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "admin@example.com"}))

      ;; Empty string is valid with current schema (uses :string, not NonBlankString)
      (is (valid? pir/PermanentIDRequestBase
                  {:type ""
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by ""}))

      (is (valid? pir/PermanentIDRequestBase
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :permanent_id ""
                   :original_path ""}))

      ;; Whitespace-only string is valid with current schema
      (is (valid? pir/PermanentIDRequestBase
                  {:type "   "
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "   "})))

    (testing "invalid params"
      ;; Empty map is invalid (missing all required fields)
      (is (not (valid? pir/PermanentIDRequestBase {})))

      ;; Map missing :type (required field)
      (is (not (valid? pir/PermanentIDRequestBase
                       {:id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"})))

      ;; Map missing :id (required field)
      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :requested_by "janedoe"})))

      ;; Map missing :requested_by (required field)
      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"})))

      ;; Map with wrong type for :type field
      (is (not (valid? pir/PermanentIDRequestBase
                       {:type 123
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type nil
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type true
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type ["DOI"]
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type {:name "DOI"}
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"})))

      ;; Map with wrong type for :id field
      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id 123
                        :requested_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id nil
                        :requested_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id true
                        :requested_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id [#uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"]
                        :requested_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id {:uuid #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"}
                        :requested_by "janedoe"})))

      ;; Map with wrong type for :requested_by field
      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by 123})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by nil})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by true})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by ["janedoe"]})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by {:username "janedoe"}})))

      ;; Map with wrong type for :permanent_id optional field
      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :permanent_id 123})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :permanent_id nil})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :permanent_id true})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :permanent_id ["https://doi.org/10.1093/nar/gkv416"]})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :permanent_id {:url "https://doi.org/10.1093/nar/gkv416"}})))

      ;; Map with wrong type for :original_path optional field
      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :original_path 123})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :original_path nil})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :original_path true})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :original_path ["/iplant/home/username/data"]})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :original_path {:path "/iplant/home/username/data"}})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :unknown-field "value"})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :permanent_id "https://doi.org/10.1093/nar/gkv416"
                        :original_path "/iplant/home/username/data"
                        :status "pending"})))

      (is (not (valid? pir/PermanentIDRequestBase
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :additional "field"}))))))

(deftest test-PermanentIDRequestStatusComments
  (testing "PermanentIDRequestStatusComments validation"
    (testing "valid params"
      ;; Empty map is valid since comments is optional
      (is (valid? pir/PermanentIDRequestStatusComments {}))

      ;; Valid comments with example from schema
      (is (valid? pir/PermanentIDRequestStatusComments
                  {:comments "Please fill out the DataCite metadata template for the data set."}))

      ;; Valid comments with other string values
      (is (valid? pir/PermanentIDRequestStatusComments
                  {:comments "Request approved by curator team."}))

      (is (valid? pir/PermanentIDRequestStatusComments
                  {:comments "Additional information needed for DOI registration."}))

      (is (valid? pir/PermanentIDRequestStatusComments
                  {:comments "The dataset meets all requirements for permanent ID assignment."}))

      ;; Single character string is valid
      (is (valid? pir/PermanentIDRequestStatusComments
                  {:comments "A"}))

      ;; Empty string is valid with current schema (uses :string, not NonBlankString)
      (is (valid? pir/PermanentIDRequestStatusComments
                  {:comments ""}))

      ;; Whitespace-only string is valid with current schema
      (is (valid? pir/PermanentIDRequestStatusComments
                  {:comments "   "})))

    (testing "invalid params"
      ;; Non-string values not allowed
      (is (not (valid? pir/PermanentIDRequestStatusComments
                       {:comments 123})))

      (is (not (valid? pir/PermanentIDRequestStatusComments
                       {:comments nil})))

      (is (not (valid? pir/PermanentIDRequestStatusComments
                       {:comments true})))

      (is (not (valid? pir/PermanentIDRequestStatusComments
                       {:comments ["Please fill out the DataCite metadata template for the data set."]})))

      (is (not (valid? pir/PermanentIDRequestStatusComments
                       {:comments {:text "Please fill out the DataCite metadata template for the data set."}})))

      ;; Extra fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequestStatusComments
                       {:comments "Please fill out the DataCite metadata template for the data set."
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequestStatusComments
                       {:unknown-field "value"}))))))

(deftest test-PermanentIDRequestStatusUpdate
  (testing "PermanentIDRequestStatusUpdate validation"
    (testing "valid params"
      ;; Empty map is valid since all three fields (comments, permanent_id, status) are optional
      (is (valid? pir/PermanentIDRequestStatusUpdate {}))

      ;; Valid map with each individual field
      ;; Valid with :comments only
      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:comments "Please fill out the DataCite metadata template for the data set."}))

      ;; Valid with :permanent_id only
      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:permanent_id "https://doi.org/10.1093/nar/gkv416"}))

      ;; Valid with :status only
      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:status "submitted"}))

      ;; Valid maps with combinations of two fields
      ;; Valid with :comments and :permanent_id
      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:comments "Please fill out the DataCite metadata template for the data set."
                   :permanent_id "https://doi.org/10.1093/nar/gkv416"}))

      ;; Valid with :comments and :status
      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:comments "Please fill out the DataCite metadata template for the data set."
                   :status "submitted"}))

      ;; Valid with :permanent_id and :status
      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:permanent_id "https://doi.org/10.1093/nar/gkv416"
                   :status "submitted"}))

      ;; Valid map with all three fields
      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:comments "Please fill out the DataCite metadata template for the data set."
                   :permanent_id "https://doi.org/10.1093/nar/gkv416"
                   :status "submitted"}))

      ;; Valid with different comments values
      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:comments "Request approved by curator team."}))

      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:comments "Additional information needed for DOI registration."}))

      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:comments "The dataset meets all requirements for permanent ID assignment."}))

      ;; Valid with different permanent_id values
      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:permanent_id "https://doi.org/10.5061/dryad.12345"}))

      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:permanent_id "ark:/12345/abcdef"}))

      ;; Valid with different status values
      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:status "approved"}))

      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:status "pending"}))

      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:status "rejected"}))

      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:status "completed"}))

      ;; Single character strings are valid
      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:comments "A"
                   :permanent_id "B"
                   :status "C"}))

      ;; Empty strings are valid with current schema (uses :string, not NonBlankString)
      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:comments ""}))

      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:permanent_id ""}))

      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:status ""}))

      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:comments ""
                   :permanent_id ""
                   :status ""}))

      ;; Whitespace-only strings are valid with current schema
      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:comments "   "}))

      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:permanent_id "   "}))

      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:status "   "}))

      (is (valid? pir/PermanentIDRequestStatusUpdate
                  {:comments "   "
                   :permanent_id "   "
                   :status "   "})))

    (testing "invalid params"
      ;; Map with wrong type for :comments field
      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:comments 123})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:comments nil})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:comments true})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:comments ["Please fill out the DataCite metadata template for the data set."]})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:comments {:text "Please fill out the DataCite metadata template for the data set."}})))

      ;; Map with wrong type for :permanent_id field
      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:permanent_id 123})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:permanent_id nil})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:permanent_id true})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:permanent_id ["https://doi.org/10.1093/nar/gkv416"]})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:permanent_id {:url "https://doi.org/10.1093/nar/gkv416"}})))

      ;; Map with wrong type for :status field
      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:status 123})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:status nil})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:status true})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:status ["submitted"]})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:status {:code "submitted"}})))

      ;; Map with multiple wrong types (combination tests)
      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:comments 123
                        :permanent_id nil
                        :status true})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:comments nil
                        :status 456})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:permanent_id false
                        :status ["approved"]})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:comments "Please fill out the DataCite metadata template for the data set."
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:permanent_id "https://doi.org/10.1093/nar/gkv416"
                        :unknown-field "value"})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:status "submitted"
                        :additional "field"})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:comments "Please fill out the DataCite metadata template for the data set."
                        :permanent_id "https://doi.org/10.1093/nar/gkv416"
                        :status "submitted"
                        :curator "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:unknown-field "value"})))

      (is (not (valid? pir/PermanentIDRequestStatusUpdate
                       {:extra "field"
                        :another "extra"}))))))

(deftest test-PermanentIDRequestStatus
  (testing "PermanentIDRequestStatus validation"
    (testing "valid params"
      ;; Map with only required fields (:status_date and :updated_by)
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"}))

      ;; Map with required fields + :permanent_id only
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :permanent_id "https://doi.org/10.1093/nar/gkv416"}))

      ;; Map with required fields + :comments only
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :comments "Please fill out the DataCite metadata template for the data set."}))

      ;; Map with required fields + :status only
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :status "approved"}))

      ;; Map with required fields + combinations of two optional fields
      ;; Required + :permanent_id + :comments
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :permanent_id "https://doi.org/10.1093/nar/gkv416"
                   :comments "Please fill out the DataCite metadata template for the data set."}))

      ;; Required + :permanent_id + :status
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :permanent_id "https://doi.org/10.1093/nar/gkv416"
                   :status "approved"}))

      ;; Required + :comments + :status
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :comments "Please fill out the DataCite metadata template for the data set."
                   :status "approved"}))

      ;; Map with all fields (required + all optional)
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :permanent_id "https://doi.org/10.1093/nar/gkv416"
                   :comments "Please fill out the DataCite metadata template for the data set."
                   :status "approved"}))

      ;; Map using example values from schema
      (is (valid? pir/PermanentIDRequestStatus
                  {:permanent_id "https://doi.org/10.1093/nar/gkv416"
                   :comments "Please fill out the DataCite metadata template for the data set."
                   :status "approved"
                   :status_date 1762296097000
                   :updated_by "janedoe"}))

      ;; Map with different valid permanent_id values
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :permanent_id "https://doi.org/10.5061/dryad.12345"}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :permanent_id "ark:/12345/abcdef"}))

      ;; Map with different valid comments values
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :comments "Request approved by curator team."}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :comments "Additional information needed for DOI registration."}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :comments "The dataset meets all requirements for permanent ID assignment."}))

      ;; Map with different valid status values
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :status "submitted"}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :status "pending"}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :status "rejected"}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "janedoe"
                   :status "completed"}))

      ;; Map with different valid status_date values
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 0
                   :updated_by "janedoe"}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1609459200000
                   :updated_by "janedoe"}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 9999999999999
                   :updated_by "janedoe"}))

      ;; Map with different valid updated_by values
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "johndoe"}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "admin@example.com"}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "curator123"}))

      ;; Single character strings are valid for optional string fields
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "A"
                   :permanent_id "B"
                   :comments "C"
                   :status "D"}))

      ;; Empty strings are valid with current schema (uses :string, not NonBlankString)
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by ""
                   :permanent_id ""}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by ""
                   :comments ""}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by ""
                   :status ""}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by ""
                   :permanent_id ""
                   :comments ""
                   :status ""}))

      ;; Whitespace-only strings are valid with current schema
      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "   "
                   :permanent_id "   "}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "   "
                   :comments "   "}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "   "
                   :status "   "}))

      (is (valid? pir/PermanentIDRequestStatus
                  {:status_date 1762296097000
                   :updated_by "   "
                   :permanent_id "   "
                   :comments "   "
                   :status "   "})))

    (testing "invalid params"
      ;; Empty map is invalid (missing required fields)
      (is (not (valid? pir/PermanentIDRequestStatus {})))

      ;; Map missing :status_date (required field)
      (is (not (valid? pir/PermanentIDRequestStatus
                       {:updated_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:updated_by "janedoe"
                        :permanent_id "https://doi.org/10.1093/nar/gkv416"
                        :comments "Please fill out the DataCite metadata template for the data set."
                        :status "approved"})))

      ;; Map missing :updated_by (required field)
      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :permanent_id "https://doi.org/10.1093/nar/gkv416"
                        :comments "Please fill out the DataCite metadata template for the data set."
                        :status "approved"})))

      ;; Map with wrong type for :permanent_id (optional field)
      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :permanent_id 123})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :permanent_id nil})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :permanent_id true})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :permanent_id ["https://doi.org/10.1093/nar/gkv416"]})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :permanent_id {:url "https://doi.org/10.1093/nar/gkv416"}})))

      ;; Map with wrong type for :comments (optional field)
      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :comments 123})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :comments nil})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :comments true})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :comments ["Please fill out the DataCite metadata template for the data set."]})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :comments {:text "Please fill out the DataCite metadata template for the data set."}})))

      ;; Map with wrong type for :status (optional field)
      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :status 123})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :status nil})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :status true})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :status ["approved"]})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :status {:code "approved"}})))

      ;; Map with wrong type for :status_date (required field)
      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date "1762296097000"
                        :updated_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date nil
                        :updated_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date true
                        :updated_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000.5
                        :updated_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date [1762296097000]
                        :updated_by "janedoe"})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date {:timestamp 1762296097000}
                        :updated_by "janedoe"})))

      ;; Map with wrong type for :updated_by (required field)
      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by 123})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by nil})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by true})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by ["janedoe"]})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by {:username "janedoe"}})))

      ;; Map with multiple wrong types (combination tests)
      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date "invalid"
                        :updated_by 123
                        :permanent_id nil
                        :comments true
                        :status ["approved"]})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date nil
                        :updated_by nil})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date true
                        :updated_by false})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :unknown-field "value"})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :permanent_id "https://doi.org/10.1093/nar/gkv416"
                        :comments "Please fill out the DataCite metadata template for the data set."
                        :status "approved"
                        :curator "admin"})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :additional "field"})))

      (is (not (valid? pir/PermanentIDRequestStatus
                       {:status_date 1762296097000
                        :updated_by "janedoe"
                        :extra "field"
                        :another "extra"}))))))

(deftest test-PermanentIDRequestDetails
  (testing "PermanentIDRequestDetails validation"
    (testing "valid params"
      ;; Map with all required fields and empty history
      (is (valid? pir/PermanentIDRequestDetails
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :history []}))

      ;; Map with all required fields + optional fields from base + empty history
      (is (valid? pir/PermanentIDRequestDetails
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :permanent_id "https://doi.org/10.1093/nar/gkv416"
                   :original_path "/iplant/home/username/analyses/example-analysis/output"
                   :history []}))

      ;; Map with single history entry
      (is (valid? pir/PermanentIDRequestDetails
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :history [{:status_date 1762296097000
                              :updated_by "janedoe"}]}))

      ;; Map with single history entry with all fields
      (is (valid? pir/PermanentIDRequestDetails
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :history [{:status_date 1762296097000
                              :updated_by "janedoe"
                              :permanent_id "https://doi.org/10.1093/nar/gkv416"
                              :comments "Please fill out the DataCite metadata template for the data set."
                              :status "approved"}]}))

      ;; Map with multiple history entries
      (is (valid? pir/PermanentIDRequestDetails
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :history [{:status_date 1762296097000
                              :updated_by "janedoe"
                              :status "submitted"}
                             {:status_date 1762300000000
                              :updated_by "admin"
                              :status "approved"
                              :comments "Approved by curator team."}]}))

      ;; Map with all fields populated including multiple history entries
      (is (valid? pir/PermanentIDRequestDetails
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :permanent_id "https://doi.org/10.1093/nar/gkv416"
                   :original_path "/iplant/home/username/analyses/example-analysis/output"
                   :history [{:status_date 1762296097000
                              :updated_by "janedoe"
                              :status "submitted"
                              :comments "Initial submission"}
                             {:status_date 1762300000000
                              :updated_by "admin"
                              :status "approved"
                              :comments "Approved by curator team."
                              :permanent_id "https://doi.org/10.1093/nar/gkv416"}]}))

      ;; Map with different ID type
      (is (valid? pir/PermanentIDRequestDetails
                  {:type "ARK"
                   :id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                   :requested_by "johndoe"
                   :permanent_id "ark:/12345/abcdef"
                   :history [{:status_date 1762296097000
                              :updated_by "johndoe"
                              :status "pending"}]}))

      ;; Map with whitespace-only strings (valid with current schema)
      (is (valid? pir/PermanentIDRequestDetails
                  {:type "   "
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "   "
                   :history [{:status_date 1762296097000
                              :updated_by "   "}]})))

    (testing "invalid params"
      ;; Empty map is invalid (missing required fields)
      (is (not (valid? pir/PermanentIDRequestDetails {})))

      ;; Map missing :type (required field)
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history []})))

      ;; Map missing :id (required field)
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :requested_by "janedoe"
                        :history []})))

      ;; Map missing :requested_by (required field)
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :history []})))

      ;; Map missing :history (required field)
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"})))

      ;; Map with wrong type for :history field (not a vector)
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history "not a vector"})))

      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history nil})))

      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history {:status_date 1762296097000
                                  :updated_by "janedoe"}})))

      ;; Map with history entry missing required field (:status_date)
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history [{:updated_by "janedoe"}]})))

      ;; Map with history entry missing required field (:updated_by)
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history [{:status_date 1762296097000}]})))

      ;; Map with history entry with wrong type for :status_date
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history [{:status_date "1762296097000"
                                   :updated_by "janedoe"}]})))

      ;; Map with history entry with wrong type for :updated_by
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history [{:status_date 1762296097000
                                   :updated_by 123}]})))

      ;; Map with history entry with wrong type for optional :permanent_id
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history [{:status_date 1762296097000
                                   :updated_by "janedoe"
                                   :permanent_id 123}]})))

      ;; Map with history entry with wrong type for optional :comments
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history [{:status_date 1762296097000
                                   :updated_by "janedoe"
                                   :comments 123}]})))

      ;; Map with history entry with wrong type for optional :status
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history [{:status_date 1762296097000
                                   :updated_by "janedoe"
                                   :status 123}]})))

      ;; Map with history entry with extra field
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history [{:status_date 1762296097000
                                   :updated_by "janedoe"
                                   :extra-field "not allowed"}]})))

      ;; Map with wrong type for base field :type
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type 123
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history []})))

      ;; Map with wrong type for base field :id
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history []})))

      ;; Map with wrong type for base field :requested_by
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by 123
                        :history []})))

      ;; Map with wrong type for optional base field :permanent_id
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :permanent_id 123
                        :history []})))

      ;; Map with wrong type for optional base field :original_path
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :original_path 123
                        :history []})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history []
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :history []
                        :unknown-field "value"})))

      (is (not (valid? pir/PermanentIDRequestDetails
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :permanent_id "https://doi.org/10.1093/nar/gkv416"
                        :original_path "/iplant/home/username/data"
                        :history []
                        :status "pending"}))))))

(deftest test-PermanentIDRequestListing
  (testing "PermanentIDRequestListing validation"
    (testing "valid params"
      ;; Map with all required fields only
      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :date_submitted 1763063226000
                   :status "Approved"
                   :date_updated 1763067980000
                   :updated_by "example_user"}))

      ;; Map with all fields (required + optional fields from base)
      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :permanent_id "https://doi.org/10.1093/nar/gkv416"
                   :original_path "/iplant/home/username/analyses/example-analysis/output"
                   :date_submitted 1763063226000
                   :status "Approved"
                   :date_updated 1763067980000
                   :updated_by "example_user"}))

      ;; Map with required fields + :permanent_id only
      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :permanent_id "https://doi.org/10.1093/nar/gkv416"
                   :date_submitted 1763063226000
                   :status "Approved"
                   :date_updated 1763067980000
                   :updated_by "example_user"}))

      ;; Map with required fields + :original_path only
      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :original_path "/iplant/home/username/analyses/example-analysis/output"
                   :date_submitted 1763063226000
                   :status "Approved"
                   :date_updated 1763067980000
                   :updated_by "example_user"}))

      ;; Map with different valid type values
      (is (valid? pir/PermanentIDRequestListing
                  {:type "ARK"
                   :id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                   :requested_by "johndoe"
                   :date_submitted 1763063226000
                   :status "Pending"
                   :date_updated 1763067980000
                   :updated_by "admin"}))

      (is (valid? pir/PermanentIDRequestListing
                  {:type "Handle"
                   :id #uuid "00000000-0000-0000-0000-000000000000"
                   :requested_by "researcher"
                   :date_submitted 1763063226000
                   :status "Submitted"
                   :date_updated 1763067980000
                   :updated_by "researcher"}))

      ;; Map with different valid status values
      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :date_submitted 1763063226000
                   :status "Submitted"
                   :date_updated 1763067980000
                   :updated_by "example_user"}))

      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :date_submitted 1763063226000
                   :status "Pending"
                   :date_updated 1763067980000
                   :updated_by "example_user"}))

      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :date_submitted 1763063226000
                   :status "Rejected"
                   :date_updated 1763067980000
                   :updated_by "example_user"}))

      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :date_submitted 1763063226000
                   :status "Completed"
                   :date_updated 1763067980000
                   :updated_by "example_user"}))

      ;; Map with different valid permanent_id values
      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :permanent_id "https://doi.org/10.5061/dryad.12345"
                   :date_submitted 1763063226000
                   :status "Approved"
                   :date_updated 1763067980000
                   :updated_by "example_user"}))

      (is (valid? pir/PermanentIDRequestListing
                  {:type "ARK"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :permanent_id "ark:/12345/abcdef"
                   :date_submitted 1763063226000
                   :status "Approved"
                   :date_updated 1763067980000
                   :updated_by "example_user"}))

      ;; Map with different valid timestamp values
      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :date_submitted 0
                   :status "Approved"
                   :date_updated 1
                   :updated_by "example_user"}))

      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :date_submitted 9999999999999
                   :status "Approved"
                   :date_updated 9999999999999
                   :updated_by "example_user"}))

      ;; Map with different valid username values
      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "user123"
                   :date_submitted 1763063226000
                   :status "Approved"
                   :date_updated 1763067980000
                   :updated_by "admin@example.com"}))

      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "admin@example.com"
                   :date_submitted 1763063226000
                   :status "Approved"
                   :date_updated 1763067980000
                   :updated_by "curator123"}))

      ;; Empty strings are valid with current schema (uses :string, not NonBlankString)
      (is (valid? pir/PermanentIDRequestListing
                  {:type ""
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by ""
                   :date_submitted 1763063226000
                   :status ""
                   :date_updated 1763067980000
                   :updated_by ""}))

      (is (valid? pir/PermanentIDRequestListing
                  {:type "DOI"
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "janedoe"
                   :permanent_id ""
                   :original_path ""
                   :date_submitted 1763063226000
                   :status "Approved"
                   :date_updated 1763067980000
                   :updated_by "example_user"}))

      ;; Whitespace-only strings are valid with current schema
      (is (valid? pir/PermanentIDRequestListing
                  {:type "   "
                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :requested_by "   "
                   :date_submitted 1763063226000
                   :status "   "
                   :date_updated 1763067980000
                   :updated_by "   "})))

    (testing "invalid params"
      ;; Empty map is invalid (missing all required fields)
      (is (not (valid? pir/PermanentIDRequestListing {})))

      ;; Map missing :type (required field from base)
      (is (not (valid? pir/PermanentIDRequestListing
                       {:id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      ;; Map missing :id (required field from base)
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      ;; Map missing :requested_by (required field from base)
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      ;; Map missing :date_submitted (required field)
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      ;; Map missing :status (required field)
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      ;; Map missing :date_updated (required field)
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :updated_by "example_user"})))

      ;; Map missing :updated_by (required field)
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000})))

      ;; Map with wrong type for :type field
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type 123
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type nil
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type ["DOI"]
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      ;; Map with wrong type for :id field
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id 123
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id nil
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      ;; Map with wrong type for :requested_by field
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by 123
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by nil
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by ["janedoe"]
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      ;; Map with wrong type for :date_submitted field
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted "1763063226000"
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted nil
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000.5
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      ;; Map with wrong type for :status field
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status 123
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status nil
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status ["Approved"]
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      ;; Map with wrong type for :date_updated field
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated "1763067980000"
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated nil
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000.5
                        :updated_by "example_user"})))

      ;; Map with wrong type for :updated_by field
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by 123})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by nil})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by ["example_user"]})))

      ;; Map with wrong type for optional :permanent_id field
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :permanent_id 123
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :permanent_id nil
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :permanent_id ["https://doi.org/10.1093/nar/gkv416"]
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      ;; Map with wrong type for optional :original_path field
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :original_path 123
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :original_path nil
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :original_path ["/iplant/home/username/data"]
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"
                        :unknown-field "value"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :permanent_id "https://doi.org/10.1093/nar/gkv416"
                        :original_path "/iplant/home/username/data"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"
                        :comments "should not be here"})))

      (is (not (valid? pir/PermanentIDRequestListing
                       {:type "DOI"
                        :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                        :requested_by "janedoe"
                        :date_submitted 1763063226000
                        :status "Approved"
                        :date_updated 1763067980000
                        :updated_by "example_user"
                        :history []}))))))

(deftest test-PermanentIDRequestList
  (testing "PermanentIDRequestList validation"
    (testing "valid params"
      ;; Map with empty requests list and total 0
      (is (valid? pir/PermanentIDRequestList
                  {:requests []
                   :total 0}))

      ;; Map with single request and total 1
      (is (valid? pir/PermanentIDRequestList
                  {:requests [{:type "DOI"
                               :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                               :requested_by "janedoe"
                               :date_submitted 1763063226000
                               :status "Approved"
                               :date_updated 1763067980000
                               :updated_by "example_user"}]
                   :total 1}))

      ;; Map with multiple requests and corresponding total
      (is (valid? pir/PermanentIDRequestList
                  {:requests [{:type "DOI"
                               :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                               :requested_by "janedoe"
                               :date_submitted 1763063226000
                               :status "Approved"
                               :date_updated 1763067980000
                               :updated_by "example_user"}
                              {:type "ARK"
                               :id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                               :requested_by "johndoe"
                               :date_submitted 1763063226000
                               :status "Pending"
                               :date_updated 1763067980000
                               :updated_by "admin"}]
                   :total 2}))

      ;; Map with request containing all fields (including optional from base)
      (is (valid? pir/PermanentIDRequestList
                  {:requests [{:type "DOI"
                               :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                               :requested_by "janedoe"
                               :permanent_id "https://doi.org/10.1093/nar/gkv416"
                               :original_path "/iplant/home/username/analyses/example-analysis/output"
                               :date_submitted 1763063226000
                               :status "Approved"
                               :date_updated 1763067980000
                               :updated_by "example_user"}]
                   :total 1}))

      ;; Map with total from schema example (27)
      (is (valid? pir/PermanentIDRequestList
                  {:requests [{:type "DOI"
                               :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                               :requested_by "janedoe"
                               :date_submitted 1763063226000
                               :status "Approved"
                               :date_updated 1763067980000
                               :updated_by "example_user"}]
                   :total 27}))

      ;; Map with large total value
      (is (valid? pir/PermanentIDRequestList
                  {:requests []
                   :total 999999}))

      ;; Map with multiple diverse requests
      (is (valid? pir/PermanentIDRequestList
                  {:requests [{:type "DOI"
                               :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                               :requested_by "janedoe"
                               :permanent_id "https://doi.org/10.1093/nar/gkv416"
                               :date_submitted 1763063226000
                               :status "Approved"
                               :date_updated 1763067980000
                               :updated_by "example_user"}
                              {:type "ARK"
                               :id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                               :requested_by "johndoe"
                               :original_path "/iplant/home/johndoe/data"
                               :date_submitted 1763063226000
                               :status "Pending"
                               :date_updated 1763067980000
                               :updated_by "admin"}
                              {:type "Handle"
                               :id #uuid "00000000-0000-0000-0000-000000000000"
                               :requested_by "researcher"
                               :date_submitted 1763063226000
                               :status "Submitted"
                               :date_updated 1763067980000
                               :updated_by "researcher"}]
                   :total 3})))

    (testing "invalid params"
      ;; Empty map is invalid (missing required fields)
      (is (not (valid? pir/PermanentIDRequestList {})))

      ;; Map missing :requests (required field)
      (is (not (valid? pir/PermanentIDRequestList
                       {:total 0})))

      ;; Map missing :total (required field)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests []})))

      ;; Map with wrong type for :requests field (not a vector)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests "not a vector"
                        :total 0})))

      (is (not (valid? pir/PermanentIDRequestList
                       {:requests nil
                        :total 0})))

      (is (not (valid? pir/PermanentIDRequestList
                       {:requests {:type "DOI"
                                   :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                   :requested_by "janedoe"
                                   :date_submitted 1763063226000
                                   :status "Approved"
                                   :date_updated 1763067980000
                                   :updated_by "example_user"}
                        :total 1})))

      (is (not (valid? pir/PermanentIDRequestList
                       {:requests 123
                        :total 0})))

      ;; Map with wrong type for :total field
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests []
                        :total "0"})))

      (is (not (valid? pir/PermanentIDRequestList
                       {:requests []
                        :total nil})))

      (is (not (valid? pir/PermanentIDRequestList
                       {:requests []
                        :total 27.5})))

      (is (not (valid? pir/PermanentIDRequestList
                       {:requests []
                        :total [27]})))

      (is (not (valid? pir/PermanentIDRequestList
                       {:requests []
                        :total {:count 27}})))

      ;; Map with invalid request entry (missing required field :type)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :requested_by "janedoe"
                                    :date_submitted 1763063226000
                                    :status "Approved"
                                    :date_updated 1763067980000
                                    :updated_by "example_user"}]
                        :total 1})))

      ;; Map with invalid request entry (missing required field :id)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:type "DOI"
                                    :requested_by "janedoe"
                                    :date_submitted 1763063226000
                                    :status "Approved"
                                    :date_updated 1763067980000
                                    :updated_by "example_user"}]
                        :total 1})))

      ;; Map with invalid request entry (missing required field :requested_by)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:type "DOI"
                                    :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :date_submitted 1763063226000
                                    :status "Approved"
                                    :date_updated 1763067980000
                                    :updated_by "example_user"}]
                        :total 1})))

      ;; Map with invalid request entry (missing required field :date_submitted)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:type "DOI"
                                    :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :requested_by "janedoe"
                                    :status "Approved"
                                    :date_updated 1763067980000
                                    :updated_by "example_user"}]
                        :total 1})))

      ;; Map with invalid request entry (missing required field :status)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:type "DOI"
                                    :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :requested_by "janedoe"
                                    :date_submitted 1763063226000
                                    :date_updated 1763067980000
                                    :updated_by "example_user"}]
                        :total 1})))

      ;; Map with invalid request entry (missing required field :date_updated)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:type "DOI"
                                    :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :requested_by "janedoe"
                                    :date_submitted 1763063226000
                                    :status "Approved"
                                    :updated_by "example_user"}]
                        :total 1})))

      ;; Map with invalid request entry (missing required field :updated_by)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:type "DOI"
                                    :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :requested_by "janedoe"
                                    :date_submitted 1763063226000
                                    :status "Approved"
                                    :date_updated 1763067980000}]
                        :total 1})))

      ;; Map with invalid request entry (wrong type for :type field)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:type 123
                                    :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :requested_by "janedoe"
                                    :date_submitted 1763063226000
                                    :status "Approved"
                                    :date_updated 1763067980000
                                    :updated_by "example_user"}]
                        :total 1})))

      ;; Map with invalid request entry (wrong type for :id field)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:type "DOI"
                                    :id "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :requested_by "janedoe"
                                    :date_submitted 1763063226000
                                    :status "Approved"
                                    :date_updated 1763067980000
                                    :updated_by "example_user"}]
                        :total 1})))

      ;; Map with invalid request entry (wrong type for :date_submitted field)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:type "DOI"
                                    :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :requested_by "janedoe"
                                    :date_submitted "1763063226000"
                                    :status "Approved"
                                    :date_updated 1763067980000
                                    :updated_by "example_user"}]
                        :total 1})))

      ;; Map with invalid request entry (wrong type for :status field)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:type "DOI"
                                    :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :requested_by "janedoe"
                                    :date_submitted 1763063226000
                                    :status 123
                                    :date_updated 1763067980000
                                    :updated_by "example_user"}]
                        :total 1})))

      ;; Map with invalid request entry (extra field)
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:type "DOI"
                                    :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :requested_by "janedoe"
                                    :date_submitted 1763063226000
                                    :status "Approved"
                                    :date_updated 1763067980000
                                    :updated_by "example_user"
                                    :extra-field "not allowed"}]
                        :total 1})))

      ;; Map with one valid and one invalid request entry
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:type "DOI"
                                    :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :requested_by "janedoe"
                                    :date_submitted 1763063226000
                                    :status "Approved"
                                    :date_updated 1763067980000
                                    :updated_by "example_user"}
                                   {:type "ARK"
                                    :id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                                    :requested_by "johndoe"
                                    :date_submitted 1763063226000
                                    :status 123
                                    :date_updated 1763067980000
                                    :updated_by "admin"}]
                        :total 2})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequestList
                       {:requests []
                        :total 0
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequestList
                       {:requests []
                        :total 0
                        :unknown-field "value"})))

      (is (not (valid? pir/PermanentIDRequestList
                       {:requests [{:type "DOI"
                                    :id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :requested_by "janedoe"
                                    :date_submitted 1763063226000
                                    :status "Approved"
                                    :date_updated 1763067980000
                                    :updated_by "example_user"}]
                        :total 1
                        :count "extra field"}))))))

(deftest test-PermanentIDRequestListPagingParams
  (testing "PermanentIDRequestListPagingParams validation"
    (testing "valid params"
      ;; Empty map is valid (all fields are optional)
      (is (valid? pir/PermanentIDRequestListPagingParams {}))

      ;; Map with only :limit
      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:limit 50}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:limit 1}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:limit 100}))

      ;; Map with only :offset
      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:offset 0}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:offset 10}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:offset 999}))

      ;; Map with only :sort-dir
      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:sort-dir "ASC"}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:sort-dir "DESC"}))

      ;; Map with only :sort-field (with valid enum values)
      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:sort-field :type}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:sort-field :target_type}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:sort-field :requested_by}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:sort-field :date_submitted}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:sort-field :status}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:sort-field :date_updated}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:sort-field :updated_by}))

      ;; Map with only :statuses
      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:statuses []}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:statuses ["Rejected"]}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:statuses ["Approved" "Rejected"]}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:statuses ["Submitted" "Pending" "Approved" "Rejected"]}))

      ;; Map with combinations of paging fields
      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:limit 50
                   :offset 0}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:limit 100
                   :offset 50}))

      ;; Map with sort-field and sort-dir
      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:sort-field :type
                   :sort-dir "ASC"}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:sort-field :date_submitted
                   :sort-dir "DESC"}))

      ;; Map with all paging params
      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:limit 50
                   :offset 0
                   :sort-field :date_submitted
                   :sort-dir "DESC"}))

      ;; Map with all paging params and statuses
      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:limit 50
                   :offset 0
                   :sort-field :status
                   :sort-dir "ASC"
                   :statuses ["Approved"]}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:limit 100
                   :offset 50
                   :sort-field :date_updated
                   :sort-dir "DESC"
                   :statuses ["Submitted" "Pending"]}))

      ;; Map with different valid statuses values
      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:statuses ["Evaluation"]}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:statuses ["Completion"]}))

      ;; Map with single character statuses (edge case)
      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:statuses ["A" "B" "C"]}))

      ;; Map with empty string in statuses (valid as strings are allowed)
      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:statuses [""]}))

      (is (valid? pir/PermanentIDRequestListPagingParams
                  {:statuses ["   "]})))

    (testing "invalid params"
      ;; Map with invalid :limit (must be positive)
      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:limit 0})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:limit -1})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:limit -10})))

      ;; Map with wrong type for :limit
      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:limit "50"})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:limit nil})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:limit 50.5})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:limit [50]})))

      ;; Map with invalid :offset (must be non-negative)
      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:offset -1})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:offset -10})))

      ;; Map with wrong type for :offset
      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:offset "0"})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:offset nil})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:offset 0.5})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:offset [0]})))

      ;; Map with invalid :sort-dir (must be "ASC" or "DESC")
      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-dir "asc"})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-dir "desc"})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-dir "ascending"})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-dir "ASCENDING"})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-dir ""})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-dir 123})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-dir nil})))

      ;; Map with invalid :sort-field (must be one of the enum values)
      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-field :name})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-field :invalid})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-field :Type})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-field "type"})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-field "date_submitted"})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-field 123})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-field nil})))

      ;; Map with wrong type for :statuses (must be a vector)
      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:statuses "Rejected"})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:statuses nil})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:statuses {:status "Rejected"}})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:statuses 123})))

      ;; Map with :statuses containing non-string values
      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:statuses [123]})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:statuses [nil]})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:statuses [true]})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:statuses [:Rejected]})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:statuses [{:status "Rejected"}]})))

      ;; Map with mix of valid and invalid status values
      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:statuses ["Approved" 123]})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:statuses ["Approved" nil "Rejected"]})))

      ;; Map with multiple invalid fields
      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:limit 0
                        :offset -1})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:limit "50"
                        :offset "0"
                        :sort-dir "asc"})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:sort-field :invalid
                        :sort-dir "INVALID"})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:limit 50
                        :unknown-field "value"})))

      (is (not (valid? pir/PermanentIDRequestListPagingParams
                       {:limit 50
                        :offset 0
                        :sort-field :type
                        :sort-dir "ASC"
                        :statuses ["Approved"]
                        :extra "field"}))))))

(deftest test-PermanentIDRequestStatusCode
  (testing "PermanentIDRequestStatusCode validation"
    (testing "valid params"
      ;; Map with all required fields using example values from schema
      (is (valid? pir/PermanentIDRequestStatusCode
                  {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                   :name "Pending"
                   :description "The curators are waiting for a response from the requesting user."}))

      ;; Map with different valid status code names
      (is (valid? pir/PermanentIDRequestStatusCode
                  {:id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                   :name "Submitted"
                   :description "The request has been submitted and is awaiting review."}))

      (is (valid? pir/PermanentIDRequestStatusCode
                  {:id #uuid "00000000-0000-0000-0000-000000000000"
                   :name "Approved"
                   :description "The request has been approved by the curators."}))

      (is (valid? pir/PermanentIDRequestStatusCode
                  {:id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :name "Rejected"
                   :description "The request has been rejected."}))

      (is (valid? pir/PermanentIDRequestStatusCode
                  {:id #uuid "f47ac10b-58cc-4372-a567-0e02b2c3d479"
                   :name "Completion"
                   :description "The permanent ID has been assigned."}))

      (is (valid? pir/PermanentIDRequestStatusCode
                  {:id #uuid "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
                   :name "Evaluation"
                   :description "The request is being evaluated."}))

      ;; Map with different UUID values
      (is (valid? pir/PermanentIDRequestStatusCode
                  {:id #uuid "12345678-1234-1234-1234-123456789abc"
                   :name "In Progress"
                   :description "The request is currently being processed."}))

      (is (valid? pir/PermanentIDRequestStatusCode
                  {:id #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff"
                   :name "On Hold"
                   :description "The request has been put on hold pending additional information."}))

      ;; Map with different description values
      (is (valid? pir/PermanentIDRequestStatusCode
                  {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                   :name "Pending"
                   :description "Awaiting curator review."}))

      (is (valid? pir/PermanentIDRequestStatusCode
                  {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                   :name "Pending"
                   :description "This is a very long description that provides detailed information about what this status code means and when it should be used in the permanent ID request workflow process."}))

      ;; Map with single character strings (edge case)
      (is (valid? pir/PermanentIDRequestStatusCode
                  {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                   :name "A"
                   :description "B"}))

      ;; Map with empty strings (valid as :string is used, not NonBlankString)
      (is (valid? pir/PermanentIDRequestStatusCode
                  {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                   :name ""
                   :description ""}))

      ;; Map with whitespace-only strings
      (is (valid? pir/PermanentIDRequestStatusCode
                  {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                   :name "   "
                   :description "   "})))

    (testing "invalid params"
      ;; Empty map is invalid (missing all required fields)
      (is (not (valid? pir/PermanentIDRequestStatusCode {})))

      ;; Map missing :id (required field)
      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:name "Pending"
                        :description "The curators are waiting for a response from the requesting user."})))

      ;; Map missing :name (required field)
      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :description "The curators are waiting for a response from the requesting user."})))

      ;; Map missing :description (required field)
      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name "Pending"})))

      ;; Map with only one field
      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:name "Pending"})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:description "The curators are waiting for a response from the requesting user."})))

      ;; Map with wrong type for :id field
      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name "Pending"
                        :description "The curators are waiting for a response from the requesting user."})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id 123
                        :name "Pending"
                        :description "The curators are waiting for a response from the requesting user."})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id nil
                        :name "Pending"
                        :description "The curators are waiting for a response from the requesting user."})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id true
                        :name "Pending"
                        :description "The curators are waiting for a response from the requesting user."})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id [#uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"]
                        :name "Pending"
                        :description "The curators are waiting for a response from the requesting user."})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id {:uuid #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"}
                        :name "Pending"
                        :description "The curators are waiting for a response from the requesting user."})))

      ;; Map with wrong type for :name field
      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name 123
                        :description "The curators are waiting for a response from the requesting user."})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name nil
                        :description "The curators are waiting for a response from the requesting user."})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name true
                        :description "The curators are waiting for a response from the requesting user."})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name ["Pending"]
                        :description "The curators are waiting for a response from the requesting user."})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name {:status "Pending"}
                        :description "The curators are waiting for a response from the requesting user."})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name :Pending
                        :description "The curators are waiting for a response from the requesting user."})))

      ;; Map with wrong type for :description field
      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name "Pending"
                        :description 123})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name "Pending"
                        :description nil})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name "Pending"
                        :description true})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name "Pending"
                        :description ["The curators are waiting for a response from the requesting user."]})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name "Pending"
                        :description {:text "The curators are waiting for a response from the requesting user."}})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name "Pending"
                        :description :description})))

      ;; Map with multiple wrong types
      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id "not-a-uuid"
                        :name 123
                        :description nil})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id nil
                        :name nil
                        :description nil})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id true
                        :name false
                        :description 0})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name "Pending"
                        :description "The curators are waiting for a response from the requesting user."
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name "Pending"
                        :description "The curators are waiting for a response from the requesting user."
                        :unknown-field "value"})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name "Pending"
                        :description "The curators are waiting for a response from the requesting user."
                        :status "extra"})))

      (is (not (valid? pir/PermanentIDRequestStatusCode
                       {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                        :name "Pending"
                        :description "The curators are waiting for a response from the requesting user."
                        :code "Pending"
                        :extra "field"}))))))

(deftest test-PermanentIDRequestStatusCodeList
  (testing "PermanentIDRequestStatusCodeList validation"
    (testing "valid params"
      ;; Map with empty status_codes vector
      (is (valid? pir/PermanentIDRequestStatusCodeList
                  {:status_codes []}))

      ;; Map with single status code
      (is (valid? pir/PermanentIDRequestStatusCodeList
                  {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                   :name "Pending"
                                   :description "The curators are waiting for a response from the requesting user."}]}))

      ;; Map with multiple status codes
      (is (valid? pir/PermanentIDRequestStatusCodeList
                  {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                   :name "Pending"
                                   :description "The curators are waiting for a response from the requesting user."}
                                  {:id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                                   :name "Submitted"
                                   :description "The request has been submitted and is awaiting review."}]}))

      ;; Map with many different status codes
      (is (valid? pir/PermanentIDRequestStatusCodeList
                  {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                   :name "Pending"
                                   :description "The curators are waiting for a response from the requesting user."}
                                  {:id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                                   :name "Submitted"
                                   :description "The request has been submitted and is awaiting review."}
                                  {:id #uuid "00000000-0000-0000-0000-000000000000"
                                   :name "Approved"
                                   :description "The request has been approved by the curators."}
                                  {:id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                   :name "Rejected"
                                   :description "The request has been rejected."}
                                  {:id #uuid "f47ac10b-58cc-4372-a567-0e02b2c3d479"
                                   :name "Completion"
                                   :description "The permanent ID has been assigned."}]}))

      ;; Map with status codes having different valid values
      (is (valid? pir/PermanentIDRequestStatusCodeList
                  {:status_codes [{:id #uuid "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
                                   :name "Evaluation"
                                   :description "The request is being evaluated."}
                                  {:id #uuid "12345678-1234-1234-1234-123456789abc"
                                   :name "In Progress"
                                   :description "The request is currently being processed."}
                                  {:id #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff"
                                   :name "On Hold"
                                   :description "The request has been put on hold pending additional information."}]}))

      ;; Map with status codes with edge case values
      (is (valid? pir/PermanentIDRequestStatusCodeList
                  {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                   :name "A"
                                   :description "B"}]}))

      ;; Map with status codes with empty string values (valid as :string is used)
      (is (valid? pir/PermanentIDRequestStatusCodeList
                  {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                   :name ""
                                   :description ""}]}))

      ;; Map with status codes with whitespace strings
      (is (valid? pir/PermanentIDRequestStatusCodeList
                  {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                   :name "   "
                                   :description "   "}]})))

    (testing "invalid params"
      ;; Empty map is invalid (missing required field)
      (is (not (valid? pir/PermanentIDRequestStatusCodeList {})))

      ;; Map with wrong type for :status_codes (not a vector)
      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes "not a vector"})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes nil})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes {:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                       :name "Pending"
                                       :description "The curators are waiting for a response from the requesting user."}})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes 123})))

      ;; Map with status code missing required field (:id)
      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:name "Pending"
                                        :description "The curators are waiting for a response from the requesting user."}]})))

      ;; Map with status code missing required field (:name)
      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :description "The curators are waiting for a response from the requesting user."}]})))

      ;; Map with status code missing required field (:description)
      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name "Pending"}]})))

      ;; Map with status code with wrong type for :id
      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name "Pending"
                                        :description "The curators are waiting for a response from the requesting user."}]})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id 123
                                        :name "Pending"
                                        :description "The curators are waiting for a response from the requesting user."}]})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id nil
                                        :name "Pending"
                                        :description "The curators are waiting for a response from the requesting user."}]})))

      ;; Map with status code with wrong type for :name
      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name 123
                                        :description "The curators are waiting for a response from the requesting user."}]})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name nil
                                        :description "The curators are waiting for a response from the requesting user."}]})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name :Pending
                                        :description "The curators are waiting for a response from the requesting user."}]})))

      ;; Map with status code with wrong type for :description
      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name "Pending"
                                        :description 123}]})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name "Pending"
                                        :description nil}]})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name "Pending"
                                        :description :description}]})))

      ;; Map with status code with extra field
      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name "Pending"
                                        :description "The curators are waiting for a response from the requesting user."
                                        :extra-field "not allowed"}]})))

      ;; Map with mix of valid and invalid status codes
      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name "Pending"
                                        :description "The curators are waiting for a response from the requesting user."}
                                       {:id "invalid-uuid"
                                        :name "Submitted"
                                        :description "The request has been submitted and is awaiting review."}]})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name "Pending"
                                        :description "The curators are waiting for a response from the requesting user."}
                                       {:id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                                        :name 123
                                        :description "The request has been submitted and is awaiting review."}]})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name "Pending"
                                        :description "The curators are waiting for a response from the requesting user."}
                                       {:id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                                        :name "Submitted"}]})))

      ;; Map with non-map elements in status_codes vector
      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes ["Pending"]})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [123]})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [nil]})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [#uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"]})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes []
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes []
                        :unknown-field "value"})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name "Pending"
                                        :description "The curators are waiting for a response from the requesting user."}]
                        :total 1})))

      (is (not (valid? pir/PermanentIDRequestStatusCodeList
                       {:status_codes [{:id #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
                                        :name "Pending"
                                        :description "The curators are waiting for a response from the requesting user."}]
                        :count 1
                        :extra "field"}))))))

(deftest test-PermanentIDRequestType
  (testing "PermanentIDRequestType validation"
    (testing "valid params"
      ;; Map with all required fields using example values from schema
      (is (valid? pir/PermanentIDRequestType
                  {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                   :type "DOI"
                   :description "Data Object Identifier"}))

      ;; Map with different valid type values
      (is (valid? pir/PermanentIDRequestType
                  {:id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                   :type "ARK"
                   :description "Archival Resource Key"}))

      (is (valid? pir/PermanentIDRequestType
                  {:id #uuid "00000000-0000-0000-0000-000000000000"
                   :type "Handle"
                   :description "Handle System Identifier"}))

      (is (valid? pir/PermanentIDRequestType
                  {:id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                   :type "URN"
                   :description "Uniform Resource Name"}))

      (is (valid? pir/PermanentIDRequestType
                  {:id #uuid "f47ac10b-58cc-4372-a567-0e02b2c3d479"
                   :type "PURL"
                   :description "Persistent Uniform Resource Locator"}))

      ;; Map with different UUID values
      (is (valid? pir/PermanentIDRequestType
                  {:id #uuid "12345678-1234-1234-1234-123456789abc"
                   :type "DOI"
                   :description "Data Object Identifier"}))

      (is (valid? pir/PermanentIDRequestType
                  {:id #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff"
                   :type "DOI"
                   :description "Data Object Identifier"}))

      ;; Map with different description values
      (is (valid? pir/PermanentIDRequestType
                  {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                   :type "DOI"
                   :description "DOI"}))

      (is (valid? pir/PermanentIDRequestType
                  {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                   :type "DOI"
                   :description "This is a very long description that provides detailed information about what this request type means and when it should be used in the permanent ID request workflow process."}))

      ;; Map with single character strings (edge case)
      (is (valid? pir/PermanentIDRequestType
                  {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                   :type "A"
                   :description "B"}))

      ;; Map with empty strings (valid as :string is used, not NonBlankString)
      (is (valid? pir/PermanentIDRequestType
                  {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                   :type ""
                   :description ""}))

      ;; Map with whitespace-only strings
      (is (valid? pir/PermanentIDRequestType
                  {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                   :type "   "
                   :description "   "})))

    (testing "invalid params"
      ;; Empty map is invalid (missing all required fields)
      (is (not (valid? pir/PermanentIDRequestType {})))

      ;; Map missing :id (required field)
      (is (not (valid? pir/PermanentIDRequestType
                       {:type "DOI"
                        :description "Data Object Identifier"})))

      ;; Map missing :type (required field)
      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :description "Data Object Identifier"})))

      ;; Map missing :description (required field)
      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type "DOI"})))

      ;; Map with only one field
      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:type "DOI"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:description "Data Object Identifier"})))

      ;; Map with wrong type for :id field
      (is (not (valid? pir/PermanentIDRequestType
                       {:id "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type "DOI"
                        :description "Data Object Identifier"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id 123
                        :type "DOI"
                        :description "Data Object Identifier"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id nil
                        :type "DOI"
                        :description "Data Object Identifier"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id true
                        :type "DOI"
                        :description "Data Object Identifier"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id [#uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"]
                        :type "DOI"
                        :description "Data Object Identifier"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id {:uuid #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"}
                        :type "DOI"
                        :description "Data Object Identifier"})))

      ;; Map with wrong type for :type field
      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type 123
                        :description "Data Object Identifier"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type nil
                        :description "Data Object Identifier"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type true
                        :description "Data Object Identifier"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type ["DOI"]
                        :description "Data Object Identifier"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type {:name "DOI"}
                        :description "Data Object Identifier"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type :DOI
                        :description "Data Object Identifier"})))

      ;; Map with wrong type for :description field
      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type "DOI"
                        :description 123})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type "DOI"
                        :description nil})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type "DOI"
                        :description true})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type "DOI"
                        :description ["Data Object Identifier"]})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type "DOI"
                        :description {:text "Data Object Identifier"}})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type "DOI"
                        :description :description})))

      ;; Map with multiple wrong types
      (is (not (valid? pir/PermanentIDRequestType
                       {:id "not-a-uuid"
                        :type 123
                        :description nil})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id nil
                        :type nil
                        :description nil})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id true
                        :type false
                        :description 0})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type "DOI"
                        :description "Data Object Identifier"
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type "DOI"
                        :description "Data Object Identifier"
                        :unknown-field "value"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type "DOI"
                        :description "Data Object Identifier"
                        :name "DOI"})))

      (is (not (valid? pir/PermanentIDRequestType
                       {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                        :type "DOI"
                        :description "Data Object Identifier"
                        :category "persistent"
                        :extra "field"}))))))

(deftest test-PermanentIDRequestTypeList
  (testing "PermanentIDRequestTypeList validation"
    (testing "valid params"
      ;; Map with empty request_types vector
      (is (valid? pir/PermanentIDRequestTypeList
                  {:request_types []}))

      ;; Map with single request type
      (is (valid? pir/PermanentIDRequestTypeList
                  {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                    :type "DOI"
                                    :description "Data Object Identifier"}]}))

      ;; Map with multiple request types
      (is (valid? pir/PermanentIDRequestTypeList
                  {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                    :type "DOI"
                                    :description "Data Object Identifier"}
                                   {:id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                                    :type "ARK"
                                    :description "Archival Resource Key"}]}))

      ;; Map with many different request types
      (is (valid? pir/PermanentIDRequestTypeList
                  {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                    :type "DOI"
                                    :description "Data Object Identifier"}
                                   {:id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                                    :type "ARK"
                                    :description "Archival Resource Key"}
                                   {:id #uuid "00000000-0000-0000-0000-000000000000"
                                    :type "Handle"
                                    :description "Handle System Identifier"}
                                   {:id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"
                                    :type "URN"
                                    :description "Uniform Resource Name"}
                                   {:id #uuid "f47ac10b-58cc-4372-a567-0e02b2c3d479"
                                    :type "PURL"
                                    :description "Persistent Uniform Resource Locator"}]}))

      ;; Map with request types having different valid values
      (is (valid? pir/PermanentIDRequestTypeList
                  {:request_types [{:id #uuid "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
                                    :type "ISBN"
                                    :description "International Standard Book Number"}
                                   {:id #uuid "12345678-1234-1234-1234-123456789abc"
                                    :type "ISSN"
                                    :description "International Standard Serial Number"}
                                   {:id #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff"
                                    :type "LSID"
                                    :description "Life Science Identifier"}]}))

      ;; Map with request types with edge case values
      (is (valid? pir/PermanentIDRequestTypeList
                  {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                    :type "A"
                                    :description "B"}]}))

      ;; Map with request types with empty string values (valid as :string is used)
      (is (valid? pir/PermanentIDRequestTypeList
                  {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                    :type ""
                                    :description ""}]}))

      ;; Map with request types with whitespace strings
      (is (valid? pir/PermanentIDRequestTypeList
                  {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                    :type "   "
                                    :description "   "}]})))

    (testing "invalid params"
      ;; Empty map is invalid (missing required field)
      (is (not (valid? pir/PermanentIDRequestTypeList {})))

      ;; Map with wrong type for :request_types (not a vector)
      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types "not a vector"})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types nil})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types {:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                        :type "DOI"
                                        :description "Data Object Identifier"}})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types 123})))

      ;; Map with request type missing required field (:id)
      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:type "DOI"
                                         :description "Data Object Identifier"}]})))

      ;; Map with request type missing required field (:type)
      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :description "Data Object Identifier"}]})))

      ;; Map with request type missing required field (:description)
      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type "DOI"}]})))

      ;; Map with request type with wrong type for :id
      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type "DOI"
                                         :description "Data Object Identifier"}]})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id 123
                                         :type "DOI"
                                         :description "Data Object Identifier"}]})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id nil
                                         :type "DOI"
                                         :description "Data Object Identifier"}]})))

      ;; Map with request type with wrong type for :type
      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type 123
                                         :description "Data Object Identifier"}]})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type nil
                                         :description "Data Object Identifier"}]})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type :DOI
                                         :description "Data Object Identifier"}]})))

      ;; Map with request type with wrong type for :description
      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type "DOI"
                                         :description 123}]})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type "DOI"
                                         :description nil}]})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type "DOI"
                                         :description :description}]})))

      ;; Map with request type with extra field
      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type "DOI"
                                         :description "Data Object Identifier"
                                         :extra-field "not allowed"}]})))

      ;; Map with mix of valid and invalid request types
      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type "DOI"
                                         :description "Data Object Identifier"}
                                        {:id "invalid-uuid"
                                         :type "ARK"
                                         :description "Archival Resource Key"}]})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type "DOI"
                                         :description "Data Object Identifier"}
                                        {:id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                                         :type 123
                                         :description "Archival Resource Key"}]})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type "DOI"
                                         :description "Data Object Identifier"}
                                        {:id #uuid "a1b2c3d4-e5f6-4a5b-8c9d-123456789abc"
                                         :type "ARK"}]})))

      ;; Map with non-map elements in request_types vector
      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types ["DOI"]})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [123]})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [nil]})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [#uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"]})))

      ;; Extra/unknown fields not allowed (schema has {:closed true})
      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types []
                        :extra-field "not allowed"})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types []
                        :unknown-field "value"})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type "DOI"
                                         :description "Data Object Identifier"}]
                        :total 1})))

      (is (not (valid? pir/PermanentIDRequestTypeList
                       {:request_types [{:id #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
                                         :type "DOI"
                                         :description "Data Object Identifier"}]
                        :count 1
                        :extra "field"}))))))
