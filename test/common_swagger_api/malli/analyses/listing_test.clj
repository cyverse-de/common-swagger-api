(ns common-swagger-api.malli.analyses.listing-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.analyses.listing :as listing]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def analysis-id #uuid "eb97403f-de50-4f65-939e-b276f7faec16")

(def batch-status {:total 42 :completed 39 :running 2 :submitted 1})

(def base-analysis
  {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
   :system_id "de"
   :id        analysis-id
   :notify    true
   :status    "Running"
   :username  "janedoe"})

(def analysis (assoc base-analysis :app_disabled false :can_share true))

(def analysis-step {:step_number 1})

(def status-update
  {:status    "Running"
   :message   "Downloading input files."
   :timestamp "1763405834987"})

(def step-history (assoc analysis-step :updates [status-update]))

(def step-list
  {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
   :steps       [analysis-step]
   :timestamp   "1763405834987"
   :total       117})

(deftest AnalysisListingParams
  (valid listing/AnalysisListingParams
         {}
         {:include-hidden true :include-deleted false}
         {:limit 50 :offset 0 :sort-field "startdate" :sort-dir "DESC"}
         {:filter "[{\"field\":\"id\",\"value\":\"C09F5907-B2A2-4429-A11E-5B96F421C3C1\"}]"})
  (invalid listing/AnalysisListingParams
           {:include-hidden "yes"}
           {:sort-dir "sideways"}
           {:filter [{:field "id"}]}
           {:extra 1}))

(deftest ResultsTotalParam
  (valid listing/ResultsTotalParam 117 0)
  (invalid listing/ResultsTotalParam "117" nil))

(deftest Timestamp
  (valid listing/Timestamp "1763405834987")
  (invalid listing/Timestamp 1763405834987 nil))

(deftest ExternalId
  (valid listing/ExternalId "7f13edd3-d9c8-40c8-b9a2-360cc9977fe9-007")
  (invalid listing/ExternalId " " nil))

(deftest BatchStatus
  (valid listing/BatchStatus batch-status (assoc batch-status :running 0))
  (invalid listing/BatchStatus
           {}
           (dissoc batch-status :submitted)
           (assoc batch-status :total "42")
           (assoc batch-status :extra 1)))

(deftest BaseAnalysis
  (valid listing/BaseAnalysis
         base-analysis
         (assoc base-analysis :app_description "BLAST Nucleotide Alignment" :app_name "BLAST" :batch false)
         (assoc base-analysis :startdate "1763405834987" :enddate "1763405934987" :batch_status batch-status)
         (assoc base-analysis
                :app_version_id   #uuid "46f2634e-6297-4ff5-9e63-dfe1c4a2ee09"
                :parent_id        #uuid "59dfbf9b-b5ab-4197-a905-e8793e360444"
                :interactive_urls ["https://vice.example.org/a123456"]))
  (invalid listing/BaseAnalysis
           {}
           (dissoc base-analysis :username)
           (assoc base-analysis :id (str analysis-id))
           (assoc base-analysis :startdate 1763405834987)
           (assoc base-analysis :batch_status (dissoc batch-status :total))
           (assoc base-analysis :extra 1)))

(deftest Analysis
  (valid listing/Analysis analysis (assoc analysis :name "BLAST Zea Mays B73" :description "An alignment."))
  (invalid listing/Analysis
           base-analysis
           (dissoc analysis :can_share)
           (assoc analysis :app_disabled "false")
           (assoc analysis :extra 1)))

(deftest AnalysisList
  (valid listing/AnalysisList
         {:analyses [] :timestamp "1763405834987" :total 0 :status-count []}
         {:analyses     [analysis]
          :timestamp    "1763405834987"
          :total        1
          :status-count [{:count 27 :status "Completed"}]})
  (invalid listing/AnalysisList
           {}
           {:analyses [] :timestamp "1763405834987" :total 0}
           {:analyses [base-analysis] :timestamp "1763405834987" :total 1 :status-count []}
           {:analyses [] :timestamp "1763405834987" :total "0" :status-count []}
           {:analyses [] :timestamp "1763405834987" :total 0 :status-count [] :extra 1}))

(deftest AnalysisUpdate
  (valid listing/AnalysisUpdate {} {:name "BLAST Zea Mays B73"} {:description "An alignment." :name "BLAST"})
  (invalid listing/AnalysisUpdate {:name 1} {:description nil} {:id analysis-id} {:name "BLAST" :extra 1}))

(deftest AnalysisUpdateResponse
  (valid listing/AnalysisUpdateResponse {:id analysis-id} {:id analysis-id :name "BLAST" :description "An alignment."})
  (invalid listing/AnalysisUpdateResponse
           {}
           {:id (str analysis-id)}
           {:id analysis-id :name 1}
           {:id analysis-id :extra 1}))

(deftest AppStepNumber
  (valid listing/AppStepNumber 0 3)
  (invalid listing/AppStepNumber "0" nil))

(deftest AnalysisStep
  (valid listing/AnalysisStep
         analysis-step
         (assoc analysis-step :external_id "d81e33c4-b915-45be-8568-5c731f37cd5d" :status "Submitted")
         (assoc analysis-step :startdate "1763405834987" :enddate "1763405934987")
         (assoc analysis-step :app_step_number 0 :step_type "Interactive"))
  (invalid listing/AnalysisStep
           {}
           (assoc analysis-step :step_number "1")
           (assoc analysis-step :app_step_number "0")
           (assoc analysis-step :extra 1)))

(deftest AnalysisStepList
  (valid listing/AnalysisStepList step-list (assoc step-list :steps []))
  (invalid listing/AnalysisStepList
           {}
           (dissoc step-list :total)
           (assoc step-list :steps [{}])
           (assoc step-list :analysis_id (str (:analysis_id step-list)))
           (assoc step-list :extra 1)))

(deftest AnalysisStatusUpdate
  (valid listing/AnalysisStatusUpdate status-update (assoc status-update :message ""))
  (invalid listing/AnalysisStatusUpdate
           {}
           (dissoc status-update :timestamp)
           (assoc status-update :status " ")
           (assoc status-update :extra 1)))

(deftest AnalysisStepHistory
  (valid listing/AnalysisStepHistory step-history (assoc step-history :updates []))
  (invalid listing/AnalysisStepHistory
           analysis-step
           (assoc step-history :updates [{}])
           (assoc step-history :updates "none")
           (assoc step-history :extra 1)))

(deftest AnalysisHistory
  (valid listing/AnalysisHistory
         (assoc step-list :steps [step-history])
         (assoc step-list :steps []))
  (invalid listing/AnalysisHistory
           step-list
           (dissoc (assoc step-list :steps [step-history]) :timestamp)
           (assoc step-list :steps [{}])
           (assoc step-list :steps [step-history] :extra 1)))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.analyses.listing))
