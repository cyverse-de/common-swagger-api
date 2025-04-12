(ns common-swagger-api.schema.apps.rating
  (:require [common-swagger-api.schema :refer [describe]]
            [schema.core :refer [defschema optional-key]]))

(def UserRatingParam (describe Long "The current user's rating for this App"))
(def CommentIdParam (describe Long "The ID of the current user's rating comment for this App"))

(defschema RatingResponse
  {:average (describe Double "The average user rating for this App")
   :total   (describe Long "The total number of user ratings for this App")})

(defschema Rating
  (merge RatingResponse
         {(optional-key :user)       UserRatingParam
          (optional-key :comment_id) CommentIdParam}))

(defschema RatingRequest
  (-> {:rating                    UserRatingParam
       (optional-key :comment_id) CommentIdParam}
      (describe "The user's new rating for this App.")))
