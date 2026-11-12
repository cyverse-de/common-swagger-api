(ns common-swagger-api.malli.metadata.comments-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.metadata.comments :as comments]
   [common-swagger-api.malli.test-util :refer [examples-valid invalid json-schema-ok valid]]))

(def comment-id #uuid "4a1d5f6c-3f9b-4b1e-9a2f-5d6c7e8f9a0b")
(def target-id #uuid "a14dfe49-f65f-418b-b3c5-6497284251fe")

(def a-comment
  {:id        comment-id
   :commenter "janedoe"
   :post_time 1757465246000
   :retracted false
   :comment   "This looks like the wrong reference genome."})

(def comment-details
  (assoc a-comment :deleted false :target_id target-id :target_type "file"))

(deftest CommenterId
  (valid comments/CommenterId "janedoe")
  (invalid comments/CommenterId 1))

(deftest CommentIdPathParam
  (valid comments/CommentIdPathParam comment-id)
  (invalid comments/CommentIdPathParam (str comment-id)))

(deftest CommentTextParam
  (valid comments/CommentTextParam "a comment")
  (invalid comments/CommentTextParam "   "))

(deftest RetractCommentQueryParams
  (valid comments/RetractCommentQueryParams {:user "ipctest" :retracted true})
  (invalid comments/RetractCommentQueryParams
           {}
           {:user "ipctest"}
           {:retracted false}
           {:user "ipctest" :retracted "true"}
           {:user "ipctest" :retracted true :extra 1}))

(deftest Comment
  (valid comments/Comment a-comment (assoc a-comment :retracted true))
  (invalid comments/Comment
           {}
           (dissoc a-comment :post_time)
           (assoc a-comment :post_time "1757465246000")
           (assoc a-comment :comment "")
           (assoc a-comment :extra 1)))

(deftest CommentList
  (valid comments/CommentList {:comments []} {:comments [a-comment]})
  (invalid comments/CommentList
           {}
           {:comments a-comment}
           {:comments [(dissoc a-comment :id)]}
           {:comments [] :extra 1}))

(deftest CommentRequest
  (valid comments/CommentRequest {:comment "a comment"})
  (invalid comments/CommentRequest
           {}
           {:comment ""}
           {:comment 1}
           {:comment "a comment" :extra 1}))

(deftest CommentResponse
  (valid comments/CommentResponse {:comment a-comment})
  (invalid comments/CommentResponse
           {}
           {:comment "a comment"}
           {:comment (dissoc a-comment :commenter)}
           {:comment a-comment :extra 1}))

(deftest CommentDetails
  (valid comments/CommentDetails comment-details (assoc comment-details :retracted_by "janedoe"))
  (invalid comments/CommentDetails
           a-comment
           (dissoc comment-details :deleted)
           (assoc comment-details :target_id (str target-id))
           (assoc comment-details :retracted_by 1)
           (assoc comment-details :extra 1)))

(deftest CommentDetailsList
  (valid comments/CommentDetailsList {:comments []} {:comments [comment-details]})
  (invalid comments/CommentDetailsList
           {}
           {:comments comment-details}
           {:comments [a-comment]}
           {:comments [] :extra 1}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.metadata.comments))

(deftest examples
  (examples-valid 'common-swagger-api.malli.metadata.comments))
