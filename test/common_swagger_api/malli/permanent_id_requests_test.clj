(ns common-swagger-api.malli.permanent-id-requests-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.permanent-id-requests :as pid]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]
   [malli.json-schema :as js]))

(def request-id #uuid "18c3c84d-38ca-45a6-96d4-38541bf764b3")

(def request-base
  {:type         "DOI"
   :id           request-id
   :requested_by "janedoe"})

(def status
  {:status_date 1762296097000
   :updated_by  "janedoe"})

(def listing
  (assoc request-base
         :date_submitted 1763063226000
         :status         "Evaluation"
         :date_updated   1763067980000
         :updated_by     "example_user"))

(def status-code
  {:id          #uuid "e1ee158b-7511-4f21-9feb-ff608d5eb80b"
   :name        "Pending"
   :description "The curators are waiting for a response from the requesting user."})

(def request-type
  {:id          #uuid "f38d9305-ca90-4c0f-94b5-f7f243855734"
   :type        "DOI"
   :description "Data Object Identifier"})

(deftest PermanentIDRequestIdParam
  (valid pid/PermanentIDRequestIdParam request-id)
  (invalid pid/PermanentIDRequestIdParam (str request-id) nil))

(deftest PermanentID
  (valid pid/PermanentID {} {:permanent_id "https://doi.org/10.1093/nar/gkv416"})
  (invalid pid/PermanentID {:permanent_id 1} {:extra 1}))

(deftest PermanentIDRequestOrigPath
  (valid pid/PermanentIDRequestOrigPath {} {:original_path "/zone/home/janedoe/folder"})
  (invalid pid/PermanentIDRequestOrigPath {:original_path 1} {:extra 1}))

(deftest PermanentIDRequest
  (valid pid/PermanentIDRequest {:type "DOI"})
  (invalid pid/PermanentIDRequest {} {:type 1} {:type "DOI" :extra 1}))

(deftest PermanentIDRequestBase
  (valid pid/PermanentIDRequestBase
         request-base
         (assoc request-base :permanent_id "https://doi.org/10.1093/nar/gkv416" :original_path "/zone/home/a"))
  (invalid pid/PermanentIDRequestBase
           {}
           (dissoc request-base :requested_by)
           (assoc request-base :id (str request-id))
           (assoc request-base :extra 1)))

(deftest PermanentIDRequestStatusComments
  (valid pid/PermanentIDRequestStatusComments {} {:comments "Please fill out the metadata template."})
  (invalid pid/PermanentIDRequestStatusComments {:comments 1} {:extra 1}))

(deftest PermanentIDRequestStatusUpdate
  (valid pid/PermanentIDRequestStatusUpdate
         {}
         {:comments "Looks good." :permanent_id "https://doi.org/10.1093/nar/gkv416" :status "Completion"})
  (invalid pid/PermanentIDRequestStatusUpdate {:status 1} {:permanent_id 1} {:extra 1}))

(deftest PermanentIDRequestStatus
  (valid pid/PermanentIDRequestStatus
         status
         (assoc status :status "Completion" :comments "Done." :permanent_id "https://doi.org/10"))
  (invalid pid/PermanentIDRequestStatus
           {}
           (dissoc status :updated_by)
           (assoc status :status_date "1762296097000")
           (assoc status :extra 1)))

(deftest PermanentIDRequestDetails
  (valid pid/PermanentIDRequestDetails
         (assoc request-base :history [])
         (assoc request-base :history [status]))
  (invalid pid/PermanentIDRequestDetails
           {}
           request-base
           (assoc request-base :history [{}])
           (assoc request-base :history [] :extra 1)))

(deftest PermanentIDRequestListing
  (valid pid/PermanentIDRequestListing listing (assoc listing :original_path "/zone/home/janedoe/folder"))
  (invalid pid/PermanentIDRequestListing
           {}
           request-base
           (assoc listing :date_updated "1763067980000")
           (assoc listing :extra 1)))

(deftest PermanentIDRequestList
  (valid pid/PermanentIDRequestList {:requests [] :total 0} {:requests [listing] :total 1})
  (invalid pid/PermanentIDRequestList
           {}
           {:requests []}
           {:requests [] :total "0"}
           {:requests [] :total 0 :extra 1}))

(deftest PermanentIDRequestListPagingParams
  (valid pid/PermanentIDRequestListPagingParams
         {}
         {:limit 50 :offset 0 :sort-field :date_submitted :sort-dir "ASC"}
         {:statuses ["Rejected" "Completion"]})
  (invalid pid/PermanentIDRequestListPagingParams
           {:sort-field :nope}
           {:sort-field "date_submitted"}
           {:statuses "Rejected"}
           {:extra 1})
  (is (= :date_submitted
         (get-in (js/transform pid/PermanentIDRequestListPagingParams) [:properties :sort-field :example]))))

(deftest PermanentIDRequestStatusCode
  (valid pid/PermanentIDRequestStatusCode status-code)
  (invalid pid/PermanentIDRequestStatusCode
           {}
           (dissoc status-code :description)
           (assoc status-code :id (str (:id status-code)))
           (assoc status-code :extra 1)))

(deftest PermanentIDRequestStatusCodeList
  (valid pid/PermanentIDRequestStatusCodeList {:status_codes []} {:status_codes [status-code]})
  (invalid pid/PermanentIDRequestStatusCodeList {} {:status_codes [{}]} {:status_codes [] :extra 1}))

(deftest PermanentIDRequestType
  (valid pid/PermanentIDRequestType request-type)
  (invalid pid/PermanentIDRequestType
           {}
           (dissoc request-type :type)
           (assoc request-type :id (str (:id request-type)))
           (assoc request-type :extra 1)))

(deftest PermanentIDRequestTypeList
  (valid pid/PermanentIDRequestTypeList {:request_types []} {:request_types [request-type]})
  (invalid pid/PermanentIDRequestTypeList {} {:request_types [{}]} {:request_types [] :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.permanent-id-requests))
