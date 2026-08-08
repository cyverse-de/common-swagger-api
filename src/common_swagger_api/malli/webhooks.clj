(ns common-swagger-api.malli.webhooks
  (:require
   [common-swagger-api.malli :refer [NonBlankString]]))

;; Endpoint description definitions
(def GetWebhooksSummary "List Webhooks")
(def GetWebhooksDesc "Returns all of the webhooks defined for the user.")
(def PutWebhooksSummary "Add a Webhook")
(def PutWebhooksDesc "Adds a new webhook to the system.")
(def GetWebhooksTopicSummary "List notification topics")
(def GetWebhooksTopicDesc "Returns all of the notification topics defined")
(def GetWebhookTypesSummary "List webhooks types")
(def GetWebhookTypesDesc "Returns all webhook types defined")

;; Field definitions

(def WebhookIdParam
  [:uuid {:description         "A UUID that is used to identify the Webhook"
          :json-schema/example #uuid "123e4567-e89b-12d3-a456-426614174000"}])

;; Schema definitions

(def WebhookType
  [:map {:closed true}
   [:id
    {:optional            true
     :description         "A UUID for the type"
     :json-schema/example #uuid "456e7890-b12c-34d5-e678-901234567890"}
    :uuid]

   [:type
    {:description         "Webhook type"
     :json-schema/example "Slack"}
    NonBlankString]

   [:template
    {:optional            true
     :description         "Template for this Webhook type"
     :json-schema/example "{{event}} occurred for {{resource}} at {{timestamp}}"}
    :string]])

(def Webhook
  [:map {:closed true}
   [:id
    {:optional true}
    WebhookIdParam]

   [:type
    {:description "Type of webhook subscription"}
    WebhookType]

   [:url
    {:description         "Url to post the notification"
     :json-schema/example "https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX"}
    NonBlankString]

   [:topics
    {:description         "A List of topic names"
     :json-schema/example ["data.object.added" "data.object.updated" "data.object.removed"]}
    [:vector NonBlankString]]])

(def WebhookList
  [:map {:closed true}
   [:webhooks
    {:description "A List of webhooks"}
    [:vector Webhook]]])

(def Topic
  [:map {:closed true}
   [:id
    {:description         "A UUID for the topic"
     :json-schema/example #uuid "789a0123-c45d-67e8-f901-234567890abc"}
    :uuid]

   [:topic
    {:description         "The topic"
     :json-schema/example "data.object.added"}
    NonBlankString]])

(def TopicList
  [:map {:closed true}
   [:topics
    {:description "A List of topics"}
    [:vector Topic]]])

(def WebhookTypeList
  [:map {:closed true}
   [:webhooktypes
    {:description "A List of webhook types"}
    [:vector WebhookType]]])
