(ns common-swagger-api.malli.ontologies-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.ontologies :as ont]
            [malli.core :as malli])
  (:import [java.util Date]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-OntologyVersionParam
  (testing "OntologyVersionParam validation"
    (is (valid? ont/OntologyVersionParam "1.0.0"))
    (is (valid? ont/OntologyVersionParam "v2.3.1"))
    (is (valid? ont/OntologyVersionParam "2024-01-15"))
    (is (valid? ont/OntologyVersionParam ""))
    (is (not (valid? ont/OntologyVersionParam 123)))
    (is (not (valid? ont/OntologyVersionParam nil)))))

(deftest test-OntologyClassIRIParam
  (testing "OntologyClassIRIParam validation"
    (is (valid? ont/OntologyClassIRIParam "http://example.org/ontology#Class1"))
    (is (valid? ont/OntologyClassIRIParam "https://purl.obolibrary.org/obo/GO_0008150"))
    (is (valid? ont/OntologyClassIRIParam "urn:example:class"))
    (is (valid? ont/OntologyClassIRIParam ""))
    (is (not (valid? ont/OntologyClassIRIParam 123)))
    (is (not (valid? ont/OntologyClassIRIParam nil)))))

(deftest test-OntologyHierarchyFilterParams
  (testing "OntologyHierarchyFilterParams validation"
    (testing "valid filter params"
      (is (valid? ont/OntologyHierarchyFilterParams
                  {:attr "metadata_attr"}))
      (is (valid? ont/OntologyHierarchyFilterParams
                  {:attr "ontology_class_iri"}))
      (is (valid? ont/OntologyHierarchyFilterParams
                  {:attr ""})))

    (testing "invalid filter params"
      ;; Missing required field
      (is (not (valid? ont/OntologyHierarchyFilterParams {})))

      ;; Wrong field name
      (is (not (valid? ont/OntologyHierarchyFilterParams
                       {:attribute "metadata_attr"})))

      ;; Wrong type
      (is (not (valid? ont/OntologyHierarchyFilterParams
                       {:attr 123})))
      (is (not (valid? ont/OntologyHierarchyFilterParams
                       {:attr nil})))

      ;; Extra fields not allowed
      (is (not (valid? ont/OntologyHierarchyFilterParams
                       {:attr "metadata_attr"
                        :extra "field"}))))))

(deftest test-OntologyDetails
  (testing "OntologyDetails validation"
    (let [valid-date (Date.)]
      (testing "valid ontology details"
        (is (valid? ont/OntologyDetails
                    {:iri "http://example.org/ontology"
                     :version "1.0.0"
                     :created_by "user123"
                     :created_on valid-date}))

        ;; IRI can be nil (maybe type)
        (is (valid? ont/OntologyDetails
                    {:iri nil
                     :version "2.0.0"
                     :created_by "admin"
                     :created_on valid-date}))

        ;; Empty created_by not allowed (NonBlankString)
        (is (not (valid? ont/OntologyDetails
                         {:iri "http://example.org/ontology"
                          :version "1.0.0"
                          :created_by ""
                          :created_on valid-date})))

        (is (not (valid? ont/OntologyDetails
                         {:iri "http://example.org/ontology"
                          :version "1.0.0"
                          :created_by "   "
                          :created_on valid-date}))))

      (testing "invalid ontology details"
        ;; Missing required fields
        (is (not (valid? ont/OntologyDetails
                         {:iri "http://example.org/ontology"
                          :version "1.0.0"
                          :created_by "user123"})))

        (is (not (valid? ont/OntologyDetails
                         {:iri "http://example.org/ontology"
                          :version "1.0.0"
                          :created_on valid-date})))

        (is (not (valid? ont/OntologyDetails
                         {:iri "http://example.org/ontology"
                          :created_by "user123"
                          :created_on valid-date})))

        ;; Wrong types
        (is (not (valid? ont/OntologyDetails
                         {:iri 123
                          :version "1.0.0"
                          :created_by "user123"
                          :created_on valid-date})))

        (is (not (valid? ont/OntologyDetails
                         {:iri "http://example.org/ontology"
                          :version "1.0.0"
                          :created_by "user123"
                          :created_on "2024-01-01"})))  ; String instead of Date

        ;; Extra fields not allowed
        (is (not (valid? ont/OntologyDetails
                         {:iri "http://example.org/ontology"
                          :version "1.0.0"
                          :created_by "user123"
                          :created_on valid-date
                          :extra "field"})))))))

