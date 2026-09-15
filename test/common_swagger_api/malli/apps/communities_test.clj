(ns common-swagger-api.malli.apps.communities-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.apps.communities :as communities]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def community-id "1a0b4e0c5d2f4e8ba4f9c3d2e1b0a987")

(def avu {:attr "cyverse-community" :value "Example Community" :unit ""})

(deftest CommunityIdPathParam
  (valid communities/CommunityIdPathParam community-id)
  (invalid communities/CommunityIdPathParam "" " " nil))

(deftest AppCommunityListRequest
  (valid communities/AppCommunityListRequest
         {}
         {:avus [avu]}
         {:community_ids [community-id]}
         {:avus [avu] :community_ids [community-id]})
  (invalid communities/AppCommunityListRequest
           {:avus avu}
           {:community_ids [""]}
           {:community_ids community-id}
           {:community_ids [community-id] :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps.communities))
