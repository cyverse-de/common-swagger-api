(ns common-swagger-api.malli.callbacks-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.callbacks :as c]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AgaveJobStatusUpdateParams
  (testing "AgaveJobStatusUpdateParams validation"
    (testing "valid params"
      ;; All required fields present
      (is (valid? c/AgaveJobStatusUpdateParams
                  {:status "STAGING_INPUTS"
                   :external-id "0c745662-d045-45e8-b89f-e94fd32d169b-007"
                   :end-time "2006-05-04T03:02:01Z"}))
      ;; Different valid values
      (is (valid? c/AgaveJobStatusUpdateParams
                  {:status "RUNNING"
                   :external-id "abc123-def456-ghi789"
                   :end-time "2023-12-15T10:30:45Z"}))
      ;; Empty strings are technically valid strings (unless NonBlankString is used)
      (is (valid? c/AgaveJobStatusUpdateParams
                  {:status ""
                   :external-id ""
                   :end-time ""})))

    (testing "invalid params - missing required fields"
      ;; Missing status
      (is (not (valid? c/AgaveJobStatusUpdateParams
                       {:external-id "0c745662-d045-45e8-b89f-e94fd32d169b-007"
                        :end-time "2006-05-04T03:02:01Z"})))
      ;; Missing external-id
      (is (not (valid? c/AgaveJobStatusUpdateParams
                       {:status "STAGING_INPUTS"
                        :end-time "2006-05-04T03:02:01Z"})))
      ;; Missing end-time
      (is (not (valid? c/AgaveJobStatusUpdateParams
                       {:status "STAGING_INPUTS"
                        :external-id "0c745662-d045-45e8-b89f-e94fd32d169b-007"})))
      ;; Empty map
      (is (not (valid? c/AgaveJobStatusUpdateParams {}))))

    (testing "invalid params - wrong types"
      ;; status is not a string
      (is (not (valid? c/AgaveJobStatusUpdateParams
                       {:status 123
                        :external-id "0c745662-d045-45e8-b89f-e94fd32d169b-007"
                        :end-time "2006-05-04T03:02:01Z"})))
      ;; external-id is not a string
      (is (not (valid? c/AgaveJobStatusUpdateParams
                       {:status "STAGING_INPUTS"
                        :external-id #uuid "0c745662-d045-45e8-b89f-e94fd32d169b"
                        :end-time "2006-05-04T03:02:01Z"})))
      ;; end-time is not a string
      (is (not (valid? c/AgaveJobStatusUpdateParams
                       {:status "STAGING_INPUTS"
                        :external-id "0c745662-d045-45e8-b89f-e94fd32d169b-007"
                        :end-time 1234567890})))
      ;; nil values
      (is (not (valid? c/AgaveJobStatusUpdateParams
                       {:status nil
                        :external-id "0c745662-d045-45e8-b89f-e94fd32d169b-007"
                        :end-time "2006-05-04T03:02:01Z"}))))

    (testing "invalid params - closed map rejects extra fields"
      ;; Extra field not allowed due to :closed true
      (is (not (valid? c/AgaveJobStatusUpdateParams
                       {:status "STAGING_INPUTS"
                        :external-id "0c745662-d045-45e8-b89f-e94fd32d169b-007"
                        :end-time "2006-05-04T03:02:01Z"
                        :extra-field "not allowed"})))
      (is (not (valid? c/AgaveJobStatusUpdateParams
                       {:status "STAGING_INPUTS"
                        :external-id "0c745662-d045-45e8-b89f-e94fd32d169b-007"
                        :end-time "2006-05-04T03:02:01Z"
                        :foo "bar"
                        :baz "qux"}))))))

(deftest test-AgaveJobStatusUpdate
  (testing "AgaveJobStatusUpdate validation"
    (testing "valid updates"
      ;; Just required field
      (is (valid? c/AgaveJobStatusUpdate
                  {:lastUpdated "2006-05-04T03:02:01Z"}))
      ;; With additional fields (open map allows this)
      (is (valid? c/AgaveJobStatusUpdate
                  {:lastUpdated "2006-05-04T03:02:01Z"
                   :status "RUNNING"}))
      (is (valid? c/AgaveJobStatusUpdate
                  {:lastUpdated "2023-12-15T10:30:45Z"
                   :job-id "abc123"
                   :progress 50
                   :metadata {:user "alice"
                              :project "test"}}))
      ;; Multiple additional keyword-value pairs
      (is (valid? c/AgaveJobStatusUpdate
                  {:lastUpdated "2006-05-04T03:02:01Z"
                   :foo "bar"
                   :baz 123
                   :qux true
                   :nested {:a 1 :b 2}})))

    (testing "invalid updates - missing required field"
      ;; Missing lastUpdated
      (is (not (valid? c/AgaveJobStatusUpdate {})))
      (is (not (valid? c/AgaveJobStatusUpdate
                       {:status "RUNNING"
                        :job-id "abc123"}))))

    (testing "invalid updates - wrong type for required field"
      ;; lastUpdated is not a string
      (is (not (valid? c/AgaveJobStatusUpdate
                       {:lastUpdated 1234567890})))
      (is (not (valid? c/AgaveJobStatusUpdate
                       {:lastUpdated nil})))
      (is (not (valid? c/AgaveJobStatusUpdate
                       {:lastUpdated true}))))))

