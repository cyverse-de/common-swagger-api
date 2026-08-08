(ns common-swagger-api.malli.sessions
  (:require
   [common-swagger-api.malli.oauth :refer [RedirectUrisResponse]]
   [malli.util :as mu]))

(def LogoutSummary "Record a User Logout")
(def LogoutDocs "This service records the fact that the user logged out.")

(def IPAddrParam
  (mu/closed-schema
   [:map
    [:ip-address
     {:optional            true
      :description         "The IP address of the requesting user, for matching login to logout requests."
      :json-schema/example "127.0.0.1"}
     :string]]))

(def LoginTimeResponseParam
  (mu/closed-schema
   [:map
    [:login_time
     {:description         "Login time as milliseconds since the epoch."
      :json-schema/example 1763768830001}
     :int]]))

(def LoginResponse
  (mu/closed-schema
   (mu/merge
    LoginTimeResponseParam
    [:map
     [:auth_redirect RedirectUrisResponse]])))

(def LogoutParams
  (mu/closed-schema
   (mu/merge
    IPAddrParam
    [:map
     [:login-time
      {:description         "The login time returned by `POST /users/login` or `/bootstrap`."
       :json-schema/example 1763768830001}
      :int]])))

(def Login
  (mu/closed-schema
   (mu/merge
    LoginTimeResponseParam
    [:map
     [:ip_address
      {:optional            true
       :description         "The IP address associated with the login, if available"
       :json-schema/example "127.0.0.1"}
      :string]])))

(def ListLoginsResponse
  (mu/closed-schema
   [:map
    [:logins
     {:description "The set of most recent logins up to the limit"}
     [:vector Login]]]))
