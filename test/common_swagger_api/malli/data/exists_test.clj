(ns common-swagger-api.malli.data.exists-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.data.exists :as exists]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]
   [malli.json-schema :as js]))

(def existence-map
  {(keyword "/iplant/home/janedoe/file.txt") true
   (keyword "/iplant/home/janedoe/folder")   false})

(def doc-path (keyword "/path/from/request/to/a/file/or/folder"))

(deftest ExistenceRequest
  (valid exists/ExistenceRequest
         {:paths ["/iplant/home/janedoe/file.txt"]}
         {:paths ["/iplant/home/janedoe/file.txt" "/iplant/home/janedoe/folder"]})
  (invalid exists/ExistenceRequest
           {}
           {:paths []}
           {:paths [""]}
           {:paths ["/iplant/home/janedoe/file.txt"] :extra 1}))

(deftest PathExistenceMap
  (valid exists/PathExistenceMap {} existence-map)
  (invalid exists/PathExistenceMap
           {"/iplant/home/janedoe/file.txt" true}
           {(keyword "/iplant/home/janedoe/file.txt") "true"}))

(deftest ExistenceInfo
  (valid exists/ExistenceInfo {:paths {}} {:paths existence-map})
  (invalid exists/ExistenceInfo
           {}
           {:paths [(keyword "/iplant/home/janedoe/file.txt")]}
           {:paths existence-map :extra 1}))

(deftest ExistenceResponsePathsMap
  (valid exists/ExistenceResponsePathsMap {doc-path true} {doc-path false})
  (invalid exists/ExistenceResponsePathsMap {} {doc-path "true"} {doc-path true :extra 1}))

(deftest ExistenceResponse
  (valid exists/ExistenceResponse {:paths {doc-path true}})
  (invalid exists/ExistenceResponse {} {:paths existence-map} {:paths {doc-path true} :extra 1}))

(deftest ExistenceErrorCodeResponses
  (is (= ["ERR_UNCHECKED_EXCEPTION" "ERR_SCHEMA_VALIDATION" "ERR_NOT_A_USER" "ERR_TOO_MANY_RESULTS"]
         exists/ExistenceErrorCodeResponses)))

(deftest ExistenceErrorResponses
  (valid exists/ExistenceErrorResponses
         {:error_code "ERR_NOT_A_USER"}
         {:error_code "ERR_TOO_MANY_RESULTS" :reason "Too many results"}
         {:error_code "ERR_UNCHECKED_EXCEPTION"})
  (invalid exists/ExistenceErrorResponses {} {:error_code "ERR_NOT_FOUND"} {:error_code "ERR_NOT_A_USER" :extra 1}))

(deftest ExistenceResponses
  (is (= #{200 500 :default} (set (keys exists/ExistenceResponses))))
  (is (= exists/ExistenceErrorResponses (get-in exists/ExistenceResponses [500 :body])))
  (valid (get-in exists/ExistenceResponses [200 :body]) {:paths existence-map})
  (invalid (get-in exists/ExistenceResponses [200 :body]) {} {:paths existence-map :extra 1})
  (is (= #{:paths} (set (keys (:properties (js/transform (get-in exists/ExistenceResponses [200 :body]))))))))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.data.exists))

(deftest examples
  (examples-valid 'common-swagger-api.malli.data.exists))
