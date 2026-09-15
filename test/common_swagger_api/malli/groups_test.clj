(ns common-swagger-api.malli.groups-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.groups :as groups]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def base-value {:name "example-group" :type "group"})

(def group-value
  (assoc base-value
         :id_index          "12345"
         :id                "abc123def456"
         :description       "An example group"
         :display_extension "example"
         :display_name      "Example Group"
         :extension         "example"))

(def subject-value {:id "user123" :source_id "ldap"})

(def detail-value
  {:created_at          1640995200000
   :has_composite       false
   :is_composite_factor false})

(def member-update-result {:success true :subject_id "user123" :source_id "ldap"})

(def privilege-update {:subject_id "user123" :privileges ["read" "update"]})

(def privilege-value {:type "access" :name "view" :subject subject-value})

(def details-params
  [:map {:closed true} (into [groups/GroupDetailsParamKey] (groups/GroupDetailsParamDesc "group"))])

(deftest ValidGroupPrivileges
  (valid groups/ValidGroupPrivileges "view" "groupAttrUpdate")
  (invalid groups/ValidGroupPrivileges :view "nope" nil))

(deftest GroupDetailsParam
  (valid details-params {} {:details true})
  (invalid details-params {:details "true"} {:extra 1}))

(deftest base-group
  (valid (groups/base-group "group") base-value (select-keys group-value [:name :type :description]))
  (invalid (groups/base-group "group")
           {}
           (dissoc base-value :type)
           (assoc base-value :name 1)
           (assoc base-value :extra 1)))

(deftest group
  (valid (groups/group "group")
         group-value
         (dissoc group-value :description :display_extension :display_name :extension))
  (invalid (groups/group "group")
           base-value
           (dissoc group-value :id)
           (assoc group-value :id_index 12345)
           (assoc group-value :extra 1)))

(deftest group-update
  (valid (groups/group-update "group") {} {:name "example-group"} (dissoc base-value :type))
  (invalid (groups/group-update "group") base-value {:name 1} {:extra 1}))

(deftest group-stub
  (valid (groups/group-stub "group") {} base-value group-value)
  (invalid (groups/group-stub "group")
           (assoc group-value :id 1)
           (assoc group-value :type nil)
           (assoc group-value :extra 1)))

(deftest group-detail
  (valid (groups/group-detail "group")
         detail-value
         (assoc detail-value :attribute_names ["attribute1"] :attribute_values ["value1"])
         (assoc detail-value :created_by "admin" :created_by_detail subject-value :composite_type "intersection")
         (assoc detail-value :left_group group-value :right_group group-value :modified_at 1640995200000))
  (invalid (groups/group-detail "group")
           {}
           (dissoc detail-value :created_at)
           (assoc detail-value :has_composite "false")
           (assoc detail-value :left_group base-value)
           (assoc detail-value :extra 1)))

(deftest group-with-detail
  (valid (groups/group-with-detail "group") group-value (assoc group-value :detail detail-value))
  (invalid (groups/group-with-detail "group")
           base-value
           (assoc group-value :detail {})
           (assoc group-value :detail detail-value :extra 1)))

(deftest group-list
  (valid (groups/group-list "group" "groups") {:groups []} {:groups [group-value]})
  (invalid (groups/group-list "group" "groups") {} {:groups [base-value]} {:groups [] :extra 1}))

(deftest group-list-with-detail
  (valid (groups/group-list-with-detail "group" "groups")
         {:groups []}
         {:groups [(assoc group-value :detail detail-value)]})
  (invalid (groups/group-list-with-detail "group" "groups") {} {:groups [{}]} {:groups [] :extra 1}))

(deftest group-members
  (valid (groups/group-members "group") {:members []} {:members [subject-value]})
  (invalid (groups/group-members "group") {} {:members [{}]} {:members [] :extra 1}))

(deftest GroupMembersUpdate
  (valid groups/GroupMembersUpdate {:members []} {:members ["user1" "user2"]})
  (invalid groups/GroupMembersUpdate {} {:members [" "]} {:members "user1"} {:members [] :extra 1}))

(deftest GroupMemberSubjectUpdateResponse
  (valid groups/GroupMemberSubjectUpdateResponse
         member-update-result
         (assoc member-update-result :subject_name "John Doe"))
  (invalid groups/GroupMemberSubjectUpdateResponse
           {}
           (dissoc member-update-result :source_id)
           (assoc member-update-result :success "true")
           (assoc member-update-result :subject_name " ")
           (assoc member-update-result :extra 1)))

(deftest GroupMembersUpdateResponse
  (valid groups/GroupMembersUpdateResponse {:results []} {:results [member-update-result]})
  (invalid groups/GroupMembersUpdateResponse {} {:results [{}]} {:results [] :extra 1}))

(deftest GroupPrivilegeUpdate
  (valid groups/GroupPrivilegeUpdate privilege-update (assoc privilege-update :privileges []))
  (invalid groups/GroupPrivilegeUpdate
           {}
           (dissoc privilege-update :privileges)
           (assoc privilege-update :privileges ["nope"])
           (assoc privilege-update :extra 1)))

(deftest GroupPrivilegeUpdates
  (valid groups/GroupPrivilegeUpdates {:updates []} {:updates [privilege-update]})
  (invalid groups/GroupPrivilegeUpdates {} {:updates [{}]} {:updates [] :extra 1}))

(deftest GroupPrivilegeRemoval
  (valid groups/GroupPrivilegeRemoval privilege-update (assoc privilege-update :privileges ["admin"]))
  (invalid groups/GroupPrivilegeRemoval
           {}
           (dissoc privilege-update :subject_id)
           (assoc privilege-update :privileges "admin")
           (assoc privilege-update :extra 1)))

(deftest GroupPrivilegeRemovals
  (valid groups/GroupPrivilegeRemovals {:updates []} {:updates [privilege-update]})
  (invalid groups/GroupPrivilegeRemovals {} {:updates [{}]} {:updates [] :extra 1}))

(deftest Privilege
  (valid groups/Privilege privilege-value (assoc privilege-value :allowed true :revokable true))
  (invalid groups/Privilege
           {}
           (dissoc privilege-value :subject)
           (assoc privilege-value :allowed "true")
           (assoc privilege-value :extra 1)))

(deftest Privileges
  (valid groups/Privileges {:privileges []} {:privileges [privilege-value]})
  (invalid groups/Privileges {} {:privileges [{}]} {:privileges [] :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.groups))
