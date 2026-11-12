(ns common-swagger-api.malli.common-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.common :as common]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]))

(deftest IncludeHiddenParams
  (valid common/IncludeHiddenParams {} {:include-hidden true} {:include-hidden false})
  (invalid common/IncludeHiddenParams {:include-hidden "true"} {:include-hidden nil} {:extra 1}))

(deftest IncludeDeletedParams
  (valid common/IncludeDeletedParams {} {:include-deleted true} {:include-deleted false})
  (invalid common/IncludeDeletedParams {:include-deleted "yes"} {:include-deleted nil} {:extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.common))

(deftest examples
  (examples-valid 'common-swagger-api.malli.common))
