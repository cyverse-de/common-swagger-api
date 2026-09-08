(ns common-swagger-api.schema.apps.communities
  (:require [common-swagger-api.schema :refer [describe NonBlankString]]
            [common-swagger-api.schema.metadata :refer [SetAvuRequest]]
            [schema.core :as s]))

(def AppCommunityMetadataDeleteSummary "Remove an App from Communities")
(def AppCommunityMetadataDeleteDocs
  "Removes the app from each of the given communities.
   The authenticated user must be an admin of every community in the request.
   A caller identifies each community by its ID in `community_ids`. The `avus`
   list is accepted as a deprecated alternative.")

(def AppCommunityAddSummary "Add an App to Communities")
(def AppCommunityAddDocs
  "Adds the app to each of the given communities.
   The authenticated user must be an admin of every community in the request.
   A caller identifies each community by its ID in `community_ids`. The `avus`
   list is accepted as a deprecated alternative.")

(def AppCommunityDeleteSummary "Remove an App from a Community")
(def AppCommunityDeleteDocs
  "Removes the app from the given community.
   The authenticated user must be an admin of that community.")

(def CommunityIdPathParam (describe NonBlankString "The community's identifier."))

;; Both keys are optional so that one route can serve the current request shape
;; and the AVU list a browser holding a stale bundle still sends. Exactly one is
;; required, which the service enforces -- expressing "one or the other" here
;; would render as an unhelpful union in the generated documentation.
(s/defschema AppCommunityListRequest
  (-> SetAvuRequest
      (assoc (s/optional-key :community_ids)
             (describe [NonBlankString] "The identifiers of the communities."))
      (describe "The communities to add the app to, or remove it from.")))
