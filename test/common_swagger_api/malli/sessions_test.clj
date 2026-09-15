(ns common-swagger-api.malli.sessions-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.sessions :as sessions]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def login-time 1763768830001)

(deftest endpoint-documentation
  (is (= "Record a User Logout" sessions/LogoutSummary))
  (is (re-find #"logged out" sessions/LogoutDocs)))

(deftest IPAddrParam
  (valid sessions/IPAddrParam {} {:ip-address "127.0.0.1"})
  (invalid sessions/IPAddrParam {:ip-address 1} {:ip-address nil} {:extra 1}))

(deftest LoginTimeResponseParam
  (valid sessions/LoginTimeResponseParam {:login_time login-time})
  (invalid sessions/LoginTimeResponseParam {} {:login_time "1763768830001"} {:login_time login-time :extra 1}))

(deftest LoginResponse
  (valid sessions/LoginResponse
         {:login_time login-time :auth_redirect {}}
         {:login_time login-time :auth_redirect {:agave "https://example.org/oauth/callback"}})
  (invalid sessions/LoginResponse
           {:login_time login-time}
           {:auth_redirect {}}
           {:login_time login-time :auth_redirect {:agave 1}}
           {:login_time login-time :auth_redirect {} :extra 1}))

(deftest LogoutParams
  (valid sessions/LogoutParams {:login-time login-time} {:login-time login-time :ip-address "127.0.0.1"})
  (invalid sessions/LogoutParams
           {}
           {:login-time "1763768830001"}
           {:login-time login-time :ip-address 1}
           {:login-time login-time :extra 1}))

(deftest Login
  (valid sessions/Login {:login_time login-time} {:login_time login-time :ip_address "127.0.0.1"})
  (invalid sessions/Login {} {:login_time nil} {:login_time login-time :ip_address 1}
           {:login_time login-time :extra 1}))

(deftest ListLoginsResponse
  (valid sessions/ListLoginsResponse {:logins []} {:logins [{:login_time login-time}]})
  (invalid sessions/ListLoginsResponse {} {:logins [{}]} {:logins [] :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.sessions))
