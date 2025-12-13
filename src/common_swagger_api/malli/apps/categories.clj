(ns common-swagger-api.malli.apps.categories
  (:require
   [common-swagger-api.malli.apps
    :refer [AppCategoryIdPathParam AppListingDetail AppListingPagingParams
            SystemId]]
   [common-swagger-api.malli.ontologies
    :refer [OntologyHierarchyFilterParams]]
   [malli.core :as m]
   [malli.util :as mu]))

(def AppCategoryListingSummary "List App Categories")
(def AppCategoryListingDocs
  "This service is used by the DE to obtain the list of app categories that are visible to the user.")

(def AppCategoryAppListingSummary "List Apps in a Category")
(def AppCategoryAppListingDocs
  "This service lists all of the apps within an app category or any of its descendents.
   The DE uses this service to obtain the list of apps when a user clicks on a category in the _Apps_ window.
   This endpoint accepts optional URL query parameters to limit and sort Apps, which will allow pagination of results.")

(def FeaturedAppListingSummary "List Featured Apps")
(def FeaturedAppListingDocs
  "This service lists all of the apps within the Featured Apps category.
   This endpoint accepts optional URL query parameters to limit and sort Apps, which will allow pagination of results.")

(def AppCategoryHierarchyListingSummary "List App Category Hierarchy")
(def AppCategoryHierarchyListingDocs
  "Gets the list of app categories that are visible to the user for the active ontology version,
   rooted at the given `root-iri`.")

(def AppCommunityAppListingSummary "List Apps in a Community")
(def AppCommunityAppListingDocs
  "Lists all of the apps under an App Community that are visible to the user.")

(def AppHierarchiesListingSummary "List App Hierarchies")
(def AppHierarchiesListingDocs
  "Lists all hierarchies saved for the active ontology version.")

(def AppHierarchyAppListingDocs "Lists all of the apps under an app category hierarchy that are visible to the user.")

(def AppHierarchyUnclassifiedListingSummary "List Unclassified Apps")
(def AppHierarchyUnclassifiedListingDocs
  "Lists all of the apps that are visible to the user that are not under the given app category
   or any of its subcategories.")

(def AppCategoryNameParam
  [:string
   {:description         "The App Category's name"
    :json-schema/example "Genome Sequencing"}])

(def AppCommunityGroupNameParam
  [:string
   {:description         "The full group name of the App Community"
    :json-schema/example "Apps Used by Example Lab"}])

(def CategoryListingParams
  (mu/closed-schema
   [:map
    [:public
     {:optional            true
      :description         (str "If set to `true`, then only app categories that are in a workspace that is marked "
                                "as public in the database are returned. If set to `false`, then only app categories "
                                "that are in the user's workspace are returned. If not set, then both public and the "
                                "user's private categories are returned.")
      :json-schema/example true}
     :boolean]]))

(def AppCategoryId
  (mu/closed-schema
   [:map
    [:system_id SystemId]

    [:id AppCategoryIdPathParam]]))

(def AppCategoryBase
  (mu/closed-schema
   (mu/merge
    AppCategoryId
    [:map
     [:name AppCategoryNameParam]])))

(def AppCategory
  (m/schema
   [:schema {:registry {::AppCategory
                        [:map {:closed true}
                         [:system_id
                          {:description         "The ID of the app execution system"
                           :json-schema/example "de"
                           :min                 1}
                          :string]

                         [:id
                          {:description         "The App Category's UUID"
                           :json-schema/example #uuid "123e4567-e89b-12d3-a456-426614174000"}
                          :uuid]

                         [:name
                          {:description         "The App Category's name"
                           :json-schema/example "Genome Sequencing"}
                          :string]

                         [:total
                          {:description         "The number of Apps under this Category and all of its children"
                           :json-schema/example 42}
                          :int]

                         [:is_public
                          {:description         (str "Whether this App Category is viewable to all users or private "
                                                     "to only the user that owns its Workspace")
                           :json-schema/example true}
                          :boolean]

                         [:categories
                          {:optional    true
                           :description "A listing of child App Categories under this App Category"}
                          [:vector [:ref ::AppCategory]]]]}}
    [:ref ::AppCategory]]))

(def AppCategoryListing
  (mu/closed-schema
   [:map
    [:categories
     {:description "A listing of App Categories visisble to the requesting user"}
     [:vector AppCategory]]]))

(def AppCategoryAppListing
  (as-> AppCategory s
    (mu/dissoc s :categories)
    (mu/merge
     s
     [:map
      [:apps
       {:description "A listing of Apps under this Category"}
       [:vector AppListingDetail]]])))

(def OntologyAppListingPagingParams
  (mu/closed-schema (mu/merge AppListingPagingParams OntologyHierarchyFilterParams)))
