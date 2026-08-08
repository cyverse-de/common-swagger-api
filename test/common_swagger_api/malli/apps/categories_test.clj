(ns common-swagger-api.malli.apps.categories-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.apps.categories :as c]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-CategoryListingParams
  (testing "CategoryListingParams validation"
    (is (valid? c/CategoryListingParams {}))
    (is (valid? c/CategoryListingParams {:public true}))
    (is (valid? c/CategoryListingParams {:public false}))
    (is (not (valid? c/CategoryListingParams {:public "not-a-boolean"})))
    (is (not (valid? c/CategoryListingParams {:extra-field "not-allowed"})))))

(deftest test-AppCategoryId
  (testing "AppCategoryId validation"
    (is (valid? c/AppCategoryId
                {:system_id "de"
                 :id #uuid "123e4567-e89b-12d3-a456-426614174000"}))
    (is (not (valid? c/AppCategoryId {})))
    (is (not (valid? c/AppCategoryId {:id #uuid "123e4567-e89b-12d3-a456-426614174000"})))
    (is (not (valid? c/AppCategoryId {:system_id "de"})))
    (is (not (valid? c/AppCategoryId {:system_id "de"
                                       :id "not-a-uuid"})))
    (is (not (valid? c/AppCategoryId {:system_id "de"
                                       :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                       :extra-field "not-allowed"})))))

(deftest test-AppCategoryBase
  (testing "AppCategoryBase validation"
    (is (valid? c/AppCategoryBase
                {:system_id "de"
                 :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                 :name "Genome Sequencing"}))
    (is (not (valid? c/AppCategoryBase {})))
    (is (not (valid? c/AppCategoryBase {:system_id "de"
                                         :id #uuid "123e4567-e89b-12d3-a456-426614174000"})))
    (is (not (valid? c/AppCategoryBase {:system_id "de"
                                         :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                         :name "Genome Sequencing"
                                         :extra-field "not-allowed"})))))

(deftest test-AppCategory
  (testing "AppCategory validation"
    (testing "valid category without children"
      (is (valid? c/AppCategory
                  {:system_id "de"
                   :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "Genome Sequencing"
                   :total 42
                   :is_public true})))

    (testing "valid category with children"
      (is (valid? c/AppCategory
                  {:system_id "de"
                   :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "Genome Sequencing"
                   :total 42
                   :is_public true
                   :categories [{:system_id "de"
                                 :id #uuid "987e6543-e21b-32c1-b456-426614174000"
                                 :name "Sub Category"
                                 :total 10
                                 :is_public false}]})))

    (testing "invalid category"
      (is (not (valid? c/AppCategory {})))
      (is (not (valid? c/AppCategory {:system_id "de"
                                       :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                       :name "Genome Sequencing"})))
      (is (not (valid? c/AppCategory {:system_id "de"
                                       :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                       :name "Genome Sequencing"
                                       :total "not-a-number"
                                       :is_public true})))
      (is (not (valid? c/AppCategory {:system_id "de"
                                       :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                       :name "Genome Sequencing"
                                       :total 42
                                       :is_public "not-a-boolean"})))
      (is (not (valid? c/AppCategory {:system_id "de"
                                       :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                                       :name "Genome Sequencing"
                                       :total 42
                                       :is_public true
                                       :extra-field "not-allowed"}))))))

(deftest test-AppCategoryListing
  (testing "AppCategoryListing validation"
    (is (valid? c/AppCategoryListing {:categories []}))
    (is (valid? c/AppCategoryListing
                {:categories [{:system_id "de"
                               :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                               :name "Genome Sequencing"
                               :total 42
                               :is_public true}]}))
    (is (not (valid? c/AppCategoryListing {})))
    (is (not (valid? c/AppCategoryListing {:categories "not-a-vector"})))))

(deftest test-AppCategoryAppListing
  (testing "AppCategoryAppListing validation"
    (let [base-category {:system_id "de"
                         :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                         :name "Genome Sequencing"
                         :total 42
                         :is_public true}]
      (testing "valid with empty apps"
        (is (valid? c/AppCategoryAppListing (assoc base-category :apps []))))

      (testing "categories field not allowed"
        (is (not (valid? c/AppCategoryAppListing
                         (assoc base-category :apps [] :categories [])))))

      (testing "apps field required"
        (is (not (valid? c/AppCategoryAppListing base-category))))

      (testing "missing required fields"
        (is (not (valid? c/AppCategoryAppListing {:apps []})))))))

(deftest test-OntologyAppListingPagingParams
  (testing "OntologyAppListingPagingParams validation"
    (is (valid? c/OntologyAppListingPagingParams
                {:attr "cyverse_avus.ontology_iris"}))
    (is (valid? c/OntologyAppListingPagingParams
                {:attr "cyverse_avus.ontology_iris"
                 :limit 50
                 :offset 0
                 :sort-field :name
                 :sort-dir "ASC"}))
    (is (not (valid? c/OntologyAppListingPagingParams {})))
    (is (not (valid? c/OntologyAppListingPagingParams
                     {:attr "cyverse_avus.ontology_iris"
                      :extra-field "not-allowed"})))))
