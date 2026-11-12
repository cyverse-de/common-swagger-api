(ns common-swagger-api.malli.apps.communities
  (:require
   [common-swagger-api.malli :refer [NonBlankString]]
   [common-swagger-api.malli.metadata :refer [AvuListRequest]]
   [malli.util :as mu]))

(def AppCommunityMetadataAddSummary "Add/Update Community Metadata AVUs")
(def AppCommunityMetadataAddDocs
  (str "Adds or updates Community Metadata AVUs on the app. The authenticated user must be a community admin for "
       "every Community AVU in the request, in order to add or edit this metadata."))

(def AppCommunityMetadataDeleteSummary "Remove Community Metadata AVUs")
(def AppCommunityMetadataDeleteDocs
  (str "Removes the given Community AVUs associated with an app. The authenticated user must be a community admin "
       "for every Community AVU in the request, in order to remove those AVUs."))

(def AppCommunityAddSummary "Add an App to Communities")
(def AppCommunityAddDocs
  (str "Adds the app to each of the given communities. The authenticated user must be an admin of every community "
       "in the request. The stored tag format belongs to the service, so a caller identifies a community by its ID "
       "rather than composing the value itself."))

(def AppCommunityDeleteSummary "Remove an App from a Community")
(def AppCommunityDeleteDocs
  "Removes the app from the given community. The authenticated user must be an admin of that community.")

(def CommunityIdPathParam
  (mu/update-properties
   NonBlankString
   merge
   {:description         "The community's identifier."
    :json-schema/example "1a0b4e0c5d2f4e8ba4f9c3d2e1b0a987"}))

;; Exactly one key is required, which the service enforces; expressing that here would document an unhelpful union.
(def AppCommunityListRequest
  (-> AvuListRequest
      (mu/optional-keys [:avus])
      (mu/merge
       [:map
        [:community_ids
         {:optional            true
          :description         "The identifiers of the communities."
          :json-schema/example ["1a0b4e0c5d2f4e8ba4f9c3d2e1b0a987"]}
         [:vector NonBlankString]]])
      (mu/update-properties assoc :description "The communities to add the app to, or remove it from.")))
