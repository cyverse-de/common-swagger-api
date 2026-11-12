(ns common-swagger-api.malli.metadata.tags-test
  (:require
   [clojure.string :as string]
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.metadata.tags :as tags]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]
   [malli.json-schema :as js]))

(def tag-id #uuid "8c0e5a9b-2f34-4f0a-9c1d-7b6e5f4a3c2d")
(def target-id #uuid "a14dfe49-f65f-418b-b3c5-6497284251fe")

(def tag {:id tag-id :value "genome" :description "Marks data related to genomes"})

(def target {:id target-id :type "file"})

(def tag-details
  (assoc tag :owner_id "janedoe" :public false :created_on 1757465246000 :modified_on 1757465251000))

(def attached-tag-details (assoc tag-details :targets [target]))

(deftest TagIdPathParam
  (valid tags/TagIdPathParam tag-id)
  (invalid tags/TagIdPathParam (str tag-id)))

(deftest TagValueString
  (valid tags/TagValueString "genome" (string/join (repeat 255 "x")))
  (invalid tags/TagValueString "   " (string/join (repeat 256 "x"))))

(deftest TagSuggestLimit
  (valid tags/TagSuggestLimit 1)
  (invalid tags/TagSuggestLimit 0 -1 "1"))

(deftest TagTypeEnum
  (valid tags/TagTypeEnum {:type "attach"} {:type "detach"})
  (invalid tags/TagTypeEnum {} {:type "remove"} {:type :attach} {:type "attach" :extra 1}))

(deftest TagSuggestQueryParams
  (valid tags/TagSuggestQueryParams {:contains "gen"} {:contains "gen" :limit 10})
  (invalid tags/TagSuggestQueryParams
           {}
           {:limit 10}
           {:contains 1}
           {:contains "gen" :limit 0}
           {:contains "gen" :extra 1}))

(deftest Tag
  (valid tags/Tag tag (dissoc tag :description))
  (invalid tags/Tag
           {}
           (dissoc tag :value)
           (assoc tag :id (str tag-id))
           (assoc tag :value "")
           (assoc tag :extra 1)))

(deftest TagRequest
  (valid tags/TagRequest (dissoc tag :id) {:value "genome"})
  (invalid tags/TagRequest
           {}
           tag
           {:value 1}
           {:value "genome" :extra 1}))

(deftest TagUpdateRequest
  (valid tags/TagUpdateRequest {} {:value "genome"} {:description "d"} (dissoc tag :id))
  (invalid tags/TagUpdateRequest tag {:value ""} {:extra 1}))

(deftest TagList
  (valid tags/TagList {:tags []} {:tags [tag]})
  (invalid tags/TagList
           {}
           {:tags tag}
           {:tags [(dissoc tag :id)]}
           {:tags [] :extra 1}))

(deftest TagIdList
  (valid tags/TagIdList {:tags []} {:tags [tag-id]})
  (invalid tags/TagIdList
           {}
           {:tags tag-id}
           {:tags [(str tag-id)]}
           {:tags [] :extra 1}))

(deftest AttachedTagTarget
  (valid tags/AttachedTagTarget target (assoc target :type "folder"))
  (invalid tags/AttachedTagTarget
           {}
           (dissoc target :type)
           (assoc target :type "data-object")
           (assoc target :extra 1)))

(deftest TagDetails
  (valid tags/TagDetails tag-details (dissoc tag-details :description))
  (invalid tags/TagDetails
           tag
           (dissoc tag-details :public)
           (assoc tag-details :created_on "1757465246000")
           (assoc tag-details :extra 1)))

(deftest AttachedTagDetails
  (valid tags/AttachedTagDetails attached-tag-details (assoc attached-tag-details :targets []))
  (invalid tags/AttachedTagDetails
           tag-details
           (assoc attached-tag-details :targets target)
           (assoc attached-tag-details :targets [(dissoc target :id)])
           (assoc attached-tag-details :extra 1)))

(deftest AttachedTagsListing
  (valid tags/AttachedTagsListing {:tags []} {:tags [attached-tag-details]})
  (invalid tags/AttachedTagsListing
           {}
           {:tags attached-tag-details}
           {:tags [tag-details]}
           {:tags [] :extra 1}))

(deftest ErrorResponseBadTagRequest
  (valid tags/ErrorResponseBadTagRequest
         {:error_code "ERR_ILLEGAL_ARGUMENT"}
         {:error_code "ERR_NOT_UNIQUE" :reason "That tag already exists"})
  (invalid tags/ErrorResponseBadTagRequest
           {}
           {:error_code "ERR_NOT_FOUND"}
           {:error_code "ERR_ILLEGAL_ARGUMENT" :extra 1})
  (is (= {:description "Bad Tag Request error codes"
          :type        "string"
          :enum        ["ERR_ILLEGAL_ARGUMENT" "ERR_NOT_UNIQUE"]
          :example     "ERR_ILLEGAL_ARGUMENT"}
         (get-in (js/transform tags/ErrorResponseBadTagRequest) [:properties :error_code]))))

(deftest TagDefaultErrorResponses
  (is (= #{500 :default} (set (keys tags/TagDefaultErrorResponses))))
  (valid (get-in tags/TagDefaultErrorResponses [500 :body]) {:error_code "ERR_UNCHECKED_EXCEPTION"})
  (invalid (get-in tags/TagDefaultErrorResponses [500 :body]) {:error_code "ERR_NOT_FOUND"}))

(deftest GetTagsResponses
  (is (= #{200 500 :default} (set (keys tags/GetTagsResponses))))
  (valid (get-in tags/GetTagsResponses [200 :body]) {:tags [attached-tag-details]})
  (invalid (get-in tags/GetTagsResponses [200 :body]) {:tags [tag-details]}))

(deftest DeleteTagsResponses
  (is (= #{200 500 :default} (set (keys tags/DeleteTagsResponses))))
  (is (= #{:description} (set (keys (get tags/DeleteTagsResponses 200))))))

(deftest DeleteUserTagsResponses
  (is (= tags/DeleteTagsResponses tags/DeleteUserTagsResponses)))

(deftest GetAttachedTagResponses
  (is (= #{200 500 :default} (set (keys tags/GetAttachedTagResponses))))
  (valid (get-in tags/GetAttachedTagResponses [200 :body]) {:tags [tag]})
  (invalid (get-in tags/GetAttachedTagResponses [200 :body]) {:tags [attached-tag-details]}))

(deftest GetUserTagsResponses
  (is (= tags/GetAttachedTagResponses tags/GetUserTagsResponses)))

(deftest PatchTags400Response
  (is (= #{:body :description} (set (keys tags/PatchTags400Response))))
  (valid (:body tags/PatchTags400Response) {:error_code "ERR_NOT_UNIQUE"})
  (invalid (:body tags/PatchTags400Response) {:error_code "ERR_NOT_FOUND"}))

(deftest PatchTags404Response
  (is (= #{:body :description} (set (keys tags/PatchTags404Response))))
  (valid (:body tags/PatchTags404Response) {:error_code "ERR_NOT_FOUND"})
  (invalid (:body tags/PatchTags404Response) {:error_code "ERR_NOT_UNIQUE"}))

(deftest GetTagSuggestionsResponses
  (is (= #{200 400 500 :default} (set (keys tags/GetTagSuggestionsResponses))))
  (valid (get-in tags/GetTagSuggestionsResponses [200 :body]) {:tags [tag]})
  (valid (get-in tags/GetTagSuggestionsResponses [400 :body]) {:error_code "ERR_ILLEGAL_ARGUMENT"})
  (invalid (get-in tags/GetTagSuggestionsResponses [400 :body]) {:error_code "ERR_NOT_UNIQUE"}))

(deftest PostTag400Response
  (is (= #{:body :description} (set (keys tags/PostTag400Response))))
  (valid (:body tags/PostTag400Response) {:error_code "ERR_ILLEGAL_ARGUMENT"})
  (invalid (:body tags/PostTag400Response) {:error_code "ERR_UNCHECKED_EXCEPTION"}))

(deftest DeleteTagResponses
  (is (= #{200 404 500 :default} (set (keys tags/DeleteTagResponses))))
  (is (= #{:description} (set (keys (get tags/DeleteTagResponses 200)))))
  (valid (get-in tags/DeleteTagResponses [404 :body]) {:error_code "ERR_NOT_FOUND"})
  (invalid (get-in tags/DeleteTagResponses [404 :body]) {:error_code "ERR_NOT_UNIQUE"}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.metadata.tags))

(deftest examples
  (examples-valid 'common-swagger-api.malli.metadata.tags))
