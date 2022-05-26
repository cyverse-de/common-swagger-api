(ns common-swagger-api.schema.callbacks
  (:use [common-swagger-api.schema :only [describe]]
        [schema.core :only [defschema optional-key Keyword Any]]))

(defschema AgaveJobStatusUpdateParams
  {:status      (describe String "The status assigned to the job by Agave")
   :external-id (describe String "Agave's identifier for the job")
   :end-time    (describe String "The analysis completion timestamp")})

(defschema AgaveJobStatusUpdate
  {:lastUpdated (describe String "The time the job status was last updated")
   Keyword      Any})
