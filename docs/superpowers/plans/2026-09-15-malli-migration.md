# Malli Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Bring `common-swagger-api.malli.*` to def-for-def parity with `common-swagger-api.schema.*`, backed by a concise table-based test suite and a parity test that keeps the two trees in sync.

**Architecture:** Each plumatic namespace `common-swagger-api.schema.X` has a twin `common-swagger-api.malli.X` with the same public var names in the same order. Tests live in `test/common_swagger_api/malli/...` one file per source file, built on a small `test-util` namespace. A parity test compares public var names across every pair.

**Tech Stack:** Clojure 1.12, Leiningen, metosin/malli 0.20.1, clojure.test.

**Spec:** `docs/superpowers/specs/2026-09-15-malli-migration-design.md`

## Global Constraints

Every task implicitly includes these. They come from the spec and the repo `CLAUDE.md`.

- Lines are 120 characters or fewer. Files end with exactly one newline. No trailing whitespace.
- Malli def order in a namespace matches the plumatic def order in its twin.
- Malli def names match the plumatic names exactly. Where the branch already has a different name, rename the Malli def.
- Map entries: when an entry's properties map has more than one key, start it on the line after the key. Blank line between entries. Align property keys and values.
- Every scalar field or vector-of-scalar field carries `:json-schema/example`. Fields whose value is another schema do not.
- Long strings are split with `str`.
- Plumatic `Long` and `Int` become `:int`. `Double` becomes `:double`. `Date` becomes `inst?`. `Any` becomes `:any`. `s/Keyword` becomes `:keyword`. `(maybe X)` becomes `[:maybe X]`. `[X]` becomes `[:vector X]`. `(enum ...)` becomes `[:enum ...]`. `NonBlankString` is `common-swagger-api.malli/NonBlankString`.
- Maps are `[:map {:closed true} ...]`. `merge` becomes `malli.util/merge`. `dissoc`/`select-keys` become `mu/dissoc`/`mu/select-keys`. `st/optional-keys` and `->optional-param` become `mu/optional-keys`.
- Reuse existing Malli defs instead of re-typing fields. Never expand a plumatic map that was derived from another map; derive it the same way.
- Response maps use the reitit shape: `{200 {:body Schema :description "..."}}`. Entries with no body keep only `:description`. Merge `common-swagger-api.malli/CommonResponses` where the plumatic version merged `CommonResponses`.
- `doc-only`, `transform-enum`, `CommonResponses` come from `common-swagger-api.malli` (Task 4).
- The plumatic `coerce-*` ring middleware is not ported.
- Comments: one line, only where the data does not explain itself. No comment restating the code.
- Test files: one per source file, same relative path with `_test` suffix. `deftest` names are the schema names. Each test defines one valid base value and derives cases with `assoc`/`dissoc`. Every test namespace ends with `(deftest json-schema (json-schema-ok 'the.namespace))`.
- Test helpers come from `common-swagger-api.malli.test-util` (Task 2): `valid`, `invalid`, `decodes`, `json-schema-ok`.
- Run tests with `lein test :only <ns>` for one namespace and `lein test` for everything. Both must pass before a commit.
- Commit messages: imperative subject, then the attribution trailer lines shown in Task 1.
- Delete the old verbose test file for a namespace in the same task that writes its replacement.

---

### Task 1: Make the build run

**Files:**
- Modify: `project.clj`

**Interfaces:**
- Produces: a `lein test` that runs to completion.

- [ ] **Step 1: Align dependency versions with main and bump Malli**

Replace the `:dependencies` vector with:

```clojure
  :dependencies [[org.clojure/clojure "1.12.5"]
                 [cheshire "6.2.0"]
                 [metosin/compojure-api "1.1.14"]
                 [metosin/malli "0.20.1"]
                 [metosin/schema-tools "0.14.0"]
                 [org.cyverse/clojure-commons "3.0.13"]
                 [org.cyverse/heuristomancer "2.8.8"]
                 [org.flatland/ordered "1.15.12"]]
```

- [ ] **Step 2: Pin tools.reader**

Add this entry to `:managed-dependencies`, keeping the vector sorted:

```clojure
                         ;; compojure-api's ring-middleware-format and malli's edamame disagree.
                         [org.clojure/tools.reader "1.5.2"]
```

- [ ] **Step 3: Run the suite**

Run: `lein test`
Expected: `Ran 290 tests containing 5749 assertions. 0 failures, 0 errors.`

- [ ] **Step 4: Commit**

```bash
git add project.clj
git commit -m "Align dependencies with main and upgrade Malli to 0.20.1

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>
Claude-Session: https://claude.ai/code/session_01P3QpCqDY3fp2JVszR4Yr2D"
```

---

### Task 2: Test utilities and the core namespace tests

**Files:**
- Create: `test/common_swagger_api/malli/test_util.clj`
- Rewrite: `test/common_swagger_api/malli_test.clj`

**Interfaces:**
- Produces:
  - `(valid schema & values)`: one passing `is` per value.
  - `(invalid schema & values)`: one `is` per value asserting it does not validate.
  - `(decodes schema in out)`: asserts string-transformer decode of `in` equals `out`.
  - `(json-schema-ok ns-sym)`: asserts every public Malli schema var in the namespace transforms to JSON schema.

- [ ] **Step 1: Write the utilities**

```clojure
(ns common-swagger-api.malli.test-util
  (:require
   [clojure.test :refer [is]]
   [malli.core :as m]
   [malli.error :as me]
   [malli.json-schema :as js]
   [malli.transform :as mt]))

(defn valid
  "Asserts that every value validates against schema."
  [schema & values]
  (doseq [v values]
    (is (m/validate schema v)
        (str "expected valid: " (pr-str v) "\n" (pr-str (me/humanize (m/explain schema v)))))))

(defn invalid
  "Asserts that no value validates against schema."
  [schema & values]
  (doseq [v values]
    (is (not (m/validate schema v)) (str "expected invalid: " (pr-str v)))))

(defn decodes
  "Asserts that string-transformer decoding turns in into out."
  [schema in out]
  (is (= out (m/decode schema in mt/string-transformer))))

(defn- schema-var?
  [v]
  (let [x @v]
    (and (not (string? x))
         (not (fn? x))
         (try (m/schema x) true (catch Exception _ false)))))

(defn json-schema-ok
  "Asserts that every public schema var in the namespace transforms to JSON schema."
  [ns-sym]
  (require ns-sym)
  (doseq [[sym v] (ns-publics ns-sym) :when (schema-var? v)]
    (is (map? (js/transform @v)) (str sym))))
```

- [ ] **Step 2: Rewrite the core tests**

Replace `test/common_swagger_api/malli_test.clj` with:

