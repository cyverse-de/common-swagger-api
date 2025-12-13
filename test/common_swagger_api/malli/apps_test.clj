(ns common-swagger-api.malli.apps-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common-swagger-api.malli.apps :as apps]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AppParameterListItem
  (testing "AppParameterListItem validation"
    (testing "valid list item"
      (is (valid? apps/AppParameterListItem
                  {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"}))
      (is (valid? apps/AppParameterListItem
                  {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                   :name "small_genome"
                   :value "1"
                   :description "Small genome (< 2 Mb)"
                   :display "Small Genome"
                   :isDefault false}))
      (is (valid? apps/AppParameterListItem
                  {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :name "large_genome"
                   :isDefault true})))

    (testing "invalid list item"
      (is (not (valid? apps/AppParameterListItem {})))
      (is (not (valid? apps/AppParameterListItem {:name "small_genome"})))
      (is (not (valid? apps/AppParameterListItem
                       {:id "not-a-uuid"
                        :name "small_genome"})))
      (is (not (valid? apps/AppParameterListItem
                       {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                        :isDefault "not-a-boolean"})))
      (is (not (valid? apps/AppParameterListItem
                       {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                        :extra-field "not-allowed"}))))))
(deftest test-AppParameterListGroup
  (testing "AppParameterListGroup validation"
    (testing "valid list group"
      (is (valid? apps/AppParameterListGroup
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"}))
      (is (valid? apps/AppParameterListGroup
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "genome_size_group"
                   :value "size_group"
                   :description "Genome size selection group"
                   :display "Genome Size"
                   :isDefault false}))
      (is (valid? apps/AppParameterListGroup
                  {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :name "options_group"
                   :arguments [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                                :name "small_genome"
                                :value "1"}]
                   :groups [{:id #uuid "abc12345-def6-7890-1234-567890abcdef"
                             :name "sub_group"}]})))

    (testing "invalid list group"
      (is (not (valid? apps/AppParameterListGroup {})))
      (is (not (valid? apps/AppParameterListGroup {:name "genome_size_group"})))
      (is (not (valid? apps/AppParameterListGroup
                       {:id "not-a-uuid"
                        :name "genome_size_group"})))
      (is (not (valid? apps/AppParameterListGroup
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :arguments "not-a-vector"})))
      (is (not (valid? apps/AppParameterListGroup
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :groups "not-a-vector"})))
      (is (not (valid? apps/AppParameterListGroup
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :extra-field "not-allowed"}))))))

(deftest test-AppParameterListItemOrTree
  (testing "AppParameterListItemOrTree validation"
    (testing "valid list item or tree"
      (is (valid? apps/AppParameterListItemOrTree
                  {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"}))
      (is (valid? apps/AppParameterListItemOrTree
                  {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                   :name "organism"
                   :value "human"
                   :description "Select organism type"
                   :display "Organism"
                   :isDefault false}))
      (is (valid? apps/AppParameterListItemOrTree
                  {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :isSingleSelect true
                   :selectionCascade "up"}))
      (is (valid? apps/AppParameterListItemOrTree
                  {:id #uuid "abc12345-def6-7890-1234-567890abcdef"
                   :name "tree_selector"
                   :arguments [{:id #uuid "111e1111-a11a-11a1-a111-111111111111"
                                :name "option1"
                                :value "val1"}
                               {:id #uuid "222e2222-b22b-22b2-b222-222222222222"
                                :name "option2"
                                :value "val2"}]}))
      (is (valid? apps/AppParameterListItemOrTree
                  {:id #uuid "def67890-abc1-2345-6789-0abcdef12345"
                   :name "grouped_tree"
                   :groups [{:id #uuid "333e3333-c33c-33c3-c333-333333333333"
                             :name "group1"}
                            {:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                             :name "group2"
                             :arguments [{:id #uuid "555e5555-e55e-55e5-e555-555555555555"
                                          :name "nested_option"}]}]}))
      (is (valid? apps/AppParameterListItemOrTree
                  {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                   :name "full_tree"
                   :display "Full Tree Selector"
                   :isSingleSelect false
                   :selectionCascade "down"
                   :arguments [{:id #uuid "666e6666-f66f-66f6-f666-666666666666"
                                :name "root_option"}]
                   :groups [{:id #uuid "777e7777-a77a-77a7-a777-777777777777"
                             :name "root_group"}]})))

    (testing "invalid list item or tree"
      (is (not (valid? apps/AppParameterListItemOrTree {})))
      (is (not (valid? apps/AppParameterListItemOrTree {:name "organism"})))
      (is (not (valid? apps/AppParameterListItemOrTree
                       {:id "not-a-uuid"
                        :name "organism"})))
      (is (not (valid? apps/AppParameterListItemOrTree
                       {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                        :isSingleSelect "not-a-boolean"})))
      (is (not (valid? apps/AppParameterListItemOrTree
                       {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                        :selectionCascade 123})))
      (is (not (valid? apps/AppParameterListItemOrTree
                       {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                        :arguments "not-a-vector"})))
      (is (not (valid? apps/AppParameterListItemOrTree
                       {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                        :groups "not-a-vector"})))
      (is (not (valid? apps/AppParameterListItemOrTree
                       {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                        :arguments [{:name "invalid-missing-id"}]})))
      (is (not (valid? apps/AppParameterListItemOrTree
                       {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                        :groups [{:name "invalid-missing-id"}]})))
      (is (not (valid? apps/AppParameterListItemOrTree
                       {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                        :extra-field "not-allowed"}))))))

(deftest test-AppParameterListItemRequest
  (testing "AppParameterListItemRequest validation"
    (testing "valid list item request - without id"
      (is (valid? apps/AppParameterListItemRequest {}))
      (is (valid? apps/AppParameterListItemRequest
                  {:name "small_genome"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:value "1"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:name "small_genome"
                   :value "1"
                   :description "Small genome (< 2 Mb)"
                   :display "Small Genome"
                   :isDefault false}))
      (is (valid? apps/AppParameterListItemRequest
                  {:name "large_genome"
                   :value "2"
                   :isDefault true}))
      (is (valid? apps/AppParameterListItemRequest
                  {:description "Medium genome (2-10 Mb)"
                   :display "Medium Genome"})))

    (testing "valid list item request - with id"
      (is (valid? apps/AppParameterListItemRequest
                  {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                   :name "small_genome"
                   :value "1"
                   :description "Small genome (< 2 Mb)"
                   :display "Small Genome"
                   :isDefault false}))
      (is (valid? apps/AppParameterListItemRequest
                  {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :name "large_genome"
                   :isDefault true})))

    (testing "valid list item request - various valid name values"
      (is (valid? apps/AppParameterListItemRequest
                  {:name "a"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:name "genome_size"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:name "genome-size"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:name "GenomeSize"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:name "genome.size"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:name "genome size with spaces"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:name "123"})))

    (testing "valid list item request - various valid value values"
      (is (valid? apps/AppParameterListItemRequest
                  {:value "0"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:value "1"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:value "true"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:value "false"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:value "complex-value-123"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:value "value with spaces"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:value ""})))

    (testing "valid list item request - various valid description values"
      (is (valid? apps/AppParameterListItemRequest
                  {:description "Short"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:description "A longer description with multiple words and punctuation!"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:description ""})))

    (testing "valid list item request - various valid display values"
      (is (valid? apps/AppParameterListItemRequest
                  {:display "Display"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:display "Display Label With Spaces"}))
      (is (valid? apps/AppParameterListItemRequest
                  {:display ""})))

    (testing "valid list item request - various valid isDefault values"
      (is (valid? apps/AppParameterListItemRequest
                  {:isDefault true}))
      (is (valid? apps/AppParameterListItemRequest
                  {:isDefault false})))

    (testing "valid list item request - all optional fields"
      (is (valid? apps/AppParameterListItemRequest
                  {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                   :name "complete_item"
                   :value "complete"
                   :description "Complete item with all fields"
                   :display "Complete Item"
                   :isDefault true})))

    (testing "invalid list item request - invalid id type"
      (is (not (valid? apps/AppParameterListItemRequest
                       {:id "not-a-uuid"})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:id 123})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:id nil})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:id true})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:id :keyword})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:id []})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:id {}}))))

    (testing "invalid list item request - invalid name type"
      (is (not (valid? apps/AppParameterListItemRequest
                       {:name 123})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:name true})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:name :keyword})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:name []})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:name {}})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:name nil}))))

    (testing "invalid list item request - invalid value type"
      (is (not (valid? apps/AppParameterListItemRequest
                       {:value 123})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:value true})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:value :keyword})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:value []})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:value {}})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:value nil}))))

    (testing "invalid list item request - invalid description type"
      (is (not (valid? apps/AppParameterListItemRequest
                       {:description 123})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:description true})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:description :keyword})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:description []})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:description {}})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:description nil}))))

    (testing "invalid list item request - invalid display type"
      (is (not (valid? apps/AppParameterListItemRequest
                       {:display 123})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:display true})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:display :keyword})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:display []})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:display {}})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:display nil}))))

    (testing "invalid list item request - invalid isDefault type"
      (is (not (valid? apps/AppParameterListItemRequest
                       {:isDefault "not-a-boolean"})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:isDefault 1})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:isDefault 0})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:isDefault "true"})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:isDefault "false"})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:isDefault :true})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:isDefault []})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:isDefault {}})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:isDefault nil}))))

    (testing "invalid list item request - multiple invalid fields"
      (is (not (valid? apps/AppParameterListItemRequest
                       {:id "not-a-uuid"
                        :name 123})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:name true
                        :value []
                        :isDefault "not-boolean"})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:id nil
                        :description {}
                        :display []}))))

    (testing "invalid list item request - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppParameterListItemRequest
                       {:extra-field "value"})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:name "valid_name"
                        :extra-field "value"})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                        :name "valid_name"
                        :extra-field "value"})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:unknown-key "unknown-value"})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:name "valid"
                        :label "not-a-valid-field"})))
      (is (not (valid? apps/AppParameterListItemRequest
                       {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                        :name "valid"
                        :type "not-a-valid-field"}))))

    (testing "invalid list item request - not a map"
      (is (not (valid? apps/AppParameterListItemRequest "string")))
      (is (not (valid? apps/AppParameterListItemRequest 123)))
      (is (not (valid? apps/AppParameterListItemRequest [])))
      (is (not (valid? apps/AppParameterListItemRequest nil)))
      (is (not (valid? apps/AppParameterListItemRequest true)))
      (is (not (valid? apps/AppParameterListItemRequest :keyword))))))

(deftest test-AppParameterListGroupRequest
  (testing "AppParameterListGroupRequest validation"
    (testing "valid list group request - empty map"
      (is (valid? apps/AppParameterListGroupRequest {})))

    (testing "valid list group request - without id"
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "genome_size_group"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "genome_size_group"
                   :value "size_group"
                   :description "Genome size selection group"
                   :display "Genome Size"
                   :isDefault false}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "options_group"
                   :description "Options for analysis"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:display "Display Only Group"})))

    (testing "valid list group request - with id"
      (is (valid? apps/AppParameterListGroupRequest
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "genome_size_group"
                   :value "size_group"
                   :description "Genome size selection group"
                   :display "Genome Size"
                   :isDefault false}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :name "options_group"
                   :isDefault true})))

    (testing "valid list group request - with arguments only"
      (is (valid? apps/AppParameterListGroupRequest
                  {:arguments []}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:arguments [{:name "small_genome"
                                :value "1"}]}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:arguments [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                                :name "small_genome"
                                :value "1"}]}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "genome_sizes"
                   :arguments [{:name "small_genome"
                                :value "1"
                                :description "Small genome (< 2 Mb)"
                                :display "Small Genome"
                                :isDefault false}
                               {:name "large_genome"
                                :value "2"
                                :description "Large genome (> 2 Mb)"
                                :display "Large Genome"
                                :isDefault true}]}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:id #uuid "abc12345-def6-7890-1234-567890abcdef"
                   :name "multiple_options"
                   :arguments [{:id #uuid "111e1111-a11a-11a1-a111-111111111111"
                                :name "option1"
                                :value "val1"}
                               {:id #uuid "222e2222-b22b-22b2-b222-222222222222"
                                :name "option2"
                                :value "val2"}
                               {:name "option3"
                                :value "val3"}]})))

    (testing "valid list group request - with groups only (recursive)"
      (is (valid? apps/AppParameterListGroupRequest
                  {:groups []}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:groups [{:name "sub_group"}]}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:groups [{:id #uuid "333e3333-c33c-33c3-c333-333333333333"
                             :name "sub_group"}]}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "parent_group"
                   :groups [{:name "child_group_1"}
                            {:name "child_group_2"}]}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                   :name "hierarchical_group"
                   :groups [{:id #uuid "555e5555-e55e-55e5-e555-555555555555"
                             :name "level1_group1"}
                            {:name "level1_group2"
                             :groups [{:name "level2_group1"}]}]})))

    (testing "valid list group request - with both arguments and groups"
      (is (valid? apps/AppParameterListGroupRequest
                  {:arguments []
                   :groups []}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "mixed_group"
                   :arguments [{:name "option1" :value "val1"}]
                   :groups [{:name "sub_group"}]}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:id #uuid "666e6666-f66f-66f6-f666-666666666666"
                   :name "complete_group"
                   :value "complete_value"
                   :description "Complete group with all features"
                   :display "Complete Group"
                   :isDefault false
                   :arguments [{:id #uuid "777e7777-a77a-77a7-a777-777777777777"
                                :name "arg1"
                                :value "val1"}
                               {:name "arg2"
                                :value "val2"}]
                   :groups [{:id #uuid "888e8888-a88a-88a8-a888-888888888888"
                             :name "nested_group1"}
                            {:name "nested_group2"
                             :arguments [{:name "nested_arg"}]}]})))

    (testing "valid list group request - deeply nested recursive structure"
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "root"
                   :groups [{:name "level1"
                             :groups [{:name "level2"
                                       :groups [{:name "level3"
                                                 :arguments [{:name "deep_arg"
                                                              :value "deep_val"}]}]}]}]}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:id #uuid "999e9999-a99a-99a9-a999-999999999999"
                   :name "complex_hierarchy"
                   :arguments [{:id #uuid "aaa1111a-b11b-11b1-b111-111111111aaa"
                                :name "root_arg"}]
                   :groups [{:id #uuid "bbb2222b-c22c-22c2-c222-222222222bbb"
                             :name "branch1"
                             :arguments [{:name "branch1_arg"}]
                             :groups [{:name "leaf1"
                                       :arguments [{:name "leaf1_arg"}]}
                                      {:name "leaf2"}]}
                            {:name "branch2"
                             :groups [{:name "branch2_leaf"}]}]})))

    (testing "valid list group request - various valid name values"
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "a"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "group_name"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "group-name"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "GroupName"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "group.name"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "group name with spaces"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:name "123"})))

    (testing "valid list group request - various valid value values"
      (is (valid? apps/AppParameterListGroupRequest
                  {:value "simple"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:value "value_with_underscore"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:value "value-with-dash"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:value ""})))

    (testing "valid list group request - various valid description values"
      (is (valid? apps/AppParameterListGroupRequest
                  {:description "Short description"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:description "A longer description with multiple words, punctuation, and numbers like 123!"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:description ""})))

    (testing "valid list group request - various valid display values"
      (is (valid? apps/AppParameterListGroupRequest
                  {:display "Display"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:display "Display Label With Spaces"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:display "Display: With Punctuation!"}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:display ""})))

    (testing "valid list group request - various valid isDefault values"
      (is (valid? apps/AppParameterListGroupRequest
                  {:isDefault true}))
      (is (valid? apps/AppParameterListGroupRequest
                  {:isDefault false})))

    (testing "valid list group request - all optional fields"
      (is (valid? apps/AppParameterListGroupRequest
                  {:id #uuid "ccc3333c-d33d-33d3-d333-333333333ccc"
                   :name "complete_group"
                   :value "complete_value"
                   :description "Complete group with all optional fields"
                   :display "Complete Group"
                   :isDefault true
                   :arguments [{:id #uuid "ddd4444d-e44e-44e4-e444-444444444ddd"
                                :name "complete_arg"
                                :value "complete_arg_value"
                                :description "Complete argument"
                                :display "Complete Argument"
                                :isDefault false}]
                   :groups [{:id #uuid "eee5555e-f55f-55f5-f555-555555555eee"
                             :name "complete_nested_group"
                             :value "nested_value"
                             :description "Nested group description"
                             :display "Nested Group"
                             :isDefault false}]})))

    (testing "invalid list group request - invalid id type"
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:id "not-a-uuid"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:id 123})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:id nil})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:id true})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:id :keyword})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:id []})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:id {}}))))

    (testing "invalid list group request - invalid name type"
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name 123})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name true})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name :keyword})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name []})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name {}})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name nil}))))

    (testing "invalid list group request - invalid value type"
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:value 123})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:value true})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:value :keyword})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:value []})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:value {}})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:value nil}))))

    (testing "invalid list group request - invalid description type"
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:description 123})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:description true})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:description :keyword})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:description []})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:description {}})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:description nil}))))

    (testing "invalid list group request - invalid display type"
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:display 123})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:display true})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:display :keyword})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:display []})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:display {}})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:display nil}))))

    (testing "invalid list group request - invalid isDefault type"
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:isDefault "not-a-boolean"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:isDefault 1})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:isDefault 0})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:isDefault "true"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:isDefault "false"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:isDefault :true})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:isDefault []})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:isDefault {}})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:isDefault nil}))))

    (testing "invalid list group request - arguments not a vector"
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:arguments "not-a-vector"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:arguments {:name "map-not-allowed"}})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:arguments 123})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:arguments nil})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:arguments true})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:arguments :keyword}))))

    (testing "invalid list group request - groups not a vector"
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups "not-a-vector"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups {:name "map-not-allowed"}})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups 123})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups nil})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups true})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups :keyword}))))

    (testing "invalid list group request - arguments contains invalid AppParameterListItemRequest"
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:arguments [{:id "not-a-uuid"}]})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:arguments [{:name 123}]})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:arguments [{:isDefault "not-a-boolean"}]})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:arguments [{:extra-field "not-allowed"}]})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:arguments [{:name "valid"}
                                    {:id "not-a-uuid"}]})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name "group"
                        :arguments [{:name "valid1"}
                                    {:name 123}
                                    {:name "valid2"}]}))))

    (testing "invalid list group request - groups contains invalid AppParameterListGroupRequest"
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups [{:id "not-a-uuid"}]})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups [{:name 123}]})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups [{:isDefault "not-a-boolean"}]})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups [{:extra-field "not-allowed"}]})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups [{:name "valid"}
                                 {:id "not-a-uuid"}]})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name "parent"
                        :groups [{:name "valid1"}
                                 {:arguments "not-a-vector"}
                                 {:name "valid2"}]}))))

    (testing "invalid list group request - nested groups with invalid structure"
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups [{:groups [{:name 123}]}]})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups [{:name "level1"
                                  :groups [{:arguments [{:id "not-a-uuid"}]}]}]})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name "root"
                        :groups [{:name "level1"
                                  :groups [{:name "level2"
                                            :groups [{:name 456}]}]}]})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups [{:groups [{:groups [{:isDefault "invalid"}]}]}]}))))

    (testing "invalid list group request - multiple invalid fields"
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:id "not-a-uuid"
                        :name 123})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name true
                        :value []
                        :isDefault "not-boolean"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:id nil
                        :description {}
                        :display []
                        :arguments "not-a-vector"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name 123
                        :arguments "not-a-vector"
                        :groups "not-a-vector"}))))

    (testing "invalid list group request - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:extra-field "value"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name "valid_name"
                        :extra-field "value"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "valid_name"
                        :extra-field "value"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:unknown-key "unknown-value"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name "valid"
                        :label "not-a-valid-field"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:name "valid"
                        :type "not-a-valid-field"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:arguments []
                        :invalid-field "value"})))
      (is (not (valid? apps/AppParameterListGroupRequest
                       {:groups []
                        :extra "not-allowed"}))))

    (testing "invalid list group request - not a map"
      (is (not (valid? apps/AppParameterListGroupRequest "string")))
      (is (not (valid? apps/AppParameterListGroupRequest 123)))
      (is (not (valid? apps/AppParameterListGroupRequest [])))
      (is (not (valid? apps/AppParameterListGroupRequest nil)))
      (is (not (valid? apps/AppParameterListGroupRequest true)))
      (is (not (valid? apps/AppParameterListGroupRequest :keyword))))))

