(ns common-swagger-api.malli.metadata-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.metadata :as meta]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-Avu
  (testing "Avu validation"
    (testing "valid AVU"
      (is (valid? meta/Avu
                  {:id          #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :attr        "attribute_name"
                   :value       "attribute_value"
                   :unit        "unit_name"
                   :target_id   #uuid "456e7890-e89b-12d3-a456-426614174000"
                   :created_by  "user123"
                   :modified_by "user456"
                   :created_on  1640995200000
                   :modified_on 1640995200000}))

      ;; With optional avus field
      (is (valid? meta/Avu
                  {:id          #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :attr        "attribute_name"
                   :value       "attribute_value"
                   :unit        "unit_name"
                   :target_id   #uuid "456e7890-e89b-12d3-a456-426614174000"
                   :created_by  "user123"
                   :modified_by "user456"
                   :created_on  1640995200000
                   :modified_on 1640995200000
                   :avus        []}))

      ;; With nested AVUs (using refs)
      (is (valid? meta/Avu
                  {:id          #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :attr        "parent_attr"
                   :value       "parent_value"
                   :unit        "parent_unit"
                   :target_id   #uuid "456e7890-e89b-12d3-a456-426614174000"
                   :created_by  "user123"
                   :modified_by "user456"
                   :created_on  1640995200000
                   :modified_on 1640995200000
                   :avus        [{:id          #uuid "123e4567-e89b-12d3-a456-426614174000"
                                  :attr        "attribute_name"
                                  :value       "attribute_value"
                                  :unit        "unit_name"
                                  :target_id   #uuid "456e7890-e89b-12d3-a456-426614174000"
                                  :created_by  "user123"
                                  :modified_by "user456"
                                  :created_on  1640995200000
                                  :modified_on 1640995200000}]})))

    (testing "invalid AVU"
      ;; Missing required fields
      (is (not (valid? meta/Avu {})))
      (is (not (valid? meta/Avu
                       {:id   #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :attr "attribute_name"})))

      ;; Wrong types
      (is (not (valid? meta/Avu
                       {:id          "not-a-uuid"
                        :attr        "attribute_name"
                        :value       "attribute_value"
                        :unit        "unit_name"
                        :target_id   #uuid "456e7890-e89b-12d3-a456-426614174000"
                        :created_by  "user123"
                        :modified_by "user456"
                        :created_on  1640995200000
                        :modified_on 1640995200000})))

      (is (not (valid? meta/Avu
                       {:id          #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :attr        "attribute_name"
                        :value       "attribute_value"
                        :unit        "unit_name"
                        :target_id   #uuid "456e7890-e89b-12d3-a456-426614174000"
                        :created_by  "user123"
                        :modified_by "user456"
                        :created_on  "not-an-int"
                        :modified_on 1640995200000})))

      ;; Extra fields not allowed due to :closed true
      (is (not (valid? meta/Avu
                       {:id          #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :attr        "attribute_name"
                        :value       "attribute_value"
                        :unit        "unit_name"
                        :target_id   #uuid "456e7890-e89b-12d3-a456-426614174000"
                        :created_by  "user123"
                        :modified_by "user456"
                        :created_on  1640995200000
                        :modified_on 1640995200000
                        :extra       "field"}))))))

(deftest test-AvuList
  (testing "AvuList validation"
    (testing "valid AVU list"
      (is (valid? meta/AvuList
                  {:avus []}))

      (is (valid? meta/AvuList
                  {:avus [{:id          #uuid "123e4567-e89b-12d3-a456-426614174000"
                           :attr        "attr1"
                           :value       "value1"
                           :unit        "unit1"
                           :target_id   #uuid "456e7890-e89b-12d3-a456-426614174000"
                           :created_by  "user123"
                           :modified_by "user456"
                           :created_on  1640995200000
                           :modified_on 1640995200000}
                          {:id          #uuid "789e4567-e89b-12d3-a456-426614174000"
                           :attr        "attr2"
                           :value       "value2"
                           :unit        "unit2"
                           :target_id   #uuid "456e7890-e89b-12d3-a456-426614174000"
                           :created_by  "user789"
                           :modified_by "user890"
                           :created_on  1640995300000
                           :modified_on 1640995300000}]})))

    (testing "invalid AVU list"
      ;; Missing required field
      (is (not (valid? meta/AvuList {})))

      ;; Wrong type for avus
      (is (not (valid? meta/AvuList
                       {:avus "not-a-vector"})))

      ;; Invalid AVUs in the list
      (is (not (valid? meta/AvuList
                       {:avus [{:id   #uuid "123e4567-e89b-12d3-a456-426614174000"
                                :attr "attr1"}]})))  ; Missing required fields

      ;; Extra fields not allowed
      (is (not (valid? meta/AvuList
                       {:avus  []
                        :extra "field"}))))))

(deftest test-AvuRequest
  (testing "AvuRequest validation"
    (testing "valid AVU request"
      ;; Minimal valid request (only attr, value, unit required)
      (is (valid? meta/AvuRequest
                  {:attr  "attribute_name"
                   :value "attribute_value"
                   :unit  "unit_name"}))

      ;; With some optional fields
      (is (valid? meta/AvuRequest
                  {:attr      "attribute_name"
                   :value     "attribute_value"
                   :unit      "unit_name"
                   :target_id #uuid "456e7890-e89b-12d3-a456-426614174000"}))

      ;; With all optional fields made optional
      (is (valid? meta/AvuRequest
                  {:id          #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :attr        "attribute_name"
                   :value       "attribute_value"
                   :unit        "unit_name"
                   :target_id   #uuid "456e7890-e89b-12d3-a456-426614174000"
                   :created_by  "user123"
                   :modified_by "user456"
                   :created_on  1640995200000
                   :modified_on 1640995200000
                   :avus        []}))

      (is (valid? meta/AvuRequest
                  {:attr  "attribute_name"
                   :value "attribute_value"
                   :unit  "unit_name"
                   :avus  [{:attr  "attribute_name"
                            :value "attribute_value"
                            :unit  "attribute_unit"}]})))

    (testing "invalid AVU request"
      ;; Missing required fields
      (is (not (valid? meta/AvuRequest {})))
      (is (not (valid? meta/AvuRequest
                       {:attr  "attribute_name"
                        :value "attribute_value"})))

      ;; Wrong types
      (is (not (valid? meta/AvuRequest
                       {:attr  123
                        :value "attribute_value"
                        :unit  "unit_name"}))))))

(deftest test-AvuListRequest
  (testing "AvuListRequest validation"
    (testing "valid AVU list request"
      (is (valid? meta/AvuListRequest
                  {:avus []}))

      (is (valid? meta/AvuListRequest
                  {:avus [{:attr  "attr1"
                           :value "value1"
                           :unit  "unit1"}
                          {:attr      "attr2"
                           :value     "value2"
                           :unit      "unit2"
                           :target_id #uuid "456e7890-e89b-12d3-a456-426614174000"}]})))

    (testing "invalid AVU list request"
      ;; Missing required field
      (is (not (valid? meta/AvuListRequest {})))

      ;; Invalid AVU requests in the list
      (is (not (valid? meta/AvuListRequest
                       {:avus [{:attr "attr1"}]})))  ; Missing required fields

      ;; Extra fields not allowed
      (is (not (valid? meta/AvuListRequest
                       {:avus  []
                        :extra "field"}))))))

(deftest test-SetAvuRequest
  (testing "SetAvuRequest validation"
    (testing "valid set AVU request"
      ;; Empty request (avus is optional)
      (is (valid? meta/SetAvuRequest {}))

      ;; With avus field
      (is (valid? meta/SetAvuRequest
                  {:avus [{:attr  "attr1"
                           :value "value1"
                           :unit  "unit1"}]})))

    (testing "invalid set AVU request"
      ;; Invalid AVUs when present
      (is (not (valid? meta/SetAvuRequest
                       {:avus [{:attr "attr1"}]})))  ; Missing required fields

      ;; Extra fields not allowed
      (is (not (valid? meta/SetAvuRequest
                       {:avus  []
                        :extra "field"}))))))

(deftest test-AvuSearchParams
  (testing "AvuSearchParams validation"
    (testing "valid search params"
      ;; Empty params (all fields optional)
      (is (valid? meta/AvuSearchParams {}))

      ;; Individual field searches
      (is (valid? meta/AvuSearchParams
                  {:attribute ["attr1" "attr2"]}))

      (is (valid? meta/AvuSearchParams
                  {:value ["value1" "value2"]}))

      (is (valid? meta/AvuSearchParams
                  {:unit ["unit1" "unit2"]}))

      (is (valid? meta/AvuSearchParams
                  {:target-id [#uuid "123e4567-e89b-12d3-a456-426614174000"
                               #uuid "456e7890-e89b-12d3-a456-426614174000"]}))

      ;; Combined searches
      (is (valid? meta/AvuSearchParams
                  {:attribute ["attr1"]
                   :value     ["value1"]
                   :unit      ["unit1"]
                   :target-id [#uuid "123e4567-e89b-12d3-a456-426614174000"]})))

    (testing "invalid search params"
      ;; Wrong types for vector fields
      (is (not (valid? meta/AvuSearchParams
                       {:attribute "not-a-vector"})))

      (is (not (valid? meta/AvuSearchParams
                       {:target-id ["not-a-uuid"]})))

      ;; Mixed types in vectors
      (is (not (valid? meta/AvuSearchParams
                       {:attribute ["valid" 123]})))

      (is (not (valid? meta/AvuSearchParams
                       {:target-id [#uuid "123e4567-e89b-12d3-a456-426614174000" "not-uuid"]})))

      ;; Extra fields not allowed
      (is (not (valid? meta/AvuSearchParams
                       {:attribute ["attr1"]
                        :extra     "field"}))))))

(deftest test-DataTypes-and-DataTypeEnum
  (testing "DataTypes and DataTypeEnum"
    (testing "DataTypes contains expected values"
      (is (= ["file" "folder"] meta/DataTypes)))

    (testing "DataTypeEnum validation"
      (is (valid? meta/DataTypeEnum "file"))
      (is (valid? meta/DataTypeEnum "folder"))
      (is (not (valid? meta/DataTypeEnum "invalid")))
      (is (not (valid? meta/DataTypeEnum "FILE")))  ; Case sensitive
      (is (not (valid? meta/DataTypeEnum nil))))))

(deftest test-schema-relationships
  (testing "Schema inheritance and composition"
    ;; AvuRequest should be like Avu but with optional fields
    (let [full-avu        {:id          #uuid "123e4567-e89b-12d3-a456-426614174000"
                           :attr        "attr"
                           :value       "value"
                           :unit        "unit"
                           :target_id   #uuid "456e7890-e89b-12d3-a456-426614174000"
                           :created_by  "user123"
                           :modified_by "user456"
                           :created_on  1640995200000
                           :modified_on 1640995200000}
          minimal-request {:attr  "attr"
                           :value "value"
                           :unit  "unit"}]

      (is (valid? meta/Avu full-avu))
      (is (not (valid? meta/Avu minimal-request)))  ; Missing required fields for Avu

      (is (valid? meta/AvuRequest full-avu))  ; Should accept full AVU data
      (is (valid? meta/AvuRequest minimal-request))  ; Should accept minimal data

      ;; AvuList vs AvuListRequest
      (is (valid? meta/AvuList {:avus [full-avu]}))
      (is (not (valid? meta/AvuList {:avus [minimal-request]})))

      (is (valid? meta/AvuListRequest {:avus [full-avu]}))
      (is (valid? meta/AvuListRequest {:avus [minimal-request]})))))

(deftest test-TargetIdParam
  (testing "TargetIdParam validation"
    (testing "valid UUIDs"
      (is (valid? meta/TargetIdParam #uuid "123e4567-e89b-12d3-a456-426614174000"))
      (is (valid? meta/TargetIdParam #uuid "a14dfe49-f65f-418b-b3c5-6497284251fe"))
      (is (valid? meta/TargetIdParam #uuid "00000000-0000-0000-0000-000000000000"))
      (is (valid? meta/TargetIdParam #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff")))

    (testing "invalid values"
      ;; Wrong types
      (is (not (valid? meta/TargetIdParam "123e4567-e89b-12d3-a456-426614174000")))  ; String UUID
      (is (not (valid? meta/TargetIdParam "not-a-uuid")))
      (is (not (valid? meta/TargetIdParam 123)))
      (is (not (valid? meta/TargetIdParam nil)))
      (is (not (valid? meta/TargetIdParam {:id "123e4567-e89b-12d3-a456-426614174000"})))
      (is (not (valid? meta/TargetIdParam []))))))
