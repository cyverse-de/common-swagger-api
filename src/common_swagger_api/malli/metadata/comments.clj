(ns common-swagger-api.malli.metadata.comments
  (:require
   [common-swagger-api.malli :refer [NonBlankString StandardUserQueryParams]]
   [malli.util :as mu]))

(def CommenterId
  [:string
   {:description         "The username of the person who added the comment."
    :json-schema/example "janedoe"}])

(def CommentIdPathParam
  [:uuid
   {:description         "The comment's UUID"
    :json-schema/example #uuid "4a1d5f6c-3f9b-4b1e-9a2f-5d6c7e8f9a0b"}])

(def CommentTextParam
  (mu/update-properties
   NonBlankString
   merge
   {:description         "The text of the comment"
    :json-schema/example "This looks like the wrong reference genome."}))

(def RetractCommentQueryParams
  (mu/merge
   StandardUserQueryParams
   [:map
    [:retracted
     {:description         "Whether to retract the comment (`true`) or readmit a retraction (`false`)"
      :json-schema/example true}
     :boolean]]))

(def Comment
  [:map {:closed true}
   [:id
    {:description         "The service-provided UUID associated with the comment"
     :json-schema/example #uuid "4a1d5f6c-3f9b-4b1e-9a2f-5d6c7e8f9a0b"}
    :uuid]

   [:commenter
    {:description         "The authenticated username of the person who made the comment"
     :json-schema/example "janedoe"}
    NonBlankString]

   [:post_time
    {:description         "The time when the comment was posted in ms since the POSIX epoch"
     :json-schema/example 1757465246000}
    :int]

   [:retracted
    {:description         "A flag indicating whether or not the comment is currently retracted"
     :json-schema/example false}
    :boolean]

   [:comment CommentTextParam]])

(def CommentList
  [:map {:closed true}
   [:comments
    {:description "A list of comments"}
    [:vector Comment]]])

(def CommentRequest
  [:map {:closed true}
   [:comment CommentTextParam]])

(def CommentResponse
  [:map {:closed true}
   [:comment Comment]])

(def CommentDetails
  (mu/merge
   Comment
   [:map
    [:retracted_by
     {:optional            true
      :description         "The username of the person who retracted the comment"
      :json-schema/example "janedoe"}
     :string]

    [:deleted
     {:description         "True if the comment has been deleted"
      :json-schema/example false}
     :boolean]

    [:target_id
     {:description         "The identifier of the target the comment is attached to"
      :json-schema/example #uuid "a14dfe49-f65f-418b-b3c5-6497284251fe"}
     :uuid]

    [:target_type
     {:description         "The type of target the comment is attached to"
      :json-schema/example "file"}
     :string]]))

(def CommentDetailsList
  [:map {:closed true}
   [:comments
    {:description "A list of comment details"}
    [:vector CommentDetails]]])
