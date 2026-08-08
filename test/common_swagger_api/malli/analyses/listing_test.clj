(ns common-swagger-api.malli.analyses.listing-test
  (:require
   [clojure.test :refer [are deftest is testing]]
   [common-swagger-api.malli.analyses.listing :as listing]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AnalysisListingParams
  (testing "AnalysisListingParams validation"
    (testing "valid params - empty map"
      ;; All fields are optional, so an empty map should be valid
      (is (valid? listing/AnalysisListingParams {})))

    (testing "valid params - with paging parameters"
      ;; Test with limit only
      (is (valid? listing/AnalysisListingParams
                  {:limit 100}))

      ;; Test with offset only
      (is (valid? listing/AnalysisListingParams
                  {:offset 0}))

      ;; Test with both limit and offset
      (is (valid? listing/AnalysisListingParams
                  {:limit 50
                   :offset 100}))

      ;; Test with sort-field only
      (is (valid? listing/AnalysisListingParams
                  {:sort-field "name"}))

      ;; Test with sort-field and sort-dir
      (is (valid? listing/AnalysisListingParams
                  {:sort-field "name"
                   :sort-dir "ASC"}))

      (is (valid? listing/AnalysisListingParams
                  {:sort-field "start_date"
                   :sort-dir "DESC"}))

      ;; Test with all paging parameters
      (is (valid? listing/AnalysisListingParams
                  {:limit 25
                   :offset 50
                   :sort-field "id"
                   :sort-dir "ASC"})))

    (testing "valid params - with include-hidden parameter"
      ;; Test with include-hidden true
      (is (valid? listing/AnalysisListingParams
                  {:include-hidden true}))

      ;; Test with include-hidden false
      (is (valid? listing/AnalysisListingParams
                  {:include-hidden false}))

      ;; Test combined with paging
      (is (valid? listing/AnalysisListingParams
                  {:include-hidden true
                   :limit 50
                   :offset 0})))

    (testing "valid params - with include-deleted parameter"
      ;; Test with include-deleted true
      (is (valid? listing/AnalysisListingParams
                  {:include-deleted true}))

      ;; Test with include-deleted false
      (is (valid? listing/AnalysisListingParams
                  {:include-deleted false}))

      ;; Test combined with paging
      (is (valid? listing/AnalysisListingParams
                  {:include-deleted true
                   :limit 100})))

    (testing "valid params - with both include parameters"
      ;; Test with both include-hidden and include-deleted
      (is (valid? listing/AnalysisListingParams
                  {:include-hidden true
                   :include-deleted true}))

      (is (valid? listing/AnalysisListingParams
                  {:include-hidden false
                   :include-deleted true}))

      (is (valid? listing/AnalysisListingParams
                  {:include-hidden true
                   :include-deleted false}))

      (is (valid? listing/AnalysisListingParams
                  {:include-hidden false
                   :include-deleted false})))

    (testing "valid params - with filter parameter"
      ;; Test with simple filter string
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"id\",\"value\":\"C09F5907-B2A2-4429-A11E-5B96F421C3C1\"}]"}))

      ;; Test with name filter
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"name\",\"value\":\"test\"}]"}))

      ;; Test with app_name filter
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"app_name\",\"value\":\"cace\"}]"}))

      ;; Test with parent_id filter
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"parent_id\",\"value\":\"b4c2f624-7cbd-496e-adad-5be8d0d3b941\"}]"}))

      ;; Test with null parent_id filter
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"parent_id\",\"value\":null}]"}))

      ;; Test with ownership filter
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"ownership\",\"value\":\"mine\"}]"}))

      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"ownership\",\"value\":\"theirs\"}]"}))

      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"ownership\",\"value\":\"all\"}]"}))

      ;; Test with multiple filters
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"name\",\"value\":\"test\"},{\"field\":\"app_name\",\"value\":\"app\"}]"}))

      ;; Test with empty string filter (still valid as it's just a string)
      (is (valid? listing/AnalysisListingParams
                  {:filter ""}))

      ;; Test filter combined with other parameters
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"name\",\"value\":\"test\"}]"
                   :limit 50
                   :offset 0
                   :include-hidden true
                   :include-deleted false
                   :sort-field "name"
                   :sort-dir "ASC"})))

    (testing "valid params - comprehensive test with all parameters"
      ;; Test with all possible parameters
      (is (valid? listing/AnalysisListingParams
                  {:limit 100
                   :offset 200
                   :sort-field "start_date"
                   :sort-dir "DESC"
                   :include-hidden true
                   :include-deleted true
                   :filter "[{\"field\":\"name\",\"value\":\"analysis\"}]"})))

    (testing "invalid params - wrong limit type"
      ;; Limit must be a positive integer
      (is (not (valid? listing/AnalysisListingParams
                       {:limit "not-a-number"})))

      (is (not (valid? listing/AnalysisListingParams
                       {:limit -1})))

      (is (not (valid? listing/AnalysisListingParams
                       {:limit 0})))

      (is (not (valid? listing/AnalysisListingParams
                       {:limit 3.14})))

      (is (not (valid? listing/AnalysisListingParams
                       {:limit nil}))))

    (testing "invalid params - wrong offset type"
      ;; Offset must be a non-negative integer
      (is (not (valid? listing/AnalysisListingParams
                       {:offset "not-a-number"})))

      (is (not (valid? listing/AnalysisListingParams
                       {:offset -1})))

      (is (not (valid? listing/AnalysisListingParams
                       {:offset 3.14})))

      (is (not (valid? listing/AnalysisListingParams
                       {:offset nil}))))

    (testing "invalid params - wrong sort-dir value"
      ;; Sort-dir must be either "ASC" or "DESC"
      (is (not (valid? listing/AnalysisListingParams
                       {:sort-field "name"
                        :sort-dir "asc"})))

      (is (not (valid? listing/AnalysisListingParams
                       {:sort-field "name"
                        :sort-dir "ascending"})))

      (is (not (valid? listing/AnalysisListingParams
                       {:sort-field "name"
                        :sort-dir "desc"})))

      (is (not (valid? listing/AnalysisListingParams
                       {:sort-field "name"
                        :sort-dir "DESCENDING"})))

      (is (not (valid? listing/AnalysisListingParams
                       {:sort-field "name"
                        :sort-dir 1})))

      (is (not (valid? listing/AnalysisListingParams
                       {:sort-field "name"
                        :sort-dir nil}))))

    (testing "invalid params - wrong sort-field type"
      ;; Sort-field must be a string
      (is (not (valid? listing/AnalysisListingParams
                       {:sort-field 123})))

      (is (not (valid? listing/AnalysisListingParams
                       {:sort-field nil})))

      (is (not (valid? listing/AnalysisListingParams
                       {:sort-field true}))))

    (testing "invalid params - wrong include-hidden type"
      ;; Include-hidden must be a boolean
      (is (not (valid? listing/AnalysisListingParams
                       {:include-hidden "true"})))

      (is (not (valid? listing/AnalysisListingParams
                       {:include-hidden 1})))

      (is (not (valid? listing/AnalysisListingParams
                       {:include-hidden "yes"})))

      (is (not (valid? listing/AnalysisListingParams
                       {:include-hidden nil}))))

    (testing "invalid params - wrong include-deleted type"
      ;; Include-deleted must be a boolean
      (is (not (valid? listing/AnalysisListingParams
                       {:include-deleted "true"})))

      (is (not (valid? listing/AnalysisListingParams
                       {:include-deleted 1})))

      (is (not (valid? listing/AnalysisListingParams
                       {:include-deleted "yes"})))

      (is (not (valid? listing/AnalysisListingParams
                       {:include-deleted nil}))))

    (testing "invalid params - wrong filter type"
      ;; Filter must be a string
      (is (not (valid? listing/AnalysisListingParams
                       {:filter 123})))

      (is (not (valid? listing/AnalysisListingParams
                       {:filter true})))

      (is (not (valid? listing/AnalysisListingParams
                       {:filter nil})))

      (is (not (valid? listing/AnalysisListingParams
                       {:filter {:field "name" :value "test"}})))

      (is (not (valid? listing/AnalysisListingParams
                       {:filter [{:field "name" :value "test"}]}))))

    (testing "invalid params - extra fields not allowed"
      ;; The schema should have :closed true from the merged schemas
      (is (not (valid? listing/AnalysisListingParams
                       {:limit 50
                        :extra-field "not allowed"})))

      (is (not (valid? listing/AnalysisListingParams
                       {:unknown-param "value"})))

      (is (not (valid? listing/AnalysisListingParams
                       {:filter "[{\"field\":\"name\",\"value\":\"test\"}]"
                        :unexpected "field"}))))))

(deftest test-AnalysisListingParams-merge-behavior
  (testing "AnalysisListingParams merge behavior"
    (testing "merged schema includes all fields from constituent schemas"
      ;; Fields from PagingParams
      (is (valid? listing/AnalysisListingParams {:limit 50}))
      (is (valid? listing/AnalysisListingParams {:offset 0}))
      (is (valid? listing/AnalysisListingParams {:sort-field "name"}))
      (is (valid? listing/AnalysisListingParams {:sort-dir "ASC"}))

      ;; Fields from IncludeHiddenParams
      (is (valid? listing/AnalysisListingParams {:include-hidden true}))

      ;; Fields from IncludeDeletedParams
      (is (valid? listing/AnalysisListingParams {:include-deleted true}))

      ;; Fields from the local map definition
      (is (valid? listing/AnalysisListingParams {:filter "[{\"field\":\"id\",\"value\":\"test\"}]"})))

    (testing "merged schema allows combinations of fields from different schemas"
      ;; Combination of PagingParams and IncludeHiddenParams
      (is (valid? listing/AnalysisListingParams
                  {:limit 50
                   :include-hidden true}))

      ;; Combination of PagingParams and IncludeDeletedParams
      (is (valid? listing/AnalysisListingParams
                  {:offset 100
                   :include-deleted true}))

      ;; Combination of IncludeHiddenParams and IncludeDeletedParams
      (is (valid? listing/AnalysisListingParams
                  {:include-hidden true
                   :include-deleted false}))

      ;; Combination of PagingParams, IncludeHiddenParams, and IncludeDeletedParams
      (is (valid? listing/AnalysisListingParams
                  {:limit 25
                   :offset 50
                   :include-hidden true
                   :include-deleted false}))

      ;; All fields together
      (is (valid? listing/AnalysisListingParams
                  {:limit 100
                   :offset 200
                   :sort-field "name"
                   :sort-dir "DESC"
                   :include-hidden true
                   :include-deleted true
                   :filter "[{\"field\":\"name\",\"value\":\"test\"}]"})))

    (testing "merged schema maintains validation constraints from constituent schemas"
      ;; PagingParams constraints - limit must be positive
      (is (not (valid? listing/AnalysisListingParams {:limit 0})))
      (is (not (valid? listing/AnalysisListingParams {:limit -1})))

      ;; PagingParams constraints - offset must be non-negative
      (is (valid? listing/AnalysisListingParams {:offset 0}))
      (is (not (valid? listing/AnalysisListingParams {:offset -1})))

      ;; PagingParams constraints - sort-dir must be enum value
      (is (not (valid? listing/AnalysisListingParams
                       {:sort-field "name"
                        :sort-dir "invalid"})))

      ;; IncludeHiddenParams constraints - must be boolean
      (is (not (valid? listing/AnalysisListingParams {:include-hidden "true"})))

      ;; IncludeDeletedParams constraints - must be boolean
      (is (not (valid? listing/AnalysisListingParams {:include-deleted "false"}))))))

