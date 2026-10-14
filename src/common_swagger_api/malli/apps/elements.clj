(ns common-swagger-api.malli.apps.elements)

;; Endpoint description definitions

(def AppElementsListingSummary "List All Available App Elements")
(def AppElementsListingDocs
  "This endpoint may be used to obtain lists of all available elements that may be included in an App.")

(def AppElementsDataSourceListingSummary "List App File Parameter Data Sources")
(def AppElementsDataSourceListingDocs
  (str "Data sources are the known possible sources for file parameters. In most cases, file parameters will come "
       "from a plain file. The only other options that are currently available are redirected standard output and "
       "redirected standard error output. Both of these options apply only to file parameters that are associated "
       "with an output."))

(def AppElementsFileFormatListingSummary "List App Parameter File Formats")
(def AppElementsFileFormatListingDocs
  (str "The known file formats can be used to describe supported input or output formats for a tool. For example, "
       "tools in the FASTX toolkit may support FASTA files, several different varieties of FASTQ files and "
       "Barcode files, among others."))

(def AppElementsInfoTypeListingSummary "List Tool Info Types")
(def AppElementsInfoTypeListingDocs
  (str "The known information types can be used to describe the type of information consumed or produced by a tool. "
       "This is distinct from the data format because some data formats may contain multiple types of information "
       "and some types of information can be described using multiple data formats. For example, the Nexus format "
       "can contain multiple types of information, including phylogenetic trees. And phylogenetic trees can also "
       "be represented in PhyloXML format, and a large number of other formats. The file format and information "
       "type together identify the type of input consumed by a tool or the type of output produced by a tool."))

(def AppElementsParameterTypeListingSummary "List App Parameter Types")
(def AppElementsParameterTypeListingDocs
  (str "Parameter types represent the types of information that can be passed to a tool. For command-line tools, "
       "a parameter generally represents a command-line option and the parameter type represents the type of data "
       "required by the command-line option. For example a `Boolean` parameter generally corresponds to a single "
       "command-line flag that takes no arguments. A `Text` parameter, on the other hand, generally represents "
       "some sort of textual information. Some parameter types are not supported by all tool types, so it is "
       "helpful in some cases to filter parameter types either by the tool type or optionally by the tool (which "
       "is used to determine the tool type). If you filter by both tool type and tool ID then the tool type will "
       "take precedence. Including either an undefined tool type or an undefined tool type name will result in "
       "an error."))

(def AppElementsRuleTypeListingSummary "List App Parameter Rule Types")
(def AppElementsRuleTypeListingDocs
  (str "Rule types represent types of validation rules that may be defined to validate user input. For example, "
       "if a parameter value must be an integer between 1 and 10 then the `IntRange` rule type may be used. "
       "Similarly, if a parameter value must contain data in a specific format, such as a phone number, then the "
       "`Regex` rule type may be used."))

(def AppElementsToolTypeListingSummary "List App Tool Types")
(def AppElementsToolTypeListingDocs
  (str "Tool types are known types of tools in the Discovery Environment. Generally, there's a different tool "
       "type for each execution environment that is supported by the DE."))

(def AppElementsValueTypeListingSummary "List App Parameter and Rule Value Types")
(def AppElementsValueTypeListingDocs
  (str "If you look closely at the response schema for parameter types and rule types listings then you'll notice "
       "that each parameter type has a single value type assocaited with it and each rule type has one or more "
       "value types associated with it. The purpose of value types is specifically to link parameter types and "
       "rule types. The App Editor uses the value type to determine which types of rules can be applied to a "
       "parameter that is being defined by the user."))

;; Schema definitions

