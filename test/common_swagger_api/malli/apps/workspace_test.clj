(ns common-swagger-api.malli.apps.workspace-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.apps.workspace :as w]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-WorkspaceId
  (testing "WorkspaceId validation"
    (is (valid? w/WorkspaceId #uuid "123e4567-e89b-12d3-a456-426614174000"))
    (is (not (valid? w/WorkspaceId "not-a-uuid")))
    (is (not (valid? w/WorkspaceId 123)))
    (is (not (valid? w/WorkspaceId nil)))))

(deftest test-Workspace
  (testing "Workspace validation"
    (testing "valid workspace"
      (is (valid? w/Workspace
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :user_id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :is_public false
                   :new_workspace true}))
      (is (valid? w/Workspace
                  {:id #uuid "111e2222-3333-4444-5555-666677778888"
                   :user_id #uuid "aaa1bbbb-cccc-dddd-eeee-fff000111222"
                   :root_category_id #uuid "333e4444-5555-6666-7777-888899990000"
                   :is_public true
                   :new_workspace false})))

    (testing "invalid workspace"
      (is (not (valid? w/Workspace {})))
      (is (not (valid? w/Workspace {:id #uuid "123e4567-e89b-12d3-a456-426614174000"})))
      (is (not (valid? w/Workspace
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :user_id #uuid "987e6543-e21b-32c1-b456-426614174000"
                        :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :is_public false})))
      (is (not (valid? w/Workspace
                       {:id "not-a-uuid"
                        :user_id #uuid "987e6543-e21b-32c1-b456-426614174000"
                        :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :is_public false
                        :new_workspace true})))
      (is (not (valid? w/Workspace
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :user_id "not-a-uuid"
                        :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :is_public false
                        :new_workspace true})))
      (is (not (valid? w/Workspace
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :user_id #uuid "987e6543-e21b-32c1-b456-426614174000"
                        :root_category_id "not-a-uuid"
                        :is_public false
                        :new_workspace true})))
      (is (not (valid? w/Workspace
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :user_id #uuid "987e6543-e21b-32c1-b456-426614174000"
                        :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :is_public "not-a-boolean"
                        :new_workspace true})))
      (is (not (valid? w/Workspace
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :user_id #uuid "987e6543-e21b-32c1-b456-426614174000"
                        :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :is_public false
                        :new_workspace "not-a-boolean"})))
      (is (not (valid? w/Workspace
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :user_id #uuid "987e6543-e21b-32c1-b456-426614174000"
                        :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :is_public false
                        :new_workspace true
                        :extra_field "should not be allowed"}))))))
