(ns common-swagger-api.malli-test
  (:require
   [clojure.test :refer [are deftest is]]
   [common-swagger-api.malli :as m]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]
   [malli.core :as malli]
   [malli.json-schema :as js]
   [malli.util :as mu]))

(deftest add-enum-values
  (are [expected schema vs] (mu/equals expected (apply m/add-enum-values schema vs))
    [:enum :foo :bar]                     [:enum :foo]                     [:bar]
    [:enum :foo :bar :baz]                [:enum :foo]                     [:bar :baz]
    [:enum :foo]                          [:enum :foo]                     []
    [:enum {:description "t"} :foo :bar]  [:enum {:description "t"} :foo]  [:bar])
  (is (thrown? Exception (m/add-enum-values [:map [:foo :string]] :bar))))

(deftest NonBlankString
  (valid m/NonBlankString "hello" "a")
  (invalid m/NonBlankString "" "   " nil 1))

(deftest StandardUserQueryParams
  (valid m/StandardUserQueryParams {:user "ipctest"})
  (invalid m/StandardUserQueryParams {} {:user ""} {:user "ipctest" :extra 1}))

(deftest StatusParams
  (valid m/StatusParams {} {:expecting "apps"})
  (invalid m/StatusParams {:expecting ""} {:unknown 1}))

(deftest PagingParams
  (valid m/PagingParams {} {:limit 1} {:offset 0} {:limit 50 :offset 10 :sort-field "name" :sort-dir "DESC"})
  (invalid m/PagingParams {:limit 0} {:limit -1} {:offset -1} {:sort-dir "asc"} {:limit "10"} {:extra 1}))

(def status
  {:service "apps" :description "An API" :version "1.0.0" :docs-url "http://apps/docs"})

(deftest StatusResponse
  (valid m/StatusResponse status (assoc status :expecting "apps"))
  (invalid m/StatusResponse {} (dissoc status :version) (assoc status :service "") (assoc status :extra 1)))

(deftest ErrorResponse
  (valid m/ErrorResponse {:error_code "ERR_X"} {:error_code "ERR_X" :reason "why"})
  (invalid m/ErrorResponse {} {:error_code ""} {:error_code "ERR_X" :reason ""}))

(deftest error-code-variants
  (are [schema code] (and (malli/validate schema {:error_code code})
                          (not (malli/validate schema {:error_code "ERR_OTHER"})))
    m/ErrorResponseExists          "ERR_EXISTS"
    m/ErrorResponseNotWritable     "ERR_NOT_WRITEABLE"
    m/ErrorResponseForbidden       "ERR_FORBIDDEN"
    m/ErrorResponseNotFound        "ERR_NOT_FOUND"
    m/ErrorResponseIllegalArgument "ERR_ILLEGAL_ARGUMENT"))

(deftest ErrorResponseUnchecked
  (valid m/ErrorResponseUnchecked
         {:error_code "ERR_UNCHECKED_EXCEPTION"}
         {:error_code "ERR_SCHEMA_VALIDATION" :reason {:detail 1}})
  (invalid m/ErrorResponseUnchecked {:error_code "ERR_NOT_FOUND"}))

(deftest transform-enum
  (is (mu/equals [:enum "a" "b"] (m/transform-enum [:enum :a :b] name)))
  (is (= {:description "d"} (malli/properties (m/transform-enum [:enum {:description "d"} :a] name)))))

(deftest doc-only
  (let [schema (m/doc-only [:map-of :keyword :string] [:map [:api-name :string]])]
    (valid schema {:github "http://x"})
    (invalid schema {:github 1})
    (is (= {:type "object" :properties {:api-name {:type "string"}} :required [:api-name]}
           (js/transform schema)))))

(deftest CommonResponses
  (is (= #{500 :default} (set (keys m/CommonResponses))))
  (is (= m/ErrorResponseUnchecked (get-in m/CommonResponses [500 :body]))))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli))
