(ns common-swagger-api.malli.analyses-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.analyses :as analyses]
   [common-swagger-api.malli.test-util :refer [decodes invalid json-schema-ok valid]]))

(def analysis-id #uuid "2b912405-5ae8-4db9-9023-db745b5a7c83")

(def parameter
  {:full_param_id "50d1fe6c-6258-477b-ab31-eefe96fe1213_17826e1e-9132-47e3-a2e0-0399f212bb92"
   :param_id      "17826e1e-9132-47e3-a2e0-0399f212bb92"
   :param_type    "FileInput"})

(def parameters
  {:app_id     "0f32febd-13fb-49b3-93f9-f6e39d46d817"
   :system_id  "de"
   :parameters [parameter]})

(def file-metadata {:attr "sample_weight" :value "2" :unit "kg"})

(def submission
  {:system_id  "de"
   :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
   :config     {:1104ffbe-3b81-4c64-868c-20f3dc86fd1a_ced83179-6aa8-424e-9cae-62fc8b21e1c0 "foo.txt"}
   :debug      false
   :name       "sequence-alignment-zea-mays"
   :notify     true
   :output_dir "/zone/home/username/folder-name"})

(def analysis-response
  {:id         #uuid "570e5ca1-194d-400a-bc91-fc64324f7367"
   :name       "An arabidopsis thaliana sequence alignment"
   :status     "Submitted"
   :start-date "1763083385000"})

(def pod
  {:name        "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05-79c44695b5-v8s4w"
   :external_id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05"})

(def job-limit {:concurrent_jobs 2 :is_default false})

(def analysis-count {:count 27 :status "Completed"})

(deftest AnalysisIdPathParam
  (valid analyses/AnalysisIdPathParam analysis-id)
  (invalid analyses/AnalysisIdPathParam (str analysis-id) nil))

(deftest ParameterValue
  (valid analyses/ParameterValue {:value "foo.txt"} {:value nil} {:value 3})
  (invalid analyses/ParameterValue {} {:value "v" :extra 1}))

(deftest AnalysisParameter
  (valid analyses/AnalysisParameter
         parameter
         (assoc parameter :param_name "Input File" :param_value {:value "foo.txt"})
         (assoc parameter :info_type "SequenceAlignment" :data_format "FASTA"
                :is_default_value false :is_visible true))
  (invalid analyses/AnalysisParameter
           {}
           (dissoc parameter :param_type)
           (assoc parameter :is_visible "yes")
           (assoc parameter :param_value {:value "v" :extra 1})
           (assoc parameter :extra 1)))

(deftest AnalysisParameters
  (valid analyses/AnalysisParameters
         parameters
         (assoc parameters :parameters [])
         (assoc parameters :app_version_id "763a0d0c-d2bd-46fc-b3f6-747ba9c82cfa"))
  (invalid analyses/AnalysisParameters
           {}
           (dissoc parameters :system_id)
           (assoc parameters :parameters [{}])
           (assoc parameters :extra 1)))

(deftest AnalysesRelauncherRequest
  (valid analyses/AnalysesRelauncherRequest {:analyses []} {:analyses [analysis-id]})
  (invalid analyses/AnalysesRelauncherRequest {} {:analyses [(str analysis-id)]}
           {:analyses [analysis-id] :extra 1}))

(deftest AnalysisShredderRequest
  (valid analyses/AnalysisShredderRequest {:analyses []} {:analyses [analysis-id]})
  (invalid analyses/AnalysisShredderRequest {} {:analyses analysis-id} {:analyses [] :extra 1}))

(deftest StopAnalysisRequest
  (valid analyses/StopAnalysisRequest {} {:job_status "Canceled"} {:job_status "Failed"})
  (invalid analyses/StopAnalysisRequest {:job_status "Nope"} {:job_status :Canceled} {:extra 1}))

(deftest StopAnalysisResponse
  (valid analyses/StopAnalysisResponse {:id analysis-id})
  (invalid analyses/StopAnalysisResponse {} {:id (str analysis-id)} {:id analysis-id :extra 1}))

(deftest FileMetadata
  (valid analyses/FileMetadata file-metadata)
  (invalid analyses/FileMetadata {} (dissoc file-metadata :unit) (assoc file-metadata :value 2)
           (assoc file-metadata :extra 1)))

(deftest AnalysisSubmissionConfig
  (valid analyses/AnalysisSubmissionConfig {} (:config submission) {:step_param nil})
  (invalid analyses/AnalysisSubmissionConfig {"step_param" "v"} [] nil))

(deftest AnalysisStepResourceRequirements
  (valid analyses/AnalysisStepResourceRequirements
         {:step_number 1}
         {:step_number 1 :min_memory_limit 1024 :min_disk_space 10737418240}
         {:step_number 1 :min_cpu_cores 1.0 :max_cpu_cores 4.0 :min_gpus 0 :max_gpus 2}
         {:step_number 1 :gpu_models ["A100"]})
  (invalid analyses/AnalysisStepResourceRequirements
           {}
           {:step_number "1"}
           {:step_number 1 :gpu_models "A100"}
           {:step_number 1 :memory_limit 1024}
           {:step_number 1 :default_max_gpus 2}))

(deftest AnalysisSubmission
  (valid analyses/AnalysisSubmission
         submission
         (assoc submission
                :app_version_id #uuid "02ff8f75-a4fc-4d8c-9d76-ef91f764cec4"
                :job_id         #uuid "1738fd3b-702d-4022-9c89-0337ef726cfb"
                :uuid           #uuid "df97797c-0f8d-41ae-898d-68dcd79abfc8")
         (assoc submission :requirements [{:step_number 1 :min_memory_limit 1024}]
                :file-metadata [file-metadata])
         (assoc submission :notify_periodic true :periodic_period 3600 :time_limit_seconds 3600))
  (invalid analyses/AnalysisSubmission
           {}
           (dissoc submission :output_dir)
           (assoc submission :app_version_id "02ff8f75-a4fc-4d8c-9d76-ef91f764cec4")
           (assoc submission :time_limit_seconds "3600")
           (assoc submission :requirements [{}])
           (assoc submission :extra 1)))

(deftest requirements-decode-longs
  (decodes analyses/AnalysisSubmission
           (assoc submission :requirements [{:step_number 1 :min_memory_limit "1024"}])
           (assoc submission :requirements [{:step_number 1 :min_memory_limit 1024}])))

(deftest AnalysisResponse
  (valid analyses/AnalysisResponse analysis-response (assoc analysis-response :missing-paths ["/foo/bar"]))
  (invalid analyses/AnalysisResponse {} (dissoc analysis-response :status)
           (assoc analysis-response :missing-paths "/foo/bar") (assoc analysis-response :extra 1)))

(deftest AnalysisPod
  (valid analyses/AnalysisPod pod)
  (invalid analyses/AnalysisPod {} (dissoc pod :external_id) (assoc pod :external_id "x")
           (assoc pod :extra 1)))

(deftest AnalysisPodList
  (valid analyses/AnalysisPodList {:pods []} {:pods [pod]})
  (invalid analyses/AnalysisPodList {} {:pods [{}]} {:pods [], :extra 1}))

(deftest AnalysisPodLogParameters
  (valid analyses/AnalysisPodLogParameters
         {}
         {:previous true :since 3600 :tail-lines 100}
         {:since-time "1763156075" :timestamps true :container "analysis"})
  (invalid analyses/AnalysisPodLogParameters {:since "3600"} {:previous "yes"} {:extra 1}))

(deftest AnalysisPodLogEntry
  (valid analyses/AnalysisPodLogEntry {:since_time "1763156649" :lines []}
         {:since_time "1763156649" :lines ["line one" "line two"]})
  (invalid analyses/AnalysisPodLogEntry {} {:since_time "1763156649"}
           {:since_time "1763156649" :lines "line one"}))

(deftest AnalysisTimeLimit
  (valid analyses/AnalysisTimeLimit {:time_limit "1763157634"} {:time_limit "null"})
  (invalid analyses/AnalysisTimeLimit {} {:time_limit 1763157634} {:time_limit "null" :extra 1}))

(deftest ConcurrentJobLimitUsername
  (valid analyses/ConcurrentJobLimitUsername "janedoe")
  (invalid analyses/ConcurrentJobLimitUsername :janedoe nil))

(deftest ConcurrentJobLimitListItem
  (valid analyses/ConcurrentJobLimitListItem job-limit (assoc job-limit :username "janedoe"))
  (invalid analyses/ConcurrentJobLimitListItem {} (dissoc job-limit :is_default)
           (assoc job-limit :concurrent_jobs "2") (assoc job-limit :extra 1)))

(deftest ConcurrentJobLimit
  (valid analyses/ConcurrentJobLimit job-limit (assoc job-limit :username "janedoe"))
  (invalid analyses/ConcurrentJobLimit {} (dissoc job-limit :concurrent_jobs)
           (assoc job-limit :is_default "no") (assoc job-limit :extra 1)))

(deftest ConcurrentJobLimits
  (valid analyses/ConcurrentJobLimits {:limits []} {:limits [job-limit]})
  (invalid analyses/ConcurrentJobLimits {} {:limits [{}]} {:limits [] :extra 1}))

(deftest ConcurrentJobLimitUpdate
  (valid analyses/ConcurrentJobLimitUpdate {:concurrent_jobs 2})
  (invalid analyses/ConcurrentJobLimitUpdate {} {:concurrent_jobs "2"} job-limit
           {:concurrent_jobs 2 :username "janedoe"}))

(deftest AnalysisCount
  (valid analyses/AnalysisCount analysis-count)
  (invalid analyses/AnalysisCount {} (dissoc analysis-count :status) (assoc analysis-count :count "27")
           (assoc analysis-count :extra 1)))

(deftest AnalysisStats
  (valid analyses/AnalysisStats {:status-count []} {:status-count [analysis-count]})
  (invalid analyses/AnalysisStats {} {:status-count [{}]} {:status-count [] :extra 1}))

(deftest AnalysisStatParams
  (valid analyses/AnalysisStatParams
         {}
         {:include-hidden true :include-deleted false}
         {:filter "[{\"field\":\"ownership\",\"value\":\"all\"}]"})
  (invalid analyses/AnalysisStatParams {:include-hidden "yes"} {:filter 1} {:extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.analyses))
