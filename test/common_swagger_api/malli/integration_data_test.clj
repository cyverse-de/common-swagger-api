(ns common-swagger-api.malli.integration-data-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.integration-data :as id]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-IntegrationDataUpdate
  (testing "IntegrationDataUpdate validation"
    (testing "valid update data"
      (is (valid? id/IntegrationDataUpdate
                  {:email "user@example.com"
                   :name "John Doe"}))
      (is (valid? id/IntegrationDataUpdate
                  {:email "test.user@company.org"
                   :name "Test User"})))

    (testing "invalid update data"
      ;; Missing required fields
      (is (not (valid? id/IntegrationDataUpdate
                       {:email "user@example.com"})))
      (is (not (valid? id/IntegrationDataUpdate
                       {:name "John Doe"})))
      (is (not (valid? id/IntegrationDataUpdate {})))

      ;; Empty strings not allowed (NonBlankString)
      (is (not (valid? id/IntegrationDataUpdate
                       {:email ""
                        :name "John Doe"})))
      (is (not (valid? id/IntegrationDataUpdate
                       {:email "user@example.com"
                        :name ""})))
      (is (not (valid? id/IntegrationDataUpdate
                       {:email "   "
                        :name "John Doe"})))
      (is (not (valid? id/IntegrationDataUpdate
                       {:email "user@example.com"
                        :name "   "})))

      ;; Extra fields not allowed due to :closed true
      (is (not (valid? id/IntegrationDataUpdate
                       {:email "user@example.com"
                        :name "John Doe"
                        :extra "field"})))

      ;; Wrong types
      (is (not (valid? id/IntegrationDataUpdate
                       {:email 123
                        :name "John Doe"})))
      (is (not (valid? id/IntegrationDataUpdate
                       {:email "user@example.com"
                        :name 456}))))))

(deftest test-IntegrationDataRequest
  (testing "IntegrationDataRequest validation"
    (testing "valid request data"
      (is (valid? id/IntegrationDataRequest
                  {:email "user@example.com"
                   :name "John Doe"}))
      (is (valid? id/IntegrationDataRequest
                  {:email "user@example.com"
                   :name "John Doe"
                   :username "johndoe"}))
      (is (valid? id/IntegrationDataRequest
                  {:email "test@company.org"
                   :name "Test User"
                   :username "testuser"})))

    (testing "invalid request data"
      ;; Missing required fields from base schema
      (is (not (valid? id/IntegrationDataRequest
                       {:username "johndoe"})))
      (is (not (valid? id/IntegrationDataRequest
                       {:email "user@example.com"
                        :username "johndoe"})))

      ;; Empty username not allowed (NonBlankString)
      (is (not (valid? id/IntegrationDataRequest
                       {:email "user@example.com"
                        :name "John Doe"
                        :username ""})))
      (is (not (valid? id/IntegrationDataRequest
                       {:email "user@example.com"
                        :name "John Doe"
                        :username "   "})))

      ;; Extra fields not allowed
      (is (not (valid? id/IntegrationDataRequest
                       {:email "user@example.com"
                        :name "John Doe"
                        :username "johndoe"
                        :extra "field"}))))))