(deftest test-AppParameterListItemOrTreeRequest
  (testing "AppParameterListItemOrTreeRequest validation"
    (testing "valid list item or tree request - empty map"
      (is (valid? apps/AppParameterListItemOrTreeRequest {})))

    (testing "valid list item or tree request - simple item without id"
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:name "organism"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:value "human"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:name "organism"
                   :value "human"
                   :description "Select organism type"
                   :display "Organism"
                   :isDefault false}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:display "Display Only"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:description "Description Only"})))

    (testing "valid list item or tree request - simple item with id"
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                   :name "organism"
                   :value "human"
                   :description "Select organism type"
                   :display "Organism"
                   :isDefault false}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :name "species"
                   :isDefault true})))

    (testing "valid list item or tree request - tree selector fields without id"
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:isSingleSelect true}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:isSingleSelect false}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:selectionCascade "up"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:selectionCascade "down"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:isSingleSelect true
                   :selectionCascade "up"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:name "tree_root"
                   :display "Tree Selector"
                   :isSingleSelect false
                   :selectionCascade "down"})))

    (testing "valid list item or tree request - tree selector fields with id"
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:id #uuid "abc12345-def6-7890-1234-567890abcdef"
                   :isSingleSelect true}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:id #uuid "def67890-abc1-2345-6789-0abcdef12345"
                   :isSingleSelect true
                   :selectionCascade "up"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                   :name "full_tree"
                   :display "Full Tree Selector"
                   :isSingleSelect false
                   :selectionCascade "down"})))

    (testing "valid list item or tree request - with arguments only (using AppParameterListItemRequest)"
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:arguments []}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:arguments [{:name "option1"}]}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:arguments [{:name "option1"
                                :value "val1"}]}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:arguments [{:id #uuid "111e1111-a11a-11a1-a111-111111111111"
                                :name "option1"
                                :value "val1"}
                               {:id #uuid "222e2222-b22b-22b2-b222-222222222222"
                                :name "option2"
                                :value "val2"}]}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:name "tree_selector"
                   :arguments [{:name "option1"
                                :value "val1"
                                :description "First option"
                                :display "Option 1"
                                :isDefault false}
                               {:name "option2"
                                :value "val2"
                                :description "Second option"
                                :display "Option 2"
                                :isDefault true}]}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:id #uuid "333e3333-c33c-33c3-c333-333333333333"
                   :name "selector_with_args"
                   :isSingleSelect true
                   :arguments [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                :name "arg1"
                                :value "val1"}
                               {:name "arg2"
                                :value "val2"}]})))

    (testing "valid list item or tree request - with groups only (using AppParameterListGroupRequest)"
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:groups []}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:groups [{:name "group1"}]}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:groups [{:id #uuid "555e5555-e55e-55e5-e555-555555555555"
                             :name "group1"}]}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:groups [{:name "group1"}
                            {:name "group2"}]}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:name "grouped_tree"
                   :groups [{:id #uuid "666e6666-f66f-66f6-f666-666666666666"
                             :name "group1"
                             :display "Group 1"}
                            {:name "group2"
                             :arguments [{:name "nested_option"}]}]}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:id #uuid "777e7777-a77a-77a7-a777-777777777777"
                   :name "hierarchical"
                   :selectionCascade "up"
                   :groups [{:name "level1"
                             :groups [{:name "level2"}]}]})))

    (testing "valid list item or tree request - with both arguments and groups"
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:arguments []
                   :groups []}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:arguments [{:name "arg1"}]
                   :groups [{:name "group1"}]}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:name "mixed_tree"
                   :arguments [{:name "root_arg"
                                :value "root_val"}]
                   :groups [{:name "root_group"}]}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:id #uuid "888e8888-a88a-88a8-a888-888888888888"
                   :name "complete_tree"
                   :display "Complete Tree Selector"
                   :isSingleSelect false
                   :selectionCascade "down"
                   :arguments [{:id #uuid "999e9999-a99a-99a9-a999-999999999999"
                                :name "root_option"
                                :value "root_val"
                                :display "Root Option"}]
                   :groups [{:id #uuid "aaa1111a-b11b-11b1-b111-111111111aaa"
                             :name "root_group"
                             :display "Root Group"
                             :arguments [{:name "group_arg"}]}]})))

    (testing "valid list item or tree request - complex nested structure"
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:id #uuid "bbb2222b-c22c-22c2-c222-222222222bbb"
                   :name "complex_selector"
                   :display "Complex Selector"
                   :description "A complex tree selector with all features"
                   :isSingleSelect true
                   :selectionCascade "up"
                   :isDefault false
                   :arguments [{:id #uuid "ccc3333c-d33d-33d3-d333-333333333ccc"
                                :name "root_arg1"
                                :value "val1"
                                :description "First root argument"
                                :display "Root Arg 1"
                                :isDefault false}
                               {:name "root_arg2"
                                :value "val2"}]
                   :groups [{:id #uuid "ddd4444d-e44e-44e4-e444-444444444ddd"
                             :name "group1"
                             :display "Group 1"
                             :arguments [{:name "g1_arg1"}
                                         {:name "g1_arg2"}]
                             :groups [{:name "subgroup1"}
                                      {:name "subgroup2"
                                       :arguments [{:name "sg2_arg"}]}]}
                            {:name "group2"
                             :groups [{:name "nested_group"}]}]})))

    (testing "valid list item or tree request - deeply nested groups"
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:groups [{:name "level1"
                             :groups [{:name "level2"
                                       :groups [{:name "level3"
                                                 :groups [{:name "level4"
                                                           :arguments [{:name "deep_arg"}]}]}]}]}]}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:id #uuid "eee5555e-f55f-55f5-f555-555555555eee"
                   :name "deep_tree"
                   :arguments [{:name "root_arg"}]
                   :groups [{:name "branch1"
                             :arguments [{:name "b1_arg"}]
                             :groups [{:name "leaf1"
                                       :arguments [{:id #uuid "fff6666f-a66a-66a6-a666-666666666fff"
                                                    :name "l1_arg"
                                                    :value "l1_val"}]}
                                      {:name "leaf2"}]}
                            {:name "branch2"
                             :groups [{:name "b2_leaf"
                                       :arguments [{:name "b2l_arg"}]}]}]})))

    (testing "valid list item or tree request - various valid field combinations"
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:name "a"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:name "selector_name"
                   :value "selector_value"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:description "Long description with multiple words and special characters!"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:display "Display Label"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:isDefault true}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:isDefault false}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:isSingleSelect true}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:isSingleSelect false}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:selectionCascade "up"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:selectionCascade "down"}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:selectionCascade ""}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:name ""}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:value ""}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:description ""}))
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:display ""})))

    (testing "valid list item or tree request - all optional fields present"
      (is (valid? apps/AppParameterListItemOrTreeRequest
                  {:id #uuid "111a111a-b11b-11c1-d111-111111111111"
                   :name "complete_selector"
                   :value "complete_value"
                   :description "Complete selector with all optional fields"
                   :display "Complete Selector"
                   :isDefault true
                   :isSingleSelect false
                   :selectionCascade "down"
                   :arguments [{:id #uuid "222b222b-c22c-22d2-e222-222222222222"
                                :name "complete_arg"
                                :value "complete_arg_value"
                                :description "Complete argument with all fields"
                                :display "Complete Argument"
                                :isDefault true}]
                   :groups [{:id #uuid "333c333c-d33d-33e3-f333-333333333333"
                             :name "complete_group"
                             :value "complete_group_value"
                             :description "Complete group with all fields"
                             :display "Complete Group"
                             :isDefault false
                             :arguments [{:name "cg_arg"}]
                             :groups [{:name "cg_nested"}]}]})))

    (testing "invalid list item or tree request - invalid id type"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:id "not-a-uuid"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:id 123})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:id nil})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:id true})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:id :keyword})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:id []})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:id {}}))))

    (testing "invalid list item or tree request - invalid name type"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:name 123})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:name true})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:name :keyword})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:name []})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:name {}})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:name nil}))))

    (testing "invalid list item or tree request - invalid value type"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:value 123})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:value true})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:value :keyword})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:value []})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:value {}})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:value nil}))))

    (testing "invalid list item or tree request - invalid description type"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:description 123})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:description true})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:description :keyword})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:description []})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:description {}})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:description nil}))))

    (testing "invalid list item or tree request - invalid display type"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:display 123})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:display true})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:display :keyword})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:display []})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:display {}})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:display nil}))))

    (testing "invalid list item or tree request - invalid isDefault type"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isDefault "not-a-boolean"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isDefault 1})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isDefault 0})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isDefault "true"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isDefault "false"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isDefault :true})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isDefault []})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isDefault {}})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isDefault nil}))))

    (testing "invalid list item or tree request - invalid isSingleSelect type"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isSingleSelect "not-a-boolean"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isSingleSelect 1})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isSingleSelect "true"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isSingleSelect nil})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isSingleSelect []})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isSingleSelect {}}))))

    (testing "invalid list item or tree request - invalid selectionCascade type"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:selectionCascade 123})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:selectionCascade true})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:selectionCascade :keyword})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:selectionCascade []})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:selectionCascade {}})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:selectionCascade nil}))))

    (testing "invalid list item or tree request - arguments not a vector"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments "not-a-vector"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments {:name "map-not-allowed"}})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments 123})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments nil})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments true})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments :keyword}))))

    (testing "invalid list item or tree request - groups not a vector"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups "not-a-vector"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups {:name "map-not-allowed"}})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups 123})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups nil})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups true})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups :keyword}))))

    (testing "invalid list item or tree request - arguments contains invalid AppParameterListItemRequest"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments [{:id "not-a-uuid"}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments [{:name 123}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments [{:isDefault "not-a-boolean"}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments [{:extra-field "not-allowed"}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments [{:name "valid"}
                                    {:id "not-a-uuid"}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments [{:name "valid1"}
                                    {:value 123}
                                    {:name "valid2"}]}))))

    (testing "invalid list item or tree request - groups contains invalid AppParameterListGroupRequest"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups [{:id "not-a-uuid"}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups [{:name 123}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups [{:isDefault "not-a-boolean"}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups [{:extra-field "not-allowed"}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups [{:name "valid"}
                                 {:id "not-a-uuid"}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups [{:name "valid1"}
                                 {:arguments "not-a-vector"}
                                 {:name "valid2"}]}))))

    (testing "invalid list item or tree request - nested groups with invalid structure"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups [{:groups [{:name 123}]}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups [{:name "level1"
                                  :groups [{:arguments [{:id "not-a-uuid"}]}]}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups [{:name "level1"
                                  :groups [{:name "level2"
                                            :groups [{:isDefault "invalid"}]}]}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments [{:name "arg"}]
                        :groups [{:groups [{:groups [{:value []}]}]}]}))))

    (testing "invalid list item or tree request - mixed invalid arguments and groups"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments [{:name 123}]
                        :groups [{:name "valid"}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments [{:name "valid"}]
                        :groups [{:name 456}]})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments [{:id "not-uuid"}]
                        :groups [{:id "not-uuid"}]}))))

    (testing "invalid list item or tree request - multiple invalid fields"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:id "not-a-uuid"
                        :name 123})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:name true
                        :value []
                        :isDefault "not-boolean"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:id nil
                        :description {}
                        :display []
                        :isSingleSelect "not-boolean"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:name 123
                        :selectionCascade 456
                        :arguments "not-a-vector"
                        :groups "not-a-vector"}))))

    (testing "invalid list item or tree request - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:extra-field "value"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:name "valid_name"
                        :extra-field "value"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "valid_name"
                        :unknown-field "value"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:label "not-a-valid-field"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:type "not-a-valid-field"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:arguments []
                        :invalid-field "value"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:groups []
                        :extra "not-allowed"})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:isSingleSelect true
                        :order 1})))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest
                       {:selectionCascade "up"
                        :required true}))))

    (testing "invalid list item or tree request - not a map"
      (is (not (valid? apps/AppParameterListItemOrTreeRequest "string")))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest 123)))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest [])))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest nil)))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest true)))
      (is (not (valid? apps/AppParameterListItemOrTreeRequest :keyword))))))

(deftest test-AppParameterValidator
  (testing "AppParameterValidator validation"
    (testing "valid validator"
      (is (valid? apps/AppParameterValidator
                  {:type "IntAbove"
                   :params [0]}))
      (is (valid? apps/AppParameterValidator
                  {:type "StringMatches"
                   :params ["^[a-zA-Z0-9]+$"]}))
      (is (valid? apps/AppParameterValidator
                  {:type "Required"
                   :params []})))

    (testing "invalid validator"
      (is (not (valid? apps/AppParameterValidator {})))
      (is (not (valid? apps/AppParameterValidator {:type "IntAbove"})))
      (is (not (valid? apps/AppParameterValidator {:params [0]})))
      (is (not (valid? apps/AppParameterValidator
                       {:type 123
                        :params [0]})))
      (is (not (valid? apps/AppParameterValidator
                       {:type "IntAbove"
                        :params "not-a-vector"})))
      (is (not (valid? apps/AppParameterValidator
                       {:type "IntAbove"
                        :params [0]
                        :extra-field "not-allowed"}))))))

(deftest test-AppFileParameters
  (testing "AppFileParameters validation"
    (testing "valid file parameters"
      (is (valid? apps/AppFileParameters {}))
      (is (valid? apps/AppFileParameters
                  {:format "fasta"}))
      (is (valid? apps/AppFileParameters
                  {:format "fastq"
                   :file_info_type "SequenceFile"}))
      (is (valid? apps/AppFileParameters
                  {:format "txt"
                   :file_info_type "TextFile"
                   :is_implicit false
                   :repeat_option_flag false
                   :data_source "file"
                   :retain true}))
      (is (valid? apps/AppFileParameters
                  {:is_implicit true
                   :data_source "stdout"}))
      (is (valid? apps/AppFileParameters
                  {:repeat_option_flag true
                   :retain false})))

    (testing "invalid file parameters"
      (is (not (valid? apps/AppFileParameters
                       {:format 123})))
      (is (not (valid? apps/AppFileParameters
                       {:file_info_type true})))
      (is (not (valid? apps/AppFileParameters
                       {:is_implicit "not-a-boolean"})))
      (is (not (valid? apps/AppFileParameters
                       {:repeat_option_flag "not-a-boolean"})))
      (is (not (valid? apps/AppFileParameters
                       {:data_source 456})))
      (is (not (valid? apps/AppFileParameters
                       {:retain "not-a-boolean"})))
      (is (not (valid? apps/AppFileParameters
                       {:extra-field "not-allowed"}))))))

