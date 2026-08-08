(ns common-swagger-api.malli.analyses.listing
  (:require
   [clojure.java.io :as io]
   [common-swagger-api.malli :refer [NonBlankString PagingParams]]
   [common-swagger-api.malli.analyses :refer [AnalysisStats]]
   [common-swagger-api.malli.apps :refer [SystemId]]
   [common-swagger-api.malli.common :refer [IncludeDeletedParams
                                            IncludeHiddenParams]]
   [malli.util :as mu]))

(def AnalysesDeleteSummary "Delete Multiple Analyses")
(def AnalysesDeleteDocs
  "This service allows the caller to mark one or more analyses as deleted in the apps database.")

(def AnalysesListingSummary "List Analyses")
(def AnalysesListingDocs
  "This service allows users to list analyses that they've previously submitted for execution.")

(def AnalysisDeleteSummary "Delete an Analysis")
(def AnalysisDeleteDocs
  "This service marks an analysis as deleted in the DE database.")

(def AnalysisHistorySummary "Get the Status Update History of an Analysis")
(def AnalysisHistoryDocs
  "This endpoint returns a status update history for each step in an analysis.")

(def AnalysisStepsSummary "Display the steps of an analysis.")
(def AnalysisStepsDocs
  "This service returns a list of steps in an analysis.")

(def AnalysisSubmitSummary "Submit an Analysis")
(def AnalysisSubmitDocs
  (str "This service allows users to submit analyses for execution. The `config` element in the analysis submission "
       "is a map from parameter IDs as they appear in the response from the `/apps/:app-id` endpoint to the desired "
       "values for those parameters."))

(def AnalysisUpdateSummary "Update an Analysis")
(def AnalysisUpdateDocs
  "This service allows an analysis name or description to be updated.")

(def AnalysisListingParams
  (reduce
    mu/merge
    [IncludeHiddenParams
     IncludeDeletedParams
     PagingParams
     [:map
      [:filter
       {:optional true
        :description (slurp (io/resource "docs/analyses/listing/filter-param.md"))
        :json-schema/example "[{\"field\":\"id\",\"value\":\"C09F5907-B2A2-4429-A11E-5B96F421C3C1\"}]"}
       :string]]]))

(def ResultsTotalParam
  [:int
   {:description         "The total number of results that would be returned without limits and offsets applied."
    :json-schema/example 117}])

(def Timestamp
  [:string
   {:description "A timestamp in milliseconds since the epoch."
    :json-schema/example "1763405834987"}])

(def ExternalId
  (mu/update-properties
   NonBlankString
   merge
   {:description         "The analysis identifier from the job execution system."
    :json-schema/example "7f13edd3-d9c8-40c8-b9a2-360cc9977fe9-007"}))

(def BatchStatus
  (mu/closed-schema
   [:map
    [:total
     {:description         "The total number of jobs in the batch."
      :json-schema/example 42}
     [:int {:min 0}]]

    [:completed
     {:description         "The number of completed jobs in the batch."
      :json-schema/example 39}
     [:int {:min 0}]]

    [:running
     {:description         "The number of running jobs in the batch."
      :json-schema/example 2}
     [:int {:min 0}]]

    [:submitted
     {:description         "The number of submitted jobs in the batch."
      :json-schema/example 1}
     [:int {:min 0}]]]))

