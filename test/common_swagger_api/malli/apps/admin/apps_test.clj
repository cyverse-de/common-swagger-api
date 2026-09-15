(ns common-swagger-api.malli.apps.admin.apps-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.apps.admin.apps :as admin-apps]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def job-stats
  {:job_count_completed 42
   :job_count           50
   :job_count_failed    3})

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

(def details-tool
  {:id      "tool-abc123"
   :name    "blast"
   :version "2.14.0"
   :type    "docker"})

(def app-details
  (assoc listing-detail
         :tools                [details-tool]
         :references           ["https://doi.org/10.1093/nar/gkv416"]
         :categories           [{:id #uuid "123e4567-e89b-12d3-a456-426614174000" :name "Bioinformatics"}]
         :suggested_categories []))

(def extra-info {:htcondor {:extra_requirements "request_gpus = 1"}})

(def publication-request
  {:id        #uuid "123e4567-e89b-12d3-a456-426614174000"
   :app       app-details
   :requestor "jsmith"})

(deftest AdminAppListingJobStats
  (valid admin-apps/AdminAppListingJobStats
         job-stats
         (assoc job-stats
                :job_last_completed #inst "2025-10-25T16:30:00.000-00:00"
                :last_used          #inst "2025-10-25T16:30:00.000-00:00"))
  (invalid admin-apps/AdminAppListingJobStats
           {}
           (dissoc job-stats :job_count)
           (assoc job-stats :job_count_failed "3")
           (assoc job-stats :last_used "2025-10-25")
           (assoc job-stats :extra 1)))

(deftest AdminAppListingDetail
  (valid admin-apps/AdminAppListingDetail listing-detail (assoc listing-detail :job_stats job-stats))
  (invalid admin-apps/AdminAppListingDetail
           {}
           (dissoc listing-detail :rating)
           (assoc listing-detail :step_count "1")
           (assoc listing-detail :job_stats {:job_count_completed 42})
           (assoc listing-detail :extra 1)))

(deftest AdminAppListing
  (valid admin-apps/AdminAppListing
         {:total 0 :apps []}
         {:total 1 :apps [(assoc listing-detail :job_stats job-stats)]})
  (invalid admin-apps/AdminAppListing
           {}
           {:total 0}
           {:total "0" :apps []}
           {:total 1 :apps [(dissoc listing-detail :rating)]}
           {:total 0 :apps [] :extra 1}))

(deftest sort-fields
  (is (contains? admin-apps/AdminAppListingJobStatsKeys :last_used))
  (is (contains? admin-apps/AdminAppSearchValidSortFields :job_count_failed))
  (is (contains? admin-apps/AdminAppSearchValidSortFields :name)))

(deftest AppPublicationRequestSearchParams
  (valid admin-apps/AppPublicationRequestSearchParams
         {}
         {:app_id #uuid "987e6543-e21b-32c1-b456-426614174000"}
         {:requestor "jsmith" :include_completed true})
  (invalid admin-apps/AppPublicationRequestSearchParams
           {:app_id "not-a-uuid"}
           {:include_completed "true"}
           {:extra 1}))

(deftest AdminAppSearchParams
  (valid admin-apps/AdminAppSearchParams
         {}
         {:search "blast" :sort-field :job_count :app-subset :private}
         {:limit 50 :offset 0 :sort-dir "ASC" :app-subset :all})
  (invalid admin-apps/AdminAppSearchParams
           {:sort-field :nope}
           {:app-subset :none}
           {:app-subset "public"}
           {:extra 1}))

(deftest AppExtraInfo
  (valid admin-apps/AppExtraInfo extra-info)
  (invalid admin-apps/AppExtraInfo
           {}
           {:htcondor {}}
           {:htcondor {:extra_requirements 1}}
           (assoc extra-info :extra 1)))

(deftest AdminAppDetails
  (valid admin-apps/AdminAppDetails
         app-details
         (assoc app-details :job_stats job-stats :extra extra-info)
         (assoc app-details :documentation {:documentation "docs" :references []}))
  (invalid admin-apps/AdminAppDetails
           {}
           listing-detail
           (assoc app-details :job_stats {:job_count_completed 42})
           (assoc app-details :documentation {:documentation "docs"})
           (assoc app-details :extra 1)))

(deftest AdminAppPatchRequest
  (valid admin-apps/AdminAppPatchRequest
         {}
         {:name "BLAST" :description "d" :wiki_url "https://wiki.example.org" :references []}
         {:deleted false :disabled false :extra extra-info
          :groups [{:id #uuid "fedcba98-7654-3210-fedc-ba9876543210" :label "Input Parameters"}]})
  (invalid admin-apps/AdminAppPatchRequest
           {:name 1}
           {:references "r"}
           {:groups [{}]}
           {:extra {:htcondor {}}}
           {:unexpected 1}))

(deftest ToolAdminAppListingResponses
  (is (= #{200 404 500 :default} (set (keys admin-apps/ToolAdminAppListingResponses))))
  (is (= admin-apps/AdminAppListing (get-in admin-apps/ToolAdminAppListingResponses [200 :body])))
  (valid (get-in admin-apps/ToolAdminAppListingResponses [404 :body]) {:error_code "ERR_NOT_FOUND"})
  (valid (get-in admin-apps/ToolAdminAppListingResponses [500 :body]) {:error_code "ERR_UNCHECKED_EXCEPTION"}))

(deftest AppPublicationRequest
  (valid admin-apps/AppPublicationRequest publication-request)
  (invalid admin-apps/AppPublicationRequest
           {}
           (dissoc publication-request :requestor)
           (assoc publication-request :id "not-a-uuid")
           (assoc publication-request :app listing-detail)
           (assoc publication-request :extra 1)))

(deftest AppPublicationRequestListing
  (valid admin-apps/AppPublicationRequestListing
         {:publication_requests []}
         {:publication_requests [publication-request]})
  (invalid admin-apps/AppPublicationRequestListing
           {}
           {:publication_requests publication-request}
           {:publication_requests [(dissoc publication-request :app)]}
           {:publication_requests [] :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps.admin.apps))
