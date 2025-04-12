(ns common-swagger-api.schema.subjects
  (:require [common-swagger-api.schema :refer [describe]]
            [schema.core :as s]))

(s/defschema BaseSubject
  {:id        (describe String "The subject identifier")
   :source_id (describe String "The ID of the source of the subject information")})

(s/defschema Subject
  (assoc BaseSubject
    (s/optional-key :name)
    (describe String "The subject name")

    (s/optional-key :first_name)
    (describe String "The subject's first name")

    (s/optional-key :last_name)
    (describe String "The subject's last name")

    (s/optional-key :email)
    (describe String "The subject email")

    (s/optional-key :institution)
    (describe String "The subject institution")

    (s/optional-key :attribute_values)
    (describe [String] "A list of additional attributes applied to the subject")

    (s/optional-key :description)
    (describe String "The subject description")))

(s/defschema SubjectList
  {:subjects (describe [Subject] "The list of subjects in the result set")})

(s/defschema SubjectIdList
  {:subject_ids (describe [String] "The list of subject IDs")})
