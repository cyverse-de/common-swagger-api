(ns common-swagger-api.malli.apps.rating
  (:require [malli.util :as mu]))

(defn UserRatingParam
  "Returns a user rating field definition with the given field name"
  [field-name & [optional?]]
  [field-name
   (cond-> {:description         "The current user's rating for this App"
            :json-schema/example 5}
     optional? (assoc :optional true))
   :int])

(defn CommentIdParam
  "Returns a comment ID field definition with the given field name"
  [field-name & [optional?]]
  [field-name
   (cond-> {:description         "The ID of the current user's rating comment for this App"
            :json-schema/example 123}
     optional? (assoc :optional true))
   :int])

(def RatingResponse
  [:map {:closed true}
   [:average
    {:description         "The average user rating for this App"
     :json-schema/example 4.5}
    :double]

   [:total
    {:description         "The total number of user ratings for this App"
     :json-schema/example 42}
    :int]])

(def Rating
  (mu/merge RatingResponse
    [:map
     (UserRatingParam :user true)
     (CommentIdParam :comment_id true)]))

(def RatingRequest
  [:map {:closed true
         :description "The user's new rating for this App."}
   (UserRatingParam :rating)
   (CommentIdParam :comment_id true)])
