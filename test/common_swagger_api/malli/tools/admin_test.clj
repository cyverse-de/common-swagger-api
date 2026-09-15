(ns common-swagger-api.malli.tools.admin-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]
   [common-swagger-api.malli.tools :as tools]
   [common-swagger-api.malli.tools.admin :as admin]))

(def tool-id #uuid "123e4567-e89b-12d3-a456-426614174000")

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

(def implementation
  {:implementor       "Jane Smith"
   :implementor_email "jane.smith@example.org"
   :test              {:input_files  ["/iplant/home/user/test_input1.txt"]
                       :output_files ["/iplant/home/user/test_output1.txt"]}})

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

(def tool-ids-list {:tool_ids [tool-id]})

(deftest ToolIdsList
  (valid admin/ToolIdsList tool-ids-list {:tool_ids []})
  (invalid admin/ToolIdsList
           {}
           {:tool_ids tool-id}
           {:tool_ids ["not-a-uuid"]}
           (assoc tool-ids-list :extra 1)))

(deftest ToolUpdateParams
  (valid admin/ToolUpdateParams {} {:overwrite-public true})
  (invalid admin/ToolUpdateParams
           {:overwrite-public "true"}
           {:overwrite-public true :extra 1}))

(deftest ToolsImportRequest
  (valid admin/ToolsImportRequest {:tools []} {:tools [tool-import]} {:tools [(assoc tool-import :id tool-id)]})
  (invalid admin/ToolsImportRequest
           {}
           {:tools tool-import}
           {:tools [(dissoc tool-import :implementation)]}
           {:tools [] :extra 1}))

(deftest ToolUpdateRequest
  (valid admin/ToolUpdateRequest
         tool-import
         {}
         (dissoc tool-import :name :version :type :implementation :container))
  (invalid admin/ToolUpdateRequest
           (assoc tool-import :name 1)
           (assoc tool-import :container container)
           (assoc tool-import :extra 1)))

(deftest ToolRequestStatusUpdate
  (valid admin/ToolRequestStatusUpdate {} {:status "Pending"} {:status "Pending" :comments "Reviewing"})
  (invalid admin/ToolRequestStatusUpdate
           {:status 1}
           {:status_date 1643723400000}
           {:updated_by "admin"}
           {:extra 1}))

(deftest ToolDeleteResponses
  (is (= #{200 400 404 500 :default} (set (keys admin/ToolDeleteResponses))))
  (is (nil? (get-in admin/ToolDeleteResponses [200 :body])))
  (valid (get-in admin/ToolDeleteResponses [400 :body]) {:error_code "ERR_NOT_WRITEABLE"})
  (valid (get-in admin/ToolDeleteResponses [404 :body]) {:error_code "ERR_NOT_FOUND"}))

(deftest ToolDetailsResponses
  (is (= #{200 404 500 :default} (set (keys admin/ToolDetailsResponses))))
  (is (= tools/ToolDetails (get-in admin/ToolDetailsResponses [200 :body])))
  (valid (get-in admin/ToolDetailsResponses [200 :body]) tool-details)
  (valid (get-in admin/ToolDetailsResponses [404 :body]) {:error_code "ERR_NOT_FOUND"}))

(deftest ToolsImportResponses
  (is (= #{200 400 500 :default} (set (keys admin/ToolsImportResponses))))
  (is (= admin/ToolIdsList (get-in admin/ToolsImportResponses [200 :body])))
  (valid (get-in admin/ToolsImportResponses [400 :body]) {:error_code "ERR_EXISTS"}))

(deftest ToolPublishResponses
  (is (= #{200 400 404 500 :default} (set (keys admin/ToolPublishResponses))))
  (is (= tools/ToolDetails (get-in admin/ToolPublishResponses [200 :body])))
  (valid (get-in admin/ToolPublishResponses [400 :body]) {:error_code "ERR_NOT_WRITEABLE"})
  (valid (get-in admin/ToolPublishResponses [404 :body]) {:error_code "ERR_NOT_FOUND"}))

(deftest ToolUpdateResponses
  (is (= #{200 400 404 500 :default} (set (keys admin/ToolUpdateResponses))))
  (is (= tools/ToolDetails (get-in admin/ToolUpdateResponses [200 :body])))
  (valid (get-in admin/ToolUpdateResponses [500 :body]) {:error_code "ERR_UNCHECKED_EXCEPTION"})
  (valid (get-in admin/ToolUpdateResponses [404 :body]) {:error_code "ERR_NOT_FOUND"}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.tools.admin))