(deftest test-OntologyClass
  (testing "OntologyClass validation"
    (testing "valid ontology class"
      ;; Minimal required fields
      (is (valid? ont/OntologyClass
                  {:iri "http://example.org/ontology#Class1"
                   :label "Class One"}))

      ;; Label can be nil
      (is (valid? ont/OntologyClass
                  {:iri "http://example.org/ontology#Class1"
                   :label nil}))

      ;; With optional description
      (is (valid? ont/OntologyClass
                  {:iri "http://example.org/ontology#Class1"
                   :label "Class One"
                   :description "A test ontology class"}))

      ;; Description can be nil
      (is (valid? ont/OntologyClass
                  {:iri "http://example.org/ontology#Class1"
                   :label "Class One"
                   :description nil})))

    (testing "invalid ontology class"
      ;; Missing required fields
      (is (not (valid? ont/OntologyClass
                       {:label "Class One"})))

      (is (not (valid? ont/OntologyClass
                       {:iri "http://example.org/ontology#Class1"})))

      ;; Wrong types
      (is (not (valid? ont/OntologyClass
                       {:iri 123
                        :label "Class One"})))

      (is (not (valid? ont/OntologyClass
                       {:iri "http://example.org/ontology#Class1"
                        :label 456})))

      ;; Extra fields not allowed
      (is (not (valid? ont/OntologyClass
                       {:iri "http://example.org/ontology#Class1"
                        :label "Class One"
                        :extra "field"}))))))

(deftest test-OntologyClassHierarchy
  (testing "OntologyClassHierarchy validation"
    (testing "valid hierarchy"
      ;; Simple class without subclasses
      (is (valid? ont/OntologyClassHierarchy
                  {:iri "http://example.org/ontology#Root"
                   :label "Root Class"}))

      ;; Class with empty subclasses
      (is (valid? ont/OntologyClassHierarchy
                  {:iri "http://example.org/ontology#Root"
                   :label "Root Class"
                   :subclasses []}))

      ;; Class with one subclass
      (is (valid? ont/OntologyClassHierarchy
                  {:iri "http://example.org/ontology#Root"
                   :label "Root Class"
                   :subclasses [{:iri "http://example.org/ontology#Child1"
                                 :label "Child Class 1"}]}))

      ;; Nested hierarchy
      (is (valid? ont/OntologyClassHierarchy
                  {:iri "http://example.org/ontology#Root"
                   :label "Root Class"
                   :description "The root of the hierarchy"
                   :subclasses [{:iri "http://example.org/ontology#Child1"
                                 :label "Child Class 1"
                                 :subclasses [{:iri "http://example.org/ontology#Grandchild1"
                                               :label "Grandchild 1"}]}
                                {:iri "http://example.org/ontology#Child2"
                                 :label "Child Class 2"
                                 :description "Second child"
                                 :subclasses []}]}))

      ;; Deep nesting
      (is (valid? ont/OntologyClassHierarchy
                  {:iri "http://example.org/ontology#Level1"
                   :label "Level 1"
                   :subclasses [{:iri "http://example.org/ontology#Level2"
                                 :label "Level 2"
                                 :subclasses [{:iri "http://example.org/ontology#Level3"
                                               :label "Level 3"
                                               :subclasses [{:iri "http://example.org/ontology#Level4"
                                                             :label "Level 4"
                                                             :subclasses [{:iri "http://example.org/ontology#Level5"
                                                                           :label "Level 5"}]}]}]}]})))

    (testing "invalid hierarchy"
      ;; Missing required fields
      (is (not (valid? ont/OntologyClassHierarchy
                       {:label "Root Class"})))

      ;; Invalid subclass structure
      (is (not (valid? ont/OntologyClassHierarchy
                       {:iri "http://example.org/ontology#Root"
                        :label "Root Class"
                        :subclasses [{:label "Missing IRI"}]})))

      ;; Wrong type for subclasses
      (is (not (valid? ont/OntologyClassHierarchy
                       {:iri "http://example.org/ontology#Root"
                        :label "Root Class"
                        :subclasses "not-a-vector"})))

      ;; Extra fields not allowed
      (is (not (valid? ont/OntologyClassHierarchy
                       {:iri "http://example.org/ontology#Root"
                        :label "Root Class"
                        :extra "field"})))

      ;; Extra fields in subclasses not allowed
      (is (not (valid? ont/OntologyClassHierarchy
                       {:iri "http://example.org/ontology#Root"
                        :label "Root Class"
                        :subclasses [{:iri "http://example.org/ontology#Child"
                                      :label "Child"
                                      :extra "field"}]}))))))

