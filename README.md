| | | |
|---:|:---:|:---:|
| [**main**](https://github.com/pmonks/tools-licenses/tree/main) | [![Lint](https://github.com/pmonks/tools-licenses/workflows/lint/badge.svg?branch=main)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Alint) | [![Dependencies](https://github.com/pmonks/tools-licenses/workflows/dependencies/badge.svg?branch=main)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Adependencies) |
| [**dev**](https://github.com/pmonks/tools-licenses/tree/dev)  | [![Lint](https://github.com/pmonks/tools-licenses/workflows/lint/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Alint) | [![Dependencies](https://github.com/pmonks/tools-licenses/workflows/dependencies/badge.svg?branch=dev)](https://github.com/pmonks/tools-licenses/actions?query=workflow%3Adependencies) |

[![Latest Version](https://img.shields.io/clojars/v/com.github.pmonks/tools-licenses)](https://clojars.org/com.github.pmonks/tools-licenses/) [![Open Issues](https://img.shields.io/github/issues/pmonks/tools-licenses.svg)](https://github.com/pmonks/tools-licenses/issues) [![License](https://img.shields.io/github/license/pmonks/tools-licenses.svg)](https://github.com/pmonks/tools-licenses/blob/main/LICENSE)


# tools-licenses

A Clojure [tools.build](https://github.com/clojure/tools.build) task library related to dependency licenses.

## Tasks

1. `licenses` - attempt to display the licenses used by all transitive dependencies of the project

## Using the library

### Dependency

Express a maven dependency in your `deps.edn`, for a build tool alias:

```edn
  :aliases
    :build
      {:deps       {com.github.pmonks/tools-licenses {:mvn/version "LATEST_CLOJARS_VERSION"}}
       :ns-default your.build.ns}
```

### Require the namespace

```clojure
(ns your.build.ns
  (:require [tools-licenses.tasks :as lic]))
```

### Add a `license` build task to your build

```clojure
(defn licenses
  "Construct a comprehensive pom.xml file for this project"
  [opts]
  (-> opts
      (set-opts)
      (lic/licenses)))
```

### API Documentation

[API documentation is available here](https://pmonks.github.io/tools-licenses/).

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