(deftest test-filter-parameter-examples
  (testing "filter parameter with real-world examples from documentation"
    (testing "filter by app_name containing CACE"
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"app_name\",\"value\":\"cace\"}]"})))

    (testing "filter by specific id"
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"id\",\"value\":\"C09F5907-B2A2-4429-A11E-5B96F421C3C1\"}]"})))

    (testing "filter by parent_id"
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"parent_id\",\"value\":\"b4c2f624-7cbd-496e-adad-5be8d0d3b941\"}]"})))

    (testing "filter by null parent_id"
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"parent_id\",\"value\":null}]"})))

    (testing "filter by ownership - all"
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"ownership\",\"value\":\"all\"}]"})))

    (testing "filter by ownership - mine"
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"ownership\",\"value\":\"mine\"}]"})))

    (testing "filter by ownership - theirs"
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"ownership\",\"value\":\"theirs\"}]"})))

    (testing "multiple filters"
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"name\",\"value\":\"test\"},{\"field\":\"app_name\",\"value\":\"cace\"}]"})))))

(deftest test-edge-cases
  (testing "AnalysisListingParams edge cases"
    (testing "maximum integer values"
      ;; Test with very large but valid integer values
      (is (valid? listing/AnalysisListingParams
                  {:limit 999999
                   :offset 999999})))

    (testing "minimum valid values"
      ;; Test with minimum valid values
      (is (valid? listing/AnalysisListingParams
                  {:limit 1
                   :offset 0})))

    (testing "complex filter strings"
      ;; Test with complex JSON-like filter strings
      (is (valid? listing/AnalysisListingParams
                  {:filter (str "[{\"field\":\"name\",\"value\":\"complex name with spaces\"},"
                               "{\"field\":\"app_name\",\"value\":\"app-with-dashes\"},"
                               "{\"field\":\"ownership\",\"value\":\"mine\"}]")}))

      ;; Test with unicode characters in filter
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"name\",\"value\":\"测试\"}]"}))

      ;; Test with escaped characters in filter
      (is (valid? listing/AnalysisListingParams
                  {:filter "[{\"field\":\"name\",\"value\":\"test\\nwith\\nnewlines\"}]"})))

    (testing "whitespace in string fields"
      ;; Sort-field with whitespace should be valid (it's just a string)
      (is (valid? listing/AnalysisListingParams
                  {:sort-field "  "}))

      (is (valid? listing/AnalysisListingParams
                  {:sort-field "field with spaces"})))

    (testing "case sensitivity of sort-dir"
      ;; Only "ASC" and "DESC" are valid, not lowercase versions
      (is (valid? listing/AnalysisListingParams
                  {:sort-field "name"
                   :sort-dir "ASC"}))

      (is (valid? listing/AnalysisListingParams
                  {:sort-field "name"
                   :sort-dir "DESC"}))

      (is (not (valid? listing/AnalysisListingParams
                       {:sort-field "name"
                        :sort-dir "asc"})))

      (is (not (valid? listing/AnalysisListingParams
                       {:sort-field "name"
                        :sort-dir "desc"}))))))

(deftest test-BatchStatus
  (testing "BatchStatus validation"
    (testing "valid batch status - example from schema"
      (is (valid? listing/BatchStatus
                  {:total 42
                   :completed 39
                   :running 2
                   :submitted 1})))

    (testing "valid batch status - all zero"
      (is (valid? listing/BatchStatus
                  {:total 0
                   :completed 0
                   :running 0
                   :submitted 0})))

    (testing "valid batch status - large numbers"
      (is (valid? listing/BatchStatus
                  {:total 1000000
                   :completed 500000
                   :running 250000
                   :submitted 250000})))

    (testing "valid batch status - various combinations"
      (is (valid? listing/BatchStatus
                  {:total 100
                   :completed 100
                   :running 0
                   :submitted 0}))

      (is (valid? listing/BatchStatus
                  {:total 50
                   :completed 0
                   :running 50
                   :submitted 0}))

      (is (valid? listing/BatchStatus
                  {:total 75
                   :completed 25
                   :running 25
                   :submitted 25})))

    (testing "valid batch status - single job"
      (is (valid? listing/BatchStatus
                  {:total 1
                   :completed 1
                   :running 0
                   :submitted 0})))

    (testing "invalid batch status - missing required fields"
      ;; Missing total
      (is (not (valid? listing/BatchStatus
                       {:completed 39
                        :running 2
                        :submitted 1})))

      ;; Missing completed
      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :running 2
                        :submitted 1})))

      ;; Missing running
      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed 39
                        :submitted 1})))

      ;; Missing submitted
      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed 39
                        :running 2})))

      ;; All fields missing
      (is (not (valid? listing/BatchStatus {}))))

    (testing "invalid batch status - wrong field types"
      ;; Total as string
      (is (not (valid? listing/BatchStatus
                       {:total "42"
                        :completed 39
                        :running 2
                        :submitted 1})))

      ;; Completed as string
      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed "39"
                        :running 2
                        :submitted 1})))

      ;; Running as string
      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed 39
                        :running "2"
                        :submitted 1})))

      ;; Submitted as string
      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed 39
                        :running 2
                        :submitted "1"})))

      ;; Total as float
      (is (not (valid? listing/BatchStatus
                       {:total 42.5
                        :completed 39
                        :running 2
                        :submitted 1})))

      ;; Completed as boolean
      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed true
                        :running 2
                        :submitted 1})))

      ;; Running as nil
      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed 39
                        :running nil
                        :submitted 1})))

      ;; Submitted as keyword
      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed 39
                        :running 2
                        :submitted :one}))))

    (testing "invalid batch status - negative values not allowed"
      ;; The schema has :min 0 constraints on all fields
      ;; Negative total
      (is (not (valid? listing/BatchStatus
                       {:total -1
                        :completed 0
                        :running 0
                        :submitted 0})))

      ;; Negative completed
      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed -5
                        :running 2
                        :submitted 1})))

      ;; Negative running
      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed 39
                        :running -2
                        :submitted 1})))

      ;; Negative submitted
      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed 39
                        :running 2
                        :submitted -1})))

      ;; All negative
      (is (not (valid? listing/BatchStatus
                       {:total -10
                        :completed -5
                        :running -3
                        :submitted -2}))))

    (testing "invalid batch status - extra fields not allowed"
      ;; The schema uses mu/closed-schema, so extra fields are rejected
      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed 39
                        :running 2
                        :submitted 1
                        :extra-field "not allowed"})))

      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed 39
                        :running 2
                        :submitted 1
                        :failed 0})))

      (is (not (valid? listing/BatchStatus
                       {:total 42
                        :completed 39
                        :running 2
                        :submitted 1
                        :pending 5}))))))

(deftest test-BatchStatus-edge-cases
  (testing "BatchStatus edge cases"
    (testing "maximum integer values"
      ;; Test with very large but valid integer values
      (is (valid? listing/BatchStatus
                  {:total 999999999
                   :completed 500000000
                   :running 250000000
                   :submitted 249999999})))

    (testing "minimum valid values - all zeros"
      (is (valid? listing/BatchStatus
                  {:total 0
                   :completed 0
                   :running 0
                   :submitted 0})))

    (testing "sum of components exceeds total (logically inconsistent but schema-valid)"
      ;; The schema doesn't enforce that completed + running + submitted = total
      ;; so this should be valid from a schema perspective
      (is (valid? listing/BatchStatus
                  {:total 10
                   :completed 20
                   :running 30
                   :submitted 40})))

    (testing "sum of components less than total (logically inconsistent but schema-valid)"
      (is (valid? listing/BatchStatus
                  {:total 100
                   :completed 10
                   :running 5
                   :submitted 3})))

    (testing "only total is non-zero"
      (is (valid? listing/BatchStatus
                  {:total 50
                   :completed 0
                   :running 0
                   :submitted 0})))

    (testing "asymmetric large values"
      (is (valid? listing/BatchStatus
                  {:total 1000000
                   :completed 1
                   :running 0
                   :submitted 999999})))))