(def AppParameterTypeParams
  [:map {:closed true}
   [:tool-type
    {:optional            true
     :description         "Filters results by tool type"
     :json-schema/example "executable"}
    :string]

   [:tool-id
    {:optional            true
     :description         "Filters results by tool identifier"
     :json-schema/example #uuid "123e4567-e89b-12d3-a456-426614174000"}
    :uuid]])

(def DataSource
  [:map {:closed true}
   [:id
    {:description         "A UUID that is used to identify the Data Source"
     :json-schema/example #uuid "123e4567-e89b-12d3-a456-426614174000"}
    :uuid]

   [:name
    {:description         "The Data Source's name"
     :json-schema/example "file"}
    :string]

   [:description
    {:description         "The Data Source's description"
     :json-schema/example "A plain file"}
    :string]

   [:label
    {:description         "The Data Source's label"
     :json-schema/example "File"}
    :string]])

(def FileFormat
  [:map {:closed true}
   [:id
    {:description         "A UUID that is used to identify the File Format"
     :json-schema/example #uuid "456e7890-b12c-34d5-e678-901234567890"}
    :uuid]

   [:name
    {:description         "The File Format's name"
     :json-schema/example "FASTA"}
    :string]

   [:label
    {:optional            true
     :description         "The File Format's label"
     :json-schema/example "FASTA sequence file"}
    :string]])

(def InfoType
  [:map {:closed true}
   [:id
    {:description         "A UUID that is used to identify the Info Type"
     :json-schema/example #uuid "789a0123-c45d-67e8-f901-234567890abc"}
    :uuid]

   [:name
    {:description         "The Info Type's name"
     :json-schema/example "BarCode"}
    :string]

   [:label
    {:optional            true
     :description         "The Info Type's label"
     :json-schema/example "Barcode sequence"}
    :string]])

(def ParameterType
  [:map {:closed true}
   [:id
    {:description         "A UUID that is used to identify the Parameter Type"
     :json-schema/example #uuid "987e6543-e21b-32c1-b456-426614174000"}
    :uuid]

   [:name
    {:description         "The Parameter Type's name"
     :json-schema/example "Text"}
    :string]

   [:description
    {:optional            true
     :description         "The Parameter Type's description"
     :json-schema/example "Free-form text input"}
    :string]

   [:value_type
    {:description         "The Parameter Type's value type name"
     :json-schema/example "String"}
    :string]])

(def RuleType
  [:map {:closed true}
   [:id
    {:description         "A UUID that is used to identify the Rule Type"
     :json-schema/example #uuid "567e8901-c34d-56e7-f890-123456789012"}
    :uuid]

   [:name
    {:description         "The Rule Type's name"
     :json-schema/example "IntRange"}
    :string]

   [:description
    {:optional            true
     :description         "The Rule Type's description"
     :json-schema/example "Integer value within a specified range"}
    :string]

   [:rule_description_format
    {:description         "The Rule Type's description format"
     :json-schema/example "Enter a number between {min} and {max}"}
    :string]

   [:subtype
    {:description         "The Rule Type's subtype"
     :json-schema/example "Number"}
    :string]

   [:value_types
    {:description         "The Rule Type's value types"
     :json-schema/example ["Integer" "Number"]}
    [:vector :string]]])

(def ToolType
  [:map {:closed true}
   [:id
    {:description         "A UUID that is used to identify the Tool Type"
     :json-schema/example #uuid "abc12345-def6-7890-1234-567890abcdef"}
    :uuid]

   [:name
    {:description         "The Tool Type's name"
     :json-schema/example "executable"}
    :string]

   [:description
    {:optional            true
     :description         "The Tool Type's description"
     :json-schema/example "Command-line executable"}
    :string]

   [:label
    {:description         "The Tool Type's label"
     :json-schema/example "Executable"}
    :string]])

(def ValueType
  [:map {:closed true}
   [:id
    {:description         "A UUID that is used to identify the Value Type"
     :json-schema/example #uuid "def67890-abc1-2345-6789-0abcdef12345"}
    :uuid]

   [:name
    {:description         "The Value Type's name"
     :json-schema/example "String"}
    :string]

   [:description
    {:description         "The Value Type's description"
     :json-schema/example "Character string value"}
    :string]])

(def DataSourceListing
  [:map {:closed true}
   [:data_sources
    {:description "Listing of App File Parameter Data Sources"}
    [:vector DataSource]]])

(def FileFormatListing
  [:map {:closed true}
   [:formats
    {:description "Listing of App Parameter File Formats"}
    [:vector FileFormat]]])

(def InfoTypeListing
  [:map {:closed true}
   [:info_types
    {:description "Listing of Tool Info Types"}
    [:vector InfoType]]])

(def ParameterTypeListing
  [:map {:closed true}
   [:parameter_types
    {:description "Listing of App Parameter Types"}
    [:vector ParameterType]]])

(def RuleTypeListing
  [:map {:closed true}
   [:rule_types
    {:description "Listing of App Parameter Rule Types"}
    [:vector RuleType]]])

(def ToolTypeListing
  [:map {:closed true}
   [:tool_types
    {:description "Listing of App Tool Types"}
    [:vector ToolType]]])

(def ValueTypeListing
  [:map {:closed true}
   [:value_types
    {:description "Listing of App Parameter and Rule Value Types"}
    [:vector ValueType]]])
