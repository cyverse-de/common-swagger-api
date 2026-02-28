(ns common-swagger-api.malli.ontologies
  (:require
   [common-swagger-api.malli :refer [NonBlankString]]
   [malli.core :as m]))

(def OntologyVersionParam
  [:string {:description         "The unique version of the Ontology"
            :json-schema/example "v1.2.3"}])

(def OntologyClassIRIParam
  [:string {:description         "A unique Class IRI"
            :json-schema/example "http://purl.obolibrary.org/obo/GO_0008150"}])

(def OntologyHierarchyFilterParams
  [:map {:closed true}
   [:attr
    {:description         "The metadata attribute that stores class IRIs under the given root IRI"
     :json-schema/example "cyverse_avus.ontology_iris"}
    :string]])

(def OntologyDetails
  [:map {:closed true}
   [:iri
    {:description         "The unique IRI for this Ontology"
     :json-schema/example "http://purl.obolibrary.org/obo/go.owl"}
    [:maybe :string]]

   [:version
    {:description         "The unique version of the Ontology"
     :json-schema/example "v2023-01-15"}
    :string]

   [:created_by
    {:description         "The user who uploaded this Ontology"
     :json-schema/example "user123"}
    NonBlankString]

   [:created_on
    {:description         "The date this Ontology was uploaded"
     :json-schema/example #inst "2023-01-15T10:30:00.000Z"}
    inst?]])

(def OntologyClass
  [:map {:closed true}
   [:iri
    {:description         "The unique IRI for this Ontology Class"
     :json-schema/example "http://purl.obolibrary.org/obo/GO_0008150"}
    :string]

   [:label
    {:description         "The label annotation of this Ontology Class"
     :json-schema/example "biological_process"}
    [:maybe :string]]

   [:description
    {:optional            true
     :description         "The description annotation of this Ontology Class"
     :json-schema/example "A biological process is the execution of a genetically-encoded biological module or program"}
    [:maybe :string]]])

(def OntologyClassHierarchy
  (m/schema
   [:schema {:registry {::OntologyClassHierarchy
                        [:map {:closed true}
                         [:iri
                          {:description         "The unique IRI for this Ontology Class"
                           :json-schema/example "http://purl.obolibrary.org/obo/GO_0008150"}
                          :string]

                         [:label
                          {:description         "The label annotation of this Ontology Class"
                           :json-schema/example "biological_process"}
                          [:maybe :string]]

                         [:description
                          {:optional            true
                           :description         "The description annotation of this Ontology Class"
                           :json-schema/example "A biological process is the execution of a genetically-encoded biological module or program"}
                          [:maybe :string]]

                         [:subclasses
                          {:optional    true
                           :description "Subclasses of this Ontology Class"}
                          [:vector [:ref ::OntologyClassHierarchy]]]]}}
    [:ref ::OntologyClassHierarchy]]))

(def OntologyHierarchy
  [:map {:closed true}
   [:hierarchy
    {:description "An Ontology Class hierarchy"}
    [:maybe OntologyClassHierarchy]]])

(def OntologyHierarchyList
  [:map {:closed true}
   [:hierarchies
    {:description "A list of Ontology Class hierarchies"}
    [:vector OntologyClassHierarchy]]])