(deftest test-BaseAnalysis
  (testing "BaseAnalysis validation"
    (testing "valid analysis objects"
      (are [obj] (valid? listing/BaseAnalysis obj)
        ;; Minimal valid analysis with only required fields
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"
         :username  "janedoe"}

        ;; With all optional fields
        {:app_id           "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id        "de"
         :id               #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify           true
         :status           "Running"
         :username         "janedoe"
         :app_description  "BLAST Nucleotide Alignment"
         :app_version_id   #uuid "46f2634e-6297-4ff5-9e63-dfe1c4a2ee09"
         :app_name         "BLAST"
         :batch            false
         :description      "BLAST nucleotide alignment of maize B73 genome sequences."
         :enddate          "1234567890000"
         :name             "BLAST Zea Mays B73"
         :resultfolderid   "/example/home/janedoe/analyses/BLAST-Zea-Mays-B73"
         :startdate        "1234567880000"
         :wiki_url         "https://confluence.cyverse.org/discovery-environment/BLAST"
         :parent_id        #uuid "59dfbf9b-b5ab-4197-a905-e8793e360444"
         :batch_status     {:total 42 :completed 39 :running 2 :submitted 1}
         :interactive_urls ["https://vice.example.org/a123456"]}

        ;; Batch analysis
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    false
         :status    "Completed"
         :username  "johndoe"
         :batch     true}

        ;; With batch_status
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :batch_status {:total 100 :completed 50 :running 25 :submitted 25}}

        ;; With multiple interactive URLs
        {:app_id           "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id        "de"
         :id               #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify           true
         :status           "Running"
         :username         "janedoe"
         :interactive_urls ["https://vice.example.org/a123456"
                            "https://vice.example.org/b789012"]}

        ;; With parent_id (child analysis)
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"
         :username  "janedoe"
         :parent_id #uuid "59dfbf9b-b5ab-4197-a905-e8793e360444"}))

    (testing "invalid analysis objects - missing required fields"
      (are [obj] (not (valid? listing/BaseAnalysis obj))
        ;; Missing app_id
        {:system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"
         :username  "janedoe"}

        ;; Missing system_id
        {:app_id   "90b343d0-2db7-4f59-be6c-767806736529"
         :id       #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify   true
         :status   "Running"
         :username "janedoe"}

        ;; Missing id
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :notify    true
         :status    "Running"
         :username  "janedoe"}

        ;; Missing notify
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :status    "Running"
         :username  "janedoe"}

        ;; Missing status
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :username  "janedoe"}

        ;; Missing username
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"}

        ;; Empty map
        {}))

    (testing "invalid analysis objects - wrong field types"
      (are [obj] (not (valid? listing/BaseAnalysis obj))
        ;; app_id as number
        {:app_id    123
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"
         :username  "janedoe"}

        ;; id as string
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"
         :username  "janedoe"}

        ;; notify as string
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    "true"
         :status    "Running"
         :username  "janedoe"}

        ;; status as number
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    123
         :username  "janedoe"}

        ;; username as number
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"
         :username  123}

        ;; app_version_id as string
        {:app_id         "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id      "de"
         :id             #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify         true
         :status         "Running"
         :username       "janedoe"
         :app_version_id "46f2634e-6297-4ff5-9e63-dfe1c4a2ee09"}

        ;; batch as string
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"
         :username  "janedoe"
         :batch     "false"}

        ;; startdate as integer (should be string)
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"
         :username  "janedoe"
         :startdate 1234567890000}

        ;; enddate as integer (should be string)
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"
         :username  "janedoe"
         :enddate   1234567890000}

        ;; parent_id as string
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"
         :username  "janedoe"
         :parent_id "59dfbf9b-b5ab-4197-a905-e8793e360444"}

        ;; interactive_urls as string instead of vector
        {:app_id           "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id        "de"
         :id               #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify           true
         :status           "Running"
         :username         "janedoe"
         :interactive_urls "https://vice.example.org/a123456"}

        ;; interactive_urls with non-string elements
        {:app_id           "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id        "de"
         :id               #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify           true
         :status           "Running"
         :username         "janedoe"
         :interactive_urls [123 456]}

        ;; batch_status with invalid structure
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :batch_status {:total "42"}}))

    (testing "invalid analysis objects - extra fields not allowed"
      (are [obj] (not (valid? listing/BaseAnalysis obj))
        ;; Extra field
        {:app_id      "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id   "de"
         :id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify      true
         :status      "Running"
         :username    "janedoe"
         :extra_field "not allowed"}

        ;; Multiple extra fields
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"
         :username  "janedoe"
         :foo       "bar"
         :baz       "qux"}))))

(deftest test-Analysis
  (testing "Analysis validation"
    (testing "valid analysis objects"
      (are [obj] (valid? listing/Analysis obj)
        ;; Minimal valid analysis with only required fields (BaseAnalysis + Analysis)
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled false
         :can_share    true}

        ;; With all fields from BaseAnalysis plus Analysis fields
        {:app_id           "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id        "de"
         :id               #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify           true
         :status           "Running"
         :username         "janedoe"
         :app_disabled     false
         :can_share        true
         :app_description  "BLAST Nucleotide Alignment"
         :app_version_id   #uuid "46f2634e-6297-4ff5-9e63-dfe1c4a2ee09"
         :app_name         "BLAST"
         :batch            false
         :description      "BLAST nucleotide alignment of maize B73 genome sequences."
         :enddate          "1234567890000"
         :name             "BLAST Zea Mays B73"
         :resultfolderid   "/example/home/janedoe/analyses/BLAST-Zea-Mays-B73"
         :startdate        "1234567880000"
         :wiki_url         "https://confluence.cyverse.org/discovery-environment/BLAST"
         :parent_id        #uuid "59dfbf9b-b5ab-4197-a905-e8793e360444"
         :batch_status     {:total 42 :completed 39 :running 2 :submitted 1}
         :interactive_urls ["https://vice.example.org/a123456"]}

        ;; Batch analysis that cannot be shared
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       false
         :status       "Completed"
         :username     "johndoe"
         :batch        true
         :app_disabled false
         :can_share    false}

        ;; With batch_status
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :batch_status {:total 100 :completed 50 :running 25 :submitted 25}
         :app_disabled false
         :can_share    true}

        ;; With multiple interactive URLs
        {:app_id           "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id        "de"
         :id               #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify           true
         :status           "Running"
         :username         "janedoe"
         :interactive_urls ["https://vice.example.org/a123456"
                            "https://vice.example.org/b789012"]
         :app_disabled     false
         :can_share        true}

        ;; With parent_id (child analysis)
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :parent_id    #uuid "59dfbf9b-b5ab-4197-a905-e8793e360444"
         :app_disabled false
         :can_share    true}

        ;; Deprecated app that cannot be shared
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Failed"
         :username     "janedoe"
         :app_disabled false
         :can_share    false}))

    (testing "invalid analysis objects - missing required fields from BaseAnalysis"
      (are [obj] (not (valid? listing/Analysis obj))
        ;; Missing app_id (from BaseAnalysis)
        {:system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled false
         :can_share    true}

        ;; Missing system_id (from BaseAnalysis)
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled false
         :can_share    true}

        ;; Missing id (from BaseAnalysis)
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled false
         :can_share    true}

        ;; Missing notify (from BaseAnalysis)
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :status       "Running"
         :username     "janedoe"
         :app_disabled false
         :can_share    true}

        ;; Missing status (from BaseAnalysis)
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :username     "janedoe"
         :app_disabled false
         :can_share    true}

        ;; Missing username (from BaseAnalysis)
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :app_disabled false
         :can_share    true}))

    (testing "invalid analysis objects - missing required fields from Analysis"
      (are [obj] (not (valid? listing/Analysis obj))
        ;; Missing app_disabled
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"
         :username  "janedoe"
         :can_share true}

        ;; Missing can_share
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled false}

        ;; Missing both Analysis-specific fields
        {:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id "de"
         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify    true
         :status    "Running"
         :username  "janedoe"}

        ;; Empty map
        {}))

    (testing "invalid analysis objects - wrong field types from BaseAnalysis"
      (are [obj] (not (valid? listing/Analysis obj))
        ;; app_id as number
        {:app_id       123
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled false
         :can_share    true}

        ;; id as string
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled false
         :can_share    true}

        ;; notify as string
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       "true"
         :status       "Running"
         :username     "janedoe"
         :app_disabled false
         :can_share    true}

        ;; status as number
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       123
         :username     "janedoe"
         :app_disabled false
         :can_share    true}

        ;; username as number
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     123
         :app_disabled false
         :can_share    true}

        ;; app_version_id as string
        {:app_id         "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id      "de"
         :id             #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify         true
         :status         "Running"
         :username       "janedoe"
         :app_version_id "46f2634e-6297-4ff5-9e63-dfe1c4a2ee09"
         :app_disabled   false
         :can_share      true}

        ;; batch as string
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :batch        "false"
         :app_disabled false
         :can_share    true}

        ;; startdate as integer (should be string)
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :startdate    1234567890000
         :app_disabled false
         :can_share    true}

        ;; enddate as integer (should be string)
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :enddate      1234567890000
         :app_disabled false
         :can_share    true}

        ;; parent_id as string
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :parent_id    "59dfbf9b-b5ab-4197-a905-e8793e360444"
         :app_disabled false
         :can_share    true}

        ;; interactive_urls as string instead of vector
        {:app_id           "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id        "de"
         :id               #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify           true
         :status           "Running"
         :username         "janedoe"
         :interactive_urls "https://vice.example.org/a123456"
         :app_disabled     false
         :can_share        true}

        ;; interactive_urls with non-string elements
        {:app_id           "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id        "de"
         :id               #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify           true
         :status           "Running"
         :username         "janedoe"
         :interactive_urls [123 456]
         :app_disabled     false
         :can_share        true}

        ;; batch_status with invalid structure
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :batch_status {:total "42"}
         :app_disabled false
         :can_share    true}))

    (testing "invalid analysis objects - wrong field types for Analysis-specific fields"
      (are [obj] (not (valid? listing/Analysis obj))
        ;; app_disabled as string
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled "false"
         :can_share    true}

        ;; app_disabled as number
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled 0
         :can_share    true}

        ;; app_disabled as nil
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled nil
         :can_share    true}

        ;; can_share as string
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled false
         :can_share    "true"}

        ;; can_share as number
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled false
         :can_share    1}

        ;; can_share as nil
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled false
         :can_share    nil}))

    (testing "invalid analysis objects - extra fields not allowed"
      (are [obj] (not (valid? listing/Analysis obj))
        ;; Extra field
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled false
         :can_share    true
         :extra_field  "not allowed"}

        ;; Multiple extra fields
        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
         :system_id    "de"
         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify       true
         :status       "Running"
         :username     "janedoe"
         :app_disabled false
         :can_share    true
         :foo          "bar"
         :baz          "qux"}))))

