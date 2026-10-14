(ns common-swagger-api.malli.apps.categories-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.apps.categories :as categories]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]))

(def category-id
  {:system_id "de"
   :id        #uuid "123e4567-e89b-12d3-a456-426614174000"})

(def category-base (assoc category-id :name "Genome Sequencing"))

(def category (assoc category-base :total 42 :is_public true))

(def subcategory
  {:system_id "de"
   :id        #uuid "987e6543-e21b-42c1-b456-426614174000"
   :name      "Sub Category"
   :total     10
   :is_public false})

(def listing-detail
  {:id                   "app-id-123"
   :name                 "BLAST"
   :description          "Basic sequence alignment"
   :app_type             "DE"
   :can_favor            true
   :can_rate             true
   :can_run              true
   :deleted              false
   :disabled             false
   :integrator_email     "user@example.org"
   :integrator_name      "Test User"
   :is_public            true
   :pipeline_eligibility {:is_valid true :reason ""}
   :rating               {:average 4.5 :total 42}
   :step_count           1
   :permission           "own"})

(deftest CategoryListingParams
  (valid categories/CategoryListingParams {} {:public true} {:public false})
  (invalid categories/CategoryListingParams {:public "yes"} {:extra 1}))

(deftest AppCategoryId
  (valid categories/AppCategoryId category-id)
  (invalid categories/AppCategoryId
           {}
           (dissoc category-id :system_id)
           (assoc category-id :id "not-a-uuid")
           (assoc category-id :extra 1)))

(deftest AppCategoryBase
  (valid categories/AppCategoryBase category-base)
  (invalid categories/AppCategoryBase
           {}
           category-id
           (assoc category-base :name 1)
           (assoc category-base :extra 1)))

(deftest AppCategory
  (valid categories/AppCategory category (assoc category :categories [subcategory])
         (assoc category :categories [(assoc subcategory :categories [])]))
  (invalid categories/AppCategory
           {}
           category-base
           (assoc category :total "42")
           (assoc category :is_public "yes")
           (assoc category :categories [{}])
           (assoc category :extra 1)))

(deftest AppCategoryListing
  (valid categories/AppCategoryListing {:categories []} {:categories [category]})
  (invalid categories/AppCategoryListing {} {:categories [{}]} {:categories category}
           {:categories [] :extra 1}))

(deftest AppCategoryAppListing
  (valid categories/AppCategoryAppListing (assoc category :apps []) (assoc category :apps [listing-detail])
         ;; plumatic's (dissoc AppCategory :categories) misses the optional key, so :categories is still accepted
         (assoc category :apps [] :categories [subcategory]))
  (invalid categories/AppCategoryAppListing
           {}
           category
           {:apps []}
           (assoc category :apps [] :categories [{}])
           (assoc category :apps [{}])))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps.categories))

(deftest examples
  (examples-valid 'common-swagger-api.malli.apps.categories))
