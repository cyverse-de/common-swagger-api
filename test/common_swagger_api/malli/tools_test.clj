(ns common-swagger-api.malli.tools-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.test-util :refer [decodes invalid json-schema-ok valid]]
   [common-swagger-api.malli.tools :as tools]))

(def tool-id #uuid "123e4567-e89b-12d3-a456-426614174000")
(def tool-request-id #uuid "987e6543-e21b-32c1-b456-426614174000")
(def status-code-id #uuid "abc12345-def6-7890-1234-567890abcdef")

(def image
  {:id   #uuid "6d1b1a2e-3c4d-4e5f-8a9b-0c1d2e3f4a5b"
   :name "example/image"
   :tag  "latest"})

(def container
  {:id           #uuid "f0e1d2c3-b4a5-4968-8778-695a4b3c2d1e"
   :image        image
   :memory_limit 2048})

(def new-container
  {:image        (dissoc image :id)
   :memory_limit 2048})

(def test-data
  {:input_files  ["/iplant/home/user/test_input1.txt"]
   :output_files ["/iplant/home/user/test_output1.txt"]})

(def implementor
  {:implementor       "Jane Smith"
   :implementor_email "jane.smith@example.org"})

(def implementation (assoc implementor :test test-data))

(def tool
  {:id      tool-id
   :name    "samtools"
   :version "1.15.1"
   :type    "executable"})

(def tool-details
  (assoc tool
         :is_public      true
         :permission     "own"
         :implementation implementation
         :container      container))

(def tool-import
  (assoc (dissoc tool :id)
         :implementation implementation
         :container      new-container))

(def tool-listing-item
  (assoc tool-details
         :implementation implementor
         :container      {:image (dissoc image :id) :memory_limit 2048}))

(def request-status
  {:status_date 1643723400000
   :updated_by  "admin"})

(def tool-request-details
  {:id                tool-request-id
   :submitted_by      "johndoe"
   :name              "samtools"
   :description       "Tool for manipulating SAM/BAM files"
   :documentation_url "https://samtools.github.io/"
   :version           "1.15.1"
   :test_data_path    "/iplant/home/user/test_data.tar.gz"
   :cmd_line          "samtools view -b input.sam > output.bam"
   :history           [request-status]})

(def tool-request-summary
  {:id             tool-request-id
   :name           "samtools"
   :version        "1.15.1"
   :requested_by   "johndoe"
   :date_submitted 1643723400000
   :status         "Pending"
   :date_updated   1643809800000
   :updated_by     "admin"})

(def status-code
  {:id          status-code-id
   :name        "Approved"
   :description "The tool request has been approved for implementation"})

(deftest ToolIdParam
  (valid tools/ToolIdParam tool-id)
  (invalid tools/ToolIdParam (str tool-id) nil))

(deftest ToolRequestIdParam
  (valid tools/ToolRequestIdParam tool-request-id)
  (invalid tools/ToolRequestIdParam (str tool-request-id) 123))

(deftest ToolRequestToolIdParam
  (valid tools/ToolRequestToolIdParam tool-id)
  (invalid tools/ToolRequestToolIdParam (str tool-id) nil))

(deftest ToolNameParam
  (valid tools/ToolNameParam "samtools" "")
  (invalid tools/ToolNameParam 123 nil))

(deftest ToolDescriptionParam
  (valid tools/ToolDescriptionParam "A suite of programs")
  (invalid tools/ToolDescriptionParam nil 1))

(deftest VersionParam
  (valid tools/VersionParam "1.15.1")
  (invalid tools/VersionParam 1.15 nil))

(deftest AttributionParam
  (valid tools/AttributionParam "Heng Li et al.")
  (invalid tools/AttributionParam nil :heng-li))

(deftest SubmittedByParam
  (valid tools/SubmittedByParam "johndoe")
  (invalid tools/SubmittedByParam nil 1))

(deftest ToolRequestStatusCodeId
  (valid tools/ToolRequestStatusCodeId status-code-id)
  (invalid tools/ToolRequestStatusCodeId (str status-code-id) nil))

(deftest Interactive
  (valid tools/Interactive true false)
  (invalid tools/Interactive "true" nil))

(deftest ToolSearchParams
  (valid tools/ToolSearchParams
         {}
         {:search "blast" :public true}
         {:include-hidden true :limit 50 :offset 0 :sort-field "name" :sort-dir "ASC"})
  (invalid tools/ToolSearchParams {:limit -1} {:offset -10} {:public "true"} {:extra 1}))

(deftest ToolDetailsParams
  (valid tools/ToolDetailsParams {} {:include-defaults true})
  (invalid tools/ToolDetailsParams {:include-defaults "yes"} {:extra 1}))

(deftest PrivateToolDeleteParams
  (valid tools/PrivateToolDeleteParams {} {:force-delete false})
  (invalid tools/PrivateToolDeleteParams {:force-delete "true"} {:extra 1}))

(deftest ToolTestData
  (valid tools/ToolTestData test-data (assoc test-data :params ["-n" "100"]))
  (invalid tools/ToolTestData
           {}
           (dissoc test-data :output_files)
           (assoc test-data :params "-n")
           (assoc test-data :extra 1)))

(deftest ToolImplementor
  (valid tools/ToolImplementor implementor)
  (invalid tools/ToolImplementor
           {}
           (dissoc implementor :implementor_email)
           (assoc implementor :implementor 1)
           (assoc implementor :extra 1)))

(deftest ToolImplementation
  (valid tools/ToolImplementation implementation)
  (invalid tools/ToolImplementation
           implementor
           (assoc implementation :test {})
           (assoc implementation :extra 1)))

(deftest Tool
  (valid tools/Tool
         tool
         (assoc tool :description "d" :attribution "a" :location "/usr/local/bin" :restricted false)
         (assoc tool :time_limit_seconds 3600 :interactive false))
  (invalid tools/Tool
           {}
           (dissoc tool :id)
           (assoc tool :time_limit_seconds "3600")
           (assoc tool :extra 1)))

(deftest ToolDetails
  (valid tools/ToolDetails tool-details)
  (invalid tools/ToolDetails
           tool
           (dissoc tool-details :permission)
           (assoc tool-details :is_public "true")
           (assoc tool-details :extra 1)))

(deftest ToolListingImage
  (valid tools/ToolListingImage {:image (dissoc image :id)} {:image (dissoc image :id) :memory_limit 2048})
  (invalid tools/ToolListingImage
           {}
           {:image image}
           {:image (dissoc image :id) :id (:id container)}
           {:image (dissoc image :id) :extra 1}))

(deftest ToolImportRequest
  (valid tools/ToolImportRequest tool-import (assoc tool-import :id tool-id))
  (invalid tools/ToolImportRequest
           tool
           (dissoc tool-import :implementation)
           (assoc tool-import :container container)
           (assoc tool-import :extra 1)))

(deftest import-request-decodes-longs
  (decodes tools/ToolImportRequest
           (assoc-in tool-import [:container :memory_limit] "2048")
           (assoc-in tool-import [:container :memory_limit] 2048)))

(deftest PrivateToolContainerImportRequest
  (valid tools/PrivateToolContainerImportRequest new-container)
  (invalid tools/PrivateToolContainerImportRequest
           {}
           (assoc new-container :container_devices [])
           (assoc new-container :container_volumes [])
           (assoc new-container :container_volumes_from [])))

(deftest PrivateToolImportRequest
  (valid tools/PrivateToolImportRequest
         tool-import
         (dissoc tool-import :type :implementation))
  (invalid tools/PrivateToolImportRequest
           {}
           (dissoc tool-import :name)
           (assoc-in tool-import [:container :container_devices] [])
           (assoc tool-import :extra 1)))

(deftest PrivateToolUpdateRequest
  (valid tools/PrivateToolUpdateRequest
         tool-import
         (dissoc tool-import :name :version :container))
  (invalid tools/PrivateToolUpdateRequest
           (assoc tool-import :name 1)
           (assoc-in tool-import [:container :container_volumes] [])
           (assoc tool-import :extra 1)))

(deftest ToolRequestStatus
  (valid tools/ToolRequestStatus request-status (assoc request-status :status "Pending" :comments "Reviewing"))
  (invalid tools/ToolRequestStatus
           {}
           (dissoc request-status :updated_by)
           (assoc request-status :status_date "2023-01-01")
           (assoc request-status :extra 1)))

(deftest ToolRequestDetails
  (valid tools/ToolRequestDetails
         tool-request-details
         (assoc tool-request-details
                :phone         "+1-555-123-4567"
                :tool_id       tool-id
                :source_url    "https://github.com/samtools/samtools"
                :multithreaded true
                :architecture  "64-bit Generic"
                :interactive   false))
  (invalid tools/ToolRequestDetails
           {}
           (dissoc tool-request-details :cmd_line)
           (assoc tool-request-details :architecture "Invalid Architecture")
           (assoc tool-request-details :extra 1)))

(deftest ToolRequest
  (valid tools/ToolRequest
         (dissoc tool-request-details :id :submitted_by :history)
         (assoc (dissoc tool-request-details :id :submitted_by :history)
                :source_upload_file "/iplant/home/user/tools/mytool.tar.gz"))
  (invalid tools/ToolRequest
           tool-request-details
           (dissoc tool-request-details :id :submitted_by :history :name)
           (dissoc tool-request-details :submitted_by :history)))

(deftest ToolRequestSummary
  (valid tools/ToolRequestSummary tool-request-summary (assoc tool-request-summary :tool_id tool-id))
  (invalid tools/ToolRequestSummary
           {}
           (dissoc tool-request-summary :updated_by)
           (assoc tool-request-summary :date_submitted "1643723400000")
           (assoc tool-request-summary :extra 1)))

(deftest ToolRequestListing
  (valid tools/ToolRequestListing {:tool_requests []} {:tool_requests [tool-request-summary]})
  (invalid tools/ToolRequestListing {} {:tool_requests [{}]} {:tool_requests [] :extra 1}))

(deftest ToolRequestListingParams
  (valid tools/ToolRequestListingParams {} {:limit 50 :offset 10 :sort-dir "DESC" :status "Approved"})
  (invalid tools/ToolRequestListingParams {:limit -1} {:status 1} {:extra 1}))

(deftest ToolRequestStatusCodeListingParams
  (valid tools/ToolRequestStatusCodeListingParams {} {:filter "approv"})
  (invalid tools/ToolRequestStatusCodeListingParams {:filter 123} {:extra 1}))

(deftest ToolRequestStatusCode
  (valid tools/ToolRequestStatusCode status-code)
  (invalid tools/ToolRequestStatusCode
           {}
           (dissoc status-code :description)
           (assoc status-code :id (str status-code-id))
           (assoc status-code :extra 1)))

(deftest ToolRequestStatusCodeListing
  (valid tools/ToolRequestStatusCodeListing {:status_codes []} {:status_codes [status-code]})
  (invalid tools/ToolRequestStatusCodeListing {} {:status_codes [{}]} {:status_codes [] :extra 1}))

(deftest ToolListingToolRequestSummary
  (valid tools/ToolListingToolRequestSummary (select-keys tool-request-summary [:id :status]))
  (invalid tools/ToolListingToolRequestSummary
           {:id tool-request-id}
           {:status "Approved"}
           tool-request-summary))

(deftest ToolListingItem
  (valid tools/ToolListingItem
         tool-listing-item
         (assoc tool-listing-item :tool_request (select-keys tool-request-summary [:id :status])))
  (invalid tools/ToolListingItem
           tool-details
           (dissoc tool-listing-item :container)
           (assoc tool-listing-item :implementation implementation)
           (assoc tool-listing-item :extra 1)))

(deftest ToolListing
  (valid tools/ToolListing {:tools [] :total 0} {:tools [tool-listing-item] :total 1})
  (invalid tools/ToolListing {} {:tools []} {:tools [] :total "0"} {:tools [] :total 0 :extra 1}))

(deftest ErrorPrivateToolRequestBadParam
  (valid tools/ErrorPrivateToolRequestBadParam
         {:error_code "ERR_EXISTS"}
         {:error_code "ERR_BAD_OR_MISSING_FIELD" :reason "Deprecated image"})
  (invalid tools/ErrorPrivateToolRequestBadParam {} {:error_code "ERR_NOT_FOUND"} {:error_code "ERR_EXISTS" :extra 1}))

(deftest PrivateToolImportResponse400
  (is (= tools/ErrorPrivateToolRequestBadParam (:body tools/PrivateToolImportResponse400)))
  (is (re-find #"ERR_BAD_OR_MISSING_FIELD" (:description tools/PrivateToolImportResponse400))))

(deftest PrivateToolImportResponses
  (is (= #{200 400 500 :default} (set (keys tools/PrivateToolImportResponses))))
  (is (= tools/ToolDetails (get-in tools/PrivateToolImportResponses [200 :body])))
  (is (= tools/PrivateToolImportResponse400 (get tools/PrivateToolImportResponses 400)))
  (valid (get-in tools/PrivateToolImportResponses [500 :body]) {:error_code "ERR_UNCHECKED_EXCEPTION"}))

(deftest ToolDeleteResponses
  (is (= #{200 400 403 404 500 :default} (set (keys tools/ToolDeleteResponses))))
  (is (nil? (get-in tools/ToolDeleteResponses [200 :body])))
  (valid (get-in tools/ToolDeleteResponses [400 :body]) {:error_code "ERR_NOT_WRITEABLE"})
  (valid (get-in tools/ToolDeleteResponses [403 :body]) {:error_code "ERR_FORBIDDEN"})
  (valid (get-in tools/ToolDeleteResponses [404 :body]) {:error_code "ERR_NOT_FOUND"}))

(deftest ToolDetailsResponses
  (is (= #{200 403 404 500 :default} (set (keys tools/ToolDetailsResponses))))
  (is (= tools/ToolDetails (get-in tools/ToolDetailsResponses [200 :body])))
  (valid (get-in tools/ToolDetailsResponses [403 :body]) {:error_code "ERR_FORBIDDEN"})
  (valid (get-in tools/ToolDetailsResponses [404 :body]) {:error_code "ERR_NOT_FOUND"}))

(deftest ToolUpdateResponses
  (is (= #{200 400 500 :default} (set (keys tools/ToolUpdateResponses))))
  (is (= tools/ToolDetails (get-in tools/ToolUpdateResponses [200 :body])))
  (is (= tools/PrivateToolImportResponse400 (get tools/ToolUpdateResponses 400))))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.tools))