```clojure
(ns common-swagger-api.malli-test
  (:require
   [clojure.test :refer [are deftest is]]
   [common-swagger-api.malli :as m]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]
   [malli.core :as malli]
   [malli.util :as mu]))

(deftest add-enum-values
  (are [expected schema vs] (mu/equals expected (apply m/add-enum-values schema vs))
    [:enum :foo :bar]                     [:enum :foo]                     [:bar]
    [:enum :foo :bar :baz]                [:enum :foo]                     [:bar :baz]
    [:enum :foo]                          [:enum :foo]                     []
    [:enum {:description "t"} :foo :bar]  [:enum {:description "t"} :foo]  [:bar])
  (is (thrown? Exception (m/add-enum-values [:map [:foo :string]] :bar))))

(deftest NonBlankString
  (valid m/NonBlankString "hello" "a")
  (invalid m/NonBlankString "" "   " nil 1))

(deftest StandardUserQueryParams
  (valid m/StandardUserQueryParams {:user "ipctest"})
  (invalid m/StandardUserQueryParams {} {:user ""} {:user "ipctest" :extra 1}))

(deftest StatusParams
  (valid m/StatusParams {} {:expecting "apps"})
  (invalid m/StatusParams {:expecting ""} {:unknown 1}))

(deftest PagingParams
  (valid m/PagingParams {} {:limit 1} {:offset 0} {:limit 50 :offset 10 :sort-field "name" :sort-dir "DESC"})
  (invalid m/PagingParams {:limit 0} {:limit -1} {:offset -1} {:sort-dir "asc"} {:limit "10"} {:extra 1}))

(def status
  {:service "apps" :description "An API" :version "1.0.0" :docs-url "http://apps/docs"})

(deftest StatusResponse
  (valid m/StatusResponse status (assoc status :expecting "apps"))
  (invalid m/StatusResponse {} (dissoc status :version) (assoc status :service "") (assoc status :extra 1)))

(deftest ErrorResponse
  (valid m/ErrorResponse {:error_code "ERR_X"} {:error_code "ERR_X" :reason "why"})
  (invalid m/ErrorResponse {} {:error_code ""} {:error_code "ERR_X" :reason ""}))

(deftest error-code-variants
  (are [schema code] (and (valid schema {:error_code code})
                          (invalid schema {:error_code "ERR_OTHER"}))
    m/ErrorResponseExists          "ERR_EXISTS"
    m/ErrorResponseNotWritable     "ERR_NOT_WRITEABLE"
    m/ErrorResponseForbidden       "ERR_FORBIDDEN"
    m/ErrorResponseNotFound        "ERR_NOT_FOUND"
    m/ErrorResponseIllegalArgument "ERR_ILLEGAL_ARGUMENT"))

(deftest ErrorResponseUnchecked
  (valid m/ErrorResponseUnchecked
         {:error_code "ERR_UNCHECKED_EXCEPTION"}
         {:error_code "ERR_SCHEMA_VALIDATION" :reason {:detail 1}})
  (invalid m/ErrorResponseUnchecked {:error_code "ERR_NOT_FOUND"}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli))
```

Note: `valid`/`invalid` return nil, so the `are` in `error-code-variants` must instead read:

```clojure
(deftest error-code-variants
  (are [schema code] (and (malli/validate schema {:error_code code})
                          (not (malli/validate schema {:error_code "ERR_OTHER"})))
    ...))
```

Use that form.

- [ ] **Step 3: Run**

Run: `lein test :only common-swagger-api.malli-test`
Expected: all pass, 0 failures.

- [ ] **Step 4: Commit**

```bash
git add test/common_swagger_api/malli/test_util.clj test/common_swagger_api/malli_test.clj
git commit -m "Add Malli test utilities and rewrite the core namespace tests"
```
(with the attribution trailer from Task 1.)

---

### Task 3: Parity test

**Files:**
- Create: `test/common_swagger_api/malli/parity_test.clj`

**Interfaces:**
- Produces: `rows`, a vector of `[schema-ns malli-ns excluded-names]`. Later tasks add rows and remove names from exclusion sets.

- [ ] **Step 1: Write the test**

