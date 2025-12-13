(ns common-swagger-api.malli.apps.communities)

(def AppCommunityMetadataAddSummary "Add/Update Community Metadata AVUs")
(def AppCommunityMetadataAddDocs
  (str "Adds or updates Community Metadata AVUs on the app. The authenticated user must be a community admin for "
       "every Community AVU in the request, in order to add or edit this metadata."))

(def AppCommunityMetadataDeleteSummary "Remove Community Metadata AVUs")
(def AppCommunityMetadataDeleteDocs
  (str "Removes the given Community AVUs associated with an app. The authenticated user must be a community admin "
       "for every Community AVU in the request, in order to remove those AVUs."))
