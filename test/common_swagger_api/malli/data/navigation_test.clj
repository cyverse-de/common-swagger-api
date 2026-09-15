(ns common-swagger-api.malli.data.navigation-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.data.navigation :as navigation]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def base-paths
  {:user_home_path  "/iplant/home/janedoe"
   :user_trash_path "/iplant/trash/home/de-irods/janedoe"
   :base_trash_path "/iplant/trash/home/de-irods"})

(def root-listing
  {:id            #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c"
   :path          "/iplant/home/janedoe"
   :label         "janedoe"
   :date-created  1763771841123
   :date-modified 1763772014456
   :permission    :own})

(deftest UserBasePaths
  (valid navigation/UserBasePaths base-paths)
  (invalid navigation/UserBasePaths
           {}
           (dissoc base-paths :base_trash_path)
           (assoc base-paths :user_home_path 1)
           (assoc base-paths :extra 1)))

(deftest RootListing
  (valid navigation/RootListing root-listing (assoc root-listing :share-count 27))
  (invalid navigation/RootListing
           {}
           (assoc root-listing :type :dir)
           (dissoc root-listing :permission)
           (assoc root-listing :date-created "1763771841123")))

(deftest NavigationRootResponse
  (valid navigation/NavigationRootResponse {:roots [root-listing] :base-paths base-paths})
  (invalid navigation/NavigationRootResponse
           {:roots [root-listing]}
           {:roots root-listing :base-paths base-paths}
           {:roots [root-listing] :base-paths (dissoc base-paths :user_home_path)}
           {:roots [root-listing] :base-paths base-paths :extra 1}))

(deftest FolderListing
  (valid navigation/FolderListing
         root-listing
         (assoc root-listing :folders [])
         (assoc root-listing :folders [(assoc root-listing :folders [root-listing])]))
  (invalid navigation/FolderListing
           {}
           (assoc root-listing :type :dir)
           (assoc root-listing :folders [{:id (:id root-listing)}])
           (assoc root-listing :folders root-listing)))

(deftest NavigationResponse
  (valid navigation/NavigationResponse {:folder (assoc root-listing :folders [root-listing])})
  (invalid navigation/NavigationResponse
           {}
           {:folder (dissoc root-listing :path)}
           {:folder root-listing :extra 1}))

(deftest NavigationRootErrorCodeResponses
  (is (= ["ERR_UNCHECKED_EXCEPTION" "ERR_SCHEMA_VALIDATION" "ERR_DOES_NOT_EXIST" "ERR_NOT_READABLE"
          "ERR_NOT_A_USER"]
         navigation/NavigationRootErrorCodeResponses)))

(deftest NavigationRootErrorResponses
  (valid navigation/NavigationRootErrorResponses
         {:error_code "ERR_DOES_NOT_EXIST"}
         {:error_code "ERR_NOT_READABLE" :reason "Not readable"}
         {:error_code "ERR_UNCHECKED_EXCEPTION"})
  (invalid navigation/NavigationRootErrorResponses
           {}
           {:error_code "ERR_NOT_A_FOLDER"}
           {:error_code "ERR_NOT_A_USER" :extra 1}))

(deftest NavigationErrorResponses
  (valid navigation/NavigationErrorResponses
         {:error_code "ERR_NOT_A_FOLDER"}
         {:error_code "ERR_DOES_NOT_EXIST" :reason "Does not exist"})
  (invalid navigation/NavigationErrorResponses {} {:error_code "ERR_NOT_FOUND"} {:error_code "ERR_NOT_A_FOLDER" :x 1}))

(deftest NavigationRootResponses
  (is (= #{200 500 :default} (set (keys navigation/NavigationRootResponses))))
  (is (= navigation/NavigationRootErrorResponses (get-in navigation/NavigationRootResponses [500 :body])))
  (valid (get-in navigation/NavigationRootResponses [200 :body]) {:roots [root-listing] :base-paths base-paths})
  (invalid (get-in navigation/NavigationRootResponses [200 :body]) {:roots [root-listing]}))

(deftest NavigationResponses
  (is (= #{200 500 :default} (set (keys navigation/NavigationResponses))))
  (is (= navigation/NavigationErrorResponses (get-in navigation/NavigationResponses [500 :body])))
  (valid (get-in navigation/NavigationResponses [200 :body]) {:folder root-listing})
  (invalid (get-in navigation/NavigationResponses [200 :body]) {}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.data.navigation))
