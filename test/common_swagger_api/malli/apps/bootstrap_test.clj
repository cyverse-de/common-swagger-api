(ns common-swagger-api.malli.apps.bootstrap-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.apps.bootstrap :as b]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-SystemIds
  (testing "SystemIds validation"
    (testing "valid system ids"
      (is (valid? b/SystemIds
                  {:de_system_id "de"
                   :all_system_ids ["de" "tapis"]}))
      (is (valid? b/SystemIds
                  {:de_system_id "de"
                   :all_system_ids ["de"]}))
      (is (valid? b/SystemIds
                  {:de_system_id "custom-system"
                   :all_system_ids ["de" "tapis" "custom-system"]})))

    (testing "invalid system ids"
      (is (not (valid? b/SystemIds {})))
      (is (not (valid? b/SystemIds {:de_system_id "de"})))
      (is (not (valid? b/SystemIds {:all_system_ids ["de" "tapis"]})))
      (is (not (valid? b/SystemIds {:de_system_id ""
                                     :all_system_ids ["de"]})))
      (is (not (valid? b/SystemIds {:de_system_id "de"
                                     :all_system_ids ["de" ""]})))
      (is (not (valid? b/SystemIds {:de_system_id "de"
                                     :all_system_ids ["de"]
                                     :extra_field "not allowed"}))))))

(deftest test-AppsBootstrapResponse
  (testing "AppsBootstrapResponse validation"
    (testing "valid bootstrap response"
      (is (valid? b/AppsBootstrapResponse
                  {:webhooks []
                   :system_ids {:de_system_id "de"
                                :all_system_ids ["de" "tapis"]}
                   :workspace {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                               :user_id #uuid "987e6543-e21b-32c1-b456-426614174000"
                               :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                               :is_public false
                               :new_workspace true}}))
      (is (valid? b/AppsBootstrapResponse
                  {:webhooks [{:type {:type "Slack"}
                               :url "https://hooks.slack.com/services/XXX"
                               :topics ["data.added"]}]
                   :system_ids {:de_system_id "de"
                                :all_system_ids ["de"]}
                   :workspace {:id #uuid "111e2222-3333-4444-5555-666677778888"
                               :user_id #uuid "aaa1bbbb-cccc-dddd-eeee-fff000111222"
                               :root_category_id #uuid "333e4444-5555-6666-7777-888899990000"
                               :is_public true
                               :new_workspace false}})))

    (testing "invalid bootstrap response"
      (is (not (valid? b/AppsBootstrapResponse {})))
      (is (not (valid? b/AppsBootstrapResponse {:webhooks []})))
      (is (not (valid? b/AppsBootstrapResponse
                       {:webhooks []
                        :system_ids {:de_system_id "de"
                                     :all_system_ids ["de"]}})))
      (is (not (valid? b/AppsBootstrapResponse
                       {:webhooks []
                        :workspace {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                    :user_id #uuid "987e6543-e21b-32c1-b456-426614174000"
                                    :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                                    :is_public false
                                    :new_workspace true}})))
      (is (not (valid? b/AppsBootstrapResponse
                       {:webhooks []
                        :system_ids {:de_system_id "de"
                                     :all_system_ids ["de"]}
                        :workspace {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                    :user_id #uuid "987e6543-e21b-32c1-b456-426614174000"
                                    :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                                    :is_public false
                                    :new_workspace true}
                        :extra_field "not allowed"})))
      (is (not (valid? b/AppsBootstrapResponse
                       {:webhooks nil
                        :system_ids {:de_system_id "de"
                                     :all_system_ids ["de"]}
                        :workspace {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                    :user_id #uuid "987e6543-e21b-32c1-b456-426614174000"
                                    :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                                    :is_public false
                                    :new_workspace true}})))
      (is (not (valid? b/AppsBootstrapResponse
                       {:webhooks []
                        :system_ids {:de_system_id ""
                                     :all_system_ids ["de"]}
                        :workspace {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                    :user_id #uuid "987e6543-e21b-32c1-b456-426614174000"
                                    :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                                    :is_public false
                                    :new_workspace true}})))
      (is (not (valid? b/AppsBootstrapResponse
                       {:webhooks []
                        :system_ids {:de_system_id "de"
                                     :all_system_ids ["de"]}
                        :workspace {:id "not-a-uuid"
                                    :user_id #uuid "987e6543-e21b-32c1-b456-426614174000"
                                    :root_category_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                                    :is_public false
                                    :new_workspace true}}))))))
