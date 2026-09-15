(ns common-swagger-api.malli.stats-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.stats :as stats]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]
   [malli.json-schema :as js]))

(def data-item-id #uuid "ca23780a-6acb-47aa-9f9a-eab1ef9a541c")

(def data-stat
  {:id            data-item-id
   :path          "/iplant/home/janedoe/file.txt"
   :type          :file
   :label         "file.txt"
   :date-created  1763771841123
   :date-modified 1763772014456
   :permission    :own})

(def dir-stat
  (assoc data-stat
         :type       :dir
         :path       "/iplant/home/janedoe/folder"
         :label      "folder"
         :file-count 42
         :dir-count  27))

(def file-stat
  (assoc data-stat
         :file-size    57
         :content-type "text/plain"
         :infoType     "perl"
         :md5          "d5a0bfa9677508d7b379c3e07284a493"))

(deftest DataTypeEnum
  (valid stats/DataTypeEnum :file :dir)
  (invalid stats/DataTypeEnum "file" :link nil))

(deftest DataItemIdParam
  (valid stats/DataItemIdParam data-item-id)
  (invalid stats/DataItemIdParam (str data-item-id) nil))

(deftest DataItemPathParam
  (valid stats/DataItemPathParam "/iplant/home/janedoe/file.txt")
  (invalid stats/DataItemPathParam "" "   " nil))

(deftest StatQueryParams
  (valid stats/StatQueryParams {} {:validation-behavior :read} {:validation-behavior :own})
  (invalid stats/StatQueryParams {:validation-behavior "read"} {:validation-behavior :none} {:extra 1}))

(deftest FilteredStatQueryParams
  (valid stats/FilteredStatQueryParams
         {}
         {:validation-behavior :read :filter-include "infoType,path"}
         {:filter-exclude "file-size"})
  (invalid stats/FilteredStatQueryParams
           {:filter-include 1}
           {:filter-exclude :file-size}
           {:extra 1}))

(deftest DataStatInfo
  (valid stats/DataStatInfo data-stat (assoc data-stat :share-count 27))
  (invalid stats/DataStatInfo
           {}
           (dissoc data-stat :permission)
           (assoc data-stat :date-created "1763771841123")
           (assoc data-stat :extra 1)))

(deftest DirStatInfo
  (valid stats/DirStatInfo dir-stat (assoc dir-stat :share-count 27))
  (invalid stats/DirStatInfo
           data-stat
           (dissoc dir-stat :dir-count)
           (assoc dir-stat :file-count "42")
           (assoc dir-stat :extra 1)))

(deftest FileStatInfo
  (valid stats/FileStatInfo file-stat (assoc file-stat :share-count 27))
  (invalid stats/FileStatInfo
           data-stat
           (dissoc file-stat :md5)
           (assoc file-stat :content-type "")
           (assoc file-stat :extra 1)))

(deftest FilteredStatInfo
  (valid stats/FilteredStatInfo {} (select-keys file-stat [:id :path]) file-stat dir-stat)
  (invalid stats/FilteredStatInfo {:file-size "57"} (assoc file-stat :extra 1)))

(deftest AvailableStatFields
  (is (= [:id :path :type :label :date-created :date-modified :permission :share-count
          :file-count :dir-count :file-size :content-type :infoType :md5]
         (vec stats/AvailableStatFields))))

(deftest FileStat
  (valid stats/FileStat {:file file-stat})
  (invalid stats/FileStat {} {:file dir-stat} {:file file-stat :extra 1}))

(deftest PathsMap
  (valid stats/PathsMap
         {}
         {(keyword (:path file-stat)) file-stat}
         {(keyword (:path dir-stat)) dir-stat})
  (invalid stats/PathsMap
           {(keyword (:path file-stat)) data-stat}
           {(:path file-stat) file-stat}
           {(keyword (:path dir-stat)) (assoc dir-stat :extra 1)}))

(deftest FilteredPathsMap
  (valid stats/FilteredPathsMap
         {}
         {(keyword (:path file-stat)) (select-keys file-stat [:id :path])}
         {(keyword (:path dir-stat)) dir-stat})
  (invalid stats/FilteredPathsMap
           {(:path file-stat) file-stat}
           {(keyword (:path file-stat)) (assoc file-stat :extra 1)}))

(deftest DataIdsMap
  (valid stats/DataIdsMap {} {(keyword (str data-item-id)) file-stat} {(keyword (str data-item-id)) dir-stat})
  (invalid stats/DataIdsMap
           {(keyword (str data-item-id)) data-stat}
           {data-item-id file-stat}))

(deftest FilteredDataIdsMap
  (valid stats/FilteredDataIdsMap {} {(keyword (str data-item-id)) {}} {(keyword (str data-item-id)) file-stat})
  (invalid stats/FilteredDataIdsMap
           {data-item-id file-stat}
           {(keyword (str data-item-id)) (assoc file-stat :extra 1)}))

(deftest StatusInfo
  (valid stats/StatusInfo
         {}
         {:paths {(keyword (:path file-stat)) file-stat}}
         {:ids {(keyword (str data-item-id)) dir-stat}})
  (invalid stats/StatusInfo
           {:paths {(keyword (:path file-stat)) data-stat}}
           {:ids []}
           {:extra 1}))

(deftest FilteredStatusInfo
  (valid stats/FilteredStatusInfo
         {}
         {:paths {(keyword (:path file-stat)) {}}}
         {:ids {(keyword (str data-item-id)) (select-keys dir-stat [:id :path])}})
  (invalid stats/FilteredStatusInfo
           {:paths {(keyword (:path file-stat)) {:file-size "57"}}}
           {:ids []}
           {:extra 1}))

(deftest StatResponsePathsMap
  (valid stats/StatResponsePathsMap
         {(keyword ":/path/from/request/to/a/folder") dir-stat
          (keyword ":/path/from/request/to/a/file")   file-stat})
  (invalid stats/StatResponsePathsMap
           {}
           {(keyword ":/path/from/request/to/a/folder") dir-stat}
           {(keyword ":/path/from/request/to/a/folder") file-stat
            (keyword ":/path/from/request/to/a/file")   file-stat}
           {(keyword ":/path/from/request/to/a/folder") dir-stat
            (keyword ":/path/from/request/to/a/file")   file-stat
            :extra                                     1}))

(deftest StatResponseIdsMap
  (valid stats/StatResponseIdsMap {:some-folder-uuid dir-stat :some-file-uuid file-stat})
  (invalid stats/StatResponseIdsMap
           {}
           {:some-folder-uuid dir-stat}
           {:some-folder-uuid file-stat :some-file-uuid file-stat}
           {:some-folder-uuid dir-stat :some-file-uuid file-stat :extra 1}))

(deftest StatResponse
  (valid stats/StatResponse
         {}
         {:paths {(keyword ":/path/from/request/to/a/folder") dir-stat
                  (keyword ":/path/from/request/to/a/file")   file-stat}}
         {:ids {:some-folder-uuid dir-stat :some-file-uuid file-stat}})
  (invalid stats/StatResponse {:paths {}} {:ids {:some-folder-uuid dir-stat}} {:extra 1}))

(deftest StatErrorResponses
  (valid stats/StatErrorResponses
         {:error_code "ERR_DOES_NOT_EXIST"}
         {:error_code "ERR_TOO_MANY_RESULTS" :reason "Too many results"}
         {:error_code "ERR_UNCHECKED_EXCEPTION"})
  (invalid stats/StatErrorResponses {} {:error_code "ERR_NOT_FOUND"} {:error_code "ERR_NOT_OWNER" :extra 1}))

(deftest StatResponses
  (is (= #{200 500 :default} (set (keys stats/StatResponses))))
  (is (= stats/StatErrorResponses (get-in stats/StatResponses [500 :body])))
  (valid (get-in stats/StatResponses [200 :body]) {} {:paths {(keyword (:path file-stat)) file-stat}})
  (invalid (get-in stats/StatResponses [200 :body]) {:extra 1})
  (is (= #{:paths :ids} (set (keys (:properties (js/transform (get-in stats/StatResponses [200 :body]))))))))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.stats))
