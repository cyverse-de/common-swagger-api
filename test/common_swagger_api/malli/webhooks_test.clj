(ns common-swagger-api.malli.webhooks-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.webhooks :as w]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-WebhookIdParam
  (testing "WebhookIdParam validation"
    (is (valid? w/WebhookIdParam #uuid "123e4567-e89b-12d3-a456-426614174000"))
    (is (not (valid? w/WebhookIdParam "not-a-uuid")))
    (is (not (valid? w/WebhookIdParam 123)))))

(deftest test-WebhookType
  (testing "WebhookType validation"
    (testing "valid webhook type"
      (is (valid? w/WebhookType
                  {:type "Slack"}))
      (is (valid? w/WebhookType
                  {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :type "Slack"}))
      (is (valid? w/WebhookType
                  {:type "Email"
                   :template "New {{event}} on {{date}}"}))
      (is (valid? w/WebhookType
                  {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :type "Teams"
                   :template "{{user}} triggered {{event}}"})))

    (testing "invalid webhook type"
      (is (not (valid? w/WebhookType {})))
      (is (not (valid? w/WebhookType {:id #uuid "456e7890-b12c-34d5-e678-901234567890"})))
      (is (not (valid? w/WebhookType {:template "template without type"})))
      (is (not (valid? w/WebhookType {:type ""})))
      (is (not (valid? w/WebhookType {:type "   "}))))))

(deftest test-Webhook
  (testing "Webhook validation"
    (testing "valid webhook"
      (is (valid? w/Webhook
                  {:type {:type "Slack"}
                   :url "https://hooks.slack.com/services/T000/B000/XXX"
                   :topics ["data.added"]}))
      (is (valid? w/Webhook
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :type {:type "Email"
                          :template "Event: {{event}}"}
                   :url "https://webhook.example.com/notify"
                   :topics ["data.added" "data.updated" "data.removed"]}))
      (is (valid? w/Webhook
                  {:type {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                          :type "Teams"}
                   :url "https://teams.webhook.office.com/webhookb2/..."
                   :topics ["app.execution.completed"]})))

    (testing "invalid webhook"
      (is (not (valid? w/Webhook {})))
      (is (not (valid? w/Webhook {:type {:type "Slack"}
                                   :url "https://example.com"})))
      (is (not (valid? w/Webhook {:type {:type "Slack"}
                                   :topics ["topic1"]})))
      (is (not (valid? w/Webhook {:url "https://example.com"
                                   :topics ["topic1"]})))
      (is (not (valid? w/Webhook {:type {:type "Slack"}
                                   :url ""
                                   :topics ["topic1"]})))
      (is (not (valid? w/Webhook {:type {:type "Slack"}
                                   :url "https://example.com"
                                   :topics nil}))))))

(deftest test-WebhookList
  (testing "WebhookList validation"
    (testing "valid webhook list"
      (is (valid? w/WebhookList {:webhooks []}))
      (is (valid? w/WebhookList
                  {:webhooks [{:type {:type "Slack"}
                               :url "https://hooks.slack.com/services/XXX"
                               :topics ["data.added"]}]}))
      (is (valid? w/WebhookList
                  {:webhooks [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                               :type {:type "Slack"}
                               :url "https://hooks.slack.com/services/XXX"
                               :topics ["data.added"]}
                              {:type {:type "Email"
                                     :template "{{event}}"}
                               :url "https://email.webhook.com"
                               :topics ["app.completed"]}]})))

    (testing "invalid webhook list"
      (is (not (valid? w/WebhookList {})))
      (is (not (valid? w/WebhookList {:webhooks nil})))
      (is (not (valid? w/WebhookList {:webhooks "not-a-vector"})))
      (is (not (valid? w/WebhookList {:webhooks [{}]}))))))

(deftest test-Topic
  (testing "Topic validation"
    (testing "valid topic"
      (is (valid? w/Topic
                  {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                   :topic "data.object.added"}))
      (is (valid? w/Topic
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :topic "app.execution.started"}))
      (is (valid? w/Topic
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :topic "user.login"})))

    (testing "invalid topic"
      (is (not (valid? w/Topic {})))
      (is (not (valid? w/Topic {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"})))
      (is (not (valid? w/Topic {:topic "data.added"})))
      (is (not (valid? w/Topic {:id "not-a-uuid" :topic "data.added"})))
      (is (not (valid? w/Topic {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                                :topic ""})))
      (is (not (valid? w/Topic {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                                :topic "   "}))))))

(deftest test-TopicList
  (testing "TopicList validation"
    (testing "valid topic list"
      (is (valid? w/TopicList {:topics []}))
      (is (valid? w/TopicList
                  {:topics [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                             :topic "data.object.added"}]}))
      (is (valid? w/TopicList
                  {:topics [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                             :topic "data.object.added"}
                            {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                             :topic "app.execution.completed"}
                            {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                             :topic "user.login"}]})))

    (testing "invalid topic list"
      (is (not (valid? w/TopicList {})))
      (is (not (valid? w/TopicList {:topics nil})))
      (is (not (valid? w/TopicList {:topics "not-a-vector"})))
      (is (not (valid? w/TopicList {:topics [{}]}))))))

(deftest test-WebhookTypeList
  (testing "WebhookTypeList validation"
    (testing "valid webhook type list"
      (is (valid? w/WebhookTypeList {:webhooktypes []}))
      (is (valid? w/WebhookTypeList
                  {:webhooktypes [{:type "Slack"}]}))
      (is (valid? w/WebhookTypeList
                  {:webhooktypes [{:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                                   :type "Slack"}
                                  {:type "Email"
                                   :template "{{event}} notification"}
                                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                   :type "Teams"
                                   :template "{{user}}: {{event}}"}]})))

    (testing "invalid webhook type list"
      (is (not (valid? w/WebhookTypeList {})))
      (is (not (valid? w/WebhookTypeList {:webhooktypes nil})))
      (is (not (valid? w/WebhookTypeList {:webhooktypes "not-a-vector"})))
      (is (not (valid? w/WebhookTypeList {:webhooktypes [{}]}))))))

(deftest test-endpoint-descriptions
  (testing "Endpoint descriptions are defined"
    (is (string? w/GetWebhooksSummary))
    (is (string? w/GetWebhooksDesc))
    (is (string? w/PutWebhooksSummary))
    (is (string? w/PutWebhooksDesc))
    (is (string? w/GetWebhooksTopicSummary))
    (is (string? w/GetWebhooksTopicDesc))
    (is (string? w/GetWebhookTypesSummary))
    (is (string? w/GetWebhookTypesDesc))))
