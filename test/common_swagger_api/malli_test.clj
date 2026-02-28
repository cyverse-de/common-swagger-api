(ns common-swagger-api.malli-test
  (:require
   [clojure.test :refer [are deftest is testing]]
   [common-swagger-api.malli :as m]
   [malli.core :as malli]
   [malli.error :as me]
   [malli.util :as mu]))

(deftest test-add-enum-values
  (testing "valid attempts to add values to an enumeration"
   (are [expected schema vs] (mu/equals expected (apply m/add-enum-values schema vs))
    [:enum :foo :bar] [:enum :foo] [:bar]
    [:enum :foo :bar :baz] [:enum :foo] [:bar :baz]
    [:enum :foo] [:enum :foo] []
    [:enum {:description "test"} :foo :bar] [:enum {:description "test"} :foo] [:bar]))

  (testing "property preservation when enumeration values are added"
    (is (= {:description "test"} (malli/properties (m/add-enum-values [:enum {:description "test"} :foo] :bar)))))

  (testing "schema type check"
    (is (thrown? Exception (m/add-enum-values [:map [:foo :string]] :bar)))))

(defn valid? [schema data]
  (malli/validate schema data))

(defn explain [schema data]
  (-> (malli/explain schema data)
      (me/humanize)))

(deftest test-NonBlankString
  (testing "NonBlankString validation"
    (is (valid? m/NonBlankString "hello"))
    (is (valid? m/NonBlankString "a"))
    (is (not (valid? m/NonBlankString "")))
    (is (not (valid? m/NonBlankString "   ")))
    (is (not (valid? m/NonBlankString nil)))))

(deftest test-StandardUserQueryParams
  (testing "StandardUserQueryParams validation"
    (testing "valid params"
      (is (valid? m/StandardUserQueryParams
                  {:user "ipctest"}))
      (is (valid? m/StandardUserQueryParams
                  {:user "john.doe"})))

    (testing "invalid params"
      (is (not (valid? m/StandardUserQueryParams {})))
      (is (not (valid? m/StandardUserQueryParams {:user ""})))
      (is (not (valid? m/StandardUserQueryParams {:user "   "})))
      (is (not (valid? m/StandardUserQueryParams {:user "valid" :extra "field"}))))))

(deftest test-StatusParams
  (testing "StatusParams validation"
    (testing "valid params"
      (is (valid? m/StatusParams {}))
      (is (valid? m/StatusParams {:expecting "apps"}))
      (is (valid? m/StatusParams {:expecting "analyses"})))

    (testing "invalid params"
      (is (not (valid? m/StatusParams {:expecting ""})))
      (is (not (valid? m/StatusParams {:expecting "   "})))
      (is (not (valid? m/StatusParams {:unknown "field"}))))))

(deftest test-PagingParams
  (testing "PagingParams validation"
    (testing "valid params"
      (is (valid? m/PagingParams {}))
      (is (valid? m/PagingParams {:limit 50}))
      (is (valid? m/PagingParams {:offset 0}))
      (is (valid? m/PagingParams {:limit 100 :offset 50}))
      (is (valid? m/PagingParams {:sort-field "name"}))
      (is (valid? m/PagingParams {:sort-dir "ASC"}))
      (is (valid? m/PagingParams {:sort-dir "DESC"}))
      (is (valid? m/PagingParams {:limit 50
                                   :offset 0
                                   :sort-field "name"
                                   :sort-dir "ASC"})))

    (testing "invalid params"
      (is (not (valid? m/PagingParams {:limit 0})))
      (is (not (valid? m/PagingParams {:limit -1})))
      (is (not (valid? m/PagingParams {:offset -1})))
      (is (not (valid? m/PagingParams {:sort-dir "invalid"})))
      (is (not (valid? m/PagingParams {:sort-dir "asc"})))
      (is (not (valid? m/PagingParams {:unknown "field"}))))))