(deftest test-AppParameter
  (testing "AppParameter validation"
    (testing "valid parameter"
      (is (valid? apps/AppParameter
                  {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                   :type "FileInput"}))
      (is (valid? apps/AppParameter
                  {:id #uuid "111e1111-a11a-11a1-a111-111111111111"
                   :type "TextInput"
                   :name "--input"
                   :label "Input File"
                   :description "Specify the input file for processing"
                   :required true
                   :isVisible true}))
      (is (valid? apps/AppParameter
                  {:id #uuid "222e2222-b22b-22b2-b222-222222222222"
                   :type "IntegerInput"
                   :defaultValue 10
                   :value 20
                   :order 1
                   :omit_if_blank false}))
      (is (valid? apps/AppParameter
                  {:id #uuid "333e3333-c33c-33c3-c333-333333333333"
                   :type "FileInput"
                   :file_parameters {:format "fasta"
                                     :file_info_type "SequenceAlignment"
                                     :retain true}}))
      (is (valid? apps/AppParameter
                  {:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                   :type "TextSelection"
                   :arguments [{:id #uuid "555e5555-e55e-55e5-e555-555555555555"
                                :name "option1"
                                :value "val1"}
                               {:id #uuid "666e6666-f66f-66f6-f666-666666666666"
                                :name "option2"
                                :value "val2"}]}))
      (is (valid? apps/AppParameter
                  {:id #uuid "777e7777-a77a-77a7-a777-777777777777"
                   :type "IntegerInput"
                   :validators [{:type "IntAbove"
                                 :params [0]}
                                {:type "IntBelow"
                                 :params [100]}]})))

    (testing "invalid parameter"
      (is (not (valid? apps/AppParameter {})))
      (is (not (valid? apps/AppParameter {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"})))
      (is (not (valid? apps/AppParameter {:type "FileInput"})))
      (is (not (valid? apps/AppParameter
                       {:id "not-a-uuid"
                        :type "FileInput"})))
      (is (not (valid? apps/AppParameter
                       {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                        :type 123})))
      (is (not (valid? apps/AppParameter
                       {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                        :type "FileInput"
                        :name 123})))
      (is (not (valid? apps/AppParameter
                       {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                        :type "FileInput"
                        :label true})))
      (is (not (valid? apps/AppParameter
                       {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                        :type "FileInput"
                        :order "not-an-int"})))
      (is (not (valid? apps/AppParameter
                       {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                        :type "FileInput"
                        :required "not-a-boolean"})))
      (is (not (valid? apps/AppParameter
                       {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                        :type "FileInput"
                        :file_parameters "not-a-map"})))
      (is (not (valid? apps/AppParameter
                       {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                        :type "FileInput"
                        :arguments "not-a-vector"})))
      (is (not (valid? apps/AppParameter
                       {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                        :type "FileInput"
                        :validators "not-a-vector"})))
      (is (not (valid? apps/AppParameter
                       {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                        :type "FileInput"
                        :extra-field "not-allowed"}))))))

(deftest test-AppGroup
  (testing "AppGroup validation"
    (testing "valid group"
      (is (valid? apps/AppGroup
                  {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                   :label "Input Parameters"}))
      (is (valid? apps/AppGroup
                  {:id #uuid "111e1111-a11a-11a1-a111-111111111111"
                   :label "Output Parameters"
                   :name "output_parameters"
                   :description "Output file and result parameters"
                   :isVisible true}))
      (is (valid? apps/AppGroup
                  {:id #uuid "222e2222-b22b-22b2-b222-222222222222"
                   :label "Settings"
                   :parameters [{:id #uuid "333e3333-c33c-33c3-c333-333333333333"
                                 :type "TextInput"
                                 :label "Name"}
                                {:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :type "IntegerInput"
                                 :label "Count"}]}))
      (is (valid? apps/AppGroup
                  {:id #uuid "555e5555-e55e-55e5-e555-555555555555"
                   :label "Advanced"
                   :name "advanced"
                   :isVisible false
                   :parameters []})))

    (testing "invalid group"
      (is (not (valid? apps/AppGroup {})))
      (is (not (valid? apps/AppGroup {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"})))
      (is (not (valid? apps/AppGroup {:label "Input Parameters"})))
      (is (not (valid? apps/AppGroup
                       {:id "not-a-uuid"
                        :label "Input Parameters"})))
      (is (not (valid? apps/AppGroup
                       {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                        :label 123})))
      (is (not (valid? apps/AppGroup
                       {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                        :label "Input Parameters"
                        :name true})))
      (is (not (valid? apps/AppGroup
                       {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                        :label "Input Parameters"
                        :description 456})))
      (is (not (valid? apps/AppGroup
                       {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                        :label "Input Parameters"
                        :isVisible "not-a-boolean"})))
      (is (not (valid? apps/AppGroup
                       {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                        :label "Input Parameters"
                        :parameters "not-a-vector"})))
      (is (not (valid? apps/AppGroup
                       {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                        :label "Input Parameters"
                        :parameters [{:id #uuid "333e3333-c33c-33c3-c333-333333333333"}]})))
      (is (not (valid? apps/AppGroup
                       {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                        :label "Input Parameters"
                        :extra-field "not-allowed"}))))))

(deftest test-AppVersionDetails
  (testing "AppVersionDetails validation"
    (testing "valid version details"
      (is (valid? apps/AppVersionDetails
                  {:version "1.0.0"
                   :version_id #uuid "123e4567-e89b-12d3-a456-426614174000"}))
      (is (valid? apps/AppVersionDetails
                  {:version "2.5.1"
                   :version_id #uuid "987e6543-e21b-32c1-b456-426614174000"})))

    (testing "invalid version details"
      (is (not (valid? apps/AppVersionDetails {})))
      (is (not (valid? apps/AppVersionDetails {:version "1.0.0"})))
      (is (not (valid? apps/AppVersionDetails {:version_id #uuid "123e4567-e89b-12d3-a456-426614174000"})))
      (is (not (valid? apps/AppVersionDetails
                       {:version 123
                        :version_id #uuid "123e4567-e89b-12d3-a456-426614174000"})))
      (is (not (valid? apps/AppVersionDetails
                       {:version "1.0.0"
                        :version_id "not-a-uuid"})))
      (is (not (valid? apps/AppVersionDetails
                       {:version "1.0.0"
                        :version_id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :extra-field "not-allowed"}))))))

(deftest test-AppVersionListing
  (testing "AppVersionListing validation"
    (testing "valid version listing"
      (is (valid? apps/AppVersionListing {}))
      (is (valid? apps/AppVersionListing
                  {:versions []}))
      (is (valid? apps/AppVersionListing
                  {:versions [{:version "1.0.0"
                               :version_id #uuid "123e4567-e89b-12d3-a456-426614174000"}]}))
      (is (valid? apps/AppVersionListing
                  {:versions [{:version "1.0.0"
                               :version_id #uuid "123e4567-e89b-12d3-a456-426614174000"}
                              {:version "2.0.0"
                               :version_id #uuid "987e6543-e21b-32c1-b456-426614174000"}
                              {:version "3.0.0"
                               :version_id #uuid "abc12345-def6-7890-abcd-ef1234567890"}]})))

    (testing "invalid version listing"
      (is (not (valid? apps/AppVersionListing
                       {:versions "not-a-vector"})))
      (is (not (valid? apps/AppVersionListing
                       {:versions [{}]})))
      (is (not (valid? apps/AppVersionListing
                       {:versions [{:version "1.0.0"}]})))
      (is (not (valid? apps/AppVersionListing
                       {:versions [{:version_id #uuid "123e4567-e89b-12d3-a456-426614174000"}]})))
      (is (not (valid? apps/AppVersionListing
                       {:versions [{:version "1.0.0"
                                    :version_id #uuid "123e4567-e89b-12d3-a456-426614174000"}
                                   {:version "2.0.0"}]})))
      (is (not (valid? apps/AppVersionListing
                       {:extra-field "not-allowed"}))))))

(deftest test-AppVersionOrderRequest
  (testing "AppVersionOrderRequest validation"
    (testing "valid version order request"
      (is (valid? apps/AppVersionOrderRequest
                  {:versions []}))
      (is (valid? apps/AppVersionOrderRequest
                  {:versions [{:version "1.0.0"
                               :version_id #uuid "123e4567-e89b-12d3-a456-426614174000"}]}))
      (is (valid? apps/AppVersionOrderRequest
                  {:versions [{:version_id #uuid "123e4567-e89b-12d3-a456-426614174000"}]}))
      (is (valid? apps/AppVersionOrderRequest
                  {:versions [{:version "3.0.0"
                               :version_id #uuid "abc12345-def6-7890-abcd-ef1234567890"}
                              {:version_id #uuid "987e6543-e21b-32c1-b456-426614174000"}
                              {:version "1.0.0"
                               :version_id #uuid "123e4567-e89b-12d3-a456-426614174000"}]})))

    (testing "invalid version order request"
      (is (not (valid? apps/AppVersionOrderRequest {})))
      (is (not (valid? apps/AppVersionOrderRequest
                       {:versions "not-a-vector"})))
      (is (not (valid? apps/AppVersionOrderRequest
                       {:versions [{}]})))
      (is (not (valid? apps/AppVersionOrderRequest
                       {:versions [{:version "1.0.0"}]})))
      (is (not (valid? apps/AppVersionOrderRequest
                       {:versions [{:version "1.0.0"
                                    :version_id #uuid "123e4567-e89b-12d3-a456-426614174000"}
                                   {:version "2.0.0"}]})))
      (is (not (valid? apps/AppVersionOrderRequest
                       {:versions []
                        :extra-field "not-allowed"}))))))

(deftest test-AppBase
  (testing "AppBase validation"
    (testing "valid app base"
      (is (valid? apps/AppBase
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool"}))
      (is (valid? apps/AppBase
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "FastQC"
                   :description "Quality control tool for high throughput sequence data"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"}))
      (is (valid? apps/AppBase
                  {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                   :name "BWA"
                   :description "Burrows-Wheeler Aligner"
                   :system_id "de"}))
      (is (valid? apps/AppBase
                  {:id #uuid "111e1111-a11a-11a1-a111-111111111111"
                   :name "Samtools"
                   :description "Tools for manipulating SAM/BAM files"
                   :version "1.2.0"
                   :version_id #uuid "222e2222-b22b-22b2-b222-222222222222"}))
      (is (valid? apps/AppBase
                  {:id #uuid "333e3333-c33c-33c3-c333-333333333333"
                   :name "Complete App"
                   :description "An app with all fields"
                   :integration_date #inst "2024-01-01T00:00:00.000-00:00"
                   :edited_date #inst "2024-12-31T23:59:59.000-00:00"
                   :system_id "condor"
                   :version "2.0.0"
                   :version_id #uuid "444e4444-d44d-44d4-d444-444444444444"})))

    (testing "invalid app base"
      (is (not (valid? apps/AppBase {})))
      (is (not (valid? apps/AppBase
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"})))
      (is (not (valid? apps/AppBase
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :description "BLAST tool"})))
      (is (not (valid? apps/AppBase
                       {:name "BLAST"
                        :description "BLAST tool"})))
      (is (not (valid? apps/AppBase
                       {:id "not-a-uuid"
                        :name "BLAST"
                        :description "BLAST tool"})))
      (is (not (valid? apps/AppBase
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name 123
                        :description "BLAST tool"})))
      (is (not (valid? apps/AppBase
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description true})))
      (is (not (valid? apps/AppBase
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :integration_date "not-a-date"})))
      (is (not (valid? apps/AppBase
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :edited_date 12345})))
      (is (not (valid? apps/AppBase
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :system_id 123})))
      (is (not (valid? apps/AppBase
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :version 123})))
      (is (not (valid? apps/AppBase
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :version_id "not-a-uuid"})))
      (is (not (valid? apps/AppBase
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :extra-field "not-allowed"}))))))

(deftest test-AppLimitCheckResult
  (testing "AppLimitCheckResult validation"
    (testing "valid limit check result"
      (is (valid? apps/AppLimitCheckResult
                  {:limitCheckID "concurrent-job-limit"
                   :reasonCodes ["MAX_JOBS_EXCEEDED"]
                   :additionalInfo {:current_jobs 10 :max_jobs 5}}))
      (is (valid? apps/AppLimitCheckResult
                  {:limitCheckID "quota-limit"
                   :reasonCodes ["QUOTA_EXCEEDED" "STORAGE_FULL"]
                   :additionalInfo {:quota_used 100 :quota_limit 100}}))
      (is (valid? apps/AppLimitCheckResult
                  {:limitCheckID "test-check"
                   :reasonCodes []
                   :additionalInfo nil}))
      (is (valid? apps/AppLimitCheckResult
                  {:limitCheckID "complex-check"
                   :reasonCodes ["CODE1" "CODE2" "CODE3"]
                   :additionalInfo {:nested {:data {:structure true}}}})))

    (testing "invalid limit check result"
      (is (not (valid? apps/AppLimitCheckResult {})))
      (is (not (valid? apps/AppLimitCheckResult
                       {:limitCheckID "test"
                        :reasonCodes ["CODE1"]})))
      (is (not (valid? apps/AppLimitCheckResult
                       {:limitCheckID "test"
                        :additionalInfo {}})))
      (is (not (valid? apps/AppLimitCheckResult
                       {:reasonCodes ["CODE1"]
                        :additionalInfo {}})))
      (is (not (valid? apps/AppLimitCheckResult
                       {:limitCheckID 123
                        :reasonCodes ["CODE1"]
                        :additionalInfo {}})))
      (is (not (valid? apps/AppLimitCheckResult
                       {:limitCheckID "test"
                        :reasonCodes "not-a-vector"
                        :additionalInfo {}})))
      (is (not (valid? apps/AppLimitCheckResult
                       {:limitCheckID "test"
                        :reasonCodes [123]
                        :additionalInfo {}})))
      (is (not (valid? apps/AppLimitCheckResult
                       {:limitCheckID "test"
                        :reasonCodes ["CODE1"]
                        :additionalInfo {}
                        :extra-field "not-allowed"}))))))

(deftest test-AppLimitCheckResultSummary
  (testing "AppLimitCheckResultSummary validation"
    (testing "valid limit check result summary"
      (is (valid? apps/AppLimitCheckResultSummary
                  {:canRun true
                   :results []}))
      (is (valid? apps/AppLimitCheckResultSummary
                  {:canRun false
                   :results [{:limitCheckID "concurrent-job-limit"
                              :reasonCodes ["MAX_JOBS_EXCEEDED"]
                              :additionalInfo {:current_jobs 10 :max_jobs 5}}]}))
      (is (valid? apps/AppLimitCheckResultSummary
                  {:canRun false
                   :results [{:limitCheckID "check1"
                              :reasonCodes ["CODE1"]
                              :additionalInfo nil}
                             {:limitCheckID "check2"
                              :reasonCodes ["CODE2" "CODE3"]
                              :additionalInfo {:data "value"}}]})))

    (testing "invalid limit check result summary"
      (is (not (valid? apps/AppLimitCheckResultSummary {})))
      (is (not (valid? apps/AppLimitCheckResultSummary
                       {:canRun true})))
      (is (not (valid? apps/AppLimitCheckResultSummary
                       {:results []})))
      (is (not (valid? apps/AppLimitCheckResultSummary
                       {:canRun "not-a-boolean"
                        :results []})))
      (is (not (valid? apps/AppLimitCheckResultSummary
                       {:canRun true
                        :results "not-a-vector"})))
      (is (not (valid? apps/AppLimitCheckResultSummary
                       {:canRun true
                        :results [{}]})))
      (is (not (valid? apps/AppLimitCheckResultSummary
                       {:canRun true
                        :results [{:limitCheckID "test"
                                   :reasonCodes ["CODE1"]}]})))
      (is (not (valid? apps/AppLimitCheckResultSummary
                       {:canRun true
                        :results []
                        :extra-field "not-allowed"}))))))

(deftest test-AppLimitChecks
  (testing "AppLimitChecks validation"
    (testing "valid limit checks"
      (is (valid? apps/AppLimitChecks {}))
      (is (valid? apps/AppLimitChecks
                  {:limitChecks {:canRun true
                                 :results []}}))
      (is (valid? apps/AppLimitChecks
                  {:limitChecks {:canRun false
                                 :results [{:limitCheckID "concurrent-job-limit"
                                            :reasonCodes ["MAX_JOBS_EXCEEDED"]
                                            :additionalInfo {:current_jobs 10 :max_jobs 5}}]}})))

    (testing "invalid limit checks"
      (is (not (valid? apps/AppLimitChecks
                       {:limitChecks "not-a-map"})))
      (is (not (valid? apps/AppLimitChecks
                       {:limitChecks {}})))
      (is (not (valid? apps/AppLimitChecks
                       {:limitChecks {:canRun true}})))
      (is (not (valid? apps/AppLimitChecks
                       {:limitChecks {:results []}})))
      (is (not (valid? apps/AppLimitChecks
                       {:limitChecks {:canRun true
                                      :results []
                                      :extra-field "not-allowed"}})))
      (is (not (valid? apps/AppLimitChecks
                       {:extra-field "not-allowed"}))))))

(deftest test-App
  (testing "App validation"
    (testing "valid app"
      (is (valid? apps/App
                  {:id #uuid "def67890-abc1-2345-6789-0abcdef12345"
                   :name "Samtools"
                   :description "Tools for manipulating SAM/BAM files"
                   :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                            :name "samtools"
                            :description "SAM/BAM file processing"
                            :version "1.15.1"
                            :type "executable"}
                           {:id #uuid "555e5555-e55e-55e5-e555-555555555555"
                            :name "bcftools"
                            :description "Variant calling utilities"
                            :version "1.15.1"
                            :type "executable"
                            :deprecated false}]}))
      (is (valid? apps/App
                  {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                   :name "Complete App"
                   :description "An app with all fields"
                   :integration_date #inst "2024-01-01T00:00:00.000-00:00"
                   :edited_date #inst "2024-12-31T23:59:59.000-00:00"
                   :system_id "condor"
                   :version "2.0.0"
                   :version_id #uuid "666e6666-f66f-66f6-f666-666666666666"
                   :versions [{:version "1.0.0"
                               :version_id #uuid "777e7777-a77a-77a7-a777-777777777777"}
                              {:version "2.0.0"
                               :version_id #uuid "666e6666-f66f-66f6-f666-666666666666"}]
                   :tools [{:id #uuid "888e8888-a88a-88a8-a888-888888888888"
                            :name "tool1"
                            :version "1.0"
                            :type "executable"
                            :deprecated false}]
                   :references ["https://doi.org/10.1093/nar/gkv416" "PMID: 25916842"]
                   :groups [{:id #uuid "999e9999-b99b-99b9-b999-999999999999"
                             :label "Input Parameters"
                             :name "input_params"
                             :description "Input file parameters"
                             :isVisible true
                             :parameters [{:id #uuid "aaaeaaaa-caaa-aaa1-aaaa-aaaaaaaaaaaa"
                                           :type "FileInput"
                                           :label "Input File"
                                           :required true}]}]})))

    (testing "invalid app"
      (is (not (valid? apps/App {})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :description "BLAST tool"})))
      (is (not (valid? apps/App
                       {:name "BLAST"
                        :description "BLAST tool"})))
      (is (not (valid? apps/App
                       {:id "not-a-uuid"
                        :name "BLAST"
                        :description "BLAST tool"})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name 123
                        :description "BLAST tool"})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description true})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :integration_date "not-a-date"})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :versions "not-a-vector"})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :versions [{}]})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :tools "not-a-vector"})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :tools [{:id #uuid "444e4444-d44d-44d4-d444-444444444444"
                                 :name "samtools"
                                 :version "1.15.1"
                                 :type "executable"
                                 :deprecated "not-a-boolean"}]})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :references "not-a-vector"})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :references [123]})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :groups "not-a-vector"})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :groups [{}]})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :groups [{:id #uuid "999e9999-b99b-99b9-b999-999999999999"}]})))
      (is (not (valid? apps/App
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :description "BLAST tool"
                        :extra-field "not-allowed"}))))))

(deftest test-AppFileParameterDetails
  (testing "AppFileParameterDetails validation"
    (testing "valid file parameter details"
      (is (valid? apps/AppFileParameterDetails
                  {:id "param_001"
                   :name "output_alignment"
                   :description "The output alignment file from the analysis"
                   :label "Output Alignment"
                   :format "bam"
                   :required true}))
      (is (valid? apps/AppFileParameterDetails
                  {:id "param_002"
                   :name "input_fastq"
                   :description "Input FASTQ file for quality control"
                   :label "Input FASTQ"
                   :format "fastq"
                   :required false}))
      (is (valid? apps/AppFileParameterDetails
                  {:id "param_003"
                   :name "output_vcf"
                   :description "Variant calling output"
                   :label "VCF Output"
                   :format "vcf"
                   :required true})))

    (testing "invalid file parameter details - missing required fields"
      (is (not (valid? apps/AppFileParameterDetails {})))
      (is (not (valid? apps/AppFileParameterDetails
                       {:name "output_alignment"
                        :description "The output alignment file from the analysis"
                        :label "Output Alignment"
                        :format "bam"
                        :required true})))
      (is (not (valid? apps/AppFileParameterDetails
                       {:id "param_001"
                        :description "The output alignment file from the analysis"
                        :label "Output Alignment"
                        :format "bam"
                        :required true})))
      (is (not (valid? apps/AppFileParameterDetails
                       {:id "param_001"
                        :name "output_alignment"
                        :label "Output Alignment"
                        :format "bam"
                        :required true})))
      (is (not (valid? apps/AppFileParameterDetails
                       {:id "param_001"
                        :name "output_alignment"
                        :description "The output alignment file from the analysis"
                        :format "bam"
                        :required true})))
      (is (not (valid? apps/AppFileParameterDetails
                       {:id "param_001"
                        :name "output_alignment"
                        :description "The output alignment file from the analysis"
                        :label "Output Alignment"
                        :required true})))
      (is (not (valid? apps/AppFileParameterDetails
                       {:id "param_001"
                        :name "output_alignment"
                        :description "The output alignment file from the analysis"
                        :label "Output Alignment"
                        :format "bam"}))))

    (testing "invalid file parameter details - incorrect field types"
      (is (not (valid? apps/AppFileParameterDetails
                       {:id 123
                        :name "output_alignment"
                        :description "The output alignment file from the analysis"
                        :label "Output Alignment"
                        :format "bam"
                        :required true})))
      (is (not (valid? apps/AppFileParameterDetails
                       {:id "param_001"
                        :name 123
                        :description "The output alignment file from the analysis"
                        :label "Output Alignment"
                        :format "bam"
                        :required true})))
      (is (not (valid? apps/AppFileParameterDetails
                       {:id "param_001"
                        :name "output_alignment"
                        :description true
                        :label "Output Alignment"
                        :format "bam"
                        :required true})))
      (is (not (valid? apps/AppFileParameterDetails
                       {:id "param_001"
                        :name "output_alignment"
                        :description "The output alignment file from the analysis"
                        :label 456
                        :format "bam"
                        :required true})))
      (is (not (valid? apps/AppFileParameterDetails
                       {:id "param_001"
                        :name "output_alignment"
                        :description "The output alignment file from the analysis"
                        :label "Output Alignment"
                        :format 789
                        :required true})))
      (is (not (valid? apps/AppFileParameterDetails
                       {:id "param_001"
                        :name "output_alignment"
                        :description "The output alignment file from the analysis"
                        :label "Output Alignment"
                        :format "bam"
                        :required "not-a-boolean"}))))

    (testing "invalid file parameter details - extra fields not allowed"
      (is (not (valid? apps/AppFileParameterDetails
                       {:id "param_001"
                        :name "output_alignment"
                        :description "The output alignment file from the analysis"
                        :label "Output Alignment"
                        :format "bam"
                        :required true
                        :extra-field "not-allowed"}))))))

(deftest test-AppTask
  (testing "AppTask validation"
    (testing "valid app task with all required fields"
      (is (valid? apps/AppTask
                  {:system_id "de"
                   :id "task_001"
                   :name "BLAST Analysis"
                   :description "Performs BLAST sequence alignment analysis"
                   :inputs []
                   :outputs []}))
      (is (valid? apps/AppTask
                  {:system_id "condor"
                   :id "task_002"
                   :name "FastQC"
                   :description "Quality control for high throughput sequence data"
                   :inputs [{:id "param_fastqc_in"
                             :name "input_fastq"
                             :description "Input FASTQ file"
                             :label "Input File"
                             :format "fastq"
                             :required true}]
                   :outputs [{:id "param_fastqc_out"
                              :name "output_html"
                              :description "QC report in HTML format"
                              :label "QC Report"
                              :format "html"
                              :required true}]})))

    (testing "valid app task with optional tool field"
      (is (valid? apps/AppTask
                  {:system_id "de"
                   :id "task_003"
                   :name "Samtools Sort"
                   :description "Sort BAM files by coordinate"
                   :tool {:id #uuid "555e5555-e55e-55e5-e555-555555555555"
                          :name "samtools"
                          :version "1.15.1"
                          :type "executable"
                          :is_public true
                          :permission "own"
                          :implementation {:implementor "Jane Smith"
                                           :implementor_email "jane.smith@example.org"
                                           :test {:input_files ["/test/input.bam"]
                                                  :output_files ["/test/output.sorted.bam"]}}
                          :container {:id #uuid "666e6666-f66f-66f6-f666-666666666666"
                                      :name "samtools-container"
                                      :image {:id #uuid "777e7777-a77a-77a7-a777-777777777777"
                                              :name "biocontainers/samtools"
                                              :tag "1.15.1"}}}
                   :inputs [{:id "param_samtools_in"
                             :name "input_bam"
                             :description "Unsorted BAM file"
                             :label "Input BAM"
                             :format "bam"
                             :required true}]
                   :outputs [{:id "param_samtools_out"
                              :name "sorted_bam"
                              :description "Sorted BAM file"
                              :label "Sorted BAM"
                              :format "bam"
                              :required true}]})))

    (testing "valid app task with multiple inputs and outputs"
      (is (valid? apps/AppTask
                  {:system_id "de"
                   :id "task_004"
                   :name "BWA Alignment"
                   :description "Align sequences using BWA"
                   :inputs [{:id "param_bwa_ref"
                             :name "reference_genome"
                             :description "Reference genome FASTA file"
                             :label "Reference Genome"
                             :format "fasta"
                             :required true}
                            {:id "param_bwa_r1"
                             :name "reads_r1"
                             :description "Forward reads FASTQ file"
                             :label "Forward Reads"
                             :format "fastq"
                             :required true}
                            {:id "param_bwa_r2"
                             :name "reads_r2"
                             :description "Reverse reads FASTQ file"
                             :label "Reverse Reads"
                             :format "fastq"
                             :required false}]
                   :outputs [{:id "param_bwa_sam"
                              :name "aligned_sam"
                              :description "Aligned sequences in SAM format"
                              :label "Alignment SAM"
                              :format "sam"
                              :required true}
                             {:id "param_bwa_log"
                              :name "alignment_log"
                              :description "Alignment statistics log"
                              :label "Log File"
                              :format "txt"
                              :required false}]})))

    (testing "invalid app task - missing required fields"
      (is (not (valid? apps/AppTask {})))
      (is (not (valid? apps/AppTask
                       {:id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs []
                        :outputs []})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs []
                        :outputs []})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs []
                        :outputs []})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :inputs []
                        :outputs []})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :outputs []})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs []}))))

    (testing "invalid app task - incorrect field types"
      (is (not (valid? apps/AppTask
                       {:system_id 123
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs []
                        :outputs []})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id 456
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs []
                        :outputs []})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name 456
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs []
                        :outputs []})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description true
                        :inputs []
                        :outputs []})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs "not-a-vector"
                        :outputs []})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs []
                        :outputs "not-a-vector"})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :tool "not-a-map"
                        :inputs []
                        :outputs []}))))

    (testing "invalid app task - invalid nested AppFileParameterDetails in inputs"
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs [{:id 123
                                  :name "input_fasta"
                                  :description "Input sequence"
                                  :label "Input"
                                  :format "fasta"
                                  :required true}]
                        :outputs []})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs [{:name "input_fasta"
                                  :description "Input sequence"
                                  :label "Input"
                                  :format "fasta"
                                  :required true}]
                        :outputs []})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs [{:id "param_input"
                                  :description "Input sequence"
                                  :label "Input"
                                  :format "fasta"
                                  :required true}]
                        :outputs []}))))

    (testing "invalid app task - invalid nested AppFileParameterDetails in outputs"
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs []
                        :outputs [{:id 456
                                   :name "output_txt"
                                   :description "BLAST results"
                                   :label "Results"
                                   :format "txt"
                                   :required true}]})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs []
                        :outputs [{:id "param_output"
                                   :name "output_txt"
                                   :description "BLAST results"
                                   :label "Results"
                                   :format "txt"}]})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs []
                        :outputs [{:id "param_output"
                                   :description "BLAST results"
                                   :label "Results"
                                   :format "txt"
                                   :required true}]}))))

    (testing "invalid app task - invalid ToolDetails in tool field"
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :tool {:id "not-a-uuid"
                               :name "blast"
                               :version "2.12.0"
                               :type "executable"
                               :is_public true
                               :permission "own"}
                        :inputs []
                        :outputs []})))
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :tool {:id #uuid "555e5555-e55e-55e5-e555-555555555555"
                               :name "blast"
                               :version "2.12.0"
                               :type "executable"}
                        :inputs []
                        :outputs []}))))

    (testing "invalid app task - extra fields not allowed"
      (is (not (valid? apps/AppTask
                       {:system_id "de"
                        :id "task_001"
                        :name "BLAST Analysis"
                        :description "Performs BLAST sequence alignment analysis"
                        :inputs []
                        :outputs []
                        :extra-field "not-allowed"}))))))

(deftest test-AppTaskListing
  (testing "AppTaskListing validation"
    (testing "valid app task listing with all required fields from AppBase"
      (is (valid? apps/AppTaskListing
                  {:id "app_001"
                   :name "Multi-step Workflow"
                   :description "A workflow consisting of multiple analysis tasks"
                   :tasks []}))
      (is (valid? apps/AppTaskListing
                  {:id "app_workflow_123"
                   :name "BLAST and FastQC Pipeline"
                   :description "Combined sequence alignment and quality control workflow"
                   :tasks []})))

    (testing "valid app task listing with empty tasks vector"
      (is (valid? apps/AppTaskListing
                  {:id "app_empty_tasks"
                   :name "Empty Workflow"
                   :description "A workflow with no tasks defined yet"
                   :tasks []})))

    (testing "valid app task listing with single task"
      (is (valid? apps/AppTaskListing
                  {:id "app_single_task"
                   :name "Single Task Workflow"
                   :description "Workflow with one task"
                   :tasks [{:system_id "de"
                            :id "task_001"
                            :name "BLAST Analysis"
                            :description "Performs BLAST sequence alignment analysis"
                            :inputs []
                            :outputs []}]})))

    (testing "valid app task listing with multiple tasks"
      (is (valid? apps/AppTaskListing
                  {:id "app_multi_task"
                   :name "Multi-Task Pipeline"
                   :description "A comprehensive pipeline with multiple analysis steps"
                   :tasks [{:system_id "de"
                            :id "task_001"
                            :name "FastQC"
                            :description "Quality control for sequence data"
                            :inputs []
                            :outputs []}
                           {:system_id "condor"
                            :id "task_002"
                            :name "BLAST"
                            :description "Sequence alignment"
                            :inputs []
                            :outputs []}
                           {:system_id "de"
                            :id "task_003"
                            :name "Report Generation"
                            :description "Generate analysis report"
                            :inputs []
                            :outputs []}]})))

    (testing "valid app task listing with realistic AppTask objects"
      (is (valid? apps/AppTaskListing
                  {:id "app_realistic"
                   :name "RNA-Seq Analysis"
                   :description "Complete RNA-Seq analysis workflow"
                   :tasks [{:system_id "de"
                            :id "task_qc"
                            :name "FastQC"
                            :description "Quality control for FASTQ files"
                            :inputs [{:id "param_fastq_in"
                                      :name "input_fastq"
                                      :description "Input FASTQ file"
                                      :label "Input File"
                                      :format "fastq"
                                      :required true}]
                            :outputs [{:id "param_qc_out"
                                       :name "qc_report"
                                       :description "QC report"
                                       :label "QC Report"
                                       :format "html"
                                       :required true}]}
                           {:system_id "de"
                            :id "task_align"
                            :name "STAR Alignment"
                            :description "Align reads to reference genome"
                            :inputs [{:id "param_fastq"
                                      :name "reads"
                                      :description "Sequencing reads"
                                      :label "Reads"
                                      :format "fastq"
                                      :required true}
                                     {:id "param_ref"
                                      :name "reference"
                                      :description "Reference genome"
                                      :label "Reference"
                                      :format "fasta"
                                      :required true}]
                            :outputs [{:id "param_bam"
                                       :name "aligned_bam"
                                       :description "Aligned reads"
                                       :label "BAM File"
                                       :format "bam"
                                       :required true}]}]})))

    (testing "valid app task listing with optional AppBase fields"
      (is (valid? apps/AppTaskListing
                  {:id "app_with_optional"
                   :name "Complete Workflow"
                   :description "Workflow with all optional fields"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                   :system_id "de"
                   :version "1.0.0"
                   :version_id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :tasks []})))

    (testing "id field accepts strings (not UUIDs)"
      (is (valid? apps/AppTaskListing
                  {:id "string_id_123"
                   :name "String ID App"
                   :description "App with string ID"
                   :tasks []}))
      (is (valid? apps/AppTaskListing
                  {:id "app-with-dashes-456"
                   :name "Dashed ID App"
                   :description "App with dashed string ID"
                   :tasks []}))
      (is (valid? apps/AppTaskListing
                  {:id "app_underscore_789"
                   :name "Underscore ID App"
                   :description "App with underscored string ID"
                   :tasks []})))

    (testing "invalid app task listing - missing required fields from AppBase"
      (is (not (valid? apps/AppTaskListing {})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_001"
                        :name "Missing Description"})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_002"
                        :description "Missing name"})))
      (is (not (valid? apps/AppTaskListing
                       {:name "No ID"
                        :description "Missing id"})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_003"
                        :name "No Tasks"
                        :description "Missing tasks field"}))))

    (testing "invalid app task listing - id field rejects non-string types"
      (is (not (valid? apps/AppTaskListing
                       {:id 123
                        :name "Numeric ID"
                        :description "ID is a number"
                        :tasks []})))
      (is (not (valid? apps/AppTaskListing
                       {:id true
                        :name "Boolean ID"
                        :description "ID is a boolean"
                        :tasks []})))
      (is (not (valid? apps/AppTaskListing
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "UUID ID"
                        :description "ID is a UUID instead of string"
                        :tasks []})))
      (is (not (valid? apps/AppTaskListing
                       {:id nil
                        :name "Nil ID"
                        :description "ID is nil"
                        :tasks []}))))

    (testing "invalid app task listing - incorrect field types"
      (is (not (valid? apps/AppTaskListing
                       {:id "app_004"
                        :name 123
                        :description "Name is not a string"
                        :tasks []})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_005"
                        :name "Valid Name"
                        :description true
                        :tasks []})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_006"
                        :name "Valid Name"
                        :description "Valid description"
                        :tasks "not-a-vector"})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_007"
                        :name "Valid Name"
                        :description "Valid description"
                        :integration_date "not-a-date"
                        :tasks []})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_008"
                        :name "Valid Name"
                        :description "Valid description"
                        :edited_date 12345
                        :tasks []})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_009"
                        :name "Valid Name"
                        :description "Valid description"
                        :system_id 123
                        :tasks []})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_010"
                        :name "Valid Name"
                        :description "Valid description"
                        :version 456
                        :tasks []})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_011"
                        :name "Valid Name"
                        :description "Valid description"
                        :version_id "not-a-uuid"
                        :tasks []}))))

    (testing "invalid app task listing - invalid nested AppTask data"
      (is (not (valid? apps/AppTaskListing
                       {:id "app_invalid_task"
                        :name "Invalid Task"
                        :description "Contains invalid task"
                        :tasks [{:id "task_001"
                                 :name "Missing system_id"}]})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_invalid_task_2"
                        :name "Invalid Task 2"
                        :description "Task missing required field"
                        :tasks [{:system_id "de"
                                 :id "task_002"
                                 :name "Missing description"
                                 :inputs []
                                 :outputs []}]})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_invalid_task_3"
                        :name "Invalid Task 3"
                        :description "Task with wrong type"
                        :tasks [{:system_id 123
                                 :id "task_003"
                                 :name "Task Name"
                                 :description "Task description"
                                 :inputs []
                                 :outputs []}]})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_invalid_nested"
                        :name "Invalid Nested Data"
                        :description "Task with invalid nested parameter"
                        :tasks [{:system_id "de"
                                 :id "task_004"
                                 :name "Task Name"
                                 :description "Task description"
                                 :inputs [{:id 123
                                           :name "invalid_param"
                                           :description "Invalid parameter"
                                           :label "Label"
                                           :format "txt"
                                           :required true}]
                                 :outputs []}]})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_mixed_tasks"
                        :name "Mixed Valid and Invalid"
                        :description "One valid and one invalid task"
                        :tasks [{:system_id "de"
                                 :id "task_valid"
                                 :name "Valid Task"
                                 :description "This task is valid"
                                 :inputs []
                                 :outputs []}
                                {:system_id "de"
                                 :id "task_invalid"
                                 :name "Invalid Task"}]}))))

    (testing "invalid app task listing - extra fields not allowed"
      (is (not (valid? apps/AppTaskListing
                       {:id "app_extra"
                        :name "Extra Field"
                        :description "Has extra field"
                        :tasks []
                        :extra-field "not-allowed"})))
      (is (not (valid? apps/AppTaskListing
                       {:id "app_extra_2"
                        :name "Another Extra"
                        :description "Another extra field"
                        :tasks []
                        :unexpected "also-not-allowed"}))))))

