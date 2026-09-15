(ns common-swagger-api.malli.apps.metadata-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.apps.metadata :as metadata]
   [common-swagger-api.malli.test-util :refer [json-schema-ok]]))

;; This namespace defines endpoint documentation only, so there are no schemas to validate.
(deftest endpoint-documentation
  (is (= "View all Metadata AVUs" metadata/AppMetadataListingSummary))
  (is (re-find #"`read` permission" metadata/AppMetadataListingDocs))
  (is (= "Set Metadata AVUs" metadata/AppMetadataSetSummary))
  (is (re-find #"`write` permission" metadata/AppMetadataSetDocs))
  (is (= "Add/Update Metadata AVUs" metadata/AppMetadataUpdateSummary))
  (is (re-find #"`write` permission" metadata/AppMetadataUpdateDocs)))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps.metadata))
