(ns common-swagger-api.malli.callbacks-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.callbacks :as callbacks]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]))

(def agave-params
  {:status      "STAGING_INPUTS"
   :external-id "0c745662-d045-45e8-b89f-e94fd32d169b-007"
   :end-time    "2006-05-04T03:02:01Z"})

(def agave-update {:lastUpdated "2006-05-04T03:02:01Z"})

(def tapis-event
  {:timestamp "2006-05-04T03:02:01Z"
   :type      "STAGING_INPUTS"
   :data      "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"})

(deftest AgaveJobStatusUpdateParams
  (valid callbacks/AgaveJobStatusUpdateParams agave-params)
  (invalid callbacks/AgaveJobStatusUpdateParams
           {}
           (dissoc agave-params :end-time)
           (assoc agave-params :status :STAGING_INPUTS)
           (assoc agave-params :extra 1)))

(deftest AgaveJobStatusUpdate
  ;; The ::m/default entry lets any additional keyword key through.
  (valid callbacks/AgaveJobStatusUpdate agave-update (assoc agave-update :id "abc" :status "RUNNING"))
  (invalid callbacks/AgaveJobStatusUpdate {} (assoc agave-update :lastUpdated 1) (assoc agave-update "extra" 1)))

(deftest TapisJobStatusUpdateEvent
  (valid callbacks/TapisJobStatusUpdateEvent tapis-event (assoc tapis-event :tenant "cyverse"))
  (invalid callbacks/TapisJobStatusUpdateEvent
           {}
           (dissoc tapis-event :data)
           (assoc tapis-event :type 1)
           (assoc tapis-event "extra" 1)))

(deftest TapisJobStatusUpdate
  (valid callbacks/TapisJobStatusUpdate {:event tapis-event} {:event tapis-event :uuid "abc"})
  (invalid callbacks/TapisJobStatusUpdate
           {}
           {:event (dissoc tapis-event :timestamp)}
           {:event tapis-event "extra" 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.callbacks))

(deftest examples
  (examples-valid 'common-swagger-api.malli.callbacks))
