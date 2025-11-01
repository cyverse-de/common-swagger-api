# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Build and Package
```bash
# Compile Clojure source to .class files
lein compile

# Package the project into a JAR
lein jar

# Package with all dependencies
lein uberjar

# Deploy to Clojars repository
lein deploy
```

### Testing and Linting
```bash
# Run tests
lein test

# Run specific test namespace
lein test namespace.name

# Lint with Eastwood
lein eastwood

# Lint with clj-kondo
lein clj-kondo

# Check for outdated dependencies
lein ancient
```

### Development
```bash
# Start a REPL session
lein repl

# Clean build artifacts
lein clean

# Check syntax and warn on reflection
lein check
```

## Architecture

This is a Clojure library providing common dependencies, functions, and schema definitions for CyVerse services using metosin/compojure-api for Swagger-documented REST endpoints.

### Core Components

1. **Schema Definitions** (`src/common_swagger_api/schema/`)
   - Provides Schema-based type definitions for API endpoints
   - Organized by domain: apps, data, metadata, tools, analyses, etc.
   - Each domain has sub-modules for specific functionality

2. **Malli Integration** (`src/common_swagger_api/malli.clj`)
   - New Malli-based schema definitions (currently being introduced)
   - Provides `StatusResponse` schema and basic validators like `NonBlankString`

3. **Route Utilities** (`src/common_swagger_api/routes.clj`)
   - Custom Compojure API metadata handlers
   - Provides `:description-file` and `:deprecated` parameter restructuring
   - Helper for generating delegation blocks in API documentation

4. **Coercion** (`src/common_swagger_api/coerce.clj`)
   - Custom coercion utilities for API parameters

### Schema Organization

The schema definitions follow a hierarchical structure:
- Top-level schemas in `schema.clj` provide base functionality and imports from compojure-api
- Domain-specific schemas are organized in subdirectories
- Admin-specific schemas are nested under their respective domains (e.g., `apps/admin/`)

### Important Notes

- This is a library project with no application entry point
- Currently uses both Schema (primary) and Malli (being introduced) for validation
- Deployed to Clojars for use by other CyVerse services
- Jenkins CI/CD pipeline handles testing and deployment via Docker

## Code Style Guidelines

### General Formatting
- Keep line lengths to 120 characters or less when possible
- **Ensure each file ends with a newline character** - All source files (`.clj`) and test files must end with a single trailing newline
- Remove trailing whitespace from all lines
- Align keys and values in map definitions when possible

## Schema Migration Guidelines

When migrating schemas from `plumatic/schema` to `metosin/malli`:

1. **Namespace Mirroring** - Migrated schemas should maintain parallel namespace structure
   - Example: `common-swagger-api.schema.apps` → `common-swagger-api.malli.apps`

2. **Code Formatting**
   - Keep line lengths to 120 characters or fewer when possible
   - When splitting strings across multiple lines, use `str` to concatenate them
   - Place blank lines betwen field definitions in Malli schemas for redability.
   - When schema properties contain multiple fields, begin the properties map on a separate line from the field name for
     readability.

3. **Field Definition Reuse**
   - When translating map fields, reuse definitions wherever an identical or similar field exists elsewhere
   - For similar but not identical fields, use functions in `malli.util` to enable reuse while preserving differences
   - When a field is defined outside of a map (for example, in `common-swagger-api.schema.apps.rating/UserRatingParam`),
     It's useful to generate a function in the Malli translation so that the field name can be specified as a parameter
     when the field is used in a map.
   - When a function is defined for a field, use that function in the same places the corresponding variable was used
     in the original schema
   - Use `malli.util/merge` when combining schemas where the original used `merge`
   - When a Schema is just a modification of an existing map, use as much of the original map as possible. Do not expand
     the map when translating it to Malli.

4. **Schema Definition Order** - The order of schema definitions should be the same in `schema` and `malli` namespaces
   - This is done to make it easier to tell which schemas still need to be migrated.

5. **Example Values** - When migrating a field, please add an example value for any field that either has a scalar type
     (for example, a string or a UUID) or is a vector of scalar types. There's no need to add example values for fields
     whose values are described by another schema. Please be sure to use the correct keyword for example values. The
     keyword to use is `:json-schema/example`.