(deftest test-AnalysisList
  (testing "AnalysisList validation"
    (testing "valid analysis list objects"
      (are [obj] (valid? listing/AnalysisList obj)
        ;; Minimal valid analysis list with empty analyses
        {:status-count []
         :analyses     []
         :timestamp    "1763405834987"
         :total        0}

        ;; Empty analyses with status counts
        {:status-count [{:count 5 :status "Running"}
                        {:count 10 :status "Completed"}]
         :analyses     []
         :timestamp    "1763405834987"
         :total        0}

        ;; Single analysis with status counts
        {:status-count [{:count 1 :status "Running"}]
         :analyses     [{:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
                         :notify       true
                         :status       "Running"
                         :username     "janedoe"
                         :app_disabled false
                         :can_share    true}]
         :timestamp    "1763405834987"
         :total        1}

        ;; Multiple analyses with full details
        {:status-count [{:count 2 :status "Running"}
                        {:count 1 :status "Completed"}]
         :analyses     [{:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
                         :notify       true
                         :status       "Running"
                         :username     "janedoe"
                         :app_disabled false
                         :can_share    true}
                        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "12345678-1234-1234-1234-123456789012"
                         :notify       false
                         :status       "Completed"
                         :username     "johndoe"
                         :app_disabled false
                         :can_share    false}]
         :timestamp    "1763405834987"
         :total        2}

        ;; Analyses with optional fields from Analysis schema
        {:status-count [{:count 1 :status "Running"}]
         :analyses     [{:app_id           "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id        "de"
                         :id               #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
                         :notify           true
                         :status           "Running"
                         :username         "janedoe"
                         :app_disabled     false
                         :can_share        true
                         :app_description  "BLAST Nucleotide Alignment"
                         :app_version_id   #uuid "46f2634e-6297-4ff5-9e63-dfe1c4a2ee09"
                         :app_name         "BLAST"
                         :batch            false
                         :description      "BLAST nucleotide alignment of maize B73 genome sequences."
                         :enddate          "1234567890000"
                         :name             "BLAST Zea Mays B73"
                         :resultfolderid   "/example/home/janedoe/analyses/BLAST-Zea-Mays-B73"
                         :startdate        "1234567880000"
                         :wiki_url         "https://confluence.cyverse.org/discovery-environment/BLAST"
                         :parent_id        #uuid "59dfbf9b-b5ab-4197-a905-e8793e360444"
                         :batch_status     {:total 42 :completed 39 :running 2 :submitted 1}
                         :interactive_urls ["https://vice.example.org/a123456"]}]
         :timestamp    "1763405834987"
         :total        1}

        ;; Large total with many analyses
        {:status-count [{:count 50 :status "Running"}
                        {:count 100 :status "Completed"}
                        {:count 25 :status "Failed"}]
         :analyses     [{:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
                         :notify       true
                         :status       "Running"
                         :username     "janedoe"
                         :app_disabled false
                         :can_share    true}
                        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "12345678-1234-1234-1234-123456789012"
                         :notify       false
                         :status       "Completed"
                         :username     "johndoe"
                         :app_disabled false
                         :can_share    false}
                        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "87654321-4321-4321-4321-210987654321"
                         :notify       true
                         :status       "Failed"
                         :username     "janedoe"
                         :app_disabled false
                         :can_share    true}]
         :timestamp    "1763405834987"
         :total        175}

        ;; Batch analyses with batch_status
        {:status-count [{:count 2 :status "Running"}]
         :analyses     [{:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
                         :notify       true
                         :status       "Running"
                         :username     "janedoe"
                         :batch        true
                         :batch_status {:total 100 :completed 50 :running 25 :submitted 25}
                         :app_disabled false
                         :can_share    true}
                        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "12345678-1234-1234-1234-123456789012"
                         :notify       false
                         :status       "Running"
                         :username     "johndoe"
                         :batch        true
                         :batch_status {:total 50 :completed 25 :running 15 :submitted 10}
                         :app_disabled false
                         :can_share    false}]
         :timestamp    "1763405834987"
         :total        2}

        ;; Analyses with interactive URLs
        {:status-count [{:count 1 :status "Running"}]
         :analyses     [{:app_id           "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id        "de"
                         :id               #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
                         :notify           true
                         :status           "Running"
                         :username         "janedoe"
                         :interactive_urls ["https://vice.example.org/a123456"
                                            "https://vice.example.org/b789012"]
                         :app_disabled     false
                         :can_share        true}]
         :timestamp    "1763405834987"
         :total        1}

        ;; Diverse analysis statuses
        {:status-count [{:count 1 :status "Submitted"}
                        {:count 1 :status "Running"}
                        {:count 1 :status "Completed"}
                        {:count 1 :status "Failed"}
                        {:count 1 :status "Canceled"}]
         :analyses     [{:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
                         :notify       true
                         :status       "Submitted"
                         :username     "janedoe"
                         :app_disabled false
                         :can_share    true}
                        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "12345678-1234-1234-1234-123456789012"
                         :notify       false
                         :status       "Running"
                         :username     "johndoe"
                         :app_disabled false
                         :can_share    false}
                        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "87654321-4321-4321-4321-210987654321"
                         :notify       true
                         :status       "Completed"
                         :username     "janedoe"
                         :app_disabled false
                         :can_share    true}
                        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
                         :notify       false
                         :status       "Failed"
                         :username     "johndoe"
                         :app_disabled false
                         :can_share    true}
                        {:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "11111111-2222-3333-4444-555555555555"
                         :notify       true
                         :status       "Canceled"
                         :username     "janedoe"
                         :app_disabled false
                         :can_share    false}]
         :timestamp    "1763405834987"
         :total        5}))

    (testing "invalid analysis list objects - missing required fields"
      (are [obj] (not (valid? listing/AnalysisList obj))
        ;; Missing status-count
        {:analyses  []
         :timestamp "1763405834987"
         :total     0}

        ;; Missing analyses
        {:status-count []
         :timestamp    "1763405834987"
         :total        0}

        ;; Missing timestamp
        {:status-count []
         :analyses     []
         :total        0}

        ;; Missing total
        {:status-count []
         :analyses     []
         :timestamp    "1763405834987"}

        ;; Empty map
        {}))

    (testing "invalid analysis list objects - wrong field types"
      (are [obj] (not (valid? listing/AnalysisList obj))
        ;; status-count as string
        {:status-count "not-a-vector"
         :analyses     []
         :timestamp    "1763405834987"
         :total        0}

        ;; status-count with invalid elements
        {:status-count [{:count "5" :status "Running"}]
         :analyses     []
         :timestamp    "1763405834987"
         :total        0}

        ;; analyses as string
        {:status-count []
         :analyses     "not-a-vector"
         :timestamp    "1763405834987"
         :total        0}

        ;; analyses with invalid elements
        {:status-count []
         :analyses     [{:app_id "90b343d0-2db7-4f59-be6c-767806736529"}]
         :timestamp    "1763405834987"
         :total        0}

        ;; timestamp as integer
        {:status-count []
         :analyses     []
         :timestamp    1763405834987
         :total        0}

        ;; timestamp as nil
        {:status-count []
         :analyses     []
         :timestamp    nil
         :total        0}

        ;; total as string
        {:status-count []
         :analyses     []
         :timestamp    "1763405834987"
         :total        "0"}

        ;; total as nil
        {:status-count []
         :analyses     []
         :timestamp    "1763405834987"
         :total        nil}))

    (testing "invalid analysis list objects - extra fields not allowed"
      (are [obj] (not (valid? listing/AnalysisList obj))
        ;; Extra field
        {:status-count []
         :analyses     []
         :timestamp    "1763405834987"
         :total        0
         :extra-field  "not allowed"}

        ;; Multiple extra fields
        {:status-count []
         :analyses     []
         :timestamp    "1763405834987"
         :total        0
         :foo          "bar"
         :baz          "qux"}))

    (testing "invalid analysis list objects - invalid nested structures"
      (are [obj] (not (valid? listing/AnalysisList obj))
        ;; Invalid status-count element (missing count)
        {:status-count [{:status "Running"}]
         :analyses     []
         :timestamp    "1763405834987"
         :total        0}

        ;; Invalid status-count element (missing status)
        {:status-count [{:count 5}]
         :analyses     []
         :timestamp    "1763405834987"
         :total        0}

        ;; Invalid analysis element (missing required BaseAnalysis fields)
        {:status-count []
         :analyses     [{:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :app_disabled false
                         :can_share    true}]
         :timestamp    "1763405834987"
         :total        1}

        ;; Invalid analysis element (missing Analysis-specific fields)
        {:status-count []
         :analyses     [{:app_id    "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id "de"
                         :id        #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
                         :notify    true
                         :status    "Running"
                         :username  "janedoe"}]
         :timestamp    "1763405834987"
         :total        1}

        ;; Invalid batch_status in analysis
        {:status-count []
         :analyses     [{:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
                         :notify       true
                         :status       "Running"
                         :username     "janedoe"
                         :batch_status {:total "42"}
                         :app_disabled false
                         :can_share    true}]
         :timestamp    "1763405834987"
         :total        1}

        ;; Analysis with extra field
        {:status-count []
         :analyses     [{:app_id       "90b343d0-2db7-4f59-be6c-767806736529"
                         :system_id    "de"
                         :id           #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
                         :notify       true
                         :status       "Running"
                         :username     "janedoe"
                         :app_disabled false
                         :can_share    true
                         :extra-field  "not allowed"}]
         :timestamp    "1763405834987"
         :total        1}))))

