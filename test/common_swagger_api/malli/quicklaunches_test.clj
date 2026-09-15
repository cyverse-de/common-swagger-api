(ns common-swagger-api.malli.quicklaunches-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.quicklaunches :as quicklaunches]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def submission
  {:system_id  "de"
   :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
   :config     {:1104ffbe-3b81-4c64-868c-20f3dc86fd1a_ced83179-6aa8-424e-9cae-62fc8b21e1c0 "foo.txt"}
   :debug      false
   :name       "sequence-alignment-zea-mays"
   :notify     true
   :output_dir "/zone/home/username/folder-name"})

(def quick-launch
  {:id             #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
   :name           "BLAST"
   :creator        "janedoe"
   :app_id         #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
   :app_version_id #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
   :submission     submission})

(def favorite
  {:id              #uuid "234f5f17-cbcf-4e23-87fa-1bceaeba09ee"
   :quick_launch_id #uuid "7bd11b7f-91f2-4e7c-9e48-9a3ba802f354"
   :user            "janedoe"})

(def user-default
  {:id              #uuid "36fe7915-5984-4848-b5bc-965899b0e061"
   :user            "janedoe"
   :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
   :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"})

(def global-default
  {:id              #uuid "416b20b1-fa0a-44e7-ac30-78a336e9c009"
   :app_id          #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
   :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"})

(deftest QuickLaunch
  (valid quicklaunches/QuickLaunch quick-launch (assoc quick-launch :description "BLAST defaults" :is_public true))
  (invalid quicklaunches/QuickLaunch
           {}
           (dissoc quick-launch :app_version_id)
           (assoc quick-launch :creator " ")
           (assoc quick-launch :submission {})
           (assoc quick-launch :extra 1)))

(deftest NewQuickLaunch
  (valid quicklaunches/NewQuickLaunch
         (dissoc quick-launch :id)
         (dissoc quick-launch :id :app_version_id))
  (invalid quicklaunches/NewQuickLaunch
           quick-launch
           (dissoc quick-launch :id :submission)
           (assoc (dissoc quick-launch :id) :name 1)
           (assoc (dissoc quick-launch :id) :extra 1)))

(deftest UpdateQuickLaunch
  ;; Every key of every nested map is optional in this schema.
  (valid quicklaunches/UpdateQuickLaunch
         {}
         {:submission {}}
         {:submission {:requirements [{}] :file-metadata [{}]}}
         (dissoc quick-launch :id))
  (invalid quicklaunches/UpdateQuickLaunch
           quick-launch
           {:name 1}
           {:submission {:notify "yes"}}
           {:extra 1}))

(deftest QuickLaunchFavorite
  (valid quicklaunches/QuickLaunchFavorite favorite)
  (invalid quicklaunches/QuickLaunchFavorite
           {}
           (dissoc favorite :user)
           (assoc favorite :quick_launch_id (str (:quick_launch_id favorite)))
           (assoc favorite :extra 1)))

(deftest NewQuickLaunchFavorite
  (valid quicklaunches/NewQuickLaunchFavorite (dissoc favorite :id :user))
  (invalid quicklaunches/NewQuickLaunchFavorite
           {}
           favorite
           (dissoc favorite :user)
           (assoc (dissoc favorite :id :user) :extra 1)))

(deftest QuickLaunchUserDefault
  (valid quicklaunches/QuickLaunchUserDefault user-default)
  (invalid quicklaunches/QuickLaunchUserDefault
           {}
           (dissoc user-default :app_id)
           (assoc user-default :user " ")
           (assoc user-default :extra 1)))

(deftest NewQuickLaunchUserDefault
  (valid quicklaunches/NewQuickLaunchUserDefault (dissoc user-default :id :user))
  (invalid quicklaunches/NewQuickLaunchUserDefault
           {}
           user-default
           (dissoc user-default :id)
           (assoc (dissoc user-default :id :user) :extra 1)))

(deftest UpdateQuickLaunchUserDefault
  (valid quicklaunches/UpdateQuickLaunchUserDefault {} (dissoc user-default :id) {:user "janedoe"})
  (invalid quicklaunches/UpdateQuickLaunchUserDefault user-default {:user 1} {:app_id "not-a-uuid"} {:extra 1}))

(deftest QuickLaunchGlobalDefault
  (valid quicklaunches/QuickLaunchGlobalDefault global-default)
  (invalid quicklaunches/QuickLaunchGlobalDefault
           {}
           (dissoc global-default :quick_launch_id)
           (assoc global-default :app_id "not-a-uuid")
           (assoc global-default :extra 1)))

(deftest NewQuickLaunchGlobalDefault
  (valid quicklaunches/NewQuickLaunchGlobalDefault (dissoc global-default :id))
  (invalid quicklaunches/NewQuickLaunchGlobalDefault
           {}
           global-default
           (dissoc global-default :id :app_id)
           (assoc (dissoc global-default :id) :extra 1)))

(deftest UpdateQuickLaunchGlobalDefault
  (valid quicklaunches/UpdateQuickLaunchGlobalDefault {} (dissoc global-default :id) {:app_id (:app_id global-default)})
  (invalid quicklaunches/UpdateQuickLaunchGlobalDefault global-default {:app_id 1} {:extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.quicklaunches))
