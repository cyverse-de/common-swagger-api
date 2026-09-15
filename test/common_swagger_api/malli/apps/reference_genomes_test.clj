(ns common-swagger-api.malli.apps.reference-genomes-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.apps.reference-genomes :as reference-genomes]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def genome-id #uuid "123e4567-e89b-12d3-a456-426614174000")

(def genome
  {:id               genome-id
   :name             "Homo sapiens (GRCh38/hg38)"
   :path             "/iplant/home/shared/iplantcollaborative/genomeservices/builds/1.0.0/24_77/de_support"
   :created_by       "admin"
   :last_modified_by "admin"})

(deftest ReferenceGenomeIdParam
  (valid reference-genomes/ReferenceGenomeIdParam genome-id)
  (invalid reference-genomes/ReferenceGenomeIdParam (str genome-id) nil))

(deftest ReferenceGenomeListingParams
  (valid reference-genomes/ReferenceGenomeListingParams {} {:deleted false} {:created_by "johndoe"})
  (invalid reference-genomes/ReferenceGenomeListingParams {:deleted "false"} {:created_by 1} {:extra 1}))

(deftest ReferenceGenome
  (valid reference-genomes/ReferenceGenome
         genome
         (assoc genome
                :deleted          false
                :created_on       #inst "2023-01-15T10:30:00.000Z"
                :last_modified_on #inst "2023-06-20T14:45:00.000Z"))
  (invalid reference-genomes/ReferenceGenome
           {}
           (dissoc genome :last_modified_by)
           (assoc genome :created_on "2023-01-15T10:30:00.000Z")
           (assoc genome :deleted "false")
           (assoc genome :extra 1)))

(deftest ReferenceGenomesList
  (valid reference-genomes/ReferenceGenomesList {:genomes []} {:genomes [genome]})
  (invalid reference-genomes/ReferenceGenomesList {} {:genomes [{}]} {:genomes genome} {:genomes [] :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps.reference-genomes))
