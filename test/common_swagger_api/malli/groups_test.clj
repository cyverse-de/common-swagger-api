(ns common-swagger-api.malli.groups-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.groups :as groups]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-ValidGroupPrivileges
  (testing "ValidGroupPrivileges validation"
    (is (valid? groups/ValidGroupPrivileges "view"))
    (is (valid? groups/ValidGroupPrivileges "read"))
    (is (valid? groups/ValidGroupPrivileges "update"))
    (is (valid? groups/ValidGroupPrivileges "admin"))
    (is (valid? groups/ValidGroupPrivileges "optin"))
    (is (valid? groups/ValidGroupPrivileges "optout"))
    (is (valid? groups/ValidGroupPrivileges "groupAttrRead"))
    (is (valid? groups/ValidGroupPrivileges "groupAttrUpdate"))
    (is (not (valid? groups/ValidGroupPrivileges "invalid")))
    (is (not (valid? groups/ValidGroupPrivileges "READ")))  ; Case sensitive
    (is (not (valid? groups/ValidGroupPrivileges nil)))))

(deftest test-GroupDetailsParamKey
  (testing "GroupDetailsParamKey is :details"
    (is (= :details groups/GroupDetailsParamKey))))

(deftest test-GroupDetailsParamDesc
  (testing "GroupDetailsParamDesc function"
    (let [param-desc (groups/GroupDetailsParamDesc "group")]
      (is (vector? param-desc))
      (is (= 2 (count param-desc)))
      (let [[props type] param-desc]
        (is (map? props))
        (is (contains? props :optional))
        (is (contains? props :description))
        (is (contains? props :json-schema/example))
        (is (= :boolean type))
        (is (true? (:optional props)))
        (is (string? (:description props)))
        (is (boolean? (:json-schema/example props)))))))

(deftest test-base-group
  (testing "base-group function"
    (let [base-schema (groups/base-group "team")]
      (is (valid? base-schema
                  {:name "test-team"
                   :type "team"}))
      (is (valid? base-schema
                  {:name "test-team"
                   :type "team"
                   :description "A test team"
                   :display_extension "test"}))

      ;; Missing required fields
      (is (not (valid? base-schema {})))
      (is (not (valid? base-schema {:name "test-team"})))
      (is (not (valid? base-schema {:type "team"}))))))

(deftest test-group
  (testing "group function"
    (let [group-schema (groups/group "team")]
      (is (valid? group-schema
                  {:name "test-team"
                   :type "team"
                   :id_index "123"
                   :id "team-id"}))
      (is (valid? group-schema
                  {:name "test-team"
                   :type "team"
                   :id_index "123"
                   :id "team-id"
                   :display_name "Test Team"
                   :extension "test"
                   :description "A test team"}))

      ;; Missing required fields
      (is (not (valid? group-schema
                       {:name "test-team"
                        :type "team"})))
      (is (not (valid? group-schema
                       {:name "test-team"
                        :type "team"
                        :id_index "123"}))))))

(deftest test-group-update
  (testing "group-update function"
    (let [group-update-schema (groups/group-update "team")]
      ;; All fields optional except description and display_extension
      (is (valid? group-update-schema {}))
      (is (valid? group-update-schema
                  {:name "updated-team"}))
      (is (valid? group-update-schema
                  {:description "Updated description"
                   :display_extension "updated"}))

      ;; Should not have type field
      (is (not (valid? group-update-schema
                       {:type "team"}))))))

(deftest test-group-stub
  (testing "group-stub function"
    (let [group-stub-schema (groups/group-stub "team")]
      ;; All fields should be optional
      (is (valid? group-stub-schema {}))
      (is (valid? group-stub-schema
                  {:name "stub-team"}))
      (is (valid? group-stub-schema
                  {:name "stub-team"
                   :type "team"
                   :id "team-id"
                   :id_index "123"})))))

(deftest test-group-members
  (testing "group-members function"
    (let [group-members-schema (groups/group-members "team")]
      (is (valid? group-members-schema
                  {:members []}))
      (is (valid? group-members-schema
                  {:members [{:id "user1"
                              :source_id "ldap"}
                             {:id "user2"
                              :source_id "ldap"
                              :name "User Two"}]}))

      ;; Missing required field
      (is (not (valid? group-members-schema {})))
      ;; Invalid member structure
      (is (not (valid? group-members-schema
                       {:members [{:id "user1"}]}))))))  ; Missing source_id

(deftest test-GroupMembersUpdate
  (testing "GroupMembersUpdate validation"
    (is (valid? groups/GroupMembersUpdate
                {:members ["user1" "user2" "user3"]}))
    (is (valid? groups/GroupMembersUpdate
                {:members []}))

    ;; Missing required field
    (is (not (valid? groups/GroupMembersUpdate {})))
    ;; Empty strings not allowed (NonBlankString)
    (is (not (valid? groups/GroupMembersUpdate
                     {:members ["user1" "" "user3"]})))
    (is (not (valid? groups/GroupMembersUpdate
                     {:members ["user1" "   " "user3"]})))))