(deftest test-AppParameterJobView
  (testing "AppParameterJobView validation"
    (testing "valid parameter with composite string id"
      (is (valid? apps/AppParameterJobView
                  {:id "step_123_param_456"
                   :type "FileInput"}))
      (is (valid? apps/AppParameterJobView
                  {:id "step_abc_param_def"
                   :type "TextInput"
                   :name "--input"
                   :label "Input File"
                   :description "Specify the input file for processing"
                   :required true
                   :isVisible true}))
      (is (valid? apps/AppParameterJobView
                  {:id "step_001_param_002"
                   :type "IntegerInput"
                   :defaultValue 10
                   :value 20
                   :order 1
                   :omit_if_blank false})))

    (testing "id field accepts various string formats"
      (is (valid? apps/AppParameterJobView
                  {:id "step-123-param-456"
                   :type "FileInput"}))
      (is (valid? apps/AppParameterJobView
                  {:id "step123param456"
                   :type "TextInput"}))
      (is (valid? apps/AppParameterJobView
                  {:id "STEP_ABC_PARAM_DEF"
                   :type "TextInput"}))
      (is (valid? apps/AppParameterJobView
                  {:id "step_1_param_2_suffix_3"
                   :type "TextInput"}))
      (is (valid? apps/AppParameterJobView
                  {:id "any-valid-string-format"
                   :type "FileInput"})))

    (testing "valid parameter with file_parameters"
      (is (valid? apps/AppParameterJobView
                  {:id "step_111_param_222"
                   :type "FileInput"
                   :file_parameters {:format "fasta"
                                     :file_info_type "SequenceAlignment"
                                     :retain true}}))
      (is (valid? apps/AppParameterJobView
                  {:id "step_file_param_out"
                   :type "FileOutput"
                   :file_parameters {:format "fastq"
                                     :file_info_type "SequenceFile"
                                     :is_implicit false
                                     :repeat_option_flag false
                                     :data_source "file"
                                     :retain true}})))

    (testing "valid parameter with arguments"
      (is (valid? apps/AppParameterJobView
                  {:id "step_args_param_select"
                   :type "TextSelection"
                   :arguments [{:id #uuid "555e5555-e55e-55e5-e555-555555555555"
                                :name "option1"
                                :value "val1"}
                               {:id #uuid "666e6666-f66f-66f6-f666-666666666666"
                                :name "option2"
                                :value "val2"}]}))
      (is (valid? apps/AppParameterJobView
                  {:id "step_tree_param_multi"
                   :type "TreeSelection"
                   :arguments [{:id #uuid "777e7777-a77a-77a7-a777-777777777777"
                                :name "root_option"
                                :isSingleSelect false
                                :groups [{:id #uuid "888e8888-a88a-88a8-a888-888888888888"
                                          :name "sub_group"}]}]})))

    (testing "valid parameter with validators"
      (is (valid? apps/AppParameterJobView
                  {:id "step_val_param_int"
                   :type "IntegerInput"
                   :validators [{:type "IntAbove"
                                 :params [0]}
                                {:type "IntBelow"
                                 :params [100]}]}))
      (is (valid? apps/AppParameterJobView
                  {:id "step_val_param_str"
                   :type "TextInput"
                   :validators [{:type "StringMatches"
                                 :params ["^[a-zA-Z0-9]+$"]}
                                {:type "Required"
                                 :params []}]})))

    (testing "valid parameter with all fields from AppParameter"
      (is (valid? apps/AppParameterJobView
                  {:id "step_complete_param_full"
                   :type "FileInput"
                   :name "--input-file"
                   :label "Input File"
                   :description "The primary input file for analysis"
                   :defaultValue "/path/to/default.txt"
                   :value "/path/to/input.txt"
                   :order 1
                   :required true
                   :isVisible true
                   :omit_if_blank false
                   :file_parameters {:format "txt"
                                     :file_info_type "TextFile"
                                     :is_implicit false
                                     :repeat_option_flag false
                                     :data_source "file"
                                     :retain true}
                   :validators [{:type "Required"
                                 :params []}]})))

    (testing "invalid parameter - missing required fields"
      (is (not (valid? apps/AppParameterJobView {})))
      (is (not (valid? apps/AppParameterJobView {:id "step_123_param_456"})))
      (is (not (valid? apps/AppParameterJobView {:type "FileInput"}))))

    (testing "invalid parameter - id field rejects non-string types"
      (is (not (valid? apps/AppParameterJobView
                       {:id 123
                        :type "FileInput"})))
      (is (not (valid? apps/AppParameterJobView
                       {:id true
                        :type "FileInput"})))
      (is (not (valid? apps/AppParameterJobView
                       {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                        :type "FileInput"})))
      (is (not (valid? apps/AppParameterJobView
                       {:id nil
                        :type "FileInput"})))
      (is (not (valid? apps/AppParameterJobView
                       {:id [:step "123" :param "456"]
                        :type "FileInput"}))))

    (testing "invalid parameter - incorrect field types"
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_invalid_param_type"
                        :type 123})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_invalid_param_name"
                        :type "FileInput"
                        :name 123})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_invalid_param_label"
                        :type "FileInput"
                        :label true})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_invalid_param_order"
                        :type "FileInput"
                        :order "not-an-int"})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_invalid_param_required"
                        :type "FileInput"
                        :required "not-a-boolean"})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_invalid_param_visible"
                        :type "FileInput"
                        :isVisible "not-a-boolean"})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_invalid_param_omit"
                        :type "FileInput"
                        :omit_if_blank "not-a-boolean"}))))

    (testing "invalid parameter - invalid file_parameters"
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_bad_param_file"
                        :type "FileInput"
                        :file_parameters "not-a-map"})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_bad_param_file2"
                        :type "FileInput"
                        :file_parameters {:format 123}})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_bad_param_file3"
                        :type "FileInput"
                        :file_parameters {:is_implicit "not-a-boolean"}})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_bad_param_file4"
                        :type "FileInput"
                        :file_parameters {:extra-field "not-allowed"}}))))

    (testing "invalid parameter - invalid arguments"
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_bad_param_args"
                        :type "TextSelection"
                        :arguments "not-a-vector"})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_bad_param_args2"
                        :type "TextSelection"
                        :arguments [{:name "invalid-missing-id"}]})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_bad_param_args3"
                        :type "TextSelection"
                        :arguments [{:id "not-a-uuid"
                                     :name "option1"}]}))))

    (testing "invalid parameter - invalid validators"
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_bad_param_val"
                        :type "IntegerInput"
                        :validators "not-a-vector"})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_bad_param_val2"
                        :type "IntegerInput"
                        :validators [{}]})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_bad_param_val3"
                        :type "IntegerInput"
                        :validators [{:type "IntAbove"}]})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_bad_param_val4"
                        :type "IntegerInput"
                        :validators [{:type 123
                                      :params [0]}]})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_bad_param_val5"
                        :type "IntegerInput"
                        :validators [{:type "IntAbove"
                                      :params [0]
                                      :extra-field "not-allowed"}]}))))

    (testing "invalid parameter - extra fields not allowed"
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_extra_param_field"
                        :type "FileInput"
                        :extra-field "not-allowed"})))
      (is (not (valid? apps/AppParameterJobView
                       {:id "step_extra_param_field2"
                        :type "TextInput"
                        :name "--input"
                        :unexpected "also-not-allowed"}))))))

(deftest test-AppStepResourceRequirements
  (testing "AppStepResourceRequirements validation"
    (testing "valid with only required step_number field"
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 1}))
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 2}))
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 10})))

    (testing "valid with step_number and fields from Settings"
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 1
                   :memory_limit 1073741824}))
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 2
                   :min_memory_limit 536870912}))
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 1
                   :min_cpu_cores 1.0}))
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 3
                   :max_cpu_cores 4.0}))
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 1
                   :min_disk_space 10737418240})))

    (testing "valid with step_number and default resource fields"
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 1
                   :default_max_cpu_cores 2.0}))
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 2
                   :default_cpu_cores 1.0}))
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 1
                   :default_memory 536870912}))
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 3
                   :default_disk_space 5368709120})))

    (testing "valid with realistic complete example"
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 1
                   :memory_limit 2147483648
                   :min_memory_limit 536870912
                   :min_cpu_cores 1.0
                   :max_cpu_cores 8.0
                   :min_disk_space 10737418240
                   :default_max_cpu_cores 2.0
                   :default_cpu_cores 1.0
                   :default_memory 536870912
                   :default_disk_space 5368709120})))

    (testing "valid with combination of Settings and default fields"
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 2
                   :min_cpu_cores 2.0
                   :max_cpu_cores 16.0
                   :default_cpu_cores 4.0
                   :default_max_cpu_cores 8.0}))
      (is (valid? apps/AppStepResourceRequirements
                  {:step_number 1
                   :memory_limit 4294967296
                   :min_memory_limit 1073741824
                   :default_memory 2147483648
                   :min_disk_space 21474836480
                   :default_disk_space 10737418240})))

    (testing "invalid - missing required step_number field"
      (is (not (valid? apps/AppStepResourceRequirements {})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:memory_limit 1073741824})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:min_cpu_cores 1.0
                        :max_cpu_cores 4.0})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:default_memory 536870912
                        :default_disk_space 5368709120}))))

    (testing "invalid - incorrect type for step_number"
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number "1"})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number 1.5})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number true})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number nil}))))

    (testing "invalid - incorrect types for memory and disk fields (int fields)"
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number 1
                        :memory_limit "1073741824"})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number 1
                        :min_memory_limit 1.5})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number 1
                        :default_memory "536870912"})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number 1
                        :min_disk_space "10737418240"})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number 1
                        :default_disk_space 5.5}))))

    (testing "invalid - incorrect types for cpu core fields (double fields)"
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number 1
                        :min_cpu_cores "1.0"})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number 1
                        :max_cpu_cores 4})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number 1
                        :default_cpu_cores "1.0"})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number 1
                        :default_max_cpu_cores true}))))

    (testing "invalid - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number 1
                        :extra-field "not-allowed"})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number 1
                        :memory_limit 1073741824
                        :unknown_field 123})))
      (is (not (valid? apps/AppStepResourceRequirements
                       {:step_number 2
                        :min_cpu_cores 1.0
                        :max_cpu_cores 4.0
                        :default_cpu_cores 2.0
                        :unexpected "value"}))))))

(deftest test-AppGroupJobView
  (testing "AppGroupJobView validation"
    (testing "valid group with all required fields"
      (is (valid? apps/AppGroupJobView
                  {:id "step_123_group_456"
                   :label "Input Parameters"
                   :step_number 1}))
      (is (valid? apps/AppGroupJobView
                  {:id "step_001_group_002"
                   :label "Output Parameters"
                   :step_number 1}))
      (is (valid? apps/AppGroupJobView
                  {:id "step_abc_group_def"
                   :label "Settings"
                   :step_number 2})))

    (testing "valid group with string id field (not UUID like AppGroup)"
      (is (valid? apps/AppGroupJobView
                  {:id "step_123_group_456"
                   :label "Input"
                   :step_number 1}))
      (is (valid? apps/AppGroupJobView
                  {:id "step-with-dashes-789"
                   :label "Settings"
                   :step_number 3}))
      (is (valid? apps/AppGroupJobView
                  {:id "STEP_UPPER_GROUP_999"
                   :label "Advanced"
                   :step_number 5})))

    (testing "valid group with optional fields from AppGroup"
      (is (valid? apps/AppGroupJobView
                  {:id "step_111_group_222"
                   :label "Output Parameters"
                   :name "output_parameters"
                   :description "Output file and result parameters"
                   :step_number 1}))
      (is (valid? apps/AppGroupJobView
                  {:id "step_333_group_444"
                   :label "Advanced"
                   :name "advanced"
                   :isVisible false
                   :step_number 2}))
      (is (valid? apps/AppGroupJobView
                  {:id "step_visible_group_yes"
                   :label "Basic Settings"
                   :isVisible true
                   :step_number 1})))

    (testing "valid group with optional parameters field"
      (is (valid? apps/AppGroupJobView
                  {:id "step_params_group_001"
                   :label "Input Settings"
                   :step_number 1
                   :parameters []}))
      (is (valid? apps/AppGroupJobView
                  {:id "step_params_group_002"
                   :label "Analysis Settings"
                   :step_number 2
                   :parameters [{:id "step_002_param_001"
                                 :type "TextInput"
                                 :label "Name"}]}))
      (is (valid? apps/AppGroupJobView
                  {:id "step_params_group_003"
                   :label "Complete Settings"
                   :step_number 1
                   :parameters [{:id "step_001_param_file"
                                 :type "FileInput"
                                 :label "Input File"}
                                {:id "step_001_param_int"
                                 :type "IntegerInput"
                                 :label "Count"
                                 :defaultValue 10}]})))

    (testing "valid group with parameters containing composite IDs"
      (is (valid? apps/AppGroupJobView
                  {:id "step_123_group_456"
                   :label "Processing Parameters"
                   :step_number 1
                   :parameters [{:id "step_123_param_789"
                                 :type "FileInput"
                                 :name "--input"
                                 :label "Input File"
                                 :description "Primary input for analysis"
                                 :required true}
                                {:id "step_123_param_abc"
                                 :type "IntegerInput"
                                 :name "--threads"
                                 :label "Thread Count"
                                 :description "Number of processing threads"
                                 :defaultValue 4
                                 :value 8
                                 :required false}]}))
      (is (valid? apps/AppGroupJobView
                  {:id "step_workflow_group_main"
                   :label "Main Parameters"
                   :step_number 2
                   :parameters [{:id "step_workflow_param_selection"
                                 :type "TextSelection"
                                 :label "Options"
                                 :arguments [{:id #uuid "111e1111-a11a-11a1-a111-111111111111"
                                              :name "option1"
                                              :value "val1"}
                                             {:id #uuid "222e2222-b22b-22b2-b222-222222222222"
                                              :name "option2"
                                              :value "val2"}]}]})))

    (testing "valid group with all optional fields and complex parameters"
      (is (valid? apps/AppGroupJobView
                  {:id "step_full_group_complete"
                   :label "Complete Parameter Group"
                   :name "complete_params"
                   :description "A fully specified parameter group for testing"
                   :isVisible true
                   :step_number 3
                   :parameters [{:id "step_full_param_file"
                                 :type "FileInput"
                                 :name "--input-file"
                                 :label "Input File"
                                 :description "Primary input file"
                                 :required true
                                 :file_parameters {:format "fasta"
                                                   :file_info_type "SequenceFile"
                                                   :retain true}}
                                {:id "step_full_param_validated"
                                 :type "IntegerInput"
                                 :name "--max-size"
                                 :label "Maximum Size"
                                 :description "Maximum processing size"
                                 :defaultValue 100
                                 :required true
                                 :validators [{:type "IntAbove"
                                               :params [0]}
                                              {:type "IntBelow"
                                               :params [1000]}]}]})))

    (testing "valid group with step_number field required"
      (is (valid? apps/AppGroupJobView
                  {:id "step_001_group_001"
                   :label "Step 1 Parameters"
                   :step_number 1}))
      (is (valid? apps/AppGroupJobView
                  {:id "step_999_group_999"
                   :label "Step 999 Parameters"
                   :step_number 999}))
      (is (valid? apps/AppGroupJobView
                  {:id "step_042_group_042"
                   :label "Step 42 Parameters"
                   :step_number 42})))

    (testing "invalid group - missing required fields"
      (is (not (valid? apps/AppGroupJobView {})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_123_group_456"})))
      (is (not (valid? apps/AppGroupJobView
                       {:label "Input Parameters"})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_123_group_456"
                        :step_number 1})))
      (is (not (valid? apps/AppGroupJobView
                       {:label "Input Parameters"
                        :step_number 1}))))

    (testing "invalid group - id field rejects non-string types"
      (is (not (valid? apps/AppGroupJobView
                       {:id 123
                        :label "Input Parameters"
                        :step_number 1})))
      (is (not (valid? apps/AppGroupJobView
                       {:id true
                        :label "Input Parameters"
                        :step_number 1})))
      (is (not (valid? apps/AppGroupJobView
                       {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                        :label "Input Parameters"
                        :step_number 1})))
      (is (not (valid? apps/AppGroupJobView
                       {:id nil
                        :label "Input Parameters"
                        :step_number 1})))
      (is (not (valid? apps/AppGroupJobView
                       {:id [:step "123"]
                        :label "Input Parameters"
                        :step_number 1}))))

    (testing "invalid group - step_number rejects non-integer types"
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_123_group_456"
                        :label "Input Parameters"
                        :step_number "1"})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_123_group_456"
                        :label "Input Parameters"
                        :step_number 1.5})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_123_group_456"
                        :label "Input Parameters"
                        :step_number true})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_123_group_456"
                        :label "Input Parameters"
                        :step_number nil})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_123_group_456"
                        :label "Input Parameters"
                        :step_number [1]}))))

    (testing "invalid group - incorrect types for optional fields"
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_group_name"
                        :label "Settings"
                        :name 123
                        :step_number 1})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_group_desc"
                        :label "Settings"
                        :description true
                        :step_number 1})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_group_label"
                        :label 456
                        :step_number 1})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_group_visible"
                        :label "Settings"
                        :isVisible "not-a-boolean"
                        :step_number 1}))))

    (testing "invalid group - parameters field with wrong type"
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_group_params1"
                        :label "Settings"
                        :step_number 1
                        :parameters "not-a-vector"})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_group_params2"
                        :label "Settings"
                        :step_number 1
                        :parameters {:not "a vector"}})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_group_params3"
                        :label "Settings"
                        :step_number 1
                        :parameters 123}))))

    (testing "invalid group - invalid nested AppParameterJobView data"
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_invalid_nested_param1"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id "step_001_param_001"}]})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_invalid_nested_param2"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:type "FileInput"}]})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_invalid_nested_param3"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id 123
                                      :type "FileInput"}]})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_invalid_nested_param4"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                                      :type "FileInput"}]})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_invalid_nested_param5"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id "step_001_param_001"
                                      :type 123}]})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_mixed_params"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id "step_001_param_valid"
                                      :type "TextInput"
                                      :label "Valid"}
                                     {:id "step_001_param_invalid"
                                      :label "Missing type"}]}))))

    (testing "invalid group - invalid nested parameter with bad file_parameters"
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_file_params"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id "step_001_param_file"
                                      :type "FileInput"
                                      :file_parameters "not-a-map"}]})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_file_params2"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id "step_001_param_file"
                                      :type "FileInput"
                                      :file_parameters {:format 123}}]})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_file_params3"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id "step_001_param_file"
                                      :type "FileInput"
                                      :file_parameters {:extra-field "not-allowed"}}]}))))

    (testing "invalid group - invalid nested parameter with bad arguments"
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_arguments"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id "step_001_param_select"
                                      :type "TextSelection"
                                      :arguments "not-a-vector"}]})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_arguments2"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id "step_001_param_select"
                                      :type "TextSelection"
                                      :arguments [{:name "option1"}]}]})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_arguments3"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id "step_001_param_select"
                                      :type "TextSelection"
                                      :arguments [{:id "not-a-uuid"
                                                   :name "option1"}]}]}))))

    (testing "invalid group - invalid nested parameter with bad validators"
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_validators"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id "step_001_param_int"
                                      :type "IntegerInput"
                                      :validators "not-a-vector"}]})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_validators2"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id "step_001_param_int"
                                      :type "IntegerInput"
                                      :validators [{}]}]})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_validators3"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id "step_001_param_int"
                                      :type "IntegerInput"
                                      :validators [{:type "IntAbove"}]}]})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_bad_validators4"
                        :label "Settings"
                        :step_number 1
                        :parameters [{:id "step_001_param_int"
                                      :type "IntegerInput"
                                      :validators [{:type "IntAbove"
                                                    :params [0]
                                                    :extra-field "not-allowed"}]}]}))))

    (testing "invalid group - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_extra_field"
                        :label "Settings"
                        :step_number 1
                        :extra-field "not-allowed"})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_extra_field2"
                        :label "Settings"
                        :step_number 1
                        :name "settings"
                        :description "Description"
                        :unexpected "also-not-allowed"})))
      (is (not (valid? apps/AppGroupJobView
                       {:id "step_extra_field3"
                        :label "Settings"
                        :step_number 1
                        :parameters []
                        :unknown_key "value"}))))))

