(ns common-swagger-api.malli.apps.admin.apps
  (:require
   [clojure.set :as sets]
   [common-swagger-api.malli :refer [CommonResponses ErrorResponseNotFound SortFieldDocs]]
   [common-swagger-api.malli.apps :as apps]
   [malli.util :as mu]))

(def AdminAppPatchSummary "Update App Details and Labels")
(def AdminAppVersionPatchSummary "Update App Version Details and Labels")

(def BlessAppSummary "Mark App Certified")
(def BlessAppDescription
  "Mark an app as having been reviewed and certified by Discovery Environment administrators.")

(def RemoveAppBlessingSummary "Mark App Not Certified")
(def RemoveAppBlessingDescription
  "Mark an app as not having been reviewed and certified by Discovery Environment administrators.")

(def AppDeleteDocs
  (str "An app can be marked as deleted in the DE without being completely removed from the database using this "
       "service. This endpoint is the same as the non-admin endpoint, except an error is not returned if the user "
       "does not own the App."))

(def AppDetailsDocs "This service allows administrative users to view detailed information about private apps.")
(def AppDocumentationAddDocs
  "This service is used by DE administrators to add documentation for an app's latest version.")
(def AppDocumentationUpdateDocs
  "This service is used by DE administrators to update documentation for an app's latest version.")

(def AppIntegrationDataUpdateSummary "Update the Integration Data Record for an App's latest Version")
(def AppIntegrationDataUpdateDocs
  "This service allows administrators to change the integration data record associated with an app's latest version.")

(def AppListingDocs
  (str "This service allows admins to list all public apps, including apps listed under the `Trash` category: "
       "deleted public apps and private apps that are 'orphaned' (not categorized in any user's workspace). "
       "If the `search` parameter is included, then the results are filtered by the App name, description, "
       "integrator's name, tool name, or category name the app is under."))

(def AppShredderSummary "Permanently Deleting Apps")
(def AppShredderDocs
  (str "This service physically removes an App from the database, which allows administrators to completely remove "
       "Apps that are causing problems."))

(def AppPublicationRequestsSummary "List App Publication Requests")
(def AppPublicationRequestsDocs
  "This service lists requests for app publication that require administrator intervention.")

(def AppVersionDetailsSummary "Get App Version Details")
(def AppVersionDetailsDocs
  "This service is used by admins to obtain high-level details about an app version.")

(def AppVersionDocumentationAddSummary "Add App Version Documentation")
(def AppVersionDocumentationAddDocs
  "This service is used by DE administrators to add documentation for an app version.")

(def AppVersionDocumentationUpdateSummary "Update App Version Documentation")
(def AppVersionDocumentationUpdateDocs
  "This service is used by DE administrators to update documentation for an app version.")

(def AppVersionIntegrationDataUpdateSummary "Update App Version Integration Data")
(def AppVersionIntegrationDataUpdateDocs
  "This service allows administrators to change the integration data record associated with an app version.")

