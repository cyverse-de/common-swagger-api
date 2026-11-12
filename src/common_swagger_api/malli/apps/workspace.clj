(ns common-swagger-api.malli.apps.workspace)

(def WorkspaceId
  [:uuid {:description         "The workspace ID."
          :json-schema/example #uuid "123e4567-e89b-12d3-a456-426614174000"}])

(def Workspace
  [:map {:closed true}
   [:id WorkspaceId]

   [:user_id
    {:description         "The user's internal ID."
     :json-schema/example #uuid "987e6543-e21b-32c1-b456-426614174000"}
    :uuid]

   [:root_category_id
    {:description         "The ID of the user's root app category."
     :json-schema/example #uuid "456e7890-b12c-34d5-e678-901234567890"}
    :uuid]

   [:is_public
    {:description         "Indicates whether the workspace is public."
     :json-schema/example false}
    :boolean]

   [:new_workspace
    {:description         "Indicates whether the workspace was just created."
     :json-schema/example true}
    :boolean]])
