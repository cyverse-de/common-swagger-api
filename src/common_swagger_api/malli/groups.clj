(ns common-swagger-api.malli.groups
  (:require [common-swagger-api.malli :refer [NonBlankString]]
            [common-swagger-api.malli.subjects :as subjects]
            [malli.util :as mu]))

(def ValidGroupPrivileges
  [:enum "view" "read" "update" "admin" "optin" "optout" "groupAttrRead" "groupAttrUpdate"])

(def GroupDetailsParamKey :details)

(defn GroupDetailsParamDesc
  [group-descriptor]
  [{:optional            true
    :description         (str "Optionally include " group-descriptor " details such as modified date and "
                              "creator information.")
    :json-schema/example true}
   :boolean])

(defn base-group [group-descriptor]
  [:map {:closed true}
   [:name
    {:description         (str "The internal " group-descriptor " name")
     :json-schema/example "example-group"}
    :string]

   [:type
    {:description         (str "The " group-descriptor " type name")
     :json-schema/example "group"}
    :string]

   [:description
    {:optional            true
     :description         (str "A brief description of the " group-descriptor)
     :json-schema/example "An example group"}
    :string]

   [:display_extension
    {:optional            true
     :description         (str "The displayable " group-descriptor " name extension")
     :json-schema/example "example"}
    :string]])

(defn group [group-descriptor]
  (mu/merge
   (base-group group-descriptor)
   [:map {:closed true}
    [:display_name
     {:optional            true
      :description         (str "The displayable " group-descriptor " name")
      :json-schema/example "Example Group"}
     :string]

    [:extension
     {:optional            true
      :description         (str "The internal " group-descriptor " name extension")
      :json-schema/example "example"}
     :string]

    [:id_index
     {:description         "The sequential ID index number"
      :json-schema/example "12345"}
     :string]

    [:id
     {:description         (str "The " group-descriptor " ID")
      :json-schema/example "abc123def456"}
     :string]]))

(defn group-update [group-descriptor]
  (-> (base-group group-descriptor)
      (mu/optional-keys [:name])
      (mu/dissoc :type)))

(defn group-stub [group-descriptor]
  (-> (group group-descriptor)
      (mu/optional-keys [:name :type :id :id_index])))

(defn group-detail [group-descriptor]
  (let [group-schema (group group-descriptor)]
    [:map {:closed true}
     [:attribute_names
      {:optional            true
       :description         (str "Attribute names, not including the ones listed in the " group-descriptor " itself")
       :json-schema/example ["attribute1" "attribute2"]}
      [:vector :string]]

     [:attribute_values
      {:optional            true
       :description         (str "Attribute values, not including the ones listed in the " group-descriptor " itself")
       :json-schema/example ["value1" "value2"]}
      [:vector :string]]

     [:composite_type
      {:optional            true
       :description         (str "The type of composite " group-descriptor ", if applicable")
       :json-schema/example "intersection"}
      :string]

     [:created_at
      {:description         (str "The date and time the " group-descriptor " was created (ms since epoch)")
       :json-schema/example 1640995200000}
      :int]

     [:created_by
      {:optional            true
       :description         (str "The ID of the subject who created the " group-descriptor)
       :json-schema/example "admin"}
      :string]

     [:created_by_detail
      {:optional    true
       :description (str "The details of the subject who created the " group-descriptor)}
      subjects/Subject]

     [:has_composite
      {:description         (str "True if this " group-descriptor " has a composite member")
       :json-schema/example false}
      :boolean]

     [:is_composite_factor
      {:description         (str "True if this " group-descriptor " is a composite member of another group")
       :json-schema/example false}
      :boolean]

     [:left_group
      {:optional    true
       :description (str "The left " group-descriptor " if this group is a composite")}
      group-schema]

     [:modified_at
      {:optional            true
       :description         (str "The date and time the " group-descriptor " was last modified (ms since epoch)")
       :json-schema/example 1640995200000}
      :int]

     [:modified_by
      {:optional            true
       :description         (str "The ID of the subject who last modified the " group-descriptor)
       :json-schema/example "admin"}
      :string]

     [:right_group
      {:optional    true
       :description (str "The right " group-descriptor " if this group is a composite")}
      group-schema]

     [:type_names
      {:optional            true
       :description         (str "The types associated with this " group-descriptor)
       :json-schema/example ["type1" "type2"]}
      [:vector :string]]]))

