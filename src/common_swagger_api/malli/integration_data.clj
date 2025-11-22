(ns common-swagger-api.malli.integration-data
  (:require [common-swagger-api.malli :refer [NonBlankString]]
            [malli.util :as mu]))

(def IntegrationDataIdPathParam
  [:uuid {:description         "A UUID that is used to identify the integration data record"
          :json-schema/example #uuid "123e4567-e89b-12d3-a456-426614174000"}])

(def IntegrationDataUpdate
  [:map {:closed true}
   [:email
    {:description         "The user's email address."
     :json-schema/example "user@example.com"}
    NonBlankString]

   [:name
    {:description         "The user's name."
     :json-schema/example "John Doe"}
    NonBlankString]])

(def IntegrationDataRequest
  (mu/merge
   IntegrationDataUpdate
   [:map {:closed true}
    [:username
     {:optional            true
      :description         "The username associated with the integration data entry."
      :json-schema/example "johndoe"}
     NonBlankString]]))

(def IntegrationData
  (mu/merge
   IntegrationDataRequest
   [:map {:closed true}
    [:id
     {:description         "The integration data identifier."
      :json-schema/example "123e4567-e89b-12d3-a456-426614174000"}
     :uuid]]))

(def IntegrationDataListing
  [:map {:closed true}
   [:integration_data
    {:description "The list of integration data entries."}
    [:vector IntegrationData]]

   [:total
    {:description         "The total number of matching integration data entries."
     :json-schema/example 42}
    :int]])
