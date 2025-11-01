(ns common-swagger-api.malli.tools-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.tools :as t]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-field-params
  (testing "Field parameter validation"
    (testing "ToolIdParam"
      (is (valid? t/ToolIdParam #uuid "123e4567-e89b-12d3-a456-426614174000"))
      (is (not (valid? t/ToolIdParam "not-a-uuid")))
      (is (not (valid? t/ToolIdParam 123))))

    (testing "ToolRequestIdParam"
      (is (valid? t/ToolRequestIdParam #uuid "987e6543-e21b-32c1-b456-426614174000"))
      (is (not (valid? t/ToolRequestIdParam "invalid"))))

    (testing "ToolNameParam"
      (is (valid? t/ToolNameParam "samtools"))
      (is (valid? t/ToolNameParam "blast-2.14.0"))
      (is (not (valid? t/ToolNameParam 123))))

    (testing "ToolDescriptionParam"
      (is (valid? t/ToolDescriptionParam "A tool for sequence alignment"))
      (is (not (valid? t/ToolDescriptionParam nil))))

    (testing "VersionParam"
      (is (valid? t/VersionParam "1.15.1"))
      (is (valid? t/VersionParam "v2.0-beta")))

    (testing "Interactive"
      (is (valid? t/Interactive true))
      (is (valid? t/Interactive false))
      (is (not (valid? t/Interactive "true"))))))

(deftest test-ToolSearchParams
  (testing "ToolSearchParams validation"
    (testing "valid search params"
      (is (valid? t/ToolSearchParams {}))
      (is (valid? t/ToolSearchParams {:search "blast"}))
      (is (valid? t/ToolSearchParams {:public true}))
      (is (valid? t/ToolSearchParams {:include-hidden true
                                       :limit 50
                                       :offset 0
                                       :search "samtools"
                                       :public false})))

    (testing "invalid search params"
      (is (not (valid? t/ToolSearchParams {:limit -1})))
      (is (not (valid? t/ToolSearchParams {:offset -10})))
      (is (not (valid? t/ToolSearchParams {:unknown-field "value"}))))))

(deftest test-ToolDetailsParams
  (testing "ToolDetailsParams validation"
    (is (valid? t/ToolDetailsParams {}))
    (is (valid? t/ToolDetailsParams {:include-defaults true}))
    (is (valid? t/ToolDetailsParams {:include-defaults false}))
    (is (not (valid? t/ToolDetailsParams {:include-defaults "yes"})))))

(deftest test-PrivateToolDeleteParams
  (testing "PrivateToolDeleteParams validation"
    (is (valid? t/PrivateToolDeleteParams {}))
    (is (valid? t/PrivateToolDeleteParams {:force-delete true}))
    (is (valid? t/PrivateToolDeleteParams {:force-delete false}))
    (is (not (valid? t/PrivateToolDeleteParams {:force-delete "true"})))))

(deftest test-ToolTestData
  (testing "ToolTestData validation"
    (testing "valid test data"
      (is (valid? t/ToolTestData
                  {:input_files ["/path/to/input1.txt" "/path/to/input2.txt"]
                   :output_files ["/path/to/output.txt"]}))
      (is (valid? t/ToolTestData
                  {:params ["-n" "100" "-o" "output.txt"]
                   :input_files ["/test/input.fasta"]
                   :output_files ["/test/output.bam"]})))

    (testing "invalid test data"
      (is (not (valid? t/ToolTestData {})))
      (is (not (valid? t/ToolTestData {:input_files []})))
      (is (not (valid? t/ToolTestData {:output_files ["/output.txt"]}))))))

(deftest test-ToolImplementor
  (testing "ToolImplementor validation"
    (testing "valid implementor"
      (is (valid? t/ToolImplementor
                  {:implementor "Jane Smith"
                   :implementor_email "jane.smith@example.org"})))

    (testing "invalid implementor"
      (is (not (valid? t/ToolImplementor {})))
      (is (not (valid? t/ToolImplementor {:implementor "Jane Smith"})))
      (is (not (valid? t/ToolImplementor {:implementor_email "jane@example.org"}))))))

(deftest test-ToolImplementation
  (testing "ToolImplementation validation"
    (testing "valid implementation"
      (is (valid? t/ToolImplementation
                  {:implementor "Jane Smith"
                   :implementor_email "jane.smith@example.org"
                   :test {:input_files ["/test/input.txt"]
                          :output_files ["/test/output.txt"]}})))

    (testing "invalid implementation"
      (is (not (valid? t/ToolImplementation
                       {:implementor "Jane Smith"
                        :implementor_email "jane@example.org"})))
      (is (not (valid? t/ToolImplementation
                       {:test {:input_files ["/test/input.txt"]
                               :output_files ["/test/output.txt"]}}))))))

(deftest test-Tool
  (testing "Tool schema validation"
    (testing "valid tool"
      (is (valid? t/Tool
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "samtools"
                   :version "1.15.1"
                   :type "executable"}))
      (is (valid? t/Tool
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "blast"
                   :description "Basic Local Alignment Search Tool"
                   :attribution "NCBI"
                   :location "/usr/local/bin"
                   :version "2.14.0"
                   :type "executable"
                   :restricted false
                   :time_limit_seconds 3600
                   :interactive false})))

    (testing "invalid tool"
      (is (not (valid? t/Tool {})))
      (is (not (valid? t/Tool {:name "samtools"
                                :version "1.15.1"
                                :type "executable"})))
      (is (not (valid? t/Tool {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                :name "samtools"
                                :version "1.15.1"}))))))

(deftest test-ToolRequestStatus
  (testing "ToolRequestStatus validation"
    (testing "valid status"
      (is (valid? t/ToolRequestStatus
                  {:status_date 1643723400000
                   :updated_by "admin"}))
      (is (valid? t/ToolRequestStatus
                  {:status "Pending"
                   :status_date 1643723400000
                   :updated_by "admin"
                   :comments "Reviewing requirements"})))

    (testing "invalid status"
      (is (not (valid? t/ToolRequestStatus {})))
      (is (not (valid? t/ToolRequestStatus {:status_date "2023-01-01"})))
      (is (not (valid? t/ToolRequestStatus {:updated_by "admin"}))))))

(deftest test-ToolRequestDetails
  (testing "ToolRequestDetails validation"
    (testing "valid request details"
      (is (valid? t/ToolRequestDetails
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :submitted_by "johndoe"
                   :name "samtools"
                   :description "Tool for manipulating SAM/BAM files"
                   :documentation_url "https://samtools.github.io/"
                   :version "1.15.1"
                   :test_data_path "/iplant/home/user/test_data.tar.gz"
                   :cmd_line "samtools view -b input.sam > output.bam"
                   :history [{:status_date 1643723400000
                             :updated_by "admin"}]}))
      (is (valid? t/ToolRequestDetails
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :submitted_by "user123"
                   :phone "+1-555-123-4567"
                   :tool_id #uuid "567e8901-c34d-56e7-f890-123456789012"
                   :name "blast"
                   :description "BLAST+ suite"
                   :source_url "https://github.com/ncbi/blast"
                   :documentation_url "https://blast.ncbi.nlm.nih.gov/"
                   :version "2.14.0"
                   :attribution "NCBI"
                   :multithreaded true
                   :test_data_path "/iplant/home/user/blast_test.tar.gz"
                   :cmd_line "blastn -query input.fasta -db database"
                   :additional_info "Requires BLAST database files"
                   :architecture "64-bit Generic"
                   :history []
                   :interactive false})))

    (testing "invalid request details"
      (is (not (valid? t/ToolRequestDetails {})))
      (is (not (valid? t/ToolRequestDetails
                       {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                        :submitted_by "johndoe"
                        :name "samtools"})))
      (is (not (valid? t/ToolRequestDetails
                       {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                        :submitted_by "johndoe"
                        :name "samtools"
                        :description "Tool"
                        :documentation_url "https://example.com"
                        :version "1.0"
                        :test_data_path "/test/data"
                        :cmd_line "run"
                        :history []
                        :architecture "Invalid Architecture"}))))))

