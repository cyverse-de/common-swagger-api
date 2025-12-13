(ns common-swagger-api.malli.permanent-id-requests
  (:require
   [common-swagger-api.malli :refer [PagingParams SortFieldDocs]]
   [malli.util :as mu]))

(def PermanentIDRequestAdminListSummary "List Permanent ID Requests")
(def PermanentIDRequestAdminListDescription
  "Allows administrators to list Permanent ID Requests from all users.")

(def PermanentIDRequestAdminDetailsSummary "Get Permanent ID Request Details")
(def PermanentIDRequestAdminDetailsDescription
  "Allows administrators to retrieve details for a Permanent ID Request from any user.")

(def PermanentIDRequestAdminStatusUpdateSummary "Update the Status of a Permanent ID Request")
(def PermanentIDRequestAdminStatusUpdateDescription
  "Allows administrators to update the status of a Permanent ID Request from any user.")

(def PermanentIDRequestSummary "Create a Permanent ID Request")
(def PermanentIDRequestDescription
  "Creates a Permanent ID Request for the requesting user.")

(def PermanentIDRequestDetailsSummary "Get Permanent ID Request Details")
(def PermanentIDRequestDetailsDescription
  "Allows a user to retrieve details for one of their Permanent ID Request submissions.")

(def PermanentIDRequestListSummary "List Permanent ID Requests")
(def PermanentIDRequestListDescription
  "Lists all Permanent ID Requests submitted by the requesting user.")

