(ns common-swagger-api.malli.containers-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.containers :as containers]
   [common-swagger-api.malli.test-util :refer [decodes invalid json-schema-ok valid]]))

(def container-id #uuid "123e4567-e89b-12d3-a456-426614174000")
(def image-id #uuid "456e7890-b12c-34d5-e678-901234567890")

(def image {:name "ncbi/blast" :id image-id})

(def settings {:id container-id})

(def device {:host_path "/dev/nvidia0" :container_path "/dev/nvidia0" :id container-id})

(def volume {:host_path "/data/host/path" :container_path "/data/container/path" :id container-id})

(def data-container (assoc image :name_prefix "data-container"))

(def port {:id container-id :container_port 80})

(def proxy-settings {:id container-id :image "discoenv/cas-proxy:latest" :name "cas-proxy"})

(deftest Image
  (valid containers/Image image (assoc image :tag "latest" :url "https://example.com/image" :deprecated false)
         (assoc image :url nil :auth nil :osg_image_path nil))
  (invalid containers/Image {} (dissoc image :name) (assoc image :id "x") (assoc image :extra 1)))

(deftest NewImage
  (valid containers/NewImage (dissoc image :id) (assoc (dissoc image :id) :tag "latest"))
  (invalid containers/NewImage {} image (assoc (dissoc image :id) :deprecated "no")))

(deftest Settings
  (valid containers/Settings
         settings
         (assoc settings :cpu_shares 1024 :pids_limit 100 :memory_limit 1073741824 :uid 1000)
         (assoc settings :min_cpu_cores 1.0 :max_cpu_cores 4.0 :gpu_models ["A100"])
         (assoc settings :network_mode "bridge" :working_directory "/workspace" :skip_tmp_mount false))
  (invalid containers/Settings {} (assoc settings :memory_limit "1073741824")
           (assoc settings :gpu_models "A100") (assoc settings :extra 1)))

(deftest settings-decode-longs
  (decodes containers/Settings {:memory_limit "1024"} {:memory_limit 1024}))

(deftest NewSettings
  (valid containers/NewSettings {} settings (assoc settings :gpu_models []))
  (invalid containers/NewSettings {:id "x"} {:min_gpus 1.5} {:extra 1}))

(deftest Device
  (valid containers/Device device)
  (invalid containers/Device {} (dissoc device :host_path) (assoc device :id "x") (assoc device :extra 1)))

(deftest NewDevice
  (valid containers/NewDevice (dissoc device :id))
  (invalid containers/NewDevice {} device (dissoc device :id :container_path)))

(deftest Volume
  (valid containers/Volume volume)
  (invalid containers/Volume {} (dissoc volume :container_path) (assoc volume :host_path 1)
           (assoc volume :extra 1)))

(deftest NewVolume
  (valid containers/NewVolume (dissoc volume :id))
  (invalid containers/NewVolume {} volume (assoc (dissoc volume :id) :container_path nil)))

(deftest DataContainer
  (valid containers/DataContainer data-container (assoc data-container :read_only true :tag "latest"))
  (invalid containers/DataContainer {} (dissoc data-container :name_prefix)
           (assoc data-container :read_only "yes") (assoc data-container :extra 1)))

(deftest VolumesFrom
  (valid containers/VolumesFrom data-container (assoc data-container :read_only false))
  (invalid containers/VolumesFrom {} (dissoc data-container :id) (assoc data-container :extra 1)))

(deftest NewVolumesFrom
  (valid containers/NewVolumesFrom (dissoc data-container :id))
  (invalid containers/NewVolumesFrom {} data-container (dissoc data-container :id :name_prefix)))

(deftest Port
  (valid containers/Port port (assoc port :host_port 8080 :bind_to_host true)
         (assoc port :host_port nil :bind_to_host nil))
  (invalid containers/Port {} (dissoc port :container_port) (assoc port :container_port "80")
           (assoc port :extra 1)))

(deftest NewPort
  (valid containers/NewPort (dissoc port :id) (assoc (dissoc port :id) :host_port 8080))
  (invalid containers/NewPort {} port (assoc (dissoc port :id) :bind_to_host "yes")))

(deftest ProxySettings
  (valid containers/ProxySettings proxy-settings
         (assoc proxy-settings :frontend_url "https://example.com/frontend" :cas_url nil))
  (invalid containers/ProxySettings {} (dissoc proxy-settings :image)
           (assoc proxy-settings :name 1) (assoc proxy-settings :extra 1)))

(deftest NewProxySettings
  (valid containers/NewProxySettings (dissoc proxy-settings :id)
         (assoc (dissoc proxy-settings :id) :ssl_cert_path "/etc/ssl/certs/cert.pem"))
  (invalid containers/NewProxySettings {} proxy-settings (dissoc proxy-settings :id :name)))

(deftest ToolContainer
  (valid containers/ToolContainer
         (assoc settings :image image)
         (assoc settings :image image :container_devices [device] :container_volumes [volume])
         (assoc settings :image image :container_volumes_from [data-container] :container_ports [port]
                :interactive_apps proxy-settings))
  (invalid containers/ToolContainer
           {}
           settings
           (assoc settings :image (dissoc image :id))
           (assoc settings :image image :container_ports [(dissoc port :id)])))

(deftest NewToolContainer
  (valid containers/NewToolContainer
         {:image (dissoc image :id)}
         (assoc settings :image (dissoc image :id) :container_devices [(dissoc device :id)])
         {:image            (dissoc image :id)
          :interactive_apps (dissoc proxy-settings :id)
          :container_ports  [(dissoc port :id)]})
  (invalid containers/NewToolContainer
           {}
           {:image image}
           {:image (dissoc image :id) :container_volumes [volume]}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.containers))
