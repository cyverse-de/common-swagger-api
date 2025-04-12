(ns common-swagger-api.schema.apps.reference-genomes
  (:require [common-swagger-api.schema :refer [describe]]
            [schema.core :refer [defschema optional-key]])
  (:import (java.util Date UUID)))

(def ReferenceGenomeDetailsSummary "Get a Reference Genome.")
(def ReferenceGenomeDetailsDocs
  "This endpoint may be used to obtain a Reference Genome by its UUID.")

(def ReferenceGenomeListingSummary "List Reference Genomes.")
(def ReferenceGenomeListingDocs
  "This endpoint may be used to obtain lists of all available Reference Genomes.")

(def ReferenceGenomeIdParam (describe UUID "A UUID that is used to identify the Reference Genome"))

(defschema ReferenceGenomeListingParams
  {(optional-key :deleted)
   (describe Boolean
             "Whether or not to include Reference Genomes that have been marked as deleted
              (false by default).")

   (optional-key :created_by)
   (describe String "Filters the Reference Genome listing by the user that added them.")})

(defschema ReferenceGenome
  {:id
   ReferenceGenomeIdParam

   :name
   (describe String "The Reference Genome's name")

   :path
   (describe String "The path of the directory containing the Reference Genome")

   (optional-key :deleted)
   (describe Boolean "Whether the Reference Genome is marked as deleted")

   :created_by
   (describe String "The username of the user that added the Reference Genome")

   (optional-key :created_on)
   (describe Date "The date the Reference Genome was added")

   :last_modified_by
   (describe String "The username of the user that updated the Reference Genome")

   (optional-key :last_modified_on)
   (describe Date "The date of last modification to the Reference Genome")})

(defschema ReferenceGenomesList
  {:genomes (describe [ReferenceGenome] "Listing of Reference Genomes.")})
