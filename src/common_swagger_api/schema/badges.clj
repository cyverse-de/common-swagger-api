(ns common-swagger-api.schema.badges
  (:use [common-swagger-api.schema :only [describe NonBlankString]])
  (:require [schema.core :as s]
            [schema-tools.core :as st]
            [common-swagger-api.schema.apps :refer [AnalysisSubmission]])
  (:import [java.util UUID]))

(s/defschema Submission
  {:id
   (describe UUID "The UUID for this submission")

   :submission
   AnalysisSubmission})

(s/defschema NewSubmission
  (st/dissoc Submission :id))

(s/defschema Badge
  {:id
   (describe UUID "The UUID for the badge")

   :name
   (describe NonBlankString "The name for the badge")

   :user
   (describe NonBlankString "The username of the user that owns the object")

   :submission
   AnalysisSubmission})

(s/defschema NewBadge
  (st/dissoc Badge :id :user)) ;user should be included in the request query params

(s/defschema UpdateBadge
  (-> Badge
      (st/dissoc :id)
      (st/optional-keys-schema)))