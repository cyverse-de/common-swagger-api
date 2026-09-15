(ns common-swagger-api.malli.tools.admin
  (:require
   [common-swagger-api.malli :refer [CommonResponses ErrorResponseExists ErrorResponseNotFound
                                     ErrorResponseNotWritable]]
   [common-swagger-api.malli.tools :as tools]
   [malli.util :as mu]))

(def ToolDeleteSummary "Delete a Tool")
(def ToolDeleteDocs
  "Deletes a tool, as long as it is not in use by any apps.")

(def ToolDetailsDocs "This endpoint returns the details for one tool.")

(def ToolsImportSummary "Add new Tools.")

(def ToolInstallRequestDeleteSummary "Delete a Tool Request")
(def ToolInstallRequestDeleteDocs
  (str "This service allows administrators to delete a tool request. This endpoint is primarily intended for use in "
       "the QA cleanup suite."))

(def ToolInstallRequestDetailsSummary "Obtain Tool Request Details")
(def ToolInstallRequestDetailsDocs
  (str "This service obtains detailed information about a tool request. This is the service that the DE support team "
       "uses to obtain the request details."))

(def ToolInstallRequestListingDocs
  (str "This endpoint lists high level details about tool requests that have been submitted. Administrators may use "
       "this endpoint to track tool requests for all users."))

(def ToolInstallRequestStatusCodeDeleteSummary "Delete a Tool Request Status Code")
(def ToolInstallRequestStatusCodeDeleteDocs
  (str "This service allows administrators to delete a tool request status code provided that the status code isn't "
       "in use in a tool request."))

(def ToolInstallRequestStatusUpdateSummary "Update the Status of a Tool Request")
(def ToolInstallRequestStatusUpdateDocs
  "This endpoint is used by Discovery Environment administrators to update the status of a tool request.")

(def ToolIntegrationUpdateSummary "Update the Integration Data Record for a Tool")
(def ToolIntegrationUpdateDocs
  "This service allows administrators to change the integration data record associated with a tool.")

(def ToolListingDocs "This endpoint allows admins to get a listing of all Tools.")

(def ToolPublishSummary "Make a Private Tool Public")
(def ToolPublishDocs
  (str "This service makes a Private Tool public and available to all users. The request body fields are optional "
       "and allow the admin to make updates to the tool in the same request."))

(def ToolUpdateSummary "Update a Tool")

(def ToolIdsList
  [:map {:closed true}
   [:tool_ids
    {:description         "A List of Tool IDs"
     :json-schema/example [#uuid "123e4567-e89b-12d3-a456-426614174000"]}
    [:vector :uuid]]])

(def ToolUpdateParams
  [:map {:closed true}
   [:overwrite-public
    {:optional            true
     :description         "Flag to force container settings updates of public tools."
     :json-schema/example false}
    :boolean]])

(def ToolsImportRequest
  (mu/update-properties
   [:map {:closed true}
    [:tools
     {:description "zero or more Tool definitions"}
     [:vector tools/ToolImportRequest]]]
   assoc :description "The Tools to import."))

(def ToolUpdateRequest
  (-> tools/ToolImportRequest
      (mu/optional-keys [:name :version :type :implementation :container])
      (mu/update-properties assoc :description "The Tool to update.")))

(def ToolRequestStatusUpdate
  (-> tools/ToolRequestStatus
      (mu/dissoc :updated_by)
      (mu/dissoc :status_date)
      (mu/update-properties assoc :description "A Tool Request status update.")))

(def ToolDeleteResponses
  (merge CommonResponses
         {200 {:description "The Tool was successfully deleted."}
          400 {:body        ErrorResponseNotWritable
               :description "The Tool is already in use by apps and could not be deleted."}
          404 {:body        ErrorResponseNotFound
               :description "A Tool with the given `tool-id` does not exist."}}))

(def ToolDetailsResponses
  (merge CommonResponses
         {200 {:body        tools/ToolDetails
               :description "The Tool details."}
          404 {:body        ErrorResponseNotFound
               :description "The `tool-id` does not exist."}}))

(def ToolsImportResponses
  (merge CommonResponses
         {200 {:body        ToolIdsList
               :description "A list of the new Tool IDs."}
          400 {:body        ErrorResponseExists
               :description "A Tool with the given `name` already exists."}}))

(def ToolPublishResponses
  (merge CommonResponses
         {200 {:body        tools/ToolDetails
               :description "The Tool details."}
          400 {:body        ErrorResponseNotWritable
               :description "The Tool is already public."}
          404 {:body        ErrorResponseNotFound
               :description "The `tool-id` does not exist."}}))

(def ToolUpdateResponses
  (merge CommonResponses
         {200 {:body        tools/ToolDetails
               :description "The Tool details."}
          400 {:body        ErrorResponseNotWritable
               :description "The Tool is in use by public apps its container could not be updated."}
          404 {:body        ErrorResponseNotFound
               :description "The `tool-id` does not exist."}}))
