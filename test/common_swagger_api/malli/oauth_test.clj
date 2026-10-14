(ns common-swagger-api.malli.oauth-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.oauth :as oauth]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]
   [malli.json-schema :as js]))

(deftest RedirectUrisResponse
  (valid oauth/RedirectUrisResponse {} {:agave "https://x"})
  (invalid oauth/RedirectUrisResponse {:agave 1} {"agave" "https://x"})
  (is (= [:api-name] (keys (:properties (js/transform oauth/RedirectUrisResponse))))))

(deftest OAuthCallbackQueryParams
  (valid oauth/OAuthCallbackQueryParams {:code "c" :state "s"})
  (invalid oauth/OAuthCallbackQueryParams {} {:code "" :state "s"} {:code "c" :state "s" :extra 1}))

(deftest TokenInfoProxyParams
  (valid oauth/TokenInfoProxyParams {} {:proxy-user "u"})
  (invalid oauth/TokenInfoProxyParams {:proxy-user ""} {:other 1}))

(deftest OAuthCallbackResponse
  (valid oauth/OAuthCallbackResponse {:state_info "s"})
  (invalid oauth/OAuthCallbackResponse {} {:state_info 1}))

(def token-info
  {:access_token "a" :expires_at 1735689600000 :refresh_token "r" :webapp "agave"})

(deftest AdminTokenInfo
  (valid oauth/AdminTokenInfo token-info)
  (invalid oauth/AdminTokenInfo (dissoc token-info :webapp) (assoc token-info :expires_at "x")))

(deftest TokenInfo
  (valid oauth/TokenInfo (select-keys token-info [:expires_at :webapp]))
  (invalid oauth/TokenInfo token-info {:webapp "agave"}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.oauth))

(deftest examples
  (examples-valid 'common-swagger-api.malli.oauth))
