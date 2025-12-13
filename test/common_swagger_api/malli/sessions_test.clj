(ns common-swagger-api.malli.sessions-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.sessions :as sessions]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(def invalid? (complement valid?))

(deftest test-IPAddrParam
  (testing "IPAddrParam validation"
    (testing "valid IP address params"
      ;; Empty map is valid since ip-address is optional
      (is (valid? sessions/IPAddrParam {}))

      ;; With IP address
      (is (valid? sessions/IPAddrParam
                  {:ip-address "127.0.0.1"}))

      (is (valid? sessions/IPAddrParam
                  {:ip-address "192.168.1.1"}))

      (is (valid? sessions/IPAddrParam
                  {:ip-address "10.0.0.1"}))

      ;; IPv6 address
      (is (valid? sessions/IPAddrParam
                  {:ip-address "2001:0db8:85a3:0000:0000:8a2e:0370:7334"})))

    (testing "invalid IP address params"
      ;; Extra fields not allowed due to :closed true
      (is (invalid? sessions/IPAddrParam
                    {:ip-address "127.0.0.1"
                     :extra-field "not allowed"}))

      ;; Wrong type for ip-address
      (is (invalid? sessions/IPAddrParam
                    {:ip-address 127001}))

      (is (invalid? sessions/IPAddrParam
                    {:ip-address true}))

      (is (invalid? sessions/IPAddrParam
                    {:ip-address nil})))))

(deftest test-LoginTimeResponseParam
  (testing "LoginTimeResponseParam validation"
    (testing "valid login time params"
      (is (valid? sessions/LoginTimeResponseParam
                  {:login_time 1763768830001}))

      ;; Zero is valid
      (is (valid? sessions/LoginTimeResponseParam
                  {:login_time 0}))

      ;; Negative values are technically valid for :int
      (is (valid? sessions/LoginTimeResponseParam
                  {:login_time -1}))

      ;; Large values
      (is (valid? sessions/LoginTimeResponseParam
                  {:login_time 9999999999999})))

    (testing "invalid login time params"
      ;; Missing required field
      (is (invalid? sessions/LoginTimeResponseParam {}))

      ;; Extra fields not allowed due to :closed true
      (is (invalid? sessions/LoginTimeResponseParam
                    {:login_time 1763768830001
                     :extra-field "not allowed"}))

      ;; Wrong type for login_time
      (is (invalid? sessions/LoginTimeResponseParam
                    {:login_time "1763768830001"}))

      (is (invalid? sessions/LoginTimeResponseParam
                    {:login_time 3.14}))

      (is (invalid? sessions/LoginTimeResponseParam
                    {:login_time true}))

      (is (invalid? sessions/LoginTimeResponseParam
                    {:login_time nil})))))

(deftest test-LoginResponse
  (testing "LoginResponse validation"
    (testing "valid login responses"
      (is (valid? sessions/LoginResponse
                  {:login_time 1763768830001
                   :auth_redirect {:de "https://example.com/oauth/callback"}}))

      (is (valid? sessions/LoginResponse
                  {:login_time 0
                   :auth_redirect {}}))

      ;; Multiple redirect URIs
      (is (valid? sessions/LoginResponse
                  {:login_time 1763768830001
                   :auth_redirect {:de "https://de.example.com"
                                   :terrain "https://terrain.example.com"
                                   :apps "https://apps.example.com"}})))

    (testing "invalid login responses"
      ;; Missing required login_time field
      (is (invalid? sessions/LoginResponse
                    {:auth_redirect {:de "https://example.com"}}))

      ;; Missing required auth_redirect field
      (is (invalid? sessions/LoginResponse
                    {:login_time 1763768830001}))

      ;; Extra fields not allowed due to :closed true
      (is (invalid? sessions/LoginResponse
                    {:login_time 1763768830001
                     :auth_redirect {:de "https://example.com"}
                     :extra-field "not allowed"}))

      ;; Invalid auth_redirect structure (should be map of keywords to strings)
      (is (invalid? sessions/LoginResponse
                    {:login_time 1763768830001
                     :auth_redirect "not a map"}))

      (is (invalid? sessions/LoginResponse
                    {:login_time 1763768830001
                     :auth_redirect {:de 123}})))))

(deftest test-LogoutParams
  (testing "LogoutParams validation"
    (testing "valid logout params"
      ;; Just login-time (ip-address is optional)
      (is (valid? sessions/LogoutParams
                  {:login-time 1763768830001}))

      ;; With both fields
      (is (valid? sessions/LogoutParams
                  {:login-time 1763768830001
                   :ip-address "127.0.0.1"}))

      (is (valid? sessions/LogoutParams
                  {:login-time 0
                   :ip-address "192.168.1.100"})))

    (testing "invalid logout params"
      ;; Missing required login-time field
      (is (invalid? sessions/LogoutParams {}))

      ;; Missing login-time, only ip-address
      (is (invalid? sessions/LogoutParams
                    {:ip-address "127.0.0.1"}))

      ;; Extra fields not allowed due to :closed true
      (is (invalid? sessions/LogoutParams
                    {:login-time 1763768830001
                     :ip-address "127.0.0.1"
                     :extra-field "not allowed"}))

      ;; Wrong type for login-time
      (is (invalid? sessions/LogoutParams
                    {:login-time "1763768830001"}))

      ;; Wrong type for ip-address
      (is (invalid? sessions/LogoutParams
                    {:login-time 1763768830001
                     :ip-address 127001})))))

