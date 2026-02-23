(ns common-swagger-api.schema.analyses
  (:require [clojure.java.io :as io]
            [common-swagger-api.schema :refer [describe]]
            [common-swagger-api.schema.apps
             :refer [AppStepResourceRequirements
                     SystemId]]
            [common-swagger-api.schema.common :refer [IncludeDeletedParams IncludeHiddenParams]]
            [common-swagger-api.schema.containers
             :refer [coerce-settings-long-values]]
            [schema.core
             :refer [defschema
                     enum
                     optional-key
                     Any
                     Keyword]]
            [schema-tools.core :as st])
  (:import (java.util UUID)))

(defn- coerce-analysis-requirements-long-values
  [analysis]
  (if (contains? analysis :requirements)
    (update analysis :requirements (partial map coerce-settings-long-values))
    analysis))

(defn coerce-analysis-submission-requirements
  "Middleware that converts any requirements values in the given analysis submission that should be a Long."
  [handler]
  (fn [request]
    (handler (update request :body-params coerce-analysis-requirements-long-values))))

(def AnalysisParametersSummary "Display the parameters used in an analysis.")
(def AnalysisParametersDocs
  "This service returns a list of parameter values used in a previously executed analysis.")

(def AnalysisRelaunchSummary "Obtain information to relaunch analysis.")
(def AnalysisRelaunchDocs
  "This service allows the Discovery Environment user interface to obtain an app description
   that can be used to relaunch a previously submitted job,
   possibly with modified parameter values.")

(def AnalysesRelauncherSummary "Auto Relaunch Analyses")

(def AnalysisStopSummary "Stop a running analysis.")
(def AnalysisStopDocs
  "This service allows DE users to stop running analyses.")

(def AnalysisIdPathParam (describe UUID "The Analysis UUID"))

(defschema ParameterValue
  {:value
   (describe Any "The value of the parameter.")})

(defschema AnalysisParameter
  {:full_param_id
   (describe String "The fully qualified parameter ID.")

   :param_id
   (describe String "The unqualified parameter ID.")

   (optional-key :param_name)
   (describe String "The name of the parameter.")

   (optional-key :param_value)
   (describe ParameterValue "The value of the parameter.")

   :param_type
   (describe String "The type of the parameter.")

   (optional-key :info_type)
   (describe String "The type of information associated with an input or output parameter.")

   (optional-key :data_format)
   (describe String "The data format associated with an input or output parameter.")

   (optional-key :is_default_value)
   (describe Boolean "Indicates whether the default parameter value was used.")

   (optional-key :is_visible)
   (describe Boolean "Indicates whether the parameter is visible in the app UI.")})

(defschema AnalysisParameters
  {:app_id
   (describe String "The ID of the app used to perform the analysis.")

   (optional-key :app_version_id)
   (describe String "The version ID of the app used to perform the analysis.")

   :system_id
   SystemId

   :parameters
   (describe [AnalysisParameter] "The list of parameters.")})

(defschema AnalysesRelauncherRequest
  {:analyses (describe [UUID] "The identifiers of the analyses to be relaunched.")})

(defschema AnalysisShredderRequest
  {:analyses (describe [UUID] "The identifiers of the analyses to be deleted.")})

(defschema StopAnalysisRequest
  {(optional-key :job_status)
   (describe (enum "Canceled" "Completed" "Failed") "The job status to set. Defaults to `Canceled`")})

(defschema StopAnalysisResponse
  {:id (describe UUID "the ID of the stopped analysis.")})

(defschema FileMetadata
  {:attr  (describe String "The attribute name.")
   :value (describe String "The attribute value.")
   :unit  (describe String "The attribute unit.")})

(defschema AnalysisSubmissionConfig
  {(describe Keyword "The step-ID_param-ID") (describe Any "The param-value")})

(defschema AnalysisStepResourceRequirements
  (st/select-keys AppStepResourceRequirements [:min_memory_limit
                                               :min_cpu_cores
                                               :max_cpu_cores
                                               :min_gpus
                                               :max_gpus
                                               :min_disk_space
                                               :gpu_models
                                               :step_number]))

(defschema AnalysisSubmission
  {:system_id
   SystemId

   :app_id
   (describe String "The ID of the app used to perform the analysis.")

   (optional-key :app_version_id)
   (describe
    UUID
    "The ID of the app version used to perform the analysis.
      If not provided, then it is assumed the submission is for the latest version of the app")

   (optional-key :job_id)
   (describe UUID "The UUID of the job being submitted.")

   (optional-key :callback)
   (describe String "The callback URL to use for job status updates.")

   :config
   (describe AnalysisSubmissionConfig "A map from (str step-id \"_\" param-id) to param-value.")

   (optional-key :requirements)
   (describe [AnalysisStepResourceRequirements] "The list of optional resource requirements requested for any step")

   (optional-key :create_output_subdir)
   (describe Boolean "Indicates whether a subdirectory should be created beneath the specified output directory.")

   :debug
   (describe Boolean "A flag indicating whether or not job debugging should be enabled.")

   (optional-key :description)
   (describe String "An optional description of the analysis.")

   :name
   (describe String "The name assigned to the analysis by the user.")

   :notify
   (describe Boolean "Indicates whether the user wants to receive job status update notifications.")

   (optional-key :notify_periodic)
   (describe Boolean "Indicates whether the user wants to receive periodic email notifications that the job is still running, for interactive jobs.")

   (optional-key :periodic_period)
   (describe Integer "The number of seconds between periodic notifications, if the user has opted to receive them.")

   :output_dir
   (describe String "The path to the analysis output directory in the data store.")

   (optional-key :starting_step)
   (describe Long "The ordinal number of the step to start the job with.")

   (optional-key :uuid)
   (describe UUID "The UUID of the analysis. A random UUID will be assigned if one isn't provided.")

   (optional-key :skip-parent-meta)
   (describe Boolean "True if metadata should not associate metadata with the parent directory.")

   (optional-key :file-metadata)
   (describe [FileMetadata] "Custom file attributes to associate with result files.")

   (optional-key :archive_logs)
   (describe Boolean "True if the job logs should be uploaded to the data store.")

   (optional-key :mount_data_store)
   (describe Boolean "True if iRODS CSI Driver mounts should be created in the container.")})

(defschema AnalysisResponse
  {:id         (describe UUID "The ID of the submitted analysis.")
   :name       (describe String "The name of the submitted analysis.")
   :status     (describe String "The current status of the analysis.")
   :start-date (describe String "The analysis start date as milliseconds since the epoch.")

   (optional-key :missing-paths)
   (describe [String] "Any paths parsed from an HT Analysis Path List that no longer exist.")})

(defschema AnalysisPod
  {:name        (describe String "The name of a pod in Kubernetes associated with an analysis.")
   :external_id (describe UUID "The external ID associated with the pod.")})

(def AnalysisPodListSummary
  "List the Kubernetes pods associated with the analysis.")

(def AnalysisPodListDescription
  "This endpoint returns a listing of pod objects associated with the analysis. Usually will return a single pod.")

(defschema AnalysisPodList
  {:pods (describe [AnalysisPod] "A list of pods in Kubernetes associated with an analysis.")})

(def AnalysisPodLogSummary
  "The logs from a pod associated with the analysis")

(def AnalysisPodLogDescription
  "This endpoint returns the logs from the provided pod associated with the provided analysis.")

(defschema AnalysisPodLogParameters
  {(optional-key :previous)
   (describe Boolean "True if the logs of a previously terminated container should be returned")

   (optional-key :since)
   (describe Long "Number of seconds in the past to start showing logs")

   (optional-key :since-time)
   (describe String "The time at which to start showing log lines. Expressed as seconds since the epoch.")

   (optional-key :tail-lines)
   (describe Long "Number of lines from the end of the log to show")

   (optional-key :timestamps)
   (describe Boolean "True if timestamps should be prepended to the log lines")

   (optional-key :container)
   (describe String "Name of the container to display logs from. Defaults to 'analysis'")})

(defschema AnalysisPodLogEntry
  {:since_time
   (describe String "Contains the seconds since the epoch for the time when the log entry was retrieved")

   :lines
   (describe [String] "The lines that make up the log entry")})

(defschema AnalysisTimeLimit
  {:time_limit
   (describe String "Contains the seconds since the epoch for the analysis's time limit or the string 'null' if the time limit isn't set.")})

(def ConcurrentJobLimitUsername
  (describe String "The username associated with the limit"))

(def ConcurrentJobLimitListingSummary
  "List Concurrent Job Limits")

(def ConcurrentJobLimitListingDescription
  "Lists the concurrent job limits for all users who have one defined. The record describing the default limit contains
   no username. Users without explicit limits defined will use the default limit.")

(def ConcurrentJobLimitRetrievalSummary
  "Get a User's Concurrent Job Limit")

(def ConcurrentJobLimitRetrievalDescription
  "Gets the concurrent job limit for a user. The limit will either be the limit that was explicitly set for the user
   or the default limit. If the default limit is returned then there will be no username in the response body.")

(def ConcurrentJobLimitUpdateSummary
  "Update a User's Concurrent Job Limit")

(def ConcurrentJobLimitUpdateDescription
  "Updates the concurrent job limit for a user. The user's limit will be set explicitly even if it's equal to the
   default limit.")

(def ConcurrentJobLimitRemovalSummary
  "Remove a User's Concurrent Job Limit")

(def ConcurrentJobLimitRemovalDescription
  "Removes the explicitly configured concurrent job limit for a user. This effectively returns the user's job limit
   to whatever the default job limit is.")

(defschema ConcurrentJobLimitListItem
  {(optional-key :username)
   (describe String "The username of the limited user, omitted for the default setting")

   :concurrent_jobs
   (describe Long "The maximum number of concurrently running jobs")

   :is_default
   (describe Boolean "True for the default setting.")})

(defschema ConcurrentJobLimit
  (-> ConcurrentJobLimitListItem
      (st/dissoc :is_default)
      (st/assoc :is_default (describe Boolean "True if the default setting is being used for the user."))))

(defschema ConcurrentJobLimits
  {:limits (describe [ConcurrentJobLimitListItem] "The list of concurrent job limits")})

(defschema ConcurrentJobLimitUpdate
  (st/dissoc ConcurrentJobLimit :username :is_default))

(def AnalysisStatSummary "List analysis counts by status")
(def AnalysisStatDescription "This service allows users to retrieve the total count of jobs grouped by job status.")

(defschema AnalysisCount
  {:count (describe Long "The total number of jobs with the attached job status.")
   :status (describe String "The status for the attached job count.")})

(defschema AnalysisStats
  {:status-count (describe [AnalysisCount] "List the total number of jobs grouped by job status for a user.")})

(defschema AnalysisStatParams
  (merge IncludeHiddenParams
         IncludeDeletedParams
         {(optional-key :filter)
          (describe String (slurp (io/resource "docs/analyses/listing/filter-param.md")))}))