(deftest test-AppJobView
  (testing "AppJobView validation"
    (testing "valid app job view with minimal required fields"
      (is (valid? apps/AppJobView
                  {:id "app-minimal-123"
                   :name "Minimal App"
                   :description "Minimal app description"
                   :version "1.0.0"
                   :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :app_type "DE"
                   :label "Minimal Label"
                   :deleted false
                   :disabled false})))

    (testing "valid app job view with all required and optional fields from AppBase"
      (is (valid? apps/AppJobView
                  {:id "app-complete-abc456"
                   :name "Complete App"
                   :description "Complete app with all fields"
                   :version "2.1.0"
                   :version_id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                   :system_id "de"
                   :app_type "DE"
                   :label "Complete Label"
                   :deleted false
                   :disabled true})))

    (testing "valid app job view with AppVersionListing"
      (is (valid? apps/AppJobView
                  {:id "app-versions-xyz789"
                   :name "Versioned App"
                   :description "App with version listing"
                   :version "1.0.0"
                   :version_id #uuid "111e1111-a11a-11a1-a111-111111111111"
                   :app_type "External"
                   :label "Versioned Label"
                   :deleted false
                   :disabled false
                   :versions [{:version "1.0.0"
                               :version_id #uuid "111e1111-a11a-11a1-a111-111111111111"}
                              {:version "1.1.0"
                               :version_id #uuid "222e2222-b22b-22b2-b222-222222222222"}]})))

    (testing "valid app job view with AppLimitChecks"
      (is (valid? apps/AppJobView
                  {:id "app-limits-def123"
                   :name "Limited App"
                   :description "App with limit checks"
                   :version "1.0.0"
                   :version_id #uuid "333e3333-c33c-33c3-c333-333333333333"
                   :app_type "DE"
                   :label "Limited Label"
                   :deleted false
                   :disabled false
                   :limitChecks {:canRun true
                                 :results []}}))
      (is (valid? apps/AppJobView
                  {:id "app-limits-failed-ghi456"
                   :name "Failed Limit App"
                   :description "App with failed limit checks"
                   :version "1.0.0"
                   :version_id #uuid "444e4444-d44d-44d4-d444-444444444444"
                   :app_type "DE"
                   :label "Failed Limit Label"
                   :deleted false
                   :disabled false
                   :limitChecks {:canRun false
                                 :results [{:limitCheckID "concurrent-job-limit"
                                            :reasonCodes ["MAX_JOBS_EXCEEDED"]
                                            :additionalInfo {:current_jobs 10 :max_jobs 5}}]}})))

    (testing "valid app job view with debug flag"
      (is (valid? apps/AppJobView
                  {:id "app-debug-jkl789"
                   :name "Debug App"
                   :description "App with debug flag"
                   :version "1.0.0"
                   :version_id #uuid "555e5555-e55e-55e5-e555-555555555555"
                   :app_type "DE"
                   :label "Debug Label"
                   :deleted false
                   :disabled false
                   :debug true}))
      (is (valid? apps/AppJobView
                  {:id "app-no-debug-mno012"
                   :name "No Debug App"
                   :description "App without debug flag"
                   :version "1.0.0"
                   :version_id #uuid "666e6666-f66f-66f6-f666-666666666666"
                   :app_type "DE"
                   :label "No Debug Label"
                   :deleted false
                   :disabled false
                   :debug false})))

    (testing "valid app job view with requirements"
      (is (valid? apps/AppJobView
                  {:id "app-requirements-pqr345"
                   :name "Requirements App"
                   :description "App with resource requirements"
                   :version "1.0.0"
                   :version_id #uuid "777e7777-a77a-77a7-a777-777777777777"
                   :app_type "DE"
                   :label "Requirements Label"
                   :deleted false
                   :disabled false
                   :requirements [{:step_number 1
                                   :default_cpu_cores 1.0
                                   :default_max_cpu_cores 2.0
                                   :default_memory 536870912
                                   :default_disk_space 5368709120}
                                  {:step_number 2
                                   :default_cpu_cores 2.0
                                   :default_max_cpu_cores 4.0}]})))

    (testing "valid app job view with groups"
      (is (valid? apps/AppJobView
                  {:id "app-groups-stu678"
                   :name "Groups App"
                   :description "App with parameter groups"
                   :version "1.0.0"
                   :version_id #uuid "888e8888-b88b-88b8-b888-888888888888"
                   :app_type "DE"
                   :label "Groups Label"
                   :deleted false
                   :disabled false
                   :groups [{:id "step_001"
                             :label "Input Parameters"
                             :step_number 1}
                            {:id "step_002"
                             :label "Analysis Settings"
                             :step_number 2
                             :parameters [{:id "param_001"
                                           :type "TextInput"}
                                          {:id "param_002"
                                           :type "IntegerInput"}]}]})))

    (testing "valid app job view with all optional fields"
      (is (valid? apps/AppJobView
                  {:id "app-complete-vwx901"
                   :name "Fully Featured App"
                   :description "App with all possible fields"
                   :version "3.0.0"
                   :version_id #uuid "999e9999-c99c-99c9-c999-999999999999"
                   :integration_date #inst "2023-06-10T08:00:00.000-00:00"
                   :edited_date #inst "2024-11-01T16:20:00.000-00:00"
                   :system_id "condor"
                   :app_type "External"
                   :label "Fully Featured Label"
                   :deleted true
                   :disabled true
                   :debug true
                   :versions [{:version "2.0.0"
                               :version_id #uuid "aaa0aaaa-daaa-aaaa-daaa-aaaaaaaaaaaa"}
                              {:version "3.0.0"
                               :version_id #uuid "999e9999-c99c-99c9-c999-999999999999"}]
                   :limitChecks {:canRun true
                                 :results []}
                   :requirements [{:step_number 1
                                   :min_cpu_cores 1.0
                                   :max_cpu_cores 8.0
                                   :default_cpu_cores 2.0
                                   :default_max_cpu_cores 4.0
                                   :min_memory_limit 268435456
                                   :memory_limit 2147483648
                                   :default_memory 1073741824
                                   :min_disk_space 1073741824
                                   :default_disk_space 10737418240}]
                   :groups [{:id "analysis_step"
                             :label "Analysis Configuration"
                             :step_number 1
                             :name "config"
                             :description "Configure analysis parameters"
                             :parameters [{:id "input_file"
                                           :type "FileInput"
                                           :name "input"
                                           :label "Input File"
                                           :description "Select input data file"
                                           :required true
                                           :order 0}
                                          {:id "threshold"
                                           :type "DoubleInput"
                                           :name "threshold"
                                           :label "Threshold"
                                           :defaultValue 0.05
                                           :validators [{:type "DoubleAbove"
                                                         :params [0.0]}
                                                        {:type "DoubleBelow"
                                                         :params [1.0]}]}]}]})))

    (testing "invalid app job view - id field must be string, not UUID"
      (is (not (valid? apps/AppJobView
                       {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false}))))

    (testing "invalid app job view - id field rejects non-string types"
      (is (not (valid? apps/AppJobView
                       {:id 12345
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false})))
      (is (not (valid? apps/AppJobView
                       {:id true
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false}))))

    (testing "invalid app job view - missing required fields"
      (is (not (valid? apps/AppJobView {})))
      (is (not (valid? apps/AppJobView
                       {:id "app-missing-name"})))
      (is (not (valid? apps/AppJobView
                       {:id "app-missing-desc"
                        :name "App"})))
      (is (not (valid? apps/AppJobView
                       {:id "app-missing-version"
                        :name "App"
                        :description "Description"})))
      (is (not (valid? apps/AppJobView
                       {:id "app-missing-version-id"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"})))
      (is (not (valid? apps/AppJobView
                       {:id "app-missing-app-type"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"})))
      (is (not (valid? apps/AppJobView
                       {:id "app-missing-label"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"})))
      (is (not (valid? apps/AppJobView
                       {:id "app-missing-deleted"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"})))
      (is (not (valid? apps/AppJobView
                       {:id "app-missing-disabled"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false}))))

    (testing "invalid app job view - incorrect field types"
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-name-type"
                        :name 12345
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-version-type"
                        :name "App"
                        :description "Description"
                        :version 100
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-version-id-type"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id "not-a-uuid"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-deleted-type"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted "false"
                        :disabled false})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-disabled-type"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled "false"})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-debug-type"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :debug "true"}))))

    (testing "invalid app job view - invalid nested AppStepResourceRequirements"
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-requirements"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :requirements "not-a-vector"})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-requirements-missing-step"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :requirements [{:default_cpu_cores 1.0}]})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-requirements-wrong-type"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :requirements [{:step_number "not-an-int"
                                        :default_cpu_cores 1.0}]})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-requirements-extra-field"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :requirements [{:step_number 1
                                        :extra_field "not-allowed"}]}))))

    (testing "invalid app job view - invalid nested AppGroupJobView"
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-groups"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :groups "not-a-vector"})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-groups-missing-label"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :groups [{:id "step_001"
                                  :step_number 1}]})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-groups-wrong-param-type"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :groups [{:id "step_001"
                                  :label "Settings"
                                  :step_number 1
                                  :parameters "not-a-vector"}]})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-groups-extra-field"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :groups [{:id "step_001"
                                  :label "Settings"
                                  :step_number 1
                                  :unknown "not-allowed"}]}))))

    (testing "invalid app job view - invalid nested limitChecks"
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-limitchecks"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :limitChecks "not-a-map"})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-limitchecks-missing-canrun"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :limitChecks {:results []}})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-limitchecks-results-type"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :limitChecks {:canRun true
                                      :results "not-a-vector"}}))))

    (testing "invalid app job view - invalid versions list"
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-versions"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :versions "not-a-vector"})))
      (is (not (valid? apps/AppJobView
                       {:id "app-bad-versions-missing-fields"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :versions [{:version "1.0.0"}]}))))

    (testing "invalid app job view - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppJobView
                       {:id "app-extra-field"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :extra-field "not-allowed"})))
      (is (not (valid? apps/AppJobView
                       {:id "app-extra-field2"
                        :name "App"
                        :description "Description"
                        :version "1.0.0"
                        :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :app_type "DE"
                        :label "Label"
                        :deleted false
                        :disabled false
                        :unknown_key "also-not-allowed"}))))))

