(defproject org.cyverse/common-swagger-api "3.4.25"
  :description "Common library for Swagger documented RESTful APIs"
  :url "https://github.com/cyverse-de/common-swagger-api"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  ;; Fail the build on a new dependency conflict rather than printing a
  ;; warning nobody reads.
  :pedantic? :abort
  ;; Records versions Leiningen already resolves, read off the resolved
  ;; classpath rather than copied from lein's "Consider using these
  ;; :managed-dependencies" hint -- that hint names the version that LOST the
  ;; conflict, so pasting it would be a silent upgrade. Most of these arbitrate
  ;; metosin/compojure-api 1.1.14, the final release of an archived project whose
  ;; transitives disagree with each other, and clj-http vs buddy-core.  ;;
  ;; The jackson-* entries align a family that the cheshire 6 upgrade split
  ;; (core/cbor/smile at 2.21.1, databind/annotations left at 2.18.3). Jackson
  ;; needs those to move together; :pedantic? cannot see it because each artifact
  ;; is individually unambiguous, only the family has drifted apart.
  :managed-dependencies [[com.fasterxml.jackson.core/jackson-annotations "2.21"]
                         [com.fasterxml.jackson.core/jackson-databind "2.21.1"]
                         [commons-codec "1.16.1"]
                         [potemkin "0.4.5"]
                         [prismatic/schema "1.1.12"]
                         [ring/ring-codec "1.1.0"]
                         [ring/ring-core "1.6.3"]]
  :dependencies [[org.clojure/clojure "1.12.5"]
                 [cheshire "6.2.0"]
                 [metosin/compojure-api "1.1.14"]
                 [metosin/schema-tools "0.14.0"]
                 [org.cyverse/clojure-commons "3.0.13"]
                 [org.cyverse/heuristomancer "2.8.8"]
                 [org.flatland/ordered "1.15.12"]]
  :eastwood {:exclude-namespaces [common-swagger-api.schema.data.exists
                                  common-swagger-api.schema.data.tickets
                                  common-swagger-api.schema.stats]
             :linters [:wrong-arity :wrong-ns-form :wrong-pre-post :wrong-tag :misplaced-docstrings]}
  :plugins [[jonase/eastwood "1.4.3"]
            [lein-ancient "1.0.0"]
            [test2junit "1.4.4"]]
  ;; lein-clj-kondo lives in its own profile because its dependency tree is
  ;; internally inconsistent -- clj-kondo pulls Clojure 1.11.4 while its own sci
  ;; dependency pulls 1.12.0 -- which trips :pedantic? :abort on a conflict that
  ;; exists entirely inside a third-party plugin and never reaches the runtime
  ;; classpath. Lint with `lein with-profile +kondo clj-kondo`.
  :profiles {:kondo {:plugins [[com.github.clj-kondo/lein-clj-kondo "2026.08.04"]]
                     :pedantic? :warn}})
