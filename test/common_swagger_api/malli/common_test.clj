(ns common-swagger-api.malli.common-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.common :as common]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-IncludeHiddenParams
  (testing "IncludeHiddenParams validation"
    (testing "valid params"
      (is (valid? common/IncludeHiddenParams {}))
      (is (valid? common/IncludeHiddenParams
                  {:include-hidden true}))
      (is (valid? common/IncludeHiddenParams
                  {:include-hidden false})))

    (testing "invalid params"
      ;; Extra fields not allowed due to :closed true
      (is (not (valid? common/IncludeHiddenParams
                       {:include-hidden true
                        :extra-field "not allowed"})))
      ;; Wrong type for include-hidden
      (is (not (valid? common/IncludeHiddenParams
                       {:include-hidden "not a boolean"})))
      (is (not (valid? common/IncludeHiddenParams
                       {:include-hidden 1})))
      (is (not (valid? common/IncludeHiddenParams
                       {:include-hidden nil}))))))

(deftest test-IncludeDeletedParams
  (testing "IncludeDeletedParams validation"
    (testing "valid params"
      (is (valid? common/IncludeDeletedParams {}))
      (is (valid? common/IncludeDeletedParams
                  {:include-deleted true}))
      (is (valid? common/IncludeDeletedParams
                  {:include-deleted false})))

    (testing "invalid params"
      ;; Extra fields not allowed due to :closed true
      (is (not (valid? common/IncludeDeletedParams
                       {:include-deleted true
                        :extra-field "not allowed"})))
      ;; Wrong type for include-deleted
      (is (not (valid? common/IncludeDeletedParams
                       {:include-deleted "not a boolean"})))
      (is (not (valid? common/IncludeDeletedParams
                       {:include-deleted 1})))
      (is (not (valid? common/IncludeDeletedParams
                       {:include-deleted nil}))))))

(deftest test-both-params-together
  (testing "Using both params in the same map"
    ;; Since these are separate schemas, they can't be combined directly
    ;; Each schema only allows its specific field
    (is (not (valid? common/IncludeHiddenParams
                     {:include-hidden true
                      :include-deleted true})))
    (is (not (valid? common/IncludeDeletedParams
                     {:include-hidden true
                      :include-deleted true})))))
