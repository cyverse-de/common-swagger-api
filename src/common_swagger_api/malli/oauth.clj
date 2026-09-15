(ns common-swagger-api.malli.oauth
  (:require
   [common-swagger-api.malli :refer [doc-only NonBlankString]]
   [malli.util :as mu]))

(def ApiName
  [:string {:description         "The name of the external API"
            :json-schema/example "agave"}])

(def GetAccessCodeSummary "Obtain an OAuth access token for an authorization code")
(def GetAccessCodeDescription
  (str "Exchanges an OAuth authorization code for an access token and stores it for the authenticated user. "
       "This endpoint is called as part of the OAuth callback flow."))

(def GetRedirectUrisSummary "List OAuth redirect URIs")
(def GetRedirectUrisDescription
  "Returns a set of OAuth redirect URIs if the user hasn't authenticated with the remote API yet.")

(def GetTokenInfoSummary "Get OAuth token info")
(def GetTokenInfoDescription
  "Returns information about an OAuth access token, not including the token itself.")

(def DeleteTokenInfoSummary "Delete OAuth token info")
(def DeleteTokenInfoDescription
  "Removes a user's OAuth access token from the DE.")

(def AdminGetTokenInfoSummary "Get OAuth token info for a user")
(def AdminGetTokenInfoDescription
  "Returns information about an OAuth access token for administrative troubleshooting.")

(def AdminDeleteTokenInfoSummary "Delete OAuth token info for a user")
(def AdminDeleteTokenInfoDescription
  "Removes a user's OAuth access token from the DE for administrative purposes.")

(def RedirectUris
  [:map-of
   [:keyword
    {:description         "The name of the API"
     :json-schema/example :agave}]
   [:string
    {:description         "The redirect URI"
     :json-schema/example "https://example.org/oauth/callback"}]])

(def RedirectUrisDoc
  [:map {:closed true}
   [:api-name
    {:description         "The redirect URI."
     :json-schema/example "https://example.org/oauth/callback"}
    :string]])

(def RedirectUrisResponse (doc-only RedirectUris RedirectUrisDoc))

(def OAuthCallbackQueryParams
  [:map {:closed true}
   [:code
    {:description         "The authorization code used to obtain the access token."
     :json-schema/example "SplxlOBeZQQYbYS6WxSbIA"}
    NonBlankString]

   [:state
    {:description         "The authorization state information."
     :json-schema/example "af0ifjsldkj"}
    NonBlankString]])

(def TokenInfoProxyParams
  [:map {:closed true}
   [:proxy-user
    {:optional            true
     :description         "The name of the proxy user for admin service calls."
     :json-schema/example "ipctest"}
    NonBlankString]])

(def OAuthCallbackResponse
  [:map {:closed true}
   [:state_info
    {:description         "Arbitrary state information required by the UI."
     :json-schema/example "/data"}
    :string]])

(def AdminTokenInfo
  [:map {:closed true}
   [:access_token
    {:description         "The access token itself."
     :json-schema/example "2YotnFZFEjr1zCsicMWpAA"}
    :string]

   [:expires_at
    {:description         "The token expiration time as milliseconds since the epoch."
     :json-schema/example 1735689600000}
    :int]

   [:refresh_token
    {:description         "The refresh token to use when the access token expires."
     :json-schema/example "tGzv3JOkF0XG5Qx2TlKWIA"}
    :string]

   [:webapp
    {:description         "The name of the external web application."
     :json-schema/example "agave"}
    :string]])

(def TokenInfo (mu/select-keys AdminTokenInfo [:expires_at :webapp]))
