(ns common-swagger-api.malli.metadata
  (:require [malli.util :as mu]
            [malli.core :as m]))

(def TargetIdParam
  [:uuid {:description         "The target item's UUID"
          :json-schema/example #uuid "a14dfe49-f65f-418b-b3c5-6497284251fe"}])

(def Avu
  (m/schema
    [:schema
     {:registry
      {::avu [:map {:closed true}

              [:id
               {:description         "The AVU's UUID"
                :json-schema/example "70fc1080-3152-4c09-92b0-f5b9cc70088b"}
               :uuid]

              [:attr
               {:description         "The Attribute's name"
                :json-schema/example "attribute-name"}
               :string]

              [:value
               {:description         "The Attribute's value"
                :json-schema/example "attribute-value"}
               :string]

              [:unit
               {:description         "The attribute's unit"
                :json-schema/example "attribute-unit"}
               :string]

              [:target_id
               {:description         "The target item's UUID"
                :json-schema/example "a14dfe49-f65f-418b-b3c5-6497284251fe"}
               :uuid]

              [:created_by
               {:description         "The ID of the user who created the AVU"
                :json-schema/example "user123"}
               :string]

              [:modified_by
               {:description         "The ID of the user who last modified the AVU"
                :json-schema/example "user321"}
               :string]

              [:created_on
               {:description         "The date the AVU was created in ms since the POSIX epoch"
                :json-schema/example 1757465246000}
               :int]

              [:modified_on
               {:description         "The date the AVU was late modified in ms since the POSIX epoch"
                :json-schema/example 1757465251000}
               :int]

              [:avus
               {:description "AVUs attached to this AVU"
                :optional    true}
               [:vector [:ref ::avu]]]]}}
     ::avu]))

(def AvuList
  [:map {:closed true}
   [:avus
    {:description "The list of AVUs"}
    [:vector Avu]]])

(def AvuRequest
  (m/schema
    [:schema
     {:registry
      {::avu-request [:map {:closed true}

                      [:id
                       {:description         "The AVU's UUID"
                        :json-schema/example "70fc1080-3152-4c09-92b0-f5b9cc70088b"
                        :optional            true}
                       :uuid]

                      [:attr
                       {:description         "The Attribute's name"
                        :json-schema/example "attribute-name"}
                       :string]

                      [:value
                       {:description         "The Attribute's value"
                        :json-schema/example "attribute-value"}
                       :string]

                      [:unit
                       {:description         "The attribute's unit"
                        :json-schema/example "attribute-unit"}
                       :string]

                      [:target_id
                       {:description         "The target item's UUID"
                        :json-schema/example "a14dfe49-f65f-418b-b3c5-6497284251fe"
                        :optional            true}
                       :uuid]

                      [:created_by
                       {:description         "The ID of the user who created the AVU"
                        :json-schema/example "user123"
                        :optional            true}
                       :string]

                      [:modified_by
                       {:description         "The ID of the user who last modified the AVU"
                        :json-schema/example "user321"
                        :optional            true}
                       :string]

                      [:created_on
                       {:description         "The date the AVU was created in ms since the POSIX epoch"
                        :json-schema/example 1757465246000
                        :optional            true}
                       :int]

                      [:modified_on
                       {:description         "The date the AVU was late modified in ms since the POSIX epoch"
                        :json-schema/example 1757465251000
                        :optional            true}
                       :int]

                      [:avus
                       {:description "AVUs attached to this AVU"
                        :optional    true}
                       [:vector [:ref ::avu-request]]]]}}
     ::avu-request]))

(def AvuListRequest
  [:map
   {:description "The Metadata AVU update request"
    :closed true}
   [:avus
    {:description "The AVUs to save for the target item"}
    [:vector AvuRequest]]])

(def SetAvuRequest
  (-> AvuListRequest
      (mu/optional-keys [:avus])
      (mu/update-properties assoc :description "The Metadata AVU save request")))

(def AvuSearchParams
  [:map {:closed true}
   [:attribute
    {:optional            true
     :description         "Attribute names to search for."
     :json-schema/example ["attr1" "attr2"]}
    [:vector :string]]

   [:value
    {:optional            true
     :description         "Values to search for."
     :json-schema/example ["value1" "value2"]}
    [:vector :string]]

   [:unit
    {:optional            true
     :description         "Units to search for."
     :json-schema/example ["unit1" "unit2"]}
    [:vector :string]]

   [:target-id
    {:optional            true
     :description         "Target IDs to search for."
     :json-schema/example ["123e4567-e89b-12d3-a456-426614174000"]}
    [:vector :uuid]]])

(def DataTypes ["file" "folder"])

(def DataTypeEnum
  (apply vector :enum DataTypes))