(deftest test-GroupMemberSubjectUpdateResponse
  (testing "GroupMemberSubjectUpdateResponse validation"
    (is (valid? groups/GroupMemberSubjectUpdateResponse
                {:success true
                 :subject_id "user123"
                 :source_id "ldap"}))
    (is (valid? groups/GroupMemberSubjectUpdateResponse
                {:success false
                 :subject_id "user123"
                 :source_id "ldap"
                 :subject_name "John Doe"}))

    ;; Missing required fields
    (is (not (valid? groups/GroupMemberSubjectUpdateResponse
                     {:success true
                      :subject_id "user123"})))
    ;; Empty strings not allowed for required NonBlankString fields
    (is (not (valid? groups/GroupMemberSubjectUpdateResponse
                     {:success true
                      :subject_id ""
                      :source_id "ldap"})))
    ;; Empty string not allowed for optional NonBlankString field
    (is (not (valid? groups/GroupMemberSubjectUpdateResponse
                     {:success true
                      :subject_id "user123"
                      :source_id "ldap"
                      :subject_name ""})))))

(deftest test-GroupMembersUpdateResponse
  (testing "GroupMembersUpdateResponse validation"
    (is (valid? groups/GroupMembersUpdateResponse
                {:results []}))
    (is (valid? groups/GroupMembersUpdateResponse
                {:results [{:success true
                            :subject_id "user123"
                            :source_id "ldap"}
                           {:success false
                            :subject_id "user456"
                            :source_id "ldap"
                            :subject_name "Jane Doe"}]}))

    ;; Missing required field
    (is (not (valid? groups/GroupMembersUpdateResponse {})))
    ;; Invalid result structure
    (is (not (valid? groups/GroupMembersUpdateResponse
                     {:results [{:success true}]})))))

(deftest test-GroupPrivilegeUpdate
  (testing "GroupPrivilegeUpdate validation"
    (is (valid? groups/GroupPrivilegeUpdate
                {:subject_id "user123"
                 :privileges ["read" "update"]}))
    (is (valid? groups/GroupPrivilegeUpdate
                {:subject_id "user123"
                 :privileges []}))

    ;; Missing required fields
    (is (not (valid? groups/GroupPrivilegeUpdate
                     {:subject_id "user123"})))
    ;; Invalid privilege values
    (is (not (valid? groups/GroupPrivilegeUpdate
                     {:subject_id "user123"
                      :privileges ["read" "invalid"]})))))

(deftest test-GroupPrivilegeUpdates
  (testing "GroupPrivilegeUpdates validation"
    (is (valid? groups/GroupPrivilegeUpdates
                {:updates []}))
    (is (valid? groups/GroupPrivilegeUpdates
                {:updates [{:subject_id "user123"
                            :privileges ["read" "update"]}
                           {:subject_id "user456"
                            :privileges ["view"]}]}))

    ;; Missing required field
    (is (not (valid? groups/GroupPrivilegeUpdates {})))))

(deftest test-GroupPrivilegeRemoval
  (testing "GroupPrivilegeRemoval validation"
    (is (valid? groups/GroupPrivilegeRemoval
                {:subject_id "user123"
                 :privileges ["admin" "update"]}))
    (is (valid? groups/GroupPrivilegeRemoval
                {:subject_id "user123"
                 :privileges []}))

    ;; Missing required fields
    (is (not (valid? groups/GroupPrivilegeRemoval
                     {:subject_id "user123"})))
    ;; Invalid privilege values
    (is (not (valid? groups/GroupPrivilegeRemoval
                     {:subject_id "user123"
                      :privileges ["admin" "invalid"]})))))

(deftest test-GroupPrivilegeRemovals
  (testing "GroupPrivilegeRemovals validation"
    (is (valid? groups/GroupPrivilegeRemovals
                {:updates []}))
    (is (valid? groups/GroupPrivilegeRemovals
                {:updates [{:subject_id "user123"
                            :privileges ["admin" "update"]}]}))

    ;; Missing required field
    (is (not (valid? groups/GroupPrivilegeRemovals {})))))

(deftest test-Privilege
  (testing "Privilege validation"
    (is (valid? groups/Privilege
                {:type "group"
                 :name "read"
                 :subject {:id "user123"
                           :source_id "ldap"}}))
    (is (valid? groups/Privilege
                {:type "group"
                 :name "read"
                 :allowed true
                 :revokable false
                 :subject {:id "user123"
                           :source_id "ldap"
                           :name "John Doe"}}))

    ;; Missing required fields
    (is (not (valid? groups/Privilege
                     {:type "group"
                      :name "read"})))
    (is (not (valid? groups/Privilege
                     {:type "group"
                      :subject {:id "user123"
                                :source_id "ldap"}})))))

(deftest test-Privileges
  (testing "Privileges validation"
    (is (valid? groups/Privileges
                {:privileges []}))
    (is (valid? groups/Privileges
                {:privileges [{:type "group"
                               :name "read"
                               :subject {:id "user123"
                                         :source_id "ldap"}}]}))

    ;; Missing required field
    (is (not (valid? groups/Privileges {})))))
