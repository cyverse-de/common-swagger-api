(ns common-swagger-api.malli.apps.pipeline-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.apps.pipeline :as pipeline]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def mapping
  {:source_step 0
   :target_step 1
   :map         {:step_1_input "step_2_output"}})

(def step
  {:name        "Sequence Alignment"
   :description "Aligns the input sequences"
   :system_id   "de"
   :app_type    "DE"})

(def file-parameter
  {:id          "param_123"
   :name        "output_alignment"
   :description "The output alignment file from the analysis"
   :label       "Output Alignment"
   :format      "bam"
   :required    true})

(def task
  {:system_id   "de"
   :id          "task_456"
   :name        "BLAST Analysis"
   :description "Performs BLAST sequence alignment analysis"
   :inputs      [file-parameter]
   :outputs     [file-parameter]})

(def pipeline-value
  {:id          #uuid "987e6543-e21b-32c1-b456-426614174000"
   :name        "Alignment Pipeline"
   :description "Aligns sequences and summarizes the results"
   :tasks       [task]
   :steps       [step]
   :mappings    [mapping]})

(deftest PipelineMappingMap
  (valid pipeline/PipelineMappingMap {} (:map mapping))
  (invalid pipeline/PipelineMappingMap {"step_1_input" "step_2_output"} {:step_1_input 1}))

(deftest PipelineMapping
  (valid pipeline/PipelineMapping mapping (assoc mapping :map {}))
  (invalid pipeline/PipelineMapping
           {}
           (dissoc mapping :map)
           (assoc mapping :source_step "0")
           (assoc mapping :map {:step_1_input 1})
           (assoc mapping :extra 1)))

(deftest PipelineStep
  (valid pipeline/PipelineStep
         step
         (assoc step :task_id "123e4567-e89b-12d3-a456-426614174000" :external_app_id "external-app-id-123"))
  (invalid pipeline/PipelineStep
           {}
           (dissoc step :app_type)
           (assoc step :name 1)
           (assoc step :task_id 1)
           (assoc step :extra 1)))

(deftest Pipeline
  (valid pipeline/Pipeline
         pipeline-value
         (assoc pipeline-value :versions [{:version "1.0.0" :version_id (:id pipeline-value)}]))
  (invalid pipeline/Pipeline
           {}
           (dissoc pipeline-value :tasks)
           (assoc pipeline-value :id "not-a-uuid")
           (assoc pipeline-value :steps [{}])
           (assoc pipeline-value :extra 1)))

(deftest PipelineUpdateRequest
  (valid pipeline/PipelineUpdateRequest pipeline-value (dissoc pipeline-value :tasks))
  (invalid pipeline/PipelineUpdateRequest
           {}
           (dissoc pipeline-value :id)
           (assoc pipeline-value :versions [])
           (assoc pipeline-value :mappings [{}])
           (assoc pipeline-value :extra 1)))

(deftest PipelineCreateRequest
  (valid pipeline/PipelineCreateRequest pipeline-value (dissoc pipeline-value :id :tasks))
  (invalid pipeline/PipelineCreateRequest
           {}
           (dissoc pipeline-value :steps)
           (assoc pipeline-value :name 1)
           (assoc pipeline-value :versions [])
           (assoc pipeline-value :extra 1)))

(deftest PipelineVersionRequest
  (valid pipeline/PipelineVersionRequest
         (assoc pipeline-value :version "1.0.0")
         (-> pipeline-value (dissoc :id :tasks) (assoc :version "1.0.0")))
  (invalid pipeline/PipelineVersionRequest
           {}
           pipeline-value
           (assoc pipeline-value :version "1.0.0" :version_id (:id pipeline-value))
           (assoc pipeline-value :version 1)
           (assoc pipeline-value :version "1.0.0" :extra 1)))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps.pipeline))