(deftest test-AnalysisUpdate
  (testing "AnalysisUpdate validation"
    (testing "valid analysis update objects"
      (are [obj] (valid? listing/AnalysisUpdate obj)
        ;; Empty map - all fields are optional
        {}

        ;; With description only
        {:description "Updated analysis description"}

        ;; With name only
        {:name "Updated Analysis Name"}

        ;; With both description and name
        {:description "Updated analysis description"
         :name        "Updated Analysis Name"}

        ;; With long description
        {:description (str "This is a very long description that provides detailed information "
                          "about the analysis and its purpose, including methodology and expected outcomes.")}

        ;; With long name
        {:name "Very Long Analysis Name With Many Words And Details"}

        ;; With empty string description (valid but not best practice)
        {:description ""}

        ;; With empty string name (valid but not best practice)
        {:name ""}

        ;; With both fields as empty strings
        {:description ""
         :name        ""}

        ;; With special characters in description
        {:description "Analysis with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?"}

        ;; With special characters in name
        {:name "Analysis: Test #1 (v2.0)"}

        ;; With unicode characters
        {:description "分析描述"
         :name        "分析名称"}

        ;; With newlines in description
        {:description "Line 1\nLine 2\nLine 3"}

        ;; With whitespace
        {:description "  Padded description  "
         :name        "  Padded name  "}))

    (testing "invalid analysis update objects - wrong field types"
      (are [obj] (not (valid? listing/AnalysisUpdate obj))
        ;; description as number
        {:description 123}

        ;; description as boolean
        {:description true}

        ;; description as nil
        {:description nil}

        ;; description as vector
        {:description ["desc1" "desc2"]}

        ;; description as map
        {:description {:text "description"}}

        ;; name as number
        {:name 123}

        ;; name as boolean
        {:name false}

        ;; name as nil
        {:name nil}

        ;; name as vector
        {:name ["name1" "name2"]}

        ;; name as map
        {:name {:text "name"}}

        ;; Both fields with wrong types
        {:description 123
         :name        true}))

    (testing "invalid analysis update objects - extra fields not allowed"
      (are [obj] (not (valid? listing/AnalysisUpdate obj))
        ;; Extra field
        {:description "Updated description"
         :name        "Updated name"
         :extra-field "not allowed"}

        ;; Only extra field
        {:id #uuid "eb97403f-de50-4f65-939e-b276f7faec16"}

        ;; Multiple extra fields
        {:description "Updated description"
         :foo         "bar"
         :baz         "qux"}

        ;; Fields from Analysis that are not in AnalysisUpdate
        {:description "Updated description"
         :name        "Updated name"
         :status      "Running"}

        {:description "Updated description"
         :app_id      "90b343d0-2db7-4f59-be6c-767806736529"}

        {:name     "Updated name"
         :username "janedoe"}

        {:description "Updated description"
         :notify      true}))))

(deftest test-AnalysisUpdateResponse
  (testing "AnalysisUpdateResponse validation"
    (testing "valid analysis update response objects"
      (are [obj] (valid? listing/AnalysisUpdateResponse obj)
        ;; Minimal valid response with only required id field
        {:id #uuid "eb97403f-de50-4f65-939e-b276f7faec16"}

        ;; With id and description
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description "Updated analysis description"}

        ;; With id and name
        {:id   #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :name "Updated Analysis Name"}

        ;; With all fields
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description "Updated analysis description"
         :name        "Updated Analysis Name"}

        ;; Different UUID
        {:id          #uuid "12345678-1234-1234-1234-123456789012"
         :description "Another description"
         :name        "Another name"}

        ;; With long description
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description (str "This is a very long description that provides detailed information "
                          "about the analysis and its purpose, including methodology and expected outcomes.")}

        ;; With long name
        {:id   #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :name "Very Long Analysis Name With Many Words And Details"}

        ;; With empty string description
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description ""}

        ;; With empty string name
        {:id   #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :name ""}

        ;; With both fields as empty strings
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description ""
         :name        ""}

        ;; With special characters in description
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description "Analysis with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?"}

        ;; With special characters in name
        {:id   #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :name "Analysis: Test #1 (v2.0)"}

        ;; With unicode characters
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description "分析描述"
         :name        "分析名称"}

        ;; With newlines in description
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description "Line 1\nLine 2\nLine 3"}

        ;; With whitespace
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description "  Padded description  "
         :name        "  Padded name  "}))

    (testing "invalid analysis update response objects - missing required id field"
      (are [obj] (not (valid? listing/AnalysisUpdateResponse obj))
        ;; Empty map
        {}

        ;; Only description
        {:description "Updated description"}

        ;; Only name
        {:name "Updated name"}

        ;; Both optional fields without id
        {:description "Updated description"
         :name        "Updated name"}))

    (testing "invalid analysis update response objects - wrong field types"
      (are [obj] (not (valid? listing/AnalysisUpdateResponse obj))
        ;; id as string
        {:id "eb97403f-de50-4f65-939e-b276f7faec16"}

        ;; id as number
        {:id 123}

        ;; id as nil
        {:id nil}

        ;; id as map
        {:id {:uuid "eb97403f-de50-4f65-939e-b276f7faec16"}}

        ;; description as number
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description 123}

        ;; description as boolean
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description true}

        ;; description as nil
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description nil}

        ;; description as vector
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description ["desc1" "desc2"]}

        ;; description as map
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description {:text "description"}}

        ;; name as number
        {:id   #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :name 123}

        ;; name as boolean
        {:id   #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :name false}

        ;; name as nil
        {:id   #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :name nil}

        ;; name as vector
        {:id   #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :name ["name1" "name2"]}

        ;; name as map
        {:id   #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :name {:text "name"}}

        ;; Multiple fields with wrong types
        {:id          "not-a-uuid"
         :description 123
         :name        true}))

    (testing "invalid analysis update response objects - extra fields not allowed"
      (are [obj] (not (valid? listing/AnalysisUpdateResponse obj))
        ;; Extra field
        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description "Updated description"
         :name        "Updated name"
         :extra-field "not allowed"}

        ;; Multiple extra fields
        {:id   #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :foo  "bar"
         :baz  "qux"}

        ;; Fields from Analysis that are not in AnalysisUpdateResponse
        {:id      #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :name    "Updated name"
         :status  "Running"}

        {:id     #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :app_id "90b343d0-2db7-4f59-be6c-767806736529"}

        {:id       #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :username "janedoe"}

        {:id     #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :notify true}

        {:id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"
         :description "Updated description"
         :app_name    "BLAST"}))))

(deftest test-AnalysisStep
  (testing "AnalysisStep validation"
    (testing "valid analysis step objects"
      (are [obj] (valid? listing/AnalysisStep obj)
        ;; Minimal valid step with only required field
        {:step_number 1}

        ;; With all optional fields
        {:step_number     1
         :external_id     "d81e33c4-b915-45be-8568-5c731f37cd5d"
         :startdate       "1763405834987"
         :enddate         "1763405835987"
         :status          "Submitted"
         :app_step_number 1
         :step_type       "Interactive"}

        ;; Different step number
        {:step_number 42}

        ;; With external_id only
        {:step_number 1
         :external_id "d81e33c4-b915-45be-8568-5c731f37cd5d"}

        ;; With startdate only
        {:step_number 1
         :startdate   "1763405834987"}

        ;; With enddate only
        {:step_number 1
         :enddate     "1763405835987"}

        ;; With status only
        {:step_number 1
         :status      "Submitted"}

        ;; With app_step_number only
        {:step_number     1
         :app_step_number 2}

        ;; With step_type only
        {:step_number 1
         :step_type   "Interactive"}

        ;; With startdate and enddate
        {:step_number 5
         :startdate   "1763405834000"
         :enddate     "1763405835000"}

        ;; With external_id and status
        {:step_number 3
         :external_id "a1b2c3d4-e5f6-4g7h-8i9j-0k1l2m3n4o5p"
         :status      "Running"}

        ;; Completed step
        {:step_number     7
         :external_id     "completed-step-id"
         :startdate       "1763405800000"
         :enddate         "1763405900000"
         :status          "Completed"
         :app_step_number 7
         :step_type       "Batch"}

        ;; Failed step
        {:step_number 2
         :external_id "failed-step-id"
         :startdate   "1763405834987"
         :enddate     "1763405835987"
         :status      "Failed"
         :step_type   "Batch"}

        ;; Step with different app_step_number
        {:step_number     10
         :app_step_number 5
         :status          "Running"}

        ;; Step zero (edge case)
        {:step_number 0}

        ;; Large step number
        {:step_number 999999}

        ;; Negative step number (testing if allowed)
        {:step_number -1}

        ;; Step with various status values
        {:step_number 1
         :status      "Canceled"}

        {:step_number 1
         :status      "Pending"}

        ;; Step with various step_type values
        {:step_number 1
         :step_type   "DE"}

        {:step_number 1
         :step_type   "Agave"}

        ;; Step with empty string fields (testing if allowed)
        {:step_number 1
         :external_id ""}

        {:step_number 1
         :status      ""}

        {:step_number 1
         :step_type   ""}

        ;; Step with special characters in string fields
        {:step_number 1
         :external_id "external-id-with-special-chars-!@#$%"}

        {:step_number 1
         :status      "Status: Running (99%)"}

        ;; Step with unicode in string fields
        {:step_number 1
         :status      "运行中"}

        ;; Step with very long timestamps
        {:step_number 1
         :startdate   "9999999999999"
         :enddate     "9999999999999"}

        ;; Step with short timestamps
        {:step_number 1
         :startdate   "0"
         :enddate     "1"}

        ;; Step with zero app_step_number
        {:step_number     5
         :app_step_number 0}

        ;; Step with negative app_step_number
        {:step_number     5
         :app_step_number -5}))

    (testing "invalid analysis step objects - missing required field"
      (are [obj] (not (valid? listing/AnalysisStep obj))
        ;; Empty map
        {}

        ;; Only optional fields, no step_number
        {:external_id "d81e33c4-b915-45be-8568-5c731f37cd5d"}

        {:startdate "1763405834987"}

        {:enddate "1763405835987"}

        {:status "Submitted"}

        {:app_step_number 1}

        {:step_type "Interactive"}

        ;; All optional fields but missing step_number
        {:external_id     "d81e33c4-b915-45be-8568-5c731f37cd5d"
         :startdate       "1763405834987"
         :enddate         "1763405835987"
         :status          "Submitted"
         :app_step_number 1
         :step_type       "Interactive"}))

    (testing "invalid analysis step objects - wrong field types"
      (are [obj] (not (valid? listing/AnalysisStep obj))
        ;; step_number as string
        {:step_number "1"}

        ;; step_number as float
        {:step_number 1.5}

        ;; step_number as nil
        {:step_number nil}

        ;; step_number as boolean
        {:step_number true}

        ;; step_number as map
        {:step_number {:value 1}}

        ;; step_number as vector
        {:step_number [1]}

        ;; external_id as number
        {:step_number 1
         :external_id 123}

        ;; external_id as boolean
        {:step_number 1
         :external_id true}

        ;; external_id as nil
        {:step_number 1
         :external_id nil}

        ;; external_id as map
        {:step_number 1
         :external_id {:id "d81e33c4-b915-45be-8568-5c731f37cd5d"}}

        ;; external_id as vector
        {:step_number 1
         :external_id ["d81e33c4-b915-45be-8568-5c731f37cd5d"]}

        ;; startdate as integer
        {:step_number 1
         :startdate   1763405834987}

        ;; startdate as boolean
        {:step_number 1
         :startdate   false}

        ;; startdate as nil
        {:step_number 1
         :startdate   nil}

        ;; startdate as map
        {:step_number 1
         :startdate   {:timestamp "1763405834987"}}

        ;; enddate as integer
        {:step_number 1
         :enddate     1763405835987}

        ;; enddate as boolean
        {:step_number 1
         :enddate     true}

        ;; enddate as nil
        {:step_number 1
         :enddate     nil}

        ;; enddate as vector
        {:step_number 1
         :enddate     ["1763405835987"]}

        ;; status as number
        {:step_number 1
         :status      123}

        ;; status as boolean
        {:step_number 1
         :status      false}

        ;; status as nil
        {:step_number 1
         :status      nil}

        ;; status as map
        {:step_number 1
         :status      {:value "Submitted"}}

        ;; app_step_number as string
        {:step_number     1
         :app_step_number "1"}

        ;; app_step_number as float
        {:step_number     1
         :app_step_number 1.5}

        ;; app_step_number as nil
        {:step_number     1
         :app_step_number nil}

        ;; app_step_number as boolean
        {:step_number     1
         :app_step_number true}

        ;; app_step_number as map
        {:step_number     1
         :app_step_number {:value 1}}

        ;; step_type as number
        {:step_number 1
         :step_type   123}

        ;; step_type as boolean
        {:step_number 1
         :step_type   true}

        ;; step_type as nil
        {:step_number 1
         :step_type   nil}

        ;; step_type as vector
        {:step_number 1
         :step_type   ["Interactive"]}

        ;; Multiple fields with wrong types
        {:step_number     "1"
         :external_id     123
         :startdate       1763405834987
         :enddate         true
         :status          456
         :app_step_number "2"
         :step_type       false}))

    (testing "invalid analysis step objects - extra fields not allowed"
      (are [obj] (not (valid? listing/AnalysisStep obj))
        ;; Extra field
        {:step_number 1
         :extra_field "not allowed"}

        ;; Multiple extra fields
        {:step_number 1
         :foo         "bar"
         :baz         "qux"}

        ;; Extra field with all valid fields
        {:step_number     1
         :external_id     "d81e33c4-b915-45be-8568-5c731f37cd5d"
         :startdate       "1763405834987"
         :enddate         "1763405835987"
         :status          "Submitted"
         :app_step_number 1
         :step_type       "Interactive"
         :extra_field     "not allowed"}

        ;; Fields that might exist in other schemas
        {:step_number 1
         :id          #uuid "eb97403f-de50-4f65-939e-b276f7faec16"}

        {:step_number 1
         :name        "Step Name"}

        {:step_number 1
         :description "Step description"}

        {:step_number 1
         :username    "janedoe"}))))

(deftest test-AnalysisStep-edge-cases
  (testing "AnalysisStep edge cases"
    (testing "step numbers at boundaries"
      ;; Zero
      (is (valid? listing/AnalysisStep {:step_number 0}))

      ;; Very large positive integer
      (is (valid? listing/AnalysisStep {:step_number 2147483647}))

      ;; Negative numbers (testing if allowed)
      (is (valid? listing/AnalysisStep {:step_number -1}))
      (is (valid? listing/AnalysisStep {:step_number -999})))

    (testing "timestamp edge cases"
      ;; Empty timestamps
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :startdate   ""
                   :enddate     ""}))

      ;; Very long timestamp strings
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :startdate   "99999999999999999999"}))

      ;; Whitespace in timestamps (still valid as strings)
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :startdate   "  1763405834987  "}))

      ;; Special timestamp values
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :startdate   "0"
                   :enddate     "0"})))

    (testing "string field edge cases"
      ;; Very long external_id
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :external_id (apply str (repeat 1000 "a"))}))

      ;; Very long status
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :status      (apply str (repeat 500 "status "))}))

      ;; Very long step_type
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :step_type   (apply str (repeat 500 "type"))}))

      ;; Whitespace-only strings
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :external_id "   "}))

      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :status      "   "}))

      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :step_type   "   "}))

      ;; Newlines in strings
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :external_id "line1\nline2\nline3"}))

      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :status      "Status:\nRunning"}))

      ;; Special characters
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :external_id "!@#$%^&*()_+-=[]{}|;':\",./<>?"}))

      ;; Unicode characters
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :status      "状态：运行中"
                   :step_type   "类型"})))

    (testing "app_step_number edge cases"
      ;; Zero
      (is (valid? listing/AnalysisStep
                  {:step_number     1
                   :app_step_number 0}))

      ;; Very large
      (is (valid? listing/AnalysisStep
                  {:step_number     1
                   :app_step_number 2147483647}))

      ;; Negative (testing if allowed)
      (is (valid? listing/AnalysisStep
                  {:step_number     1
                   :app_step_number -1}))

      ;; app_step_number different from step_number
      (is (valid? listing/AnalysisStep
                  {:step_number     10
                   :app_step_number 1}))

      (is (valid? listing/AnalysisStep
                  {:step_number     1
                   :app_step_number 100})))

    (testing "timestamp ordering (startdate vs enddate)"
      ;; enddate before startdate (logically invalid but schema-valid)
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :startdate   "1763405835987"
                   :enddate     "1763405834987"}))

      ;; Same startdate and enddate
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :startdate   "1763405834987"
                   :enddate     "1763405834987"}))

      ;; Only startdate without enddate
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :startdate   "1763405834987"}))

      ;; Only enddate without startdate
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :enddate     "1763405835987"})))

    (testing "combinations of optional fields"
      ;; All optional fields except one
      (is (valid? listing/AnalysisStep
                  {:step_number     1
                   :startdate       "1763405834987"
                   :enddate         "1763405835987"
                   :status          "Submitted"
                   :app_step_number 1
                   :step_type       "Interactive"}))

      ;; Only timestamps
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :startdate   "1763405834987"
                   :enddate     "1763405835987"}))

      ;; Only identifiers
      (is (valid? listing/AnalysisStep
                  {:step_number     1
                   :external_id     "d81e33c4-b915-45be-8568-5c731f37cd5d"
                   :app_step_number 1}))

      ;; Only status and type
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :status      "Running"
                   :step_type   "Interactive"})))

    (testing "real-world-like scenarios"
      ;; Step just submitted
      (is (valid? listing/AnalysisStep
                  {:step_number 1
                   :external_id "newly-submitted-step"
                   :status      "Submitted"}))

      ;; Step currently running
      (is (valid? listing/AnalysisStep
                  {:step_number     2
                   :external_id     "running-step-id"
                   :startdate       "1763405834987"
                   :status          "Running"
                   :app_step_number 2
                   :step_type       "Interactive"}))

      ;; Step completed successfully
      (is (valid? listing/AnalysisStep
                  {:step_number     3
                   :external_id     "completed-step-id"
                   :startdate       "1763405800000"
                   :enddate         "1763405900000"
                   :status          "Completed"
                   :app_step_number 3
                   :step_type       "Batch"}))

      ;; Step that failed
      (is (valid? listing/AnalysisStep
                  {:step_number 4
                   :external_id "failed-step-id"
                   :startdate   "1763405834987"
                   :enddate     "1763405835000"
                   :status      "Failed"
                   :step_type   "DE"}))

      ;; Step canceled by user
      (is (valid? listing/AnalysisStep
                  {:step_number 5
                   :external_id "canceled-step-id"
                   :startdate   "1763405834987"
                   :enddate     "1763405834990"
                   :status      "Canceled"}))

      ;; Combined app steps
      (is (valid? listing/AnalysisStep
                  {:step_number     1
                   :app_step_number 3
                   :status          "Running"
                   :step_type       "Combined"})))))

