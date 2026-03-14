(ns common-swagger-api.schema
  (:require
   [clojure-commons.error-codes :as ce]
   [clojure.string :refer [blank?]]
   [compojure.api.sweet]
   [potemkin :refer [import-vars]]
   [ring.swagger.json-schema :as json-schema]
   [schema-tools.core :as st]
   [schema.core :as s]
   [schema.spec.core :as spec :include-macros true]
   [schema.spec.variant :as variant]))

(import-vars
  [compojure.api.sweet
   api
   defapi

   describe

   swagger-routes

   routes
   defroutes
   undocumented
   middleware
   context

   GET
   ANY
   HEAD
   PATCH
   DELETE
   OPTIONS
   POST
   PUT])

(def optional-key->keyword s/explicit-schema-key)

(defn copy-json-schema-meta
  "Copies JSON schema metadata from one schema element to another. This is useful in cases where a transformation
   is being applied to a metadata schema element, and the metadata has to be copied from the original element to
   the transformed one."
  [src dest]
  (if-let [json-schema-meta (:json-schema (meta src))]
    (vary-meta dest assoc :json-schema json-schema-meta)
    dest))

(defn transform-enum
  "Converts the items in an enumeration using a provided transformation function."
  [enum f]
  (->> (st/schema-value enum)
       (map f)
       (apply s/enum)
       (copy-json-schema-meta enum)))

(defn add-to-enum
  "Adds additional values to an existing enumeration."
  [enum & vs]
  (as-> (st/schema-value enum) x
    (apply conj x vs)
    (apply s/enum x)
    (copy-json-schema-meta enum x)))

(defn ->optional-param
  "Removes a required param from the given schema and re-adds it as an optional param."
  [schema param]
  (-> schema
    (assoc (s/optional-key param) (schema param))
    (dissoc param)))

(def NonBlankString
  (describe (s/both String (s/pred (complement blank?) 'non-blank-string?)) "A non-blank string."))

(s/defschema StandardUserQueryParams
  {:user (describe NonBlankString "The username of the authenticated, requesting user")})

(s/defschema StatusParams
  {(s/optional-key :expecting)
   (describe NonBlankString "The service which the requesting client is expecting to see here.
                             Should throw a 500 error if provided and does not match the actual
                             service running here.")})

;; The SortField Docs and OptionalKey are defined seperately so that they can be used to describe
;; different enums in the PagingParams in different endpoints.
(def SortFieldOptionalKey (s/optional-key :sort-field))
(def SortFieldDocs
  "Sorts the results in the listing array by the given field, before limits and offsets are applied.
   See http://www.postgresql.org/docs/9.2/interactive/queries-order.html")

(s/defschema PagingParams
  {(s/optional-key :limit)
   (describe (s/both Long (s/pred pos? 'positive-integer?))
     "Limits the response to X number of results in the listing array.
      See http://www.postgresql.org/docs/9.2/interactive/queries-limit.html")

   (s/optional-key :offset)
   (describe (s/both Long (s/pred (partial <= 0) 'non-negative-integer?))
     "Skips the first X number of results in the listing array.
      See http://www.postgresql.org/docs/9.2/interactive/queries-limit.html")

   ;; SortField is a String by default.
   SortFieldOptionalKey
   (describe String SortFieldDocs)

   (s/optional-key :sort-dir)
   (describe (s/enum "ASC" "DESC")
     "Only used when sort-field is present. Sorts the results in either ascending (`ASC`) or
      descending (`DESC`) order, before limits and offsets are applied. Defaults to `ASC`.
      See http://www.postgresql.org/docs/9.2/interactive/queries-order.html")})

(s/defschema StatusResponse
  {:service     (describe NonBlankString "The name of the service")
   :description (describe NonBlankString "The service description")
   :version     (describe NonBlankString "The service version")
   :docs-url    (describe NonBlankString "The service API docs")
   (s/optional-key :expecting) (describe String "The service the requesting client was expecting to see, if any")})

(s/defschema ErrorResponse
  {:error_code              (describe NonBlankString "The code identifying the type of error")
   (s/optional-key :reason) (describe NonBlankString "A brief description of the reason for the error")})

(s/defschema ErrorResponseExists
  (assoc ErrorResponse
    :error_code (describe (s/enum ce/ERR_EXISTS) "Exists error code")))

(s/defschema ErrorResponseNotWritable
  (assoc ErrorResponse
    :error_code (describe (s/enum ce/ERR_NOT_WRITEABLE) "Not Writeable error code")))

(s/defschema ErrorResponseForbidden
  (assoc ErrorResponse
    :error_code (describe (s/enum ce/ERR_FORBIDDEN) "Insufficient privileges error code")))

(s/defschema ErrorResponseNotFound
  (assoc ErrorResponse
    :error_code (describe (s/enum ce/ERR_NOT_FOUND) "Not Found error code")))

(s/defschema ErrorResponseIllegalArgument
  (assoc ErrorResponse
    :error_code (describe (s/enum ce/ERR_ILLEGAL_ARGUMENT) "Illegal Argument error code")))

(s/defschema ErrorResponseUnchecked
  {:error_code              (describe (s/enum ce/ERR_UNCHECKED_EXCEPTION ce/ERR_SCHEMA_VALIDATION)
                                      "Response schema validation and Unchecked error codes")
   (s/optional-key :reason) (describe s/Any "A brief text or object describing the reason for the error")})

(def CommonResponses
  {500      {:schema      ErrorResponseUnchecked
             :description "Unchecked errors"}
   :default {:schema      ErrorResponse
             :description "All other errors"}})

(defrecord DocOnly [schema-real schema-doc]
  s/Schema
  (spec [_this]
    (variant/variant-spec
     spec/+no-precondition+
     [{:schema schema-real}]))
  (explain [_this] (list 'doc-only (s/explain schema-real) (s/explain schema-doc))))

(defn doc-only [schema-to-use schema-to-doc]
  (DocOnly. schema-to-use schema-to-doc))

(extend-protocol json-schema/JsonSchema
  DocOnly
  (convert [e _]
    (json-schema/->swagger (:schema-doc e))))
