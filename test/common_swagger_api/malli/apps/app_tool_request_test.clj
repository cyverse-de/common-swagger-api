(ns common-swagger-api.malli.apps.app-tool-request-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common-swagger-api.malli.apps :as apps]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AppToolRequest
  (testing "AppToolRequest validation"
    (testing "valid tool request - minimal required fields from Tool base"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :name "samtools"
                   :version "1.15.1"
                   :type "executable"}))
      (is (valid? apps/AppToolRequest
                  {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                   :name "blast"
                   :version "2.14.0"
                   :type "docker"})))

    (testing "valid tool request - with optional Tool fields"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                   :name "bowtie2"
                   :description "Fast and sensitive read alignment"
                   :version "2.5.0"
                   :type "executable"}))
      (is (valid? apps/AppToolRequest
                  {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                   :name "trinity"
                   :description "De novo transcript assembly from RNA-Seq data"
                   :attribution "Broad Institute"
                   :location "/usr/local/bin"
                   :version "2.13.2"
                   :type "executable"
                   :restricted false
                   :time_limit_seconds 7200
                   :interactive false})))

    (testing "valid tool request - with is_public field (optional in AppToolRequest)"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                   :name "samtools"
                   :version "1.15.1"
                   :type "executable"
                   :is_public true}))
      (is (valid? apps/AppToolRequest
                  {:id #uuid "def67890-abc1-2345-6789-0abcdef12345"
                   :name "blast"
                   :version "2.14.0"
                   :type "docker"
                   :is_public false})))

    (testing "valid tool request - without is_public field (made optional)"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                   :name "bowtie2"
                   :version "2.5.0"
                   :type "executable"})))

    (testing "valid tool request - with permission field (optional in AppToolRequest)"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                   :name "trinity"
                   :version "2.13.2"
                   :type "executable"
                   :permission "own"}))
      (is (valid? apps/AppToolRequest
                  {:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                   :name "cufflinks"
                   :version "2.2.1"
                   :type "executable"
                   :permission "read"})))

    (testing "valid tool request - without permission field (made optional)"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "333a0123-c45d-67e8-f901-234567890abc"
                   :name "tophat"
                   :version "2.1.0"
                   :type "executable"})))

    (testing "valid tool request - with implementation field (ToolImplementation with test)"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "444a0123-c45d-67e8-f901-234567890abc"
                   :name "hisat2"
                   :version "2.2.1"
                   :type "executable"
                   :implementation {:implementor "Jane Smith"
                                    :implementor_email "jane.smith@example.org"
                                    :test {:input_files ["/test/input.txt"]
                                           :output_files ["/test/output.txt"]}}})))

    (testing "valid tool request - without implementation field (made optional)"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "555a0123-c45d-67e8-f901-234567890abc"
                   :name "star"
                   :version "2.7.10"
                   :type "executable"})))

    (testing "valid tool request - with container field (ToolContainer)"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "666a0123-c45d-67e8-f901-234567890abc"
                   :name "kallisto"
                   :version "0.48.0"
                   :type "docker"
                   :container {:id #uuid "777a0123-c45d-67e8-f901-234567890abc"
                               :image {:id #uuid "888a0123-c45d-67e8-f901-234567890abc"
                                       :name "biocontainers/kallisto"}}}))
      (is (valid? apps/AppToolRequest
                  {:id #uuid "999a0123-c45d-67e8-f901-234567890abc"
                   :name "salmon"
                   :version "1.9.0"
                   :type "docker"
                   :container {:id #uuid "aaaa0123-c45d-67e8-f901-234567890abc"
                               :cpu_shares 1024
                               :memory_limit 2147483648
                               :min_memory_limit 1073741824
                               :min_cpu_cores 1.0
                               :max_cpu_cores 4.0
                               :network_mode "bridge"
                               :working_directory "/workspace"
                               :image {:id #uuid "bbbb0123-c45d-67e8-f901-234567890abc"
                                       :name "combinelab/salmon"
                                       :tag "1.9.0"}}})))

    (testing "valid tool request - without container field (made optional)"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "cccc0123-c45d-67e8-f901-234567890abc"
                   :name "rsem"
                   :version "1.3.3"
                   :type "executable"})))

    (testing "valid tool request - with deprecated field (AppToolRequest-specific)"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "dddd0123-c45d-67e8-f901-234567890abc"
                   :name "deprecated-tool"
                   :version "1.0.0"
                   :type "executable"
                   :deprecated true}))
      (is (valid? apps/AppToolRequest
                  {:id #uuid "eeee0123-c45d-67e8-f901-234567890abc"
                   :name "active-tool"
                   :version "2.0.0"
                   :type "executable"
                   :deprecated false})))

    (testing "valid tool request - without deprecated field (optional)"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "ffff0123-c45d-67e8-f901-234567890abc"
                   :name "standard-tool"
                   :version "1.5.0"
                   :type "executable"})))

    (testing "valid tool request - with tool_request field (from ToolListingItem)"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "0000aaaa-c45d-67e8-f901-234567890abc"
                   :name "requested-tool"
                   :version "3.0.0"
                   :type "executable"
                   :tool_request {:id #uuid "1111aaaa-c45d-67e8-f901-234567890abc"
                                  :status "Approved"}})))

    (testing "valid tool request - without tool_request field (optional)"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "2222aaaa-c45d-67e8-f901-234567890abc"
                   :name "unrequested-tool"
                   :version "1.0.0"
                   :type "executable"})))

    (testing "valid tool request - all optional fields present"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "3333aaaa-c45d-67e8-f901-234567890abc"
                   :name "complete-tool"
                   :description "A fully specified tool with all possible fields"
                   :attribution "John Doe et al."
                   :location "/opt/tools/complete"
                   :version "5.2.1"
                   :type "docker"
                   :restricted true
                   :time_limit_seconds 3600
                   :interactive false
                   :is_public true
                   :permission "own"
                   :implementation {:implementor "Alice Johnson"
                                    :implementor_email "alice@example.org"
                                    :test {:input_files ["/test/input.txt"]
                                           :output_files ["/test/output.txt"]}}
                   :container {:id #uuid "4444aaaa-c45d-67e8-f901-234567890abc"
                               :cpu_shares 512
                               :pids_limit 100
                               :memory_limit 1073741824
                               :min_memory_limit 536870912
                               :min_cpu_cores 0.5
                               :max_cpu_cores 2.0
                               :min_disk_space 10737418240
                               :network_mode "none"
                               :working_directory "/work"
                               :name "complete-container"
                               :entrypoint "/bin/bash"
                               :skip_tmp_mount true
                               :uid 1000
                               :image {:id #uuid "5555aaaa-c45d-67e8-f901-234567890abc"
                                       :name "complete/complete"
                                       :tag "5.2.1"
                                       :url "https://example.com/complete"
                                       :deprecated false
                                       :auth "token123"
                                       :osg_image_path "/cvmfs/singularity.opensciencegrid.org/complete"}}
                   :deprecated true
                   :tool_request {:id #uuid "6666aaaa-c45d-67e8-f901-234567890abc"
                                  :status "Approved"}})))

    (testing "invalid tool request - missing required fields from Tool base"
      (is (not (valid? apps/AppToolRequest {})))
      (is (not (valid? apps/AppToolRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "samtools"
                        :version "1.15.1"})))
      (is (not (valid? apps/AppToolRequest
                       {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                        :name "blast"
                        :type "executable"})))
      (is (not (valid? apps/AppToolRequest
                       {:name "bowtie2"
                        :version "2.5.0"
                        :type "executable"})))
      (is (not (valid? apps/AppToolRequest
                       {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :version "1.0"
                        :type "executable"})))
      (is (not (valid? apps/AppToolRequest
                       {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                        :name "tool"
                        :version "1.0"}))))

    (testing "invalid tool request - wrong field types for Tool base fields"
      (is (not (valid? apps/AppToolRequest
                       {:id "not-a-uuid"
                        :name "samtools"
                        :version "1.15.1"
                        :type "executable"})))
      (is (not (valid? apps/AppToolRequest
                       {:id 12345
                        :name "samtools"
                        :version "1.15.1"
                        :type "executable"})))
      (is (not (valid? apps/AppToolRequest
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name 123
                        :version "1.15.1"
                        :type "executable"})))
      (is (not (valid? apps/AppToolRequest
                       {:id #uuid "987e6543-e21b-32c1-b456-426614174000"
                        :name "blast"
                        :version 2.14
                        :type "executable"})))
      (is (not (valid? apps/AppToolRequest
                       {:id #uuid "456e7890-b12c-34d5-e678-901234567890"
                        :name "bowtie2"
                        :version "2.5.0"
                        :type :executable}))))

    (testing "invalid tool request - wrong type for deprecated field"
      (is (not (valid? apps/AppToolRequest
                       {:id #uuid "789a0123-c45d-67e8-f901-234567890abc"
                        :name "deprecated-tool"
                        :version "1.0.0"
                        :type "executable"
                        :deprecated "yes"})))
      (is (not (valid? apps/AppToolRequest
                       {:id #uuid "abc12345-def6-7890-abcd-ef1234567890"
                        :name "another-tool"
                        :version "2.0.0"
                        :type "executable"
                        :deprecated 1}))))

    (testing "invalid tool request - extra fields not allowed (closed map)"
      (is (not (valid? apps/AppToolRequest
                       {:id #uuid "def67890-abc1-2345-6789-0abcdef12345"
                        :name "samtools"
                        :version "1.15.1"
                        :type "executable"
                        :extra-field "not-allowed"})))
      (is (not (valid? apps/AppToolRequest
                       {:id #uuid "fedcba98-7654-3210-fedc-ba9876543210"
                        :name "blast"
                        :version "2.14.0"
                        :type "docker"
                        :random-key "random-value"}))))

    (testing "edge case - empty string values for optional string fields"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "111a0123-c45d-67e8-f901-234567890abc"
                   :name "samtools"
                   :version "1.15.1"
                   :type "executable"
                   :description ""}))
      (is (valid? apps/AppToolRequest
                  {:id #uuid "222a0123-c45d-67e8-f901-234567890abc"
                   :name "blast"
                   :version "2.14.0"
                   :type "executable"
                   :attribution ""}))
      (is (valid? apps/AppToolRequest
                  {:id #uuid "333a0123-c45d-67e8-f901-234567890abc"
                   :name "bowtie2"
                   :version "2.5.0"
                   :type "executable"
                   :location ""}))
      (is (valid? apps/AppToolRequest
                  {:id #uuid "444a0123-c45d-67e8-f901-234567890abc"
                   :name "trinity"
                   :version "2.13.2"
                   :type "executable"
                   :permission ""})))

    (testing "edge case - time_limit_seconds with zero value"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "555a0123-c45d-67e8-f901-234567890abc"
                   :name "samtools"
                   :version "1.15.1"
                   :type "executable"
                   :time_limit_seconds 0})))

    (testing "edge case - time_limit_seconds with large value"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "666a0123-c45d-67e8-f901-234567890abc"
                   :name "blast"
                   :version "2.14.0"
                   :type "executable"
                   :time_limit_seconds 86400})))

    (testing "edge case - boolean fields set to false"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "777a0123-c45d-67e8-f901-234567890abc"
                   :name "bowtie2"
                   :version "2.5.0"
                   :type "executable"
                   :restricted false
                   :interactive false
                   :is_public false
                   :deprecated false})))

    (testing "edge case - boolean fields set to true"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "888a0123-c45d-67e8-f901-234567890abc"
                   :name "trinity"
                   :version "2.13.2"
                   :type "executable"
                   :restricted true
                   :interactive true
                   :is_public true
                   :deprecated true})))

    (testing "real-world example - basic executable tool request"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "999a0123-c45d-67e8-f901-234567890abc"
                   :name "samtools"
                   :description "A suite of programs for interacting with high-throughput sequencing data"
                   :attribution "Heng Li et al."
                   :location "/usr/local/bin"
                   :version "1.15.1"
                   :type "executable"
                   :restricted false
                   :time_limit_seconds 3600
                   :interactive false
                   :is_public true
                   :permission "read"})))

    (testing "real-world example - minimal private tool request"
      (is (valid? apps/AppToolRequest
                  {:id #uuid "aaaa0123-c45d-67e8-f901-234567890abc"
                   :name "my-custom-tool"
                   :version "1.0.0"
                   :type "executable"})))))
