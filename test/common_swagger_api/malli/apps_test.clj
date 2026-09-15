(ns common-swagger-api.malli.apps-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.apps :as apps]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]
   [malli.json-schema :as js]))

(def app-id #uuid "987e6543-e21b-32c1-b456-426614174000")
(def version-id #uuid "456e7890-b12c-34d5-e678-901234567890")

(def list-item
  {:id #uuid "789a0123-c45d-67e8-f901-234567890abc" :name "n" :value "v" :isDefault false})

(def parameter
  {:id #uuid "abc12345-def6-7890-abcd-ef1234567890" :type "FileInput"})

(def group
  {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210" :label "Input Parameters"})

(def tool
  {:id #uuid "123e4567-e89b-12d3-a456-426614174000" :name "blast" :version "2.14.0" :type "docker"})

(def app-base
  {:id app-id :name "BLAST" :description "Basic Local Alignment Search Tool"})

(def tool-details
  (assoc tool
         :is_public      true
         :permission     "own"
         :implementation {:implementor       "Jane Smith"
                          :implementor_email "jane.smith@example.org"
                          :test              {:input_files [] :output_files []}}
         :container      {:id    #uuid "123e4567-e89b-12d3-a456-426614174000"
                          :image {:id   #uuid "456e7890-b12c-34d5-e678-901234567890"
                                  :name "ncbi/blast"}}))

(def file-parameter-details
  {:id "param_123" :name "out" :description "d" :label "Output" :format "bam" :required true})

(def task
  {:system_id   "de"
   :id          "task_456"
   :name        "BLAST Analysis"
   :description "Performs BLAST sequence alignment analysis"
   :inputs      [file-parameter-details]
   :outputs     [file-parameter-details]})

(def listing-detail
  {:id                   "app-id-123"
   :name                 "BLAST"
   :description          "Basic sequence alignment"
   :app_type             "DE"
   :can_favor            true
   :can_rate             true
   :can_run              true
   :deleted              false
   :disabled             false
   :integrator_email     "user@example.org"
   :integrator_name      "Test User"
   :is_public            true
   :pipeline_eligibility {:is_valid true :reason ""}
   :rating               {:average 4.5 :total 42}
   :step_count           1
   :permission           "own"})

(def details-tool (assoc tool :id "tool-abc123"))

(def app-details
  (assoc listing-detail
         :tools                [details-tool]
         :references           ["https://doi.org/10.1093/nar/gkv416"]
         :categories           [{:id #uuid "123e4567-e89b-12d3-a456-426614174000" :name "Bioinformatics"}]
         :suggested_categories []))

(def job-view
  (assoc app-base
         :id       "app-id-abc123"
         :app_type "DE"
         :label    "BLAST Analysis Tool"
         :deleted  false
         :disabled false))

(def resource-preset
  {:id                 #uuid "8f0d3f2c-1c4a-4b7e-9a2f-5d6c7e8f9a0b"
   :label              "Small"
   :description        nil
   :max_cpu_cores      2.0
   :min_memory_limit   4294967296
   :max_gpus           0
   :time_limit_seconds nil
   :display_order      1
   :is_default         false
   :is_enabled         true})

(def avu {:attr "a" :value "v" :unit "u"})

(deftest StringAppIdParam
  (valid apps/StringAppIdParam "app-id-12345")
  (invalid apps/StringAppIdParam "" " " 1))

(deftest SystemId
  (valid apps/SystemId "de")
  (invalid apps/SystemId "" " " 1))

(deftest AppParameterListItem
  (valid apps/AppParameterListItem list-item {:id (:id list-item)}
         (assoc list-item :description "d" :display "D"))
  (invalid apps/AppParameterListItem {} (assoc list-item :id "x") (assoc list-item :extra 1)))

(deftest AppParameterListGroup
  (valid apps/AppParameterListGroup
         list-item
         (assoc list-item :arguments [list-item])
         (assoc list-item :arguments [list-item] :groups [(assoc list-item :groups [list-item])]))
  (invalid apps/AppParameterListGroup {} (assoc list-item :groups [{:id "x"}]) (assoc list-item :groups list-item)))

(deftest AppParameterListItemOrTree
  (valid apps/AppParameterListItemOrTree
         list-item
         (assoc list-item :isSingleSelect true :selectionCascade "up")
         (assoc list-item :arguments [list-item] :groups [(assoc list-item :groups [list-item])]))
  (invalid apps/AppParameterListItemOrTree {} (assoc list-item :isSingleSelect "yes") (assoc list-item :extra 1)))

(deftest AppParameterValidator
  (valid apps/AppParameterValidator {:type "IntAbove" :params [0]} {:type "Regex" :params []})
  (invalid apps/AppParameterValidator {} {:type "IntAbove"} {:type "IntAbove" :params 0}))

(deftest AppFileParameters
  (valid apps/AppFileParameters {} {:format "fasta" :file_info_type "SequenceAlignment"}
         {:is_implicit false :repeat_option_flag false :data_source "stdout" :retain true})
  (invalid apps/AppFileParameters {:format 1} {:retain "yes"} {:extra 1}))

(deftest AppParameter
  (valid apps/AppParameter
         parameter
         (assoc parameter :name "--input" :label "Input File" :order 1 :required true)
         (assoc parameter :file_parameters {:format "fasta"} :arguments [list-item]
                :validators [{:type "IntAbove" :params [0]}]))
  (invalid apps/AppParameter {} (dissoc parameter :type) (assoc parameter :order "1")))

(deftest AppGroup
  (valid apps/AppGroup group (assoc group :name "input_parameters" :isVisible true)
         (assoc group :parameters [parameter]))
  (invalid apps/AppGroup {} (dissoc group :label) (assoc group :parameters [{}])))

(deftest AppVersionDetails
  (valid apps/AppVersionDetails {:version "1.2.0" :version_id version-id})
  (invalid apps/AppVersionDetails {} {:version "1.2.0"} {:version "1.2.0" :version_id "x"}))

(deftest AppVersionListing
  (valid apps/AppVersionListing {} {:versions []} {:versions [{:version "1.2.0" :version_id version-id}]})
  (invalid apps/AppVersionListing {:versions [{}]} {:versions {}}))

(deftest AppVersionOrderRequest
  (valid apps/AppVersionOrderRequest {:versions []} {:versions [{:version_id version-id}]}
         {:versions [{:version "1.2.0" :version_id version-id}]})
  (invalid apps/AppVersionOrderRequest {} {:versions [{:version "1.2.0"}]}))

(deftest AppBase
  (valid apps/AppBase app-base (assoc app-base :system_id "de" :version "1.2.0" :version_id version-id)
         (assoc app-base :integration_date #inst "2024-01-15T10:30:00.000-00:00"))
  (invalid apps/AppBase {} (dissoc app-base :name) (assoc app-base :id "x")))

(deftest AppLimitCheckResult
  (valid apps/AppLimitCheckResult
         {:limitCheckID "concurrent-job-limit" :reasonCodes ["MAX_JOBS_EXCEEDED"] :additionalInfo {}})
  (invalid apps/AppLimitCheckResult {} {:limitCheckID "x" :reasonCodes ["y"]}
           {:limitCheckID "x" :reasonCodes "y" :additionalInfo {}}))

(deftest AppLimitCheckResultSummary
  (valid apps/AppLimitCheckResultSummary
         {:canRun true :results []}
         {:canRun  false
          :results [{:limitCheckID "x" :reasonCodes ["y"] :additionalInfo {}}]})
  (invalid apps/AppLimitCheckResultSummary {} {:canRun true} {:canRun "yes" :results []}))

(deftest AppLimitChecks
  (valid apps/AppLimitChecks {} {:limitChecks {:canRun true :results []}})
  (invalid apps/AppLimitChecks {:limitChecks {}} {:limitChecks true}))

(deftest AppTools
  (valid apps/AppTools {} {:tools [tool]} {:references ["r"] :groups [group]})
  (invalid apps/AppTools {:tools [{}]} {:groups [{}]}))

(deftest App
  (valid apps/App app-base (assoc app-base :tools [(assoc tool :deprecated false)])
         (assoc app-base :references ["r"] :groups [group] :versions []))
  (invalid apps/App {} (dissoc app-base :id) (assoc app-base :tools [{}])))

(deftest AppLabelUpdateRequest
  (valid apps/AppLabelUpdateRequest app-base (assoc app-base :groups [group]))
  (invalid apps/AppLabelUpdateRequest {} (assoc app-base :versions [])))

(deftest AppFileParameterDetails
  (valid apps/AppFileParameterDetails file-parameter-details)
  (invalid apps/AppFileParameterDetails {} (dissoc file-parameter-details :format)
           (assoc file-parameter-details :required "yes")))

(deftest AppTask
  (valid apps/AppTask task (assoc task :tool tool-details))
  (invalid apps/AppTask {} (dissoc task :inputs) (assoc task :outputs [{}]) (assoc task :tool tool)))

(deftest AppTaskListing
  (valid apps/AppTaskListing (assoc app-base :id "app_789" :tasks []) (assoc app-base :id "a" :tasks [task]))
  (invalid apps/AppTaskListing {} (assoc app-base :tasks []) (assoc app-base :id "a")))

(deftest AppParameterJobView
  (valid apps/AppParameterJobView (assoc parameter :id "step_123_param_456")
         (assoc parameter :id "s_p" :name "--input" :arguments [list-item]))
  (invalid apps/AppParameterJobView {} parameter (assoc parameter :id "s_p" :type 1)))

(deftest AppStepResourceRequirements
  (valid apps/AppStepResourceRequirements
         {:step_number 1}
         {:step_number 1 :memory_limit 1073741824 :min_cpu_cores 1.0 :max_cpu_cores 4.0 :gpu_models ["A100"]}
         {:step_number 1 :default_max_gpus 2 :default_gpus 0 :default_gpu_models ["A100"]})
  (invalid apps/AppStepResourceRequirements {} {:step_number "1"} {:step_number 1 :default_gpu_models "A100"}))

(deftest ResourcePreset
  (valid apps/ResourcePreset resource-preset (assoc resource-preset :description "d" :time_limit_seconds 10))
  (invalid apps/ResourcePreset (dissoc resource-preset :label) (assoc resource-preset :max_cpu_cores "2")))

(deftest ResourcePresetList
  (valid apps/ResourcePresetList {:resource_presets []} {:resource_presets [resource-preset]})
  (invalid apps/ResourcePresetList {} {:resource_presets [{}]}))

(deftest ResourcePresetRequest
  (valid apps/ResourcePresetRequest
         (select-keys resource-preset [:label :max_cpu_cores :min_memory_limit])
         (dissoc resource-preset :id))
  (invalid apps/ResourcePresetRequest resource-preset (select-keys resource-preset [:label])))

(deftest ResourcePresetUpdateRequest
  (valid apps/ResourcePresetUpdateRequest {} {:description nil} (dissoc resource-preset :id))
  (invalid apps/ResourcePresetUpdateRequest {:id (:id resource-preset)} {:label 1}))

(deftest AppGroupJobView
  (valid apps/AppGroupJobView
         {:id "step_123_group_456" :label "Input Parameters" :step_number 1}
         {:id "g" :label "l" :step_number 2 :parameters [(assoc parameter :id "s_p")]})
  (invalid apps/AppGroupJobView {} {:id "g" :label "l"} {:id "g" :label "l" :step_number 1 :parameters [parameter]}))

(deftest AppJobView
  (valid apps/AppJobView
         job-view
         (assoc job-view :debug false :mount_data_store true :overall_job_type "executable")
         (assoc job-view :time_limit_seconds 28800 :max_time_limit_seconds 86400
                :requirements [{:step_number 1}] :limitChecks {:canRun true :results []}))
  (invalid apps/AppJobView {} (dissoc job-view :label) (assoc job-view :mount_data_store "yes")))

(deftest AppDetailCategory
  (valid apps/AppDetailCategory {:id #uuid "123e4567-e89b-12d3-a456-426614174000" :name "Bioinformatics"})
  (invalid apps/AppDetailCategory {} {:id "x" :name "n"} {:name "n"}))

(deftest AppDetailsTool
  (valid apps/AppDetailsTool details-tool
         (assoc details-tool :container {:image {:name "ncbi/blast" :tag "2.14.0"}}))
  (invalid apps/AppDetailsTool {} tool (dissoc details-tool :version)))

(deftest AppListingJobStats
  (valid apps/AppListingJobStats {:job_count_completed 42}
         {:job_count_completed 0 :job_last_completed #inst "2025-10-25T16:30:00.000-00:00"})
  (invalid apps/AppListingJobStats {} {:job_count_completed "42"} {:job_count_completed 1 :extra 1}))

(deftest AppToolListing
  (valid apps/AppToolListing {:tools []} {:tools [details-tool]})
  (invalid apps/AppToolListing {} {:tools [tool]}))

(deftest AppDocumentation
  (valid apps/AppDocumentation
         {:documentation "docs" :references []}
         {:documentation "docs" :references ["r"] :app_id "app-id-12345" :version_id "v1"
          :created_by "jsmith" :created_on #inst "2024-02-10T09:15:00.000-00:00"})
  (invalid apps/AppDocumentation {} {:documentation "docs"} {:documentation "docs" :references "r"}))

(deftest AppDocumentationRequest
  (valid apps/AppDocumentationRequest {:documentation "docs"} {:documentation "docs" :modified_by "jdoe"})
  (invalid apps/AppDocumentationRequest {} {:documentation "docs" :references []}))

(deftest PipelineEligibility
  (valid apps/PipelineEligibility {:is_valid true :reason ""})
  (invalid apps/PipelineEligibility {} {:is_valid true} {:is_valid "yes" :reason ""}))

(deftest AppListingDetail
  (valid apps/AppListingDetail
         listing-detail
         (assoc listing-detail :system_id "de" :overall_job_type "executable" :version "1.2.0"
                :version_id "version-id-456" :is_favorite true :beta false :isBlessed true)
         (assoc listing-detail :limitChecks {:canRun true :results []} :wiki_url "https://wiki.example.org"))
  (invalid apps/AppListingDetail {} (dissoc listing-detail :rating) (assoc listing-detail :step_count "1")))

(deftest AppDetails
  (valid apps/AppDetails
         app-details
         (assoc app-details :job_stats {:job_count_completed 42} :versions [])
         (assoc app-details :hierarchies []))
  (invalid apps/AppDetails {} listing-detail (assoc app-details :tools [tool])))

(deftest AppListing
  (valid apps/AppListing {:total 0 :apps []} {:total 1 :apps [listing-detail]})
  (invalid apps/AppListing {} {:total 0} {:total "0" :apps []}))

(deftest sort-fields
  (is (contains? apps/AppListingValidSortFields :name))
  (is (not (contains? apps/AppListingValidSortFields :rating)))
  (is (contains? apps/AppSearchValidSortFields :total))
  (is (not (contains? apps/AppSearchValidSortFields :user_rating))))

(deftest AppFilterParams
  (valid apps/AppFilterParams {} {:app-type "DE"})
  (invalid apps/AppFilterParams {:app-type 1} {:app-type "DE" :extra 1}))

(deftest AttributeValueSelectionParams
  (valid apps/AttributeValueSelectionParams {} {:attribute "category"} {:attribute_value "genomics"})
  (invalid apps/AttributeValueSelectionParams {:attribute 1} {:attribute_value 1}))

(deftest AppListingPagingParams
  (valid apps/AppListingPagingParams {} {:limit 50 :offset 0 :sort-dir "ASC" :sort-field :name}
         {:app-type "DE" :attribute "category" :attribute_value "genomics"})
  (invalid apps/AppListingPagingParams {:sort-field :nope} {:limit 0} {:sort-dir "UP"})
  (is (= :name (get-in (js/transform apps/AppListingPagingParams) [:properties :sort-field :example]))))

(deftest AppSearchParams
  (valid apps/AppSearchParams {} {:attribute "a" :attribute_value "b" :search "x" :sort-field :name}
         {:start_date #inst "2024-01-01T00:00:00.000-00:00" :end_date #inst "2025-12-31T23:59:59.000-00:00"})
  (invalid apps/AppSearchParams {:attribute 1} {:sort-field :nope} {:start_date "2024-01-01"})
  (is (= :name (get-in (js/transform apps/AppSearchParams) [:properties :sort-field :example]))))

(deftest QualifiedAppId
  (valid apps/QualifiedAppId {:system_id "de" :app_id "app-id-12345"})
  (invalid apps/QualifiedAppId {} {:system_id "" :app_id "a"} {:system_id "de"}))

(deftest AppDeletionRequest
  (valid apps/AppDeletionRequest {:app_ids []}
         {:app_ids [{:system_id "de" :app_id "a"}] :root_deletion_request true})
  (invalid apps/AppDeletionRequest {} {:app_ids [{}]} {:app_ids [] :root_deletion_request "yes"}))

(deftest AppParameterListItemRequest
  (valid apps/AppParameterListItemRequest {} list-item (dissoc list-item :id))
  (invalid apps/AppParameterListItemRequest (assoc list-item :id "x") (assoc list-item :extra 1)))

(deftest AppParameterListGroupRequest
  (valid apps/AppParameterListGroupRequest
         {}
         (dissoc list-item :id)
         (assoc list-item :arguments [(dissoc list-item :id)] :groups [{:groups [{}]}]))
  (invalid apps/AppParameterListGroupRequest (assoc list-item :id "x") {:groups [{:id "x"}]}))

(deftest AppParameterListItemOrTreeRequest
  (valid apps/AppParameterListItemOrTreeRequest
         {}
         (assoc list-item :isSingleSelect true)
         (assoc list-item :arguments [{}] :groups [{:groups [{}]}]))
  (invalid apps/AppParameterListItemOrTreeRequest (assoc list-item :id "x") {:arguments [{:id "x"}]}))

(deftest AppParameterRequest
  (valid apps/AppParameterRequest {:type "FileInput"} parameter
         (assoc parameter :arguments [{:isSingleSelect true}]))
  (invalid apps/AppParameterRequest {} (assoc parameter :id "x") (assoc parameter :arguments [{:id "x"}])))

(deftest AppGroupRequest
  (valid apps/AppGroupRequest {:label "l"} group (assoc group :parameters [{:type "FileInput"}]))
  (invalid apps/AppGroupRequest {} (assoc group :id "x") (assoc group :parameters [{}])))

(deftest AppToolRequest
  (valid apps/AppToolRequest tool (assoc tool :deprecated false :is_public true)
         (assoc tool :description "d" :location "/usr/local/bin"))
  (invalid apps/AppToolRequest {} (dissoc tool :version) (assoc tool :deprecated "yes")))

(deftest AppRequest
  (valid apps/AppRequest
         (dissoc app-base :id)
         app-base
         (assoc app-base :tools [tool] :groups [{:label "l"}] :version "1.2.0" :version_id version-id))
  (invalid apps/AppRequest {} (dissoc app-base :name) (assoc app-base :versions [])))

(deftest AppVersionRequest
  (valid apps/AppVersionRequest (assoc (dissoc app-base :id) :version "1.2.0")
         (assoc app-base :version "1.2.0" :tools [tool]))
  (invalid apps/AppVersionRequest app-base (assoc app-base :version "1.2.0" :version_id version-id)))

(deftest AppCreateRequest
  (valid apps/AppCreateRequest app-base)
  (invalid apps/AppCreateRequest {}))

(deftest AppUpdateRequest
  (valid apps/AppUpdateRequest app-base)
  (invalid apps/AppUpdateRequest {}))

(deftest AppPreviewRequest
  (valid apps/AppPreviewRequest {} app-base (assoc app-base :is_public true :tools [tool] :groups [{:label "l"}]))
  (invalid apps/AppPreviewRequest (assoc app-base :id "x") (assoc app-base :tools [{}])))

(deftest AppCategoryMetadataAddRequest
  (valid apps/AppCategoryMetadataAddRequest {:avus []} {:avus [(assoc avu :avus [avu])]})
  (invalid apps/AppCategoryMetadataAddRequest {} {:avus [{}]}))

(deftest AppCategoryMetadataDeleteRequest
  (valid apps/AppCategoryMetadataDeleteRequest {:avus []} {:avus [avu]})
  (invalid apps/AppCategoryMetadataDeleteRequest {} {:avus [{:attr "a"}]}))

(deftest PublishAppRequest
  (valid apps/PublishAppRequest
         {}
         (assoc app-base :documentation "docs" :references ["r"])
         {:avus [(assoc avu :avus [avu])] :version "1.2.0" :version_id version-id})
  (invalid apps/PublishAppRequest (assoc app-base :id "x") {:avus [{}]} {:extra 1}))

(deftest AppPublishableResponse
  (valid apps/AppPublishableResponse {:publishable true} {:publishable false :reason "not public"})
  (invalid apps/AppPublishableResponse {} {:publishable "yes"} {:publishable true :extra 1}))

(deftest ToolAppListingResponses
  (is (= #{200 404 500 :default} (set (keys apps/ToolAppListingResponses))))
  (is (= apps/AppListing (get-in apps/ToolAppListingResponses [200 :body]))))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps))

(deftest examples
  (examples-valid 'common-swagger-api.malli.apps))