(deftest test-AnalysisStepList
  (testing "AnalysisStepList validation"
    (testing "valid analysis step list objects"
      (are [obj] (valid? listing/AnalysisStepList obj)
        ;; Minimal valid step list with empty steps
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"
         :total       0}

        ;; With single step (minimal)
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1}]
         :timestamp   "1763405834987"
         :total       1}

        ;; With single step (full)
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number     1
                        :external_id     "d81e33c4-b915-45be-8568-5c731f37cd5d"
                        :startdate       "1763405834987"
                        :enddate         "1763405835987"
                        :status          "Completed"
                        :app_step_number 1
                        :step_type       "DE"}]
         :timestamp   "1763405836000"
         :total       1}

        ;; With multiple steps
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1
                        :status      "Completed"}
                       {:step_number 2
                        :status      "Running"}]
         :timestamp   "1763405834987"
         :total       2}

        ;; With multiple steps with full details
        {:analysis_id #uuid "abc12345-1234-5678-9abc-def012345678"
         :steps       [{:step_number     1
                        :external_id     "step-1-id"
                        :startdate       "1763405834987"
                        :enddate         "1763405835000"
                        :status          "Completed"
                        :app_step_number 1
                        :step_type       "DE"}
                       {:step_number     2
                        :external_id     "step-2-id"
                        :startdate       "1763405835001"
                        :enddate         "1763405836000"
                        :status          "Completed"
                        :app_step_number 2
                        :step_type       "DE"}
                       {:step_number 3
                        :external_id "step-3-id"
                        :startdate   "1763405836001"
                        :status      "Running"
                        :step_type   "Interactive"}]
         :timestamp   "1763405837000"
         :total       3}

        ;; With many steps
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1}
                       {:step_number 2}
                       {:step_number 3}
                       {:step_number 4}
                       {:step_number 5}]
         :timestamp   "1763405834987"
         :total       5}

        ;; With total larger than steps count (paging scenario)
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1}
                       {:step_number 2}]
         :timestamp   "1763405834987"
         :total       100}

        ;; With steps in various states
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1
                        :status      "Completed"
                        :startdate   "1763405834987"
                        :enddate     "1763405835000"}
                       {:step_number 2
                        :status      "Running"
                        :startdate   "1763405835001"}
                       {:step_number 3
                        :status      "Submitted"}
                       {:step_number 4
                        :status      "Failed"
                        :startdate   "1763405836000"
                        :enddate     "1763405836100"}]
         :timestamp   "1763405837000"
         :total       4}

        ;; With batch analysis steps
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number     1
                        :external_id     "batch-step-1"
                        :app_step_number 1
                        :step_type       "DE"}
                       {:step_number     2
                        :external_id     "batch-step-2"
                        :app_step_number 2
                        :step_type       "DE"}]
         :timestamp   "1763405834987"
         :total       2}

        ;; Large total value
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"
         :total       1000000}

        ;; With different timestamp formats (still strings)
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1}]
         :timestamp   "0"
         :total       1}))

    (testing "invalid analysis step list objects - missing required fields"
      (are [obj] (not (valid? listing/AnalysisStepList obj))
        ;; Empty map
        {}

        ;; Missing analysis_id
        {:steps     []
         :timestamp "1763405834987"
         :total     0}

        ;; Missing steps
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :timestamp   "1763405834987"
         :total       0}

        ;; Missing timestamp
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :total       0}

        ;; Missing total
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"}

        ;; Only analysis_id
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"}

        ;; Missing multiple fields
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []}

        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :timestamp   "1763405834987"}))

    (testing "invalid analysis step list objects - wrong field types"
      (are [obj] (not (valid? listing/AnalysisStepList obj))
        ;; analysis_id as string
        {:analysis_id "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"
         :total       0}

        ;; analysis_id as number
        {:analysis_id 123
         :steps       []
         :timestamp   "1763405834987"
         :total       0}

        ;; analysis_id as nil
        {:analysis_id nil
         :steps       []
         :timestamp   "1763405834987"
         :total       0}

        ;; steps as string
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       "not-a-vector"
         :timestamp   "1763405834987"
         :total       0}

        ;; steps as map
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       {:step_number 1}
         :timestamp   "1763405834987"
         :total       1}

        ;; steps as nil
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       nil
         :timestamp   "1763405834987"
         :total       0}

        ;; timestamp as number
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   1763405834987
         :total       0}

        ;; timestamp as nil
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   nil
         :total       0}

        ;; total as string
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"
         :total       "0"}

        ;; total as float
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"
         :total       0.5}

        ;; total as nil
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"
         :total       nil}

        ;; Multiple fields with wrong types
        {:analysis_id "not-a-uuid"
         :steps       "not-a-vector"
         :timestamp   123
         :total       "0"}))

    (testing "invalid analysis step list objects - extra fields not allowed"
      (are [obj] (not (valid? listing/AnalysisStepList obj))
        ;; Extra field
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"
         :total       0
         :extra_field "not allowed"}

        ;; Multiple extra fields
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"
         :total       0
         :foo         "bar"
         :baz         "qux"}

        ;; Extra field with valid data
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1}]
         :timestamp   "1763405834987"
         :total       1
         :status      "Running"}))

    (testing "invalid analysis step list objects - invalid nested steps"
      (are [obj] (not (valid? listing/AnalysisStepList obj))
        ;; Step missing required step_number
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:status "Running"}]
         :timestamp   "1763405834987"
         :total       1}

        ;; Step with invalid step_number type
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number "1"}]
         :timestamp   "1763405834987"
         :total       1}

        ;; Step with extra fields
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number  1
                        :extra_field "not allowed"}]
         :timestamp   "1763405834987"
         :total       1}

        ;; Multiple steps, one invalid
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1
                        :status      "Completed"}
                       {:status "Running"}]
         :timestamp   "1763405834987"
         :total       2}

        ;; Step with invalid nested field type
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1
                        :startdate   123}]
         :timestamp   "1763405834987"
         :total       1}

        ;; Step with app_step_number as string
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number     1
                        :app_step_number "1"}]
         :timestamp   "1763405834987"
         :total       1}

        ;; Steps as vector with non-map elements
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       ["step1" "step2"]
         :timestamp   "1763405834987"
         :total       2}

        ;; Steps with nil element
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [nil]
         :timestamp   "1763405834987"
         :total       1}))

    (testing "realistic usage scenarios"
      ;; Analysis with no steps yet (just submitted)
      (is (valid? listing/AnalysisStepList
                  {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
                   :steps       []
                   :timestamp   "1763405834987"
                   :total       0}))

      ;; Analysis with one completed step
      (is (valid? listing/AnalysisStepList
                  {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
                   :steps       [{:step_number 1
                                  :external_id "completed-step-id"
                                  :startdate   "1763405834987"
                                  :enddate     "1763405835987"
                                  :status      "Completed"
                                  :step_type   "DE"}]
                   :timestamp   "1763405836000"
                   :total       1}))

      ;; Multi-step analysis in progress
      (is (valid? listing/AnalysisStepList
                  {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
                   :steps       [{:step_number     1
                                  :external_id     "step-1-completed"
                                  :startdate       "1763405834987"
                                  :enddate         "1763405835000"
                                  :status          "Completed"
                                  :app_step_number 1
                                  :step_type       "DE"}
                                 {:step_number     2
                                  :external_id     "step-2-running"
                                  :startdate       "1763405835001"
                                  :status          "Running"
                                  :app_step_number 2
                                  :step_type       "DE"}
                                 {:step_number     3
                                  :status          "Submitted"
                                  :app_step_number 3
                                  :step_type       "DE"}]
                   :timestamp   "1763405836000"
                   :total       3}))

      ;; Interactive analysis step
      (is (valid? listing/AnalysisStepList
                  {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
                   :steps       [{:step_number 1
                                  :external_id "interactive-step-id"
                                  :startdate   "1763405834987"
                                  :status      "Running"
                                  :step_type   "Interactive"}]
                   :timestamp   "1763405835000"
                   :total       1}))

      ;; Failed analysis step
      (is (valid? listing/AnalysisStepList
                  {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
                   :steps       [{:step_number 1
                                  :external_id "failed-step-id"
                                  :startdate   "1763405834987"
                                  :enddate     "1763405835000"
                                  :status      "Failed"
                                  :step_type   "DE"}]
                   :timestamp   "1763405836000"
                   :total       1}))

      ;; Analysis with combined app steps
      (is (valid? listing/AnalysisStepList
                  {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
                   :steps       [{:step_number     1
                                  :app_step_number 3
                                  :status          "Running"
                                  :step_type       "Combined"}]
                   :timestamp   "1763405834987"
                   :total       1})))))

