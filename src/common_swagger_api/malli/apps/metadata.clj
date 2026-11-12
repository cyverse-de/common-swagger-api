(ns common-swagger-api.malli.apps.metadata)

(def AppMetadataListingSummary "View all Metadata AVUs")
(def AppMetadataListingDocs
  (str "Lists all AVUs associated with an app. The authenticated user must have `read` permission to view this "
       "metadata."))

(def AppMetadataSetSummary "Set Metadata AVUs")
(def AppMetadataSetDocs
  (str "Sets Metadata AVUs on the app. The authenticated user must have `write` permission to edit this metadata, "
       "and the app's name must not duplicate the name of any other app (visible to the requesting user) that also "
       "has any of the ontology hierarchy AVUs given in the request."))

(def AppMetadataUpdateSummary "Add/Update Metadata AVUs")
(def AppMetadataUpdateDocs
  (str "Adds or updates Metadata AVUs on the app. The authenticated user must have `write` permission to edit this "
       "metadata, and the app's name must not duplicate the name of any other app (visible to the requesting user) "
       "that also has any of the ontology hierarchy AVUs given in the request."))
