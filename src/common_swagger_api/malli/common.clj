(ns common-swagger-api.malli.common)

(def IncludeHiddenParams
  [:map {:closed true}
   [:include-hidden
    {:description         "True if hidden elements should be included in the results"
     :json-schema/example true
     :optional            true}
    :boolean]])

(def IncludeDeletedParams
  [:map {:closed true}
   [:include-deleted
    {:description         "True if deleted elements should be included in the results"
     :json-schema/example true
     :optional            true}
    :boolean]])
