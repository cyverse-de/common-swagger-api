(ns common-swagger-api.malli.apps.app-documentation-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common-swagger-api.malli.apps :as apps]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AppDocumentation
  (testing "AppDocumentation validation"
    (testing "valid app documentation - minimal required fields only"
      (is (valid? apps/AppDocumentation
                  {:documentation "This tool performs sequence alignment using BLAST algorithm."
                   :references ["https://doi.org/10.1093/nar/gkv416" "PMID: 25916842"]}))
      (is (valid? apps/AppDocumentation
                  {:documentation "A simple analysis tool for genomic data processing."
                   :references []}))
      (is (valid? apps/AppDocumentation
                  {:documentation ""
                   :references ["https://example.org/docs"]})))

    (testing "valid app documentation - with optional app_id field"
      (is (valid? apps/AppDocumentation
                  {:app_id "app-id-12345"
                   :documentation "This tool performs sequence alignment using BLAST algorithm."
                   :references ["https://doi.org/10.1093/nar/gkv416"]}))
      (is (valid? apps/AppDocumentation
                  {:app_id "another-app-id"
                   :documentation "Analysis documentation"
                   :references []})))

    (testing "valid app documentation - with optional version_id field"
      (is (valid? apps/AppDocumentation
                  {:version_id "latest-version-id-123"
                   :documentation "This tool performs sequence alignment using BLAST algorithm."
                   :references ["https://doi.org/10.1093/nar/gkv416"]}))
      (is (valid? apps/AppDocumentation
                  {:version_id "version-abc"
                   :documentation "Version documentation"
                   :references []})))

    (testing "valid app documentation - with optional timestamp fields"
      (is (valid? apps/AppDocumentation
                  {:documentation "This tool performs sequence alignment using BLAST algorithm."
                   :references ["https://doi.org/10.1093/nar/gkv416"]
                   :created_on #inst "2024-02-10T09:15:00.000-00:00"}))
      (is (valid? apps/AppDocumentation
                  {:documentation "Analysis documentation"
                   :references []
                   :modified_on #inst "2024-10-15T11:20:00.000-00:00"}))
      (is (valid? apps/AppDocumentation
                  {:documentation "Complete timestamp documentation"
                   :references ["https://example.org"]
                   :created_on #inst "2024-01-01T00:00:00.000-00:00"
                   :modified_on #inst "2024-12-31T23:59:59.000-00:00"})))

    (testing "valid app documentation - with optional user fields"
      (is (valid? apps/AppDocumentation
                  {:documentation "This tool performs sequence alignment using BLAST algorithm."
                   :references ["https://doi.org/10.1093/nar/gkv416"]
                   :created_by "jsmith"}))
      (is (valid? apps/AppDocumentation
                  {:documentation "Analysis documentation"
                   :references []
                   :modified_by "jdoe"}))
      (is (valid? apps/AppDocumentation
                  {:documentation "Complete user documentation"
                   :references ["https://example.org"]
                   :created_by "alice"
                   :modified_by "bob"})))

    (testing "valid app documentation - with all optional fields populated"
      (is (valid? apps/AppDocumentation
                  {:app_id "app-id-12345"
                   :version_id "latest-version-id-123"
                   :documentation "This tool performs sequence alignment using BLAST algorithm."
                   :references ["https://doi.org/10.1093/nar/gkv416" "PMID: 25916842"]
                   :created_on #inst "2024-02-10T09:15:00.000-00:00"
                   :modified_on #inst "2024-10-15T11:20:00.000-00:00"
                   :created_by "jsmith"
                   :modified_by "jdoe"}))
      (is (valid? apps/AppDocumentation
                  {:app_id "blast-app"
                   :version_id "v2.0"
                   :documentation "BLAST+ suite for sequence alignment and analysis"
                   :references ["https://blast.ncbi.nlm.nih.gov/Blast.cgi"
                                "https://doi.org/10.1186/1471-2105-10-421"]
                   :created_on #inst "2023-06-01T10:00:00.000-00:00"
                   :modified_on #inst "2025-01-10T15:30:00.000-00:00"
                   :created_by "researcher1"
                   :modified_by "admin"})))

    (testing "valid app documentation - with various combinations of optional fields"
      (is (valid? apps/AppDocumentation
                  {:app_id "app-123"
                   :documentation "Documentation text"
                   :references []
                   :created_by "user1"}))
      (is (valid? apps/AppDocumentation
                  {:version_id "v1.5"
                   :documentation "Version documentation"
                   :references ["https://example.com"]
                   :modified_on #inst "2024-05-15T12:00:00.000-00:00"}))
      (is (valid? apps/AppDocumentation
                  {:app_id "tool-456"
                   :version_id "beta-1"
                   :documentation "Beta documentation"
                   :references []
                   :created_on #inst "2024-03-01T08:00:00.000-00:00"
                   :modified_by "tester"})))

    (testing "invalid app documentation - missing required documentation field"
      (is (not (valid? apps/AppDocumentation
                       {:references ["https://doi.org/10.1093/nar/gkv416"]})))
      (is (not (valid? apps/AppDocumentation
                       {:app_id "app-123"
                        :references []}))))

    (testing "invalid app documentation - missing required references field"
      (is (not (valid? apps/AppDocumentation
                       {:documentation "This tool performs sequence alignment using BLAST algorithm."})))
      (is (not (valid? apps/AppDocumentation
                       {:app_id "app-123"
                        :documentation "Documentation text"}))))

    (testing "invalid app documentation - missing both required fields"
      (is (not (valid? apps/AppDocumentation {})))
      (is (not (valid? apps/AppDocumentation
                       {:app_id "app-123"
                        :version_id "v1.0"}))))

    (testing "invalid app documentation - wrong type for documentation field"
      (is (not (valid? apps/AppDocumentation
                       {:documentation 12345
                        :references []})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation true
                        :references ["https://example.org"]})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation ["not" "a" "string"]
                        :references []})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation nil
                        :references []}))))

    (testing "invalid app documentation - wrong type for references field"
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references "not-a-vector"})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references {:ref1 "value"}})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references nil}))))

    (testing "invalid app documentation - references vector with non-string elements"
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references [123 456]})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references ["valid-ref" 789]})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references [true false]}))))

    (testing "invalid app documentation - wrong type for app_id field"
      (is (not (valid? apps/AppDocumentation
                       {:app_id 12345
                        :documentation "Documentation text"
                        :references []})))
      (is (not (valid? apps/AppDocumentation
                       {:app_id true
                        :documentation "Documentation text"
                        :references []})))
      (is (not (valid? apps/AppDocumentation
                       {:app_id ["not-a-string"]
                        :documentation "Documentation text"
                        :references []}))))

    (testing "invalid app documentation - wrong type for version_id field"
      (is (not (valid? apps/AppDocumentation
                       {:version_id 12345
                        :documentation "Documentation text"
                        :references []})))
      (is (not (valid? apps/AppDocumentation
                       {:version_id false
                        :documentation "Documentation text"
                        :references []})))
      (is (not (valid? apps/AppDocumentation
                       {:version_id {:version "1.0"}
                        :documentation "Documentation text"
                        :references []}))))

    (testing "invalid app documentation - wrong type for timestamp fields"
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references []
                        :created_on "2024-02-10"})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references []
                        :modified_on "2024-10-15"})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references []
                        :created_on 1234567890})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references []
                        :modified_on true}))))

    (testing "invalid app documentation - wrong type for user fields"
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references []
                        :created_by 12345})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references []
                        :modified_by false})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references []
                        :created_by ["not-a-string"]})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references []
                        :modified_by {:user "name"}}))))

    (testing "invalid app documentation - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references []
                        :extra-field "not-allowed"})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references []
                        :author "someone"})))
      (is (not (valid? apps/AppDocumentation
                       {:documentation "Documentation text"
                        :references []
                        :id "doc-123"})))
      (is (not (valid? apps/AppDocumentation
                       {:app_id "app-123"
                        :version_id "v1.0"
                        :documentation "Documentation text"
                        :references ["https://example.org"]
                        :created_on #inst "2024-02-10T09:15:00.000-00:00"
                        :modified_on #inst "2024-10-15T11:20:00.000-00:00"
                        :created_by "jsmith"
                        :modified_by "jdoe"
                        :status "published"}))))

    (testing "invalid app documentation - app_id with empty string"
      (is (not (valid? apps/AppDocumentation
                       {:app_id ""
                        :documentation "Documentation text"
                        :references []}))))

    (testing "invalid app documentation - multiple validation errors"
      (is (not (valid? apps/AppDocumentation
                       {:app_id 12345
                        :documentation 67890
                        :references "not-a-vector"})))
      (is (not (valid? apps/AppDocumentation
                       {:version_id false
                        :documentation nil
                        :references ["valid" 123 "refs"]}))))))