(deftest test-AnalysisStatusUpdate
  (testing "AnalysisStatusUpdate validation"
    (testing "valid status update objects"
      (are [obj] (valid? listing/AnalysisStatusUpdate obj)
        ;; Minimal valid status update with all required fields
        {:status    "Running"
         :message   "Downloading input files."
         :timestamp "1763405834987"}

        ;; Status update with running status
        {:status    "Running"
         :message   "Processing data."
         :timestamp "1763405835000"}

        ;; Status update with completed status
        {:status    "Completed"
         :message   "Analysis completed successfully."
         :timestamp "1763405836000"}

        ;; Status update with failed status
        {:status    "Failed"
         :message   "An error occurred during processing."
         :timestamp "1763405837000"}

        ;; Status update with submitted status
        {:status    "Submitted"
         :message   "Job submitted to execution system."
         :timestamp "1763405838000"}

        ;; Status update with empty message
        {:status    "Running"
         :message   ""
         :timestamp "1763405839000"}

        ;; Status update with long message
        {:status    "Running"
         :message   "This is a very long message describing what is happening in great detail during the analysis execution process."
         :timestamp "1763405840000"}))

    (testing "invalid status update objects - missing required fields"
      (are [obj] (not (valid? listing/AnalysisStatusUpdate obj))
        ;; Missing status
        {:message   "Downloading input files."
         :timestamp "1763405834987"}

        ;; Missing message
        {:status    "Running"
         :timestamp "1763405834987"}

        ;; Missing timestamp
        {:status  "Running"
         :message "Downloading input files."}

        ;; Empty map
        {}))

    (testing "invalid status update objects - wrong field types"
      (are [obj] (not (valid? listing/AnalysisStatusUpdate obj))
        ;; status as number
        {:status    123
         :message   "Downloading input files."
         :timestamp "1763405834987"}

        ;; status as nil
        {:status    nil
         :message   "Downloading input files."
         :timestamp "1763405834987"}

        ;; status as empty string (NonBlankString should reject this)
        {:status    ""
         :message   "Downloading input files."
         :timestamp "1763405834987"}

        ;; status as whitespace only (NonBlankString should reject this)
        {:status    "   "
         :message   "Downloading input files."
         :timestamp "1763405834987"}

        ;; message as number
        {:status    "Running"
         :message   123
         :timestamp "1763405834987"}

        ;; message as nil
        {:status    "Running"
         :message   nil
         :timestamp "1763405834987"}

        ;; timestamp as number
        {:status    "Running"
         :message   "Downloading input files."
         :timestamp 1763405834987}

        ;; timestamp as nil
        {:status    "Running"
         :message   "Downloading input files."
         :timestamp nil}))

    (testing "invalid status update objects - extra fields not allowed"
      (are [obj] (not (valid? listing/AnalysisStatusUpdate obj))
        ;; Extra field
        {:status      "Running"
         :message     "Downloading input files."
         :timestamp   "1763405834987"
         :extra_field "not allowed"}

        ;; Multiple extra fields
        {:status "Running"
         :message "Downloading input files."
         :timestamp "1763405834987"
         :foo    "bar"
         :baz    "qux"}))))

