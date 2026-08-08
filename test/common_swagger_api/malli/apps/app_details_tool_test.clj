(ns common-swagger-api.malli.apps.app-details-tool-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common-swagger-api.malli.apps :as apps]
   [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-AppDetailsTool
  (testing "AppDetailsTool validation"
    (testing "valid app details tool - minimal required fields"
      (is (valid? apps/AppDetailsTool
                  {:id "tool-abc123"
                   :name "samtools"
                   :version "1.15.1"
                   :type "executable"}))
      (is (valid? apps/AppDetailsTool
                  {:id "blast-tool-456"
                   :name "blast"
                   :version "2.14.0"
                   :type "docker"})))

    (testing "valid app details tool - with optional Tool fields"
      (is (valid? apps/AppDetailsTool
                  {:id "tool-789"
                   :name "bowtie2"
                   :description "Fast and sensitive read alignment"
                   :version "2.5.0"
                   :type "executable"}))
      (is (valid? apps/AppDetailsTool
                  {:id "tool-full-example"
                   :name "trinity"
                   :description "De novo transcript assembly from RNA-Seq data"
                   :attribution "Broad Institute"
                   :location "/usr/local/bin"
                   :version "2.13.2"
                   :type "executable"
                   :restricted false
                   :time_limit_seconds 7200
                   :interactive false})))

    (testing "valid app details tool - with container field"
      (is (valid? apps/AppDetailsTool
                  {:id "tool-with-container"
                   :name "samtools"
                   :version "1.15.1"
                   :type "docker"
                   :container {:image {:name "biocontainers/samtools"
                                       :tag "1.15.1"}}}))
      (is (valid? apps/AppDetailsTool
                  {:id "tool-full-container"
                   :name "blast"
                   :version "2.14.0"
                   :type "docker"
                   :description "BLAST+ suite"
                   :attribution "NCBI"
                   :container {:cpu_shares 1024
                               :memory_limit 2147483648
                               :min_memory_limit 1073741824
                               :min_cpu_cores 1.0
                               :max_cpu_cores 4.0
                               :network_mode "bridge"
                               :working_directory "/workspace"
                               :image {:name "ncbi/blast"
                                       :tag "2.14.0"
                                       :url "https://hub.docker.com/r/ncbi/blast"
                                       :deprecated false}}})))

    (testing "valid app details tool - all optional fields present"
      (is (valid? apps/AppDetailsTool
                  {:id "complete-tool"
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
                                       :osg_image_path "/cvmfs/singularity.opensciencegrid.org/cufflinks"}}})))

    (testing "invalid app details tool - missing required fields"
      (is (not (valid? apps/AppDetailsTool {})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-123"
                        :name "samtools"
                        :version "1.15.1"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-456"
                        :name "blast"
                        :type "executable"})))
      (is (not (valid? apps/AppDetailsTool
                       {:name "bowtie2"
                        :version "2.5.0"
                        :type "executable"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-789"
                        :version "1.0"
                        :type "executable"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-abc"
                        :name "tool"
                        :version "1.0"}))))

    (testing "invalid app details tool - wrong field types"
      (is (not (valid? apps/AppDetailsTool
                       {:id 12345
                        :name "samtools"
                        :version "1.15.1"
                        :type "executable"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                        :name "samtools"
                        :version "1.15.1"
                        :type "executable"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-123"
                        :name 123
                        :version "1.15.1"
                        :type "executable"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-456"
                        :name "blast"
                        :version 2.14
                        :type "executable"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-789"
                        :name "bowtie2"
                        :version "2.5.0"
                        :type :executable})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-abc"
                        :name "trinity"
                        :description 12345
                        :version "2.13.2"
                        :type "executable"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-def"
                        :name "cufflinks"
                        :attribution [:author "Someone"]
                        :version "2.2.1"
                        :type "executable"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-ghi"
                        :name "tophat"
                        :location 123
                        :version "2.1.0"
                        :type "executable"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-jkl"
                        :name "hisat2"
                        :version "2.2.1"
                        :type "executable"
                        :restricted "yes"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-mno"
                        :name "star"
                        :version "2.7.10"
                        :type "executable"
                        :time_limit_seconds "3600"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-pqr"
                        :name "kallisto"
                        :version "0.48.0"
                        :type "executable"
                        :interactive "true"}))))

    (testing "invalid app details tool - invalid container field"
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-123"
                        :name "samtools"
                        :version "1.15.1"
                        :type "docker"
                        :container "not-a-map"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-456"
                        :name "blast"
                        :version "2.14.0"
                        :type "docker"
                        :container {:image "not-a-proper-image-object"}})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-789"
                        :name "bowtie2"
                        :version "2.5.0"
                        :type "docker"
                        :container {:cpu_shares "not-an-int"
                                    :image {:name "biocontainers/bowtie2"
                                            :tag "2.5.0"}}})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-abc"
                        :name "trinity"
                        :version "2.13.2"
                        :type "docker"
                        :container {:memory_limit "1GB"
                                    :image {:name "trinityrnaseq/trinityrnaseq"
                                            :tag "2.13.2"}}})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-def"
                        :name "cufflinks"
                        :version "2.2.1"
                        :type "docker"
                        :container {:min_cpu_cores "2.0"
                                    :image {:name "cufflinks/cufflinks"
                                            :tag "2.2.1"}}}))))

    (testing "invalid app details tool - extra fields not allowed"
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-123"
                        :name "samtools"
                        :version "1.15.1"
                        :type "executable"
                        :extra-field "not-allowed"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-456"
                        :name "blast"
                        :version "2.14.0"
                        :type "docker"
                        :is_public true})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-789"
                        :name "bowtie2"
                        :version "2.5.0"
                        :type "executable"
                        :permission "own"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-abc"
                        :name "trinity"
                        :version "2.13.2"
                        :type "executable"
                        :implementation {:implementor "Someone"
                                        :implementor_email "someone@example.org"}})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-def"
                        :name "cufflinks"
                        :version "2.2.1"
                        :type "executable"
                        :random-key "random-value"}))))

    (testing "inherited Tool field validation - string fields"
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-123"
                        :name nil
                        :version "1.15.1"
                        :type "executable"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-789"
                        :name "bowtie2"
                        :version nil
                        :type "executable"})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-abc"
                        :name "trinity"
                        :version "2.13.2"
                        :type nil}))))

    (testing "inherited Tool field validation - optional string fields"
      (is (valid? apps/AppDetailsTool
                  {:id "tool-123"
                   :name "samtools"
                   :version "1.15.1"
                   :type "executable"
                   :description ""}))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-456"
                        :name "blast"
                        :version "2.14.0"
                        :type "executable"
                        :description nil})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-789"
                        :name "bowtie2"
                        :version "2.5.0"
                        :type "executable"
                        :attribution nil})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-abc"
                        :name "trinity"
                        :version "2.13.2"
                        :type "executable"
                        :location nil}))))

    (testing "inherited Tool field validation - numeric fields"
      (is (valid? apps/AppDetailsTool
                  {:id "tool-123"
                   :name "samtools"
                   :version "1.15.1"
                   :type "executable"
                   :time_limit_seconds 0}))
      (is (valid? apps/AppDetailsTool
                  {:id "tool-456"
                   :name "blast"
                   :version "2.14.0"
                   :type "executable"
                   :time_limit_seconds 86400}))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-abc"
                        :name "trinity"
                        :version "2.13.2"
                        :type "executable"
                        :time_limit_seconds 3600.5}))))

    (testing "inherited Tool field validation - boolean fields"
      (is (valid? apps/AppDetailsTool
                  {:id "tool-123"
                   :name "samtools"
                   :version "1.15.1"
                   :type "executable"
                   :restricted true}))
      (is (valid? apps/AppDetailsTool
                  {:id "tool-456"
                   :name "blast"
                   :version "2.14.0"
                   :type "executable"
                   :restricted false}))
      (is (valid? apps/AppDetailsTool
                  {:id "tool-789"
                   :name "bowtie2"
                   :version "2.5.0"
                   :type "executable"
                   :interactive true}))
      (is (valid? apps/AppDetailsTool
                  {:id "tool-abc"
                   :name "trinity"
                   :version "2.13.2"
                   :type "executable"
                   :interactive false})))

    (testing "container field validation - ToolListingImage structure"
      (is (valid? apps/AppDetailsTool
                  {:id "tool-123"
                   :name "samtools"
                   :version "1.15.1"
                   :type "docker"
                   :container {:image {:name "biocontainers/samtools"}}}))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-456"
                        :name "blast"
                        :version "2.14.0"
                        :type "docker"
                        :container {:image {}}})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-789"
                        :name "bowtie2"
                        :version "2.5.0"
                        :type "docker"
                        :container {:image {:name 123}}})))
      (is (not (valid? apps/AppDetailsTool
                       {:id "tool-abc"
                        :name "trinity"
                        :version "2.13.2"
                        :type "docker"
                        :container {:image {:name "trinityrnaseq/trinityrnaseq"
                                            :id #uuid "123e4567-e89b-12d3-a456-426614174000"}}}))))))
