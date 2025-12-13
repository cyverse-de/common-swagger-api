(ns common-swagger-api.malli
  (:require
   [clojure-commons.error-codes :as ce]
   [malli.core :as m]
   [malli.util :as mu]))

(defn add-enum-values
  "Adds values to an existing enumeration form or schema, throwing an error if the form or schema does not appear to be
   an enumeration type."
  [schema & vs]
  (when-not (= (m/type schema) :enum)
    (throw (ex-info "provided schema is not an enum" {:schema schema})))
  (as-> schema s
    (if (m/schema? s) (m/form s) s)
    (into s vs)
    (m/schema s)))

(def NonBlankString [:re "\\S+"])

(def StandardUserQueryParams
  [:map {:closed true}
   [:user
    {:description         "The username of the authenticated, requesting user"
     :json-schema/example "ipctest"}
    NonBlankString]])

(def StatusParams
  [:map {:closed true}
   [:expecting
    {:description         (str "The service which the requesting client is expecting to see here. "
                               "Should throw a 500 error if provided and does not match the actual "
                               "service running here.")
     :json-schema/example "apps"
     :optional            true}
    NonBlankString]])

(def SortFieldDocs
  (str "Sorts the results in the listing array by the given field, before limits and offsets are applied. "
       "See http://www.postgresql.org/docs/9.2/interactive/queries-order.html"))

(def PagingParams
  [:map {:closed true}
   [:limit
    {:description         (str "Limits the response to X number of results in the listing array. "
                               "See http://www.postgresql.org/docs/9.2/interactive/queries-limit.html")
     :json-schema/example 50
     :optional            true}
    [:and :int [:fn pos?]]]

   [:offset
    {:description         (str "Skips the first X number of results in the listing array. "
                               "See http://www.postgresql.org/docs/9.2/interactive/queries-limit.html")
     :json-schema/example 0
     :optional            true}
    [:and :int [:fn (comp not neg?)]]]

   [:sort-field
    {:description         SortFieldDocs
     :json-schema/example "name"
     :optional            true}
    :string]

   [:sort-dir
    {:description         (str "Only used when sort-field is present. Sorts the results in either ascending "
                               "(`ASC`) or descending (`DESC`) order, before limits and offsets are applied. "
                               "Defaults to `ASC`. "
                               "See http://www.postgresql.org/docs/9.2/interactive/queries-order.html")
     :json-schema/example "ASC"
     :optional            true}
    [:enum "ASC" "DESC"]]])

(def StatusResponse
  [:map {:closed true}
   [:service
    {:description         "The name of the service"
     :json-schema/example "example-api"}
    NonBlankString]

   [:description
    {:description         "The service description"
     :json-schema/example "An API"}
    NonBlankString]

   [:version
    {:description         "The service version"
     :json-schema/example "1.2.3"}
    NonBlankString]

   [:docs-url
    {:description         "The URL for the API documentation"
     :json-schema/example "http://example-api/docs"}
    NonBlankString]

   [:expecting
    {:description         "The service the requesting client was expecting to see, if any"
     :json-schema/example "example-service"
     :optional            true}
    :string]])

(def ErrorResponse
  [:map {:closed true}
   [:error_code
    {:description         "The code identifying the type of error"
     :json-schema/example "ERR_NOT_FOUND"}
    NonBlankString]

   [:reason
    {:description         "A brief description of the reason for the error"
     :json-schema/example "The requested resource was not found"
     :optional            true}
    NonBlankString]])

(def ErrorResponseExists
  (mu/assoc ErrorResponse :error_code
    [:enum
     {:description         "Exists error code"
      :json-schema/example ce/ERR_EXISTS}
     ce/ERR_EXISTS]))

(def ErrorResponseNotWritable
  (mu/assoc ErrorResponse :error_code
    [:enum
     {:description         "Not Writeable error code"
      :json-schema/example ce/ERR_NOT_WRITEABLE}
     ce/ERR_NOT_WRITEABLE]))

(def ErrorResponseForbidden
  (mu/assoc ErrorResponse :error_code
    [:enum
     {:description         "Insufficient privileges error code"
      :json-schema/example ce/ERR_FORBIDDEN}
     ce/ERR_FORBIDDEN]))

(def ErrorResponseNotFound
  (mu/assoc ErrorResponse :error_code
    [:enum
     {:description         "Not Found error code"
      :json-schema/example ce/ERR_NOT_FOUND}
     ce/ERR_NOT_FOUND]))

(def ErrorResponseIllegalArgument
  (mu/assoc ErrorResponse :error_code
    [:enum
     {:description         "Illegal Argument error code"
      :json-schema/example ce/ERR_ILLEGAL_ARGUMENT}
     ce/ERR_ILLEGAL_ARGUMENT]))

(def ErrorResponseUnchecked
  [:map {:closed true}
   [:error_code
    {:description         "Response schema validation and Unchecked error codes"
     :json-schema/example ce/ERR_UNCHECKED_EXCEPTION}
    [:enum ce/ERR_UNCHECKED_EXCEPTION ce/ERR_SCHEMA_VALIDATION]]

   [:reason
    {:description         "A brief text or object describing the reason for the error"
     :json-schema/example "An unexpected error occurred"
     :optional            true}
    :any]])
