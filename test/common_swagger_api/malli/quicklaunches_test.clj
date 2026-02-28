(ns common-swagger-api.malli.quicklaunches-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.quicklaunches :as ql]
            [malli.core :as malli]
            [malli.error :as me]))

(defn valid? [schema data]
  (malli/validate schema data))

(defn explain [schema data]
  (-> (malli/explain schema data)
      (me/humanize)))

(deftest test-QuickLaunch
  (testing "QuickLaunch validation"
    (testing "valid QuickLaunch with all required fields"
      (is (valid? ql/QuickLaunch
                  {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                   :name            "BLAST"
                   :creator         "janedoe"
                   :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                   :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                   :submission      {:system_id  "de"
                                     :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                     :config     {}
                                     :debug      false
                                     :name       "sequence-alignment"
                                     :notify     true
                                     :output_dir "/zone/home/janedoe/analyses"}})))

    (testing "valid QuickLaunch with optional description"
      (is (valid? ql/QuickLaunch
                  {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                   :name            "BLAST"
                   :description     "BLAST Nucleotide Sequence Alignment with default settings."
                   :creator         "janedoe"
                   :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                   :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                   :submission      {:system_id  "de"
                                     :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                     :config     {}
                                     :debug      false
                                     :name       "sequence-alignment"
                                     :notify     true
                                     :output_dir "/zone/home/janedoe/analyses"}})))

    (testing "valid QuickLaunch with optional is_public"
      (is (valid? ql/QuickLaunch
                  {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                   :name            "BLAST"
                   :creator         "janedoe"
                   :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                   :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                   :is_public       true
                   :submission      {:system_id  "de"
                                     :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                     :config     {}
                                     :debug      false
                                     :name       "sequence-alignment"
                                     :notify     true
                                     :output_dir "/zone/home/janedoe/analyses"}})))

    (testing "valid QuickLaunch with all optional fields"
      (is (valid? ql/QuickLaunch
                  {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                   :name            "BLAST Nucleotide Alignment"
                   :description     "Quick launch for BLAST nucleotide sequence alignment with optimized parameters."
                   :creator         "janedoe"
                   :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                   :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                   :is_public       false
                   :submission      {:system_id             "de"
                                     :app_id                "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                     :app_version_id        "02ff8f75-a4fc-4d8c-9d76-ef91f764cec4"
                                     :config                {:step-1_param-1 "value1"
                                                             :step-1_param-2 "value2"}
                                     :debug                 false
                                     :description           "Sequence alignment for Zea Mays."
                                     :name                  "sequence-alignment-zea-mays"
                                     :notify                true
                                     :output_dir            "/zone/home/janedoe/analyses"
                                     :create_output_subdir  true
                                     :notify_periodic       false
                                     :archive_logs          false
                                     :mount_data_store      true}})))

    (testing "invalid QuickLaunch - missing id"
      (is (not (valid? ql/QuickLaunch
                       {:name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - missing name"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - missing creator"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "BLAST"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - missing app_id"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "BLAST"
                        :creator         "janedoe"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - missing app_version_id"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - missing submission"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"}))))

    (testing "invalid QuickLaunch - id is string instead of UUID"
      (is (not (valid? ql/QuickLaunch
                       {:id              "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - app_id is string instead of UUID"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "BLAST"
                        :creator         "janedoe"
                        :app_id          "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - app_version_id is string instead of UUID"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - blank name (NonBlankString validation)"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            ""
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - whitespace-only name (NonBlankString validation)"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "   "
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - blank creator (NonBlankString validation)"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "BLAST"
                        :creator         ""
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - whitespace-only creator (NonBlankString validation)"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "BLAST"
                        :creator         "   "
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - extra fields (closed schema)"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :extra_field     "should not be here"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - is_public is string instead of boolean"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :is_public       "true"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid QuickLaunch - invalid submission (missing required field)"
      (is (not (valid? ql/QuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          ;; missing output_dir
                                          }}))))))

(deftest test-NewQuickLaunch
  (testing "NewQuickLaunch validation"
    (testing "valid NewQuickLaunch with all required fields (no id, app_version_id omitted)"
      (is (valid? ql/NewQuickLaunch
                  {:name            "BLAST"
                   :creator         "janedoe"
                   :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                   :submission      {:system_id  "de"
                                     :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                     :config     {}
                                     :debug      false
                                     :name       "sequence-alignment"
                                     :notify     true
                                     :output_dir "/zone/home/janedoe/analyses"}})))

    (testing "valid NewQuickLaunch with app_version_id included (now optional)"
      (is (valid? ql/NewQuickLaunch
                  {:name            "BLAST"
                   :creator         "janedoe"
                   :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                   :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                   :submission      {:system_id  "de"
                                     :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                     :config     {}
                                     :debug      false
                                     :name       "sequence-alignment"
                                     :notify     true
                                     :output_dir "/zone/home/janedoe/analyses"}})))

    (testing "valid NewQuickLaunch with optional description"
      (is (valid? ql/NewQuickLaunch
                  {:name            "BLAST"
                   :description     "BLAST Nucleotide Sequence Alignment with default settings."
                   :creator         "janedoe"
                   :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                   :submission      {:system_id  "de"
                                     :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                     :config     {}
                                     :debug      false
                                     :name       "sequence-alignment"
                                     :notify     true
                                     :output_dir "/zone/home/janedoe/analyses"}})))

    (testing "valid NewQuickLaunch with optional is_public"
      (is (valid? ql/NewQuickLaunch
                  {:name            "BLAST"
                   :creator         "janedoe"
                   :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                   :is_public       true
                   :submission      {:system_id  "de"
                                     :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                     :config     {}
                                     :debug      false
                                     :name       "sequence-alignment"
                                     :notify     true
                                     :output_dir "/zone/home/janedoe/analyses"}})))

    (testing "valid NewQuickLaunch with all optional fields"
      (is (valid? ql/NewQuickLaunch
                  {:name            "BLAST Nucleotide Alignment"
                   :description     "Quick launch for BLAST nucleotide sequence alignment with optimized parameters."
                   :creator         "janedoe"
                   :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                   :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                   :is_public       false
                   :submission      {:system_id             "de"
                                     :app_id                "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                     :app_version_id        "02ff8f75-a4fc-4d8c-9d76-ef91f764cec4"
                                     :config                {:step-1_param-1 "value1"
                                                             :step-1_param-2 "value2"}
                                     :debug                 false
                                     :description           "Sequence alignment for Zea Mays."
                                     :name                  "sequence-alignment-zea-mays"
                                     :notify                true
                                     :output_dir            "/zone/home/janedoe/analyses"
                                     :create_output_subdir  true
                                     :notify_periodic       false
                                     :archive_logs          false
                                     :mount_data_store      true}})))

    (testing "invalid NewQuickLaunch - including id field (should be removed)"
      (is (not (valid? ql/NewQuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid NewQuickLaunch - missing name"
      (is (not (valid? ql/NewQuickLaunch
                       {:creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid NewQuickLaunch - missing creator"
      (is (not (valid? ql/NewQuickLaunch
                       {:name            "BLAST"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid NewQuickLaunch - missing app_id"
      (is (not (valid? ql/NewQuickLaunch
                       {:name            "BLAST"
                        :creator         "janedoe"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid NewQuickLaunch - missing submission"
      (is (not (valid? ql/NewQuickLaunch
                       {:name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"}))))

    (testing "invalid NewQuickLaunch - app_id is string instead of UUID"
      (is (not (valid? ql/NewQuickLaunch
                       {:name            "BLAST"
                        :creator         "janedoe"
                        :app_id          "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid NewQuickLaunch - app_version_id is string instead of UUID"
      (is (not (valid? ql/NewQuickLaunch
                       {:name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid NewQuickLaunch - blank name (NonBlankString validation)"
      (is (not (valid? ql/NewQuickLaunch
                       {:name            ""
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid NewQuickLaunch - whitespace-only name (NonBlankString validation)"
      (is (not (valid? ql/NewQuickLaunch
                       {:name            "   "
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid NewQuickLaunch - blank creator (NonBlankString validation)"
      (is (not (valid? ql/NewQuickLaunch
                       {:name            "BLAST"
                        :creator         ""
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid NewQuickLaunch - whitespace-only creator (NonBlankString validation)"
      (is (not (valid? ql/NewQuickLaunch
                       {:name            "BLAST"
                        :creator         "   "
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid NewQuickLaunch - extra fields (closed schema)"
      (is (not (valid? ql/NewQuickLaunch
                       {:name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :extra_field     "should not be here"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid NewQuickLaunch - is_public is string instead of boolean"
      (is (not (valid? ql/NewQuickLaunch
                       {:name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :is_public       "true"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/janedoe/analyses"}}))))

    (testing "invalid NewQuickLaunch - invalid submission (missing required field)"
      (is (not (valid? ql/NewQuickLaunch
                       {:name            "BLAST"
                        :creator         "janedoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          ;; missing output_dir
                                          }}))))))

(deftest test-UpdateQuickLaunch
  (testing "UpdateQuickLaunch validation"
    (testing "valid UpdateQuickLaunch - empty map (all fields optional)"
      (is (valid? ql/UpdateQuickLaunch {})))

    (testing "valid UpdateQuickLaunch with only one field (name)"
      (is (valid? ql/UpdateQuickLaunch
                  {:name "Updated BLAST"})))

    (testing "valid UpdateQuickLaunch with only one field (description)"
      (is (valid? ql/UpdateQuickLaunch
                  {:description "Updated description for BLAST quick launch."})))

    (testing "valid UpdateQuickLaunch with only one field (is_public)"
      (is (valid? ql/UpdateQuickLaunch
                  {:is_public true})))

    (testing "valid UpdateQuickLaunch with only one field (app_version_id)"
      (is (valid? ql/UpdateQuickLaunch
                  {:app_version_id #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"})))

    (testing "valid UpdateQuickLaunch with multiple fields but not all"
      (is (valid? ql/UpdateQuickLaunch
                  {:name        "Updated BLAST"
                   :description "Updated description for BLAST quick launch."
                   :is_public   true})))

    (testing "valid UpdateQuickLaunch with name and creator"
      (is (valid? ql/UpdateQuickLaunch
                  {:name    "Updated BLAST"
                   :creator "johndoe"})))

    (testing "valid UpdateQuickLaunch with app_id and app_version_id"
      (is (valid? ql/UpdateQuickLaunch
                  {:app_id         #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                   :app_version_id #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"})))

    (testing "valid UpdateQuickLaunch with submission only"
      (is (valid? ql/UpdateQuickLaunch
                  {:submission {:system_id  "de"
                                :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                :config     {}
                                :debug      false
                                :name       "updated-sequence-alignment"
                                :notify     true
                                :output_dir "/zone/home/janedoe/analyses"}})))

    (testing "valid UpdateQuickLaunch with all fields present (except id)"
      (is (valid? ql/UpdateQuickLaunch
                  {:name            "Updated BLAST Nucleotide Alignment"
                   :description     "Updated quick launch for BLAST nucleotide sequence alignment."
                   :creator         "johndoe"
                   :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                   :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                   :is_public       true
                   :submission      {:system_id             "de"
                                     :app_id                "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                     :app_version_id        "02ff8f75-a4fc-4d8c-9d76-ef91f764cec4"
                                     :config                {:step-1_param-1 "updated-value1"
                                                             :step-1_param-2 "updated-value2"}
                                     :debug                 false
                                     :description           "Updated sequence alignment for Zea Mays."
                                     :name                  "updated-sequence-alignment-zea-mays"
                                     :notify                true
                                     :output_dir            "/zone/home/johndoe/analyses"
                                     :create_output_subdir  true
                                     :notify_periodic       false
                                     :archive_logs          false
                                     :mount_data_store      true}})))

    (testing "valid UpdateQuickLaunch with optional fields (description and is_public)"
      (is (valid? ql/UpdateQuickLaunch
                  {:description "A new description."
                   :is_public   false})))

    (testing "invalid UpdateQuickLaunch - including id field (should be removed)"
      (is (not (valid? ql/UpdateQuickLaunch
                       {:id              #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"
                        :name            "Updated BLAST"
                        :creator         "johndoe"
                        :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                        :app_version_id  #uuid "48b8e32e-427c-40db-b389-0d535c4a7cfd"
                        :submission      {:system_id  "de"
                                          :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                          :config     {}
                                          :debug      false
                                          :name       "sequence-alignment"
                                          :notify     true
                                          :output_dir "/zone/home/johndoe/analyses"}}))))

    (testing "invalid UpdateQuickLaunch - id field only"
      (is (not (valid? ql/UpdateQuickLaunch
                       {:id #uuid "f8256157-63d3-4f5b-adee-820bd28019b8"}))))

    (testing "invalid UpdateQuickLaunch - app_id is string instead of UUID"
      (is (not (valid? ql/UpdateQuickLaunch
                       {:app_id "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"}))))

    (testing "invalid UpdateQuickLaunch - app_version_id is string instead of UUID"
      (is (not (valid? ql/UpdateQuickLaunch
                       {:app_version_id "48b8e32e-427c-40db-b389-0d535c4a7cfd"}))))

    (testing "invalid UpdateQuickLaunch - blank name (NonBlankString validation)"
      (is (not (valid? ql/UpdateQuickLaunch
                       {:name ""}))))

    (testing "invalid UpdateQuickLaunch - whitespace-only name (NonBlankString validation)"
      (is (not (valid? ql/UpdateQuickLaunch
                       {:name "   "}))))

    (testing "invalid UpdateQuickLaunch - blank creator (NonBlankString validation)"
      (is (not (valid? ql/UpdateQuickLaunch
                       {:creator ""}))))

    (testing "invalid UpdateQuickLaunch - whitespace-only creator (NonBlankString validation)"
      (is (not (valid? ql/UpdateQuickLaunch
                       {:creator "   "}))))

    (testing "invalid UpdateQuickLaunch - extra fields (closed schema)"
      (is (not (valid? ql/UpdateQuickLaunch
                       {:name         "Updated BLAST"
                        :extra_field  "should not be here"}))))

    (testing "invalid UpdateQuickLaunch - is_public is string instead of boolean"
      (is (not (valid? ql/UpdateQuickLaunch
                       {:is_public "true"}))))

    (testing "invalid UpdateQuickLaunch - is_public is number instead of boolean"
      (is (not (valid? ql/UpdateQuickLaunch
                       {:is_public 1}))))

    (testing "invalid UpdateQuickLaunch - invalid submission (missing required field)"
      (is (not (valid? ql/UpdateQuickLaunch
                       {:submission {:system_id  "de"
                                     :app_id     "007a8434-1b84-42e8-b647-4073a62b4b3b"
                                     :config     {}
                                     :debug      false
                                     :name       "sequence-alignment"
                                     :notify     true
                                     ;; missing output_dir
                                     }}))))

    (testing "invalid UpdateQuickLaunch - multiple invalid fields"
      (is (not (valid? ql/UpdateQuickLaunch
                       {:name           ""
                        :creator        "   "
                        :app_id         "not-a-uuid"
                        :is_public      "true"
                        :extra_field    "should not be here"}))))))

(deftest test-QuickLaunchFavorite
  (testing "QuickLaunchFavorite validation"
    (testing "valid QuickLaunchFavorite with all required fields"
      (is (valid? ql/QuickLaunchFavorite
                  {:id              #uuid "234f5f17-cbcf-4e23-87fa-1bceaeba09ee"
                   :quick_launch_id #uuid "7bd11b7f-91f2-4e7c-9e48-9a3ba802f354"
                   :user            "janedoe"})))

    (testing "invalid QuickLaunchFavorite - missing id"
      (is (not (valid? ql/QuickLaunchFavorite
                       {:quick_launch_id #uuid "7bd11b7f-91f2-4e7c-9e48-9a3ba802f354"
                        :user            "janedoe"}))))

    (testing "invalid QuickLaunchFavorite - missing quick_launch_id"
      (is (not (valid? ql/QuickLaunchFavorite
                       {:id   #uuid "234f5f17-cbcf-4e23-87fa-1bceaeba09ee"
                        :user "janedoe"}))))

    (testing "invalid QuickLaunchFavorite - missing user"
      (is (not (valid? ql/QuickLaunchFavorite
                       {:id              #uuid "234f5f17-cbcf-4e23-87fa-1bceaeba09ee"
                        :quick_launch_id #uuid "7bd11b7f-91f2-4e7c-9e48-9a3ba802f354"}))))

    (testing "invalid QuickLaunchFavorite - id is string instead of UUID"
      (is (not (valid? ql/QuickLaunchFavorite
                       {:id              "234f5f17-cbcf-4e23-87fa-1bceaeba09ee"
                        :quick_launch_id #uuid "7bd11b7f-91f2-4e7c-9e48-9a3ba802f354"
                        :user            "janedoe"}))))

    (testing "invalid QuickLaunchFavorite - quick_launch_id is string instead of UUID"
      (is (not (valid? ql/QuickLaunchFavorite
                       {:id              #uuid "234f5f17-cbcf-4e23-87fa-1bceaeba09ee"
                        :quick_launch_id "7bd11b7f-91f2-4e7c-9e48-9a3ba802f354"
                        :user            "janedoe"}))))

    (testing "invalid QuickLaunchFavorite - blank user (NonBlankString validation)"
      (is (not (valid? ql/QuickLaunchFavorite
                       {:id              #uuid "234f5f17-cbcf-4e23-87fa-1bceaeba09ee"
                        :quick_launch_id #uuid "7bd11b7f-91f2-4e7c-9e48-9a3ba802f354"
                        :user            ""}))))

    (testing "invalid QuickLaunchFavorite - whitespace-only user (NonBlankString validation)"
      (is (not (valid? ql/QuickLaunchFavorite
                       {:id              #uuid "234f5f17-cbcf-4e23-87fa-1bceaeba09ee"
                        :quick_launch_id #uuid "7bd11b7f-91f2-4e7c-9e48-9a3ba802f354"
                        :user            "   "}))))

    (testing "invalid QuickLaunchFavorite - extra fields (closed schema)"
      (is (not (valid? ql/QuickLaunchFavorite
                       {:id              #uuid "234f5f17-cbcf-4e23-87fa-1bceaeba09ee"
                        :quick_launch_id #uuid "7bd11b7f-91f2-4e7c-9e48-9a3ba802f354"
                        :user            "janedoe"
                        :extra_field     "should not be here"}))))))

(deftest test-QuickLaunchUserDefault
  (testing "QuickLaunchUserDefault validation"
    (testing "valid QuickLaunchUserDefault with all required fields"
      (is (valid? ql/QuickLaunchUserDefault
                  {:id              #uuid "36fe7915-5984-4848-b5bc-965899b0e061"
                   :user            "janedoe"
                   :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                   :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"})))

    (testing "invalid QuickLaunchUserDefault - missing id"
      (is (not (valid? ql/QuickLaunchUserDefault
                       {:user            "janedoe"
                        :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid QuickLaunchUserDefault - missing user"
      (is (not (valid? ql/QuickLaunchUserDefault
                       {:id              #uuid "36fe7915-5984-4848-b5bc-965899b0e061"
                        :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid QuickLaunchUserDefault - missing app_id"
      (is (not (valid? ql/QuickLaunchUserDefault
                       {:id              #uuid "36fe7915-5984-4848-b5bc-965899b0e061"
                        :user            "janedoe"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid QuickLaunchUserDefault - missing quick_launch_id"
      (is (not (valid? ql/QuickLaunchUserDefault
                       {:id      #uuid "36fe7915-5984-4848-b5bc-965899b0e061"
                        :user    "janedoe"
                        :app_id  #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"}))))

    (testing "invalid QuickLaunchUserDefault - id is string instead of UUID"
      (is (not (valid? ql/QuickLaunchUserDefault
                       {:id              "36fe7915-5984-4848-b5bc-965899b0e061"
                        :user            "janedoe"
                        :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid QuickLaunchUserDefault - app_id is string instead of UUID"
      (is (not (valid? ql/QuickLaunchUserDefault
                       {:id              #uuid "36fe7915-5984-4848-b5bc-965899b0e061"
                        :user            "janedoe"
                        :app_id          "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid QuickLaunchUserDefault - quick_launch_id is string instead of UUID"
      (is (not (valid? ql/QuickLaunchUserDefault
                       {:id              #uuid "36fe7915-5984-4848-b5bc-965899b0e061"
                        :user            "janedoe"
                        :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid QuickLaunchUserDefault - blank user (NonBlankString validation)"
      (is (not (valid? ql/QuickLaunchUserDefault
                       {:id              #uuid "36fe7915-5984-4848-b5bc-965899b0e061"
                        :user            ""
                        :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid QuickLaunchUserDefault - whitespace-only user (NonBlankString validation)"
      (is (not (valid? ql/QuickLaunchUserDefault
                       {:id              #uuid "36fe7915-5984-4848-b5bc-965899b0e061"
                        :user            "   "
                        :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid QuickLaunchUserDefault - extra fields (closed schema)"
      (is (not (valid? ql/QuickLaunchUserDefault
                       {:id              #uuid "36fe7915-5984-4848-b5bc-965899b0e061"
                        :user            "janedoe"
                        :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"
                        :extra_field     "should not be here"}))))))

(deftest test-NewQuickLaunchUserDefault
  (testing "NewQuickLaunchUserDefault validation"
    (testing "valid NewQuickLaunchUserDefault with both required fields"
      (is (valid? ql/NewQuickLaunchUserDefault
                  {:app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                   :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"})))

    (testing "invalid NewQuickLaunchUserDefault - including id field (should be removed)"
      (is (not (valid? ql/NewQuickLaunchUserDefault
                       {:id              #uuid "36fe7915-5984-4848-b5bc-965899b0e061"
                        :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid NewQuickLaunchUserDefault - including user field (should be removed)"
      (is (not (valid? ql/NewQuickLaunchUserDefault
                       {:user            "janedoe"
                        :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid NewQuickLaunchUserDefault - including both id and user fields (both should be removed)"
      (is (not (valid? ql/NewQuickLaunchUserDefault
                       {:id              #uuid "36fe7915-5984-4848-b5bc-965899b0e061"
                        :user            "janedoe"
                        :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid NewQuickLaunchUserDefault - missing app_id"
      (is (not (valid? ql/NewQuickLaunchUserDefault
                       {:quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid NewQuickLaunchUserDefault - missing quick_launch_id"
      (is (not (valid? ql/NewQuickLaunchUserDefault
                       {:app_id #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"}))))

    (testing "invalid NewQuickLaunchUserDefault - missing both required fields"
      (is (not (valid? ql/NewQuickLaunchUserDefault {}))))

    (testing "invalid NewQuickLaunchUserDefault - app_id is string instead of UUID"
      (is (not (valid? ql/NewQuickLaunchUserDefault
                       {:app_id          "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid NewQuickLaunchUserDefault - quick_launch_id is string instead of UUID"
      (is (not (valid? ql/NewQuickLaunchUserDefault
                       {:app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid NewQuickLaunchUserDefault - both fields are strings instead of UUIDs"
      (is (not (valid? ql/NewQuickLaunchUserDefault
                       {:app_id          "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid NewQuickLaunchUserDefault - extra fields (closed schema)"
      (is (not (valid? ql/NewQuickLaunchUserDefault
                       {:app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"
                        :extra_field     "should not be here"}))))))

(deftest test-UpdateQuickLaunchUserDefault
  (testing "UpdateQuickLaunchUserDefault validation"
    (testing "valid UpdateQuickLaunchUserDefault - empty map (all fields optional)"
      (is (valid? ql/UpdateQuickLaunchUserDefault {})))

    (testing "valid UpdateQuickLaunchUserDefault with only one field (user)"
      (is (valid? ql/UpdateQuickLaunchUserDefault
                  {:user "johndoe"})))

    (testing "valid UpdateQuickLaunchUserDefault with only one field (app_id)"
      (is (valid? ql/UpdateQuickLaunchUserDefault
                  {:app_id #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"})))

    (testing "valid UpdateQuickLaunchUserDefault with only one field (quick_launch_id)"
      (is (valid? ql/UpdateQuickLaunchUserDefault
                  {:quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"})))

    (testing "valid UpdateQuickLaunchUserDefault with two fields (app_id and quick_launch_id)"
      (is (valid? ql/UpdateQuickLaunchUserDefault
                  {:app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                   :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"})))

    (testing "valid UpdateQuickLaunchUserDefault with two fields (user and app_id)"
      (is (valid? ql/UpdateQuickLaunchUserDefault
                  {:user   "johndoe"
                   :app_id #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"})))

    (testing "valid UpdateQuickLaunchUserDefault with two fields (user and quick_launch_id)"
      (is (valid? ql/UpdateQuickLaunchUserDefault
                  {:user            "johndoe"
                   :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"})))

    (testing "valid UpdateQuickLaunchUserDefault with all fields present (except id)"
      (is (valid? ql/UpdateQuickLaunchUserDefault
                  {:user            "johndoe"
                   :app_id          #uuid "187f7628-3bb7-4e9c-9efb-138d66ad6ae0"
                   :quick_launch_id #uuid "d2f6e891-53ad-47e9-b72c-90f2c4b2d3e8"})))

    (testing "invalid UpdateQuickLaunchUserDefault - including id field (should be removed)"
      (is (not (valid? ql/UpdateQuickLaunchUserDefault
                       {:id              #uuid "36fe7915-5984-4848-b5bc-965899b0e061"
                        :user            "johndoe"
                        :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid UpdateQuickLaunchUserDefault - id field only"
      (is (not (valid? ql/UpdateQuickLaunchUserDefault
                       {:id #uuid "36fe7915-5984-4848-b5bc-965899b0e061"}))))

    (testing "invalid UpdateQuickLaunchUserDefault - app_id is string instead of UUID"
      (is (not (valid? ql/UpdateQuickLaunchUserDefault
                       {:app_id "061df93e-4d5d-469d-9e46-5cd754d0665c"}))))

    (testing "invalid UpdateQuickLaunchUserDefault - quick_launch_id is string instead of UUID"
      (is (not (valid? ql/UpdateQuickLaunchUserDefault
                       {:quick_launch_id "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid UpdateQuickLaunchUserDefault - blank user (NonBlankString validation when present)"
      (is (not (valid? ql/UpdateQuickLaunchUserDefault
                       {:user ""}))))

    (testing "invalid UpdateQuickLaunchUserDefault - whitespace-only user (NonBlankString validation when present)"
      (is (not (valid? ql/UpdateQuickLaunchUserDefault
                       {:user "   "}))))

    (testing "invalid UpdateQuickLaunchUserDefault - blank user with valid app_id"
      (is (not (valid? ql/UpdateQuickLaunchUserDefault
                       {:user   ""
                        :app_id #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"}))))

    (testing "invalid UpdateQuickLaunchUserDefault - whitespace-only user with valid quick_launch_id"
      (is (not (valid? ql/UpdateQuickLaunchUserDefault
                       {:user            "   "
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"}))))

    (testing "invalid UpdateQuickLaunchUserDefault - extra fields (closed schema)"
      (is (not (valid? ql/UpdateQuickLaunchUserDefault
                       {:user        "johndoe"
                        :extra_field "should not be here"}))))

    (testing "invalid UpdateQuickLaunchUserDefault - extra fields with all valid fields present"
      (is (not (valid? ql/UpdateQuickLaunchUserDefault
                       {:user            "johndoe"
                        :app_id          #uuid "061df93e-4d5d-469d-9e46-5cd754d0665c"
                        :quick_launch_id #uuid "c71dfe2b-65b5-4464-8e00-413c67102326"
                        :extra_field     "should not be here"}))))

    (testing "invalid UpdateQuickLaunchUserDefault - multiple invalid fields"
      (is (not (valid? ql/UpdateQuickLaunchUserDefault
                       {:user            ""
                        :app_id          "not-a-uuid"
                        :quick_launch_id "also-not-a-uuid"
                        :extra_field     "should not be here"}))))))

(deftest test-QuickLaunchGlobalDefault
  (testing "QuickLaunchGlobalDefault validation"
    (testing "valid QuickLaunchGlobalDefault with all required fields"
      (is (valid? ql/QuickLaunchGlobalDefault
                  {:id              #uuid "416b20b1-fa0a-44e7-ac30-78a336e9c009"
                   :app_id          #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
                   :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"})))

    (testing "invalid QuickLaunchGlobalDefault - missing id"
      (is (not (valid? ql/QuickLaunchGlobalDefault
                       {:app_id          #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
                        :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"}))))

    (testing "invalid QuickLaunchGlobalDefault - missing app_id"
      (is (not (valid? ql/QuickLaunchGlobalDefault
                       {:id              #uuid "416b20b1-fa0a-44e7-ac30-78a336e9c009"
                        :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"}))))

    (testing "invalid QuickLaunchGlobalDefault - missing quick_launch_id"
      (is (not (valid? ql/QuickLaunchGlobalDefault
                       {:id     #uuid "416b20b1-fa0a-44e7-ac30-78a336e9c009"
                        :app_id #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"}))))

    (testing "invalid QuickLaunchGlobalDefault - id is string instead of UUID"
      (is (not (valid? ql/QuickLaunchGlobalDefault
                       {:id              "416b20b1-fa0a-44e7-ac30-78a336e9c009"
                        :app_id          #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
                        :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"}))))

    (testing "invalid QuickLaunchGlobalDefault - app_id is string instead of UUID"
      (is (not (valid? ql/QuickLaunchGlobalDefault
                       {:id              #uuid "416b20b1-fa0a-44e7-ac30-78a336e9c009"
                        :app_id          "20281d6d-7aa0-4491-a550-28a87487c9ef"
                        :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"}))))

    (testing "invalid QuickLaunchGlobalDefault - quick_launch_id is string instead of UUID"
      (is (not (valid? ql/QuickLaunchGlobalDefault
                       {:id              #uuid "416b20b1-fa0a-44e7-ac30-78a336e9c009"
                        :app_id          #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
                        :quick_launch_id "70a7a607-dc17-4eb2-a477-9bffb64fccff"}))))

    (testing "invalid QuickLaunchGlobalDefault - extra fields (closed schema)"
      (is (not (valid? ql/QuickLaunchGlobalDefault
                       {:id              #uuid "416b20b1-fa0a-44e7-ac30-78a336e9c009"
                        :app_id          #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
                        :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"
                        :extra_field     "should not be here"}))))))

(deftest test-NewQuickLaunchGlobalDefault
  (testing "NewQuickLaunchGlobalDefault validation"
    (testing "valid NewQuickLaunchGlobalDefault with both required fields"
      (is (valid? ql/NewQuickLaunchGlobalDefault
                  {:app_id          #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
                   :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"})))

    (testing "invalid NewQuickLaunchGlobalDefault - including id field (should be removed)"
      (is (not (valid? ql/NewQuickLaunchGlobalDefault
                       {:id              #uuid "416b20b1-fa0a-44e7-ac30-78a336e9c009"
                        :app_id          #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
                        :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"}))))

    (testing "invalid NewQuickLaunchGlobalDefault - missing app_id"
      (is (not (valid? ql/NewQuickLaunchGlobalDefault
                       {:quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"}))))

    (testing "invalid NewQuickLaunchGlobalDefault - missing quick_launch_id"
      (is (not (valid? ql/NewQuickLaunchGlobalDefault
                       {:app_id #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"}))))

    (testing "invalid NewQuickLaunchGlobalDefault - missing both required fields"
      (is (not (valid? ql/NewQuickLaunchGlobalDefault {}))))

    (testing "invalid NewQuickLaunchGlobalDefault - app_id is string instead of UUID"
      (is (not (valid? ql/NewQuickLaunchGlobalDefault
                       {:app_id          "20281d6d-7aa0-4491-a550-28a87487c9ef"
                        :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"}))))

    (testing "invalid NewQuickLaunchGlobalDefault - quick_launch_id is string instead of UUID"
      (is (not (valid? ql/NewQuickLaunchGlobalDefault
                       {:app_id          #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
                        :quick_launch_id "70a7a607-dc17-4eb2-a477-9bffb64fccff"}))))

    (testing "invalid NewQuickLaunchGlobalDefault - both fields are strings instead of UUIDs"
      (is (not (valid? ql/NewQuickLaunchGlobalDefault
                       {:app_id          "20281d6d-7aa0-4491-a550-28a87487c9ef"
                        :quick_launch_id "70a7a607-dc17-4eb2-a477-9bffb64fccff"}))))

    (testing "invalid NewQuickLaunchGlobalDefault - extra fields (closed schema)"
      (is (not (valid? ql/NewQuickLaunchGlobalDefault
                       {:app_id          #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
                        :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"
                        :extra_field     "should not be here"}))))))

(deftest test-UpdateQuickLaunchGlobalDefault
  (testing "UpdateQuickLaunchGlobalDefault validation"
    (testing "valid UpdateQuickLaunchGlobalDefault - empty map (all fields optional)"
      (is (valid? ql/UpdateQuickLaunchGlobalDefault {})))

    (testing "valid UpdateQuickLaunchGlobalDefault with only one field (app_id)"
      (is (valid? ql/UpdateQuickLaunchGlobalDefault
                  {:app_id #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"})))

    (testing "valid UpdateQuickLaunchGlobalDefault with only one field (quick_launch_id)"
      (is (valid? ql/UpdateQuickLaunchGlobalDefault
                  {:quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"})))

    (testing "valid UpdateQuickLaunchGlobalDefault with both fields present"
      (is (valid? ql/UpdateQuickLaunchGlobalDefault
                  {:app_id          #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
                   :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"})))

    (testing "invalid UpdateQuickLaunchGlobalDefault - including id field (should be removed)"
      (is (not (valid? ql/UpdateQuickLaunchGlobalDefault
                       {:id              #uuid "416b20b1-fa0a-44e7-ac30-78a336e9c009"
                        :app_id          #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
                        :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"}))))

    (testing "invalid UpdateQuickLaunchGlobalDefault - id field only"
      (is (not (valid? ql/UpdateQuickLaunchGlobalDefault
                       {:id #uuid "416b20b1-fa0a-44e7-ac30-78a336e9c009"}))))

    (testing "invalid UpdateQuickLaunchGlobalDefault - app_id is string instead of UUID"
      (is (not (valid? ql/UpdateQuickLaunchGlobalDefault
                       {:app_id "20281d6d-7aa0-4491-a550-28a87487c9ef"}))))

    (testing "invalid UpdateQuickLaunchGlobalDefault - quick_launch_id is string instead of UUID"
      (is (not (valid? ql/UpdateQuickLaunchGlobalDefault
                       {:quick_launch_id "70a7a607-dc17-4eb2-a477-9bffb64fccff"}))))

    (testing "invalid UpdateQuickLaunchGlobalDefault - extra fields (closed schema)"
      (is (not (valid? ql/UpdateQuickLaunchGlobalDefault
                       {:app_id      #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
                        :extra_field "should not be here"}))))

    (testing "invalid UpdateQuickLaunchGlobalDefault - extra fields with all valid fields present"
      (is (not (valid? ql/UpdateQuickLaunchGlobalDefault
                       {:app_id          #uuid "20281d6d-7aa0-4491-a550-28a87487c9ef"
                        :quick_launch_id #uuid "70a7a607-dc17-4eb2-a477-9bffb64fccff"
                        :extra_field     "should not be here"}))))

    (testing "invalid UpdateQuickLaunchGlobalDefault - multiple invalid fields"
      (is (not (valid? ql/UpdateQuickLaunchGlobalDefault
                       {:app_id          "not-a-uuid"
                        :quick_launch_id "also-not-a-uuid"
                        :extra_field     "should not be here"}))))))