(deftest test-Login
  (testing "Login validation"
    (testing "valid login records"
      ;; Just login_time (ip_address is optional)
      (is (valid? sessions/Login
                  {:login_time 1763768830001}))

      ;; With both fields
      (is (valid? sessions/Login
                  {:login_time 1763768830001
                   :ip_address "127.0.0.1"}))

      (is (valid? sessions/Login
                  {:login_time 0
                   :ip_address "192.168.1.100"}))

      ;; IPv6 address
      (is (valid? sessions/Login
                  {:login_time 1763768830001
                   :ip_address "2001:0db8:85a3::8a2e:0370:7334"})))

    (testing "invalid login records"
      ;; Missing required login_time field
      (is (invalid? sessions/Login {}))

      ;; Missing login_time, only ip_address
      (is (invalid? sessions/Login
                    {:ip_address "127.0.0.1"}))

      ;; Extra fields not allowed due to :closed true
      (is (invalid? sessions/Login
                    {:login_time 1763768830001
                     :ip_address "127.0.0.1"
                     :extra-field "not allowed"}))

      ;; Wrong type for login_time
      (is (invalid? sessions/Login
                    {:login_time "1763768830001"}))

      (is (invalid? sessions/Login
                    {:login_time 3.14}))

      ;; Wrong type for ip_address
      (is (invalid? sessions/Login
                    {:login_time 1763768830001
                     :ip_address 127001}))

      (is (invalid? sessions/Login
                    {:login_time 1763768830001
                     :ip_address nil})))))

(deftest test-ListLoginsResponse
  (testing "ListLoginsResponse validation"
    (testing "valid list logins responses"
      ;; Empty vector is valid
      (is (valid? sessions/ListLoginsResponse
                  {:logins []}))

      ;; Single login
      (is (valid? sessions/ListLoginsResponse
                  {:logins [{:login_time 1763768830001}]}))

      ;; Single login with IP address
      (is (valid? sessions/ListLoginsResponse
                  {:logins [{:login_time 1763768830001
                             :ip_address "127.0.0.1"}]}))

      ;; Multiple logins
      (is (valid? sessions/ListLoginsResponse
                  {:logins [{:login_time 1763768830001
                             :ip_address "127.0.0.1"}
                            {:login_time 1763768830002}
                            {:login_time 1763768830003
                             :ip_address "192.168.1.1"}]})))

    (testing "invalid list logins responses"
      ;; Missing required logins field
      (is (invalid? sessions/ListLoginsResponse {}))

      ;; Extra fields not allowed due to :closed true
      (is (invalid? sessions/ListLoginsResponse
                    {:logins []
                     :extra-field "not allowed"}))

      ;; Invalid logins structure (should be a vector, not a single Login map)
      (is (invalid? sessions/ListLoginsResponse
                    {:logins {:login_time 1763768830001}}))

      ;; Invalid logins structure (should be a vector, not a string)
      (is (invalid? sessions/ListLoginsResponse
                    {:logins "not a vector"}))

      ;; Invalid Login data in vector
      (is (invalid? sessions/ListLoginsResponse
                    {:logins [{:login_time "not a number"}]}))

      ;; Vector with invalid Login (missing login_time)
      (is (invalid? sessions/ListLoginsResponse
                    {:logins [{:ip_address "127.0.0.1"}]})))))

(deftest test-edge-cases
  (testing "Edge cases and boundary conditions"
    (testing "very large timestamps"
      (is (valid? sessions/LoginTimeResponseParam
                  {:login_time 999999999999999}))

      (is (valid? sessions/Login
                  {:login_time 999999999999999
                   :ip_address "255.255.255.255"})))

    (testing "special IP addresses"
      ;; Localhost
      (is (valid? sessions/Login
                  {:login_time 1763768830001
                   :ip_address "127.0.0.1"}))

      ;; Any address
      (is (valid? sessions/Login
                  {:login_time 1763768830001
                   :ip_address "0.0.0.0"}))

      ;; Broadcast
      (is (valid? sessions/Login
                  {:login_time 1763768830001
                   :ip_address "255.255.255.255"}))

      ;; IPv6 localhost
      (is (valid? sessions/Login
                  {:login_time 1763768830001
                   :ip_address "::1"}))

      ;; IPv6 any
      (is (valid? sessions/Login
                  {:login_time 1763768830001
                   :ip_address "::"})))

    (testing "empty strings for IP address"
      ;; Empty string is technically a valid string type in Malli
      ;; unless constrained otherwise
      (is (valid? sessions/IPAddrParam
                  {:ip-address ""}))

      (is (valid? sessions/Login
                  {:login_time 1763768830001
                   :ip_address ""})))))
