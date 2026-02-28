(ns common-swagger-api.malli.quicklaunches
  (:require
   [common-swagger-api.malli :refer [NonBlankString]]
   [common-swagger-api.malli.analyses :refer [AnalysisSubmission]]
   [malli.util :as mu]))

(def QuickLaunch
  (mu/closed-schema
   [:map
    [:id
     {:description         "The UUID for the quick launch"
      :json-schema/example #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"}
     :uuid]

    [:name
     {:description         "The name of the quick launch"
      :json-schema/example "BLAST"}
     NonBlankString]

    [:description
     {:optional            true
      :description         "The description of the quick launch"
      :json-schema/example "BLAST Nucleotide Sequence Alignment with default settings."}
     :string]

    [:creator
     {:description         "The username for the creator of the quick launch"
      :json-schema/example "janedoe"}
     NonBlankString]

    [:app_id
     {:description         "The UUID for the app the quick launch is associated with"
      :json-schema/example #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"}
     :uuid]

    [:app_version_id
     {:description         "The UUID for the app version the quick launch is associated with"
      :json-schema/example #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"}
     :uuid]

    [:is_public
     {:optional            true
      :description         "Whether the quick launch is publicly available. Defaults to false"
      :json-schema/example true}
     :boolean]

    [:submission
     {:description "The default parameter values to use when submitting a job using this quick launch"}
     AnalysisSubmission]]))

(def NewQuickLaunch
  (as-> QuickLaunch schema
    (mu/dissoc schema :id)
    (mu/optional-keys schema [:app_version_id])))

(def UpdateQuickLaunch
  (as-> QuickLaunch schema
    (mu/dissoc schema :id)
    (mu/optional-keys schema)))

(def QuickLaunchFavorite
  (mu/closed-schema
   [:map
    [:id
     {:description         "The UUID for the quick launch favorite entry"
      :json-schema/example #uuid "234f5f17-cbcf-4e23-87fa-1bceaeba09ee"}
     :uuid]

    [:quick_launch_id
     {:description         "The UUID of the quick launch that was favorited"
      :json-schema/example #uuid "7bd11b7f-91f2-4e7c-9e48-9a3ba802f354"}
     :uuid]

    [:user
     {:description         "The username of the user that favorited the quick launch"
      :json-schema/example "janedoe"}
     NonBlankString]]))

(def NewQuickLaunchFavorite
  (reduce mu/dissoc QuickLaunchFavorite [:id :user]))

(def QuickLaunchUserDefault
  (mu/closed-schema
   [:map
    [:id
     {:description         "The UUID for the quick launch user default"
      :json-schema/example #uuid "36fe7915-5984-4848-b5bc-965899b0e061"}
     :uuid]

    [:user
     {:description         "The username of the user that is using the quick launch as the default for the app"
      :json-schema/example "janedoe"}
     NonBlankString]

    [:app_id
     {:description         "The UUID of the app for which the user default is being set"
      :json-schema/example #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"}
     :uuid]

    [:quick_launch_id
     {:description         "The UUID of the quick launch being used as the default for the app"
      :json-schema/example #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}
     :uuid]]))

(def NewQuickLaunchUserDefault
  (reduce mu/dissoc QuickLaunchUserDefault [:id :user]))

(def UpdateQuickLaunchUserDefault
  (as-> QuickLaunchUserDefault schema
    (mu/dissoc schema :id)
    (mu/optional-keys schema)))

(def QuickLaunchGlobalDefault
  (mu/closed-schema
   [:map
    [:id
     {:description         "The UUID for the quick launch global default"
      :json-schema/example #uuid "416b20b1-fa0a-44e7-ac30-78a336e9c009"}
     :uuid]

    [:app_id
     {:description         "The UUID for the app which has a global default"
      :json-schema/example #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"}
     :uuid]

    [:quick_launch_id
     {:description         "The UUID for the quick launch that is the global default for the app"
      :json-schema/example #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"}
     :uuid]]))

(def NewQuickLaunchGlobalDefault
  (mu/dissoc QuickLaunchGlobalDefault :id))

(def UpdateQuickLaunchGlobalDefault
  (as-> QuickLaunchGlobalDefault schema
    (mu/dissoc schema :id)
    (mu/optional-keys schema)))