```clojure
(ns common-swagger-api.malli.parity-test
  (:require
   [clojure.set :as set]
   [clojure.test :refer [deftest is]]))

;; compojure-api re-exports and plumatic-only helpers that have no Malli twin.
(def core-exclusions
  '#{api defapi describe swagger-routes routes defroutes undocumented middleware context
     GET ANY HEAD PATCH DELETE OPTIONS POST PUT
     ->optional-param copy-json-schema-meta optional-key->keyword SortFieldOptionalKey ->DocOnly map->DocOnly})

;; Per-namespace exclusions. "pending" entries are removed as tasks land.
(def rows
  '[[common-swagger-api.schema common-swagger-api.malli
     #{CommonResponses doc-only transform-enum}]                             ; pending Task 4
    [common-swagger-api.schema.analyses common-swagger-api.malli.analyses
     #{coerce-analysis-submission-requirements
       AnalysesRelauncherRequest AnalysisIdPathParam}]                       ; pending Task 6
    [common-swagger-api.schema.analyses.listing common-swagger-api.malli.analyses.listing
     #{OptionalKeyFilter}]
    [common-swagger-api.schema.apps common-swagger-api.malli.apps
     #{OptionalDebugKey OptionalDeprecatedKey OptionalGroupsKey OptionalParameterArgumentsKey
       OptionalParametersKey OptionalToolsKey
       AppJobStatsEndDateOptionalParam AppJobStatsStartDateOptionalParam
       AttributeValueSelectionParams ResourcePreset ResourcePresetList ResourcePresetRequest
       ResourcePresetUpdateRequest ToolAppListingResponses}]                  ; pending Task 5
    [common-swagger-api.schema.apps.bootstrap common-swagger-api.malli.apps.bootstrap #{}]
    [common-swagger-api.schema.apps.categories common-swagger-api.malli.apps.categories #{}]
    [common-swagger-api.schema.apps.communities common-swagger-api.malli.apps.communities
     #{AppCommunityAddDocs AppCommunityAddSummary AppCommunityDeleteDocs AppCommunityDeleteSummary
       AppCommunityListRequest CommunityIdPathParam}]                        ; pending Task 10
    [common-swagger-api.schema.apps.elements common-swagger-api.malli.apps.elements #{}]
    [common-swagger-api.schema.apps.metadata common-swagger-api.malli.apps.metadata #{}]
    [common-swagger-api.schema.apps.permission common-swagger-api.malli.apps.permission
     #{ToolPermissionsListingResponses}]                                     ; pending Task 11
    [common-swagger-api.schema.apps.rating common-swagger-api.malli.apps.rating #{}]
    [common-swagger-api.schema.apps.reference-genomes common-swagger-api.malli.apps.reference-genomes #{}]
    [common-swagger-api.schema.apps.workspace common-swagger-api.malli.apps.workspace #{}]
    [common-swagger-api.schema.callbacks common-swagger-api.malli.callbacks #{}]
    [common-swagger-api.schema.common common-swagger-api.malli.common #{}]
    [common-swagger-api.schema.containers common-swagger-api.malli.containers
     #{coerce-settings-long-values DevicesParamOptional PortsParamOptional ProxySettingsParamOptional
       VolumesFromParamOptional VolumesParamOptional}]
    [common-swagger-api.schema.data common-swagger-api.malli.data #{}]
    [common-swagger-api.schema.filetypes common-swagger-api.malli.filetypes #{}]
    [common-swagger-api.schema.groups common-swagger-api.malli.groups #{}]
    [common-swagger-api.schema.integration-data common-swagger-api.malli.integration-data #{}]
    [common-swagger-api.schema.metadata common-swagger-api.malli.metadata #{}]
    [common-swagger-api.schema.oauth common-swagger-api.malli.oauth
     #{AdminDeleteTokenInfoDescription AdminDeleteTokenInfoSummary AdminGetTokenInfoDescription
       AdminGetTokenInfoSummary AdminTokenInfo ApiName DeleteTokenInfoDescription DeleteTokenInfoSummary
       GetAccessCodeDescription GetAccessCodeSummary GetRedirectUrisDescription GetRedirectUrisSummary
       GetTokenInfoDescription GetTokenInfoSummary OAuthCallbackQueryParams OAuthCallbackResponse
       RedirectUris RedirectUrisDoc TokenInfo TokenInfoProxyParams}]         ; pending Task 9
    [common-swagger-api.schema.ontologies common-swagger-api.malli.ontologies
     #{TargetOntologyHierarchies TargetOntologyHierarchiesList}]             ; pending Task 8
    [common-swagger-api.schema.permanent-id-requests common-swagger-api.malli.permanent-id-requests
     #{ValidPermanentIDRequestListSortFields}]                               ; pending Task 12
    [common-swagger-api.schema.quicklaunches common-swagger-api.malli.quicklaunches #{}]
    [common-swagger-api.schema.sessions common-swagger-api.malli.sessions #{}]
    [common-swagger-api.schema.stats common-swagger-api.malli.stats
     #{StatResponse StatResponseIdsMap StatResponsePathsMap StatResponses}]  ; pending Task 13
    [common-swagger-api.schema.subjects common-swagger-api.malli.subjects #{}]
    [common-swagger-api.schema.tools common-swagger-api.malli.tools
     #{coerce-tool-import-requests coerce-tool-list-import-request
       PrivateToolImportResponse400 PrivateToolImportResponses ToolDeleteResponses ToolDetailsResponses
       ToolUpdateResponses}]                                                 ; pending Task 14
    [common-swagger-api.schema.webhooks common-swagger-api.malli.webhooks #{}]])

(defn- public-names [ns-sym]
  (require ns-sym)
  (set (keys (ns-publics ns-sym))))

(deftest every-schema-def-has-a-malli-twin
  (doseq [[schema-ns malli-ns excluded] rows]
    (let [missing (set/difference (public-names schema-ns) core-exclusions excluded (public-names malli-ns))]
      (is (empty? missing) (str malli-ns " is missing " (sort missing))))))
```

- [ ] **Step 2: Run and fix any mismatch**

Run: `lein test :only common-swagger-api.malli.parity-test`
Expected: pass. If a name is reported missing that is not in the plan's lists above, check whether the plumatic def is genuinely absent from Malli; if so add it to that row's exclusion set with a `; pending Task N` comment for the task that will port it.

- [ ] **Step 3: Commit**

```bash
git add test/common_swagger_api/malli/parity_test.clj
git commit -m "Add a parity test between schema and malli namespaces"
```

---

### Task 4: Core helpers in `common-swagger-api.malli`

