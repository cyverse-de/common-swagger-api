(ns common-swagger-api.malli.apps.workspace-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.apps.workspace :as workspace]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]))

(def workspace-id #uuid "123e4567-e89b-12d3-a456-426614174000")

(def workspace
  {:id               workspace-id
   :user_id          #uuid "987e6543-e21b-32c1-b456-426614174000"
   :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
   :is_public        false
   :new_workspace    true})

(deftest WorkspaceId
  (valid workspace/WorkspaceId workspace-id)
  (invalid workspace/WorkspaceId (str workspace-id) nil))

(deftest Workspace
  (valid workspace/Workspace workspace (assoc workspace :is_public true :new_workspace false))
  (invalid workspace/Workspace
           {}
           (dissoc workspace :root_category_id)
           (assoc workspace :user_id "987e6543-e21b-32c1-b456-426614174000")
           (assoc workspace :is_public "false")
           (assoc workspace :extra 1)))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps.workspace))

(deftest examples
  (examples-valid 'common-swagger-api.malli.apps.workspace))
