(ns common-swagger-api.malli.apps.reference-genomes-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.apps.reference-genomes :as rg]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-ReferenceGenomeIdParam
  (testing "ReferenceGenomeIdParam validation"
    (is (valid? rg/ReferenceGenomeIdParam #uuid "123e4567-e89b-12d3-a456-426614174000"))
    (is (not (valid? rg/ReferenceGenomeIdParam "not-a-uuid")))
    (is (not (valid? rg/ReferenceGenomeIdParam 123)))
    (is (not (valid? rg/ReferenceGenomeIdParam nil)))))

(deftest test-ReferenceGenomeListingParams
  (testing "ReferenceGenomeListingParams validation"
    (testing "valid params"
      (is (valid? rg/ReferenceGenomeListingParams {}))
      (is (valid? rg/ReferenceGenomeListingParams {:deleted false}))
      (is (valid? rg/ReferenceGenomeListingParams {:created_by "johndoe"}))
      (is (valid? rg/ReferenceGenomeListingParams {:deleted true
                                                   :created_by "admin"})))

    (testing "invalid params"
      (is (not (valid? rg/ReferenceGenomeListingParams {:deleted "false"})))
      (is (not (valid? rg/ReferenceGenomeListingParams {:created_by 123})))
      (is (not (valid? rg/ReferenceGenomeListingParams {:extra-field "not-allowed"}))))))

(deftest test-ReferenceGenome
  (testing "ReferenceGenome validation"
    (testing "valid reference genome"
      (is (valid? rg/ReferenceGenome
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "Homo sapiens (GRCh38/hg38)"
                   :path "/iplant/home/shared/genomes/hg38"
                   :created_by "admin"
                   :last_modified_by "admin"}))
      (is (valid? rg/ReferenceGenome
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "Mus musculus (GRCm38/mm10)"
                   :path "/iplant/home/shared/genomes/mm10"
                   :deleted false
                   :created_by "bioinformatics"
                   :created_on #inst "2023-01-15T10:30:00.000Z"
                   :last_modified_by "bioinformatics"
                   :last_modified_on #inst "2023-06-20T14:45:00.000Z"})))

    (testing "invalid reference genome"
      (is (not (valid? rg/ReferenceGenome {})))
      (is (not (valid? rg/ReferenceGenome
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "Human genome"
                        :path "/genomes/human"})))
      (is (not (valid? rg/ReferenceGenome
                       {:id "not-a-uuid"
                        :name "Human genome"
                        :path "/genomes/human"
                        :created_by "admin"
                        :last_modified_by "admin"})))
      (is (not (valid? rg/ReferenceGenome
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :path "/genomes/human"
                        :created_by "admin"
                        :last_modified_by "admin"})))
      (is (not (valid? rg/ReferenceGenome
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "Human genome"
                        :path "/genomes/human"
                        :deleted "false"
                        :created_by "admin"
                        :last_modified_by "admin"})))
      (is (not (valid? rg/ReferenceGenome
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "Human genome"
                        :path "/genomes/human"
                        :created_by "admin"
                        :last_modified_by "admin"
                        :created_on "2023-01-15"})))
      (is (not (valid? rg/ReferenceGenome
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "Human genome"
                        :path "/genomes/human"
                        :created_by "admin"
                        :last_modified_by "admin"
                        :extra-field "not-allowed"}))))))

(deftest test-ReferenceGenomesList
  (testing "ReferenceGenomesList validation"
    (testing "valid genomes list"
      (is (valid? rg/ReferenceGenomesList {:genomes []}))
      (is (valid? rg/ReferenceGenomesList
                  {:genomes [{:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                              :name "Homo sapiens (GRCh38/hg38)"
                              :path "/iplant/home/shared/genomes/hg38"
                              :created_by "admin"
                              :last_modified_by "admin"}
                             {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                              :name "Mus musculus (GRCm38/mm10)"
                              :path "/iplant/home/shared/genomes/mm10"
                              :deleted false
                              :created_by "bioinformatics"
                              :created_on #inst "2023-01-15T10:30:00.000Z"
                              :last_modified_by "bioinformatics"
                              :last_modified_on #inst "2023-06-20T14:45:00.000Z"}]})))

    (testing "invalid genomes list"
      (is (not (valid? rg/ReferenceGenomesList {})))
      (is (not (valid? rg/ReferenceGenomesList {:genomes nil})))
      (is (not (valid? rg/ReferenceGenomesList {:genomes "not-a-vector"})))
      (is (not (valid? rg/ReferenceGenomesList {:genomes [{}]}))))))

(deftest test-endpoint-descriptions
  (testing "Endpoint descriptions are defined and are strings"
    (is (string? rg/ReferenceGenomeDetailsSummary))
    (is (string? rg/ReferenceGenomeDetailsDocs))
    (is (string? rg/ReferenceGenomeListingSummary))
    (is (string? rg/ReferenceGenomeListingDocs)))

  (testing "Endpoint descriptions have expected content"
    (is (re-find #"Get a Reference Genome" rg/ReferenceGenomeDetailsSummary))
    (is (re-find #"UUID" rg/ReferenceGenomeDetailsDocs))
    (is (re-find #"List Reference Genomes" rg/ReferenceGenomeListingSummary))
    (is (re-find #"available Reference Genomes" rg/ReferenceGenomeListingDocs))))
