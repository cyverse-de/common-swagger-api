(ns common-swagger-api.malli.subjects
  (:require [malli.util :as mu]))

(def BaseSubject
  [:map {:closed true}
   [:id
    {:description         "The subject identifier"
     :json-schema/example "user123"}
    :string]

   [:source_id
    {:description         "The ID of the source of the subject information"
     :json-schema/example "ldap"}
    :string]])

(def Subject
  (mu/merge
   BaseSubject
   [:map {:closed true}
    [:name
     {:optional            true
      :description         "The subject name"
      :json-schema/example "John Doe"}
     :string]

    [:first_name
     {:optional            true
      :description         "The subject's first name"
      :json-schema/example "John"}
     :string]

    [:last_name
     {:optional            true
      :description         "The subject's last name"
      :json-schema/example "Doe"}
     :string]

    [:email
     {:optional            true
      :description         "The subject email"
      :json-schema/example "john.doe@example.com"}
     :string]

    [:institution
     {:optional            true
      :description         "The subject institution"
      :json-schema/example "University of Example"}
     :string]

    [:attribute_values
     {:optional            true
      :description         "A list of additional attributes applied to the subject"}
     [:vector :string]]

    [:description
     {:optional            true
      :description         "The subject description"
      :json-schema/example "A researcher"}
     :string]]))

(def SubjectList
  [:map {:closed true}
   [:subjects
    {:description "The list of subjects in the result set"}
    [:vector Subject]]])

(def SubjectIdList
  [:map {:closed true}
   [:subject_ids
    {:description         "The list of subject IDs"
     :json-schema/example ["user123" "user456"]}
    [:vector :string]]])
