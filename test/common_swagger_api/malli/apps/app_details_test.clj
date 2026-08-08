(ns common-swagger-api.malli.apps.app-details-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common-swagger-api.malli.apps :as apps]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AppDetails
  (testing "AppDetails validation"
    (testing "valid app details - minimal required fields only"
      (is (valid? apps/AppDetails
                  {:id "app-id-abc123"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool for sequence comparison"
                   :app_type "DE"
                   :can_favor true
                   :can_rate true
                   :can_run true
                   :deleted false
                   :disabled false
                   :integrator_email "jsmith@example.org"
                   :integrator_name "John Smith"
                   :is_public true
                   :pipeline_eligibility {:is_valid true
                                          :reason "This app contains tasks that are not public"}
                   :rating {:average 4.5
                            :total 10
                            :user 5}
                   :step_count 1
                   :permission "own"
                   :tools []
                   :references []
                   :categories []
                   :suggested_categories []})))

    (testing "valid app details - with all required and common optional fields"
      (is (valid? apps/AppDetails
                  {:id "app-id-abc123"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool for sequence comparison"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                   :system_id "de"
                   :app_type "DE"
                   :overall_job_type "executable"
                   :can_favor true
                   :can_rate true
                   :can_run true
                   :deleted false
                   :disabled false
                   :version "2.1.0"
                   :version_id "latest-version-id-123"
                   :integrator_email "jsmith@example.org"
                   :integrator_name "John Smith"
                   :is_favorite true
                   :is_public true
                   :beta false
                   :isBlessed true
                   :pipeline_eligibility {:is_valid true
                                          :reason "This app contains tasks that are not public"}
                   :rating {:average 4.5
                            :total 10
                            :user 5}
                   :step_count 1
                   :wiki_url "https://wiki.example.org/apps/blast"
                   :permission "own"
                   :tools [{:id "tool-abc123"
                            :name "samtools"
                            :version "1.15.1"
                            :type "executable"}]
                   :references ["https://doi.org/10.1093/nar/gkv416" "PMID: 25916842"]
                   :categories [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                 :name "Bioinformatics"}]
                   :suggested_categories [{:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                                           :name "Sequence Analysis"}]})))

    (testing "valid app details - with optional job_stats field"
      (is (valid? apps/AppDetails
                  {:id "app-id-abc123"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool for sequence comparison"
                   :app_type "DE"
                   :can_favor true
                   :can_rate true
                   :can_run true
                   :deleted false
                   :disabled false
                   :integrator_email "jsmith@example.org"
                   :integrator_name "John Smith"
                   :is_public true
                   :pipeline_eligibility {:is_valid true
                                          :reason ""}
                   :rating {:average 4.5
                            :total 10
                            :user 5}
                   :step_count 1
                   :permission "own"
                   :tools []
                   :references []
                   :categories []
                   :suggested_categories []
                   :job_stats {:job_count_completed 42
                               :job_last_completed #inst "2025-10-25T16:30:00.000-00:00"}})))

    (testing "valid app details - with optional hierarchies field"
      (is (valid? apps/AppDetails
                  {:id "app-id-abc123"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool for sequence comparison"
                   :app_type "DE"
                   :can_favor true
                   :can_rate true
                   :can_run true
                   :deleted false
                   :disabled false
                   :integrator_email "jsmith@example.org"
                   :integrator_name "John Smith"
                   :is_public true
                   :pipeline_eligibility {:is_valid true
                                          :reason ""}
                   :rating {:average 4.5
                            :total 10
                            :user 5}
                   :step_count 1
                   :permission "own"
                   :tools []
                   :references []
                   :categories []
                   :suggested_categories []
                   :hierarchies [{:iri "http://purl.obolibrary.org/obo/GO_0008150"
                                  :label "biological_process"}]})))

    (testing "valid app details - with versions field from AppVersionListing"
      (is (valid? apps/AppDetails
                  {:id "app-id-abc123"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool for sequence comparison"
                   :app_type "DE"
                   :can_favor true
                   :can_rate true
                   :can_run true
                   :deleted false
                   :disabled false
                   :integrator_email "jsmith@example.org"
                   :integrator_name "John Smith"
                   :is_public true
                   :pipeline_eligibility {:is_valid true
                                          :reason ""}
                   :rating {:average 4.5
                            :total 10
                            :user 5}
                   :step_count 1
                   :permission "own"
                   :tools []
                   :references []
                   :categories []
                   :suggested_categories []
                   :versions [{:version "1.0.0"
                               :version_id #uuid "111e1111-a11a-11a1-a111-111111111111"}
                              {:version "2.0.0"
                               :version_id #uuid "222e2222-b22b-22b2-b222-222222222222"}]})))

    (testing "valid app details - with limitChecks from AppLimitChecks"
      (is (valid? apps/AppDetails
                  {:id "app-id-abc123"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool for sequence comparison"
                   :app_type "DE"
                   :can_favor true
                   :can_rate true
                   :can_run true
                   :deleted false
                   :disabled false
                   :integrator_email "jsmith@example.org"
                   :integrator_name "John Smith"
                   :is_public true
                   :pipeline_eligibility {:is_valid true
                                          :reason ""}
                   :rating {:average 4.5
                            :total 10
                            :user 5}
                   :step_count 1
                   :permission "own"
                   :tools []
                   :references []
                   :categories []
                   :suggested_categories []
                   :limitChecks {:canRun true
                                 :results []}})))

    (testing "valid app details - with complex tools"
      (is (valid? apps/AppDetails
                  {:id "app-id-abc123"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool for sequence comparison"
                   :app_type "DE"
                   :can_favor true
                   :can_rate true
                   :can_run true
                   :deleted false
                   :disabled false
                   :integrator_email "jsmith@example.org"
                   :integrator_name "John Smith"
                   :is_public true
                   :pipeline_eligibility {:is_valid true
                                          :reason ""}
                   :rating {:average 4.5
                            :total 10
                            :user 5}
                   :step_count 2
                   :permission "own"
                   :tools [{:id "tool-1"
                            :name "samtools"
                            :description "Tools for manipulating SAM/BAM files"
                            :attribution "Heng Li"
                            :version "1.15.1"
                            :type "executable"}
                           {:id "tool-2"
                            :name "blast"
                            :description "BLAST+ suite"
                            :version "2.14.0"
                            :type "docker"
                            :container {:image {:name "ncbi/blast"
                                                :tag "2.14.0"}}}]
                   :references ["https://doi.org/10.1093/nar/gkv416"]
                   :categories [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                 :name "Bioinformatics"}
                                {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                                 :name "Sequence Analysis"}]
                   :suggested_categories [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                                           :name "Genomics"}]})))

    (testing "valid app details - with all optional fields populated"
      (is (valid? apps/AppDetails
                  {:id "app-id-abc123"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool for sequence comparison"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                   :system_id "de"
                   :app_type "DE"
                   :overall_job_type "executable"
                   :can_favor true
                   :can_rate true
                   :can_run true
                   :deleted false
                   :disabled false
                   :version "2.1.0"
                   :version_id "latest-version-id-123"
                   :integrator_email "jsmith@example.org"
                   :integrator_name "John Smith"
                   :is_favorite true
                   :is_public true
                   :beta false
                   :isBlessed true
                   :pipeline_eligibility {:is_valid true
                                          :reason "This app contains tasks that are not public"}
                   :rating {:average 4.5
                            :total 10
                            :user 5}
                   :step_count 1
                   :wiki_url "https://wiki.example.org/apps/blast"
                   :permission "own"
                   :tools [{:id "tool-abc123"
                            :name "samtools"
                            :version "1.15.1"
                            :type "executable"}]
                   :references ["https://doi.org/10.1093/nar/gkv416" "PMID: 25916842"]
                   :job_stats {:job_count_completed 42
                               :job_last_completed #inst "2025-10-25T16:30:00.000-00:00"}
                   :categories [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                 :name "Bioinformatics"}]
                   :suggested_categories [{:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                                           :name "Sequence Analysis"}]
                   :hierarchies [{:iri "http://purl.obolibrary.org/obo/GO_0008150"
                                  :label "biological_process"
                                  :description
                                  "A biological process is the execution of a genetically-encoded biological module"}]
                   :versions [{:version "1.0.0"
                               :version_id #uuid "111e1111-a11a-11a1-a111-111111111111"}
                              {:version "2.0.0"
                               :version_id #uuid "222e2222-b22b-22b2-b222-222222222222"}]
                   :limitChecks {:canRun true
                                 :results []}})))

    (testing "invalid app details - missing required field: id"
      (is (not (valid? apps/AppDetails
                       {:name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []}))))

    (testing "invalid app details - missing required field: name"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []}))))

    (testing "invalid app details - missing required field: tools"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :references []
                        :categories []
                        :suggested_categories []}))))

    (testing "invalid app details - missing required field: references"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :categories []
                        :suggested_categories []}))))

    (testing "invalid app details - missing required field: categories"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :suggested_categories []}))))

    (testing "invalid app details - missing required field: suggested_categories"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []}))))

    (testing "invalid app details - wrong type for id field"
      (is (not (valid? apps/AppDetails
                       {:id 12345
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []}))))

    (testing "invalid app details - wrong type for tools field"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools "not-a-vector"
                        :references []
                        :categories []
                        :suggested_categories []}))))

    (testing "invalid app details - invalid tool in tools vector"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools [{:id "tool-abc123"
                                 :name "samtools"
                                 :version "1.15.1"}]
                        :references []
                        :categories []
                        :suggested_categories []}))))

    (testing "invalid app details - wrong type for references field"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references "not-a-vector"
                        :categories []
                        :suggested_categories []}))))

    (testing "invalid app details - wrong type for categories field"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories "not-a-vector"
                        :suggested_categories []}))))

    (testing "invalid app details - invalid category in categories vector"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories [{:id "not-a-uuid"
                                      :name "Bioinformatics"}]
                        :suggested_categories []}))))

    (testing "invalid app details - wrong type for job_stats field"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []
                        :job_stats "not-a-map"}))))

    (testing "invalid app details - invalid job_stats structure"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []
                        :job_stats {:job_count_completed "not-an-int"}}))))

    (testing "invalid app details - wrong type for hierarchies field"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []
                        :hierarchies "not-a-vector"}))))

    (testing "invalid app details - invalid hierarchy in hierarchies vector"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []
                        :hierarchies [{:iri "http://purl.obolibrary.org/obo/GO_0008150"}]}))))

    (testing "invalid app details - wrong type for versions field"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []
                        :versions "not-a-vector"}))))

    (testing "invalid app details - invalid version in versions vector"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []
                        :versions [{:version "1.0.0"}]}))))

    (testing "invalid app details - wrong type for rating field"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating "not-a-map"
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []}))))

    (testing "invalid app details - wrong type for pipeline_eligibility field"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility "not-a-map"
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []}))))

    (testing "invalid app details - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []
                        :extra-field "not-allowed"}))))

    (testing "invalid app details - wrong type for boolean fields"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor "not-a-boolean"
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count 1
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []}))))

    (testing "invalid app details - wrong type for step_count field"
      (is (not (valid? apps/AppDetails
                       {:id "app-id-abc123"
                        :name "BLAST"
                        :description "Basic Local Alignment Search Tool for sequence comparison"
                        :app_type "DE"
                        :can_favor true
                        :can_rate true
                        :can_run true
                        :deleted false
                        :disabled false
                        :integrator_email "jsmith@example.org"
                        :integrator_name "John Smith"
                        :is_public true
                        :pipeline_eligibility {:is_valid true
                                               :reason ""}
                        :rating {:average 4.5
                                 :total 10
                                 :user 5}
                        :step_count "not-an-int"
                        :permission "own"
                        :tools []
                        :references []
                        :categories []
                        :suggested_categories []}))))))
