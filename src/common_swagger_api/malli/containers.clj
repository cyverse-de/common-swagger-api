(ns common-swagger-api.malli.containers
  (:require
   [malli.util :as mu]))

(def Image
  [:map
   {:description "A map describing a container image."
    :closed      true}

   [:name
    {:description         "The name of the container image"
     :json-schema/example "example/image"}
    :string]

   [:id
    {:description         "The unique identifier for the image"
     :json-schema/example "123e4567-e89b-12d3-a456-426614174000"}
    :uuid]

   [:tag
    {:optional            true
     :description         "The tag for the container image"
     :json-schema/example "latest"}
    :string]

   [:url
    {:optional            true
     :description         "The URL for the container image"
     :json-schema/example "https://example.com/image"}
    [:maybe :string]]

   [:deprecated
    {:optional            true
     :description         "Whether the image is deprecated"
     :json-schema/example false}
    :boolean]

   [:auth
    {:optional            true
     :description         "Authentication information for the image"
     :json-schema/example "bearer token123"}
    [:maybe :string]]

   [:osg_image_path
    {:optional            true
     :description         "The OSG image path"
     :json-schema/example "/cvmfs/singularity.opensciencegrid.org/example/image:latest"}
    [:maybe :string]]])

(def NewImage
  (-> Image
      (mu/dissoc :id)
      (mu/update-properties assoc :description "The values needed to add a new image to a tool.")))

(def Settings
  [:map
   {:description "The group of settings for a container."
    :closed      true}

   [:cpu_shares
    {:optional            true
     :description         "The shares of the CPU that the tool container will receive"
     :json-schema/example 1024}
    :int]

   [:pids_limit
    {:optional            true
     :description         "The maximum number of PIDs allowed in the container"
     :json-schema/example 100}
    :int]

   [:memory_limit
    {:optional            true
     :description         "The amount of memory (in bytes) that the tool container is restricted to"
     :json-schema/example 1073741824}
    :int]

   [:min_memory_limit
    {:optional            true
     :description         "The minimum about of memory (in bytes) that is required to run the tool container"
     :json-schema/example 536870912}
    :int]

   [:min_cpu_cores
    {:optional            true
     :description         "The minimum number of CPU cores needed to run the tool container"
     :json-schema/example 1.0}
    :double]

   [:max_cpu_cores
    {:optional            true
     :description         "The maximum number of CPU cores allowed when running the tool container"
     :json-schema/example 4.0}
    :double]

   [:min_gpus
    {:optional            true
     :description         "The minimum number of GPUs needed to run the tool container"
     :json-schema/example 1}
    :int]

   [:max_gpus
    {:optional            true
     :description         "The maximum number of GPUs allowed when running the tool container"
     :json-schema/example 4}
    :int]

   [:min_disk_space
    {:optional            true
     :description         "The minimum amount of disk space needed to run the tool container"
     :json-schema/example 10737418240}
    :int]

   [:network_mode
    {:optional            true
     :description         "The network mode for the tool container"
     :json-schema/example "bridge"}
    :string]

   [:working_directory
    {:optional            true
     :description         "The working directory in the tool container"
     :json-schema/example "/workspace"}
    :string]

   [:name
    {:optional            true
     :description         "The name given to the tool container"
     :json-schema/example "analysis-container"}
    :string]

   [:entrypoint
    {:optional            true
     :description         "The entrypoint for a tool container"
     :json-schema/example "/bin/bash"}
    :string]

   [:skip_tmp_mount
    {:optional            true
     :description         "Whether to skip mounting the tmp directory"
     :json-schema/example false}
    :boolean]

   [:uid
    {:optional            true
     :description         "The user ID to run the container as"
     :json-schema/example 1000}
    :int]

   [:id
    {:description         "The unique identifier for the container settings"
     :json-schema/example "123e4567-e89b-12d3-a456-426614174000"}
    :uuid]])

(def NewSettings
  (-> Settings
      (mu/optional-keys [:id])
      (mu/update-properties assoc :description "The values needed to add a new container to a tool.")))

(def Device
  [:map
   {:description "Information about a device associated with a tool's container."
    :closed      true}

   [:host_path
    {:description         "A device's path on the container host"
     :json-schema/example "/dev/nvidia0"}
    :string]

   [:container_path
    {:description         "A device's path inside the tool container"
     :json-schema/example "/dev/nvidia0"}
    :string]

   [:id
    {:description         "The unique identifier for the device"
     :json-schema/example "123e4567-e89b-12d3-a456-426614174000"}
    :uuid]])

(def NewDevice
  (-> Device
      (mu/dissoc :id)
      (mu/update-properties assoc :description "The map needed to add a device to a container.")))

(def Volume
  [:map
   {:description "A map representing a bind mounted container volume."
    :closed      true}

   [:host_path
    {:description         "The path to a bind mounted volume on the host machine"
     :json-schema/example "/data/host/path"}
    :string]

   [:container_path
    {:description         "The path to a bind mounted volume in the tool container"
     :json-schema/example "/data/container/path"}
    :string]

   [:id
    {:description         "The unique identifier for the volume"
     :json-schema/example "123e4567-e89b-12d3-a456-426614174000"}
    :uuid]])

