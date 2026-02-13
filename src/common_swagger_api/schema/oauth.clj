(ns common-swagger-api.schema.oauth
  (:require
   [common-swagger-api.schema :refer [describe doc-only NonBlankString]]
   [schema.core :as s]))

(def ApiName (describe String "The name of the external API"))

(def GetAccessCodeSummary "Obtain an OAuth access token for an authorization code")
(def GetAccessCodeDescription
  "Exchanges an OAuth authorization code for an access token and stores it for the authenticated user.
   This endpoint is called as part of the OAuth callback flow.")

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

(s/defschema RedirectUris
  {(describe s/Keyword "The name of the API")
   (describe String "The redirect URI")})

(s/defschema RedirectUrisDoc
  {:api-name (describe String "The redirect URI.")})

(def RedirectUrisResponse (doc-only RedirectUris RedirectUrisDoc))

(s/defschema OAuthCallbackQueryParams
  {:code  (describe NonBlankString "The authorization code used to obtain the access token.")
   :state (describe NonBlankString "The authorization state information.")})

(s/defschema TokenInfoProxyParams
  {(s/optional-key :proxy-user)
   (describe NonBlankString "The name of the proxy user for admin service calls.")})

(s/defschema OAuthCallbackResponse
  {:state_info (describe String "Arbitrary state information required by the UI.")})

(s/defschema AdminTokenInfo
  {:access_token  (describe String "The access token itself.")
   :expires_at    (describe Long "The token expiration time as milliseconds since the epoch.")
   :refresh_token (describe String "The refresh token to use when the access token expires.")
   :webapp        (describe String "The name of the external web application.")})

(s/defschema TokenInfo
  (select-keys AdminTokenInfo [:expires_at :webapp]))
