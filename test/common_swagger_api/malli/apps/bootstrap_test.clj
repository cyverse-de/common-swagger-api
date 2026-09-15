(ns common-swagger-api.malli.apps.bootstrap-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.apps.bootstrap :as bootstrap]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def system-ids {:de_system_id "de" :all_system_ids ["de" "tapis"]})

(def workspace
  {:id               #uuid "123e4567-e89b-12d3-a456-426614174000"
   :user_id          #uuid "987e6543-e21b-32c1-b456-426614174000"
   :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
   :is_public        false
   :new_workspace    true})

(def webhook
  {:type   {:type "Slack"}
   :url    "https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX"
   :topics ["data.object.added"]})

(def bootstrap-response
  {:system_ids system-ids
   :workspace  workspace
   :webhooks   []})

(deftest SystemIds
  (valid bootstrap/SystemIds system-ids (assoc system-ids :all_system_ids []))
  (invalid bootstrap/SystemIds
           {}
           (dissoc system-ids :all_system_ids)
           (assoc system-ids :de_system_id "")
           (assoc system-ids :all_system_ids "de")
           (assoc system-ids :extra 1)))

(deftest AppsBootstrapResponse
  (valid bootstrap/AppsBootstrapResponse bootstrap-response (assoc bootstrap-response :webhooks [webhook]))
  (invalid bootstrap/AppsBootstrapResponse
           {}
           (dissoc bootstrap-response :webhooks)
           (assoc bootstrap-response :workspace (dissoc workspace :id))
           (assoc bootstrap-response :system_ids {})
           (assoc bootstrap-response :extra 1)))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps.bootstrap))