(def NewVolume
  (-> Volume
      (mu/dissoc :id)
      (mu/update-properties assoc :description "A map for adding a new volume to a container.")))

(def DataContainer
  (-> (mu/merge
       (mu/dissoc Image :id)
       [:map
        [:id
         {:description         "The unique identifier for the data container"
          :json-schema/example "123e4567-e89b-12d3-a456-426614174000"}
         :uuid]

        [:name_prefix
         {:description         "The name prefix for the data container"
          :json-schema/example "data-container"}
         :string]

        [:read_only
         {:optional            true
          :description         "Whether the data container is read-only"
          :json-schema/example false}
         :boolean]])
      (mu/update-properties assoc :description "A description of a data container.")))

(def VolumesFrom
  (mu/update-properties DataContainer assoc :description "A description of a data container volumes-from settings."))

(def NewVolumesFrom
  (-> VolumesFrom
      (mu/dissoc :id)
      (mu/update-properties assoc :description "A map for adding a new container from which to bind mount volumes.")))

(def Port
  [:map
   {:description "Port information for a tool container."
    :closed      true}

   [:id
    {:description         "The unique identifier for the port configuration"
     :json-schema/example "123e4567-e89b-12d3-a456-426614174000"}
    :uuid]

   [:host_port
    {:optional            true
     :description         "The port on the host machine"
     :json-schema/example 8080}
    [:maybe :int]]

   [:container_port
    {:description         "The port inside the container"
     :json-schema/example 80}
    :int]

   [:bind_to_host
    {:optional            true
     :description         "Whether to bind the port to the host"
     :json-schema/example true}
    [:maybe :boolean]]])

(def NewPort
  (-> Port
      (mu/dissoc :id)
      (mu/update-properties assoc :description "A map for adding a new port configuration to a tool container.")))

(def ProxySettings
  [:map
   {:description "Interactive app settings for the reverse proxy that runs on the HTCondor nodes for each job."
    :closed      true}

   [:id
    {:description         "The unique identifier for the proxy settings"
     :json-schema/example "123e4567-e89b-12d3-a456-426614174000"}
    :uuid]

   [:image
    {:description         "The proxy image name"
     :json-schema/example "discoenv/cas-proxy:latest"}
    :string]

   [:name
    {:description         "The name of the proxy"
     :json-schema/example "cas-proxy"}
    :string]

   [:frontend_url
    {:optional            true
     :description         "The frontend URL for the proxy"
     :json-schema/example "https://example.com/frontend"}
    [:maybe :string]]

   [:cas_url
    {:optional            true
     :description         "The CAS URL for authentication"
     :json-schema/example "https://cas.example.com"}
    [:maybe :string]]

   [:cas_validate
    {:optional            true
     :description         "The CAS validation endpoint"
     :json-schema/example "https://cas.example.com/validate"}
    [:maybe :string]]

   [:ssl_cert_path
    {:optional            true
     :description         "The path to the SSL certificate"
     :json-schema/example "/etc/ssl/certs/cert.pem"}
    [:maybe :string]]

   [:ssl_key_path
    {:optional            true
     :description         "The path to the SSL key"
     :json-schema/example "/etc/ssl/private/key.pem"}
    [:maybe :string]]])

(def NewProxySettings
  (-> ProxySettings
      (mu/dissoc :id)
      (mu/update-properties assoc :description "A map for adding new interactive app reverse proxy settings.")))

(def ToolContainer
  (-> (mu/merge
       Settings
       [:map
        [:container_devices
         {:optional    true
          :description "A list of devices associated with a tool's container"}
         [:vector Device]]

        [:container_volumes
         {:optional    true
          :description "A list of Volumes associated with a tool's container"}
         [:vector Volume]]

        [:container_volumes_from
         {:optional    true
          :description "The list of VolumeFroms associated with a tool's container."}
         [:vector VolumesFrom]]

        [:container_ports
         {:optional    true
          :description "A list of port configurations for the tool container"}
         [:vector Port]]

        [:interactive_apps
         {:optional    true
          :description "Interactive app proxy settings"}
         ProxySettings]

        [:image
         {:description "The container image information"}
         Image]])
      (mu/update-properties assoc :description "All container and container image information associated with a tool.")))

(def NewToolContainer
  (-> (mu/merge
       NewSettings
       [:map
        [:container_devices
         {:optional    true
          :description "A list of devices to add to the tool's container"}
         [:vector NewDevice]]

        [:container_volumes
         {:optional    true
          :description "A list of volumes to add to the tool's container"}
         [:vector NewVolume]]

        [:container_volumes_from
         {:optional    true
          :description "The list of containers from which to bind mount volumes"}
         [:vector NewVolumesFrom]]

        [:container_ports
         {:optional    true
          :description "A list of port configurations to add to the tool container"}
         [:vector NewPort]]

        [:interactive_apps
         {:optional    true
          :description "Interactive app proxy settings to add"}
         NewProxySettings]

        [:image
         {:description "The container image information to add"}
         NewImage]])
      (mu/update-properties assoc :description "The settings for adding a new full container definition to a tool.")))
