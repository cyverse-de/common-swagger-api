(ns common-swagger-api.malli.integration-data-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.integration-data :as integration-data]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def integration-data-id #uuid "123e4567-e89b-12d3-a456-426614174000")

(def update-request {:email "user@example.com" :name "John Doe"})

(def integration-data (assoc update-request :username "johndoe" :id integration-data-id))

(deftest IntegrationDataIdPathParam
  (valid integration-data/IntegrationDataIdPathParam integration-data-id)
  (invalid integration-data/IntegrationDataIdPathParam (str integration-data-id) nil))

(deftest IntegrationDataUpdate
  (valid integration-data/IntegrationDataUpdate update-request)
  (invalid integration-data/IntegrationDataUpdate
           {}
           (dissoc update-request :name)
           (assoc update-request :email " ")
           (assoc update-request :extra 1)))

(deftest IntegrationDataRequest
  (valid integration-data/IntegrationDataRequest update-request (assoc update-request :username "johndoe"))
  (invalid integration-data/IntegrationDataRequest
           {}
           (dissoc update-request :email)
           (assoc update-request :username "")
           (assoc update-request :id integration-data-id)))

(deftest IntegrationData
  (valid integration-data/IntegrationData integration-data (dissoc integration-data :username))
  (invalid integration-data/IntegrationData
           update-request
           (assoc integration-data :id (str integration-data-id))
           (assoc integration-data :extra 1)))

(deftest IntegrationDataListing
  (valid integration-data/IntegrationDataListing
         {:integration_data [] :total 0}
         {:integration_data [integration-data] :total 1})
  (invalid integration-data/IntegrationDataListing
           {}
           {:integration_data []}
           {:integration_data [{}] :total 1}
           {:integration_data [] :total "0"}
           {:integration_data [] :total 0 :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.integration-data))
