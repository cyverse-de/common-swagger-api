(ns common-swagger-api.malli.metadata-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.metadata :as metadata]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]
   [malli.core :as m]
   [malli.util :as mu]))

(defn- entry-description
  [schema k]
  (-> (m/deref-all (m/schema schema)) (mu/find k) second :description))

(def target-id #uuid "a14dfe49-f65f-418b-b3c5-6497284251fe")

(def avu
  {:id          #uuid "70fc1080-3152-4c09-92b0-f5b9cc70088b"
   :attr        "attribute-name"
   :value       "attribute-value"
   :unit        "attribute-unit"
   :target_id   target-id
   :created_by  "user123"
   :modified_by "user321"
   :created_on  1757465246000
   :modified_on 1757465251000})

(def avu-request {:attr "attribute-name" :value "attribute-value" :unit "attribute-unit"})

(deftest TargetIdParam
  (valid metadata/TargetIdParam target-id)
  (invalid metadata/TargetIdParam (str target-id) nil))

(deftest Avu
  (valid metadata/Avu avu (assoc avu :avus [avu]) (assoc avu :avus [(assoc avu :avus [])]))
  (invalid metadata/Avu
           {}
           (dissoc avu :unit)
           (assoc avu :created_on "1757465246000")
           (assoc avu :avus [{}])
           (assoc avu :extra 1))
  (is (= "The Attribute's unit" (entry-description metadata/Avu :unit)))
  (is (= "The date the AVU was last modified in ms since the POSIX epoch"
         (entry-description metadata/Avu :modified_on))))

(deftest AvuList
  (valid metadata/AvuList {:avus []} {:avus [avu]})
  (invalid metadata/AvuList {} {:avus [{}]} {:avus avu} {:avus [] :extra 1}))

(deftest AvuRequest
  (valid metadata/AvuRequest avu-request avu (assoc avu-request :avus [avu-request]))
  (invalid metadata/AvuRequest
           {}
           (dissoc avu-request :value)
           (assoc avu-request :target_id (str target-id))
           (assoc avu-request :avus [{}])
           (assoc avu-request :extra 1))
  (is (= "The Attribute's unit" (entry-description metadata/AvuRequest :unit)))
  (is (= "The date the AVU was last modified in ms since the POSIX epoch"
         (entry-description metadata/AvuRequest :modified_on))))

(deftest AvuListRequest
  (valid metadata/AvuListRequest {:avus []} {:avus [avu-request]})
  (invalid metadata/AvuListRequest {} {:avus "x"} {:avus [{}]} {:avus [] :extra 1}))

(deftest SetAvuRequest
  (valid metadata/SetAvuRequest {} {:avus []} {:avus [avu-request]})
  (invalid metadata/SetAvuRequest {:avus [{}]} {:avus avu-request} {:extra 1}))

(deftest AvuSearchParams
  (valid metadata/AvuSearchParams
         {}
         {:attribute ["attr1"] :value ["value1"]}
         {:unit ["unit1"] :target-id [target-id]})
  (invalid metadata/AvuSearchParams {:attribute "attr1"} {:target-id [(str target-id)]} {:extra 1}))

(deftest DataTypeEnum
  (is (= ["file" "folder"] metadata/DataTypes))
  (valid metadata/DataTypeEnum "file" "folder")
  (invalid metadata/DataTypeEnum :file "other" nil))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.metadata))

(deftest examples
  (examples-valid 'common-swagger-api.malli.metadata))
