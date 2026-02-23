(ns common-swagger-api.schema.containers
  (:require [common-swagger-api.coerce :as coerce]
            [common-swagger-api.schema :refer [->optional-param describe]]
            [schema.core :as s]))

(defn coerce-settings-long-values
  "Converts any values in the given settings map that should be a Long, according to the Settings schema."
  [settings]
  (-> settings
      (coerce/string->long :memory_limit)
      (coerce/string->long :min_memory_limit)
      (coerce/string->long :min_disk_space)))

(s/defschema Image
  (-> {:name                            s/Str
       :id                              s/Uuid
       (s/optional-key :tag)            s/Str
       (s/optional-key :url)            (s/maybe s/Str)
       (s/optional-key :deprecated)     Boolean
       (s/optional-key :auth)           (s/maybe s/Str)
       (s/optional-key :osg_image_path) (s/maybe s/Str)}
      (describe "A map describing a container image.")))

(s/defschema NewImage
  (-> Image
      (dissoc :id)
      (describe "The values needed to add a new image to a tool.")))

(s/defschema Settings
  (-> {(s/optional-key :cpu_shares)        (describe s/Int "The shares of the CPU that the tool container will receive")
       (s/optional-key :pids_limit)        s/Int
       (s/optional-key :memory_limit)      (describe Long "The amount of memory (in bytes) that the tool container is restricted to")
       (s/optional-key :min_memory_limit)  (describe Long "The minimum about of memory (in bytes) that is required to run the tool container")
       (s/optional-key :min_cpu_cores)     (describe Double "The minimum number of CPU cores needed to run the tool container")
       (s/optional-key :max_cpu_cores)     (describe Double "The maximum number of CPU cores allowed when running the tool container")
       (s/optional-key :min_gpus)          (describe s/Int "The minimum number of GPUs needed to run the tool container")
       (s/optional-key :max_gpus)          (describe s/Int "The maximum number of GPUs allowed when running the tool container")
       (s/optional-key :min_disk_space)    (describe Long "The minimum amount of disk space needed to run the tool container")
       (s/optional-key :gpu_models)        (describe [s/Str] "The GPU models that work with this tool container. Empty means no limitation on GPU model.")
       (s/optional-key :network_mode)      (describe s/Str "The network mode for the tool container")
       (s/optional-key :working_directory) (describe s/Str "The working directory in the tool container")
       (s/optional-key :name)              (describe s/Str "The name given to the tool container")
       (s/optional-key :entrypoint)        (describe s/Str "The entrypoint for a tool container")
       (s/optional-key :skip_tmp_mount)    Boolean
       (s/optional-key :uid)               s/Int
       :id                                 s/Uuid}
      (describe "The group of settings for a container.")))

(s/defschema NewSettings
  (-> Settings
      (->optional-param :id)
      (describe "The values needed to add a new container to a tool.")))

(s/defschema Device
  (-> {:host_path      (describe s/Str "A device's path on the container host")
       :container_path (describe s/Str "A device's path inside the tool container")
       :id             s/Uuid}
      (describe "Information about a device associated with a tool's container.")))

(s/defschema NewDevice
  (-> Device
      (dissoc :id)
      (describe "The map needed to add a device to a container.")))

(s/defschema Volume
  (-> {:host_path      (describe s/Str "The path to a bind mounted volume on the host machine")
       :container_path (describe s/Str "The path to a bind mounted volume in the tool container")
       :id             s/Uuid}
      (describe "A map representing a bind mounted container volume.")))

(s/defschema NewVolume
  (-> Volume
      (dissoc :id)
      (describe "A map for adding a new volume to a container.")))

(s/defschema DataContainer
  (-> (merge (dissoc Image :id)
             {:id                         s/Uuid
              :name_prefix                s/Str
              (s/optional-key :read_only) s/Bool})
      (describe "A description of a data container.")))

(s/defschema VolumesFrom
  (describe DataContainer "A description of a data container volumes-from settings."))

(s/defschema NewVolumesFrom
  (-> VolumesFrom
      (dissoc :id)
      (describe "A map for adding a new container from which to bind mount volumes.")))

(s/defschema Port
  (-> {:id                            s/Uuid
       (s/optional-key :host_port)    (s/maybe s/Int)
       :container_port                s/Int
       (s/optional-key :bind_to_host) (s/maybe Boolean)}
      (describe "Port information for a tool container.")))

(s/defschema NewPort
  (-> Port
      (dissoc :id)
      (describe "A map for adding a new port configuration to a tool container.")))

(s/defschema ProxySettings
  (-> {:id                             s/Uuid
       :image                          String
       :name                           String
       (s/optional-key :frontend_url)  (s/maybe String)
       (s/optional-key :cas_url)       (s/maybe String)
       (s/optional-key :cas_validate)  (s/maybe String)
       (s/optional-key :ssl_cert_path) (s/maybe String)
       (s/optional-key :ssl_key_path)  (s/maybe String)}
      (describe "Interactive app settings for the reverse proxy that runs on the HTCondor nodes for each job.")))

(s/defschema NewProxySettings
  (-> ProxySettings
      (dissoc :id)
      (describe "A map for adding new interactive app reverse proxy settings.")))

(def DevicesParamOptional       (s/optional-key :container_devices))
(def VolumesParamOptional       (s/optional-key :container_volumes))
(def VolumesFromParamOptional   (s/optional-key :container_volumes_from))
(def PortsParamOptional         (s/optional-key :container_ports))
(def ProxySettingsParamOptional (s/optional-key :interactive_apps))

(s/defschema ToolContainer
  (-> (merge Settings
             {DevicesParamOptional       (describe [Device] "A list of devices associated with a tool's container")
              VolumesParamOptional       (describe [Volume] "A list of Volumes associated with a tool's container")
              VolumesFromParamOptional   (describe [VolumesFrom] "The list of VolumeFroms associated with a tool's container.")
              PortsParamOptional         [Port]
              ProxySettingsParamOptional ProxySettings
              :image                     Image})
      (describe "All container and container image information associated with a tool.")))

(s/defschema NewToolContainer
  (-> (merge NewSettings
             {DevicesParamOptional       [NewDevice]
              VolumesParamOptional       [NewVolume]
              VolumesFromParamOptional   [NewVolumesFrom]
              PortsParamOptional         [NewPort]
              ProxySettingsParamOptional NewProxySettings
              :image                     NewImage})
      (describe "The settings for adding a new full container definition to a tool.")))