(deftest test-AppListingJobStats
  (testing "AppListingJobStats validation"
    (testing "valid job stats"
      (testing "with all fields"
        (is (valid? apps/AppListingJobStats
                    {:job_count_completed 42
                     :job_last_completed #inst "2025-10-25T16:30:00.000-00:00"}))
        (is (valid? apps/AppListingJobStats
                    {:job_count_completed 100
                     :job_last_completed #inst "2024-01-15T10:30:00.000-00:00"}))
        (is (valid? apps/AppListingJobStats
                    {:job_count_completed 0
                     :job_last_completed #inst "2023-12-31T23:59:59.999-00:00"})))

      (testing "without optional job_last_completed field"
        (is (valid? apps/AppListingJobStats
                    {:job_count_completed 42}))
        (is (valid? apps/AppListingJobStats
                    {:job_count_completed 0}))
        (is (valid? apps/AppListingJobStats
                    {:job_count_completed 999}))))

    (testing "invalid job stats"
      (testing "missing required fields"
        (is (not (valid? apps/AppListingJobStats {})))
        (is (not (valid? apps/AppListingJobStats
                         {:job_last_completed #inst "2025-10-25T16:30:00.000-00:00"}))))

      (testing "invalid field types"
        (is (not (valid? apps/AppListingJobStats
                         {:job_count_completed "42"
                          :job_last_completed #inst "2025-10-25T16:30:00.000-00:00"})))
        (is (not (valid? apps/AppListingJobStats
                         {:job_count_completed 42.5
                          :job_last_completed #inst "2025-10-25T16:30:00.000-00:00"})))
        (is (not (valid? apps/AppListingJobStats
                         {:job_count_completed nil})))
        (is (not (valid? apps/AppListingJobStats
                         {:job_count_completed true})))
        (is (not (valid? apps/AppListingJobStats
                         {:job_count_completed 42
                          :job_last_completed "2025-10-25T16:30:00.000-00:00"})))
        (is (not (valid? apps/AppListingJobStats
                         {:job_count_completed 42
                          :job_last_completed 1729872600000})))
        (is (not (valid? apps/AppListingJobStats
                         {:job_count_completed 42
                          :job_last_completed nil}))))

      (testing "extra fields not allowed"
        (is (not (valid? apps/AppListingJobStats
                         {:job_count_completed 42
                          :job_last_completed #inst "2025-10-25T16:30:00.000-00:00"
                          :extra-field "not-allowed"})))
        (is (not (valid? apps/AppListingJobStats
                         {:job_count_completed 42
                          :job_count_failed 5})))
        (is (not (valid? apps/AppListingJobStats
                         {:job_count_completed 42
                          :job_last_completed #inst "2025-10-25T16:30:00.000-00:00"
                          :job_first_completed #inst "2024-01-01T00:00:00.000-00:00"})))))))

(deftest test-AppDetailCategory
  (testing "AppDetailCategory validation"
    (testing "valid app detail category"
      (is (valid? apps/AppDetailCategory
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "Bioinformatics"}))
      (is (valid? apps/AppDetailCategory
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174001"
                   :name "Data Science"}))
      (is (valid? apps/AppDetailCategory
                  {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :name "Image Analysis"}))
      (is (valid? apps/AppDetailCategory
                  {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                   :name "A"})))

    (testing "invalid app detail category"
      (testing "missing required fields"
        (is (not (valid? apps/AppDetailCategory {})))
        (is (not (valid? apps/AppDetailCategory {:id #uuid "123e4567-e89b-12d3-a456-426614174000"})))
        (is (not (valid? apps/AppDetailCategory {:name "Bioinformatics"}))))

      (testing "invalid field types"
        (is (not (valid? apps/AppDetailCategory
                         {:id "not-a-uuid"
                          :name "Bioinformatics"})))
        (is (not (valid? apps/AppDetailCategory
                         {:id 123
                          :name "Bioinformatics"})))
        (is (not (valid? apps/AppDetailCategory
                         {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                          :name 123})))
        (is (not (valid? apps/AppDetailCategory
                         {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                          :name nil})))
        (is (not (valid? apps/AppDetailCategory
                         {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                          :name true}))))

      (testing "extra fields not allowed"
        (is (not (valid? apps/AppDetailCategory
                         {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                          :name "Bioinformatics"
                          :extra-field "not-allowed"})))
        (is (not (valid? apps/AppDetailCategory
                         {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                          :name "Bioinformatics"
                          :description "This should not be here"})))))))

(deftest test-PipelineEligibility
  (testing "PipelineEligibility validation"
    (testing "valid pipeline eligibility"
      (is (valid? apps/PipelineEligibility
                  {:is_valid true
                   :reason "This app contains tasks that are not public"}))
      (is (valid? apps/PipelineEligibility
                  {:is_valid false
                   :reason "App cannot be used in a pipeline because it contains private tools"}))
      (is (valid? apps/PipelineEligibility
                  {:is_valid true
                   :reason ""}))
      (is (valid? apps/PipelineEligibility
                  {:is_valid false
                   :reason "All tasks must be public to use in a pipeline"})))

    (testing "invalid pipeline eligibility"
      (testing "missing required fields"
        (is (not (valid? apps/PipelineEligibility {})))
        (is (not (valid? apps/PipelineEligibility {:is_valid true})))
        (is (not (valid? apps/PipelineEligibility {:reason "Some reason"}))))

      (testing "invalid field types"
        (is (not (valid? apps/PipelineEligibility
                         {:is_valid "not-a-boolean"
                          :reason "Some reason"})))
        (is (not (valid? apps/PipelineEligibility
                         {:is_valid 1
                          :reason "Some reason"})))
        (is (not (valid? apps/PipelineEligibility
                         {:is_valid nil
                          :reason "Some reason"})))
        (is (not (valid? apps/PipelineEligibility
                         {:is_valid true
                          :reason 123})))
        (is (not (valid? apps/PipelineEligibility
                         {:is_valid true
                          :reason nil})))
        (is (not (valid? apps/PipelineEligibility
                         {:is_valid true
                          :reason true}))))

      (testing "extra fields not allowed"
        (is (not (valid? apps/PipelineEligibility
                         {:is_valid true
                          :reason "Some reason"
                          :extra-field "not-allowed"})))
        (is (not (valid? apps/PipelineEligibility
                         {:is_valid false
                          :reason "App has private components"
                          :details "Additional details should not be here"})))))))

(deftest test-AppListingDetail
  (testing "AppListingDetail validation"
    (let [minimal-rating {:average 4.5
                          :total   42}
          minimal-pipeline {:is_valid true
                            :reason   ""}
          base-app-listing {:id                    "app-id-123"
                            :name                  "BLAST"
                            :description           "Basic sequence alignment"
                            :app_type              "DE"
                            :can_favor             true
                            :can_rate              true
                            :can_run               true
                            :deleted               false
                            :disabled              false
                            :integrator_email      "user@example.org"
                            :integrator_name       "Test User"
                            :is_public             true
                            :pipeline_eligibility  minimal-pipeline
                            :rating                minimal-rating
                            :step_count            1
                            :permission            "own"}]

      (testing "valid app listing with minimal required fields"
        (is (valid? apps/AppListingDetail base-app-listing)))

      (testing "valid app listing with all optional fields"
        (is (valid? apps/AppListingDetail
                    (assoc base-app-listing
                           :system_id "de"
                           :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                           :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                           :overall_job_type "executable"
                           :version "1.2.0"
                           :version_id "version-id-456"
                           :is_favorite true
                           :beta false
                           :isBlessed true
                           :wiki_url "https://wiki.example.org/apps/blast"
                           :limitChecks {:canRun  true
                                         :results []}))))

      (testing "valid app listing with complex rating including user data"
        (is (valid? apps/AppListingDetail
                    (assoc base-app-listing
                           :rating {:average    4.5
                                    :total      42
                                    :user       5
                                    :comment_id 123}))))

      (testing "valid app listing with complex pipeline eligibility"
        (is (valid? apps/AppListingDetail
                    (assoc base-app-listing
                           :pipeline_eligibility {:is_valid false
                                                  :reason   "App contains private tasks"}))))

      (testing "valid app listing with limit checks indicating failure"
        (is (valid? apps/AppListingDetail
                    (assoc base-app-listing
                           :limitChecks {:canRun  false
                                         :results [{:limitCheckID   "concurrent-job-limit"
                                                    :reasonCodes    ["MAX_JOBS_EXCEEDED"]
                                                    :additionalInfo {:current_jobs 10
                                                                     :max_jobs     5}}]}))))

      (testing "valid app listing with multiple limit check results"
        (is (valid? apps/AppListingDetail
                    (assoc base-app-listing
                           :limitChecks {:canRun  false
                                         :results [{:limitCheckID   "concurrent-job-limit"
                                                    :reasonCodes    ["MAX_JOBS_EXCEEDED"]
                                                    :additionalInfo {:current_jobs 10
                                                                     :max_jobs     5}}
                                                   {:limitCheckID   "quota-limit"
                                                    :reasonCodes    ["QUOTA_EXCEEDED" "DISK_FULL"]
                                                    :additionalInfo {:used  1000
                                                                     :limit 800}}]}))))

      (testing "missing required fields"
        (is (not (valid? apps/AppListingDetail {})))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :id))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :name))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :description))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :app_type))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :can_favor))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :can_rate))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :can_run))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :deleted))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :disabled))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :integrator_email))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :integrator_name))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :is_public))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :pipeline_eligibility))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :rating))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :step_count))))
        (is (not (valid? apps/AppListingDetail (dissoc base-app-listing :permission)))))

      (testing "invalid field types"
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :id 123))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :name 123))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :description nil))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :app_type 456))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :overall_job_type 123))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :can_favor "true"))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :can_rate "false"))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :can_run 1))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :deleted "false"))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :disabled "true"))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :version 123))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :version_id 456))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :integrator_email 123))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :integrator_name nil))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :is_favorite "yes"))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :is_public "true"))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :beta "false"))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :isBlessed "true"))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :pipeline_eligibility "eligible"))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :rating "5 stars"))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :step_count "1"))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :step_count 1.5))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :wiki_url 123))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :permission 123))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :system_id 123))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :integration_date "2024-01-15"))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :edited_date "2024-10-20")))))

      (testing "invalid nested rating structure"
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :rating {}))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :rating {:average 4.5}))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :rating {:total 42}))))
        (is (not (valid? apps/AppListingDetail
                         (assoc base-app-listing
                                :rating {:average "4.5"
                                         :total   42}))))
        (is (not (valid? apps/AppListingDetail
                         (assoc base-app-listing
                                :rating {:average 4.5
                                         :total   "42"}))))
        (is (not (valid? apps/AppListingDetail
                         (assoc base-app-listing
                                :rating {:average 4.5
                                         :total   42
                                         :user    "5"})))))

      (testing "invalid nested pipeline eligibility structure"
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :pipeline_eligibility {}))))
        (is (not (valid? apps/AppListingDetail
                         (assoc base-app-listing
                                :pipeline_eligibility {:is_valid true}))))
        (is (not (valid? apps/AppListingDetail
                         (assoc base-app-listing
                                :pipeline_eligibility {:reason "Some reason"}))))
        (is (not (valid? apps/AppListingDetail
                         (assoc base-app-listing
                                :pipeline_eligibility {:is_valid "true"
                                                       :reason   "Some reason"}))))
        (is (not (valid? apps/AppListingDetail
                         (assoc base-app-listing
                                :pipeline_eligibility {:is_valid true
                                                       :reason   123})))))

      (testing "invalid nested limit checks structure"
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :limitChecks {}))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :limitChecks {:canRun true}))))
        (is (not (valid? apps/AppListingDetail
                         (assoc base-app-listing
                                :limitChecks {:canRun  "true"
                                              :results []}))))
        (is (not (valid? apps/AppListingDetail
                         (assoc base-app-listing
                                :limitChecks {:canRun  true
                                              :results "[]"}))))
        (is (not (valid? apps/AppListingDetail
                         (assoc base-app-listing
                                :limitChecks {:canRun  true
                                              :results [{:limitCheckID "test"}]}))))
        (is (not (valid? apps/AppListingDetail
                         (assoc base-app-listing
                                :limitChecks {:canRun  true
                                              :results [{:limitCheckID   "test"
                                                         :reasonCodes    ["CODE"]
                                                         :additionalInfo {}
                                                         :extra-field    "not-allowed"}]})))))

      (testing "extra fields not allowed at top level"
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :extra-field "not-allowed"))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :custom_data {:key "value"}))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :tags ["tag1" "tag2"])))))

      (testing "optional fields can be omitted"
        (is (valid? apps/AppListingDetail base-app-listing))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :system_id "de")))
        (is (valid? apps/AppListingDetail (dissoc (assoc base-app-listing :system_id "de") :system_id)))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :overall_job_type "executable")))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :version "1.0.0")))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :version_id "v123")))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :is_favorite false)))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :beta true)))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :isBlessed false)))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :wiki_url "https://example.org")))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :integration_date #inst "2024-01-01")))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :edited_date #inst "2024-02-01")))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :limitChecks {:canRun  true
                                                                                 :results []}))))

      (testing "step_count must be integer not long/double"
        (is (valid? apps/AppListingDetail (assoc base-app-listing :step_count 1)))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :step_count 100)))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :step_count 0)))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :step_count 1.5))))
        (is (not (valid? apps/AppListingDetail (assoc base-app-listing :step_count 2.0)))))

      (testing "boundary cases for string fields"
        (is (valid? apps/AppListingDetail (assoc base-app-listing :id "")))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :name "")))
        (is (valid? apps/AppListingDetail (assoc base-app-listing :description "")))
        (is (valid? apps/AppListingDetail
                    (assoc base-app-listing
                           :id (apply str (repeat 1000 "x")))))
        (is (valid? apps/AppListingDetail
                    (assoc base-app-listing
                           :name (apply str (repeat 1000 "y"))))))

      (testing "all boolean fields accept only true/false"
        (doseq [field [:can_favor :can_rate :can_run :deleted :disabled :is_public]]
          (is (valid? apps/AppListingDetail (assoc base-app-listing field true)))
          (is (valid? apps/AppListingDetail (assoc base-app-listing field false)))
          (is (not (valid? apps/AppListingDetail (assoc base-app-listing field nil))))
          (is (not (valid? apps/AppListingDetail (assoc base-app-listing field "true"))))
          (is (not (valid? apps/AppListingDetail (assoc base-app-listing field 1))))
          (is (not (valid? apps/AppListingDetail (assoc base-app-listing field 0)))))

        (doseq [field [:is_favorite :beta :isBlessed]]
          (is (valid? apps/AppListingDetail (assoc base-app-listing field true)))
          (is (valid? apps/AppListingDetail (assoc base-app-listing field false)))
          (is (not (valid? apps/AppListingDetail (assoc base-app-listing field "true"))))
          (is (not (valid? apps/AppListingDetail (assoc base-app-listing field 1))))))

      (testing "date fields accept inst types"
        (is (valid? apps/AppListingDetail
                    (assoc base-app-listing
                           :integration_date #inst "2024-01-15T10:30:00.000-00:00")))
        (is (valid? apps/AppListingDetail
                    (assoc base-app-listing
                           :edited_date #inst "2024-10-20T14:45:00.000-00:00")))
        (is (valid? apps/AppListingDetail
                    (assoc base-app-listing
                           :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                           :edited_date #inst "2024-10-20T14:45:00.000-00:00")))
        (is (not (valid? apps/AppListingDetail
                         (assoc base-app-listing
                                :integration_date "2024-01-15T10:30:00Z"))))
        (is (not (valid? apps/AppListingDetail
                         (assoc base-app-listing
                                :edited_date "2024-10-20"))))))))

(deftest test-AppListing
  (testing "AppListing validation"
    (let [minimal-rating {:average 4.5
                          :total   42}
          minimal-pipeline {:is_valid true
                            :reason   ""}
          base-app-listing-detail {:id                   "app-id-123"
                                   :name                 "BLAST"
                                   :description          "Basic sequence alignment"
                                   :app_type             "DE"
                                   :can_favor            true
                                   :can_rate             true
                                   :can_run              true
                                   :deleted              false
                                   :disabled             false
                                   :integrator_email     "user@example.org"
                                   :integrator_name      "Test User"
                                   :is_public            true
                                   :pipeline_eligibility minimal-pipeline
                                   :rating               minimal-rating
                                   :step_count           1
                                   :permission           "own"}
          minimal-app-listing {:total 0
                               :apps  []}
          single-app-listing {:total 1
                              :apps  [base-app-listing-detail]}]

      (testing "valid app listing with no apps"
        (is (valid? apps/AppListing minimal-app-listing)))

      (testing "valid app listing with single app"
        (is (valid? apps/AppListing single-app-listing)))

      (testing "valid app listing with multiple apps"
        (is (valid? apps/AppListing
                    {:total 3
                     :apps  [base-app-listing-detail
                             (assoc base-app-listing-detail :id "app-id-456" :name "BWA")
                             (assoc base-app-listing-detail :id "app-id-789" :name "Bowtie2")]})))

      (testing "valid app listing with apps containing optional fields"
        (is (valid? apps/AppListing
                    {:total 1
                     :apps  [(assoc base-app-listing-detail
                                    :system_id "de"
                                    :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                                    :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                                    :overall_job_type "executable"
                                    :version "1.2.0"
                                    :version_id "version-id-456"
                                    :is_favorite true
                                    :beta false
                                    :isBlessed true
                                    :wiki_url "https://wiki.example.org/apps/blast"
                                    :limitChecks {:canRun  true
                                                  :results []})]})))

      (testing "valid app listing with large total"
        (is (valid? apps/AppListing
                    {:total 1000
                     :apps  [base-app-listing-detail]})))

      (testing "valid app listing with zero total and empty apps list"
        (is (valid? apps/AppListing
                    {:total 0
                     :apps  []})))

      (testing "missing required fields"
        (is (not (valid? apps/AppListing {})))
        (is (not (valid? apps/AppListing {:total 0})))
        (is (not (valid? apps/AppListing {:apps []})))
        (is (not (valid? apps/AppListing {:total 1 :apps [base-app-listing-detail] :extra-field "not-allowed"}))))

      (testing "invalid field types for total"
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :total "0"))))
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :total 0.0))))
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :total 1.5))))
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :total nil))))
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :total true)))))

      (testing "invalid field types for apps"
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :apps nil))))
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :apps "[]"))))
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :apps {}))))
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :apps base-app-listing-detail)))))

      (testing "invalid app listing detail in apps vector"
        (is (not (valid? apps/AppListing
                         {:total 1
                          :apps  [{}]})))
        (is (not (valid? apps/AppListing
                         {:total 1
                          :apps  [(dissoc base-app-listing-detail :id)]})))
        (is (not (valid? apps/AppListing
                         {:total 1
                          :apps  [(dissoc base-app-listing-detail :name)]})))
        (is (not (valid? apps/AppListing
                         {:total 1
                          :apps  [(assoc base-app-listing-detail :id 123)]})))
        (is (not (valid? apps/AppListing
                         {:total 1
                          :apps  [(assoc base-app-listing-detail :extra-field "not-allowed")]}))))

      (testing "extra fields not allowed"
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :extra-field "not-allowed"))))
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :custom_data {:key "value"}))))
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :metadata "extra")))))

      (testing "boundary cases for total"
        (is (valid? apps/AppListing (assoc minimal-app-listing :total 0)))
        (is (valid? apps/AppListing (assoc minimal-app-listing :total 1)))
        (is (valid? apps/AppListing (assoc minimal-app-listing :total 999999)))
        (is (valid? apps/AppListing (assoc minimal-app-listing :total -1)))
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :total 1.0))))
        (is (not (valid? apps/AppListing (assoc minimal-app-listing :total 0.5)))))

      (testing "apps vector can contain multiple complete app listings"
        (let [app2 (assoc base-app-listing-detail
                          :id "app-id-456"
                          :name "Different App"
                          :description "Another app"
                          :version "2.0.0"
                          :is_favorite false
                          :beta true)
              app3 (assoc base-app-listing-detail
                          :id "app-id-789"
                          :name "Third App"
                          :description "Yet another app"
                          :overall_job_type "interactive"
                          :limitChecks {:canRun  false
                                        :results [{:limitCheckID   "concurrent-job-limit"
                                                   :reasonCodes    ["MAX_JOBS_EXCEEDED"]
                                                   :additionalInfo {:current_jobs 10
                                                                    :max_jobs     5}}]})]
          (is (valid? apps/AppListing
                      {:total 3
                       :apps  [base-app-listing-detail app2 app3]}))))

      (testing "apps with complex nested structures"
        (is (valid? apps/AppListing
                    {:total 1
                     :apps  [(assoc base-app-listing-detail
                                    :rating {:average    4.8
                                             :total      100
                                             :user       5
                                             :comment_id 456}
                                    :pipeline_eligibility {:is_valid false
                                                           :reason   "Contains private tasks"}
                                    :limitChecks {:canRun  false
                                                  :results [{:limitCheckID   "quota-limit"
                                                             :reasonCodes    ["QUOTA_EXCEEDED"]
                                                             :additionalInfo {:used  1000
                                                                              :limit 800}}]})]})))

      (testing "empty apps vector with various totals"
        (is (valid? apps/AppListing {:total 0 :apps []}))
        (is (valid? apps/AppListing {:total -1 :apps []}))
        (is (valid? apps/AppListing {:total 1000 :apps []})))

      (testing "apps vector with heterogeneous optional fields"
        (let [app-with-version (assoc base-app-listing-detail :version "1.0.0")
              app-with-dates (assoc base-app-listing-detail
                                    :id "app-with-dates"
                                    :integration_date #inst "2024-01-01T00:00:00.000-00:00"
                                    :edited_date #inst "2024-02-01T00:00:00.000-00:00")
              app-with-flags (assoc base-app-listing-detail
                                    :id "app-with-flags"
                                    :is_favorite true
                                    :beta true
                                    :isBlessed false)]
          (is (valid? apps/AppListing
                      {:total 3
                       :apps  [app-with-version app-with-dates app-with-flags]})))))))

(deftest test-AppListingValidSortFields
  (testing "AppListingValidSortFields contains expected fields"
    (testing "contains all AppListingDetail fields except excluded ones"
      (is (contains? apps/AppListingValidSortFields :id))
      (is (contains? apps/AppListingValidSortFields :name))
      (is (contains? apps/AppListingValidSortFields :description))
      (is (contains? apps/AppListingValidSortFields :integration_date))
      (is (contains? apps/AppListingValidSortFields :edited_date))
      (is (contains? apps/AppListingValidSortFields :system_id))
      (is (contains? apps/AppListingValidSortFields :version))
      (is (contains? apps/AppListingValidSortFields :version_id))
      (is (contains? apps/AppListingValidSortFields :limitChecks))
      (is (contains? apps/AppListingValidSortFields :overall_job_type))
      (is (contains? apps/AppListingValidSortFields :deleted))
      (is (contains? apps/AppListingValidSortFields :disabled))
      (is (contains? apps/AppListingValidSortFields :integrator_email))
      (is (contains? apps/AppListingValidSortFields :integrator_name))
      (is (contains? apps/AppListingValidSortFields :is_favorite))
      (is (contains? apps/AppListingValidSortFields :is_public))
      (is (contains? apps/AppListingValidSortFields :beta))
      (is (contains? apps/AppListingValidSortFields :isBlessed))
      (is (contains? apps/AppListingValidSortFields :step_count))
      (is (contains? apps/AppListingValidSortFields :wiki_url))
      (is (contains? apps/AppListingValidSortFields :permission)))

    (testing "contains added rating fields"
      (is (contains? apps/AppListingValidSortFields :average_rating))
      (is (contains? apps/AppListingValidSortFields :user_rating)))

    (testing "does not contain excluded fields"
      (is (not (contains? apps/AppListingValidSortFields :app_type)))
      (is (not (contains? apps/AppListingValidSortFields :can_favor)))
      (is (not (contains? apps/AppListingValidSortFields :can_rate)))
      (is (not (contains? apps/AppListingValidSortFields :can_run)))
      (is (not (contains? apps/AppListingValidSortFields :pipeline_eligibility)))
      (is (not (contains? apps/AppListingValidSortFields :rating))))

    (testing "is a set of keywords"
      (is (set? apps/AppListingValidSortFields))
      (is (every? keyword? apps/AppListingValidSortFields)))

    (testing "has expected size"
      (is (= 23 (count apps/AppListingValidSortFields))))))