(deftest test-TapisJobStatusUpdateEvent
  (testing "TapisJobStatusUpdateEvent validation"
    (testing "valid events"
      ;; All required fields
      (is (valid? c/TapisJobStatusUpdateEvent
                  {:timestamp "2006-05-04T03:02:01Z"
                   :type "STAGING_INPUTS"
                   :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"}))
      ;; With additional fields (open map)
      (is (valid? c/TapisJobStatusUpdateEvent
                  {:timestamp "2023-12-15T10:30:45Z"
                   :type "RUNNING"
                   :data "{\"status\":\"active\"}"
                   :job-id "xyz789"}))
      ;; Multiple additional fields
      (is (valid? c/TapisJobStatusUpdateEvent
                  {:timestamp "2006-05-04T03:02:01Z"
                   :type "STAGING_INPUTS"
                   :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"
                   :extra1 "value1"
                   :extra2 42
                   :extra3 {:nested "map"}})))

    (testing "invalid events - missing required fields"
      ;; Empty map
      (is (not (valid? c/TapisJobStatusUpdateEvent {})))
      ;; Missing timestamp
      (is (not (valid? c/TapisJobStatusUpdateEvent
                       {:type "STAGING_INPUTS"
                        :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"})))
      ;; Missing type
      (is (not (valid? c/TapisJobStatusUpdateEvent
                       {:timestamp "2006-05-04T03:02:01Z"
                        :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"})))
      ;; Missing data
      (is (not (valid? c/TapisJobStatusUpdateEvent
                       {:timestamp "2006-05-04T03:02:01Z"
                        :type "STAGING_INPUTS"}))))

    (testing "invalid events - wrong types"
      ;; timestamp is not a string
      (is (not (valid? c/TapisJobStatusUpdateEvent
                       {:timestamp 1234567890
                        :type "STAGING_INPUTS"
                        :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"})))
      ;; type is not a string
      (is (not (valid? c/TapisJobStatusUpdateEvent
                       {:timestamp "2006-05-04T03:02:01Z"
                        :type :staging-inputs
                        :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"})))
      ;; data is not a string
      (is (not (valid? c/TapisJobStatusUpdateEvent
                       {:timestamp "2006-05-04T03:02:01Z"
                        :type "STAGING_INPUTS"
                        :data {:timestamp "2006-05-04T03:02:01Z"}})))
      ;; nil values
      (is (not (valid? c/TapisJobStatusUpdateEvent
                       {:timestamp nil
                        :type "STAGING_INPUTS"
                        :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"})))
      (is (not (valid? c/TapisJobStatusUpdateEvent
                       {:timestamp "2006-05-04T03:02:01Z"
                        :type nil
                        :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"})))
      (is (not (valid? c/TapisJobStatusUpdateEvent
                       {:timestamp "2006-05-04T03:02:01Z"
                        :type "STAGING_INPUTS"
                        :data nil})))))

(deftest test-TapisJobStatusUpdate
  (testing "TapisJobStatusUpdate validation"
    (testing "valid updates"
      ;; Just required field
      (is (valid? c/TapisJobStatusUpdate
                  {:event {:timestamp "2006-05-04T03:02:01Z"
                           :type "STAGING_INPUTS"
                           :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"}}))
      ;; With additional fields (open map)
      (is (valid? c/TapisJobStatusUpdate
                  {:event {:timestamp "2006-05-04T03:02:01Z"
                           :type "STAGING_INPUTS"
                           :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"}
                   :job-id "abc123"}))
      ;; Multiple additional fields
      (is (valid? c/TapisJobStatusUpdate
                  {:event {:timestamp "2023-12-15T10:30:45Z"
                           :type "RUNNING"
                           :data "{\"status\":\"active\"}"}
                   :status "RUNNING"
                   :progress 75
                   :metadata {:user "bob"}}))
      ;; Event with additional fields
      (is (valid? c/TapisJobStatusUpdate
                  {:event {:timestamp "2006-05-04T03:02:01Z"
                           :type "STAGING_INPUTS"
                           :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"
                           :extra "field"}
                   :custom-field "custom-value"})))

    (testing "invalid updates - missing required field"
      ;; Empty map
      (is (not (valid? c/TapisJobStatusUpdate {})))
      ;; Missing event
      (is (not (valid? c/TapisJobStatusUpdate
                       {:status "RUNNING"
                        :progress 50}))))

    (testing "invalid updates - wrong type for event"
      ;; event is not a map
      (is (not (valid? c/TapisJobStatusUpdate
                       {:event "not-a-map"})))
      (is (not (valid? c/TapisJobStatusUpdate
                       {:event nil})))
      (is (not (valid? c/TapisJobStatusUpdate
                       {:event []}))))

    (testing "invalid updates - invalid event structure"
      ;; event is missing required fields
      (is (not (valid? c/TapisJobStatusUpdate
                       {:event {}})))
      (is (not (valid? c/TapisJobStatusUpdate
                       {:event {:timestamp "2006-05-04T03:02:01Z"}})))
      (is (not (valid? c/TapisJobStatusUpdate
                       {:event {:timestamp "2006-05-04T03:02:01Z"
                                :type "STAGING_INPUTS"}})))
      ;; event has wrong types
      (is (not (valid? c/TapisJobStatusUpdate
                       {:event {:timestamp 1234567890
                                :type "STAGING_INPUTS"
                                :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"}})))
      (is (not (valid? c/TapisJobStatusUpdate
                       {:event {:timestamp "2006-05-04T03:02:01Z"
                                :type :staging-inputs
                                :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"}})))
      (is (not (valid? c/TapisJobStatusUpdate
                       {:event {:timestamp "2006-05-04T03:02:01Z"
                                :type "STAGING_INPUTS"
                                :data {:timestamp "2006-05-04T03:02:01Z"}}}))))))

(deftest test-edge-cases
  (testing "Edge cases for string fields"
    (testing "special characters in strings"
      ;; Special characters should be valid in string fields
      (is (valid? c/AgaveJobStatusUpdateParams
                  {:status "STATUS-WITH_SPECIAL.CHARS!@#$%"
                   :external-id "id-with-unicode-\u00e9\u00f1"
                   :end-time "2006-05-04T03:02:01Z"}))
      (is (valid? c/AgaveJobStatusUpdate
                  {:lastUpdated "timestamp-with-\ttabs\nand\nnewlines"}))
      (is (valid? c/TapisJobStatusUpdateEvent
                  {:timestamp "2006-05-04T03:02:01Z"
                   :type "TYPE_WITH_UNDERSCORES"
                   :data "{\"key\":\"value with \\\"escaped\\\" quotes\"}"})))

    (testing "very long strings"
      ;; Long strings should still be valid
      (let [long-string (apply str (repeat 1000 "x"))]
        (is (valid? c/AgaveJobStatusUpdateParams
                    {:status long-string
                     :external-id long-string
                     :end-time long-string}))))))

(deftest test-open-vs-closed-maps
  (testing "Verifying open vs closed map behavior"
    (testing "AgaveJobStatusUpdateParams is closed - rejects extra fields"
      (is (not (valid? c/AgaveJobStatusUpdateParams
                       {:status "STAGING_INPUTS"
                        :external-id "0c745662-d045-45e8-b89f-e94fd32d169b-007"
                        :end-time "2006-05-04T03:02:01Z"
                        :unexpected-key "value"}))))

    (testing "AgaveJobStatusUpdate is open - accepts extra fields"
      (is (valid? c/AgaveJobStatusUpdate
                  {:lastUpdated "2006-05-04T03:02:01Z"
                   :any-keyword "any-value"
                   :another-keyword 123
                   :yet-another true})))

    (testing "TapisJobStatusUpdateEvent is open - accepts extra fields"
      (is (valid? c/TapisJobStatusUpdateEvent
                  {:timestamp "2006-05-04T03:02:01Z"
                   :type "STAGING_INPUTS"
                   :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"
                   :custom-field-1 "value1"
                   :custom-field-2 "value2"})))

    (testing "TapisJobStatusUpdate is open - accepts extra fields"
      (is (valid? c/TapisJobStatusUpdate
                  {:event {:timestamp "2006-05-04T03:02:01Z"
                           :type "STAGING_INPUTS"
                           :data "{\"timestamp\":\"2006-05-04T03:02:01Z\"}"}
                   :additional-data "some-value"
                   :more-data {:nested "map"}}))))))
