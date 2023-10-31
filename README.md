| | | | |
|---:|:---:|:---:|:---:|
| [**main**](https://github.com/pmonks/tools-licenses/tree/main) | [![CI](https://github.com/pmonks/tools-licenses/workflows/CI/badge.svg?branch=main)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3ACI+branch%3Amain) | [![Dependencies](https://github.com/pmonks/tools-licenses/workflows/dependencies/badge.svg?branch=main)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Adependencies+branch%3Amain) | [![Vulnerabilities](https://github.com/pmonks/lice-comb/workflows/vulnerabilities/badge.svg?branch=main)](https://pmonks.github.io/tools-licenses/nvd/dependency-check-report.html) |
| [**dev**](https://github.com/pmonks/tools-licenses/tree/dev) | [![CI](https://github.com/pmonks/tools-licenses/workflows/CI/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3ACI+branch%3Adev) | [![Dependencies](https://github.com/pmonks/tools-licenses/workflows/dependencies/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Adependencies+branch%3Adev) | [![Vulnerabilities](https://github.com/pmonks/lice-comb/workflows/vulnerabilities/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Avulnerabilities+branch%3Adev) |

[![Latest Version](https://img.shields.io/clojars/v/com.github.pmonks/tools-licenses)](https://clojars.org/com.github.pmonks/tools-licenses/) [![Open Issues](https://img.shields.io/github/issues/pmonks/tools-licenses.svg)](https://github.com/pmonks/tools-licenses/issues) [![License](https://img.shields.io/github/license/pmonks/tools-licenses.svg)](https://github.com/pmonks/tools-licenses/blob/main/LICENSE)


# tools-licenses

A Clojure [tools.build](https://github.com/clojure/tools.build) task library for interrogating your project's dependencies' licenses.  Somewhat inspired by the (discontinued) [`lein-licenses`](https://github.com/technomancy/lein-licenses/) Leiningen plugin, but with the added benefit of canonicalisation to [SPDX](https://spdx.dev/) [License Expressions](https://spdx.github.io/spdx-spec/v2.3/SPDX-license-expressions/).

It also provides the ability to check your (Apache-2.0 licensed) project against the [Apache Software Foundation's 3rd Party License Policy](https://www.apache.org/legal/resolved.html).

## Disclaimer

**The author and contributors to `tools-licenses` are not lawyers, and neither they nor `tools-licenses` itself provide legal advice. This is simply a tool that might help you and your legal counsel perform licensing due diligence on your projects.**

## Tasks

1. `licenses` - attempt to display the licenses used by all transitive dependencies of the project
2. `check-asf-policy` - attempt to check your project's probable compliance (or not) with the ASF's 3rd Party License Policy

## Why not [`tools.deps`' built-in license detection](https://clojure.org/reference/deps_and_cli#_other_programs)?

`tools.deps`' license discovery logic (provided via the command `clj -X:deps list`) has several serious shortcomings, including:

* It only scans pom.xml files for license information, and silently ignores projects that don't have license tags in their pom.xml file, or don't have a pom.xml file at all. This is a problem because:
  * git-only dependencies don't need a pom.xml file (and in practice most don't provide one)
  * [Clojars only recently started mandating license information in the pom.xml files it hosts](https://github.com/clojars/clojars-web/issues/873), and as of mid-2023 around 1/3 of all projects deployed hosted there do not include any licensing information in their pom.xml files
* It's coupled to tools.deps and cannot easily be consumed as an independent library. It's also dependent on tools.deps state management (e.g. requires pom.xml files to be downloaded locally).
* It doesn't canonicalise license information to SPDX License Expressions (it leaves canonicalisation, a fairly difficult problem, to the caller).

In contrast, `tools-licenses` leverages the [`lice-comb` library](https://github.com/pmonks/lice-comb), which takes a more comprehensive approach to license detection.

## Usage

### Documentation

[API documentation is available here](https://pmonks.github.io/tools-licenses/), or [here on cljdoc](https://cljdoc.org/d/com.github.pmonks/tools-licenses/).

[FAQ is available here](https://github.com/pmonks/tools-licenses/wiki/FAQ).

### Adding the tasks to your tools.build script

Add the tool as a Maven dependency to your `deps.edn`, in your build alias:

```edn
  :aliases
    :build
      {:deps {com.github.pmonks/tools-licenses {:mvn/version "LATEST_CLOJARS_VERSION"}}  ; Or use "RELEASE" to blindly follow the latest release of the tool
       :ns-default your.build.ns}
```

Require the namespace in your tools.build script (typically called `build.clj`), and add task functions that delegate to the tool:

```clojure
(ns your.build.ns
  (:require [tools-licenses.tasks :as lic]))

(defn licenses
  "Attempts to list all licenses for the transitive set of dependencies of the
  project, as SPDX license expressions."
  [opts]
  (lic/licenses opts))

(defn check-asf-policy
  "Checks this project's dependencies' licenses against the ASF's 3rd party
  license policy (https://www.apache.org/legal/resolved.html).

  Note: only meaningful if this project is Apache-2.0 licensed."
  [opts]
  (lic/check-asf-policy opts))
```

### Using the tasks from the command line

#### `licenses` task

Example summary output:

```
$ clj -T:build licenses

This project: Apache-2.0

License Expression                                           # of Deps
------------------------------------------------------------ ---------
Apache-2.0                                                          61
BSD-3-Clause                                                         1
CDDL-1.1                                                             1
EPL-1.0                                                             25
EPL-2.0                                                              5
GPL-2.0-only WITH Classpath-exception-2.0                            1
Public domain                                                        1
MIT                                                                  7
No licenses found                                                    1
```

If you see `Unlisted (<some text>)` licenses in the output, **[please raise an issue here](https://github.com/pmonks/lice-comb/issues/new?assignees=pmonks&labels=unknown+licenses&template=Unknown_licenses.md)**.

Other invocation possibilities:
* `clj -T:build licenses :output :summary` - the default (see above)
* `clj -T:build licenses :output :detailed` - detailed per-dependency license information
* `clj -T:build licenses :output :edn` - detailed per-dependency license information in EDN format
* `clj -T:build licenses :output :explain :dep <dep symbol>` - an explanation of how the tool arrived at the given license(s) for a single dep (expressed as a tools.dep symbol). For example:

```
$ clj -T:build licenses :output :explain :dep org.clojure/clojure
Dependency: org.clojure/clojure
Licenses: EPL-1.0

EPL-1.0 Concluded
    Confidence: high
    Strategy: SPDX listed name (case insensitive match)
    Source:
      org.clojure/clojure@1.11.1
      /Users/pmonks/.m2/repository/org/clojure/clojure/1.11.1/clojure-1.11.1.pom
      <licenses><license><name>
      Eclipse Public License 1.0
```

#### `check-asf-policy` task

Example summary output:

```
$ clj -T:build check-asf-policy

ASF Category                  # of Deps
----------------------------- ---------
Category A                           69
Category A (with caveats)             1
Category B                           27
Creative Commons Licenses             0
Category X                            0
Uncategorised                         1
```

Other invocation possibilities:
* `clj -T:build check-asf-policy :output :summary` - the default (see above)
* `clj -T:build check-asf-policy :output :detailed` - detailed per-dependency ASF category information
* `clj -T:build check-asf-policy :output :edn` - detailed per-dependency ASF category information in EDN format

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