(def PermanentIDRequestStatusCodeListSummary "List Permanent ID Request Status Codes")
(def PermanentIDRequestStatusCodeListDescription
  "Lists all Permanent ID Request Status Codes that have been assigned to a request status update.
   This allows a status to easily be reused by admins in future status updates.")

(def PermanentIDRequestTypesSummary "List Permanent ID Request Types")
(def PermanentIDRequestTypesDescription
  "Lists the allowed Permanent ID Request Types the user can select when submitting a new request.")

(def PermanentIDRequestIdParam
  [:uuid {:description         "The Permanent ID Request's UUID"
          :json-schema/example #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3"}])

(def PermanentID
  [:map {:closed true}
   [:permanent_id
    {:optional            true
     :description         "The identifier of a completed Permanent ID Request"
     :json-schema/example "https://doi.org/10.1093/nar/gkv416"}
    :string]])

(def PermanentIDRequestOrigPath
  [:map {:closed true}
   [:original_path
    {:optional            true
     :description         (str "The original path of the target data set at the time the Permanent ID Request was "
                               "initially created")
     :json-schema/example "/zone/home/username/original_folder"}
    :string]])

(def PermanentIDRequest
  [:map {:closed true}
   [:type
    {:description         "The type of persistent ID requested"
     :json-schema/example "DOI"}
    :string]])

(def PermanentIDRequestBase
  (-> (mu/merge PermanentIDRequest PermanentID)
      (mu/merge PermanentIDRequestOrigPath)
      (mu/merge
       [:map {:closed true}
        [:id PermanentIDRequestIdParam]

        [:requested_by
         {:description         "The username of the user that submitted the Permanent ID Request"
          :json-schema/example "janedoe"}
         :string]])))

(def PermanentIDRequestStatusComments
  [:map {:closed true}
   [:comments
    {:optional            true
     :description         "The curator comments of the Permanent ID Request status update"
     :json-schema/example "Please fill out the DataCite metadata template for the data set."}
    :string]])

(def PermanentIDRequestStatusUpdate
  (reduce
   mu/merge
   [PermanentIDRequestStatusComments
    [:map {:closed true}
     [:permanent_id
      {:optional            true
       :description         (str "The identifier of a completed Permanent ID Request. If the `permanent_id` is "
                                 "provided in the request, then the Permanent ID Request must not already have "
                                 "a `permanent_id` set, otherwise an error is returned and the Status is not "
                                 "updated.")
       :json-schema/example "https://doi.org/10.1093/nar/gkv416"}
      :string]

     [:status
      {:optional            true
       :description         (str "The status code of the Permanent ID Request update. The status code is "
                                 "case-sensitive, and if it isn't defined in the database already then it "
                                 "will be added to the list of known status codes")
       :json-schema/example "Submitted"}
      :string]]]))

(def PermanentIDRequestStatus
  (mu/closed-schema
   (reduce
    mu/merge
    [PermanentID
     PermanentIDRequestStatusComments
     [:map
      [:status
       {:optional            true
        :description         "The status code of the Permanent ID Request update"
        :json-schema/example "Completion"}
       :string]

      [:status_date
       {:description         "The timestamp of the Permanent ID Request status update"
        :json-schema/example 1762296097000}
       :int]

      [:updated_by
       {:description         "The username that updated the Permanent ID Request status"
        :json-schema/example "janedoe"}
       :string]]])))

(def PermanentIDRequestDetails
  (mu/closed-schema
   (mu/merge
    PermanentIDRequestBase
    [:map
     [:history
      {:description "A list of Permanent ID Request status updates"}
      [:vector PermanentIDRequestStatus]]])))

(def PermanentIDRequestListing
  (mu/closed-schema
   (mu/merge
    PermanentIDRequestBase
    [:map
     [:date_submitted
      {:description         "The timestamp of the Permanent ID Request submission"
       :json-schema/example 1763063226000}
      :int]

     [:status
      {:description         "The current status of the Permanent ID Request"
       :json-schema/example "Evaluation"}
      :string]

     [:date_updated
      {:description         "The timestamp of the last Permanent ID Request status update"
       :json-schema/example 1763067980000}
      :int]

     [:updated_by
      {:description         "The username of the user that last updated the Permanent ID Request status"
       :json-schema/example "example_user"}
      :string]])))

(def PermanentIDRequestList
  (mu/closed-schema
   [:map
    [:requests
     {:description "A list of Permanent ID Requests"}
     [:vector PermanentIDRequestListing]]

    [:total
     {:description         "The total number of permanent id requests in the listing"
      :json-schema/example 27}
     :int]]))

(def ValidPermanentIDRequestListPagingParams
  [:enum :type :target_type :requested_by :date_submitted :status :date_updated :updated_by])

(def PermanentIDRequestListPagingParams
  (mu/closed-schema
   (mu/merge
    PagingParams
    [:map
     [:sort-field
      {:optional    true
       :description SortFieldDocs}
      ValidPermanentIDRequestListPagingParams]

     [:statuses
      {:optional            true
       :description         "Status Codes with which to filter results"
       :json-schema/example "Rejected"}
      [:vector :string]]])))

(def PermanentIDRequestStatusCode
  (mu/closed-schema
   [:map
    [:id
     {:description         "The Status Code's UUID"
      :json-schema/example #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"}
     :uuid]

    [:name
     {:description         "The Status Code"
      :json-schema/example "Pending"}
     :string]

    [:description
     {:description         "A brief description of the Status Code"
      :json-schema/example "The curators are waiting for a response from the requesting user."}
     :string]]))

(def PermanentIDRequestStatusCodeList
  (mu/closed-schema
    [:map
     [:status_codes
      {:description "A list of Permanent ID Request Status Codes"}
      [:vector PermanentIDRequestStatusCode]]]))

(def PermanentIDRequestType
  (mu/closed-schema
   [:map
    [:id
     {:description         "The Request Type's UUID"
      :json-schema/example #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"}
     :uuid]

    [:type
     {:description         "The Request Type"
      :json-schema/example "DOI"}
     :string]

    [:description
     {:description         "A brief description of the Request Type"
      :json-schema/example "Data Object Identifier"}
     :string]]))

(def PermanentIDRequestTypeList
  (mu/closed-schema
    [:map
     [:request_types
      {:description "A list of Permanent ID Request Types"}
      [:vector PermanentIDRequestType]]]))