(deftest test-IntegrationData
  (testing "IntegrationData validation"
    (testing "valid integration data"
      (is (valid? id/IntegrationData
                  {:email "user@example.com"
                   :name "John Doe"
                   :id #uuid "123e4567-e89b-12d3-a456-426614174000"}))
      (is (valid? id/IntegrationData
                  {:email "user@example.com"
                   :name "John Doe"
                   :username "johndoe"
                   :id #uuid "123e4567-e89b-12d3-a456-426614174000"}))
      (is (valid? id/IntegrationData
                  {:email "test@company.org"
                   :name "Test User"
                   :username "testuser"
                   :id #uuid "456e7890-e89b-12d3-a456-426614174000"})))

    (testing "invalid integration data"
      ;; Missing required id field
      (is (not (valid? id/IntegrationData
                       {:email "user@example.com"
                        :name "John Doe"})))
      (is (not (valid? id/IntegrationData
                       {:email "user@example.com"
                        :name "John Doe"
                        :username "johndoe"})))

      ;; Missing required fields from base schemas
      (is (not (valid? id/IntegrationData
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"})))
      (is (not (valid? id/IntegrationData
                       {:email "user@example.com"
                        :id #uuid "123e4567-e89b-12d3-a456-426614174000"})))

      ;; Wrong type for id
      (is (not (valid? id/IntegrationData
                       {:email "user@example.com"
                        :name "John Doe"
                        :id "not-a-uuid"})))
      (is (not (valid? id/IntegrationData
                       {:email "user@example.com"
                        :name "John Doe"
                        :id 123})))

      ;; Extra fields not allowed
      (is (not (valid? id/IntegrationData
                       {:email "user@example.com"
                        :name "John Doe"
                        :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :extra "field"}))))))

(deftest test-IntegrationDataListing
  (testing "IntegrationDataListing validation"
    (testing "valid listing data"
      (is (valid? id/IntegrationDataListing
                  {:integration_data []
                   :total 0}))
      (is (valid? id/IntegrationDataListing
                  {:integration_data [{:email "user1@example.com"
                                       :name "User One"
                                       :id #uuid "123e4567-e89b-12d3-a456-426614174000"}
                                      {:email "user2@example.com"
                                       :name "User Two"
                                       :username "user2"
                                       :id #uuid "456e7890-e89b-12d3-a456-426614174000"}]
                   :total 2}))
      (is (valid? id/IntegrationDataListing
                  {:integration_data [{:email "user@example.com"
                                       :name "User"
                                       :username "user"
                                       :id #uuid "123e4567-e89b-12d3-a456-426614174000"}]
                   :total 1})))

    (testing "invalid listing data"
      ;; Missing required fields
      (is (not (valid? id/IntegrationDataListing
                       {:integration_data []})))
      (is (not (valid? id/IntegrationDataListing
                       {:total 0})))
      (is (not (valid? id/IntegrationDataListing {})))

      ;; Wrong types
      (is (not (valid? id/IntegrationDataListing
                       {:integration_data "not-a-vector"
                        :total 0})))
      (is (not (valid? id/IntegrationDataListing
                       {:integration_data []
                        :total "not-a-number"})))

      ;; Invalid items in integration_data vector
      (is (not (valid? id/IntegrationDataListing
                       {:integration_data [{:email "user@example.com"}]  ; Missing required fields
                        :total 1})))
      (is (not (valid? id/IntegrationDataListing
                       {:integration_data [{:email "user@example.com"
                                            :name "User"
                                            :id "not-a-uuid"}]
                        :total 1})))

      ;; Extra fields not allowed
      (is (not (valid? id/IntegrationDataListing
                       {:integration_data []
                        :total 0
                        :extra "field"}))))))

(deftest test-schema-inheritance
  (testing "Schema inheritance and composition"
    ;; IntegrationDataRequest should accept all IntegrationDataUpdate fields
    (let [update-data {:email "user@example.com"
                       :name "John Doe"}
          request-data (assoc update-data :username "johndoe")
          integration-data (assoc request-data :id #uuid "123e4567-e89b-12d3-a456-426614174000")]

      (is (valid? id/IntegrationDataUpdate update-data))
      (is (valid? id/IntegrationDataRequest update-data))  ; Should work without username
      (is (valid? id/IntegrationDataRequest request-data))
      (is (valid? id/IntegrationData integration-data))

      ;; But not the other way around
      (is (not (valid? id/IntegrationDataUpdate request-data)))  ; username not allowed
      (is (not (valid? id/IntegrationDataRequest integration-data)))  ; id not allowed
      (is (not (valid? id/IntegrationDataUpdate integration-data))))))

(deftest test-IntegrationDataIdPathParam
  (testing "IntegrationDataIdPathParam validation"
    (testing "valid UUIDs"
      (is (valid? id/IntegrationDataIdPathParam #uuid "123e4567-e89b-12d3-a456-426614174000"))
      (is (valid? id/IntegrationDataIdPathParam #uuid "456e7890-b12c-34d5-e678-901234567890"))
      (is (valid? id/IntegrationDataIdPathParam #uuid "00000000-0000-0000-0000-000000000000"))
      (is (valid? id/IntegrationDataIdPathParam #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff")))

    (testing "invalid values"
      ;; Wrong types
      (is (not (valid? id/IntegrationDataIdPathParam "123e4567-e89b-12d3-a456-426614174000")))  ; String UUID
      (is (not (valid? id/IntegrationDataIdPathParam "not-a-uuid")))
      (is (not (valid? id/IntegrationDataIdPathParam 123)))
      (is (not (valid? id/IntegrationDataIdPathParam nil)))
      (is (not (valid? id/IntegrationDataIdPathParam {:id "123e4567-e89b-12d3-a456-426614174000"})))
      (is (not (valid? id/IntegrationDataIdPathParam []))))))