(deftest test-ToolRequest
  (testing "ToolRequest validation"
    (testing "valid request"
      (is (valid? t/ToolRequest
                  {:name "samtools"
                   :description "Tool for manipulating SAM/BAM files"
                   :documentation_url "https://samtools.github.io/"
                   :version "1.15.1"
                   :test_data_path "/iplant/home/user/test_data.tar.gz"
                   :cmd_line "samtools view -b input.sam > output.bam"}))
      (is (valid? t/ToolRequest
                  {:name "blast"
                   :description "BLAST+ suite"
                   :source_url "https://github.com/ncbi/blast"
                   :documentation_url "https://blast.ncbi.nlm.nih.gov/"
                   :version "2.14.0"
                   :test_data_path "/test/data"
                   :cmd_line "blastn -query input.fasta"})))

    (testing "invalid request - should not have id, submitted_by, or history"
      (is (not (valid? t/ToolRequest
                       {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                        :name "samtools"
                        :description "Tool"
                        :documentation_url "https://example.com"
                        :version "1.0"
                        :test_data_path "/test"
                        :cmd_line "run"}))))))

(deftest test-ToolRequestSummary
  (testing "ToolRequestSummary validation"
    (testing "valid summary"
      (is (valid? t/ToolRequestSummary
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "samtools"
                   :version "1.15.1"
                   :requested_by "johndoe"
                   :date_submitted 1643723400000
                   :status "Pending"
                   :date_updated 1643809800000
                   :updated_by "admin"}))
      (is (valid? t/ToolRequestSummary
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "blast"
                   :version "2.14.0"
                   :requested_by "user123"
                   :tool_id #uuid "567e8901-c34d-56e7-f890-123456789012"
                   :date_submitted 1643723400000
                   :status "Approved"
                   :date_updated 1643809800000
                   :updated_by "admin"})))

    (testing "invalid summary"
      (is (not (valid? t/ToolRequestSummary {})))
      (is (not (valid? t/ToolRequestSummary
                       {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                        :name "samtools"
                        :version "1.15.1"}))))))

