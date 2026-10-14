(ns common-swagger-api.malli.apps.reference-genomes)

;; Endpoint description definitions

(def ReferenceGenomeDetailsSummary "Get a Reference Genome.")
(def ReferenceGenomeDetailsDocs
  "This endpoint may be used to obtain a Reference Genome by its UUID.")

(def ReferenceGenomeListingSummary "List Reference Genomes.")
(def ReferenceGenomeListingDocs
  "This endpoint may be used to obtain lists of all available Reference Genomes.")

;; Field definitions

(def ReferenceGenomeIdParam
  [:uuid {:description         "A UUID that is used to identify the Reference Genome"
          :json-schema/example #uuid "123e4567-e89b-12d3-a456-426614174000"}])

;; Schema definitions

(def ReferenceGenomeListingParams
  [:map {:closed true}
   [:deleted
    {:optional            true
     :description         (str "Whether or not to include Reference Genomes that have been marked as deleted "
                              "(false by default).")
     :json-schema/example false}
    :boolean]

   [:created_by
    {:optional            true
     :description         "Filters the Reference Genome listing by the user that added them."
     :json-schema/example "johndoe"}
    :string]])

(def ReferenceGenome
  [:map {:closed true}
   [:id ReferenceGenomeIdParam]

   [:name
    {:description         "The Reference Genome's name"
     :json-schema/example "Homo sapiens (GRCh38/hg38)"}
    :string]

   [:path
    {:description         "The path of the directory containing the Reference Genome"
     :json-schema/example "/iplant/home/shared/iplantcollaborative/genomeservices/builds/1.0.0/24_77/de_support"}
    :string]

   [:deleted
    {:optional            true
     :description         "Whether the Reference Genome is marked as deleted"
     :json-schema/example false}
    :boolean]

   [:created_by
    {:description         "The username of the user that added the Reference Genome"
     :json-schema/example "admin"}
    :string]

   [:created_on
    {:optional            true
     :description         "The date the Reference Genome was added"
     :json-schema/example #inst "2023-01-15T10:30:00.000Z"}
    inst?]

   [:last_modified_by
    {:description         "The username of the user that updated the Reference Genome"
     :json-schema/example "admin"}
    :string]

   [:last_modified_on
    {:optional            true
     :description         "The date of last modification to the Reference Genome"
     :json-schema/example #inst "2023-06-20T14:45:00.000Z"}
    inst?]])

(def ReferenceGenomesList
  [:map {:closed true}
   [:genomes
    {:description "Listing of Reference Genomes."}
    [:vector ReferenceGenome]]])
