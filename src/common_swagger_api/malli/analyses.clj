(ns common-swagger-api.malli.analyses
  (:require
   [clojure.java.io :as io]
   [common-swagger-api.malli.apps :refer [AppStepResourceRequirements SystemId]]
   [common-swagger-api.malli.common :refer [IncludeDeletedParams
                                            IncludeHiddenParams]]
   [malli.util :as mu]))

(def AnalysisParametersSummary "Display the parameters used in an analysis.")
(def AnalysisParametersDocs
  "This service returns a list of parameter values used in a previously executed analysis.")

(def AnalysisRelaunchSummary "Obtain information to relaunch analysis.")
(def AnalysisRelaunchDocs
  (str "This service allows the Discovery Environment user interface to obtain an app description that can be used "
       "to relaunch a previously submitted job, possibly with modified parameter values."))

(def AnalysesRelauncherSummary "Auto Relaunch Analyses")

(def AnalysisStopSummary "Stop a running analysis.")
(def AnalysisStopDocs "This service allows DE users to stop running analyses.")

(def AnalysisIDPathParam
  [:uuid
   {:description         "The Analysis UUID"
    :json-schema/example #uuid "2b912405-5ae8-4db9-9023-db745b5a7c83"}])

(def ParameterValue
  (mu/closed-schema
   [:map
    [:value
     {:description         "The value of the parameter."
      :json-schema/example "param_value"}
     :any]]))

(def AnalysisParameter
  (mu/closed-schema
   [:map
    [:full_param_id
     {:description         "The fully qualified parameter ID."
      :json-schema/example "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"}
     :string]

    [:param_id
     {:description         "The unqualified parameter ID."
      :json-schema/example "17826e1e-9132-47e3-a2e0-0399f212bb92"}
     :string]

    [:param_name
     {:optional            true
      :description         "The name of the parameter."
      :json-schema/example "Input File"}
     :string]

    [:param_value
     {:optional    true
      :description "The value of the parameter."}
     ParameterValue]

    [:param_type
     {:description         "The type of the parameter."
      :json-schema/example "FileInput"}
     :string]

    [:info_type
     {:optional            true
      :description         "The type of information associated with an input or output parameter."
      :json-schema/example "SequenceAlignment"}
     :string]

    [:data_format
     {:optional            true
      :description         "The data format associated with an input or output parameter."
      :json-schema/example "FASTA"}
     :string]

    [:is_default_value
     {:optional            true
      :description         "Indicates whether the default parameter value was used."
      :json-schema/example false}
     :boolean]

    [:is_visible
     {:optional            true
      :description         "Indicates whether the parameter is visible in the app UI."
      :json-schema/example true}
     :boolean]]))

(def AnalysisParameters
  (mu/closed-schema
   [:map
    [:app_id
     {:description         "The ID of the app used to perform the analysis."
      :json-schema/example "0f32febd-13fb-49b3-93f9-f6e39d46d817"}
     :string]

    [:app_version_id
     {:optional            true
      :description         "The version ID of the app used to perform the analysis."
      :json-schema/example "763a0d0c-d2bd-46fc-b3f6-747ba9c82cfa"}
     :string]

    [:system_id SystemId]

    [:parameters
     {:description "The list of parameters."}
     [:vector AnalysisParameter]]]))

(def AnalysisRelauncherRequest
  (mu/closed-schema
   [:map
    [:analyses
     {:description         "The identifiers of the analyses to be relaunched."
      :json-schema/example [#uuid "cca90887-fb54-4504-b882-e8f631f9613e" #uuid "4658e4f9-0297-4493-ab56-ba06960a9bd0"]}
     [:vector :uuid]]]))

(def AnalysisShredderRequest
  (mu/closed-schema
   [:map
    [:analyses
     {:description "The identifiers of the analyses to be deleted."
      :json-schema/example [#uuid "2506f0bf-9664-4fa2-8465-41e3a0a487de" #uuid "88e81af2-4f22-4f73-aaec-5a4a81c62256"]}
     [:vector :uuid]]]))

(def StopAnalysisRequest
  (mu/closed-schema
   [:map
    [:job_status
     {:optional            true
      :description         "The job status to set. Defaults to `Canceled`"
      :json-schema/example "Canceled"}
     [:enum "Canceled" "Completed" "Failed"]]]))

(def StopAnalysisResponse
  (mu/closed-schema
   [:map
    [:id
     {:description         "the ID of the stopped analysis."
      :json-schema/example #uuid "b2a1790c-fe7d-4e4c-8a2e-4734b69bbba6"}
     :uuid]]))

(def FileMetadata
  (mu/closed-schema
   [:map
    [:attr
     {:description         "The attribute name."
      :json-schema/example "sample_weight"}
     :string]

    [:value
     {:description         "The attribute value."
      :json-schema/example "2"}
     :string]

    [:unit
     {:description         "The attribute unit."
      :json-schema/example "kg"}
     :string]]))

(def AnalysisSubmissionConfig
  (mu/closed-schema
   [:map-of
    [:keyword
     {:description         "The step-ID_param-ID"
      :json-schema/example "1104ffbe-3b81-4c64-868c-20f3dc86fd1a_ced83179-6aa8-424e-9cae-62fc8b21e1c0"}]

    [:any
     {:description         "The param-value"
      :json-schema/example "foo.txt"}]]))

(def AnalysisStepResourceRequirements
  (mu/select-keys AppStepResourceRequirements
                  [:min_memory_limit
                   :min_cpu_cores
                   :max_cpu_cores
                   :min_gpus
                   :max_gpus
                   :min_disk_space
                   :step_number]))

(def AnalysisSubmission
  (mu/closed-schema
   [:map
    [:system_id SystemId]

    [:app_id
     {:description         "The ID of the app used to perform the analysis."
      :json-schema/example "007a8434-1b84-42e8-b647-4073a62b4b3b"}
     :string]

    [:app_version_id
     {:optional            true
      :description         (str "The ID of the app version used to perform the analysis. If not provided, then it is "
                                "assumed the submission is for the latest version of the app")
      :json-schema/example "02ff8f75-a4fc-4d8c-9d76-ef91f764cec4"}
     :string]

    [:job_id
     {:optional            true
      :description         "The UUID of the job being submitted."
      :json-schema/example #uuid "1738fd3b-702d-4022-9c89-0337ef726cfb"}
     :uuid]

    [:callback
     {:optional            true
      :description         "The callback URL to use for job status updates."
      :json-schema/example "https://example.com/foo"}
     :string]

    [:config
     {:description "A map from (str step-id \"_\" param-id) to param-value."}
     AnalysisSubmissionConfig]

    [:requirements
     {:optional    true
      :description "The list of optional resource requirements requested for any step"}
     [:vector AnalysisStepResourceRequirements]]

    [:create_output_subdir
     {:optional            true
      :description         "Indicates whether a subdirectory should be created beneath the specified output directory."
      :json-schema/example true}
     :boolean]

    [:debug
     {:description         "A flag indicating whether or not job debugging should be enabled."
      :json-schema/example false}
     :boolean]

    [:description
     {:optional            true
      :description         "An optional description of the analysis."
      :json-schema/example "Sequence alignment for Zea Mays."}
     :string]

    [:name
     {:description         "The name assigned to the analysis by the user."
      :json-schema/example "sequence-alignment-zea-mays"}
     :string]

    [:notify
     {:description         "Indicates whether the user wants to receive job status update notifications."
      :json-schema/example true}
     :boolean]

    [:notify_periodic
     {:optional            true
      :description         (str "Indicates whether the user wants to receive periodic email notifications that the "
                                "job is still running, for interactive jobs.")
      :json-schema/example false}
     :boolean]

    [:periodic_period
     {:optional            true
      :description         (str "The number of seconds between periodic notifications, if the user has opted to "
                                "receive them.")
      :json-schema/example 3600}
     :int]

    [:output_dir
     {:description         "The path to the analysis output directory in the data store."
      :json-schema/example "/zone/home/username/folder-name"}
     :string]

    [:starting_step
     {:optional            true
      :description         "The ordinal number of the step to start the job with."
      :json-schema/example 3}
     :int]

    [:uuid
     {:optional            true
      :description         "The UUID of the analysis. A random UUID will be assigned if one isn't provided."
      :json-schema/example #uuid "df97797c-0f8d-41ae-898d-68dcd79abfc8"}
     :uuid]

    [:skip-parent-meta
     {:optional            true
      :description         "True if metadata should not associate metadata with the parent directory."
      :json-schema/example true}
     :boolean]

    [:file-metadata
     {:optional    true
      :description "Custom file attributes to associate with result files."}
     [:vector FileMetadata]]

    [:archive_logs
     {:optional            true
      :description         "True if the job logs should be uploaded to the data store."
      :json-schema/example false}
     :boolean]

    [:mount_data_store
     {:optional            true
      :description         "True if iRODS CSI Driver mounts should be created in the container."
      :json-schema/example true}
     :boolean]]))

(def AnalysisResponse
  (mu/closed-schema
   [:map
    [:id
     {:description         "The ID of the submitted analysis."
      :json-schema/example #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"}
     :uuid]

    [:name
     {:description         "The name of the submitted analysis."
      :json-schema/example "An arabidopsis thaliana sequence alignment"}
     :string]

    [:status
     {:description         "The current status of the analysis."
      :json-schema/example "Submitted"}
     :string]

    [:start-date
     {:description         "The analysis start date as milliseconds since the epoch."
      :json-schema/example "1763083385000"}
     :string]

    [:missing-paths
     {:optional            true
      :description         "Any paths parsed from an HT Analysis Path List that no longer exist."
      :json-schema/example ["/foo/bar/baz" "/foo/bar/quux"]}
     [:vector :string]]]))

(def AnalysisPod
  (mu/closed-schema
   [:map
    [:name
     {:description         "The name of a pod in Kubernetes associated with an analysis."
      :json-schema/example "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05-79c44695b5-v8s4w"}
     :string]

    [:external_id
     {:description         "The external ID associated with the pod."
      :json-schema/example #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"}
     :uuid]]))

(def AnalysisPodListSummary
  "List the Kubernetes pods associated with the analysis.")

(def AnalysisPodListDescription
  "This endpoint returns a listing of pod objects associated with the analysis. Usually will return a single pod.")

(def AnalysisPodList
  (mu/closed-schema
    [:map
     [:pods
      {:description "A list of pods in Kubernetes associated with an analysis."}
      [:vector AnalysisPod]]]))

(def AnalysisPodLogSummary
  "The logs from a pod associated with the analysis")

(def AnalysisPodLogDescription
  "This endpoint returns the logs from the provided pod associated with the provided analysis.")

(def AnalysisPodLogParameters
  (mu/closed-schema
   [:map
    [:previous
     {:optional            true
      :description         "True if the logs of a previously terminated container should be returned"
      :json-schema/example true}
     :boolean]

    [:since
     {:optional            true
      :description         "Number of seconds in the past to start showing logs"
      :json-schema/example 3600}
     :int]

    [:since-time
     {:optional            true
      :description         "The time at which to start showing log lines. Expressed as seconds since the epoch."
      :json-schema/example "1763156075"}
     :string]

    [:tail-lines
     {:optional            true
      :description         "Number of lines from the end of the log to show"
      :json-schema/example 100}
     :int]

    [:timestamps
     {:optional            true
      :description         "True if timestamps should be prepended to the log lines"
      :json-schema/example true}
     :boolean]

    [:container
     {:optional            true
      :description         "Name of the container to display logs from. Defaults to 'analysis'"
      :json-schema/example "analysis"}
     :string]]))

(def AnalysisPodLogEntry
  (mu/closed-schema
   [:map
    [:since_time
     {:description         "Contains the seconds since the epoch for the time when the log entry was retrieved"
      :json-schema/example "1763156649"}
     :string]

    [:lines
     {:description         "The lines that make up the log entry"
      :json-schema/example ["[I 2025-11-14 18:24:35.960 ServerApp] Jupyter Server 2.15.0 is running at:"
                            "[I 2025-11-14 18:24:35.960 ServerApp] http://a9e6f09ad:8888/lab"
                            "[I 2025-11-14 18:24:35.960 ServerApp]     http://127.0.0.1:8888/lab"]}
     [:vector :string]]]))

(def AnalysisTimeLimit
  (mu/closed-schema
   [:map
    [:time_limit
     {:description         (str "Contains the seconds since the epoch for the analysis's time limit or the string "
                                "'null' if the time limit isn't set.")
      :json-schema/example "1763157634"}
     :string]]))

(def ConcurrentJobLimitUsername
  [:string
   {:description         "The username associated with the limit"
    :json-schema/example "janedoe"}])

(def ConcurrentJobLimitListingSummary
  "List Concurrent Job Limits")

(def ConcurrentJobLimitListingDescription
  (str "Lists the concurrent job limits for all users who have one defined. The record describing the default "
       "limit contains no username. Users without explicit limits defined will use the default limit."))

(def ConcurrentJobLimitRetrievalSummary
  "Get a User's Concurrent Job Limit")

(def ConcurrentJobLimitRetrievalDescription
  (str "Gets the concurrent job limit for a user. The limit will either be the limit that was explicitly set for the "
       "user or the default limit. If the default limit is returned then there will be no username in the response "
       "body."))

(def ConcurrentJobLimitUpdateSummary
  "Update a User's Concurrent Job Limit")

(def ConcurrentJobLimitUpdateDescription
  (str "Updates the concurrent job limit for a user. The user's limit will be set explicitly even if it's equal to "
       "the default limit."))

(def ConcurrentJobLimitRemovalSummary
  "Remove a User's Concurrent Job Limit")

(def ConcurrentJobLimitRemovalDescription
  (str "Removes the explicitly configured concurrent job limit for a user. This effectively returns the user's job "
       "limit to whatever the default job limit is."))

(def ConcurrentJobLimitListItem
  (mu/closed-schema
   [:map
    [:username
     {:optional            true
      :description         "The username of the limited user, omitted for the default setting"
      :json-schema/example "janedoe"}
     :string]

    [:concurrent_jobs
     {:description         "The maximum number of concurrently running jobs"
      :json-schema/example 2}
     :int]

    [:is_default
     {:description         "True for the default setting."
      :json-schema/example false}
     :boolean]]))

(def ConcurrentJobLimit
  (mu/update-entry-properties
    ConcurrentJobLimitListItem :is_default
    assoc :description "True if the default setting is being used for the user."))

(def ConcurrentJobLimits
  (mu/closed-schema
    [:map
     [:limits
      {:description "The list of concurrent job limits"}
      [:vector ConcurrentJobLimitListItem]]]))

(def ConcurrentJobLimitUpdate
  (reduce mu/dissoc ConcurrentJobLimit [:username :is_default]))

(def AnalysisStatSummary "List analysis counts by status")
(def AnalysisStatDescription "This service allows users to retrieve the total count of jobs grouped by job status.")

(def AnalysisCount
  (mu/closed-schema
   [:map
    [:count
     {:description         "The total number of jobs with the attached job status."
      :json-schema/example 27}
     :int]

    [:status
     {:description         "The status for the attached job count."
      :json-schema/example "Completed"}
     :string]]))

(def AnalysisStats
  (mu/closed-schema
    [:map
     [:status-count
      {:description "List the total number of jobs grouped by job status for a user."}
      [:vector AnalysisCount]]]))

(def AnalysisStatParams
  (mu/closed-schema
   (reduce
    mu/merge
    [IncludeHiddenParams
     IncludeDeletedParams
     [:map
      [:filter
       {:optional            true
        :description         (slurp (io/resource "docs/analyses/listing/filter-param.md"))
        :json-schema/example "[{\"field\":\"ownership\",\"value\":\"all\"}]"}
       :string]]])))
