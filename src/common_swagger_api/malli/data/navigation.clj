(ns common-swagger-api.malli.data.navigation
  (:require
   [clojure-commons.error-codes :as ce]
   [common-swagger-api.malli :refer [add-enum-values CommonResponses ErrorResponseUnchecked]]
   [common-swagger-api.malli.data :as data-schema]
   [common-swagger-api.malli.stats :as stats-schema]
   [malli.core :as m]
   [malli.util :as mu]))

(def NavigationRootSummary "Root Listing")
(def NavigationRootDocs
  (str "This endpoint provides a shortcut for the client to list the top-level directories "
       "(e.g. the user's home directory, trash, and shared directories)."))

(def NavigationSummary "Directory List (Non-Recursive)")
(def NavigationDocs
  "Only lists subdirectories of the directory path passed into it.")

(def UserBasePaths
  [:map {:closed true}
   [:user_home_path
    {:description         "The absolute path to the user's home folder"
     :json-schema/example "/iplant/home/janedoe"}
    :string]

   [:user_trash_path
    {:description         "The absolute path to the user's trash folder"
     :json-schema/example "/iplant/trash/home/de-irods/janedoe"}
    :string]

   [:base_trash_path
    {:description         "The absolute path to the base trash folder"
     :json-schema/example "/iplant/trash/home/de-irods"}
    :string]])

(def RootListing
  (mu/dissoc stats-schema/DataStatInfo :type))

(def NavigationRootResponse
  [:map {:closed true}
   [:roots [:vector RootListing]]

   [:base-paths UserBasePaths]])

(def FolderListing
  (m/schema
   [:schema
    {:registry
     {::FolderListing
      (conj (m/form RootListing)
            [:folders
             {:optional    true
              :description "Subdirectories of this directory"}
             [:vector [:ref ::FolderListing]]])}}
    ::FolderListing]))

(def NavigationResponse
  [:map {:closed true}
   [:folder FolderListing]])

(def NavigationRootErrorCodeResponses
  (conj data-schema/CommonErrorCodeResponses
        ce/ERR_DOES_NOT_EXIST
        ce/ERR_NOT_READABLE
        ce/ERR_NOT_A_USER))

(def NavigationRootErrorResponses
  (mu/update
   ErrorResponseUnchecked
   :error_code
   add-enum-values
   ce/ERR_DOES_NOT_EXIST
   ce/ERR_NOT_READABLE
   ce/ERR_NOT_A_USER))

(def NavigationErrorResponses
  (mu/update NavigationRootErrorResponses :error_code add-enum-values ce/ERR_NOT_A_FOLDER))

(def NavigationRootResponses
  (merge CommonResponses
         {200 {:body        NavigationRootResponse
               :description "The Root Listing."}
          500 {:body        NavigationRootErrorResponses
               :description data-schema/CommonErrorCodeDocs}}))

(def NavigationResponses
  (merge CommonResponses
         {200 {:body        NavigationResponse
               :description "The Folder Listing."}
          500 {:body        NavigationErrorResponses
               :description data-schema/CommonErrorCodeDocs}}))
