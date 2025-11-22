(ns common-swagger-api.malli.filetypes
  (:require
   [common-swagger-api.malli :refer [NonBlankString]]
   [heuristomancer.core :as hm]
   [malli.util :as mu]))

(def ValidInfoTypes (conj (hm/supported-formats) "unknown"))

(def ValidInfoTypesEnum
  (apply vector :enum ValidInfoTypes))

(def ValidInfoTypesEnumPlusBlank
  (apply vector :enum (conj ValidInfoTypes "")))

(def TypesList
  [:map {:closed true}
   [:types
    {:description         "The available file types"
     :json-schema/example ValidInfoTypes}
    [:vector :string]]])

(def FileType
  [:map {:closed true}
   [:type
    {:description         "The file's type"
     :json-schema/example "fasta"}
    ValidInfoTypesEnumPlusBlank]])

(def FileTypeReturn
  (mu/merge
   FileType
   [:map
    [:user
     {:description         "The user performing the request"
      :json-schema/example "ipctest"}
     NonBlankString]

    [:path
     {:description         "The iRODS path to the file"
      :json-schema/example "/iplant/home/user/file.txt"}
     NonBlankString]]))
