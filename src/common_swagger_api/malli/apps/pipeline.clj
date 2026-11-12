(ns common-swagger-api.malli.apps.pipeline
  (:require
   [common-swagger-api.malli.apps :refer [AppTaskListing AppVersionListing]]
   [malli.util :as mu]))

(def PipelineCopySummary "Make a Copy of a Pipeline Available for Editing")
(def PipelineCopyDocs
  (str "This service can be used to make a copy of a Pipeline in the user's workspace. This endpoint will copy the "
       "App details, steps, and mappings, but will not copy tasks used in the Pipeline steps."))

(def PipelineVersionCopySummary "Copy a Pipeline Version for Editing")
(def PipelineVersionCopyDocs
  (str "This service can be used to make a copy of a Pipeline's Version in the user's workspace. This endpoint will "
       "copy the App details, steps, and mappings, but will not copy tasks used in the Pipeline steps."))

(def PipelineCreateSummary "Create a Pipeline")
(def PipelineCreateDocs "This service adds a new Pipeline.")

(def PipelineVersionCreateSummary "Add a new Pipeline Version")
(def PipelineVersionCreateDocs "This service adds a new Version to an existing Pipeline.")

(def PipelineEditingViewSummary "Make a Pipeline Available for Editing")
(def PipelineEditingViewDocs
  (str "The DE uses this service to obtain a JSON representation of a Pipeline for editing. The requesting user must "
       "have write permissions for the Pipeline, and it must not already be public."))

(def PipelineVersionEditingViewSummary "Make a Pipeline Version Available for Editing")
(def PipelineVersionEditingViewDocs
  (str "The DE uses this service to obtain a JSON representation of a Pipeline Version for editing. The requesting "
       "user must have write permissions for the Pipeline, and it must not already be public."))

(def PipelineUpdateSummary "Update a Pipeline")
(def PipelineUpdateDocs
  (str "This service updates an existing Pipeline in the database, as long as the Pipeline has not been submitted "
       "for public use."))

(def PipelineVersionUpdateSummary "Update a Pipeline Version")
(def PipelineVersionUpdateDocs
  (str "This service updates an existing Pipeline Version in the database, as long as the Pipeline has not been "
       "submitted for public use."))

(def PipelineMappingMap
  [:map-of
   [:keyword
    {:description         "The input ID"
     :json-schema/example :step_1_input}]
   [:string
    {:description         "The output ID"
     :json-schema/example "step_2_output"}]])

(def PipelineMapping
  [:map {:closed true}
   [:source_step
    {:description         "The step index of the Source Step"
     :json-schema/example 0}
    :int]

   [:target_step
    {:description         "The step index of the Target Step"
     :json-schema/example 1}
    :int]

   [:map
    {:description "The {'input-id': 'output-id'} mappings"}
    PipelineMappingMap]])

(def PipelineStep
  [:map {:closed true}
   [:name
    {:description         "The Step's name"
     :json-schema/example "Sequence Alignment"}
    :string]

   [:description
    {:description         "The Step's description"
     :json-schema/example "Aligns the input sequences"}
    :string]

   [:system_id
    {:description         "The ID of the execution system."
     :json-schema/example "de"}
    :string]

   [:task_id
    {:optional            true
     :description         (str "A String referring to either an internal task or an external app. If the string "
                               "refers to an internal task then this must be a string representation of a UUID. "
                               "Otherwise, it should be the ID of the external app.")
     :json-schema/example "123e4567-e89b-12d3-a456-426614174000"}
    :string]

   [:external_app_id
    {:optional            true
     :description         (str "A string referring to an external app that is used to perform the step. This field "
                               "is required any time the task ID isn't provided.")
     :json-schema/example "external-app-id-123"}
    :string]

   [:app_type
    {:description         "The Step's App type"
     :json-schema/example "DE"}
    :string]])

(def Pipeline
  (-> AppTaskListing
      (mu/merge AppVersionListing)
      (mu/dissoc :id)
      (mu/merge
       [:map
        [:id
         {:description         "The pipeline's ID"
          :json-schema/example #uuid "987e6543-e21b-32c1-b456-426614174000"}
         :uuid]

        [:steps
         {:description "The Pipeline's steps"}
         [:vector PipelineStep]]

        [:mappings
         {:description "The Pipeline's input/output mappings"}
         [:vector PipelineMapping]]])))

(def PipelineUpdateRequest
  (-> Pipeline
      (mu/dissoc :versions)
      (mu/optional-keys [:tasks])
      (mu/update-properties assoc :description "The Pipeline to update.")))

(def PipelineCreateRequest
  (-> PipelineUpdateRequest
      (mu/optional-keys [:id])
      (mu/update-properties assoc :description "The Pipeline to create.")))

(def PipelineVersionRequest
  (-> PipelineCreateRequest
      (mu/dissoc :version_id)
      (mu/required-keys [:version])
      (mu/update-properties assoc :description "The Pipeline Version to add.")))
