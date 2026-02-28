(ns common-swagger-api.malli.apps.app-tool-listing-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common-swagger-api.malli.apps :as apps]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AppToolListing
  (testing "AppToolListing validation"
    (testing "valid app tool listing - empty vector of tools"
      (is (valid? apps/AppToolListing
                  {:tools []})))

    (testing "valid app tool listing - with one tool"
      (is (valid? apps/AppToolListing
                  {:tools [{:id "tool-abc123"
                            :name "samtools"
                            :version "1.15.1"
                            :type "executable"}]}))
      (is (valid? apps/AppToolListing
                  {:tools [{:id "blast-tool-456"
                            :name "blast"
                            :version "2.14.0"
                            :type "docker"
                            :description "BLAST+ suite"
                            :attribution "NCBI"}]})))

    (testing "valid app tool listing - with multiple tools"
      (is (valid? apps/AppToolListing
                  {:tools [{:id "tool-1"
                            :name "samtools"
                            :version "1.15.1"
                            :type "executable"}
                           {:id "tool-2"
                            :name "blast"
                            :version "2.14.0"
                            :type "docker"}]}))
      (is (valid? apps/AppToolListing
                  {:tools [{:id "tool-a"
                            :name "bowtie2"
                            :description "Fast and sensitive read alignment"
                            :version "2.5.0"
                            :type "executable"}
                           {:id "tool-b"
                            :name "trinity"
                            :description "De novo transcript assembly from RNA-Seq data"
                            :attribution "Broad Institute"
                            :version "2.13.2"
                            :type "executable"}
                           {:id "tool-c"
                            :name "cufflinks"
                            :version "2.2.1"
                            :type "docker"
                            :container {:image {:name "cufflinks/cufflinks"
                                                :tag "2.2.1"}}}]})))

    (testing "valid app tool listing - with tools containing all optional fields"
      (is (valid? apps/AppToolListing
                  {:tools [{:id "complete-tool"
                            :name "cufflinks"
                            :description "Transcriptome assembly and differential expression analysis"
                            :attribution "Cole Trapnell"
                            :location "/opt/tools/cufflinks"
                            :version "2.2.1"
                            :type "executable"
                            :restricted true
                            :time_limit_seconds 3600
                            :interactive false
                            :container {:cpu_shares 512
                                        :pids_limit 100
                                        :memory_limit 1073741824
                                        :min_memory_limit 536870912
                                        :min_cpu_cores 0.5
                                        :max_cpu_cores 2.0
                                        :min_disk_space 10737418240
                                        :network_mode "none"
                                        :working_directory "/work"
                                        :name "cufflinks-container"
                                        :entrypoint "/bin/bash"
                                        :skip_tmp_mount true
                                        :uid 1000
                                        :image {:name "cufflinks/cufflinks"
                                                :tag "2.2.1"
                                                :url "https://example.com/cufflinks"
                                                :deprecated false
                                                :auth "token123"
                                                :osg_image_path
                                                "/cvmfs/singularity.opensciencegrid.org/cufflinks"}}}]})))

    (testing "invalid app tool listing - missing required field"
      (is (not (valid? apps/AppToolListing {}))))

    (testing "invalid app tool listing - wrong type for tools field"
      (is (not (valid? apps/AppToolListing
                       {:tools "not-a-vector"})))
      (is (not (valid? apps/AppToolListing
                       {:tools {:tool1 "value"}})))
      (is (not (valid? apps/AppToolListing
                       {:tools nil}))))

    (testing "invalid app tool listing - tools vector contains invalid elements"
      (is (not (valid? apps/AppToolListing
                       {:tools ["not-a-map"]})))
      (is (not (valid? apps/AppToolListing
                       {:tools [{}]})))
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-123"
                                 :name "samtools"
                                 :version "1.15.1"}]})))
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-456"
                                 :name "blast"
                                 :type "executable"}]})))
      (is (not (valid? apps/AppToolListing
                       {:tools [{:name "bowtie2"
                                 :version "2.5.0"
                                 :type "executable"}]})))
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-789"
                                 :version "1.0"
                                 :type "executable"}]}))))

    (testing "invalid app tool listing - mix of valid and invalid tools"
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-1"
                                 :name "samtools"
                                 :version "1.15.1"
                                 :type "executable"}
                                {:id "tool-2"
                                 :name "blast"
                                 :version "2.14.0"}]})))
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-a"
                                 :name "bowtie2"
                                 :version "2.5.0"
                                 :type "executable"}
                                "invalid-tool"]}))))

    (testing "invalid app tool listing - tools with wrong field types"
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id 12345
                                 :name "samtools"
                                 :version "1.15.1"
                                 :type "executable"}]})))
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-123"
                                 :name 123
                                 :version "1.15.1"
                                 :type "executable"}]})))
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-456"
                                 :name "blast"
                                 :version 2.14
                                 :type "executable"}]})))
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-789"
                                 :name "bowtie2"
                                 :version "2.5.0"
                                 :type :executable}]}))))

    (testing "invalid app tool listing - tools with invalid container fields"
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-123"
                                 :name "samtools"
                                 :version "1.15.1"
                                 :type "docker"
                                 :container "not-a-map"}]})))
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-456"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "docker"
                                 :container {:image "not-a-proper-image-object"}}]}))))

    (testing "invalid app tool listing - extra fields not allowed"
      (is (not (valid? apps/AppToolListing
                       {:tools []
                        :extra-field "not-allowed"})))
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-123"
                                 :name "samtools"
                                 :version "1.15.1"
                                 :type "executable"}]
                        :app_id "app-123"})))
      (is (not (valid? apps/AppToolListing
                       {:tools []
                        :system_id "de"}))))

    (testing "invalid app tool listing - tools with extra fields"
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-123"
                                 :name "samtools"
                                 :version "1.15.1"
                                 :type "executable"
                                 :extra-field "not-allowed"}]})))
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-456"
                                 :name "blast"
                                 :version "2.14.0"
                                 :type "docker"
                                 :is_public true}]})))
      (is (not (valid? apps/AppToolListing
                       {:tools [{:id "tool-789"
                                 :name "bowtie2"
                                 :version "2.5.0"
                                 :type "executable"
                                 :permission "own"}]}))))))
