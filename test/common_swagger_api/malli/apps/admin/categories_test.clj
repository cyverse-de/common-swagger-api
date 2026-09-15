(ns common-swagger-api.malli.apps.admin.categories-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.apps.admin.categories :as admin-categories]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]))

(def category-id
  {:system_id "de"
   :id        #uuid "123e4567-e89b-12d3-a456-426614174000"})

(def category-id-list {:category_ids [category-id]})

(def categorization
  (assoc category-id-list :system_id "de" :app_id "app-id-12345"))

(deftest AppCategoryIdList
  (valid admin-categories/AppCategoryIdList category-id-list {:category_ids []})
  (invalid admin-categories/AppCategoryIdList
           {}
           (assoc category-id-list :category_ids [(dissoc category-id :id)])
           (assoc category-id-list :category_ids category-id)
           (assoc category-id-list :extra 1)))

(deftest AppCategorization
  (valid admin-categories/AppCategorization categorization (assoc categorization :category_ids []))
  (invalid admin-categories/AppCategorization
           {}
           category-id-list
           (dissoc categorization :app_id)
           (assoc categorization :app_id 1)
           (assoc categorization :extra 1)))

(deftest AppCategorizationRequest
  (valid admin-categories/AppCategorizationRequest {:categories []} {:categories [categorization]})
  (invalid admin-categories/AppCategorizationRequest
           {}
           {:categories categorization}
           {:categories [(dissoc categorization :system_id)]}
           {:categories [] :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps.admin.categories))

(deftest examples
  (examples-valid 'common-swagger-api.malli.apps.admin.categories))
