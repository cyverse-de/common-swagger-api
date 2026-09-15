(ns common-swagger-api.malli.ontologies-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.ontologies :as ontologies]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]))

(def target-id #uuid "cdff6d22-5634-4ad5-92f6-ffc4cee9ad05")

(def details
  {:iri        "http://purl.obolibrary.org/obo/go.owl"
   :version    "v2023-01-15"
   :created_by "user123"
   :created_on #inst "2023-01-15T10:30:00.000Z"})

(def ontology-class
  {:iri "http://purl.obolibrary.org/obo/GO_0008150" :label "biological_process"})

(def hierarchy (assoc ontology-class :subclasses [(assoc ontology-class :subclasses [])]))

(deftest OntologyVersionParam
  (valid ontologies/OntologyVersionParam "v1.2.3" "")
  (invalid ontologies/OntologyVersionParam :v1 nil))

(deftest OntologyClassIRIParam
  (valid ontologies/OntologyClassIRIParam "http://purl.obolibrary.org/obo/GO_0008150")
  (invalid ontologies/OntologyClassIRIParam 1 nil))

(deftest OntologyHierarchyFilterParams
  (valid ontologies/OntologyHierarchyFilterParams {:attr "cyverse_avus.ontology_iris"})
  (invalid ontologies/OntologyHierarchyFilterParams {} {:attr 1} {:attr "a" :extra 1}))

(deftest OntologyDetails
  (valid ontologies/OntologyDetails details (assoc details :iri nil))
  (invalid ontologies/OntologyDetails {} (dissoc details :version) (assoc details :created_by "")
           (assoc details :created_on "2023-01-15") (assoc details :extra 1)))

(deftest OntologyClass
  (valid ontologies/OntologyClass ontology-class (assoc ontology-class :label nil :description "d")
         (assoc ontology-class :description nil))
  (invalid ontologies/OntologyClass {} (dissoc ontology-class :label) (assoc ontology-class :iri nil)
           (assoc ontology-class :extra 1)))

(deftest OntologyClassHierarchy
  (valid ontologies/OntologyClassHierarchy ontology-class hierarchy
         (assoc ontology-class :subclasses []))
  (invalid ontologies/OntologyClassHierarchy {} (assoc ontology-class :subclasses [{}])
           (assoc ontology-class :subclasses ontology-class) (assoc ontology-class :extra 1)))

(deftest OntologyHierarchy
  (valid ontologies/OntologyHierarchy {:hierarchy nil} {:hierarchy hierarchy})
  (invalid ontologies/OntologyHierarchy {} {:hierarchy {}} {:hierarchy nil :extra 1}))

(deftest OntologyHierarchyList
  (valid ontologies/OntologyHierarchyList {:hierarchies []} {:hierarchies [hierarchy]})
  (invalid ontologies/OntologyHierarchyList {} {:hierarchies [{}]} {:hierarchies [] :extra 1}))

(deftest TargetOntologyHierarchies
  (valid ontologies/TargetOntologyHierarchies {:id target-id :hierarchies []}
         {:id target-id :hierarchies [hierarchy]})
  (invalid ontologies/TargetOntologyHierarchies
           {}
           {:id target-id}
           {:id (str target-id) :hierarchies []}
           {:id target-id :hierarchies [] :extra 1}))

(deftest TargetOntologyHierarchiesList
  (valid ontologies/TargetOntologyHierarchiesList {:targets []}
         {:targets [{:id target-id :hierarchies [hierarchy]}]})
  (invalid ontologies/TargetOntologyHierarchiesList {} {:targets [{}]} {:targets [] :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.ontologies))

(deftest examples
  (examples-valid 'common-swagger-api.malli.ontologies))
