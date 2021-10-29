| | | |
|---:|:---:|:---:|
| [**main**](https://github.com/pmonks/tools-licenses/tree/main) | [![Lint](https://github.com/pmonks/tools-licenses/workflows/lint/badge.svg?branch=main)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Alint) | [![Dependencies](https://github.com/pmonks/tools-licenses/workflows/dependencies/badge.svg?branch=main)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Adependencies) |
| [**dev**](https://github.com/pmonks/tools-licenses/tree/dev)  | [![Lint](https://github.com/pmonks/tools-licenses/workflows/lint/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Alint) | [![Dependencies](https://github.com/pmonks/tools-licenses/workflows/dependencies/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Adependencies) |

[![Latest Version](https://img.shields.io/clojars/v/com.github.pmonks/tools-licenses)](https://clojars.org/com.github.pmonks/tools-licenses/) [![Open Issues](https://img.shields.io/github/issues/pmonks/tools-licenses.svg)](https://github.com/pmonks/tools-licenses/issues) [![License](https://img.shields.io/github/license/pmonks/tools-licenses.svg)](https://github.com/pmonks/tools-licenses/blob/main/LICENSE)


# tools-licenses

A Clojure [tools.build](https://github.com/clojure/tools.build) task library related to dependency licenses.  Somewhat inspired by the (discontinued) [`lein-licenses`](https://github.com/technomancy/lein-licenses/) Leiningen plugin, but with the added benefit of license canonicalisation (leveraging the *excellent* [Software Package Data Exchange (SPDX)](https://spdx.dev/) standard), and with the ability to check your project against the [Apache Software Foundation's 3rd Party License Policy](https://www.apache.org/legal/resolved.html).

## Tasks

1. `licenses` - attempt to display the licenses used by all transitive dependencies of the project
2. `check-asf-policy` - attempt to check your project's compliance with the ASF's 3rd Party License Policy

## Using the library

### Documentation

[API documentation is available here](https://pmonks.github.io/tools-licenses/).

[FAQ is available here](https://github.com/pmonks/tools-licenses/wiki/FAQ).

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

### Add one or more of the build tasks to your build

```clojure
(defn licenses
  "Attempts to list all licenses for the transitive set of dependencies of the project, using SPDX license expressions."
  [opts]
  (-> opts
      (set-opts)
      (lic/licenses)))

(defn check-asf-policy
  "Checks this project's dependencies' licenses against the ASF's 3rd party license policy (https://www.apache.org/legal/resolved.html)."
  [opts]
  (-> opts
      (set-opts)
      (lic/check-asf-policy)))
```

### Use the build tasks

#### `licenses` task

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

#### `check-asf-policy` task

Example summary output:

```
$ clj -T:build check-asf-policy
Category                       Number of Deps
------------------------------ --------------
Category A                     72
Category A (with caveats)      1
Category B                     36
Creative Commons Licenses      0
Category X                     0
Non-OSI Approved Licenses      0
Uncategorised                  0

For more information, please see https://github.com/pmonks/tools-licenses/wiki/FAQ
```

Example detailed output:

```
clj -T:build check-asf-policy :output :detailed
Category A:
  * cheshire/cheshire
  * com.amazonaws/aws-java-sdk-core
  * com.amazonaws/aws-java-sdk-kms
  * com.amazonaws/aws-java-sdk-s3
  * com.amazonaws/aws-java-sdk-sts
  * com.amazonaws/jmespath-java
  * com.cognitect/http-client
  * com.cognitect.aws/api
  * com.cognitect.aws/endpoints
  * com.cognitect.aws/s3
  * com.fasterxml.jackson.core/jackson-annotations
  * com.fasterxml.jackson.core/jackson-core
  * com.fasterxml.jackson.core/jackson-databind
  * com.fasterxml.jackson.dataformat/jackson-dataformat-cbor
  * com.fasterxml.jackson.dataformat/jackson-dataformat-smile
  * com.github.pmonks/tools-convenience
  * com.google.code.findbugs/jsr305
  * com.google.errorprone/error_prone_annotations
  * com.google.guava/guava
  * com.google.inject/guice$no_aop
  * com.google.j2objc/j2objc-annotations
  * commons-codec/commons-codec
  * commons-io/commons-io
  * commons-logging/commons-logging
  * io.github.seancorfield/build-clj
  * io.github.seancorfield/build-uber-log4j2-handler
  * javax.enterprise/cdi-api
  * javax.inject/javax.inject
  * joda-time/joda-time
  * org.apache.commons/commons-lang3
  * org.apache.httpcomponents/httpclient
  * org.apache.httpcomponents/httpcore
  * org.apache.logging.log4j/log4j-api
  * org.apache.logging.log4j/log4j-core
  * org.apache.maven/maven-artifact
  * org.apache.maven/maven-builder-support
  * org.apache.maven/maven-core
  * org.apache.maven/maven-model
  * org.apache.maven/maven-model-builder
  * org.apache.maven/maven-plugin-api
  * org.apache.maven/maven-repository-metadata
  * org.apache.maven/maven-resolver-provider
  * org.apache.maven/maven-settings
  * org.apache.maven/maven-settings-builder
  * org.apache.maven.resolver/maven-resolver-api
  * org.apache.maven.resolver/maven-resolver-connector-basic
  * org.apache.maven.resolver/maven-resolver-impl
  * org.apache.maven.resolver/maven-resolver-spi
  * org.apache.maven.resolver/maven-resolver-transport-file
  * org.apache.maven.resolver/maven-resolver-transport-http
  * org.apache.maven.resolver/maven-resolver-transport-wagon
  * org.apache.maven.resolver/maven-resolver-util
  * org.apache.maven.shared/maven-shared-utils
  * org.apache.maven.wagon/wagon-http
  * org.apache.maven.wagon/wagon-http-shared
  * org.apache.maven.wagon/wagon-provider-api
  * org.checkerframework/checker-compat-qual
  * org.codehaus.mojo/animal-sniffer-annotations
  * org.codehaus.plexus/plexus-classworlds
  * org.codehaus.plexus/plexus-component-annotations
  * org.codehaus.plexus/plexus-interpolation
  * org.codehaus.plexus/plexus-utils
  * org.jsoup/jsoup
  * org.ow2.asm/asm
  * org.slf4j/jcl-over-slf4j
  * org.slf4j/slf4j-api
  * org.slf4j/slf4j-nop
  * org.sonatype.plexus/plexus-cipher
  * org.sonatype.plexus/plexus-sec-dispatcher
  * org.springframework.build/aws-maven
  * s3-wagon-private/s3-wagon-private
  * software.amazon.ion/ion-java

Category A (with caveats):
  * aopalliance/aopalliance

Category B:
  * camel-snake-kebab/camel-snake-kebab
  * ch.qos.logback/logback-classic
  * ch.qos.logback/logback-core
  * clj-commons/pomegranate
  * clj-xml-validation/clj-xml-validation
  * io.github.clojure/tools.build
  * javax.annotation/jsr250-api
  * org.clojure/clojure
  * org.clojure/core.async
  * org.clojure/core.cache
  * org.clojure/core.memoize
  * org.clojure/core.specs.alpha
  * org.clojure/data.codec
  * org.clojure/data.json
  * org.clojure/data.priority-map
  * org.clojure/data.xml
  * org.clojure/java.classpath
  * org.clojure/spec.alpha
  * org.clojure/tools.analyzer
  * org.clojure/tools.analyzer.jvm
  * org.clojure/tools.cli
  * org.clojure/tools.deps.alpha
  * org.clojure/tools.gitlibs
  * org.clojure/tools.logging
  * org.clojure/tools.namespace
  * org.clojure/tools.reader
  * org.eclipse.jetty/jetty-client
  * org.eclipse.jetty/jetty-http
  * org.eclipse.jetty/jetty-io
  * org.eclipse.jetty/jetty-util
  * org.eclipse.sisu/org.eclipse.sisu.inject
  * org.eclipse.sisu/org.eclipse.sisu.plexus
  * org.tcrawley/dynapath
  * slipset/deps-deploy
  * tigris/tigris
  * tolitius/xml-in

For more information, please see https://github.com/pmonks/tools-licenses/wiki/FAQ
```

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