(deftest test-OntologyHierarchy
  (testing "OntologyHierarchy validation"
    (testing "valid ontology hierarchy"
      ;; Hierarchy can be nil
      (is (valid? ont/OntologyHierarchy
                  {:hierarchy nil}))

      ;; Simple hierarchy
      (is (valid? ont/OntologyHierarchy
                  {:hierarchy {:iri "http://example.org/ontology#Root"
                               :label "Root Class"}}))

      ;; Complex hierarchy
      (is (valid? ont/OntologyHierarchy
                  {:hierarchy {:iri "http://example.org/ontology#Root"
                               :label "Root Class"
                               :subclasses [{:iri "http://example.org/ontology#Child"
                                             :label "Child"}]}})))

    (testing "invalid ontology hierarchy"
      ;; Missing required field
      (is (not (valid? ont/OntologyHierarchy {})))

      ;; Invalid hierarchy structure
      (is (not (valid? ont/OntologyHierarchy
                       {:hierarchy {:invalid "structure"}})))

      ;; Extra fields not allowed
      (is (not (valid? ont/OntologyHierarchy
                       {:hierarchy nil
                        :extra "field"}))))))

(deftest test-OntologyHierarchyList
  (testing "OntologyHierarchyList validation"
    (testing "valid hierarchy list"
      ;; Empty list
      (is (valid? ont/OntologyHierarchyList
                  {:hierarchies []}))

      ;; Single hierarchy
      (is (valid? ont/OntologyHierarchyList
                  {:hierarchies [{:iri "http://example.org/ontology#Root"
                                  :label "Root Class"}]}))

      ;; Multiple hierarchies
      (is (valid? ont/OntologyHierarchyList
                  {:hierarchies [{:iri "http://example.org/ontology#Root1"
                                  :label "Root 1"
                                  :subclasses [{:iri "http://example.org/ontology#Child1"
                                                :label "Child 1"}]}
                                 {:iri "http://example.org/ontology#Root2"
                                  :label "Root 2"}
                                 {:iri "http://example.org/ontology#Root3"
                                  :label "Root 3"
                                  :description "Third root"
                                  :subclasses []}]})))

    (testing "invalid hierarchy list"
      ;; Missing required field
      (is (not (valid? ont/OntologyHierarchyList {})))

      ;; Wrong type
      (is (not (valid? ont/OntologyHierarchyList
                       {:hierarchies "not-a-vector"})))

      ;; Invalid hierarchy in list
      (is (not (valid? ont/OntologyHierarchyList
                       {:hierarchies [{:invalid "hierarchy"}]})))

      ;; Extra fields not allowed
      (is (not (valid? ont/OntologyHierarchyList
                       {:hierarchies []
                        :extra "field"}))))))

(deftest test-recursive-validation
  (testing "Recursive hierarchy validation"
    ;; Test that validation properly handles deeply nested structures
    (let [create-deep-hierarchy (fn create-deep-hierarchy [depth]
                                  (if (zero? depth)
                                    {:iri (str "http://example.org/ontology#Level" depth)
                                     :label (str "Level " depth)}
                                    {:iri (str "http://example.org/ontology#Level" depth)
                                     :label (str "Level " depth)
                                     :subclasses [(create-deep-hierarchy (dec depth))]}))
          deep-hierarchy (create-deep-hierarchy 10)]

      (is (valid? ont/OntologyClassHierarchy deep-hierarchy)))

    ;; Test that invalid nested data is caught
    (is (not (valid? ont/OntologyClassHierarchy
                     {:iri "http://example.org/ontology#Root"
                      :label "Root"
                      :subclasses [{:iri "http://example.org/ontology#Child"
                                    :label "Child"
                                    :subclasses [{:iri "http://example.org/ontology#Grandchild"
                                                  ;; Missing label
                                                  :subclasses []}]}]})))))