(def BaseAnalysis
  (mu/closed-schema
   [:map
    [:app_description
     {:optional            true
      :description         "A description of the app used to perform the analysis."
      :json-schema/example "BLAST Nucleotide Alignment"}
     :string]

    [:app_id
     {:description         "The ID of the app used to perform the analysis."
      :json-schema/example "90b343d0-2db7-4f59-be6c-767806736529"}
     :string]

    [:app_version_id
     {:optional            true
      :description         "The version ID of the app used to perform the analysis."
      :json-schema/example #uuid "46f2634e-6297-4ff5-9e63-dfe1c4a2ee09"}
     :uuid]

    [:system_id SystemId]

    [:app_name
     {:optional            true
      :description         "The name of the app used to perform the analysis."
      :json-schema/example "BLAST"}
     :string]

    [:batch
     {:optional            true
      :description         "Indicates whether the analysis is a batch analysis."
      :json-schema/example false}
     :boolean]

    [:description
     {:optional            true
      :description         "The analysis description."
      :json-schema/example "BLAST nucleotide alignment of maize B73 genome sequences."}
     :string]

    ;; The Timestamp type has an example, so one isn't specified here for now. We can add an example later if needed.
    [:enddate
     {:optional    true
      :description "The time the analysis ended."}
     Timestamp]

    [:id
     {:description         "The analysis ID."
      :json-schema/example #uuid "eb97403f-de50-4f65-939e-b276f7faec16"}
     :uuid]

    [:name
     {:optional            true
      :description         "The analysis name."
      :json-schema/example "BLAST Zea Mays B73"}
     :string]

    [:notify
     {:description         "Indicates whether the user wants status updates via email."
      :json-schema/example true}
     :boolean]

    [:resultfolderid
     {:optional            true
      :description         "The path to the folder containing the analysis results."
      :json-schema/example "/example/home/janedoe/analyses/BLAST-Zea-Mays-B73"}
     :string]

    ;; The Timestamp type has an example, so one isn't specified here for now. We can add an example later if needed.
    [:startdate
     {:optional    true
      :description "The time the analysis started."}
     Timestamp]

    [:status
     {:description         "The status of the analysis."
      :json-schema/example "Running"}
     :string]

    [:username
     {:description         "The name of the user who submitted the analysis."
      :json-schema/example "janedoe"}
     :string]

    [:wiki_url
     {:optional            true
      :description         "The URL to app documentation in Confluence."
      :json-schema/example "https://confluence.cyverse.org/discovery-environment/BLAST"}
     :string]

    [:parent_id
     {:optional            true
      :description         "The identifier of the parent analysis."
      :json-schema/example #uuid "59dfbf9b-b5ab-4197-a905-e8793e360444"}
     :uuid]

    [:batch_status
     {:optional    true
      :description "A summary of the status of the batch."}
     BatchStatus]

    [:interactive_urls
     {:optional            true
      :description         "The list of externally accessible interactive analysis URLs."
      :json-schema/example ["https://vice.example.org/a123456"]}
     [:vector :string]]]))

(def Analysis
  (mu/closed-schema
   (mu/merge
    BaseAnalysis
    [:map
     [:app_disabled
      {:description         "Indicates whether the app is currently disabled. DEPRECATED - always returns `false`."
       :json-schema/example false}
      :boolean]

     [:can_share
      {:description         "Indicates whether or not the analysis can be shared."
       :json-schema/example true}
      :boolean]])))

(def AnalysisList
  (mu/closed-schema
    (mu/merge
      AnalysisStats
      [:map
       [:analyses
        {:description "The list of analyses."}
        [:vector Analysis]]

       ;; The Timestamp type already has an example value, so one isn't provided here.
       [:timestamp
        {:description "The time the analysis list was retrieved."}
        Timestamp]

       ;; The ResultsTotalParam type already has an example value, so one isn't provided here.
       [:total ResultsTotalParam]])))

(def AnalysisUpdate
  (mu/select-keys Analysis [:description :name]))

(def AnalysisUpdateResponse
  (mu/select-keys Analysis [:id :description :name]))

(def AppStepNumber
  [:int
   {:description         (str "The sequential step number from the app, which might be different "
                              "from the analysis step number if app steps have been combined.")
    :json-schema/example 0}])

(def AnalysisStep
  (mu/closed-schema
   [:map
    [:step_number
     {:description         "The sequential step number in the analysis."
      :json-schema/example 1}
     :int]

    [:external_id
     {:optional            true
      :description         "The step ID from the execution system."
      :json-schema/example "d81e33c4-b915-45be-8568-5c731f37cd5d"}
     :string]

    ;; The timestamp type already has an example, so one isn't provided here.
    [:startdate
     {:optional    true
      :description "The time the step started."}
     Timestamp]

    ;; The timestamp type already has an example, so one isn't provided here.
    [:enddate
     {:optional    true
      :description "The time the step ended."}
     Timestamp]

    [:status
     {:optional            true
      :description         "The status of the step."
      :json-schema/example "Submitted"}
     :string]

    ;; The AppStepNumber type already has an example, so one isn't provided here.
    [:app_step_number
     {:optional true}
     AppStepNumber]

    [:step_type
     {:optional            true
      :description         "The analysis type associated with the step."
      :json-schema/example "Interactive"}
     :string]]))

(def AnalysisStepList
  (mu/closed-schema
   [:map
    [:analysis_id
     {:description         "The analysis ID."
      :json-schema/example #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"}
     :uuid]

    [:steps
     {:description "The list of analysis steps."}
     [:vector AnalysisStep]]

    ;; The Timestamp field has an example value, so one isn't provided here.
    [:timestamp
     {:description "The time the list of analysis steps was retrieved."}
     Timestamp]

    ;; The ResultsTotalParam field has an example value, so one isn't provided here.
    [:total ResultsTotalParam]]))

(def AnalysisStatusUpdate
  (mu/closed-schema
   [:map
    [:status
     {:description         "The job status associated with the update."
      :json-schema/example "Running"}
     NonBlankString]

    [:message
     {:description         "A brief description of the job status update."
      :json-schema/example "Downloading input files."}
     :string]

    ;; This used a string type originally, but it was migrated to the Timestamp type for consistency.
    [:timestamp
     {:description "The date and time when the job status update was created."}
     Timestamp]]))

(def AnalysisStepHistory
  (mu/closed-schema
   (mu/merge
    AnalysisStep
    [:map
     [:updates
      {:description "The list of updates received for the analysis step."}
      [:vector AnalysisStatusUpdate]]])))

(def AnalysisHistory
  (mu/closed-schema
    (mu/merge
      AnalysisStepList
      [:map
       [:steps
        {:description "The history of each step in the analysis."}
        [:vector AnalysisStepHistory]]])))