(deftest test-ToolRequestListing
  (testing "ToolRequestListing validation"
    (is (valid? t/ToolRequestListing {:tool_requests []}))
    (is (valid? t/ToolRequestListing
                {:tool_requests [{:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                                  :name "samtools"
                                  :version "1.15.1"
                                  :requested_by "johndoe"
                                  :date_submitted 1643723400000
                                  :status "Pending"
                                  :date_updated 1643809800000
                                  :updated_by "admin"}]}))
    (is (not (valid? t/ToolRequestListing {})))))

(deftest test-ToolRequestListingParams
  (testing "ToolRequestListingParams validation"
    (is (valid? t/ToolRequestListingParams {}))
    (is (valid? t/ToolRequestListingParams {:limit 10 :offset 0}))
    (is (valid? t/ToolRequestListingParams {:status "Pending"}))
    (is (valid? t/ToolRequestListingParams
                {:limit 50
                 :offset 10
                 :sort-field "date_submitted"
                 :sort-dir "DESC"
                 :status "Approved"}))
    (is (not (valid? t/ToolRequestListingParams {:limit -1})))))

(deftest test-ToolRequestStatusCodeListingParams
  (testing "ToolRequestStatusCodeListingParams validation"
    (is (valid? t/ToolRequestStatusCodeListingParams {}))
    (is (valid? t/ToolRequestStatusCodeListingParams {:filter "approv"}))
    (is (not (valid? t/ToolRequestStatusCodeListingParams {:filter 123})))))

(deftest test-ToolRequestStatusCode
  (testing "ToolRequestStatusCode validation"
    (is (valid? t/ToolRequestStatusCode
                {:id #uuid "abc12345-def6-7890-1234-567890abcdef"
                 :name "Approved"
                 :description "The tool request has been approved"}))
    (is (not (valid? t/ToolRequestStatusCode {})))
    (is (not (valid? t/ToolRequestStatusCode
                     {:id #uuid "abc12345-def6-7890-1234-567890abcdef"
                      :name "Approved"})))))

(deftest test-ToolRequestStatusCodeListing
  (testing "ToolRequestStatusCodeListing validation"
    (is (valid? t/ToolRequestStatusCodeListing {:status_codes []}))
    (is (valid? t/ToolRequestStatusCodeListing
                {:status_codes [{:id #uuid "abc12345-def6-7890-1234-567890abcdef"
                                 :name "Pending"
                                 :description "Request is pending review"}
                                {:id #uuid "def67890-abc1-2345-6789-0abcdef12345"
                                 :name "Approved"
                                 :description "Request has been approved"}]}))
    (is (not (valid? t/ToolRequestStatusCodeListing {})))))

(deftest test-ToolListingToolRequestSummary
  (testing "ToolListingToolRequestSummary validation"
    (is (valid? t/ToolListingToolRequestSummary
                {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                 :status "Approved"}))
    (is (not (valid? t/ToolListingToolRequestSummary
                     {:id #uuid "987e6543-e21b-32c1-b456-426614174000"})))
    (is (not (valid? t/ToolListingToolRequestSummary
                     {:status "Approved"})))))

(deftest test-ToolListing
  (testing "ToolListing validation"
    (is (valid? t/ToolListing
                {:tools []
                 :total 0}))
    (is (valid? t/ToolListing
                {:tools []
                 :total 42}))
    (is (not (valid? t/ToolListing {})))
    (is (not (valid? t/ToolListing {:tools []})))
    (is (not (valid? t/ToolListing {:total 10})))))
