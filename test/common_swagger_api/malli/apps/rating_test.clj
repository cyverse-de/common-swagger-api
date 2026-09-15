(ns common-swagger-api.malli.apps.rating-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.apps.rating :as rating]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def rating-response {:average 4.5 :total 42})

(deftest UserRatingParam
  (valid [:map {:closed true} (rating/UserRatingParam :user)] {:user 5})
  (valid [:map {:closed true} (rating/UserRatingParam :user true)] {} {:user 5})
  (invalid [:map {:closed true} (rating/UserRatingParam :user)] {} {:user 5.0} {:user "5"}))

(deftest CommentIdParam
  (valid [:map {:closed true} (rating/CommentIdParam :comment_id)] {:comment_id 123})
  (valid [:map {:closed true} (rating/CommentIdParam :comment_id true)] {} {:comment_id 123})
  (invalid [:map {:closed true} (rating/CommentIdParam :comment_id)] {} {:comment_id "123"}))

(deftest RatingResponse
  (valid rating/RatingResponse rating-response)
  (invalid rating/RatingResponse
           {}
           (dissoc rating-response :total)
           (assoc rating-response :average 4)
           (assoc rating-response :extra 1)))

(deftest Rating
  (valid rating/Rating rating-response (assoc rating-response :user 5 :comment_id 123))
  (invalid rating/Rating
           {}
           (dissoc rating-response :average)
           (assoc rating-response :user "5")
           (assoc rating-response :extra 1)))

(deftest RatingRequest
  (valid rating/RatingRequest {:rating 5} {:rating 5 :comment_id 123})
  (invalid rating/RatingRequest {} {:rating "5"} {:rating 5 :comment_id 1.5} {:rating 5 :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps.rating))
