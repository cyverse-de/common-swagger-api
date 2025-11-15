(ns common-swagger-api.malli.oauth-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.oauth :as oauth]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(def invalid? (complement valid?))

(deftest test-RedirectUrisResponse
  (testing "RedirectUrisResponse validation"
    (testing "valid redirect URIs response"
      ;; Empty map is valid since :closed false
      (is (valid? oauth/RedirectUrisResponse {}))

      ;; Single API with redirect URI
      (is (valid? oauth/RedirectUrisResponse
                  {:de "https://example.com/oauth/callback"}))

      ;; Multiple APIs with redirect URIs
      (is (valid? oauth/RedirectUrisResponse
                  {:de "https://example.com/oauth/callback"
                   :terrain "https://terrain.example.com/auth/redirect"
                   :apps "https://apps.example.com/oauth2/callback"}))

      ;; Keywords as keys with string values
      (is (valid? oauth/RedirectUrisResponse
                  {:api-v1 "https://api.example.com/v1/callback"
                   :api-v2 "https://api.example.com/v2/callback"}))

      (is (invalid? oauth/RedirectUrisResponse
                  {"string-key" "https://example.com/callback"
                   :keyword-key "https://example.com/callback2"
                   123 "https://example.com/callback3"})))))

(deftest test-edge-cases
  (testing "Edge cases and boundary conditions"
    (testing "nil values"
      (is (invalid? oauth/RedirectUrisResponse
                    {:api nil})))

    (testing "large maps"
      ;; Create a large map with many entries
      (let [large-map (into {} (map (fn [i] [(keyword (str "api" i))
                                             (str "https://example.com/callback" i)])
                                    (range 100)))]
        (is (valid? oauth/RedirectUrisResponse large-map))))

    (testing "boolean and numeric keys/values"
      (is (invalid? oauth/RedirectUrisResponse
                    {:api1 true
                     :api2 false
                     :api3 42
                     :api4 3.14})))))