(defn group-with-detail [group-descriptor]
  (mu/merge
   (group group-descriptor)
   [:map {:closed true}
    [:detail
     {:optional    true
      :description (str "Detailed information about the " group-descriptor)}
     (group-detail group-descriptor)]]))

(defn group-list [group-descriptor plural-group-descriptor]
  [:map {:closed true}
   [:groups
    {:description (str "The list of " plural-group-descriptor " in the result set")}
    [:vector (group group-descriptor)]]])

(defn group-list-with-detail [group-descriptor plural-group-descriptor]
  [:map {:closed true}
   [:groups
    {:description (str "The list of " plural-group-descriptor " in the result set")}
    [:vector (group-with-detail group-descriptor)]]])

(defn group-members [group-descriptor]
  [:map {:closed true}
   [:members
    {:description (str "The list of " group-descriptor " members")}
    [:vector subjects/Subject]]])

(def GroupMembersUpdate
  [:map {:closed true}
   [:members
    {:description         "The new list of member subject IDs"
     :json-schema/example ["user1" "user2" "user3"]}
    [:vector NonBlankString]]])

(def GroupMemberSubjectUpdateResponse
  [:map {:closed true}
   [:success
    {:description         "True if the user was added successfully"
     :json-schema/example true}
    :boolean]

   [:subject_id
    {:description         "The subject ID"
     :json-schema/example "user123"}
    NonBlankString]

   [:source_id
    {:description         "The subject source ID"
     :json-schema/example "ldap"}
    NonBlankString]

   [:subject_name
    {:optional            true
     :description         "The subject name"
     :json-schema/example "John Doe"}
    NonBlankString]])

(def GroupMembersUpdateResponse
  [:map {:closed true}
   [:results
    {:description "The list of membership update results"}
    [:vector GroupMemberSubjectUpdateResponse]]])

(def GroupPrivilegeUpdate
  [:map {:closed true}
   [:subject_id
    {:description         "The subject ID"
     :json-schema/example "user123"}
    :string]

   [:privileges
    {:description         "The group privileges to assign"
     :json-schema/example ["read" "update"]}
    [:vector ValidGroupPrivileges]]])

(def GroupPrivilegeUpdates
  [:map {:closed true}
   [:updates
    {:description "The privilege updates to process"}
    [:vector GroupPrivilegeUpdate]]])

(def GroupPrivilegeRemoval
  [:map {:closed true}
   [:subject_id
    {:description         "The subject ID"
     :json-schema/example "user123"}
    :string]

   [:privileges
    {:description         "The group privileges to remove"
     :json-schema/example ["admin" "update"]}
    [:vector ValidGroupPrivileges]]])

(def GroupPrivilegeRemovals
  [:map {:closed true}
   [:updates
    {:description "The privilege updates to process"}
    [:vector GroupPrivilegeRemoval]]])

(def Privilege
  [:map {:closed true}
   [:type
    {:description         "The general type of privilege"
     :json-schema/example "access"}
    :string]

   [:name
    {:description         "The privilege name, under the type"
     :json-schema/example "view"}
    :string]

   [:allowed
    {:optional            true
     :description         "Whether the privilege is marked allowed"
     :json-schema/example true}
    :boolean]

   [:revokable
    {:optional            true
     :description         "Whether the privilege is marked revokable"
     :json-schema/example true}
    :boolean]

   [:subject
    {:description "The subject/user with the privilege"}
    subjects/Subject]])

(def Privileges
  [:map {:closed true}
   [:privileges
    {:description "The list of privileges"}
    [:vector Privilege]]])
