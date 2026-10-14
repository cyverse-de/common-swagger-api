(ns common-swagger-api.malli.data.tickets
  (:require
   [clojure-commons.error-codes :as ce]
   [common-swagger-api.malli :refer [add-enum-values CommonResponses doc-only ErrorResponseUnchecked NonBlankString]]
   [common-swagger-api.malli.data :as data-schema]
   [malli.util :as mu]))

(def AddTicketSummary "Create Tickets")
(def AddTicketDocs
  "This endpoint allows creating tickets for a set of provided paths.")

(def DeleteTicketSummary "Delete Tickets")
(def DeleteTicketDocs
  "This endpoint deletes the provided set of tickets.")

(def ListTicketSummary "List Tickets")
(def ListTicketDocs
  "This endpoint lists tickets for a set of provided paths.")

(def ModeParamValues [:read :write])
(def ModeParamDocs
  "Whether the created tickets allow `write` or `read` only access. Default is `read` only.")

(def AddTicketQueryParams
  [:map {:closed true}
   [:mode
    {:optional            true
     :description         ModeParamDocs
     :json-schema/example :read}
    (into [:enum] ModeParamValues)]

   [:uses-limit
    {:optional            true
     :description         "Sets the `uses-limit` of the created tickets, when provided"
     :json-schema/example 5}
    :int]

   [:file-write-limit
    {:optional            true
     :description         "Sets the `file-write-limit` of the created tickets, when provided (10 by default)"
     :json-schema/example 10}
    :int]

   [:public
    {:description         "Whether the created tickets should be made public"
     :json-schema/example false}
    :boolean]

   [:for-job
    {:optional            true
     :description         "Indicates whether the tickets are being created for an analysis"
     :json-schema/example false}
    :boolean]])

(def DeleteTicketQueryParams
  [:map {:closed true}
   [:for-job
    {:optional            true
     :description         "Indicates whether the tickets being deleted were created for an analysis"
     :json-schema/example false}
    :boolean]])

(def TicketDefinition
  [:map {:closed true}
   [:path
    {:description         "The iRODS path for the ticket"
     :json-schema/example "/iplant/home/janedoe/file.txt"}
    NonBlankString]

   [:ticket-id
    {:description         "The ID of the ticket. Usually, but not always, a UUID."
     :json-schema/example "4ea5ba1f-1a5a-4f2b-a1bc-0f4a0e0c6ac5"}
    NonBlankString]

   [:download-url
    {:description         "The URL for downloading the file associated with this ticket."
     :json-schema/example "https://example.org/dl/d/4ea5ba1f-1a5a-4f2b-a1bc-0f4a0e0c6ac5/file.txt"}
    NonBlankString]

   [:download-page-url
    {:description         "The URL for managing this ticket, getting links, seeing metadata, etc."
     :json-schema/example "https://example.org/d/4ea5ba1f-1a5a-4f2b-a1bc-0f4a0e0c6ac5"}
    NonBlankString]])

(def AddTicketResponse
  [:map {:closed true}
   [:user
    {:description         "The user performing the request."
     :json-schema/example "ipctest"}
    NonBlankString]

   [:tickets
    {:description "The tickets created"}
    [:vector TicketDefinition]]])

(def ListTicketsResponseMap
  [:map-of
   [:keyword
    {:description         "The iRODS data item's path"
     :json-schema/example (keyword "/example/home/janedoe/file.txt")}]

   [:vector {:description "The tickets for this path"} TicketDefinition]])

(def ListTicketsResponse
  [:map {:closed true}
   [:tickets
    {:description "Map of tickets"}
    ListTicketsResponseMap]])

;; used only for documentation
(def ListTicketsPathsMap
  [:map {:closed true}
   [(keyword "/path/from/request/to/a/file/or/folder")
    {:description "The tickets for this path"}
    [:vector TicketDefinition]]])

;; used only for documentation
(def ListTicketsDocumentation
  [:map {:closed true}
   [:tickets
    {:description "the tickets"}
    [:vector ListTicketsPathsMap]]])

(def Tickets
  [:map {:closed true}
   [:tickets
    {:description         "A list of ticket IDs"
     :json-schema/example ["4ea5ba1f-1a5a-4f2b-a1bc-0f4a0e0c6ac5"]}
    [:vector NonBlankString]]])

(def DeleteTicketsResponse
  (mu/merge
   Tickets
   [:map
    [:user
     {:description         "The user performing the request."
      :json-schema/example "ipctest"}
     NonBlankString]]))

(def TicketCommonErrorCodes
  (conj data-schema/CommonErrorCodeResponses
        ce/ERR_TOO_MANY_RESULTS
        ce/ERR_DOES_NOT_EXIST
        ce/ERR_NOT_A_USER))

(def AddTicketErrorResponses
  (mu/update
   ErrorResponseUnchecked
   :error_code
   add-enum-values
   ce/ERR_TOO_MANY_RESULTS
   ce/ERR_DOES_NOT_EXIST
   ce/ERR_NOT_A_USER
   ce/ERR_NOT_WRITEABLE))

(def AddTicketResponses
  (merge CommonResponses
         {200 {:body        AddTicketResponse
               :description "Create Tickets Response."}
          500 {:body        AddTicketErrorResponses
               :description data-schema/CommonErrorCodeDocs}}))

(def ListTicketErrorResponses
  (mu/update
   ErrorResponseUnchecked
   :error_code
   add-enum-values
   ce/ERR_TOO_MANY_RESULTS
   ce/ERR_DOES_NOT_EXIST
   ce/ERR_NOT_A_USER
   ce/ERR_NOT_READABLE))

(def ListTicketResponses
  (merge CommonResponses
         {200 {:body        (doc-only ListTicketsResponse ListTicketsDocumentation)
               :description "List Tickets Response."}
          500 {:body        ListTicketErrorResponses
               :description data-schema/CommonErrorCodeDocs}}))

(def DeleteTicketErrorResponses
  (mu/update
   ErrorResponseUnchecked
   :error_code
   add-enum-values
   ce/ERR_NOT_WRITEABLE
   ce/ERR_TICKET_DOES_NOT_EXIST
   ce/ERR_NOT_A_USER))

(def DeleteTicketResponses
  (merge CommonResponses
         {200 {:body        DeleteTicketsResponse
               :description "Delete Tickets Response."}
          500 {:body        DeleteTicketErrorResponses
               :description data-schema/CommonErrorCodeDocs}}))
