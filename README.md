| | | |
|---:|:---:|:---:|
| [**main**](https://github.com/pmonks/tools-licenses/tree/main) | [![CI](https://github.com/pmonks/tools-licenses/workflows/CI/badge.svg?branch=main)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3ACI+branch%3Amain) | [![Dependencies](https://github.com/pmonks/tools-licenses/workflows/dependencies/badge.svg?branch=main)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Adependencies+branch%3Amain) |
| [**dev**](https://github.com/pmonks/tools-licenses/tree/dev) | [![CI](https://github.com/pmonks/tools-licenses/workflows/CI/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3ACI+branch%3Adev) | [![Dependencies](https://github.com/pmonks/tools-licenses/workflows/dependencies/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Adependencies+branch%3Adev) |

[![Latest Version](https://img.shields.io/clojars/v/com.github.pmonks/tools-licenses)](https://clojars.org/com.github.pmonks/tools-licenses/) [![Open Issues](https://img.shields.io/github/issues/pmonks/tools-licenses.svg)](https://github.com/pmonks/tools-licenses/issues) [![License](https://img.shields.io/github/license/pmonks/tools-licenses.svg)](https://github.com/pmonks/tools-licenses/blob/main/LICENSE)


# tools-licenses

A Clojure [tools.build](https://github.com/clojure/tools.build) task library related to dependency licenses.  Somewhat inspired by the (discontinued) [`lein-licenses`](https://github.com/technomancy/lein-licenses/) Leiningen plugin, but with the added benefit of license canonicalisation (leveraging the *excellent* [Software Package Data Exchange (SPDX)](https://spdx.dev/) standard), and with the ability to check your project against the [Apache Software Foundation's 3rd Party License Policy](https://www.apache.org/legal/resolved.html).

## Tasks

1. `licenses` - attempt to display the licenses used by all transitive dependencies of the project
2. `check-asf-policy` - attempt to check your project's compliance with the ASF's 3rd Party License Policy

## Using the library

### Documentation

[API documentation is available here](https://pmonks.github.io/tools-licenses/), though since the refactoring out of the [license detection](https://github.com/pmonks/lice-comb) and [ASF policy validation](https://github.com/pmonks/asf-cat) code, that's not very interesting or useful any longer.

[FAQ is available here](https://github.com/pmonks/tools-licenses/wiki/FAQ).

### Dependency

Express the correct maven dependencies in your `deps.edn`, for a build tool alias:

```edn
  :aliases
    :build
      {:deps       {com.github.pmonks/tools-licenses {:mvn/version "LATEST_CLOJARS_VERSION"}
                    io.github.seancorfield/build-clj {:git/tag "v0.6.7" :git/sha "22c2d09"}}
       :ns-default your.build.ns}
```

Note that you must express an explicit dependency on `io.github.seancorfield/build-clj`, as that project [doesn't publish artifacts to Clojars yet](https://github.com/seancorfield/build-clj/issues/11), and transitive git coordinate dependencies are not supported by tools.deps.

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
This project: Apache-2.0

License                                  Number of Deps
---------------------------------------- --------------
Apache-2.0                               72
BSD-3-Clause                             1
CDDL-1.0                                 1
EPL-1.0                                  35
GPL-2.0-with-classpath-exception         2
LGPL-2.1                                 2
MIT                                      6
NON-SPDX-Public-Domain                   1
```

Use `clj -T:build licenses :output :detailed` to get detailed, per-dependency output (too long to reasonably include here).

If you see `NON-SPDX-Unknown` license identifiers, and/or the task displays a list of dependencies with unknown licenses, **[please raise an issue here](https://github.com/pmonks/lice-comb/issues/new?assignees=pmonks&labels=unknown+licenses&template=Unknown_licenses_tools.md)**.

#### `check-asf-policy` task

Example summary output:

```
$ clj -T:build check-asf-policy
Category                       Number of Deps
------------------------------ --------------
Category A                     79
Category A (with caveats)      1
Category B                     32
Creative Commons Licenses      0
Category X                     0
Uncategorised                  0

For more information, please see https://github.com/pmonks/tools-licenses/wiki/FAQ
```

Use `clj -T:build check-asf-policy :output :detailed` to get detailed, per-dependency output (too long to reasonably include here).

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