(deftest test-AppSearchValidSortFields
  (testing "AppSearchValidSortFields contains expected fields"
    (testing "contains all AppListingDetail fields except excluded ones"
      (is (contains? apps/AppSearchValidSortFields :id))
      (is (contains? apps/AppSearchValidSortFields :name))
      (is (contains? apps/AppSearchValidSortFields :description))
      (is (contains? apps/AppSearchValidSortFields :integration_date))
      (is (contains? apps/AppSearchValidSortFields :edited_date))
      (is (contains? apps/AppSearchValidSortFields :system_id))
      (is (contains? apps/AppSearchValidSortFields :version))
      (is (contains? apps/AppSearchValidSortFields :version_id))
      (is (contains? apps/AppSearchValidSortFields :limitChecks))
      (is (contains? apps/AppSearchValidSortFields :overall_job_type))
      (is (contains? apps/AppSearchValidSortFields :deleted))
      (is (contains? apps/AppSearchValidSortFields :disabled))
      (is (contains? apps/AppSearchValidSortFields :integrator_email))
      (is (contains? apps/AppSearchValidSortFields :integrator_name))
      (is (contains? apps/AppSearchValidSortFields :is_favorite))
      (is (contains? apps/AppSearchValidSortFields :is_public))
      (is (contains? apps/AppSearchValidSortFields :beta))
      (is (contains? apps/AppSearchValidSortFields :isBlessed))
      (is (contains? apps/AppSearchValidSortFields :step_count))
      (is (contains? apps/AppSearchValidSortFields :wiki_url))
      (is (contains? apps/AppSearchValidSortFields :permission)))

    (testing "contains search-specific rating fields"
      (is (contains? apps/AppSearchValidSortFields :average))
      (is (contains? apps/AppSearchValidSortFields :total)))

    (testing "does not contain listing-specific rating fields"
      (is (not (contains? apps/AppSearchValidSortFields :average_rating)))
      (is (not (contains? apps/AppSearchValidSortFields :user_rating))))

    (testing "does not contain excluded fields from AppListingValidSortFields"
      (is (not (contains? apps/AppSearchValidSortFields :app_type)))
      (is (not (contains? apps/AppSearchValidSortFields :can_favor)))
      (is (not (contains? apps/AppSearchValidSortFields :can_rate)))
      (is (not (contains? apps/AppSearchValidSortFields :can_run)))
      (is (not (contains? apps/AppSearchValidSortFields :pipeline_eligibility)))
      (is (not (contains? apps/AppSearchValidSortFields :rating))))

    (testing "is a set of keywords"
      (is (set? apps/AppSearchValidSortFields))
      (is (every? keyword? apps/AppSearchValidSortFields)))

    (testing "has expected size"
      (is (= 23 (count apps/AppSearchValidSortFields))))

    (testing "is derived from AppListingValidSortFields"
      (let [expected-fields (-> apps/AppListingValidSortFields
                                (clojure.set/difference #{:average_rating :user_rating})
                                (conj :average :total))]
        (is (= expected-fields apps/AppSearchValidSortFields))))))

(deftest test-AppFilterParams
  (testing "AppFilterParams validation"
    (testing "valid filter params with app-type"
      (is (valid? apps/AppFilterParams {:app-type "DE"}))
      (is (valid? apps/AppFilterParams {:app-type "External"}))
      (is (valid? apps/AppFilterParams {:app-type "agave"}))
      (is (valid? apps/AppFilterParams {:app-type ""})))

    (testing "valid filter params without app-type (optional field)"
      (is (valid? apps/AppFilterParams {})))

    (testing "invalid filter params - wrong type for app-type"
      (is (not (valid? apps/AppFilterParams {:app-type 123})))
      (is (not (valid? apps/AppFilterParams {:app-type true})))
      (is (not (valid? apps/AppFilterParams {:app-type nil})))
      (is (not (valid? apps/AppFilterParams {:app-type []})))
      (is (not (valid? apps/AppFilterParams {:app-type {}}))))

    (testing "invalid filter params - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppFilterParams {:app-type "DE" :extra-field "value"})))
      (is (not (valid? apps/AppFilterParams {:unknown-key "value"})))
      (is (not (valid? apps/AppFilterParams {:app-type "DE" :another-field 123}))))

    (testing "invalid filter params - not a map"
      (is (not (valid? apps/AppFilterParams "string")))
      (is (not (valid? apps/AppFilterParams 123)))
      (is (not (valid? apps/AppFilterParams [])))
      (is (not (valid? apps/AppFilterParams nil))))))

(deftest test-AppListingPagingParams
  (testing "AppListingPagingParams validation"
    (testing "valid params with no fields (all optional)"
      (is (valid? apps/AppListingPagingParams {})))

    (testing "valid params with limit"
      (is (valid? apps/AppListingPagingParams {:limit 50}))
      (is (valid? apps/AppListingPagingParams {:limit 1}))
      (is (valid? apps/AppListingPagingParams {:limit 1000})))

    (testing "valid params with offset"
      (is (valid? apps/AppListingPagingParams {:offset 0}))
      (is (valid? apps/AppListingPagingParams {:offset 100}))
      (is (valid? apps/AppListingPagingParams {:offset 999})))

    (testing "valid params with sort-field from AppListingValidSortFields"
      (is (valid? apps/AppListingPagingParams {:sort-field :name}))
      (is (valid? apps/AppListingPagingParams {:sort-field :id}))
      (is (valid? apps/AppListingPagingParams {:sort-field :integration_date}))
      (is (valid? apps/AppListingPagingParams {:sort-field :edited_date}))
      (is (valid? apps/AppListingPagingParams {:sort-field :average_rating}))
      (is (valid? apps/AppListingPagingParams {:sort-field :user_rating})))

    (testing "valid params with sort-dir from PagingParams"
      (is (valid? apps/AppListingPagingParams {:sort-dir "ASC"}))
      (is (valid? apps/AppListingPagingParams {:sort-dir "DESC"})))

    (testing "valid params with app-type from AppFilterParams"
      (is (valid? apps/AppListingPagingParams {:app-type "DE"}))
      (is (valid? apps/AppListingPagingParams {:app-type "External"}))
      (is (valid? apps/AppListingPagingParams {:app-type "agave"}))
      (is (valid? apps/AppListingPagingParams {:app-type ""})))

    (testing "valid params with all fields"
      (is (valid? apps/AppListingPagingParams
                  {:limit 50
                   :offset 0
                   :sort-field :name
                   :sort-dir "ASC"
                   :app-type "DE"}))
      (is (valid? apps/AppListingPagingParams
                  {:limit 100
                   :offset 200
                   :sort-field :integration_date
                   :sort-dir "DESC"
                   :app-type "External"})))

    (testing "valid params with subset of fields"
      (is (valid? apps/AppListingPagingParams {:limit 25 :offset 50}))
      (is (valid? apps/AppListingPagingParams {:sort-field :name :sort-dir "DESC"}))
      (is (valid? apps/AppListingPagingParams {:limit 100 :app-type "DE"}))
      (is (valid? apps/AppListingPagingParams {:offset 0 :sort-field :edited_date})))

    (testing "invalid params - invalid limit (not positive)"
      (is (not (valid? apps/AppListingPagingParams {:limit 0})))
      (is (not (valid? apps/AppListingPagingParams {:limit -1})))
      (is (not (valid? apps/AppListingPagingParams {:limit -100}))))

    (testing "invalid params - invalid limit (wrong type)"
      (is (not (valid? apps/AppListingPagingParams {:limit "50"})))
      (is (not (valid? apps/AppListingPagingParams {:limit 50.5})))
      (is (not (valid? apps/AppListingPagingParams {:limit nil})))
      (is (not (valid? apps/AppListingPagingParams {:limit true}))))

    (testing "invalid params - invalid offset (negative)"
      (is (not (valid? apps/AppListingPagingParams {:offset -1})))
      (is (not (valid? apps/AppListingPagingParams {:offset -100}))))

    (testing "invalid params - invalid offset (wrong type)"
      (is (not (valid? apps/AppListingPagingParams {:offset "0"})))
      (is (not (valid? apps/AppListingPagingParams {:offset 10.5})))
      (is (not (valid? apps/AppListingPagingParams {:offset nil})))
      (is (not (valid? apps/AppListingPagingParams {:offset false}))))

    (testing "invalid params - invalid sort-field (not in enum)"
      (is (not (valid? apps/AppListingPagingParams {:sort-field :invalid_field})))
      (is (not (valid? apps/AppListingPagingParams {:sort-field :app_type})))
      (is (not (valid? apps/AppListingPagingParams {:sort-field :can_favor})))
      (is (not (valid? apps/AppListingPagingParams {:sort-field :can_rate})))
      (is (not (valid? apps/AppListingPagingParams {:sort-field :can_run})))
      (is (not (valid? apps/AppListingPagingParams {:sort-field :pipeline_eligibility})))
      (is (not (valid? apps/AppListingPagingParams {:sort-field :rating}))))

    (testing "invalid params - invalid sort-field (wrong type)"
      (is (not (valid? apps/AppListingPagingParams {:sort-field "name"})))
      (is (not (valid? apps/AppListingPagingParams {:sort-field 123})))
      (is (not (valid? apps/AppListingPagingParams {:sort-field nil})))
      (is (not (valid? apps/AppListingPagingParams {:sort-field true}))))

    (testing "invalid params - invalid sort-dir (not in enum)"
      (is (not (valid? apps/AppListingPagingParams {:sort-dir "asc"})))
      (is (not (valid? apps/AppListingPagingParams {:sort-dir "desc"})))
      (is (not (valid? apps/AppListingPagingParams {:sort-dir "ascending"})))
      (is (not (valid? apps/AppListingPagingParams {:sort-dir "ASCENDING"})))
      (is (not (valid? apps/AppListingPagingParams {:sort-dir "invalid"}))))

    (testing "invalid params - invalid sort-dir (wrong type)"
      (is (not (valid? apps/AppListingPagingParams {:sort-dir :ASC})))
      (is (not (valid? apps/AppListingPagingParams {:sort-dir 123})))
      (is (not (valid? apps/AppListingPagingParams {:sort-dir nil})))
      (is (not (valid? apps/AppListingPagingParams {:sort-dir true}))))

    (testing "invalid params - invalid app-type (wrong type)"
      (is (not (valid? apps/AppListingPagingParams {:app-type 123})))
      (is (not (valid? apps/AppListingPagingParams {:app-type true})))
      (is (not (valid? apps/AppListingPagingParams {:app-type nil})))
      (is (not (valid? apps/AppListingPagingParams {:app-type []})))
      (is (not (valid? apps/AppListingPagingParams {:app-type {}}))))

    (testing "invalid params - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppListingPagingParams {:extra-field "value"})))
      (is (not (valid? apps/AppListingPagingParams {:unknown-key "value"})))
      (is (not (valid? apps/AppListingPagingParams {:limit 50 :extra-field "value"})))
      (is (not (valid? apps/AppListingPagingParams {:app-type "DE" :another-field 123}))))

    (testing "invalid params - not a map"
      (is (not (valid? apps/AppListingPagingParams "string")))
      (is (not (valid? apps/AppListingPagingParams 123)))
      (is (not (valid? apps/AppListingPagingParams [])))
      (is (not (valid? apps/AppListingPagingParams nil))))))

(deftest test-AppSearchParams
  (testing "AppSearchParams validation"
    (testing "valid params with no fields (all optional)"
      (is (valid? apps/AppSearchParams {})))

    (testing "valid params with search field"
      (is (valid? apps/AppSearchParams {:search "BLAST"}))
      (is (valid? apps/AppSearchParams {:search "genome"}))
      (is (valid? apps/AppSearchParams {:search ""}))
      (is (valid? apps/AppSearchParams {:search "RNA-seq analysis"})))

    (testing "valid params with start_date field"
      (is (valid? apps/AppSearchParams {:start_date #inst "2024-01-01T00:00:00.000-00:00"}))
      (is (valid? apps/AppSearchParams {:start_date #inst "2020-06-15T12:30:45.000-00:00"}))
      (is (valid? apps/AppSearchParams {:start_date #inst "2025-12-31T23:59:59.000-00:00"})))

    (testing "valid params with end_date field"
      (is (valid? apps/AppSearchParams {:end_date #inst "2025-12-31T23:59:59.000-00:00"}))
      (is (valid? apps/AppSearchParams {:end_date #inst "2024-06-30T00:00:00.000-00:00"}))
      (is (valid? apps/AppSearchParams {:end_date #inst "2020-01-01T00:00:00.000-00:00"})))

    (testing "valid params with both start_date and end_date"
      (is (valid? apps/AppSearchParams {:start_date #inst "2024-01-01T00:00:00.000-00:00"
                                        :end_date #inst "2025-12-31T23:59:59.000-00:00"}))
      (is (valid? apps/AppSearchParams {:start_date #inst "2020-01-01T00:00:00.000-00:00"
                                        :end_date #inst "2020-12-31T23:59:59.000-00:00"})))

    (testing "valid params with sort-field from AppSearchValidSortFields"
      ;; Fields that are in AppSearchValidSortFields (includes :average and :total)
      (is (valid? apps/AppSearchParams {:sort-field :average}))
      (is (valid? apps/AppSearchParams {:sort-field :total}))
      (is (valid? apps/AppSearchParams {:sort-field :name}))
      (is (valid? apps/AppSearchParams {:sort-field :id}))
      (is (valid? apps/AppSearchParams {:sort-field :integration_date}))
      (is (valid? apps/AppSearchParams {:sort-field :edited_date}))
      (is (valid? apps/AppSearchParams {:sort-field :description}))
      (is (valid? apps/AppSearchParams {:sort-field :integrator_name}))
      (is (valid? apps/AppSearchParams {:sort-field :integrator_email})))

    (testing "valid params with limit from PagingParams"
      (is (valid? apps/AppSearchParams {:limit 50}))
      (is (valid? apps/AppSearchParams {:limit 1}))
      (is (valid? apps/AppSearchParams {:limit 1000})))

    (testing "valid params with offset from PagingParams"
      (is (valid? apps/AppSearchParams {:offset 0}))
      (is (valid? apps/AppSearchParams {:offset 100}))
      (is (valid? apps/AppSearchParams {:offset 999})))

    (testing "valid params with sort-dir from PagingParams"
      (is (valid? apps/AppSearchParams {:sort-dir "ASC"}))
      (is (valid? apps/AppSearchParams {:sort-dir "DESC"})))

    (testing "valid params with app-type from AppFilterParams"
      (is (valid? apps/AppSearchParams {:app-type "DE"}))
      (is (valid? apps/AppSearchParams {:app-type "External"}))
      (is (valid? apps/AppSearchParams {:app-type "agave"}))
      (is (valid? apps/AppSearchParams {:app-type ""})))

    (testing "valid params with all fields"
      (is (valid? apps/AppSearchParams
                  {:search "BLAST"
                   :start_date #inst "2024-01-01T00:00:00.000-00:00"
                   :end_date #inst "2025-12-31T23:59:59.000-00:00"
                   :sort-field :name
                   :limit 50
                   :offset 0
                   :sort-dir "ASC"
                   :app-type "DE"}))
      (is (valid? apps/AppSearchParams
                  {:search "genome analysis"
                   :start_date #inst "2020-01-01T00:00:00.000-00:00"
                   :end_date #inst "2024-06-30T23:59:59.000-00:00"
                   :sort-field :total
                   :limit 100
                   :offset 200
                   :sort-dir "DESC"
                   :app-type "External"})))

    (testing "valid params with subset of fields"
      (is (valid? apps/AppSearchParams {:search "RNA" :limit 25}))
      (is (valid? apps/AppSearchParams {:start_date #inst "2024-01-01T00:00:00.000-00:00" :offset 50}))
      (is (valid? apps/AppSearchParams {:end_date #inst "2025-12-31T23:59:59.000-00:00" :sort-field :average}))
      (is (valid? apps/AppSearchParams {:sort-field :name :sort-dir "DESC"}))
      (is (valid? apps/AppSearchParams {:limit 100 :app-type "DE"}))
      (is (valid? apps/AppSearchParams {:search "protein" :start_date #inst "2024-01-01T00:00:00.000-00:00"}))
      (is (valid? apps/AppSearchParams {:offset 0 :sort-field :edited_date :app-type "agave"})))

    (testing "invalid params - invalid search field (wrong type)"
      (is (not (valid? apps/AppSearchParams {:search 123})))
      (is (not (valid? apps/AppSearchParams {:search true})))
      (is (not (valid? apps/AppSearchParams {:search nil})))
      (is (not (valid? apps/AppSearchParams {:search []})))
      (is (not (valid? apps/AppSearchParams {:search {}}))))

    (testing "invalid params - invalid start_date (wrong type)"
      (is (not (valid? apps/AppSearchParams {:start_date "2024-01-01"})))
      (is (not (valid? apps/AppSearchParams {:start_date 1704067200000})))
      (is (not (valid? apps/AppSearchParams {:start_date nil})))
      (is (not (valid? apps/AppSearchParams {:start_date true})))
      (is (not (valid? apps/AppSearchParams {:start_date {}}))))

    (testing "invalid params - invalid end_date (wrong type)"
      (is (not (valid? apps/AppSearchParams {:end_date "2025-12-31"})))
      (is (not (valid? apps/AppSearchParams {:end_date 1735689599000})))
      (is (not (valid? apps/AppSearchParams {:end_date nil})))
      (is (not (valid? apps/AppSearchParams {:end_date false})))
      (is (not (valid? apps/AppSearchParams {:end_date []}))))

    (testing "invalid params - invalid sort-field (not in AppSearchValidSortFields enum)"
      ;; Fields that are excluded from AppSearchValidSortFields
      (is (not (valid? apps/AppSearchParams {:sort-field :average_rating})))
      (is (not (valid? apps/AppSearchParams {:sort-field :user_rating})))
      (is (not (valid? apps/AppSearchParams {:sort-field :app_type})))
      (is (not (valid? apps/AppSearchParams {:sort-field :can_favor})))
      (is (not (valid? apps/AppSearchParams {:sort-field :can_rate})))
      (is (not (valid? apps/AppSearchParams {:sort-field :can_run})))
      (is (not (valid? apps/AppSearchParams {:sort-field :pipeline_eligibility})))
      (is (not (valid? apps/AppSearchParams {:sort-field :rating})))
      ;; Completely invalid fields
      (is (not (valid? apps/AppSearchParams {:sort-field :invalid_field})))
      (is (not (valid? apps/AppSearchParams {:sort-field :nonexistent}))))

    (testing "invalid params - invalid sort-field (wrong type)"
      (is (not (valid? apps/AppSearchParams {:sort-field "name"})))
      (is (not (valid? apps/AppSearchParams {:sort-field 123})))
      (is (not (valid? apps/AppSearchParams {:sort-field nil})))
      (is (not (valid? apps/AppSearchParams {:sort-field true}))))

    (testing "invalid params - invalid limit (not positive)"
      (is (not (valid? apps/AppSearchParams {:limit 0})))
      (is (not (valid? apps/AppSearchParams {:limit -1})))
      (is (not (valid? apps/AppSearchParams {:limit -100}))))

    (testing "invalid params - invalid limit (wrong type)"
      (is (not (valid? apps/AppSearchParams {:limit "50"})))
      (is (not (valid? apps/AppSearchParams {:limit 50.5})))
      (is (not (valid? apps/AppSearchParams {:limit nil})))
      (is (not (valid? apps/AppSearchParams {:limit true}))))

    (testing "invalid params - invalid offset (negative)"
      (is (not (valid? apps/AppSearchParams {:offset -1})))
      (is (not (valid? apps/AppSearchParams {:offset -100}))))

    (testing "invalid params - invalid offset (wrong type)"
      (is (not (valid? apps/AppSearchParams {:offset "0"})))
      (is (not (valid? apps/AppSearchParams {:offset 10.5})))
      (is (not (valid? apps/AppSearchParams {:offset nil})))
      (is (not (valid? apps/AppSearchParams {:offset false}))))

    (testing "invalid params - invalid sort-dir (not in enum)"
      (is (not (valid? apps/AppSearchParams {:sort-dir "asc"})))
      (is (not (valid? apps/AppSearchParams {:sort-dir "desc"})))
      (is (not (valid? apps/AppSearchParams {:sort-dir "ascending"})))
      (is (not (valid? apps/AppSearchParams {:sort-dir "ASCENDING"})))
      (is (not (valid? apps/AppSearchParams {:sort-dir "invalid"}))))

    (testing "invalid params - invalid sort-dir (wrong type)"
      (is (not (valid? apps/AppSearchParams {:sort-dir :ASC})))
      (is (not (valid? apps/AppSearchParams {:sort-dir 123})))
      (is (not (valid? apps/AppSearchParams {:sort-dir nil})))
      (is (not (valid? apps/AppSearchParams {:sort-dir true}))))

    (testing "invalid params - invalid app-type (wrong type)"
      (is (not (valid? apps/AppSearchParams {:app-type 123})))
      (is (not (valid? apps/AppSearchParams {:app-type true})))
      (is (not (valid? apps/AppSearchParams {:app-type nil})))
      (is (not (valid? apps/AppSearchParams {:app-type []})))
      (is (not (valid? apps/AppSearchParams {:app-type {}}))))

    (testing "invalid params - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppSearchParams {:extra-field "value"})))
      (is (not (valid? apps/AppSearchParams {:unknown-key "value"})))
      (is (not (valid? apps/AppSearchParams {:search "BLAST" :extra-field "value"})))
      (is (not (valid? apps/AppSearchParams {:limit 50 :unknown "field"})))
      (is (not (valid? apps/AppSearchParams {:app-type "DE" :another-field 123}))))

    (testing "invalid params - not a map"
      (is (not (valid? apps/AppSearchParams "string")))
      (is (not (valid? apps/AppSearchParams 123)))
      (is (not (valid? apps/AppSearchParams [])))
      (is (not (valid? apps/AppSearchParams nil))))))

(deftest test-QualifiedAppId
  (testing "QualifiedAppId validation"
    (testing "valid qualified app id - all required fields"
      (is (valid? apps/QualifiedAppId
                  {:system_id "de"
                   :app_id "app-id-12345"}))
      (is (valid? apps/QualifiedAppId
                  {:system_id "agave"
                   :app_id "blast-2.2.30"}))
      (is (valid? apps/QualifiedAppId
                  {:system_id "osg"
                   :app_id "tool-abc-123"})))

    (testing "valid qualified app id - various valid system_id values"
      (is (valid? apps/QualifiedAppId
                  {:system_id "d"
                   :app_id "app"}))
      (is (valid? apps/QualifiedAppId
                  {:system_id "discovery-environment"
                   :app_id "app123"}))
      (is (valid? apps/QualifiedAppId
                  {:system_id "123"
                   :app_id "app"}))
      (is (valid? apps/QualifiedAppId
                  {:system_id "system-with-dashes"
                   :app_id "app"}))
      (is (valid? apps/QualifiedAppId
                  {:system_id "system_with_underscores"
                   :app_id "app"})))

    (testing "valid qualified app id - various valid app_id values"
      (is (valid? apps/QualifiedAppId
                  {:system_id "de"
                   :app_id "a"}))
      (is (valid? apps/QualifiedAppId
                  {:system_id "de"
                   :app_id "app-with-dashes"}))
      (is (valid? apps/QualifiedAppId
                  {:system_id "de"
                   :app_id "app_with_underscores"}))
      (is (valid? apps/QualifiedAppId
                  {:system_id "de"
                   :app_id "AppWithMixedCase123"}))
      (is (valid? apps/QualifiedAppId
                  {:system_id "de"
                   :app_id "123456789"}))
      (is (valid? apps/QualifiedAppId
                  {:system_id "de"
                   :app_id "app.with.dots"})))

    (testing "invalid qualified app id - missing required fields"
      (is (not (valid? apps/QualifiedAppId {})))
      (is (not (valid? apps/QualifiedAppId {:system_id "de"})))
      (is (not (valid? apps/QualifiedAppId {:app_id "app-id-12345"}))))

    (testing "invalid qualified app id - empty strings (min length is 1)"
      (is (not (valid? apps/QualifiedAppId
                       {:system_id ""
                        :app_id "app-id-12345"})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id "de"
                        :app_id ""})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id ""
                        :app_id ""}))))

    (testing "invalid qualified app id - invalid system_id type"
      (is (not (valid? apps/QualifiedAppId
                       {:system_id 123
                        :app_id "app-id-12345"})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id nil
                        :app_id "app-id-12345"})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id true
                        :app_id "app-id-12345"})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id :de
                        :app_id "app-id-12345"})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id []
                        :app_id "app-id-12345"})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id {}
                        :app_id "app-id-12345"}))))

    (testing "invalid qualified app id - invalid app_id type"
      (is (not (valid? apps/QualifiedAppId
                       {:system_id "de"
                        :app_id 123})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id "de"
                        :app_id nil})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id "de"
                        :app_id true})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id "de"
                        :app_id :app-id})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id "de"
                        :app_id []})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id "de"
                        :app_id {}})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id "de"
                        :app_id #uuid "123e4567-e89b-12d3-a456-426614174000"}))))

    (testing "invalid qualified app id - both fields invalid"
      (is (not (valid? apps/QualifiedAppId
                       {:system_id 123
                        :app_id 456})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id nil
                        :app_id nil})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id ""
                        :app_id ""}))))

    (testing "invalid qualified app id - extra fields not allowed (closed map)"
      (is (not (valid? apps/QualifiedAppId
                       {:system_id "de"
                        :app_id "app-id-12345"
                        :extra-field "value"})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id "de"
                        :app_id "app-id-12345"
                        :version "1.0"})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id "de"
                        :app_id "app-id-12345"
                        :name "My App"})))
      (is (not (valid? apps/QualifiedAppId
                       {:system_id "de"
                        :app_id "app-id-12345"
                        :unknown-key "unknown-value"}))))

    (testing "invalid qualified app id - not a map"
      (is (not (valid? apps/QualifiedAppId "string")))
      (is (not (valid? apps/QualifiedAppId 123)))
      (is (not (valid? apps/QualifiedAppId [])))
      (is (not (valid? apps/QualifiedAppId nil)))
      (is (not (valid? apps/QualifiedAppId true)))
      (is (not (valid? apps/QualifiedAppId :keyword))))))

(deftest test-AppDeletionRequest
  (testing "AppDeletionRequest validation"
    (testing "valid deletion request - single app"
      (is (valid? apps/AppDeletionRequest
                  {:app_ids [{:system_id "de"
                              :app_id "app-id-12345"}]}))
      (is (valid? apps/AppDeletionRequest
                  {:app_ids [{:system_id "agave"
                              :app_id "blast-2.2.30"}]}))
      (is (valid? apps/AppDeletionRequest
                  {:app_ids [{:system_id "osg"
                              :app_id "tool-abc-123"}]})))

    (testing "valid deletion request - multiple apps"
      (is (valid? apps/AppDeletionRequest
                  {:app_ids [{:system_id "de"
                              :app_id "app-1"}
                             {:system_id "de"
                              :app_id "app-2"}]}))
      (is (valid? apps/AppDeletionRequest
                  {:app_ids [{:system_id "de"
                              :app_id "app-1"}
                             {:system_id "agave"
                              :app_id "app-2"}
                             {:system_id "osg"
                              :app_id "app-3"}]}))
      (is (valid? apps/AppDeletionRequest
                  {:app_ids [{:system_id "system-1"
                              :app_id "app-alpha"}
                             {:system_id "system-2"
                              :app_id "app-beta"}
                             {:system_id "system-3"
                              :app_id "app-gamma"}
                             {:system_id "system-4"
                              :app_id "app-delta"}]})))

    (testing "valid deletion request - with root_deletion_request true"
      (is (valid? apps/AppDeletionRequest
                  {:app_ids [{:system_id "de"
                              :app_id "app-id-12345"}]
                   :root_deletion_request true}))
      (is (valid? apps/AppDeletionRequest
                  {:app_ids [{:system_id "de"
                              :app_id "app-1"}
                             {:system_id "de"
                              :app_id "app-2"}]
                   :root_deletion_request true})))

    (testing "valid deletion request - with root_deletion_request false"
      (is (valid? apps/AppDeletionRequest
                  {:app_ids [{:system_id "de"
                              :app_id "app-id-12345"}]
                   :root_deletion_request false}))
      (is (valid? apps/AppDeletionRequest
                  {:app_ids [{:system_id "agave"
                              :app_id "blast-tool"}
                             {:system_id "de"
                              :app_id "analysis-tool"}]
                   :root_deletion_request false})))

    (testing "valid deletion request - without optional root_deletion_request"
      (is (valid? apps/AppDeletionRequest
                  {:app_ids [{:system_id "de"
                              :app_id "app-id-12345"}]}))
      (is (valid? apps/AppDeletionRequest
                  {:app_ids [{:system_id "de"
                              :app_id "app-1"}
                             {:system_id "de"
                              :app_id "app-2"}
                             {:system_id "de"
                              :app_id "app-3"}]})))

    (testing "valid deletion request - empty app_ids vector"
      (is (valid? apps/AppDeletionRequest
                  {:app_ids []}))
      (is (valid? apps/AppDeletionRequest
                  {:app_ids []
                   :root_deletion_request false})))

    (testing "invalid deletion request - missing required app_ids field"
      (is (not (valid? apps/AppDeletionRequest {})))
      (is (not (valid? apps/AppDeletionRequest {:root_deletion_request true})))
      (is (not (valid? apps/AppDeletionRequest {:root_deletion_request false}))))

    (testing "invalid deletion request - app_ids not a vector"
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids "not-a-vector"})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids {:system_id "de"
                                  :app_id "app-id-12345"}})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids 123})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids nil})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids true})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids :keyword}))))

    (testing "invalid deletion request - app_ids contains invalid QualifiedAppId"
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"}]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:app_id "app-id-12345"}]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{}]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id ""
                                   :app_id "app-id-12345"}]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id ""}]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id 123
                                   :app_id "app-id-12345"}]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id 456}]}))))

    (testing "invalid deletion request - app_ids contains mix of valid and invalid QualifiedAppId"
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-1"}
                                  {:system_id "de"}]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-1"}
                                  {:app_id "app-2"}]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-1"}
                                  {}]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-1"}
                                  "not-a-map"]}))))

    (testing "invalid deletion request - app_ids contains non-map elements"
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids ["string"]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [123]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [nil]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [true]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [:keyword]})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [[]]}))))

    (testing "invalid deletion request - root_deletion_request not a boolean"
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-id-12345"}]
                        :root_deletion_request "true"})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-id-12345"}]
                        :root_deletion_request 1})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-id-12345"}]
                        :root_deletion_request nil})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-id-12345"}]
                        :root_deletion_request :true})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-id-12345"}]
                        :root_deletion_request []})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-id-12345"}]
                        :root_deletion_request {}}))))

    (testing "invalid deletion request - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-id-12345"}]
                        :extra-field "value"})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-id-12345"}]
                        :root_deletion_request true
                        :extra-field "value"})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-id-12345"}]
                        :user-id "jsmith"})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-id-12345"}]
                        :delete-all true})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids [{:system_id "de"
                                   :app_id "app-id-12345"}]
                        :unknown-key "unknown-value"}))))

    (testing "invalid deletion request - not a map"
      (is (not (valid? apps/AppDeletionRequest "string")))
      (is (not (valid? apps/AppDeletionRequest 123)))
      (is (not (valid? apps/AppDeletionRequest [])))
      (is (not (valid? apps/AppDeletionRequest nil)))
      (is (not (valid? apps/AppDeletionRequest true)))
      (is (not (valid? apps/AppDeletionRequest :keyword))))

    (testing "invalid deletion request - both fields invalid"
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids "not-a-vector"
                        :root_deletion_request "not-a-boolean"})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids 123
                        :root_deletion_request nil})))
      (is (not (valid? apps/AppDeletionRequest
                       {:app_ids nil
                        :root_deletion_request 123}))))))

