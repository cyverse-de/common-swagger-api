(ns common-swagger-api.malli.apps.metadata-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.apps.metadata :as metadata]))

;; This namespace defines endpoint documentation only, so there are no schemas to validate and no json-schema or
;; examples test.
(deftest endpoint-documentation
  (is (= "View all Metadata AVUs" metadata/AppMetadataListingSummary))
  (is (re-find #"`read` permission" metadata/AppMetadataListingDocs))
  (is (= "Set Metadata AVUs" metadata/AppMetadataSetSummary))
  (is (re-find #"`write` permission" metadata/AppMetadataSetDocs))
  (is (= "Add/Update Metadata AVUs" metadata/AppMetadataUpdateSummary))
  (is (re-find #"`write` permission" metadata/AppMetadataUpdateDocs)))
