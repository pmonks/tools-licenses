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

### Add a `license` build task to your build

```clojure
(defn licenses
  "Attempts to list all licenses for the transitive set of dependencies of the project, using SPDX license expressions."
  [opts]
  (-> opts
      (set-opts)
      (lic/licenses)))
```

### API Documentation

[API documentation is available here](https://pmonks.github.io/tools-licenses/).

## FAQ

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
