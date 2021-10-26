| | | |
|---:|:---:|:---:|
| [**main**](https://github.com/pmonks/tools-licenses/tree/main) | [![Lint](https://github.com/pmonks/tools-licenses/workflows/lint/badge.svg?branch=main)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Alint) | [![Dependencies](https://github.com/pmonks/tools-licenses/workflows/dependencies/badge.svg?branch=main)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Adependencies) |
| [**dev**](https://github.com/pmonks/tools-licenses/tree/dev)  | [![Lint](https://github.com/pmonks/tools-licenses/workflows/lint/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Alint) | [![Dependencies](https://github.com/pmonks/tools-licenses/workflows/dependencies/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Adependencies) |

[![Latest Version](https://img.shields.io/clojars/v/com.github.pmonks/tools-licenses)](https://clojars.org/com.github.pmonks/tools-licenses/) [![Open Issues](https://img.shields.io/github/issues/pmonks/tools-licenses.svg)](https://github.com/pmonks/tools-licenses/issues) [![License](https://img.shields.io/github/license/pmonks/tools-licenses.svg)](https://github.com/pmonks/tools-licenses/blob/main/LICENSE)


# tools-licenses

A Clojure [tools.build](https://github.com/clojure/tools.build) task library related to dependency licenses.  Somewhat inspired by the (discontinued) [`lein-licenses`](https://github.com/technomancy/lein-licenses/) Leiningen plugin, but with the added benefit of license canonicalisation (leveraging the *excellent* [Software Package Data Exchange (SPDX)](https://spdx.dev/) standard).

## Tasks

1. `licenses` - attempt to display the licenses used by all transitive dependencies of the project

## Using the library

### Dependency

Express the correct maven dependencies in your `deps.edn`, for a build tool alias:

```edn
  :aliases
    :build
      {:deps       {com.github.pmonks/tools-licenses {:mvn/version "LATEST_CLOJARS_VERSION"}
                    io.github.seancorfield/build-clj {:git/tag "v0.5.2" :git/sha "8f75b81"}}
       :ns-default your.build.ns}
```

Note that you must express an explicit dependency on `io.github.seancorfield/build-clj`, as that project [doesn't publish artifacts to Clojars yet](https://github.com/seancorfield/build-clj/issues/11), and transitive dependencies that only have git coordinates are not supported by tools.deps yet.


### Require the namespace

```clojure
(ns your.build.ns
  (:require [tools-licenses.tasks :as lic]))
```

### Add a `licenses` build task to your build

```clojure
(defn licenses
  "Attempts to list all licenses for the transitive set of dependencies of the project, using SPDX license expressions."
  [opts]
  (-> opts
      (set-opts)
      (lic/licenses)))
```

### Use the `licenses` build task

Example summary output:

```
$ clj -T:build licenses
This project:
  * Apache-2.0

Upstream dependencies (occurrences):
  * Apache-2.0 (65)
  * BSD-3-Clause (1)
  * CDDL-1.0 OR (GPL-2.0 WITH Classpath-exception-2.0) (1)
  * EPL-1.0 (31)
  * EPL-2.0 (4)
  * LGPL-2.0 (2)
  * MIT (6)
  * Public domain (1)
```

Example detailed output:

```
$ clj -T:build licenses :output :detailed
This project:
  * com.github.pmonks/tools-licenses: Apache-2.0

Direct dependencies:
  * camel-snake-kebab/camel-snake-kebab: EPL-1.0
  * cheshire/cheshire: MIT
  * clj-xml-validation/clj-xml-validation: EPL-1.0
  * com.github.pmonks/tools-convenience: Apache-2.0
  * io.github.clojure/tools.build: EPL-1.0
  * io.github.seancorfield/build-clj: Apache-2.0
  * org.clojure/clojure: EPL-1.0
  * org.clojure/data.xml: EPL-1.0
  * tolitius/xml-in: EPL-1.0

Transitive dependencies:
  * aopalliance/aopalliance: Public domain
  * ch.qos.logback/logback-classic: EPL-1.0, LGPL-2.0
  * ch.qos.logback/logback-core: EPL-1.0, LGPL-2.0
  * clj-commons/pomegranate: EPL-1.0
  * com.amazonaws/aws-java-sdk-core: Apache-2.0
  * com.amazonaws/aws-java-sdk-kms: Apache-2.0
  * com.amazonaws/aws-java-sdk-s3: Apache-2.0
  * com.amazonaws/aws-java-sdk-sts: Apache-2.0
  * com.amazonaws/jmespath-java: Apache-2.0
  * com.cognitect/http-client: Apache-2.0
  * com.cognitect.aws/api: Apache-2.0
  * com.cognitect.aws/endpoints: Apache-2.0
  * com.cognitect.aws/s3: Apache-2.0
  * com.fasterxml.jackson.core/jackson-annotations: Apache-2.0
  * com.fasterxml.jackson.core/jackson-core: Apache-2.0
  * com.fasterxml.jackson.core/jackson-databind: Apache-2.0
  * com.fasterxml.jackson.dataformat/jackson-dataformat-cbor: Apache-2.0
  * com.fasterxml.jackson.dataformat/jackson-dataformat-smile: Apache-2.0
  * com.google.code.findbugs/jsr305: Apache-2.0
  * com.google.errorprone/error_prone_annotations: Apache-2.0
  * com.google.guava/guava: Apache-2.0
  * com.google.inject/guice$no_aop: Apache-2.0
  * com.google.j2objc/j2objc-annotations: Apache-2.0
  * commons-codec/commons-codec: Apache-2.0
  * commons-io/commons-io: Apache-2.0
  * commons-logging/commons-logging: Apache-2.0
  * io.github.seancorfield/build-uber-log4j2-handler: Apache-2.0
  * javax.annotation/jsr250-api: CDDL-1.0 OR (GPL-2.0 WITH Classpath-exception-2.0)
  * javax.enterprise/cdi-api: Apache-2.0
  * javax.inject/javax.inject: Apache-2.0
  * joda-time/joda-time: Apache-2.0
  * org.apache.commons/commons-lang3: Apache-2.0
  * org.apache.httpcomponents/httpclient: Apache-2.0
  * org.apache.httpcomponents/httpcore: Apache-2.0
  * org.apache.logging.log4j/log4j-api: Apache-2.0
  * org.apache.logging.log4j/log4j-core: Apache-2.0
  * org.apache.maven/maven-artifact: Apache-2.0
  * org.apache.maven/maven-builder-support: Apache-2.0
  * org.apache.maven/maven-core: Apache-2.0
  * org.apache.maven/maven-model: Apache-2.0
  * org.apache.maven/maven-model-builder: Apache-2.0
  * org.apache.maven/maven-plugin-api: Apache-2.0
  * org.apache.maven/maven-repository-metadata: Apache-2.0
  * org.apache.maven/maven-resolver-provider: Apache-2.0
  * org.apache.maven/maven-settings: Apache-2.0
  * org.apache.maven/maven-settings-builder: Apache-2.0
  * org.apache.maven.resolver/maven-resolver-api: Apache-2.0
  * org.apache.maven.resolver/maven-resolver-connector-basic: Apache-2.0
  * org.apache.maven.resolver/maven-resolver-impl: Apache-2.0
  * org.apache.maven.resolver/maven-resolver-spi: Apache-2.0
  * org.apache.maven.resolver/maven-resolver-transport-file: Apache-2.0
  * org.apache.maven.resolver/maven-resolver-transport-http: Apache-2.0
  * org.apache.maven.resolver/maven-resolver-transport-wagon: Apache-2.0
  * org.apache.maven.resolver/maven-resolver-util: Apache-2.0
  * org.apache.maven.shared/maven-shared-utils: Apache-2.0
  * org.apache.maven.wagon/wagon-http: Apache-2.0
  * org.apache.maven.wagon/wagon-http-shared: Apache-2.0
  * org.apache.maven.wagon/wagon-provider-api: Apache-2.0
  * org.checkerframework/checker-compat-qual: MIT
  * org.clojure/core.async: EPL-1.0
  * org.clojure/core.cache: EPL-1.0
  * org.clojure/core.memoize: EPL-1.0
  * org.clojure/core.specs.alpha: EPL-1.0
  * org.clojure/data.codec: EPL-1.0
  * org.clojure/data.json: EPL-1.0
  * org.clojure/data.priority-map: EPL-1.0
  * org.clojure/java.classpath: EPL-1.0
  * org.clojure/spec.alpha: EPL-1.0
  * org.clojure/tools.analyzer: EPL-1.0
  * org.clojure/tools.analyzer.jvm: EPL-1.0
  * org.clojure/tools.cli: EPL-1.0
  * org.clojure/tools.deps.alpha: EPL-1.0
  * org.clojure/tools.gitlibs: EPL-1.0
  * org.clojure/tools.logging: EPL-1.0
  * org.clojure/tools.namespace: EPL-1.0
  * org.clojure/tools.reader: EPL-1.0
  * org.codehaus.mojo/animal-sniffer-annotations: MIT
  * org.codehaus.plexus/plexus-classworlds: Apache-2.0
  * org.codehaus.plexus/plexus-component-annotations: Apache-2.0
  * org.codehaus.plexus/plexus-interpolation: Apache-2.0
  * org.codehaus.plexus/plexus-utils: Apache-2.0
  * org.eclipse.jetty/jetty-client: EPL-2.0
  * org.eclipse.jetty/jetty-http: EPL-2.0
  * org.eclipse.jetty/jetty-io: EPL-2.0
  * org.eclipse.jetty/jetty-util: EPL-2.0
  * org.eclipse.sisu/org.eclipse.sisu.inject: EPL-1.0
  * org.eclipse.sisu/org.eclipse.sisu.plexus: EPL-1.0
  * org.jsoup/jsoup: MIT
  * org.ow2.asm/asm: BSD-3-Clause
  * org.slf4j/jcl-over-slf4j: Apache-2.0
  * org.slf4j/slf4j-api: MIT
  * org.slf4j/slf4j-nop: MIT
  * org.sonatype.plexus/plexus-cipher: Apache-2.0
  * org.sonatype.plexus/plexus-sec-dispatcher: Apache-2.0
  * org.springframework.build/aws-maven: Apache-2.0
  * org.tcrawley/dynapath: EPL-1.0
  * s3-wagon-private/s3-wagon-private: Apache-2.0
  * slipset/deps-deploy: EPL-1.0
  * software.amazon.ion/ion-java: Apache-2.0
  * tigris/tigris: EPL-1.0
```

### API Documentation

[API documentation is available here](https://pmonks.github.io/tools-licenses/).

## FAQ

[//]: # (Comment: Every Question in this list has two spaces at the end THAT MUST NOT BE REMOVED!!)

**Q.** How comprehensive is the license task?  
**A.** While it makes a pretty good effort to find license information included in the published artifacts for a project's dependencies, and [falls back](https://github.com/pmonks/tools-licenses/blob/data/fallbacks.edn) on manually verified information when necessary, this logic is no substitute for a forensic software license compliance service (such as [WhiteSource](https://www.whitesourcesoftware.com/), [fossa](https://fossa.com/), [SourceAuditor](https://sourceauditor.com/compliance/index.php/legal-and-compliance-professionals/) etc.).  It is, however, substantially cheaper than those services.

**Q.** The license task says "Unable to determine licenses for these dependencies", gives me a list of deps and then asks me to raise an issue [here](https://github.com/pmonks/tools-licenses/issues/new?assignees=pmonks&labels=unknown+licenses&template=Unknown_licenses.md). Why?  
**A.** If an artifact contains no identifiable license information, the logic falls back on a [manually curated list of dependency -> licenses](https://github.com/pmonks/tools-licenses/blob/data/fallbacks.edn).  That message appears when there is no identifiable license information in the artifact AND the dependency has no fallback information either.  By raising a bug including the list of deps(s) that the tool emitted, you give the author an opportunity to manually determine the licenses for those dep(s) and update the fallback list accordingly.

**Q.** When the fallback list is updated, will I need to update my new version of tools-licenses to get it?  
**A.** No - the fallback list is retrieved at runtime, so any updates to it will be picked up soon after they are made by all versions of tools-licenses.

**Q.** Doesn't that mean that tools-licenses requires an internet connection in order to function?  
**A.** Yes indeed.

## Contributor Information

[Contributing Guidelines](https://github.com/pmonks/tools-licenses/blob/main/.github/CONTRIBUTING.md)

[Bug Tracker](https://github.com/pmonks/tools-licenses/issues)

[Code of Conduct](https://github.com/pmonks/tools-licenses/blob/main/.github/CODE_OF_CONDUCT.md)

### Developer Workflow

The repository has two permanent branches: `main` and `dev`.  **All development must occur either in branch `dev`, or (preferably) in feature branches off of `dev`.**  All PRs must also be submitted against `dev`; the `main` branch is **only** updated from `dev` via PRs created by the core development team.  All other changes submitted to `main` will be rejected.

This model allows otherwise unrelated changes to be batched up in the `dev` branch, integration tested there, and then released en masse to the `main` branch, which will trigger automated generation and deployment of the release (Codox docs to github.io, JARs to Clojars, etc.).

## License

Copyright © 2021 Peter Monks

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

SPDX-License-Identifier: [Apache-2.0](https://spdx.org/licenses/Apache-2.0)
