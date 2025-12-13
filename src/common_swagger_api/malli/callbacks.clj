(ns common-swagger-api.malli.callbacks
  (:require
   [malli.core :as m]))

(def AgaveJobStatusUpdateParams
  [:map {:closed true}
   [:status
    {:description         "The status assigned to the job by Agave"
     :json-schema/example "STAGING_INPUTS"}
    :string]

   [:external-id
    {:description         "Agave's identifier for the job"
     :json-schema/example "0c745662-d045-45e8-b89f-e94fd32d169b-007"}
    :string]

   [:end-time
    {:description         "The analysis completion timestamp"
     :json-schema/example "2006-05-04T03:02:01Z"}
    :string]])

(def AgaveJobStatusUpdate
  [:map
   [:lastUpdated
    {:description         "The time the job status was last updated"
     :json-schema/example "2006-05-04T03:02:01Z"}
    :string]

   [::m/default [:map-of :keyword :any]]])

(def TapisJobStatusUpdateEvent
  [:map
   [:timestamp
    {:description         "The time the job status was created"
     :json-schema/example "2006-05-04T03:02:01Z"}
    :string]

   [:type
    {:description         "The status assigned to the job by Tapis"
     :json-schema/example "STAGING_INPUTS"}
    :string]

   [:data
    {:description         "The job status JSON encoded as a string"
     :json-schema/example "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"}
    :string]

   [::m/default [:map-of :keyword :any]]])

(def TapisJobStatusUpdate
  [:map
   [:event
    {:description "The job status update event associated with this update"}
    TapisJobStatusUpdateEvent]

   [::m/default [:map-of :keyword :any]]])
