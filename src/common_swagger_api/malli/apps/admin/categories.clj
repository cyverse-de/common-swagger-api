(ns common-swagger-api.malli.apps.admin.categories
  (:require
   [common-swagger-api.malli.apps :refer [SystemId]]
   [common-swagger-api.malli.apps.categories :as categories-schema]
   [malli.util :as mu]))

(def AppCategorizationSummary "Categorize Apps")
(def AppCategorizationDocs
  "This endpoint is used by the Admin interface to add or move Apps to into multiple Categories.")

(def AppCategoryIdList
  [:map {:closed true}
   [:category_ids
    {:description "A List of App Category identifiers"}
    [:vector categories-schema/AppCategoryId]]])

(def AppCategorization
  (mu/merge
   AppCategoryIdList
   [:map
    [:system_id SystemId]

    [:app_id
     {:description         "The ID of the App to be Categorized"
      :json-schema/example "app-id-12345"}
     :string]]))

(def AppCategorizationRequest
  (mu/update-properties
   [:map {:closed true}
    [:categories
     {:description "Apps and the Categories they should be listed under"}
     [:vector AppCategorization]]]
   assoc :description "An App Categorization Request."))