(deftest test-AnalysisStepHistory
  (testing "AnalysisStepHistory validation"
    (testing "valid step history objects"
      (are [obj] (valid? listing/AnalysisStepHistory obj)
        ;; Minimal step history with required fields from AnalysisStep and empty updates
        {:step_number 1
         :updates     []}

        ;; Step history with single update
        {:step_number 1
         :external_id "step-1-id"
         :startdate   "1763405834987"
         :enddate     "1763405836000"
         :status      "Completed"
         :step_type   "DE"
         :updates     [{:status    "Submitted"
                        :message   "Job submitted."
                        :timestamp "1763405834987"}]}

        ;; Step history with multiple updates showing progression
        {:step_number     1
         :external_id     "step-1-id"
         :startdate       "1763405834987"
         :enddate         "1763405838000"
         :status          "Completed"
         :app_step_number 1
         :step_type       "DE"
         :updates         [{:status    "Submitted"
                            :message   "Job submitted to execution system."
                            :timestamp "1763405834987"}
                           {:status    "Running"
                            :message   "Downloading input files."
                            :timestamp "1763405835000"}
                           {:status    "Running"
                            :message   "Processing data."
                            :timestamp "1763405836000"}
                           {:status    "Completed"
                            :message   "Analysis completed successfully."
                            :timestamp "1763405838000"}]}

        ;; Failed step with error updates
        {:step_number 1
         :external_id "failed-step-id"
         :startdate   "1763405834987"
         :enddate     "1763405836000"
         :status      "Failed"
         :step_type   "DE"
         :updates     [{:status    "Submitted"
                        :message   "Job submitted."
                        :timestamp "1763405834987"}
                       {:status    "Running"
                        :message   "Starting execution."
                        :timestamp "1763405835000"}
                       {:status    "Failed"
                        :message   "An error occurred during processing."
                        :timestamp "1763405836000"}]}

        ;; Interactive step history
        {:step_number 1
         :external_id "interactive-step-id"
         :startdate   "1763405834987"
         :status      "Running"
         :step_type   "Interactive"
         :updates     [{:status    "Submitted"
                        :message   "Interactive job submitted."
                        :timestamp "1763405834987"}
                       {:status    "Running"
                        :message   "Interactive session started."
                        :timestamp "1763405835000"}]}

        ;; Step with app_step_number
        {:step_number     2
         :app_step_number 5
         :external_id     "combined-step-id"
         :startdate       "1763405834987"
         :status          "Running"
         :step_type       "Combined"
         :updates         [{:status    "Running"
                            :message   "Processing combined steps."
                            :timestamp "1763405835000"}]}

        ;; Running step without enddate
        {:step_number 1
         :external_id "running-step-id"
         :startdate   "1763405834987"
         :status      "Running"
         :step_type   "DE"
         :updates     [{:status    "Submitted"
                        :message   "Job submitted."
                        :timestamp "1763405834987"}
                       {:status    "Running"
                        :message   "Currently processing."
                        :timestamp "1763405835000"}]}))

    (testing "invalid step history objects - missing required fields from AnalysisStep"
      (are [obj] (not (valid? listing/AnalysisStepHistory obj))
        ;; Missing step_number
        {:updates [{:status    "Running"
                    :message   "Processing."
                    :timestamp "1763405834987"}]}

        ;; Missing updates
        {:step_number 1
         :external_id "step-1-id"
         :status      "Running"}

        ;; Empty map
        {}))

    (testing "invalid step history objects - wrong field types from AnalysisStep"
      (are [obj] (not (valid? listing/AnalysisStepHistory obj))
        ;; step_number as string
        {:step_number "1"
         :updates     []}

        ;; external_id as number
        {:step_number 1
         :external_id 123
         :updates     []}

        ;; startdate as number
        {:step_number 1
         :startdate   1763405834987
         :updates     []}

        ;; enddate as number
        {:step_number 1
         :enddate     1763405836000
         :updates     []}

        ;; status as number
        {:step_number 1
         :status      123
         :updates     []}

        ;; app_step_number as string
        {:step_number     1
         :app_step_number "1"
         :updates         []}

        ;; step_type as number
        {:step_number 1
         :step_type   123
         :updates     []}))

    (testing "invalid step history objects - wrong updates type"
      (are [obj] (not (valid? listing/AnalysisStepHistory obj))
        ;; updates as string
        {:step_number 1
         :updates     "not a vector"}

        ;; updates as nil
        {:step_number 1
         :updates     nil}

        ;; updates as single object instead of vector
        {:step_number 1
         :updates     {:status    "Running"
                       :message   "Processing."
                       :timestamp "1763405834987"}}

        ;; updates vector with invalid status update (missing fields)
        {:step_number 1
         :updates     [{:status  "Running"
                        :message "Processing."}]}

        ;; updates vector with invalid status update (wrong types)
        {:step_number 1
         :updates     [{:status    123
                        :message   "Processing."
                        :timestamp "1763405834987"}]}))

    (testing "invalid step history objects - extra fields not allowed"
      (are [obj] (not (valid? listing/AnalysisStepHistory obj))
        ;; Extra field
        {:step_number  1
         :updates      []
         :extra_field  "not allowed"}

        ;; Multiple extra fields
        {:step_number 1
         :updates     []
         :foo         "bar"
         :baz         "qux"}))))

(deftest test-AnalysisHistory
  (testing "AnalysisHistory validation"
    (testing "valid analysis history objects"
      (are [obj] (valid? listing/AnalysisHistory obj)
        ;; Minimal analysis history with empty steps
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"
         :total       0}

        ;; Single step analysis history with no updates
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1
                        :external_id "step-1-id"
                        :startdate   "1763405834987"
                        :enddate     "1763405836000"
                        :status      "Completed"
                        :step_type   "DE"
                        :updates     []}]
         :timestamp   "1763405837000"
         :total       1}

        ;; Single step with status updates
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1
                        :external_id "step-1-id"
                        :startdate   "1763405834987"
                        :enddate     "1763405838000"
                        :status      "Completed"
                        :step_type   "DE"
                        :updates     [{:status    "Submitted"
                                       :message   "Job submitted."
                                       :timestamp "1763405834987"}
                                      {:status    "Running"
                                       :message   "Processing data."
                                       :timestamp "1763405835000"}
                                      {:status    "Completed"
                                       :message   "Analysis completed."
                                       :timestamp "1763405838000"}]}]
         :timestamp   "1763405839000"
         :total       1}

        ;; Multi-step analysis history
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number     1
                        :external_id     "step-1-id"
                        :startdate       "1763405834987"
                        :enddate         "1763405836000"
                        :status          "Completed"
                        :app_step_number 1
                        :step_type       "DE"
                        :updates         [{:status    "Submitted"
                                           :message   "Step 1 submitted."
                                           :timestamp "1763405834987"}
                                          {:status    "Running"
                                           :message   "Step 1 running."
                                           :timestamp "1763405835000"}
                                          {:status    "Completed"
                                           :message   "Step 1 completed."
                                           :timestamp "1763405836000"}]}
                       {:step_number     2
                        :external_id     "step-2-id"
                        :startdate       "1763405836001"
                        :enddate         "1763405838000"
                        :status          "Completed"
                        :app_step_number 2
                        :step_type       "DE"
                        :updates         [{:status    "Submitted"
                                           :message   "Step 2 submitted."
                                           :timestamp "1763405836001"}
                                          {:status    "Running"
                                           :message   "Step 2 running."
                                           :timestamp "1763405837000"}
                                          {:status    "Completed"
                                           :message   "Step 2 completed."
                                           :timestamp "1763405838000"}]}
                       {:step_number     3
                        :external_id     "step-3-id"
                        :startdate       "1763405838001"
                        :status          "Running"
                        :app_step_number 3
                        :step_type       "DE"
                        :updates         [{:status    "Submitted"
                                           :message   "Step 3 submitted."
                                           :timestamp "1763405838001"}
                                          {:status    "Running"
                                           :message   "Step 3 running."
                                           :timestamp "1763405839000"}]}]
         :timestamp   "1763405840000"
         :total       3}

        ;; Failed analysis history
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1
                        :external_id "failed-step-id"
                        :startdate   "1763405834987"
                        :enddate     "1763405836000"
                        :status      "Failed"
                        :step_type   "DE"
                        :updates     [{:status    "Submitted"
                                       :message   "Job submitted."
                                       :timestamp "1763405834987"}
                                      {:status    "Running"
                                       :message   "Starting execution."
                                       :timestamp "1763405835000"}
                                      {:status    "Failed"
                                       :message   "An error occurred."
                                       :timestamp "1763405836000"}]}]
         :timestamp   "1763405837000"
         :total       1}

        ;; Interactive analysis history
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1
                        :external_id "interactive-step-id"
                        :startdate   "1763405834987"
                        :status      "Running"
                        :step_type   "Interactive"
                        :updates     [{:status    "Submitted"
                                       :message   "Interactive job submitted."
                                       :timestamp "1763405834987"}
                                      {:status    "Running"
                                       :message   "Session started."
                                       :timestamp "1763405835000"}]}]
         :timestamp   "1763405836000"
         :total       1}

        ;; Analysis with combined app steps
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number     1
                        :app_step_number 3
                        :external_id     "combined-step-id"
                        :startdate       "1763405834987"
                        :status          "Running"
                        :step_type       "Combined"
                        :updates         [{:status    "Running"
                                           :message   "Processing combined steps."
                                           :timestamp "1763405835000"}]}]
         :timestamp   "1763405836000"
         :total       1}))

    (testing "invalid analysis history objects - missing required fields from AnalysisStepList"
      (are [obj] (not (valid? listing/AnalysisHistory obj))
        ;; Missing analysis_id
        {:steps     []
         :timestamp "1763405834987"
         :total     0}

        ;; Missing steps
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :timestamp   "1763405834987"
         :total       0}

        ;; Missing timestamp
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :total       0}

        ;; Missing total
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"}

        ;; Empty map
        {}))

    (testing "invalid analysis history objects - wrong field types from AnalysisStepList"
      (are [obj] (not (valid? listing/AnalysisHistory obj))
        ;; analysis_id as string
        {:analysis_id "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"
         :total       0}

        ;; steps as nil
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       nil
         :timestamp   "1763405834987"
         :total       0}

        ;; steps as string
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       "not a vector"
         :timestamp   "1763405834987"
         :total       0}

        ;; timestamp as number
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   1763405834987
         :total       0}

        ;; total as string
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"
         :total       "0"}))

    (testing "invalid analysis history objects - steps with invalid AnalysisStepHistory"
      (are [obj] (not (valid? listing/AnalysisHistory obj))
        ;; Step missing step_number
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:updates []}]
         :timestamp   "1763405834987"
         :total       1}

        ;; Step missing updates
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1}]
         :timestamp   "1763405834987"
         :total       1}

        ;; Step with invalid update (missing fields)
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1
                        :updates     [{:status "Running"}]}]
         :timestamp   "1763405834987"
         :total       1}

        ;; Step with invalid update (wrong types)
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1
                        :updates     [{:status    123
                                       :message   "Processing."
                                       :timestamp "1763405834987"}]}]
         :timestamp   "1763405834987"
         :total       1}

        ;; Step with updates as non-vector
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       [{:step_number 1
                        :updates     "not a vector"}]
         :timestamp   "1763405834987"
         :total       1}))

    (testing "invalid analysis history objects - extra fields not allowed"
      (are [obj] (not (valid? listing/AnalysisHistory obj))
        ;; Extra field
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"
         :total       0
         :extra_field "not allowed"}

        ;; Multiple extra fields
        {:analysis_id #uuid "6a62b1db-453c-49e0-ae23-b0f7974aa0f8"
         :steps       []
         :timestamp   "1763405834987"
         :total       0
         :foo         "bar"
         :baz         "qux"}))))
