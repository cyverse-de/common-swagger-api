(ns common-swagger-api.malli.apps.permission-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.apps.permission :as permission]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def subject {:id "user123" :source_id "ldap"})

(def error {:error_code "ERR_NOT_FOUND"})

(def qualified-app-id {:system_id "de" :app_id "app-id-123"})

(def subject-permission {:subject subject :permission "read"})

(def app-permission-element (assoc qualified-app-id :permissions [subject-permission]))

(def app-sharing-request-element (assoc qualified-app-id :permission "own"))

(def app-sharing-response-element
  (assoc app-sharing-request-element :app_name "BLAST" :success true))

(def subject-app-sharing-request {:subject subject :apps [app-sharing-request-element]})

(def subject-app-sharing-response {:subject subject :apps [app-sharing-response-element]})

(def app-unsharing-response-element (assoc qualified-app-id :app_name "BLAST" :success true))

(def subject-app-unsharing-request {:subject subject :apps [qualified-app-id]})

(def subject-app-unsharing-response {:subject subject :apps [app-unsharing-response-element]})

(def analysis-id #uuid "df011ebc-4a79-4d7e-8d72-62d21a83cb43")

(def analysis-permission-element
  {:id analysis-id :name "BLAST Alignment" :permissions [subject-permission]})

(def analysis-sharing-request-element {:analysis_id analysis-id :permission "write"})

(def analysis-sharing-response-element
  (assoc analysis-sharing-request-element :analysis_name "BLAST Alignment" :ok true))

(def subject-analysis-sharing-request {:subject subject :analyses [analysis-sharing-request-element]})

(def subject-analysis-sharing-response {:subject subject :analyses [analysis-sharing-response-element]})

(def analysis-unsharing-response-element
  {:analysis_id analysis-id :analysis_name "BLAST Alignment" :ok true})

(def subject-analysis-unsharing-request {:subject subject :analyses [analysis-id]})

(def subject-analysis-unsharing-response {:subject subject :analyses [analysis-unsharing-response-element]})

(def tool-id #uuid "371a6f9d-2069-4de2-9f2e-062d464197d1")

(def tool-permission-element {:id tool-id :name "JupyterLab" :permissions [subject-permission]})

(def tool-sharing-request-element {:tool_id tool-id :permission "read"})

(def tool-sharing-response-element
  (assoc tool-sharing-request-element :tool_name "JupyterLab" :success true))

(def subject-tool-sharing-request {:subject subject :tools [tool-sharing-request-element]})

(def subject-tool-sharing-response {:subject subject :tools [tool-sharing-response-element]})

(def tool-unsharing-response-element {:tool_id tool-id :tool_name "JupyterLab" :success true})

(def subject-tool-unsharing-request {:subject subject :tools [tool-id]})

(def subject-tool-unsharing-response {:subject subject :tools [tool-unsharing-response-element]})

(deftest AppPermissionEnum
  (valid permission/AppPermissionEnum "read" "write" "own" "")
  (valid permission/AnalysisPermissionEnum "own")
  (valid permission/ToolPermissionEnum "write")
  (invalid permission/AppPermissionEnum "admin" :read nil))

(deftest PermissionListerQueryParams
  (valid permission/PermissionListerQueryParams {} {:full-listing true})
  (invalid permission/PermissionListerQueryParams {:full-listing "true"} {:extra 1}))

(deftest AppPermissionListingRequest
  (valid permission/AppPermissionListingRequest {:apps []} {:apps [qualified-app-id]})
  (invalid permission/AppPermissionListingRequest
           {}
           {:apps [{}]}
           {:apps qualified-app-id}
           {:apps [] :extra 1}))

(deftest SubjectPermissionListElement
  (valid permission/SubjectPermissionListElement subject-permission)
  (invalid permission/SubjectPermissionListElement
           {}
           (dissoc subject-permission :subject)
           (assoc subject-permission :permission "admin")
           (assoc subject-permission :extra 1)))

(deftest AppPermissionListElement
  (valid permission/AppPermissionListElement
         app-permission-element
         (assoc app-permission-element :name "BLAST"))
  (invalid permission/AppPermissionListElement
           {}
           (dissoc app-permission-element :app_id)
           (assoc app-permission-element :name "")
           (assoc app-permission-element :extra 1)))

(deftest AppPermissionListing
  (valid permission/AppPermissionListing {:apps []} {:apps [app-permission-element]})
  (invalid permission/AppPermissionListing {} {:apps [{}]} {:apps [] :extra 1}))

(deftest AppSharingRequestElement
  (valid permission/AppSharingRequestElement app-sharing-request-element)
  (invalid permission/AppSharingRequestElement
           {}
           qualified-app-id
           (assoc app-sharing-request-element :permission "admin")
           (assoc app-sharing-request-element :extra 1)))

(deftest AppSharingResponseElement
  (valid permission/AppSharingResponseElement
         app-sharing-response-element
         (assoc app-sharing-response-element :error error))
  (invalid permission/AppSharingResponseElement
           {}
           app-sharing-request-element
           (assoc app-sharing-response-element :success "yes")
           (assoc app-sharing-response-element :error {})))

(deftest SubjectAppSharingRequestElement
  (valid permission/SubjectAppSharingRequestElement subject-app-sharing-request)
  (invalid permission/SubjectAppSharingRequestElement
           {}
           (dissoc subject-app-sharing-request :apps)
           (assoc subject-app-sharing-request :apps [{}])
           (assoc subject-app-sharing-request :extra 1)))

(deftest SubjectAppSharingResponseElement
  (valid permission/SubjectAppSharingResponseElement subject-app-sharing-response)
  (invalid permission/SubjectAppSharingResponseElement
           {}
           subject-app-sharing-request
           (assoc subject-app-sharing-response :subject {})
           (assoc subject-app-sharing-response :extra 1)))

(deftest AppSharingRequest
  (valid permission/AppSharingRequest {:sharing []} {:sharing [subject-app-sharing-request]})
  (invalid permission/AppSharingRequest {} {:sharing [{}]} {:sharing [] :extra 1}))

(deftest AppSharingResponse
  (valid permission/AppSharingResponse {:sharing []} {:sharing [subject-app-sharing-response]})
  (invalid permission/AppSharingResponse {} {:sharing [{}]} {:sharing [] :extra 1}))

(deftest AppUnsharingResponseElement
  (valid permission/AppUnsharingResponseElement
         app-unsharing-response-element
         (assoc app-unsharing-response-element :error error))
  (invalid permission/AppUnsharingResponseElement
           {}
           qualified-app-id
           (assoc app-unsharing-response-element :app_name "")
           (assoc app-unsharing-response-element :extra 1)))

(deftest SubjectAppUnsharingRequestElement
  (valid permission/SubjectAppUnsharingRequestElement subject-app-unsharing-request)
  (invalid permission/SubjectAppUnsharingRequestElement
           {}
           (dissoc subject-app-unsharing-request :subject)
           (assoc subject-app-unsharing-request :apps [{}])
           (assoc subject-app-unsharing-request :extra 1)))

(deftest SubjectAppUnsharingResponseElement
  (valid permission/SubjectAppUnsharingResponseElement subject-app-unsharing-response)
  (invalid permission/SubjectAppUnsharingResponseElement
           {}
           subject-app-unsharing-request
           (assoc subject-app-unsharing-response :analyses [])
           (assoc subject-app-unsharing-response :extra 1)))

(deftest AppUnsharingRequest
  (valid permission/AppUnsharingRequest {:unsharing []} {:unsharing [subject-app-unsharing-request]})
  (invalid permission/AppUnsharingRequest {} {:unsharing [{}]} {:unsharing [] :extra 1}))

(deftest AppUnsharingResponse
  (valid permission/AppUnsharingResponse {:unsharing []} {:unsharing [subject-app-unsharing-response]})
  (invalid permission/AppUnsharingResponse {} {:unsharing [{}]} {:unsharing [] :extra 1}))

(deftest AnalysisIdList
  (valid permission/AnalysisIdList {:analyses []} {:analyses [analysis-id]})
  (invalid permission/AnalysisIdList {} {:analyses [(str analysis-id)]} {:analyses [] :extra 1}))

(deftest AnalysisPermissionListElement
  (valid permission/AnalysisPermissionListElement analysis-permission-element)
  (invalid permission/AnalysisPermissionListElement
           {}
           (dissoc analysis-permission-element :name)
           (assoc analysis-permission-element :id (str analysis-id))
           (assoc analysis-permission-element :extra 1)))

(deftest AnalysisPermissionListing
  (valid permission/AnalysisPermissionListing {:analyses []} {:analyses [analysis-permission-element]})
  (invalid permission/AnalysisPermissionListing {} {:analyses [{}]} {:analyses [] :extra 1}))

(deftest AnalysisSharingRequestElement
  (valid permission/AnalysisSharingRequestElement analysis-sharing-request-element)
  (invalid permission/AnalysisSharingRequestElement
           {}
           (dissoc analysis-sharing-request-element :permission)
           (assoc analysis-sharing-request-element :permission "admin")
           (assoc analysis-sharing-request-element :extra 1)))

(deftest AnalysisSharingResponseElement
  (valid permission/AnalysisSharingResponseElement
         analysis-sharing-response-element
         (assoc analysis-sharing-response-element :error error))
  (invalid permission/AnalysisSharingResponseElement
           {}
           analysis-sharing-request-element
           (assoc analysis-sharing-response-element :ok "yes")
           (assoc analysis-sharing-response-element :extra 1)))

(deftest SubjectAnalysisSharingRequestElement
  (valid permission/SubjectAnalysisSharingRequestElement subject-analysis-sharing-request)
  (invalid permission/SubjectAnalysisSharingRequestElement
           {}
           (dissoc subject-analysis-sharing-request :analyses)
           (assoc subject-analysis-sharing-request :analyses [{}])
           (assoc subject-analysis-sharing-request :extra 1)))

(deftest SubjectAnalysisSharingResponseElement
  (valid permission/SubjectAnalysisSharingResponseElement subject-analysis-sharing-response)
  (invalid permission/SubjectAnalysisSharingResponseElement
           {}
           subject-analysis-sharing-request
           (assoc subject-analysis-sharing-response :subject {})
           (assoc subject-analysis-sharing-response :extra 1)))

(deftest AnalysisSharingRequest
  (valid permission/AnalysisSharingRequest {:sharing []} {:sharing [subject-analysis-sharing-request]})
  (invalid permission/AnalysisSharingRequest {} {:sharing [{}]} {:sharing [] :extra 1}))

(deftest AnalysisSharingResponse
  (valid permission/AnalysisSharingResponse
         {:sharing []}
         {:sharing [subject-analysis-sharing-response] :asyncTaskID analysis-id})
  (invalid permission/AnalysisSharingResponse {} {:sharing [{}]} {:sharing [] :asyncTaskID "x"}))

(deftest AnalysisUnsharingResponseElement
  (valid permission/AnalysisUnsharingResponseElement
         analysis-unsharing-response-element
         (assoc analysis-unsharing-response-element :error error))
  (invalid permission/AnalysisUnsharingResponseElement
           {}
           (dissoc analysis-unsharing-response-element :ok)
           (assoc analysis-unsharing-response-element :analysis_name "")
           (assoc analysis-unsharing-response-element :extra 1)))

(deftest SubjectAnalysisUnsharingRequestElement
  (valid permission/SubjectAnalysisUnsharingRequestElement subject-analysis-unsharing-request)
  (invalid permission/SubjectAnalysisUnsharingRequestElement
           {}
           (dissoc subject-analysis-unsharing-request :analyses)
           (assoc subject-analysis-unsharing-request :analyses [(str analysis-id)])
           (assoc subject-analysis-unsharing-request :extra 1)))

(deftest SubjectAnalysisUnsharingResponseElement
  (valid permission/SubjectAnalysisUnsharingResponseElement subject-analysis-unsharing-response)
  (invalid permission/SubjectAnalysisUnsharingResponseElement
           {}
           subject-analysis-unsharing-request
           (assoc subject-analysis-unsharing-response :analyses [{}])
           (assoc subject-analysis-unsharing-response :extra 1)))

(deftest AnalysisUnsharingRequest
  (valid permission/AnalysisUnsharingRequest {:unsharing []} {:unsharing [subject-analysis-unsharing-request]})
  (invalid permission/AnalysisUnsharingRequest {} {:unsharing [{}]} {:unsharing [] :extra 1}))

(deftest AnalysisUnsharingResponse
  (valid permission/AnalysisUnsharingResponse
         {:unsharing []}
         {:unsharing [subject-analysis-unsharing-response] :asyncTaskID analysis-id})
  (invalid permission/AnalysisUnsharingResponse {} {:unsharing [{}]} {:unsharing [] :asyncTaskID "x"}))

(deftest ToolIdList
  (valid permission/ToolIdList {:tools []} {:tools [tool-id]})
  (invalid permission/ToolIdList {} {:tools [(str tool-id)]} {:tools [] :extra 1}))

(deftest ToolPermissionListElement
  (valid permission/ToolPermissionListElement tool-permission-element)
  (invalid permission/ToolPermissionListElement
           {}
           (dissoc tool-permission-element :permissions)
           (assoc tool-permission-element :permissions subject-permission)
           (assoc tool-permission-element :extra 1)))

(deftest ToolPermissionListing
  (valid permission/ToolPermissionListing {:tools []} {:tools [tool-permission-element]})
  (invalid permission/ToolPermissionListing {} {:tools [{}]} {:tools [] :extra 1}))

(deftest ToolSharingRequestElement
  (valid permission/ToolSharingRequestElement tool-sharing-request-element)
  (invalid permission/ToolSharingRequestElement
           {}
           (dissoc tool-sharing-request-element :tool_id)
           (assoc tool-sharing-request-element :permission "admin")
           (assoc tool-sharing-request-element :extra 1)))

(deftest ToolSharingResponseElement
  (valid permission/ToolSharingResponseElement
         tool-sharing-response-element
         (assoc tool-sharing-response-element :error error))
  (invalid permission/ToolSharingResponseElement
           {}
           tool-sharing-request-element
           (assoc tool-sharing-response-element :tool_name "")
           (assoc tool-sharing-response-element :extra 1)))

(deftest SubjectToolSharingRequestElement
  (valid permission/SubjectToolSharingRequestElement subject-tool-sharing-request)
  (invalid permission/SubjectToolSharingRequestElement
           {}
           (dissoc subject-tool-sharing-request :tools)
           (assoc subject-tool-sharing-request :tools [{}])
           (assoc subject-tool-sharing-request :extra 1)))

(deftest SubjectToolSharingResponseElement
  (valid permission/SubjectToolSharingResponseElement subject-tool-sharing-response)
  (invalid permission/SubjectToolSharingResponseElement
           {}
           subject-tool-sharing-request
           (assoc subject-tool-sharing-response :subject {})
           (assoc subject-tool-sharing-response :extra 1)))

(deftest ToolSharingRequest
  (valid permission/ToolSharingRequest {:sharing []} {:sharing [subject-tool-sharing-request]})
  (invalid permission/ToolSharingRequest {} {:sharing [{}]} {:sharing [] :extra 1}))

(deftest ToolSharingResponse
  (valid permission/ToolSharingResponse {:sharing []} {:sharing [subject-tool-sharing-response]})
  (invalid permission/ToolSharingResponse {} {:sharing [{}]} {:sharing [] :extra 1}))

(deftest ToolUnsharingResponseElement
  (valid permission/ToolUnsharingResponseElement
         tool-unsharing-response-element
         (assoc tool-unsharing-response-element :error error))
  (invalid permission/ToolUnsharingResponseElement
           {}
           (dissoc tool-unsharing-response-element :success)
           (assoc tool-unsharing-response-element :tool_id (str tool-id))
           (assoc tool-unsharing-response-element :extra 1)))

(deftest SubjectToolUnsharingRequestElement
  (valid permission/SubjectToolUnsharingRequestElement subject-tool-unsharing-request)
  (invalid permission/SubjectToolUnsharingRequestElement
           {}
           (dissoc subject-tool-unsharing-request :tools)
           (assoc subject-tool-unsharing-request :tools [(str tool-id)])
           (assoc subject-tool-unsharing-request :extra 1)))

(deftest SubjectToolUnsharingResponseElement
  (valid permission/SubjectToolUnsharingResponseElement subject-tool-unsharing-response)
  (invalid permission/SubjectToolUnsharingResponseElement
           {}
           subject-tool-unsharing-request
           (assoc subject-tool-unsharing-response :tools [{}])
           (assoc subject-tool-unsharing-response :extra 1)))

(deftest ToolUnsharingRequest
  (valid permission/ToolUnsharingRequest {:unsharing []} {:unsharing [subject-tool-unsharing-request]})
  (invalid permission/ToolUnsharingRequest {} {:unsharing [{}]} {:unsharing [] :extra 1}))

(deftest ToolUnsharingResponse
  (valid permission/ToolUnsharingResponse {:unsharing []} {:unsharing [subject-tool-unsharing-response]})
  (invalid permission/ToolUnsharingResponse {} {:unsharing [{}]} {:unsharing [] :extra 1}))

(deftest ToolPermissionsListingResponses
  (is (= #{200 403 404 500 :default} (set (keys permission/ToolPermissionsListingResponses))))
  (is (= permission/ToolPermissionListing (get-in permission/ToolPermissionsListingResponses [200 :body])))
  (valid (get-in permission/ToolPermissionsListingResponses [403 :body]) {:error_code "ERR_FORBIDDEN"})
  (valid (get-in permission/ToolPermissionsListingResponses [404 :body]) {:error_code "ERR_NOT_FOUND"}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps.permission))
