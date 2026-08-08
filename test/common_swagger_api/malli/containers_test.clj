(ns common-swagger-api.malli.containers-test
  (:require [clojure.test :refer [deftest testing is]]
            [common-swagger-api.malli.containers :as c]
            [malli.core :as malli]))

(defn valid? [schema data]
  (malli/validate schema data))

(deftest test-Image
  (testing "Image schema validation"
    (testing "valid image"
      (is (valid? c/Image
                  {:name "example/image"
                   :id #uuid "123e4567-e89b-12d3-a456-426614174000"}))
      (is (valid? c/Image
                  {:name "example/image"
                   :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :tag "latest"
                   :url "https://example.com/image"
                   :deprecated false
                   :auth "bearer token"
                   :osg_image_path "/cvmfs/path"})))

    (testing "invalid image"
      (is (not (valid? c/Image {})))
      (is (not (valid? c/Image {:name "example/image"})))
      (is (not (valid? c/Image {:id #uuid "123e4567-e89b-12d3-a456-426614174000"}))))))

(deftest test-NewImage
  (testing "NewImage schema validation"
    (testing "valid new image"
      (is (valid? c/NewImage
                  {:name "example/image"}))
      (is (valid? c/NewImage
                  {:name "example/image"
                   :tag "latest"})))

    (testing "invalid new image - should not have id"
      (is (not (valid? c/NewImage
                       {:name "example/image"
                        :id #uuid "123e4567-e89b-12d3-a456-426614174000"}))))))

(deftest test-Settings
  (testing "Settings schema validation"
    (testing "valid settings"
      (is (valid? c/Settings
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"}))
      (is (valid? c/Settings
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :cpu_shares 1024
                   :memory_limit 1073741824
                   :min_cpu_cores 1.0
                   :max_cpu_cores 4.0}))
      (is (valid? c/Settings
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :min_gpus 1
                   :max_gpus 4})))

    (testing "invalid settings"
      (is (not (valid? c/Settings {})))
      (is (not (valid? c/Settings {:cpu_shares 1024}))))

    (testing "gpu fields are optional"
      (is (valid? c/Settings
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :min_gpus 1}))
      (is (valid? c/Settings
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                   :max_gpus 4})))))

(deftest test-NewSettings
  (testing "NewSettings schema validation"
    (testing "valid new settings"
      (is (valid? c/NewSettings {}))
      (is (valid? c/NewSettings
                  {:id #uuid "123e4567-e89b-12d3-a456-426614174000"}))
      (is (valid? c/NewSettings
                  {:cpu_shares 1024
                   :memory_limit 1073741824}))
      (is (valid? c/NewSettings
                  {:min_gpus 1
                   :max_gpus 4})))

    (testing "id is optional in NewSettings"
      (is (valid? c/NewSettings {:cpu_shares 1024})))

    (testing "gpu fields are optional in NewSettings"
      (is (valid? c/NewSettings {:min_gpus 1}))
      (is (valid? c/NewSettings {:max_gpus 4})))))

(deftest test-Device
  (testing "Device schema validation"
    (is (valid? c/Device
                {:host_path "/dev/nvidia0"
                 :container_path "/dev/nvidia0"
                 :id #uuid "123e4567-e89b-12d3-a456-426614174000"}))
    (is (not (valid? c/Device
                     {:host_path "/dev/nvidia0"
                      :container_path "/dev/nvidia0"})))))

(deftest test-NewDevice
  (testing "NewDevice schema validation"
    (is (valid? c/NewDevice
                {:host_path "/dev/nvidia0"
                 :container_path "/dev/nvidia0"}))
    (is (not (valid? c/NewDevice
                     {:host_path "/dev/nvidia0"
                      :container_path "/dev/nvidia0"
                      :id #uuid "123e4567-e89b-12d3-a456-426614174000"})))))

(deftest test-Volume
  (testing "Volume schema validation"
    (is (valid? c/Volume
                {:host_path "/data/host"
                 :container_path "/data/container"
                 :id #uuid "123e4567-e89b-12d3-a456-426614174000"}))
    (is (not (valid? c/Volume
                     {:host_path "/data/host"
                      :container_path "/data/container"})))))

(deftest test-NewVolume
  (testing "NewVolume schema validation"
    (is (valid? c/NewVolume
                {:host_path "/data/host"
                 :container_path "/data/container"}))
    (is (not (valid? c/NewVolume
                     {:host_path "/data/host"
                      :container_path "/data/container"
                      :id #uuid "123e4567-e89b-12d3-a456-426614174000"})))))

(deftest test-DataContainer
  (testing "DataContainer schema validation"
    (is (valid? c/DataContainer
                {:name "data-container"
                 :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                 :name_prefix "prefix"}))
    (is (valid? c/DataContainer
                {:name "data-container"
                 :id #uuid "123e4567-e89b-12d3-a456-426614174000"
                 :name_prefix "prefix"
                 :read_only true}))))

(deftest test-Port
  (testing "Port schema validation"
    (is (valid? c/Port
                {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                 :container_port 80}))
    (is (valid? c/Port
                {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                 :container_port 80
                 :host_port 8080
                 :bind_to_host true}))))

(deftest test-NewPort
  (testing "NewPort schema validation"
    (is (valid? c/NewPort
                {:container_port 80}))
    (is (valid? c/NewPort
                {:container_port 80
                 :host_port 8080}))))

(deftest test-ProxySettings
  (testing "ProxySettings schema validation"
    (is (valid? c/ProxySettings
                {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                 :image "discoenv/cas-proxy:latest"
                 :name "cas-proxy"}))
    (is (valid? c/ProxySettings
                {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                 :image "discoenv/cas-proxy:latest"
                 :name "cas-proxy"
                 :frontend_url "https://example.com"
                 :cas_url "https://cas.example.com"}))))

(deftest test-NewProxySettings
  (testing "NewProxySettings schema validation"
    (is (valid? c/NewProxySettings
                {:image "discoenv/cas-proxy:latest"
                 :name "cas-proxy"}))
    (is (not (valid? c/NewProxySettings
                     {:image "discoenv/cas-proxy:latest"
                      :name "cas-proxy"
                      :id #uuid "123e4567-e89b-12d3-a456-426614174000"})))))

(deftest test-ToolContainer
  (testing "ToolContainer schema validation"
    (is (valid? c/ToolContainer
                {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                 :image {:name "example/image"
                         :id #uuid "456e4567-e89b-12d3-a456-426614174000"}}))
    (is (valid? c/ToolContainer
                {:id #uuid "123e4567-e89b-12d3-a456-426614174000"
                 :image {:name "example/image"
                         :id #uuid "456e4567-e89b-12d3-a456-426614174000"}
                 :container_devices [{:host_path "/dev/nvidia0"
                                      :container_path "/dev/nvidia0"
                                      :id #uuid "789e4567-e89b-12d3-a456-426614174000"}]
                 :container_volumes [{:host_path "/data"
                                      :container_path "/data"
                                      :id #uuid "890e4567-e89b-12d3-a456-426614174000"}]}))))

(deftest test-NewToolContainer
  (testing "NewToolContainer schema validation"
    (is (valid? c/NewToolContainer
                {:image {:name "example/image"}}))
    (is (valid? c/NewToolContainer
                {:image {:name "example/image"}
                 :container_devices [{:host_path "/dev/nvidia0"
                                      :container_path "/dev/nvidia0"}]
                 :container_volumes [{:host_path "/data"
                                      :container_path "/data"}]}))))