(def AdminAppListingJobStats
  (mu/merge
   apps/AppListingJobStats
   [:map {:closed true}
    [:job_count
     {:description         "The number of times this app has run"
      :json-schema/example 42}
     :int]

    [:job_count_failed
     {:description         "The number of times this app has run to `Failed` status"
      :json-schema/example 3}
     :int]

    [:last_used
     {:optional            true
      :description         "The start date this app was last run"
      :json-schema/example #inst "2025-10-25T16:30:00.000-00:00"}
     inst?]]))

(def AdminAppListingDetail
  (mu/merge
   apps/AppListingDetail
   [:map {:closed true}
    [:job_stats
     {:optional    true
      :description apps/AppListingJobStatsDocs}
     AdminAppListingJobStats]]))

;; The apps entry is dissociated first because `mu/merge` merges entry properties instead of replacing them.
(def AdminAppListing
  (-> apps/AppListing
      (mu/dissoc :apps)
      (mu/merge
       [:map {:closed true}
        [:apps
         {:description "A listing of App details"}
         [:vector AdminAppListingDetail]]])))

(def AdminAppListingJobStatsKeys
  (set (mu/keys AdminAppListingJobStats)))

(def AdminAppSearchValidSortFields
  (sets/union apps/AppSearchValidSortFields
              AdminAppListingJobStatsKeys))

(def AppSubsetDocs "The subset of apps to search.")
(def AppSubsets [:public :private :all])

(def AppPublicationRequestSearchParams
  [:map {:closed true}
   [:app_id
    {:optional            true
     :description         "The ID of the app to list publication requests for"
     :json-schema/example #uuid "987e6543-e21b-32c1-b456-426614174000"}
    :uuid]

   [:requestor
    {:optional            true
     :description         "The username of the person who requested the app publication"
     :json-schema/example "jsmith"}
    :string]

   [:include_completed
    {:optional            true
     :description         "If set to true, completed publication requests will be included in the listing"
     :json-schema/example false}
    :boolean]])

;; The sort-field entry is dissociated first because `mu/merge` merges entry properties instead of replacing them.
(def AdminAppSearchParams
  (-> apps/AppSearchParams
      (mu/dissoc :sort-field)
      (mu/merge
       [:map {:closed true}
        [:sort-field
         {:optional            true
          :description         SortFieldDocs
          :json-schema/example :name}
         (into [:enum] AdminAppSearchValidSortFields)]

        [:app-subset
         {:optional            true
          :description         AppSubsetDocs
          :json-schema/default :public
          :json-schema/example :public}
         (into [:enum] AppSubsets)]])))

(def AppExtraInfo
  [:map {:closed true}
   [:htcondor
    [:map {:closed true}
     [:extra_requirements
      {:description         "A set of additional requirements to add to the HTCondor submit file"
       :json-schema/example "request_gpus = 1"}
      :string]]]])

;; The job_stats entry is dissociated first because `mu/merge` merges nested map schemas instead of replacing them.
(def AdminAppDetails
  (-> apps/AppDetails
      (mu/dissoc :job_stats)
      (mu/merge
       [:map {:closed true}
        [:job_stats
         {:optional    true
          :description apps/AppListingJobStatsDocs}
         AdminAppListingJobStats]

        [:documentation
         {:optional    true
          :description "App documentation as returned by the specific app documentation endpoints."}
         apps/AppDocumentation]

        [:extra
         {:optional true}
         AppExtraInfo]])))

(def AdminAppPatchRequest
  (-> apps/AppBase
      (mu/optional-keys [:id
                         :name
                         :description])
      (mu/merge
       [:map {:closed true}
        [:extra
         {:optional true}
         AppExtraInfo]

        [:wiki_url
         {:optional true}
         apps/AppDocUrlParam]

        [:references
         {:optional true}
         apps/AppReferencesParam]

        [:deleted
         {:optional true}
         apps/AppDeletedParam]

        [:disabled
         {:optional true}
         apps/AppDisabledParam]

        [:groups
         {:optional    true
          :description apps/GroupListDocs}
         [:vector apps/AppGroup]]])
      (mu/update-properties assoc :description "The App to update.")))

(def ToolAdminAppListingResponses
  (merge CommonResponses
         {200 {:body        AdminAppListing
               :description "The listing of Apps using the given Tool."}
          404 {:body        ErrorResponseNotFound
               :description "The `tool-id` does not exist."}}))

(def AppPublicationRequest
  [:map {:closed true}
   [:id
    {:description         "The app publication request identifier"
     :json-schema/example #uuid "123e4567-e89b-12d3-a456-426614174000"}
    :uuid]

   [:app
    {:description "Details about the app that the user wants to publish"}
    apps/AppDetails]

   [:requestor
    {:description         "The username of the person who requested the app publication"
     :json-schema/example "jsmith"}
    :string]])

(def AppPublicationRequestListing
  [:map {:closed true}
   [:publication_requests
    {:description "The list of app publication requests"}
    [:vector AppPublicationRequest]]])
