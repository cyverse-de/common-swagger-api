(ns common-swagger-api.malli.subjects-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.subjects :as subjects]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-BaseSubject
  (testing "BaseSubject validation"
    (testing "valid base subject"
      (is (valid? subjects/BaseSubject
                  {:id "user123"
                   :source_id "ldap"}))
      (is (valid? subjects/BaseSubject
                  {:id "admin"
                   :source_id "local"}))
      (is (valid? subjects/BaseSubject
                  {:id "service-account"
                   :source_id "oauth"})))

    (testing "invalid base subject"
      ;; Missing required fields
      (is (not (valid? subjects/BaseSubject
                       {:id "user123"})))
      (is (not (valid? subjects/BaseSubject
                       {:source_id "ldap"})))
      (is (not (valid? subjects/BaseSubject {})))

      ;; Empty strings are allowed (plain :string, not NonBlankString)
      (is (valid? subjects/BaseSubject
                  {:id ""
                   :source_id "ldap"}))
      (is (valid? subjects/BaseSubject
                  {:id "user123"
                   :source_id ""}))
      (is (valid? subjects/BaseSubject
                  {:id "   "
                   :source_id "ldap"}))

      ;; Wrong types
      (is (not (valid? subjects/BaseSubject
                       {:id 123
                        :source_id "ldap"})))
      (is (not (valid? subjects/BaseSubject
                       {:id "user123"
                        :source_id 456})))

      ;; Extra fields not allowed due to :closed true
      (is (not (valid? subjects/BaseSubject
                       {:id "user123"
                        :source_id "ldap"
                        :extra "field"}))))))

(deftest test-Subject
  (testing "Subject validation"
    (testing "valid subject"
      ;; Minimal valid subject (only required fields from BaseSubject)
      (is (valid? subjects/Subject
                  {:id "user123"
                   :source_id "ldap"}))

      ;; Subject with some optional fields
      (is (valid? subjects/Subject
                  {:id "user123"
                   :source_id "ldap"
                   :name "John Doe"}))

      ;; Subject with all fields
      (is (valid? subjects/Subject
                  {:id "user123"
                   :source_id "ldap"
                   :name "John Doe"
                   :first_name "John"
                   :last_name "Doe"
                   :email "john.doe@example.com"
                   :institution "University of Example"
                   :attribute_values ["attr1" "attr2"]
                   :description "A researcher"}))

      ;; Subject with empty attribute_values vector
      (is (valid? subjects/Subject
                  {:id "user123"
                   :source_id "ldap"
                   :attribute_values []})))

    (testing "invalid subject"
      ;; Missing required fields from BaseSubject
      (is (not (valid? subjects/Subject
                       {:name "John Doe"})))
      (is (not (valid? subjects/Subject
                       {:id "user123"})))

      ;; Empty strings are allowed for string fields
      (is (valid? subjects/Subject
                  {:id "user123"
                   :source_id "ldap"
                   :name ""}))
      (is (valid? subjects/Subject
                  {:id "user123"
                   :source_id "ldap"
                   :email ""}))

      ;; Wrong types for optional fields
      (is (not (valid? subjects/Subject
                       {:id "user123"
                        :source_id "ldap"
                        :name 123})))
      (is (not (valid? subjects/Subject
                       {:id "user123"
                        :source_id "ldap"
                        :attribute_values "not-a-vector"})))
      (is (not (valid? subjects/Subject
                       {:id "user123"
                        :source_id "ldap"
                        :attribute_values ["valid" 123]})))  ; Non-string in vector

      ;; Extra fields not allowed
      (is (not (valid? subjects/Subject
                       {:id "user123"
                        :source_id "ldap"
                        :name "John Doe"
                        :extra "field"}))))))

(deftest test-SubjectList
  (testing "SubjectList validation"
    (testing "valid subject list"
      ;; Empty list
      (is (valid? subjects/SubjectList
                  {:subjects []}))

      ;; List with one subject
      (is (valid? subjects/SubjectList
                  {:subjects [{:id "user123"
                               :source_id "ldap"}]}))

      ;; List with multiple subjects
      (is (valid? subjects/SubjectList
                  {:subjects [{:id "user123"
                               :source_id "ldap"
                               :name "John Doe"}
                              {:id "user456"
                               :source_id "ldap"
                               :name "Jane Smith"
                               :email "jane@example.com"}
                              {:id "admin"
                               :source_id "local"}]})))

    (testing "invalid subject list"
      ;; Missing required field
      (is (not (valid? subjects/SubjectList {})))

      ;; Wrong type for subjects field
      (is (not (valid? subjects/SubjectList
                       {:subjects "not-a-vector"})))

      ;; Invalid subjects in the vector
      (is (not (valid? subjects/SubjectList
                       {:subjects [{:id "user123"}]})))  ; Missing source_id
      (is (not (valid? subjects/SubjectList
                       {:subjects [{:id "user123"
                                    :source_id "ldap"
                                    :invalid_field "value"}]})))  ; Invalid field

      ;; Extra fields not allowed
      (is (not (valid? subjects/SubjectList
                       {:subjects []
                        :extra "field"}))))))

(deftest test-SubjectIdList
  (testing "SubjectIdList validation"
    (testing "valid subject id list"
      ;; Empty list
      (is (valid? subjects/SubjectIdList
                  {:subject_ids []}))

      ;; List with one id
      (is (valid? subjects/SubjectIdList
                  {:subject_ids ["user123"]}))

      ;; List with multiple ids
      (is (valid? subjects/SubjectIdList
                  {:subject_ids ["user123" "user456" "admin"]})))

    (testing "invalid subject id list"
      ;; Missing required field
      (is (not (valid? subjects/SubjectIdList {})))

      ;; Wrong type for subject_ids field
      (is (not (valid? subjects/SubjectIdList
                       {:subject_ids "not-a-vector"})))

      ;; Non-string values in vector
      (is (not (valid? subjects/SubjectIdList
                       {:subject_ids ["user123" 456]})))
      (is (not (valid? subjects/SubjectIdList
                       {:subject_ids [123 "user456"]})))

      ;; Extra fields not allowed
      (is (not (valid? subjects/SubjectIdList
                       {:subject_ids ["user123"]
                        :extra "field"}))))))

(deftest test-schema-inheritance
  (testing "Schema inheritance and composition"
    ;; Subject should accept all BaseSubject fields plus optional ones
    (let [base-data {:id "user123"
                     :source_id "ldap"}
          subject-data (assoc base-data
                              :name "John Doe"
                              :email "john@example.com")]

      (is (valid? subjects/BaseSubject base-data))
      (is (valid? subjects/Subject base-data))  ; Should work with just base fields
      (is (valid? subjects/Subject subject-data))

      ;; But BaseSubject should not accept extra fields
      (is (not (valid? subjects/BaseSubject subject-data)))))

(deftest test-attribute-values-vector
  (testing "attribute_values field vector validation"
    (is (valid? subjects/Subject
                {:id "user123"
                 :source_id "ldap"
                 :attribute_values ["value1" "value2" "value3"]}))

    (is (valid? subjects/Subject
                {:id "user123"
                 :source_id "ldap"
                 :attribute_values []}))

    ;; Mixed types not allowed
    (is (not (valid? subjects/Subject
                     {:id "user123"
                      :source_id "ldap"
                      :attribute_values ["string" 123 true]})))

    ;; Non-vector not allowed
    (is (not (valid? subjects/Subject
                     {:id "user123"
                      :source_id "ldap"
                      :attribute_values "not-a-vector"}))))))
