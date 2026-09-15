(ns common-swagger-api.malli.subjects-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.subjects :as subjects]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def base-subject {:id "user123" :source_id "ldap"})

(def subject
  (assoc base-subject
         :name             "John Doe"
         :first_name       "John"
         :last_name        "Doe"
         :email            "john.doe@example.com"
         :institution      "University of Example"
         :attribute_values ["value1"]
         :description      "A researcher"))

(deftest BaseSubject
  (valid subjects/BaseSubject base-subject)
  (invalid subjects/BaseSubject
           {}
           (dissoc base-subject :source_id)
           (assoc base-subject :id 123)
           (assoc base-subject :extra 1)))

(deftest Subject
  (valid subjects/Subject base-subject subject)
  (invalid subjects/Subject
           {}
           (dissoc subject :id)
           (assoc subject :attribute_values "value1")
           (assoc subject :email 1)
           (assoc subject :extra 1)))

(deftest SubjectList
  (valid subjects/SubjectList {:subjects []} {:subjects [subject]})
  (invalid subjects/SubjectList {} {:subjects [{}]} {:subjects subject} {:subjects [] :extra 1}))

(deftest SubjectIdList
  (valid subjects/SubjectIdList {:subject_ids []} {:subject_ids ["user123" "user456"]})
  (invalid subjects/SubjectIdList {} {:subject_ids "user123"} {:subject_ids [1]} {:subject_ids [] :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.subjects))
