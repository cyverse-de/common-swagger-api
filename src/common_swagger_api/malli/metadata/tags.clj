(ns common-swagger-api.malli.metadata.tags
  (:require
   [clojure-commons.error-codes :as ce]
   [common-swagger-api.malli :refer [CommonResponses
                                     ErrorResponse
                                     ErrorResponseIllegalArgument
                                     ErrorResponseNotFound
                                     NonBlankString]]
   [common-swagger-api.malli.metadata :refer [DataTypeEnum]]
   [malli.util :as mu]))

(def TagIdPathParam
  [:uuid
   {:description         "The tag's UUID"
    :json-schema/example #uuid "8c0e5a9b-2f34-4f0a-9c1d-7b6e5f4a3c2d"}])

(def TagValueString [:and NonBlankString [:string {:max 255}]])

(def TagSuggestLimit [:and :int [:fn pos?]])

(def GetTagsSummary "List All Attached Tags for a User")
(def GetTagsDescription "This endpoint lists all tags that have been attached to a file or folder by a user.")
(def DeleteTagsSummary "Permanently Delete All Attached Tags for a User")
(def DeleteTagsDescription
  "This endpoint permanently deletes all tag attachments that have been added to a file or folder by a user.")
(def GetAttachedTagSummary "List Attached Tags")
(def GetAttachedTagDescription
  "This endpoint lists the tags of the user that are attached to the indicated file or folder.")
(def PatchTagsSummary "Attach/Detach Tags to a File/Folder")
(def PatchTagsDescription
  (str "Depending on the `type` parameter, this endpoint either attaches a set of the authenticated user's tags to "
       "the indicated file or folder, or it detaches the set."))
(def GetTagSuggestionsSummary "Suggest a Tag")
(def GetTagSuggestionsDescription
  (str "Given a textual fragment of a tag's value, this endpoint will list up to a given number of the authenticated "
       "user's tags that contain the fragment."))
(def GetUserTagsSummary "List Tags Defined by a User")
(def GetUserTagsDescription "This endpoint lists all of the tags defined by a user.")
(def DeleteUserTagsSummary "Delete Tags Defined by a User")
(def DeleteUserTagsDescription
  "This endpoint deletes all tags defined by the current user. Corresponding attached tags will also be deleted.")
(def PostTagSummary "Create a Tag")
(def PostTagDescription "This endpoint creates a tag for use by the authenticated user.")
(def DeleteTagSummary "Delete a Tag")
(def DeleteTagDescription "This endpoint allows a user tag to be deleted, detaching it from all metadata.")
(def PatchTagSummary "Update Tag Labels/Descriptions")
(def PatchTagDescription "This endpoint allows a tag's label and description to be modified by the owning user.")

(def TagTypeEnum
  [:map {:closed true}
   [:type
    {:description         "Whether to attach or detach the provided set of tags to the file/folder"
     :json-schema/example "attach"}
    [:enum "attach" "detach"]]])

(def TagSuggestQueryParams
  [:map {:closed true}
   [:contains
    {:description         "The value fragment"
     :json-schema/example "gen"}
    :string]

   [:limit
    {:optional            true
     :description         "The maximum number of suggestions to return. No limit means return all"
     :json-schema/example 10}
    TagSuggestLimit]])

(def Tag
  [:map {:closed true}
   [:id
    {:description         "The service-provided UUID associated with the tag"
     :json-schema/example #uuid "8c0e5a9b-2f34-4f0a-9c1d-7b6e5f4a3c2d"}
    :uuid]

   [:value
    {:description         "The value used to identify the tag, at most 255 characters in length"
     :json-schema/example "genome"}
    TagValueString]

   [:description
    {:optional            true
     :description         "The description of the purpose of the tag"
     :json-schema/example "Marks data related to genomes"}
    :string]])

(def TagRequest
  (-> Tag
      (mu/dissoc :id)
      (mu/update-properties assoc :description "The user tag to create.")))

(def TagUpdateRequest
  (-> TagRequest
      (mu/optional-keys [:value])
      (mu/update-properties assoc :description "The tag fields to update.")))

(def TagList
  [:map {:closed true}
   [:tags
    {:description "A list of Tags"}
    [:vector Tag]]])

(def TagIdList
  [:map {:closed true}
   [:tags
    {:description         "A list of Tag UUIDs"
     :json-schema/example [#uuid "8c0e5a9b-2f34-4f0a-9c1d-7b6e5f4a3c2d"]}
    [:vector :uuid]]])

(def AttachedTagTarget
  [:map {:closed true}
   [:id
    {:description         "The target's UUID"
     :json-schema/example #uuid "a14dfe49-f65f-418b-b3c5-6497284251fe"}
    :uuid]

   [:type
    {:description         "The target's data type"
     :json-schema/example "file"}
    DataTypeEnum]])

(def TagDetails
  (mu/merge
   Tag
   [:map
    [:owner_id
     {:description         "The owner of the tag"
      :json-schema/example "janedoe"}
     :string]

    [:public
     {:description         "Whether the tag is publicly accessible"
      :json-schema/example false}
     :boolean]

    [:created_on
     {:description         "The date the tag was created in ms since the POSIX epoch"
      :json-schema/example 1757465246000}
     :int]

    [:modified_on
     {:description         "The date the tag was last modified in ms since the POSIX epoch"
      :json-schema/example 1757465251000}
     :int]]))

(def AttachedTagDetails
  (mu/merge
   TagDetails
   [:map
    [:targets
     {:description "A list of targets attached to the tag"}
     [:vector AttachedTagTarget]]]))

(def AttachedTagsListing
  [:map {:closed true}
   [:tags
    {:description "A list of tags and their attached targets"}
    [:vector AttachedTagDetails]]])

;; The key is given with its properties so that the entry properties inherited from ErrorResponse are replaced.
(def ErrorResponseBadTagRequest
  (mu/assoc ErrorResponse
            [:error_code
             {:description         "Bad Tag Request error codes"
              :json-schema/example ce/ERR_ILLEGAL_ARGUMENT}]
            [:enum ce/ERR_ILLEGAL_ARGUMENT ce/ERR_NOT_UNIQUE]))

;; The plumatic namespace spells this map out under its own name; it holds the same entries as CommonResponses.
(def TagDefaultErrorResponses CommonResponses)

(def GetTagsResponses
  (merge {200 {:body        AttachedTagsListing
               :description "Attached tags are listed in the response"}}
         TagDefaultErrorResponses))

(def DeleteTagsResponses
  (merge {200 {:description "The attached tags were successfully deleted"}}
         TagDefaultErrorResponses))

(def DeleteUserTagsResponses DeleteTagsResponses)

(def GetAttachedTagResponses
  (merge {200 {:body        TagList
               :description "The tags are listed in the response"}}
         TagDefaultErrorResponses))

(def GetUserTagsResponses GetAttachedTagResponses)

(def PatchTags400Response
  {:body        ErrorResponseBadTagRequest
   :description (str "The `type` wasn't provided or had a value other than `attach` or `detach`; "
                     "or the request body wasn't syntactically correct")})

(def PatchTags404Response
  {:body        ErrorResponseNotFound
   :description "One of the provided tag Ids doesn't map to a tag for the authenticated user"})

(def GetTagSuggestionsResponses
  (merge {200 {:body        TagList
               :description "zero or more suggestions were returned"}
          400 {:body        ErrorResponseIllegalArgument
               :description (str "the `contains` parameter was missing or the `limit` parameter was set to a "
                                 "something other than a non-negative number.")}}
         TagDefaultErrorResponses))

(def PostTag400Response
  {:body        ErrorResponseBadTagRequest
   :description "The `value` was not unique, too long, or the request body wasn't syntactically correct"})

(def DeleteTagResponses
  (merge {200 {:description "The tag was successfully deleted"}
          404 {:body        ErrorResponseNotFound
               :description "`tag-id` wasn't a UUID of a tag owned by the authenticated user"}}
         TagDefaultErrorResponses))