(deftest test-AppPreviewRequest
  (testing "AppPreviewRequest validation"
    (testing "valid preview request - minimal (empty map, all fields optional)"
      (is (valid? apps/AppPreviewRequest {})))

    (testing "valid preview request - with id only"
      (is (valid? apps/AppPreviewRequest
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"})))

    (testing "valid preview request - with basic required fields from original App"
      (is (valid? apps/AppPreviewRequest
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool"})))

    (testing "valid preview request - with optional fields"
      (is (valid? apps/AppPreviewRequest
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                   :system_id "de"})))

    (testing "valid preview request - with versions"
      (is (valid? apps/AppPreviewRequest
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :versions [{:version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                               :version "1.0.0"}]})))

    (testing "valid preview request - with groups"
      (is (valid? apps/AppPreviewRequest
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :groups [{:label "Input Parameters"}]})))

    (testing "valid preview request - with is_public"
      (is (valid? apps/AppPreviewRequest
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :is_public true}))
      (is (valid? apps/AppPreviewRequest
                  {:is_public false})))

    (testing "valid preview request - with tools"
      (is (valid? apps/AppPreviewRequest
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :tools [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                            :name "blastn"
                            :version "2.12.0"
                            :type "executable"}]})))

    (testing "valid preview request - comprehensive with all optional fields"
      (is (valid? apps/AppPreviewRequest
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                   :system_id "de"
                   :versions [{:version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                               :version "1.0.0"}]
                   :groups [{:label "Input Parameters"
                             :parameters []}]
                   :is_public true
                   :tools [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                            :name "blastn"
                            :version "2.12.0"
                            :type "executable"}]
                   :references ["https://doi.org/10.1093/nar/gkv416"]})))

    (testing "valid preview request - with multiple tools"
      (is (valid? apps/AppPreviewRequest
                  {:tools [{:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                            :name "blastn"
                            :version "2.12.0"
                            :type "executable"}
                           {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                            :name "blastx"
                            :version "2.12.0"
                            :type "executable"}]})))

    (testing "valid preview request - with multiple groups"
      (is (valid? apps/AppPreviewRequest
                  {:groups [{:label "Input Parameters"}
                            {:label "Output Parameters"}]})))

    (testing "invalid preview request - wrong id type (must be UUID)"
      (is (not (valid? apps/AppPreviewRequest
                       {:id "not-a-uuid"})))
      (is (not (valid? apps/AppPreviewRequest
                       {:id 123})))
      (is (not (valid? apps/AppPreviewRequest
                       {:id "123e4567-e89b-12d3-a456-426614174000"}))))

    (testing "invalid preview request - wrong name type"
      (is (not (valid? apps/AppPreviewRequest
                       {:name 123})))
      (is (not (valid? apps/AppPreviewRequest
                       {:name true})))
      (is (not (valid? apps/AppPreviewRequest
                       {:name nil}))))

    (testing "invalid preview request - wrong description type"
      (is (not (valid? apps/AppPreviewRequest
                       {:description 123})))
      (is (not (valid? apps/AppPreviewRequest
                       {:description true})))
      (is (not (valid? apps/AppPreviewRequest
                       {:description nil}))))

    (testing "invalid preview request - wrong versions type (must be vector)"
      (is (not (valid? apps/AppPreviewRequest
                       {:versions "not-a-vector"})))
      (is (not (valid? apps/AppPreviewRequest
                       {:versions {:version "1.0.0"}})))
      (is (not (valid? apps/AppPreviewRequest
                       {:versions nil}))))

    (testing "invalid preview request - wrong groups type (must be vector)"
      (is (not (valid? apps/AppPreviewRequest
                       {:groups "not-a-vector"})))
      (is (not (valid? apps/AppPreviewRequest
                       {:groups {:label "Input Parameters"}})))
      (is (not (valid? apps/AppPreviewRequest
                       {:groups nil}))))

    (testing "invalid preview request - wrong is_public type (must be boolean)"
      (is (not (valid? apps/AppPreviewRequest
                       {:is_public "true"})))
      (is (not (valid? apps/AppPreviewRequest
                       {:is_public 1})))
      (is (not (valid? apps/AppPreviewRequest
                       {:is_public nil}))))

    (testing "invalid preview request - wrong tools type (must be vector)"
      (is (not (valid? apps/AppPreviewRequest
                       {:tools "not-a-vector"})))
      (is (not (valid? apps/AppPreviewRequest
                       {:tools {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"}})))
      (is (not (valid? apps/AppPreviewRequest
                       {:tools nil}))))

    (testing "invalid preview request - invalid tool in vector (missing required fields)"
      (is (not (valid? apps/AppPreviewRequest
                       {:tools [{}]})))
      (is (not (valid? apps/AppPreviewRequest
                       {:tools [{:name "blastn"}]}))))

    (testing "invalid preview request - invalid group in vector (missing required label)"
      (is (not (valid? apps/AppPreviewRequest
                       {:groups [{}]})))
      (is (not (valid? apps/AppPreviewRequest
                       {:groups [{:name "input_params"}]}))))

    (testing "invalid preview request - wrong integration_date type"
      (is (not (valid? apps/AppPreviewRequest
                       {:integration_date "2024-01-15"})))
      (is (not (valid? apps/AppPreviewRequest
                       {:integration_date 1705314600000}))))

    (testing "invalid preview request - wrong edited_date type"
      (is (not (valid? apps/AppPreviewRequest
                       {:edited_date "2024-10-20"})))
      (is (not (valid? apps/AppPreviewRequest
                       {:edited_date 1729426500000}))))

    (testing "invalid preview request - wrong system_id type"
      (is (not (valid? apps/AppPreviewRequest
                       {:system_id 123})))
      (is (not (valid? apps/AppPreviewRequest
                       {:system_id :de})))
      (is (not (valid? apps/AppPreviewRequest
                       {:system_id nil}))))

    (testing "invalid preview request - wrong references type (must be vector of strings)"
      (is (not (valid? apps/AppPreviewRequest
                       {:references "not-a-vector"})))
      (is (not (valid? apps/AppPreviewRequest
                       {:references [123]})))
      (is (not (valid? apps/AppPreviewRequest
                       {:references [nil]}))))

    (testing "invalid preview request - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppPreviewRequest
                       {:extra-field "value"})))
      (is (not (valid? apps/AppPreviewRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "BLAST"
                        :unknown-key "value"})))
      (is (not (valid? apps/AppPreviewRequest
                       {:groups []
                        :extra "field"}))))

    (testing "invalid preview request - not a map"
      (is (not (valid? apps/AppPreviewRequest "string")))
      (is (not (valid? apps/AppPreviewRequest 123)))
      (is (not (valid? apps/AppPreviewRequest [])))
      (is (not (valid? apps/AppPreviewRequest nil)))
      (is (not (valid? apps/AppPreviewRequest true))))))

(deftest test-PublishAppRequest
  (testing "PublishAppRequest validation"
    (testing "minimal valid request - empty map"
      (is (valid? apps/PublishAppRequest {})))

    (testing "valid request with all optional fields"
      (is (valid? apps/PublishAppRequest
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "BLAST"
                   :description "Basic Local Alignment Search Tool for sequence comparison"
                   :integration_date #inst "2024-01-15T10:30:00.000-00:00"
                   :edited_date #inst "2024-10-20T14:45:00.000-00:00"
                   :system_id "de"
                   :version "1.2.0"
                   :version_id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :documentation "This tool performs sequence alignment using BLAST algorithm."
                   :references ["https://doi.org/10.1093/nar/gkv416" "PMID: 25916842"]
                   :avus [{:attr "analysis_type"
                           :value "sequence_alignment"
                           :unit "category"}]})))

    (testing "valid request with partial fields"
      (is (valid? apps/PublishAppRequest
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "BLAST"}))
      (is (valid? apps/PublishAppRequest
                  {:version "2.0.0"
                   :documentation "Updated documentation"}))
      (is (valid? apps/PublishAppRequest
                  {:references ["https://example.com"]
                   :system_id "de"})))

    (testing "valid request with simple AVUs"
      (is (valid? apps/PublishAppRequest
                  {:avus [{:attr "author"
                           :value "John Doe"
                           :unit "text"}
                          {:attr "license"
                           :value "MIT"
                           :unit "string"}]})))

    (testing "valid request with AVUs containing optional fields"
      (is (valid? apps/PublishAppRequest
                  {:avus [{:id #uuid "70fc1080-3152-4c09-92b0-f5b9cc70088b"
                           :attr "attribute-name"
                           :value "attribute-value"
                           :unit "attribute-unit"
                           :target_id #uuid "a14dfe49-f65f-418b-b3c5-6497284251fe"
                           :created_by "user123"
                           :modified_by "user321"
                           :created_on 1757465246000
                           :modified_on 1757465251000}]})))

    (testing "valid request with nested AVUs - single level"
      (is (valid? apps/PublishAppRequest
                  {:avus [{:attr "parent"
                           :value "parent_value"
                           :unit "text"
                           :avus [{:attr "child"
                                   :value "child_value"
                                   :unit "text"}]}]})))

    (testing "valid request with nested AVUs - multiple levels"
      (is (valid? apps/PublishAppRequest
                  {:avus [{:attr "level1"
                           :value "value1"
                           :unit "text"
                           :avus [{:attr "level2"
                                   :value "value2"
                                   :unit "text"
                                   :avus [{:attr "level3"
                                           :value "value3"
                                           :unit "text"
                                           :avus [{:attr "level4"
                                                   :value "value4"
                                                   :unit "text"}]}]}]}]})))

    (testing "valid request with multiple nested AVUs at same level"
      (is (valid? apps/PublishAppRequest
                  {:avus [{:attr "parent1"
                           :value "value1"
                           :unit "text"
                           :avus [{:attr "child1a"
                                   :value "value1a"
                                   :unit "text"}
                                  {:attr "child1b"
                                   :value "value1b"
                                   :unit "text"}]}
                          {:attr "parent2"
                           :value "value2"
                           :unit "text"
                           :avus [{:attr "child2a"
                                   :value "value2a"
                                   :unit "text"}]}]})))

    (testing "valid request with empty references vector"
      (is (valid? apps/PublishAppRequest {:references []})))

    (testing "valid request with empty avus vector"
      (is (valid? apps/PublishAppRequest {:avus []})))

    (testing "invalid request - wrong type for id"
      (is (not (valid? apps/PublishAppRequest
                       {:id "not-a-uuid"})))
      (is (not (valid? apps/PublishAppRequest
                       {:id 12345}))))

    (testing "invalid request - wrong type for name"
      (is (not (valid? apps/PublishAppRequest
                       {:name 123})))
      (is (not (valid? apps/PublishAppRequest
                       {:name true}))))

    (testing "invalid request - wrong type for description"
      (is (not (valid? apps/PublishAppRequest
                       {:description 456})))
      (is (not (valid? apps/PublishAppRequest
                       {:description []}))))

    (testing "invalid request - wrong type for integration_date"
      (is (not (valid? apps/PublishAppRequest
                       {:integration_date "2024-01-15"})))
      (is (not (valid? apps/PublishAppRequest
                       {:integration_date 1234567890}))))

    (testing "invalid request - wrong type for edited_date"
      (is (not (valid? apps/PublishAppRequest
                       {:edited_date "2024-10-20"})))
      (is (not (valid? apps/PublishAppRequest
                       {:edited_date 9876543210}))))

    (testing "invalid request - wrong type for system_id"
      (is (not (valid? apps/PublishAppRequest
                       {:system_id 123})))
      (is (not (valid? apps/PublishAppRequest
                       {:system_id true}))))

    (testing "invalid request - empty system_id"
      (is (not (valid? apps/PublishAppRequest
                       {:system_id ""}))))

    (testing "invalid request - wrong type for version"
      (is (not (valid? apps/PublishAppRequest
                       {:version 1.2})))
      (is (not (valid? apps/PublishAppRequest
                       {:version false}))))

    (testing "invalid request - wrong type for version_id"
      (is (not (valid? apps/PublishAppRequest
                       {:version_id "not-a-uuid"})))
      (is (not (valid? apps/PublishAppRequest
                       {:version_id 789}))))

    (testing "invalid request - wrong type for documentation"
      (is (not (valid? apps/PublishAppRequest
                       {:documentation 999})))
      (is (not (valid? apps/PublishAppRequest
                       {:documentation {}}))))

    (testing "invalid request - references not a vector"
      (is (not (valid? apps/PublishAppRequest
                       {:references "string"})))
      (is (not (valid? apps/PublishAppRequest
                       {:references {:key "value"}}))))

    (testing "invalid request - references vector contains non-strings"
      (is (not (valid? apps/PublishAppRequest
                       {:references [123]})))
      (is (not (valid? apps/PublishAppRequest
                       {:references ["valid" 456 "also-valid"]})))
      (is (not (valid? apps/PublishAppRequest
                       {:references [true false]}))))

    (testing "invalid request - avus not a vector"
      (is (not (valid? apps/PublishAppRequest
                       {:avus "string"})))
      (is (not (valid? apps/PublishAppRequest
                       {:avus {:attr "value"}}))))

    (testing "invalid request - AVU missing required field attr"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:value "value"
                                :unit "text"}]}))))

    (testing "invalid request - AVU missing required field value"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr "attribute"
                                :unit "text"}]}))))

    (testing "invalid request - AVU missing required field unit"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr "attribute"
                                :value "value"}]}))))

    (testing "invalid request - AVU with wrong type for attr"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr 123
                                :value "value"
                                :unit "text"}]}))))

    (testing "invalid request - AVU with wrong type for value"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr "attribute"
                                :value 456
                                :unit "text"}]}))))

    (testing "invalid request - AVU with wrong type for unit"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr "attribute"
                                :value "value"
                                :unit 789}]}))))

    (testing "invalid request - AVU with wrong type for optional id"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:id "not-a-uuid"
                                :attr "attribute"
                                :value "value"
                                :unit "text"}]}))))

    (testing "invalid request - AVU with wrong type for optional target_id"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr "attribute"
                                :value "value"
                                :unit "text"
                                :target_id "not-a-uuid"}]}))))

    (testing "invalid request - AVU with wrong type for created_by"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr "attribute"
                                :value "value"
                                :unit "text"
                                :created_by 123}]}))))

    (testing "invalid request - AVU with wrong type for modified_by"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr "attribute"
                                :value "value"
                                :unit "text"
                                :modified_by true}]}))))

    (testing "invalid request - AVU with wrong type for created_on"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr "attribute"
                                :value "value"
                                :unit "text"
                                :created_on "not-an-int"}]}))))

    (testing "invalid request - AVU with wrong type for modified_on"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr "attribute"
                                :value "value"
                                :unit "text"
                                :modified_on "not-an-int"}]}))))

    (testing "invalid request - AVU with extra field (closed map)"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr "attribute"
                                :value "value"
                                :unit "text"
                                :extra_field "not-allowed"}]}))))

    (testing "invalid request - nested AVU missing required fields"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr "parent"
                                :value "parent_value"
                                :unit "text"
                                :avus [{:attr "child"
                                        :value "child_value"}]}]}))))

    (testing "invalid request - nested AVU with wrong type"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr "parent"
                                :value "parent_value"
                                :unit "text"
                                :avus [{:attr 123
                                        :value "child_value"
                                        :unit "text"}]}]}))))

    (testing "invalid request - nested avus not a vector"
      (is (not (valid? apps/PublishAppRequest
                       {:avus [{:attr "parent"
                                :value "parent_value"
                                :unit "text"
                                :avus "not-a-vector"}]}))))

    (testing "invalid request - extra field at top level (closed map)"
      (is (not (valid? apps/PublishAppRequest
                       {:extra_field "not-allowed"})))
      (is (not (valid? apps/PublishAppRequest
                       {:name "BLAST"
                        :unknown_key "value"}))))

    (testing "invalid request - not a map"
      (is (not (valid? apps/PublishAppRequest "string")))
      (is (not (valid? apps/PublishAppRequest 123)))
      (is (not (valid? apps/PublishAppRequest [])))
      (is (not (valid? apps/PublishAppRequest nil)))
      (is (not (valid? apps/PublishAppRequest true))))))

(deftest test-AppPublishableResponse
  (testing "AppPublishableResponse validation"
    (testing "valid response - app is publishable without reason"
      (is (valid? apps/AppPublishableResponse
                  {:publishable true})))

    (testing "valid response - app is not publishable with reason"
      (is (valid? apps/AppPublishableResponse
                  {:publishable false
                   :reason "HPC apps must be published using the TAPIS API."}))
      (is (valid? apps/AppPublishableResponse
                  {:publishable false
                   :reason "The app is missing required parameters."})))

    (testing "valid response - app is not publishable without reason (reason is optional)"
      (is (valid? apps/AppPublishableResponse
                  {:publishable false})))

    (testing "valid response - app is publishable with reason (semantically odd but valid)"
      (is (valid? apps/AppPublishableResponse
                  {:publishable true
                   :reason "Some informational message"})))

    (testing "invalid response - missing required publishable field"
      (is (not (valid? apps/AppPublishableResponse {})))
      (is (not (valid? apps/AppPublishableResponse
                       {:reason "Missing publishable field"}))))

    (testing "invalid response - wrong type for publishable field"
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable "true"})))
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable 1})))
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable 0})))
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable nil})))
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable "false"})))
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable :true}))))

    (testing "invalid response - wrong type for reason field"
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable false
                        :reason true})))
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable false
                        :reason 123})))
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable false
                        :reason :keyword})))
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable false
                        :reason []})))
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable false
                        :reason {}}))))

    (testing "invalid response - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable true
                        :extra-field "value"})))
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable false
                        :reason "Not publishable"
                        :unknown-key "value"})))
      (is (not (valid? apps/AppPublishableResponse
                       {:publishable true
                        :additional "field"}))))

    (testing "invalid response - not a map"
      (is (not (valid? apps/AppPublishableResponse "string")))
      (is (not (valid? apps/AppPublishableResponse 123)))
      (is (not (valid? apps/AppPublishableResponse [])))
      (is (not (valid? apps/AppPublishableResponse nil)))
      (is (not (valid? apps/AppPublishableResponse true))))))
