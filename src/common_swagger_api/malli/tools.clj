(ns common-swagger-api.malli.tools
  (:require
   [clojure-commons.error-codes :as ce]
   [common-swagger-api.malli :refer [ErrorResponse PagingParams]]
   [common-swagger-api.malli.common :refer [IncludeHiddenParams]]
   [common-swagger-api.malli.containers :refer [Image NewToolContainer
                                                Settings ToolContainer]]
   [malli.util :as mu]))

;; Field and endpoint description definitions.
(def ToolAddSummary "Add Private Tool")

(def ToolAppListingSummary "Get Apps by Tool")
(def ToolAppListingDocs "This endpoint returns a listing of Apps using the given Tool.")

(def ToolDeleteSummary "Delete a Private Tool")
(def ToolDeleteDocs
  (str "Deletes a private Tool, as long as it is not in use by any Apps. The requesting user must have ownership "
       "permission for the Tool. If the Tool is already in use in private Apps, then an `ERR_NOT_WRITEABLE` will "
       "be returned along with a listing of the Apps using this Tool, unless the `force-delete` flag is set to "
       "`true`."))

(def ToolDetailsSummary "Get a Tool")
(def ToolDetailsDocs "This endpoint returns the details for one tool accessible to the user.")

(def ToolInstallRequestSummary "Request Tool Installation")
(def ToolInstallRequestDocs
  (str "This service submits a request for a tool to be installed so that it can be used from within the Discovery "
       "Environment. The installation request and all status updates related to the tool request will be tracked in "
       "the Discovery Environment database."))

(def ToolInstallRequestListingSummary "List Tool Requests")
(def ToolInstallRequestListingDocs
  (str "This endpoint lists high level details about tool requests that have been submitted. A user may track their "
       "own tool requests with this endpoint."))

(def ToolInstallRequestStatusCodeListingSummary "List Tool Request Status Codes")
(def ToolInstallRequestStatusCodeListingDocs
  (str "Tool request status codes are largely arbitrary, but once they've been used once, they're stored in the "
       "database so that they can be reused easily. This endpoint allows the caller to list the known status codes."))

(def ToolIntegrationDataListingSummary "Return the Integration Data Record for a Tool")
(def ToolIntegrationDataListingDocs "This service returns the integration data associated with an app.")

(def ToolListingSummary "List Tools")
(def ToolListingDocs "This endpoint allows users to get a listing of all Tools accessible to the user.")

(def ToolPermissionsListingSummary "List Tool Permissions")
(def ToolPermissionsListingDocs
  (str "This endpoint allows the caller to list the permissions for one or more Tools. The authenticated user must "
       "have read permission on every Tool in the request body for this endpoint to succeed."))

(def ToolUpdateSummary "Update a Private Tool")

;; Field definitions

