(ns common-swagger-api.malli.apps.admin.reference-genomes-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.apps.admin.reference-genomes :as admin-reference-genomes]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def reference-genome
  {:id               #uuid "123e4567-e89b-12d3-a456-426614174000"
   :name             "Homo sapiens (GRCh38/hg38)"
   :path             "/iplant/home/shared/iplantcollaborative/genomeservices/builds/1.0.0/24_77/de_support"
   :created_by       "admin"
   :last_modified_by "admin"})

(deftest ReferenceGenomeDeletionParams
  (valid admin-reference-genomes/ReferenceGenomeDeletionParams {} {:permanent true} {:permanent false})
  (invalid admin-reference-genomes/ReferenceGenomeDeletionParams {:permanent "yes"} {:extra 1}))

(deftest ReferenceGenomeRequest
  (valid admin-reference-genomes/ReferenceGenomeRequest
         reference-genome
         (select-keys reference-genome [:name :path])
         (assoc reference-genome :deleted false :created_on #inst "2023-01-15T10:30:00.000Z"))
  (invalid admin-reference-genomes/ReferenceGenomeRequest
           {}
           (dissoc reference-genome :name)
           (assoc reference-genome :id "not-a-uuid")
           (assoc reference-genome :extra 1)))

(deftest ReferenceGenomeAddRequest
  (valid admin-reference-genomes/ReferenceGenomeAddRequest
         reference-genome
         (select-keys reference-genome [:name :path]))
  (invalid admin-reference-genomes/ReferenceGenomeAddRequest
           {}
           (dissoc reference-genome :path)
           (assoc reference-genome :path 1)
           (assoc reference-genome :extra 1)))

(deftest ReferenceGenomeUpdateRequest
  (valid admin-reference-genomes/ReferenceGenomeUpdateRequest
         reference-genome
         (select-keys reference-genome [:name :path]))
  (invalid admin-reference-genomes/ReferenceGenomeUpdateRequest
           {}
           (dissoc reference-genome :name)
           (assoc reference-genome :name 1)
           (assoc reference-genome :extra 1)))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps.admin.reference-genomes))
