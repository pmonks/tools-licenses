| | | | |
|---:|:---:|:---:|:---:|
| [**main**](https://github.com/pmonks/tools-licenses/tree/main) | [![CI](https://github.com/pmonks/tools-licenses/workflows/CI/badge.svg?branch=main)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3ACI+branch%3Amain) | [![Dependencies](https://github.com/pmonks/tools-licenses/workflows/dependencies/badge.svg?branch=main)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Adependencies+branch%3Amain) | [![Vulnerabilities](https://github.com/pmonks/lice-comb/workflows/vulnerabilities/badge.svg?branch=main)](https://pmonks.github.io/tools-licenses/nvd/dependency-check-report.html) |
| [**dev**](https://github.com/pmonks/tools-licenses/tree/dev) | [![CI](https://github.com/pmonks/tools-licenses/workflows/CI/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3ACI+branch%3Adev) | [![Dependencies](https://github.com/pmonks/tools-licenses/workflows/dependencies/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Adependencies+branch%3Adev) | [![Vulnerabilities](https://github.com/pmonks/lice-comb/workflows/vulnerabilities/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Avulnerabilities+branch%3Adev) |

[![Latest Version](https://img.shields.io/clojars/v/com.github.pmonks/tools-licenses)](https://clojars.org/com.github.pmonks/tools-licenses/) [![Open Issues](https://img.shields.io/github/issues/pmonks/tools-licenses.svg)](https://github.com/pmonks/tools-licenses/issues) [![License](https://img.shields.io/github/license/pmonks/tools-licenses.svg)](https://github.com/pmonks/tools-licenses/blob/main/LICENSE)


# tools-licenses

A Clojure [tools.build](https://github.com/clojure/tools.build) task library related to dependency licenses.  Somewhat inspired by the (discontinued) [`lein-licenses`](https://github.com/technomancy/lein-licenses/) Leiningen plugin, but with the added benefit of license canonicalisation (leveraging the *excellent* [Software Package Data Exchange (SPDX)](https://spdx.dev/) standard), and with the ability to check your project against the [Apache Software Foundation's 3rd Party License Policy](https://www.apache.org/legal/resolved.html).

## Tasks

1. `licenses` - attempt to display the licenses used by all transitive dependencies of the project
2. `check-asf-policy` - attempt to check your project's compliance with the ASF's 3rd Party License Policy

## Why not [`tools.deps`' built-in license detection](https://clojure.org/reference/deps_and_cli#_other_programs)?

That functionality:
1. only looks for license information declared in `pom.xml` files (which may not exist for git dependencies)
2. [makes only rudimentary efforts to normalise licenses into a canonical form](https://github.com/clojure/tools.deps/blob/master/src/main/resources/clojure/tools/deps/license-abbrev.edn) (i.e. SPDX license identifiers)
3. doesn't make use of SPDX license expressions

In contrast, `tools-licenses` leverages the [`lice-comb` library](https://github.com/pmonks/lice-comb), which takes a more comprehensive approach to license detection.

## Using the library

### Documentation

[API documentation is available here](https://pmonks.github.io/tools-licenses/), or [here on cljdoc](https://cljdoc.org/d/com.github.pmonks/tools-licenses/).

[FAQ is available here](https://github.com/pmonks/tools-licenses/wiki/FAQ).

### Dependency

Express the correct maven dependencies in your `deps.edn`, for a build tool alias:

```edn
  :aliases
    :build
      {:deps {com.github.pmonks/tools-licenses {:mvn/version "LATEST_CLOJARS_VERSION"}}
       :ns-default your.build.ns}
```

### Require the namespace

```clojure
(ns your.build.ns
  (:require [tools-licenses.tasks :as lic]))
```

### Add one or more of the build tasks to your build

```clojure
(defn licenses
  "Attempts to list all licenses for the transitive set of dependencies of the project, as SPDX license expressions."
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

This project uses the [git-flow branching strategy](https://nvie.com/posts/a-successful-git-branching-model/), with the caveat that the permanent branches are called `main` and `dev`, and any changes to the `main` branch are considered a release and auto-deployed (JARs to Clojars, API docs to GitHub Pages, etc.).

For this reason, **all development must occur either in branch `dev`, or (preferably) in temporary branches off of `dev`.**  All PRs from forked repos must also be submitted against `dev`; the `main` branch is **only** updated from `dev` via PRs created by the core development team.  All other changes submitted to `main` will be rejected.

### Build Tasks

`tools-licenses` uses [`tools.build`](https://clojure.org/guides/tools_build). You can get a list of available tasks by running:

```
clojure -A:deps -T:build help/doc
```

Of particular interest are:

* `clojure -T:build test` - run the unit tests
* `clojure -T:build lint` - run the linters (clj-kondo and eastwood)
* `clojure -T:build ci` - run the full CI suite (check for outdated dependencies, run the unit tests, run the linters)
* `clojure -T:build install` - build the JAR and install it locally (e.g. so you can test it with downstream code)

Please note that the `deploy` task is restricted to the core development team (and will not function if you run it yourself).

## License

Copyright © 2021 Peter Monks

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

SPDX-License-Identifier: [Apache-2.0](https://spdx.org/licenses/Apache-2.0)
