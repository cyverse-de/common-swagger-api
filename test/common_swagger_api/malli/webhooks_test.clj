(ns common-swagger-api.malli.webhooks-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]
   [common-swagger-api.malli.webhooks :as webhooks]))

(def webhook-id #uuid "123e4567-e89b-12d3-a456-426614174000")

(def webhook-type {:type "Slack"})

(def webhook
  {:type   webhook-type
   :url    "https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX"
   :topics ["data.object.added"]})

(def topic
  {:id    #uuid "789a0123-c45d-67e8-f901-234567890abc"
   :topic "data.object.added"})

(deftest WebhookIdParam
  (valid webhooks/WebhookIdParam webhook-id)
  (invalid webhooks/WebhookIdParam (str webhook-id) nil))

(deftest WebhookType
  (valid webhooks/WebhookType
         webhook-type
         (assoc webhook-type :id #uuid "456e7890-b12c-34d5-e678-901234567890" :template "{{event}}"))
  (invalid webhooks/WebhookType
           {}
           (assoc webhook-type :type " ")
           (assoc webhook-type :id "456e7890-b12c-34d5-e678-901234567890")
           (assoc webhook-type :extra 1)))

(deftest Webhook
  (valid webhooks/Webhook webhook (assoc webhook :id webhook-id :topics []))
  (invalid webhooks/Webhook
           {}
           (dissoc webhook :url)
           (assoc webhook :type "Slack")
           (assoc webhook :topics "data.object.added")
           (assoc webhook :extra 1)))

(deftest WebhookList
  (valid webhooks/WebhookList {:webhooks []} {:webhooks [webhook]})
  (invalid webhooks/WebhookList {} {:webhooks "x"} {:webhooks [{}]} {:webhooks [] :extra 1}))

(deftest Topic
  (valid webhooks/Topic topic)
  (invalid webhooks/Topic {} (dissoc topic :topic) (assoc topic :id (str (:id topic))) (assoc topic :extra 1)))

(deftest TopicList
  (valid webhooks/TopicList {:topics []} {:topics [topic]})
  (invalid webhooks/TopicList {} {:topics "x"} {:topics [{}]} {:topics [] :extra 1}))

(deftest WebhookTypeList
  (valid webhooks/WebhookTypeList {:webhooktypes []} {:webhooktypes [webhook-type]})
  (invalid webhooks/WebhookTypeList {} {:webhooktypes "x"} {:webhooktypes [{}]} {:webhooktypes [] :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.webhooks))

(deftest examples
  (examples-valid 'common-swagger-api.malli.webhooks))
