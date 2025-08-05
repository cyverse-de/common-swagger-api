(ns common-swagger-api.schema.groups
  (:require [common-swagger-api.schema :refer [describe NonBlankString ->optional-param]]
            [common-swagger-api.schema.subjects :as subjects]
            [schema.core :as s]))

(def ValidGroupPrivileges (s/enum "view" "read" "update" "admin" "optin" "optout" "groupAttrRead" "groupAttrUpdate"))

(def GroupDetailsParamKey (s/optional-key :details))

(defn GroupDetailsParamDesc
  [group-descriptor]
  (describe Boolean (str "Optionally include " group-descriptor " details such as modified date and creator information.")))

(defn base-group [group-descriptor]
  {:name
   (describe String (str "The internal " group-descriptor " name"))

   :type
   (describe String (str "The " group-descriptor " type name"))

   (s/optional-key :description)
   (describe String (str "A brief description of the " group-descriptor))

   (s/optional-key :display_extension)
   (describe String (str "The displayable " group-descriptor " name extension"))})

(defn group [group-descriptor]
  (assoc (base-group group-descriptor)

         (s/optional-key :display_name)
         (describe String (str "The displayable " group-descriptor " name"))

         (s/optional-key :extension)
         (describe String (str "The internal " group-descriptor " name extension"))

         :id_index
         (describe String "The sequential ID index number")

         :id
         (describe String (str "The " group-descriptor " ID"))))

(defn group-update [group-descriptor]
  (-> (base-group group-descriptor)
      (->optional-param :name)
      (dissoc :type)))

(defn group-stub [group-descriptor]
  (-> (group group-descriptor)
      (->optional-param :name)
      (->optional-param :type)
      (->optional-param :id)
      (->optional-param :id_index)))

(defn group-detail [group-descriptor]
  (let [group-schema (group group-descriptor)]
    {(s/optional-key :attribute_names)
     (describe [String] (str "Attribute names, not including the ones listed in the " group-descriptor " itself"))

     (s/optional-key :attribute_values)
     (describe [String] (str "Attribute values, not including the ones listed in the " group-descriptor " itself"))

     (s/optional-key :composite_type)
     (describe String (str "The type of composite " group-descriptor ", if applicable"))

     :created_at
     (describe Long (str "The date and time the " group-descriptor " was created (ms since epoch)"))

     (s/optional-key :created_by)
     (describe String (str "The ID of the subject who created the " group-descriptor))

     (s/optional-key :created_by_detail)
     (describe subjects/Subject (str "The details of the subject who created the " group-descriptor))

     :has_composite
     (describe Boolean (str "True if this " group-descriptor " has a composite member"))

     :is_composite_factor
     (describe Boolean (str "True if this " group-descriptor " is a composite member of another group"))

     (s/optional-key :left_group)
     (describe group-schema (str "The left " group-descriptor " if this group is a composite"))

     (s/optional-key :modified_at)
     (describe Long (str "The date and time the " group-descriptor " was last modified (ms since epoch)"))

     (s/optional-key :modified_by)
     (describe String (str "The ID of the subject who last modified the " group-descriptor))

     (s/optional-key :right_group)
     (describe group-schema (str "The right " group-descriptor " if this group is a composite"))

     (s/optional-key :type_names)
     (describe [String] (str "The types associated with this " group-descriptor))}))

(defn group-with-detail [group-descriptor]
  (assoc (group group-descriptor)
         (s/optional-key :detail)
         (describe (group-detail group-descriptor) (str "Detailed information about the " group-descriptor))))

(defn group-list [group-descriptor plural-group-descriptor]
  {:groups (describe [(group group-descriptor)] (str "The list of " plural-group-descriptor " in the result set"))})

(defn group-list-with-detail [group-descriptor plural-group-descriptor]
  {:groups (describe [(group-with-detail group-descriptor)] (str "The list of " plural-group-descriptor " in the result set"))})

(defn group-members [group-descriptor]
  {:members (describe [subjects/Subject] (str "The list of " group-descriptor " members"))})

(s/defschema GroupMembersUpdate
  {:members (describe [NonBlankString] "The new list of member subject IDs")})

(s/defschema GroupMemberSubjectUpdateResponse
  {:success
   (describe Boolean "True if the user was added successfully")

   :subject_id
   (describe NonBlankString "The subject ID")

   :source_id
   (describe NonBlankString "The subject source ID")

   (s/optional-key :subject_name)
   (describe NonBlankString "The subject name")})

(s/defschema GroupMembersUpdateResponse
  {:results (describe [GroupMemberSubjectUpdateResponse] "The list of membership update results")})

(s/defschema GroupPrivilegeUpdate
  {:subject_id (describe String "The subject ID")
   :privileges (describe [ValidGroupPrivileges] "The group privileges to assign")})

(s/defschema GroupPrivilegeUpdates
  {:updates (describe [GroupPrivilegeUpdate] "The privilege updates to process")})

(s/defschema GroupPrivilegeRemoval
  {:subject_id (describe String "The subject ID")
   :privileges (describe [ValidGroupPrivileges] "The group privileges to remove")})

(s/defschema GroupPrivilegeRemovals
  {:updates (describe [GroupPrivilegeRemoval] "The privilege updates to process")})

(s/defschema Privilege
  {:type
   (describe String "The general type of privilege")

   :name
   (describe String "The privilege name, under the type")

   (s/optional-key :allowed)
   (describe Boolean "Whether the privilege is marked allowed")

   (s/optional-key :revokable)
   (describe Boolean "Whether the privilege is marked revokable")

   :subject
   (describe subjects/Subject "The subject/user with the privilege")})

(s/defschema Privileges
  {:privileges (describe [Privilege] "The list of privileges")})