**Files:**
- Modify: `src/common_swagger_api/malli.clj`
- Modify: `test/common_swagger_api/malli_test.clj`
- Modify: `test/common_swagger_api/malli/parity_test.clj` (remove the `common-swagger-api.schema` row's exclusions)

**Interfaces:**
- Produces:
  - `transform-enum [enum f]` returns an enum schema with `f` mapped over the values, properties preserved.
  - `doc-only [schema-to-use schema-to-doc]` returns `schema-to-use` whose JSON schema output is that of `schema-to-doc`.
  - `CommonResponses`: `{500 {:body ErrorResponseUnchecked :description "Unchecked errors"} :default {:body ErrorResponse :description "All other errors"}}`.

- [ ] **Step 1: Write failing tests** (append to `malli_test.clj`, before `json-schema`)

```clojure
(deftest transform-enum
  (is (mu/equals [:enum "a" "b"] (m/transform-enum [:enum :a :b] name)))
  (is (= {:description "d"} (malli/properties (m/transform-enum [:enum {:description "d"} :a] name)))))

(deftest doc-only
  (let [schema (m/doc-only [:map-of :keyword :string] [:map [:api-name :string]])]
    (valid schema {:github "http://x"})
    (invalid schema {:github 1})
    (is (= {:type "object" :properties {:api-name {:type "string"}} :required [:api-name]}
           (js/transform schema)))))

(deftest CommonResponses
  (is (= #{500 :default} (set (keys m/CommonResponses))))
  (is (= m/ErrorResponseUnchecked (get-in m/CommonResponses [500 :body]))))
```

Add `[malli.json-schema :as js]` to the test namespace requires.

- [ ] **Step 2: Run to see them fail**

Run: `lein test :only common-swagger-api.malli-test`
Expected: compile error, `transform-enum` not found.

- [ ] **Step 3: Implement**

Add to `src/common_swagger_api/malli.clj`, `transform-enum` right after `add-enum-values`, and the other two at the end of the file. Add `[malli.json-schema :as js]` to the requires.

```clojure
(defn transform-enum
  "Maps f over the values of an enum schema, preserving its properties."
  [enum f]
  (let [schema (m/schema enum)]
    (when-not (= (m/type schema) :enum)
      (throw (ex-info "provided schema is not an enum" {:schema enum})))
    (m/schema (into [:enum (m/properties schema)] (map f) (m/children schema)))))
```

```clojure
(defn doc-only
  "Validates with schema-to-use but documents schema-to-doc, mirroring the plumatic DocOnly record."
  [schema-to-use schema-to-doc]
  (mu/update-properties (m/schema schema-to-use) assoc :json-schema (js/transform schema-to-doc)))

(def CommonResponses
  {500      {:body        ErrorResponseUnchecked
             :description "Unchecked errors"}
   :default {:body        ErrorResponse
             :description "All other errors"}})
```

`(m/properties schema)` may be nil; `(into [:enum nil] ...)` is a valid form.

- [ ] **Step 4: Run tests, then parity**

Run: `lein test :only common-swagger-api.malli-test` then edit the first `rows` entry in `parity_test.clj` to `#{}` and run `lein test :only common-swagger-api.malli.parity-test`.
Expected: both pass.

- [ ] **Step 5: Commit**

```bash
git add src/common_swagger_api/malli.clj test/common_swagger_api/malli_test.clj test/common_swagger_api/malli/parity_test.clj
git commit -m "Add transform-enum, doc-only, and CommonResponses to the Malli core namespace"
```

---

### Task 5: Sync `common-swagger-api.malli.apps` and resolve its FIXMEs

**Files:**
- Modify: `src/common_swagger_api/malli/apps.clj`
- Rewrite: `test/common_swagger_api/malli/apps_test.clj` and the nine `test/common_swagger_api/malli/apps/app_*_test.clj` and `label_update_test.clj` files (delete them; fold their coverage into `apps_test.clj`)
- Modify: `test/common_swagger_api/malli/parity_test.clj`
- Reference: `src/common_swagger_api/schema/apps.clj`

**Interfaces:**
- Produces: `ResourcePreset`, `ResourcePresetList`, `ResourcePresetRequest`, `ResourcePresetUpdateRequest`, `AttributeValueSelectionParams`, `AppJobStatsStartDateOptionalParam`, `AppJobStatsEndDateOptionalParam`, `ToolAppListingResponses`; `AppParameterListGroup`, `PublishAppRequest` without duplicated fields.

- [ ] **Step 1: Diff the plumatic file against the Malli file def by def**

Run: `grep -oE '^\((def|defschema)\s+[^ ]+' src/common_swagger_api/schema/apps.clj | awk '{print $2}'` and the same for the Malli file. Walk the plumatic file top to bottom and confirm each def's fields exist in Malli. Known gaps to port, in addition to any you find:

1. `AppJobStatsStartDateOptionalParam` / `AppJobStatsEndDateOptionalParam`: plumatic defines these as optional keys. In Malli define them as the full entry vectors so they can be spliced into maps:
   ```clojure
   (def AppJobStatsStartDateOptionalParam
     [:start_date
      {:optional            true
       :description         AppJobStatsStartDateParamDocs
       :json-schema/example #inst "2024-01-01T00:00:00.000-00:00"}
      inst?])
   ```
   and the same for `:end_date`. Use them in `AppSearchParams` and wherever the plumatic file uses them.
2. `AttributeValueSelectionParams`:
   ```clojure
   (def AttributeValueSelectionParams
     [:map {:closed true}
      [:attribute
       {:optional            true
        :description         (str "Must be used in conjunction with `attribute_value`. If specified, only apps that "
                                  "are tagged with the specified attribute/value pair will be included in the listing.")
        :json-schema/example "category"}
       :string]

      [:attribute_value
       {:optional            true
        :description         (str "Must be used in conjunction with `attribute`. If specified, only apps that are "
                                  "tagged with the specified attribute/value pair will be included in the listing.")
        :json-schema/example "genomics"}
       :string]])
   ```
   Merge it into `AppListingPagingParams` and `AppSearchParams` (via `mu/merge`) and remove the inline `:attribute`/`:attribute_value` entries from `AppSearchParams`.
3. `ResourcePreset` family, placed after `AppStepResourceRequirements` to match plumatic order:
   ```clojure
   (def ResourcePreset
     [:map {:closed true}
      [:id
       {:description         "The resource preset identifier."
        :json-schema/example #uuid "8f0d3f2c-1c4a-4b7e-9a2f-5d6c7e8f9a0b"}
       :uuid]

      [:label
       {:description         "The display label for this preset."
        :json-schema/example "Small"}
       :string]

      [:description
       {:description         "A longer description of this preset, or null if not set."
        :json-schema/example "2 cores, 4 GiB"}
       [:maybe :string]]

      [:max_cpu_cores
       {:description         "The CPU cores allocated by this preset."
        :json-schema/example 2.0}
       :double]

      [:min_memory_limit
       {:description         "The memory in bytes allocated by this preset."
        :json-schema/example 4294967296}
       :int]

      [:max_gpus
       {:description         "The number of GPUs allocated by this preset."
        :json-schema/example 0}
       :int]

      [:time_limit_seconds
       {:description         "The VICE analysis time limit in seconds for this preset, or null for no override."
        :json-schema/example 28800}
       [:maybe :int]]

      [:display_order
       {:description         "The display ordering of this preset relative to others."
        :json-schema/example 1}
       :int]

      [:is_default
       {:description         "True if this preset is the global default selection."
        :json-schema/example false}
       :boolean]

      [:is_enabled
       {:description         "True if this preset is currently active and available for selection."
        :json-schema/example true}
       :boolean]])

   (def ResourcePresetList
     [:map {:closed true}
      [:resource_presets {:description "The list of resource presets."} [:vector ResourcePreset]]])

   (def ResourcePresetRequest
     (-> ResourcePreset
         (mu/dissoc :id)
         (mu/optional-keys [:description :time_limit_seconds :max_gpus :display_order :is_default :is_enabled])
         (mu/update-properties assoc :description
                               "Schema for creating a new resource preset. Required: label, max_cpu_cores, min_memory_limit.")))

   (def ResourcePresetUpdateRequest
     (-> ResourcePreset
         (mu/dissoc :id)
         mu/optional-keys
         (mu/update-properties assoc :description
                               (str "Schema for updating an existing resource preset. All fields are optional. "
                                    "Nullable fields (description, time_limit_seconds) accept null to clear the value."))))
   ```
4. Job view fields: confirm `mount_data_store`, `overall_job_type`, `gpu_models`, `default_gpu_models`, `min_gpus`, `max_gpus`, and the launch-time duration fields added by commit `64b7e59` (`git show 64b7e59 -- src/common_swagger_api/schema/apps.clj`) exist on the same Malli schemas as on the plumatic ones. Add any missing entry with a description copied from plumatic and an example.
5. `ToolAppListingResponses` after `AppPublishableResponse`:
   ```clojure
   (def ToolAppListingResponses
     (merge CommonResponses
            {200 {:body        AppListing
                  :description "The listing of Apps using the given Tool."}
             404 {:body        ErrorResponseNotFound
                  :description "The `tool-id` does not exist."}}))
   ```
   Add `CommonResponses` and `ErrorResponseNotFound` to the `common-swagger-api.malli` refer list.

- [ ] **Step 2: Resolve the recursive-schema FIXMEs**

Replace the `AppParameterListGroup` definition with one that reuses `AppParameterListItem`'s form:

```clojure
(def AppParameterListGroup
  (m/schema
   [:schema
    {:registry
     {::AppParameterListGroup
      (conj (m/form AppParameterListItem)
            [:arguments
             {:optional true :description TreeSelectorGroupParameterListDocs}
             [:vector AppParameterListItem]]
            [:groups
             {:optional true :description TreeSelectorGroupGroupListDocs}
             [:vector [:ref ::AppParameterListGroup]]])}}
    ::AppParameterListGroup]))
```

`m/form` of a `[:map ...]` vector returns the same vector, so `conj` appends entries. Apply the same technique to `PublishAppRequest`: keep the local registry, but build each registry entry from the form of the existing non-recursive Malli schema it duplicates (`AvuRequest`/`AvuListRequest` from `common-swagger-api.malli.metadata`, and the app-level fields from the existing Malli defs) instead of re-typing fields. Delete all three FIXME comments. If `PublishAppRequest` turns out not to be recursive at all, define it with `mu/merge` and no registry.

- [ ] **Step 3: Rewrite the tests**

Delete `test/common_swagger_api/malli/apps/app_details_test.clj`, `app_details_tool_test.clj`, `app_documentation_test.clj`, `app_group_request_test.clj`, `app_parameter_request_test.clj`, `app_request_test.clj`, `app_tool_listing_test.clj`, `app_tool_request_test.clj`, `app_version_request_test.clj`, `label_update_test.clj`, and replace `apps_test.clj`. Cover every map schema in the namespace with one base value and a handful of derived cases. Shape:

```clojure
(ns common-swagger-api.malli.apps-test
  (:require
   [clojure.test :refer [deftest]]
   [common-swagger-api.malli.apps :as apps]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]))

(def list-item
  {:id #uuid "789a0123-c45d-67e8-f901-234567890abc" :name "n" :value "v" :isDefault false})

(deftest AppParameterListItem
  (valid apps/AppParameterListItem list-item {:id (:id list-item)})
  (invalid apps/AppParameterListItem {} (assoc list-item :id "x") (assoc list-item :extra 1)))

(deftest AppParameterListGroup
  (valid apps/AppParameterListGroup
         list-item
         (assoc list-item :arguments [list-item] :groups [(assoc list-item :groups [list-item])]))
  (invalid apps/AppParameterListGroup (assoc list-item :groups [{:id "x"}])))

(def resource-preset
  {:id                 #uuid "8f0d3f2c-1c4a-4b7e-9a2f-5d6c7e8f9a0b"
   :label              "Small"
   :description        nil
   :max_cpu_cores      2.0
   :min_memory_limit   4294967296
   :max_gpus           0
   :time_limit_seconds nil
   :display_order      1
   :is_default         false
   :is_enabled         true})

(deftest ResourcePreset
  (valid apps/ResourcePreset resource-preset (assoc resource-preset :description "d" :time_limit_seconds 10))
  (invalid apps/ResourcePreset (dissoc resource-preset :label) (assoc resource-preset :max_cpu_cores "2")))

(deftest ResourcePresetRequest
  (valid apps/ResourcePresetRequest
         (select-keys resource-preset [:label :max_cpu_cores :min_memory_limit])
         (dissoc resource-preset :id))
  (invalid apps/ResourcePresetRequest resource-preset (select-keys resource-preset [:label])))

(deftest ResourcePresetUpdateRequest
  (valid apps/ResourcePresetUpdateRequest {} {:description nil} (dissoc resource-preset :id))
  (invalid apps/ResourcePresetUpdateRequest {:id (:id resource-preset)} {:label 1}))

(deftest AppSearchParams
  (valid apps/AppSearchParams {} {:attribute "a" :attribute_value "b" :search "x" :sort-field "name"})
  (invalid apps/AppSearchParams {:attribute 1} {:sort-field "nope"}))

;; ... one deftest per remaining map schema, same shape ...

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.apps))
```

Base values for the large schemas (`App`, `AppJobView`, `AppListing`, `AppRequest`, `AppVersionRequest`, `AppGroup`, `AppParameter`) can be lifted from the valid cases in the deleted test files; keep one base each.

- [ ] **Step 4: Update the parity row**

Set the `common-swagger-api.schema.apps` row exclusions to the six `Optional*Key` names only.

- [ ] **Step 5: Run**

Run: `lein test :only common-swagger-api.malli.apps-test`, then `lein test`.
Expected: pass.

- [ ] **Step 6: Commit**

```bash
git add -A src/common_swagger_api/malli/apps.clj test/common_swagger_api/malli/apps_test.clj test/common_swagger_api/malli/apps test/common_swagger_api/malli/parity_test.clj
git commit -m "Sync the Malli apps schemas with main and remove duplicated recursive fields"
```

---

### Task 6: Sync `common-swagger-api.malli.analyses`

**Files:**
- Modify: `src/common_swagger_api/malli/analyses.clj`
- Rewrite: `test/common_swagger_api/malli/analyses_test.clj`
- Modify: `parity_test.clj`
- Reference: `src/common_swagger_api/schema/analyses.clj`, `git show 937beb4 64b7e59 -- src/common_swagger_api/schema/analyses.clj`

- [ ] **Step 1: Port the drift**

1. Rename `AnalysisIDPathParam` to `AnalysisIdPathParam` and `AnalysisRelauncherRequest` to `AnalysesRelauncherRequest` (update every use in `src` and `test`).
2. Add `gpu_models` and the launch-time duration fields where the two referenced commits added them to the plumatic schemas, with descriptions copied and examples added.
3. Walk the plumatic file def by def as in Task 5 and port anything else missing.

- [ ] **Step 2: Rewrite the tests**

Same shape as Task 5. Add a decode check for the fields the omitted middleware coerced:

```clojure
(deftest requirements-decode-longs
  (decodes analyses/AnalysisSubmission
           (assoc-in submission [:requirements 0 :min_memory_limit] "1024")
           (assoc-in submission [:requirements 0 :min_memory_limit] 1024)))
```

(where `submission` is the test's base value; adjust the schema name to whichever Malli schema holds `:requirements`).

- [ ] **Step 3: Parity row**

Set the analyses row exclusions to `#{coerce-analysis-submission-requirements}`.

- [ ] **Step 4: Run and commit**

Run: `lein test`. Commit: `"Sync the Malli analyses schemas with main"`.

---

### Task 7: Sync `common-swagger-api.malli.containers`

**Files:**
- Modify: `src/common_swagger_api/malli/containers.clj`
- Rewrite: `test/common_swagger_api/malli/containers_test.clj`
- Reference: `src/common_swagger_api/schema/containers.clj`, `git show 937beb4 -- src/common_swagger_api/schema/containers.clj`

- [ ] **Step 1: Port the drift**

Add `gpu_models` (`[:vector :string]`, example `["A100"]`) wherever commit `937beb4` added it. Walk the plumatic file def by def and port anything else missing. The `*ParamOptional` vars stay excluded (they are plumatic optional-key vars).

- [ ] **Step 2: Rewrite the tests**

Same shape as Task 5. Include:

```clojure
(deftest settings-decode-longs
  (decodes containers/Settings {:memory_limit "1024"} {:memory_limit 1024}))
```

- [ ] **Step 3: Run and commit**

Run: `lein test`. Commit: `"Sync the Malli container schemas with main"`.

---

### Task 8: Sync `common-swagger-api.malli.ontologies`

**Files:**
- Modify: `src/common_swagger_api/malli/ontologies.clj`
- Rewrite: `test/common_swagger_api/malli/ontologies_test.clj`
- Modify: `parity_test.clj`
- Reference: `git show 58e42a3 -- src/common_swagger_api/schema/ontologies.clj`

- [ ] **Step 1: Port** `TargetOntologyHierarchies` and `TargetOntologyHierarchiesList` from the plumatic file, in plumatic order.
- [ ] **Step 2: Rewrite the tests** in the Task 5 shape.
- [ ] **Step 3: Parity row** exclusions to `#{}`.
- [ ] **Step 4: Run and commit**: `"Sync the Malli ontology schemas with main"`.

---

### Task 9: Complete `common-swagger-api.malli.oauth`

**Files:**
- Rewrite: `src/common_swagger_api/malli/oauth.clj`
- Rewrite: `test/common_swagger_api/malli/oauth_test.clj`
- Modify: `parity_test.clj`
- Reference: `src/common_swagger_api/schema/oauth.clj`

- [ ] **Step 1: Write the namespace**

```clojure
(ns common-swagger-api.malli.oauth
  (:require
   [common-swagger-api.malli :refer [doc-only NonBlankString]]
   [malli.util :as mu]))

(def ApiName
  [:string {:description         "The name of the external API"
            :json-schema/example "agave"}])

(def GetAccessCodeSummary "Obtain an OAuth access token for an authorization code")
(def GetAccessCodeDescription
  (str "Exchanges an OAuth authorization code for an access token and stores it for the authenticated user. "
       "This endpoint is called as part of the OAuth callback flow."))

;; ... the remaining Summary/Description strings, copied verbatim from the plumatic file ...

(def RedirectUris
  [:map-of
   [:keyword
    {:description         "The name of the API"
     :json-schema/example :agave}]
   [:string
    {:description         "The redirect URI"
     :json-schema/example "https://example.org/oauth/callback"}]])

(def RedirectUrisDoc
  [:map {:closed true}
   [:api-name
    {:description         "The redirect URI."
     :json-schema/example "https://example.org/oauth/callback"}
    :string]])

(def RedirectUrisResponse (doc-only RedirectUris RedirectUrisDoc))

(def OAuthCallbackQueryParams
  [:map {:closed true}
   [:code
    {:description         "The authorization code used to obtain the access token."
     :json-schema/example "SplxlOBeZQQYbYS6WxSbIA"}
    NonBlankString]

   [:state
    {:description         "The authorization state information."
     :json-schema/example "af0ifjsldkj"}
    NonBlankString]])

(def TokenInfoProxyParams
  [:map {:closed true}
   [:proxy-user
    {:optional            true
     :description         "The name of the proxy user for admin service calls."
     :json-schema/example "ipctest"}
    NonBlankString]])

(def OAuthCallbackResponse
  [:map {:closed true}
   [:state_info
    {:description         "Arbitrary state information required by the UI."
     :json-schema/example "/data"}
    :string]])

(def AdminTokenInfo
  [:map {:closed true}
   [:access_token
    {:description         "The access token itself."
     :json-schema/example "2YotnFZFEjr1zCsicMWpAA"}
    :string]

   [:expires_at
    {:description         "The token expiration time as milliseconds since the epoch."
     :json-schema/example 1735689600000}
    :int]

   [:refresh_token
    {:description         "The refresh token to use when the access token expires."
     :json-schema/example "tGzv3JOkF0XG5Qx2TlKWIA"}
    :string]

   [:webapp
    {:description         "The name of the external web application."
     :json-schema/example "agave"}
    :string]])

(def TokenInfo (mu/select-keys AdminTokenInfo [:expires_at :webapp]))
```

- [ ] **Step 2: Tests**

```clojure
(ns common-swagger-api.malli.oauth-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.oauth :as oauth]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]
   [malli.json-schema :as js]))

(deftest RedirectUrisResponse
  (valid oauth/RedirectUrisResponse {} {:agave "https://x"})
  (invalid oauth/RedirectUrisResponse {:agave 1} {"agave" "https://x"})
  (is (= [:api-name] (keys (:properties (js/transform oauth/RedirectUrisResponse))))))

(deftest OAuthCallbackQueryParams
  (valid oauth/OAuthCallbackQueryParams {:code "c" :state "s"})
  (invalid oauth/OAuthCallbackQueryParams {} {:code "" :state "s"} {:code "c" :state "s" :extra 1}))

(deftest TokenInfoProxyParams
  (valid oauth/TokenInfoProxyParams {} {:proxy-user "u"})
  (invalid oauth/TokenInfoProxyParams {:proxy-user ""} {:other 1}))

(deftest OAuthCallbackResponse
  (valid oauth/OAuthCallbackResponse {:state_info "s"})
  (invalid oauth/OAuthCallbackResponse {} {:state_info 1}))

(def token-info
  {:access_token "a" :expires_at 1735689600000 :refresh_token "r" :webapp "agave"})

(deftest AdminTokenInfo
  (valid oauth/AdminTokenInfo token-info)
  (invalid oauth/AdminTokenInfo (dissoc token-info :webapp) (assoc token-info :expires_at "x")))

(deftest TokenInfo
  (valid oauth/TokenInfo (select-keys token-info [:expires_at :webapp]))
  (invalid oauth/TokenInfo token-info {:webapp "agave"}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.oauth))
```

- [ ] **Step 3: Parity row** exclusions to `#{}`.
- [ ] **Step 4: Run and commit**: `"Complete the Malli oauth schemas"`.

---

### Task 10: Sync `common-swagger-api.malli.apps.communities` and `apps.categories`

**Files:**
- Modify: `src/common_swagger_api/malli/apps/communities.clj`, `src/common_swagger_api/malli/apps/categories.clj`
- Rewrite: `test/common_swagger_api/malli/apps/categories_test.clj`; create `test/common_swagger_api/malli/apps/communities_test.clj`
- Modify: `parity_test.clj`
- Reference: `src/common_swagger_api/schema/apps/communities.clj`, `src/common_swagger_api/schema/apps/categories.clj`

- [ ] **Step 1: communities**: add the four doc strings, `CommunityIdPathParam`, and `AppCommunityListRequest`, translated from the plumatic file in plumatic order.
- [ ] **Step 2: categories**: delete `OntologyAppListingPagingParams` (removed on main). Walk the plumatic file def by def and port anything else missing.
- [ ] **Step 3: Tests** in the Task 5 shape for both namespaces.
- [ ] **Step 4: Parity row** for communities to `#{}`.
- [ ] **Step 5: Run and commit**: `"Sync the Malli community and category schemas with main"`.

---

### Task 11: Sync `common-swagger-api.malli.apps.permission`

**Files:**
- Modify: `src/common_swagger_api/malli/apps/permission.clj`
- Create: `test/common_swagger_api/malli/apps/permission_test.clj` (there is no existing test file)
- Modify: `parity_test.clj`

- [ ] **Step 1: Port** `ToolPermissionsListingResponses` in the reitit shape from the plumatic file.
- [ ] **Step 2: Tests** in the Task 5 shape.
- [ ] **Step 3: Parity row** exclusions to `#{}`.
- [ ] **Step 4: Run and commit**: `"Add ToolPermissionsListingResponses and tests for the Malli permission schemas"`.

---

### Task 12: Sync `common-swagger-api.malli.permanent-id-requests`

**Files:**
- Modify: `src/common_swagger_api/malli/permanent_id_requests.clj`
- Rewrite: `test/common_swagger_api/malli/permanent_id_requests_test.clj`
- Modify: `parity_test.clj`

- [ ] **Step 1: Port**: define `ValidPermanentIDRequestListSortFields` as the set of sort-field keywords (as plumatic does) and use it inside the existing `ValidPermanentIDRequestListPagingParams`. Keep `ValidPermanentIDRequestListPagingParams` only if the plumatic file has it too; otherwise fold it into the def the plumatic file uses.
- [ ] **Step 2: Tests** in the Task 5 shape.
- [ ] **Step 3: Parity row** exclusions to `#{}`.
- [ ] **Step 4: Run and commit**: `"Sync the Malli permanent ID request schemas with main"`.

---

### Task 13: Sync `common-swagger-api.malli.stats` and resolve its FIXMEs

**Files:**
- Modify: `src/common_swagger_api/malli/stats.clj`
- Rewrite: `test/common_swagger_api/malli/stats_test.clj`
- Modify: `parity_test.clj`
- Reference: `src/common_swagger_api/schema/stats.clj:91-145`

- [ ] **Step 1: Resolve the map-of FIXMEs**

Rewrite `PathsMap`, `FilteredPathsMap`, `DataIdsMap`, `FilteredDataIdsMap` so the description lives on the value schema, matching plumatic:

```clojure
(def PathsMap
  [:map-of
   [:keyword
    {:description         "The iRODS data item's path"
     :json-schema/example (keyword "/example/home/janedoe/file.txt")}]
   [:or {:description "The data item's info"} FileStatInfo DirStatInfo]])
```

and for the filtered variants `[:schema {:description "The data item's info"} FilteredStatInfo]`. Remove any description that was moved into `FileStatInfo`/`DirStatInfo`/`FilteredStatInfo` only to work around this. Delete the four FIXME comments.

- [ ] **Step 2: Port the missing defs**

```clojure
(def StatResponsePathsMap
  [:map {:closed true}
   [(keyword ":/path/from/request/to/a/folder") {:description "A folder's info"} DirStatInfo]
   [(keyword ":/path/from/request/to/a/file") {:description "A file's info"} FileStatInfo]])

(def StatResponseIdsMap
  [:map {:closed true}
   [:some-folder-uuid {:description "A folder's info"} DirStatInfo]
   [:some-file-uuid {:description "A file's info"} FileStatInfo]])

(def StatResponse
  [:map {:closed true}
   [:paths {:optional true :description "A map of paths from the request to their status info"} StatResponsePathsMap]
   [:ids {:optional true :description "A map of ids from the request to their status info"} StatResponseIdsMap]])

(def StatResponses
  (merge CommonResponses
         {200 {:body        (doc-only StatusInfo StatResponse)
               :description "..."}   ; copy the description text from the plumatic file
          ...}))                      ; copy the remaining status entries from the plumatic file
```

- [ ] **Step 3: Tests** in the Task 5 shape. Include a check that `(js/transform (get-in stats/StatResponses [200 :body]))` has `:paths` and `:ids` under `:properties`.
- [ ] **Step 4: Parity row** exclusions to `#{}`.
- [ ] **Step 5: Run and commit**: `"Sync the Malli stats schemas with main and restore map-of descriptions"`.

---

### Task 14: Sync `common-swagger-api.malli.tools`

**Files:**
- Modify: `src/common_swagger_api/malli/tools.clj`
- Rewrite: `test/common_swagger_api/malli/tools_test.clj`
- Modify: `parity_test.clj`
- Reference: `src/common_swagger_api/schema/tools.clj`

- [ ] **Step 1: Port** `PrivateToolImportResponse400`, `PrivateToolImportResponses`, `ToolDeleteResponses`, `ToolDetailsResponses`, `ToolUpdateResponses` in the reitit shape. Also confirm `gpu_models` from commit `937beb4` is present. Walk the plumatic file def by def for anything else missing.
- [ ] **Step 2: Tests** in the Task 5 shape, plus:

```clojure
(deftest import-request-decodes-longs
  (decodes tools/ToolImportRequest
           (assoc-in tool-import [:container :memory_limit] "2048")
           (assoc-in tool-import [:container :memory_limit] 2048)))
```
- [ ] **Step 3: Parity row** exclusions to `#{coerce-tool-import-requests coerce-tool-list-import-request}`.
- [ ] **Step 4: Run and commit**: `"Sync the Malli tool schemas with main"`.

---

### Task 15: Rewrite the remaining existing test files

**Files:**
- Rewrite: `test/common_swagger_api/malli/analyses/listing_test.clj`, `apps/bootstrap_test.clj`, `apps/elements_test.clj`, `apps/reference_genomes_test.clj`, `apps/workspace_test.clj`, `callbacks_test.clj`, `common_test.clj`, `data_test.clj`, `filetypes_test.clj`, `groups_test.clj`, `integration_data_test.clj`, `metadata_test.clj`, `quicklaunches_test.clj`, `sessions_test.clj`, `subjects_test.clj`, `webhooks_test.clj`
- Create: `apps/metadata_test.clj`, `apps/rating_test.clj` (no existing tests)

- [ ] **Step 1**: For each file, replace it with the Task 5 shape: one base value per map schema, a `valid` call and an `invalid` call per schema, and a closing `json-schema` test. Keep the assertion count per schema to roughly six.
- [ ] **Step 2**: Run `lein test`. Expected: pass, with a total line count under `test/` below 10k (`cat $(find test -name '*.clj') | wc -l`).
- [ ] **Step 3**: Commit: `"Rewrite the remaining Malli tests as tables"`. This task can be split into two commits if it is more convenient.

---

### Task 16: New namespace `common-swagger-api.malli.apps.admin.categories`

**Files:**
- Create: `src/common_swagger_api/malli/apps/admin/categories.clj`, `test/common_swagger_api/malli/apps/admin/categories_test.clj`
- Modify: `parity_test.clj` (add row)
- Reference: `src/common_swagger_api/schema/apps/admin/categories.clj`

- [ ] **Step 1**: Translate the five defs (`AppCategorizationSummary`, `AppCategorizationDocs`, `AppCategoryIdList`, `AppCategorization`, `AppCategorizationRequest`) in plumatic order, reusing defs from `common-swagger-api.malli.apps` and `common-swagger-api.malli.apps.categories` where the plumatic file reuses them.
- [ ] **Step 2**: Tests in the Task 5 shape.
- [ ] **Step 3**: Add `[common-swagger-api.schema.apps.admin.categories common-swagger-api.malli.apps.admin.categories #{}]` to `rows`.
- [ ] **Step 4**: Run `lein test`; commit `"Add the Malli admin category schemas"`.

---

### Task 17: New namespace `common-swagger-api.malli.apps.admin.reference-genomes`

Same steps as Task 16 with:
- Reference: `src/common_swagger_api/schema/apps/admin/reference_genomes.clj` (10 defs: `ReferenceGenomeAddSummary`, `ReferenceGenomeAddDocs`, `ReferenceGenomeDeleteSummary`, `ReferenceGenomeDeleteDocs`, `ReferenceGenomeUpdateSummary`, `ReferenceGenomeUpdateDocs`, `ReferenceGenomeDeletionParams`, `ReferenceGenomeRequest`, `ReferenceGenomeAddRequest`, `ReferenceGenomeUpdateRequest`).
- Reuse `common-swagger-api.malli.apps.reference-genomes/ReferenceGenome` for the derived request maps exactly the way plumatic derives them.
- Parity row: `[common-swagger-api.schema.apps.admin.reference-genomes common-swagger-api.malli.apps.admin.reference-genomes #{}]`.
- Commit: `"Add the Malli admin reference genome schemas"`.

---

### Task 18: New namespace `common-swagger-api.malli.apps.pipeline`

Same steps as Task 16 with:
- Reference: `src/common_swagger_api/schema/apps/pipeline.clj` (23 defs).
- `PipelineMappingMap` is a map from keyword to string: `[:map-of :keyword :string]` with descriptions and examples on key and value.
- Reuse `App`-derived defs from `common-swagger-api.malli.apps` where plumatic does.
- Parity row: `[common-swagger-api.schema.apps.pipeline common-swagger-api.malli.apps.pipeline #{}]`.
- Commit: `"Add the Malli pipeline schemas"`.

---

### Task 19: New namespace `common-swagger-api.malli.apps.admin.apps`

Same steps as Task 16 with:
- Reference: `src/common_swagger_api/schema/apps/admin/apps.clj` (41 defs).
- `AppSubsetOptionalKey` is a plumatic optional-key var: exclude it in the parity row. Define the corresponding entry inline.
- Response maps (`ToolAdminAppListingResponses`) in the reitit shape.
- Parity row: `[common-swagger-api.schema.apps.admin.apps common-swagger-api.malli.apps.admin.apps #{AppSubsetOptionalKey}]`.
- Commit: `"Add the Malli admin app schemas"`.

---

### Task 20: New namespace `common-swagger-api.malli.tools.admin`

Same steps as Task 16 with:
- Reference: `src/common_swagger_api/schema/tools/admin.clj` (29 defs).
- Reuse `common-swagger-api.malli.tools` defs; response maps in the reitit shape.
- Parity row: `[common-swagger-api.schema.tools.admin common-swagger-api.malli.tools.admin #{}]`.
- Commit: `"Add the Malli admin tool schemas"`.

---

### Task 21: New namespace `common-swagger-api.malli.data.exists`

Same steps as Task 16 with:
- Reference: `src/common_swagger_api/schema/data/exists.clj` (10 defs). Uses `doc-only` for the 200 response body.
- `PathExistenceMap` is `[:map-of :keyword :boolean]` with descriptions and examples.
- Parity row: `[common-swagger-api.schema.data.exists common-swagger-api.malli.data.exists #{}]`.
- Commit: `"Add the Malli data existence schemas"`.

---

### Task 22: New namespace `common-swagger-api.malli.data.navigation`

Same steps as Task 16 with:
- Reference: `src/common_swagger_api/schema/data/navigation.clj` (14 defs).
- Reuse `common-swagger-api.malli.stats` and `common-swagger-api.malli.data` defs where plumatic does.
- Parity row: `[common-swagger-api.schema.data.navigation common-swagger-api.malli.data.navigation #{}]`.
- Commit: `"Add the Malli data navigation schemas"`.

---

### Task 23: New namespace `common-swagger-api.malli.data.tickets`

Same steps as Task 16 with:
- Reference: `src/common_swagger_api/schema/data/tickets.clj` (26 defs). Uses `doc-only`.
- `ModeParamOptionalKey` is a plumatic optional-key var: exclude it; define the entry inline.
- Parity row: `[common-swagger-api.schema.data.tickets common-swagger-api.malli.data.tickets #{ModeParamOptionalKey}]`.
- Commit: `"Add the Malli data ticket schemas"`.

---

### Task 24: New namespace `common-swagger-api.malli.metadata.comments`

Same steps as Task 16 with:
- Reference: `src/common_swagger_api/schema/metadata/comments.clj` (10 defs).
- Parity row: `[common-swagger-api.schema.metadata.comments common-swagger-api.malli.metadata.comments #{}]`.
- Commit: `"Add the Malli comment schemas"`.

---

### Task 25: New namespace `common-swagger-api.malli.metadata.tags`

Same steps as Task 16 with:
- Reference: `src/common_swagger_api/schema/metadata/tags.clj` (46 defs). Many are response maps; use the reitit shape.
- `TagValueString` is a bounded string: `[:string {:max 255 ...}]` if plumatic bounds it; copy the exact constraint.
- Parity row: `[common-swagger-api.schema.metadata.tags common-swagger-api.malli.metadata.tags #{}]`.
- Commit: `"Add the Malli tag schemas"`.

---

### Task 26: Final verification

- [ ] **Step 1**: `grep -rn FIXME src/common_swagger_api/malli` returns nothing.
- [ ] **Step 2**: Every row in `parity_test.clj` has an exclusion set containing only names of the kinds listed in the spec (`coerce-*`, `Optional*Key`/`*OptionalKey`, `*ParamOptional`, and the core plumatic helpers). Remove any `; pending` comment.
- [ ] **Step 3**: `lein test` passes; `lein eastwood` reports no new warnings; `lein with-profile +kondo clj-kondo` reports no new warnings in `src/common_swagger_api/malli*` or `test/`.
- [ ] **Step 4**: `cat $(find test -name '*.clj') | wc -l` is below 10000.
- [ ] **Step 5**: Commit any fixes: `"Finish the Malli migration verification"`.