(deftest test-StatusResponse
  (testing "StatusResponse validation"
    (testing "valid response"
      (is (valid? m/StatusResponse
                  {:service "example-api"
                   :description "An API"
                   :version "1.2.3"
                   :docs-url "http://example-api/docs"}))
      (is (valid? m/StatusResponse
                  {:service "example-api"
                   :description "An API"
                   :version "1.2.3"
                   :docs-url "http://example-api/docs"
                   :expecting "example-service"})))

    (testing "invalid response"
      (is (not (valid? m/StatusResponse {})))
      (is (not (valid? m/StatusResponse
                       {:service ""
                        :description "An API"
                        :version "1.2.3"
                        :docs-url "http://example-api/docs"})))
      (is (not (valid? m/StatusResponse
                       {:service "example-api"
                        :description "An API"
                        :version "1.2.3"
                        :docs-url "http://example-api/docs"
                        :extra "field"}))))))

(deftest test-ErrorResponse
  (testing "ErrorResponse validation"
    (testing "valid response"
      (is (valid? m/ErrorResponse
                  {:error_code "ERR_NOT_FOUND"}))
      (is (valid? m/ErrorResponse
                  {:error_code "ERR_NOT_FOUND"
                   :reason "The requested resource was not found"})))

    (testing "invalid response"
      (is (not (valid? m/ErrorResponse {})))
      (is (not (valid? m/ErrorResponse {:error_code ""})))
      (is (not (valid? m/ErrorResponse {:error_code "   "})))
      (is (not (valid? m/ErrorResponse
                       {:error_code "ERR_NOT_FOUND"
                        :extra "field"}))))))

(deftest test-ErrorResponseExists
  (testing "ErrorResponseExists validation"
    (testing "valid response"
      (is (valid? m/ErrorResponseExists
                  {:error_code "ERR_EXISTS"}))
      (is (valid? m/ErrorResponseExists
                  {:error_code "ERR_EXISTS"
                   :reason "Resource already exists"})))

    (testing "invalid response"
      (is (not (valid? m/ErrorResponseExists
                       {:error_code "ERR_NOT_FOUND"})))
      (is (not (valid? m/ErrorResponseExists
                       {:error_code "INVALID_CODE"}))))))

(deftest test-ErrorResponseNotWritable
  (testing "ErrorResponseNotWritable validation"
    (is (valid? m/ErrorResponseNotWritable
                {:error_code "ERR_NOT_WRITEABLE"}))
    (is (not (valid? m/ErrorResponseNotWritable
                     {:error_code "ERR_EXISTS"})))))

(deftest test-ErrorResponseForbidden
  (testing "ErrorResponseForbidden validation"
    (is (valid? m/ErrorResponseForbidden
                {:error_code "ERR_FORBIDDEN"}))
    (is (not (valid? m/ErrorResponseForbidden
                     {:error_code "ERR_NOT_FOUND"})))))

(deftest test-ErrorResponseNotFound
  (testing "ErrorResponseNotFound validation"
    (is (valid? m/ErrorResponseNotFound
                {:error_code "ERR_NOT_FOUND"}))
    (is (not (valid? m/ErrorResponseNotFound
                     {:error_code "ERR_EXISTS"})))))

(deftest test-ErrorResponseIllegalArgument
  (testing "ErrorResponseIllegalArgument validation"
    (is (valid? m/ErrorResponseIllegalArgument
                {:error_code "ERR_ILLEGAL_ARGUMENT"}))
    (is (not (valid? m/ErrorResponseIllegalArgument
                     {:error_code "ERR_NOT_FOUND"})))))

(deftest test-ErrorResponseUnchecked
  (testing "ErrorResponseUnchecked validation"
    (testing "valid response"
      (is (valid? m/ErrorResponseUnchecked
                  {:error_code "ERR_UNCHECKED_EXCEPTION"}))
      (is (valid? m/ErrorResponseUnchecked
                  {:error_code "ERR_SCHEMA_VALIDATION"}))
      (is (valid? m/ErrorResponseUnchecked
                  {:error_code "ERR_UNCHECKED_EXCEPTION"
                   :reason "An unexpected error occurred"}))
      (is (valid? m/ErrorResponseUnchecked
                  {:error_code "ERR_UNCHECKED_EXCEPTION"
                   :reason {:details "Complex error object"}})))

    (testing "invalid response"
      (is (not (valid? m/ErrorResponseUnchecked
                       {:error_code "ERR_NOT_FOUND"})))
      (is (not (valid? m/ErrorResponseUnchecked
                       {:error_code "INVALID_CODE"}))))))
