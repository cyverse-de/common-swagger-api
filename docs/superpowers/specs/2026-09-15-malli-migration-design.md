# Malli migration: sync, completion, and test framework

Date: 2026-09-15. Branch: `malli`.

## Goal

Bring the `common-swagger-api.malli.*` namespaces to full parity with the
`common-swagger-api.schema.*` namespaces, so downstream services can move
endpoints from plumatic/schema to Malli one at a time. Add a test framework that
verifies the Malli schemas validate correctly and keeps the two namespace trees
from drifting apart again.

Out of scope: changing any plumatic/schema definition, changing downstream
services, and switching this library to reitit.

## Current state

- Every `schema.*` namespace with a `malli.*` twin has been rebased onto main,
  but changes made on main after the twin was written were never ported.
- Ten `schema.*` namespaces have no twin: `apps.admin.apps`,
  `apps.admin.categories`, `apps.admin.reference-genomes`, `apps.pipeline`,
  `data.exists`, `data.navigation`, `data.tickets`, `metadata.comments`,
  `metadata.tags`, `tools.admin`.
- `lein test` aborts before running because the rebase kept older dependency
  versions and main's `:pedantic? :abort` turns the conflicts into failures.
- 31k lines of tests exist, one assertion per line, all passing once the build
  is fixed.
- Three FIXMEs record Malli rough edges: descriptions on `:map-of` values
  (stats) and duplicated field lists in recursive schemas (apps).

## Design

### 1. Build

`project.clj` takes main's dependency versions (schema-tools 0.14.0,
clojure-commons 3.0.13, heuristomancer 2.8.8), Malli moves to 0.20.1, and
`[org.clojure/tools.reader "1.5.2"]` joins `:managed-dependencies` with a
comment naming the conflict (compojure-api's ring-middleware-format vs. Malli's
edamame). Verified: the existing suite passes with this setup.

### 2. Test framework

New namespace `common-swagger-api.malli.test-util` under `test/`:

- `(valid schema & values)` and `(invalid schema & values)`: one `is` per
  value. A failure message carries the value and the humanized explanation.
- `(json-schema-ok ns-sym)`: for every public var in the namespace whose value
  is a Malli schema, assert `malli.json-schema/transform` returns without
  throwing. Vars holding strings (doc summaries) and response maps are skipped.
- `(decodes schema in out)`: asserts `m/decode` with the string transformer
  turns `in` into `out`. Used where plumatic middleware coerced strings to
  longs, since that middleware is not ported.

New namespace `common-swagger-api.malli.parity-test`: a table of
`[schema-ns malli-ns excluded-names]`. For each row, every public var name in
the schema namespace must exist in the Malli namespace unless excluded.
Exclusions are limited to plumatic-only helpers (`coerce-*` middleware,
`copy-json-schema-meta`, `optional-key->keyword`, `->optional-param`,
`SortFieldOptionalKey`, `Optional*Key` vars, the compojure-api route
re-exports) and are listed per row so each omission is visible.

Test files mirror source files one-to-one (`malli/apps_test.clj` for
`malli/apps.clj`). Each schema test defines one valid base value, then derives
cases with `assoc`/`dissoc`, and calls `valid`/`invalid` with them. Each test
namespace ends with one `json-schema-ok` test. The existing test files are
rewritten in this style; the current per-assertion files are deleted.

Comments in tests and schemas stay to one line and only where the reason for a
case is not obvious from the data.

### 3. Drift sync

Port into existing Malli namespaces, in plumatic definition order:

| Namespace | Changes to port |
|---|---|
| apps | ResourcePreset, ResourcePresetList, ResourcePresetRequest, ResourcePresetUpdateRequest, AttributeValueSelectionParams; `mount_data_store`, `overall_job_type`, `gpu_models`, `default_gpu_models`, and launch-time duration fields on the job view schemas; missing `ToolAppListingResponses`; `AppTools` is a Malli-only helper and stays |
| analyses | AnalysesRelauncherRequest and AnalysisIdPathParam names, `gpu_models`, duration fields |
| analyses.listing | nothing to port; `OptionalKeyFilter` is a plumatic-only key var and is excluded in the parity test |
| containers | `gpu_models`; the `*ParamOptional` vars become plain optional entries |
| ontologies | TargetOntologyHierarchies, TargetOntologyHierarchiesList |
| oauth | the 20 unmigrated defs |
| apps.communities | AppCommunityListRequest, CommunityIdPathParam, doc strings |
| apps.categories | remove OntologyAppListingPagingParams (removed on main) |
| apps.permission | ToolPermissionsListingResponses |
| stats | StatResponse, StatResponseIdsMap, StatResponsePathsMap, StatResponses |
| tools | PrivateToolImportResponse400 and the four `*Responses` maps |
| permanent-id-requests | ValidPermanentIDRequestListSortFields name |

Where a Malli def already exists under a different name than its plumatic twin,
the Malli def is renamed to match.

### 4. Finish the migration

Create the ten missing namespaces, mirroring the plumatic files def for def and
in the same order. Conventions, in addition to those in `CLAUDE.md`:

- Response maps use the reitit shape:
  `{200 {:body Schema :description "..."}}`. Status entries with no body keep
  only `:description`. `common-swagger-api.malli/CommonResponses` provides the
  shared 500 and `:default` entries.
- `doc-only` in `common-swagger-api.malli` wraps a schema so that validation
  uses one schema and documentation another, via Malli's `:json-schema`
  property override.
- `transform-enum` in `common-swagger-api.malli` maps a function over an
  enum's values, preserving properties.
- Plumatic `->optional-param` and `Optional*Key` vars translate to
  `malli.util/optional-keys` and inline `{:optional true}` entries.
- The `coerce-*` ring middleware is not ported; the `decodes` test covers the
  fields it handled.

### 5. FIXME resolution

- Recursive schemas (`AppParameterListGroup`, `PublishAppRequest`): the local
  registry entry is built from the base schema's form,
  `(conj (m/form Base) [:groups {:optional true} [:vector [:ref ::group]]])`,
  so the base fields are defined once. Verified on Malli 0.20.1 for validation
  and JSON schema output.
- `:map-of` value descriptions (stats): descriptions move back onto the value
  schema, as properties on `[:or ...]` or a `[:schema {...} X]` wrapper.
  Verified to appear in JSON schema output.

### 6. Process

Work proceeds one namespace at a time: sync or create the Malli namespace,
write its test file, run `lein test`, commit on `malli`. Independent namespaces
may be handled in parallel. The parity test is added early, with the not-yet-
migrated namespaces excluded, and exclusions are removed as namespaces land.

## Acceptance

- `lein test` runs and passes on the `malli` branch.
- The parity test covers every `schema.*` namespace with no namespace-level
  exclusions remaining.
- No FIXME comments remain in `src/common_swagger_api/malli`.
- Every Malli schema transforms to JSON schema without error.
