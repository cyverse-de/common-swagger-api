(ns common-swagger-api.malli.apps.bootstrap
  (:require
   [common-swagger-api.malli.apps :refer [SystemId]]
   [common-swagger-api.malli.apps.workspace :refer [Workspace]]
   [common-swagger-api.malli.webhooks :refer [WebhookList]]
   [malli.util :as mu]))

(def SystemIds
  (mu/closed-schema
   [:map
    [:de_system_id
     {:description         "The internal system ID used by the Discovery Environment."
      :json-schema/example "de"}
     SystemId]

    [:all_system_ids
     {:description         "The list of system IDs available to the Discovery Environment."
      :json-schema/example ["de" "tapis"]}
     [:vector SystemId]]]))

(def AppsBootstrapResponse
  (mu/closed-schema
   (mu/merge
    WebhookList
    [:map
     [:system_ids
      {:description "Information about system IDs available to the Discovery Environment."}
      SystemIds]

     [:workspace
      {:description "Information about the user's Discovery Environment workspace."}
      Workspace]])))