(def ToolIdParam
  [:uuid {:description         "A UUID that is used to identify the Tool"
          :json-schema/example #uuid "123e4567-e89b-12d3-a456-426614174000"}])

(def ToolRequestIdParam
  [:uuid {:description         "The Tool Requests's UUID"
          :json-schema/example #uuid "987e6543-e21b-32c1-b456-426614174000"}])

(def ToolRequestToolIdParam
  [:uuid {:description         "The ID of the tool the user is requesting to be made public"
          :json-schema/example #uuid "567e8901-c34d-56e7-f890-123456789012"}])

(def ToolNameParam
  [:string {:description         "The Tool's name (can be the file name or Docker image)"
            :json-schema/example "samtools"}])

(def ToolDescriptionParam
  [:string {:description         "A brief description of the Tool"
            :json-schema/example "A suite of programs for interacting with high-throughput sequencing data"}])

(def VersionParam
  [:string {:description         "The Tool's version"
            :json-schema/example "1.15.1"}])

(def AttributionParam
  [:string {:description         "The Tool's author or publisher"
            :json-schema/example "Heng Li et al."}])

(def SubmittedByParam
  [:string {:description         "The username of the user that submitted the Tool Request"
            :json-schema/example "johndoe"}])

(def ToolImplementationDocs "Information about the user who integrated the Tool into the DE")

(def ToolRequestStatusCodeId
  [:uuid {:description         "The Status Code's UUID"
          :json-schema/example #uuid "abc12345-def6-7890-1234-567890abcdef"}])

(def Interactive
  [:boolean {:description         "Determines whether the tool is interactive."
             :json-schema/example false}])

;; Schema definitions

(def ToolSearchParams
  (-> IncludeHiddenParams
      (mu/merge PagingParams)
      (mu/merge
       [:map {:closed true}
        [:search
         {:optional            true
          :description         "The pattern to match in a Tool's Name or Description."
          :json-schema/example "blast"}
         :string]

        [:public
         {:optional            true
          :description         (str "Set to `true` to list only public Tools, `false` to list only private Tools, "
                                    "or leave unset to list all Tools.")
          :json-schema/example true}
         :boolean]])))

(def ToolDetailsParams
  [:map {:closed true}
   [:include-defaults
    {:optional            true
     :description         "Flag to include defaults set by configuration or not"
     :json-schema/example true}
    :boolean]])

(def PrivateToolDeleteParams
  [:map {:closed true}
   [:force-delete
    {:optional            true
     :description         "Flag to force deletion of a Tool already in use by Apps."
     :json-schema/example false}
    :boolean]])

(def ToolTestData
  [:map {:closed true}
   [:params
    {:optional            true
     :description         "The list of command-line parameters"
     :json-schema/example ["-n" "100" "-o" "output.txt"]}
    [:vector :string]]

   [:input_files
    {:description         "The list of paths to test input files in iRODS"
     :json-schema/example ["/iplant/home/user/test_input1.txt" "/iplant/home/user/test_input2.txt"]}
    [:vector :string]]

   [:output_files
    {:description         "The list of paths to expected output files in iRODS"
     :json-schema/example ["/iplant/home/user/test_output1.txt" "/iplant/home/user/test_output2.txt"]}
    [:vector :string]]])

(def ToolImplementor
  [:map {:closed true}
   [:implementor
    {:description         "The name of the implementor"
     :json-schema/example "Jane Smith"}
    :string]

   [:implementor_email
    {:description         "The email address of the implementor"
     :json-schema/example "jane.smith@example.org"}
    :string]])

(def ToolImplementation
  (mu/merge
   ToolImplementor
   [:map {:closed true}
    [:test
     {:description ToolImplementationDocs}
     ToolTestData]]))

(def Tool
  [:map {:closed true}
   [:id ToolIdParam]

   [:name ToolNameParam]

   [:description
    {:optional true}
    ToolDescriptionParam]

   [:attribution
    {:optional true}
    AttributionParam]

   [:location
    {:optional            true
     :description         "The path of the directory containing the Tool"
     :json-schema/example "/usr/local/bin"}
    :string]

   [:version VersionParam]

   [:type
    {:description         "The Tool Type name"
     :json-schema/example "executable"}
    :string]

   [:restricted
    {:optional            true
     :description         "Determines whether a time limit is applied and whether network access is granted"
     :json-schema/example false}
    :boolean]

   [:time_limit_seconds
    {:optional            true
     :description         "The number of seconds that a tool is allowed to execute. A value of 0 means the time limit is disabled"
     :json-schema/example 3600}
    :int]

   [:interactive
    {:optional true}
    Interactive]])

(def ToolDetails
  (mu/merge
   Tool
   [:map {:closed true}
    [:is_public
     {:description         "Whether the Tool has been published and is viewable by all users"
      :json-schema/example true}
     :boolean]

    [:permission
     {:description         "The user's access level for the Tool"
      :json-schema/example "own"}
     :string]

    [:implementation
     {:description ToolImplementationDocs}
     ToolImplementation]

    [:container ToolContainer]]))

(def ToolListingImage
  (mu/merge
   (mu/dissoc Settings :id)
   [:map {:closed true}
    [:image (mu/dissoc Image :id)]]))

(def ToolImportRequest
  (mu/merge
   (mu/optional-keys Tool [:id])
   [:map {:closed true}
    [:implementation
     {:description ToolImplementationDocs}
     ToolImplementation]

    [:container NewToolContainer]]))

(def PrivateToolContainerImportRequest
  (-> NewToolContainer
      (mu/dissoc :container_devices)
      (mu/dissoc :container_volumes)
      (mu/dissoc :container_volumes_from)))

(def PrivateToolImportRequest
  (-> ToolImportRequest
      (mu/optional-keys [:type :implementation])
      (mu/assoc :container PrivateToolContainerImportRequest)
      (mu/update-properties assoc :description "The private Tool to import.")))

(def PrivateToolUpdateRequest
  (-> PrivateToolImportRequest
      (mu/optional-keys [:name :version :container])
      (mu/update-properties assoc :description "The private Tool to update.")))

(def ToolRequestStatus
  [:map {:closed true}
   [:status
    {:optional            true
     :description         (str "The status code of the Tool Request update. The status code is case-sensitive, and if it isn't "
                               "defined in the database already then it will be added to the list of known status codes")
     :json-schema/example "Pending"}
    :string]

   [:status_date
    {:description         "The timestamp of the Tool Request status update"
     :json-schema/example 1643723400000}
    :int]

   [:updated_by
    {:description         "The username of the user that updated the Tool Request status"
     :json-schema/example "admin"}
    :string]

   [:comments
    {:optional            true
     :description         "The administrator comments of the Tool Request status update"
     :json-schema/example "Reviewing tool compatibility with current infrastructure"}
    :string]])

(def ToolRequestDetails
  [:map {:closed true}
   [:id ToolRequestIdParam]

   [:submitted_by SubmittedByParam]

   [:phone
    {:optional            true
     :description         "The phone number of the user submitting the request"
     :json-schema/example "+1-555-123-4567"}
    :string]

   [:tool_id
    {:optional true}
    ToolRequestToolIdParam]

   [:name ToolNameParam]

   [:description ToolDescriptionParam]

   [:source_url
    {:optional            true
     :description         "A link that can be used to obtain the tool"
     :json-schema/example "https://github.com/samtools/samtools"}
    :string]

   [:source_upload_file
    {:optional            true
     :description         "The path to a file that has been uploaded into iRODS"
     :json-schema/example "/iplant/home/user/tools/mytool.tar.gz"}
    :string]

   [:documentation_url
    {:description         "A link to the tool documentation"
     :json-schema/example "https://samtools.github.io/"}
    :string]

   [:version VersionParam]

   [:attribution
    {:optional true}
    AttributionParam]

   [:multithreaded
    {:optional            true
     :description         (str "A flag indicating whether or not the tool is multithreaded. This can be `true` to indicate "
                               "that the user requesting the tool knows that it is multithreaded, `false` to indicate that the "
                               "user knows that the tool is not multithreaded, or omitted if the user does not know whether or "
                               "not the tool is multithreaded")
     :json-schema/example true}
    :boolean]

   [:test_data_path
    {:description         "The path to a test data file that has been uploaded to iRODS"
     :json-schema/example "/iplant/home/user/test_data/sample.fasta"}
    :string]

   [:cmd_line
    {:description         "Instructions for using the tool"
     :json-schema/example "samtools view -b -S input.sam > output.bam"}
    :string]

   [:additional_info
    {:optional            true
     :description         "Any additional information that may be helpful during tool installation or validation"
     :json-schema/example "Requires minimum 4GB RAM for large datasets"}
    :string]

   [:additional_data_file
    {:optional            true
     :description         "Any additional data file that may be helpful during tool installation or validation"
     :json-schema/example "/iplant/home/user/tools/reference_data.tar.gz"}
    :string]

   [:architecture
    {:optional            true
     :description         (str "One of the architecture names known to the DE. Currently, the valid values are "
                               "`32-bit Generic` for a 32-bit executable that will run in the DE, "
                               "`64-bit Generic` for a 64-bit executable that will run in the DE, "
                               "`Others` for tools run in a virtual machine or interpreter, and "
                               "`Don't know` if the user requesting the tool doesn't know what the architecture is")
     :json-schema/example "64-bit Generic"}
    [:enum "32-bit Generic" "64-bit Generic" "Others" "Don't know"]]

   [:history
    {:description "A history of status updates for this Tool Request"}
    [:vector ToolRequestStatus]]

   [:interactive
    {:optional true}
    Interactive]])

(def ToolRequest
  (-> ToolRequestDetails
      (mu/dissoc :id)
      (mu/dissoc :submitted_by)
      (mu/dissoc :history)
      (mu/update-properties assoc :description
                           (str "A tool installation request. One of `source_url` or `source_upload_file` "
                               "fields are required, but not both."))))

(def ToolRequestSummary
  [:map {:closed true}
   [:id ToolRequestIdParam]

   [:name ToolNameParam]

   [:version VersionParam]

   [:requested_by SubmittedByParam]

   [:tool_id
    {:optional true}
    ToolRequestToolIdParam]

   [:date_submitted
    {:description         "The timestamp of the Tool Request submission"
     :json-schema/example 1643723400000}
    :int]

   [:status
    {:description         "The current status of the Tool Request"
     :json-schema/example "Approved"}
    :string]

   [:date_updated
    {:description         "The timestamp of the last Tool Request status update"
     :json-schema/example 1643809800000}
    :int]

   [:updated_by
    {:description         "The username of the user that last updated the Tool Request status"
     :json-schema/example "admin"}
    :string]])

(def ToolRequestListing
  [:map {:closed true}
   [:tool_requests
    {:description "A listing of high level details about tool requests that have been submitted"}
    [:vector ToolRequestSummary]]])

(def ToolRequestListingParams
  (mu/merge
   PagingParams
   [:map {:closed true}
    [:status
     {:optional            true
      :description         (str "The name of a status code to include in the results. The name of the status code is case "
                                "sensitive. If the status code isn't already defined, it will be added to the database")
      :json-schema/example "Pending"}
     :string]]))

(def ToolRequestStatusCodeListingParams
  [:map {:closed true}
   [:filter
    {:optional            true
     :description         (str "If this parameter is set then only the status codes that contain the string passed in this "
                               "query parameter will be listed. This is a case-insensitive search")
     :json-schema/example "approv"}
    :string]])

(def ToolRequestStatusCode
  [:map {:closed true}
   [:id ToolRequestStatusCodeId]

   [:name
    {:description         "The Status Code"
     :json-schema/example "Approved"}
    :string]

   [:description
    {:description         "A brief description of the Status Code"
     :json-schema/example "The tool request has been approved for implementation"}
    :string]])

(def ToolRequestStatusCodeListing
  [:map {:closed true}
   [:status_codes
    {:description "A listing of known Status Codes"}
    [:vector ToolRequestStatusCode]]])

(def ToolListingToolRequestSummary
  [:map {:closed true}
   [:id ToolRequestIdParam]

   [:status
    {:description         "The current status of the Tool Request"
     :json-schema/example "Approved"}
    :string]])

(def ToolListingItem
  (mu/merge
   ToolDetails
   [:map {:closed true}
    [:implementation
     {:description ToolImplementationDocs}
     ToolImplementor]

    [:container ToolListingImage]

    [:tool_request
     {:optional true}
     ToolListingToolRequestSummary]]))

(def ToolListing
  [:map {:closed true}
   [:tools
    {:description "Listing of App Tools"}
    [:vector ToolListingItem]]

   [:total
    {:description         "The total number of App Tools in the listing"
     :json-schema/example 42}
    :int]])

(def ErrorPrivateToolRequestBadParam
  (mu/assoc ErrorResponse :error_code
            [:enum
             {:description         "Exists or Bad Field error code"
              :json-schema/example ce/ERR_EXISTS}
             ce/ERR_EXISTS
             ce/ERR_BAD_OR_MISSING_FIELD]))
