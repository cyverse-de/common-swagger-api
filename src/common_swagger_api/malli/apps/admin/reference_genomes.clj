(ns common-swagger-api.malli.apps.admin.reference-genomes
  (:require
   [common-swagger-api.malli.apps.reference-genomes :refer [ReferenceGenome]]
   [malli.util :as mu]))

(def ReferenceGenomeAddSummary "Add a Reference Genome")
(def ReferenceGenomeAddDocs
  "This endpoint adds a Reference Genome to the Discovery Environment.")

(def ReferenceGenomeDeleteSummary "Delete a Reference Genome")
(def ReferenceGenomeDeleteDocs
  (str "A Reference Genome can be marked as deleted in the DE without being completely removed from the database "
       "using this service. **Note**: an attempt to delete a Reference Genome that is already marked as deleted is "
       "treated as a no-op rather than an error condition. If the Reference Genome doesn't exist in the database at "
       "all, however, then that is treated as an error condition."))

(def ReferenceGenomeUpdateSummary "Update a Reference Genome")
(def ReferenceGenomeUpdateDocs
  (str "This endpoint modifies the `name`, `path`, and `deleted` fields of a Reference Genome in the Discovery "
       "Environment."))

(def ReferenceGenomeDeletionParams
  [:map {:closed true}
   [:permanent
    {:optional            true
     :description         "If true, completely remove the reference genome from the database."
     :json-schema/example false}
    :boolean]])

(def ReferenceGenomeRequest
  (mu/optional-keys ReferenceGenome [:id :created_by :last_modified_by]))

(def ReferenceGenomeAddRequest
  (mu/update-properties ReferenceGenomeRequest assoc :description "The Reference Genome to add."))

(def ReferenceGenomeUpdateRequest
  (mu/update-properties ReferenceGenomeRequest assoc :description "The Reference Genome fields to update."))
