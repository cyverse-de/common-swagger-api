(ns common-swagger-api.schema.apps.communities
  (:require [common-swagger-api.schema :refer [->optional-param describe NonBlankString]]
            [common-swagger-api.schema.metadata :refer [AvuListRequest]]
            [schema.core :as s]))

(def AppCommunityMetadataAddSummary "Add/Update Community Metadata AVUs")
(def AppCommunityMetadataAddDocs
  "Adds or updates Community Metadata AVUs on the app.
   The authenticated user must be a community admin for every Community AVU in the request,
   in order to add or edit this metadata.")

(def AppCommunityMetadataDeleteSummary "Remove Community Metadata AVUs")
(def AppCommunityMetadataDeleteDocs
  "Removes the given Community AVUs associated with an app.
   The authenticated user must be a community admin for every Community AVU in the request,
   in order to remove those AVUs.")

(def AppCommunityAddSummary "Add an App to Communities")
(def AppCommunityAddDocs
  "Adds the app to each of the given communities.
   The authenticated user must be an admin of every community in the request.
   The stored tag format belongs to the service, so a caller identifies a community
   by its ID rather than composing the value itself.")

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
  (-> AvuListRequest
      (->optional-param :avus)
      (assoc (s/optional-key :community_ids)
             (describe [NonBlankString] "The identifiers of the communities."))
      (describe "The communities to add the app to, or remove it from.")))
