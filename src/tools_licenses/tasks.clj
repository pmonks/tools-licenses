;
; Copyright © 2021 Peter Monks
;
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;     http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;
; SPDX-License-Identifier: Apache-2.0
;

(ns tools-licenses.tasks
  "Clojure tools.build tasks related to dependency licenses.

  All of the build tasks return the opts hash map they were passed
  (unlike some of the functions in clojure.tools.build.api)."
  (:require [clojure.string          :as s]
            [clojure.pprint          :as pp]
            [clojure.java.io         :as io]
            [clojure.tools.deps      :as d]
            [clojure.tools.build.api :as b]
            [jansi-clj.core          :as ansi]
            [wcwidth.api             :as wcw]
            [spdx.expressions        :as sexp]
            [lice-comb.matching      :as lcm]
            [lice-comb.deps          :as lcd]
            [lice-comb.files         :as lcf]
            [lice-comb.utils         :as lcu]
            [asf-cat.api             :as asf]))

(ansi/install!)

(defn- prep-project
  "Prepares the project and returns the lib-map for it."
  []
  (let [basis   (b/create-basis {})
        lib-map (d/resolve-deps basis {})
        _       (d/prep-libs! lib-map {:action :prep :log :info} {})]  ; Make sure everything is "prepped" (downloaded locally) before we start looking for licenses
    lib-map))

(defn- expression-minus-license-refs
  "Converts lice-comb specific LicenseRefs to a human readable name (and colours
  them grey), but leaves other expressions unchanged."
  [exp]
  (if (lcm/lice-comb-license-ref? exp)
    (ansi/fg-bright :black (lcm/id->name exp))  ; "Bright black" = dark grey
    exp))

(defn- get-version
  [dep-info]
  (case (:deps/manifest dep-info)
    :mvn  (:mvn/version dep-info)
    :deps (str (when (:git/tag dep-info) (str (:git/tag dep-info) "@"))
               (:git/sha dep-info))))

(defn- dep-and-license-expressions
  [dep-name license-expressions]
  (let [sorted-license-expressions (seq (sort (if (map? license-expressions) (keys license-expressions) license-expressions)))]
    (str dep-name " [" (if sorted-license-expressions (s/join ", " (map expression-minus-license-refs sorted-license-expressions)) (ansi/fg-bright :red "No licenses found")) "]")))

(defn- dep-and-licenses->string
  [[dep-ga dep-info]]
  (let [dep-ga              (str dep-ga)
        dep-v               (get-version dep-info)
        license-expressions (:lice-comb/license-info dep-info)]
    (dep-and-license-expressions (str dep-ga "@" dep-v) license-expressions)))

(defn- fit-width
  "Pads or trims string s to display width w, with control over whether padding
  happens before or after"
  ([w s] (fit-width w s true))
  ([w s pad-after?]
   (when (and w s)
     (let [sw (wcw/display-width s)]
       (case (compare w sw)
         ; s is too long - trim
         -1 (fit-width w (subs s 0 (- (count s) (- sw w))))   ; Note: we recursively call fit-width here to ensure that display-width is recalculated each time - trimming non-printing chars doesn't change the display width

         ; s is perfect
         0 s

         ; s is too short - pad
         1 (let [padding (s/join (repeat (- w sw) \space))]
             (if pad-after?
               (str s padding)
               (str padding s))))))))

(defn- summary-output!
  "Emit summary output to stdout."
  [proj-expressions-info deps-lib-map-with-info]
  (let [proj-expressions (sort (keys proj-expressions-info))
        freqs            (frequencies (filter identity (mapcat #(keys (get % :lice-comb/license-info)) (vals deps-lib-map-with-info))))
        deps-expressions (seq (sort (keys freqs)))
        no-license-count (count (filter empty? (map #(:lice-comb/license-info (val %)) deps-lib-map-with-info)))]
    (print (str "\n" (ansi/bold "This project: ")))
    (if (seq proj-expressions)
      (println (s/join ", " proj-expressions))
      (println "- no license information found -"))
    (println (ansi/bold "\nLicense Expression                                           # of Deps"
                        "\n------------------------------------------------------------ ---------"))
    (if (or deps-expressions (pos? no-license-count))
      (do
        (run! #(println (str (fit-width 60 (expression-minus-license-refs %)) " " (fit-width 9 (str (get freqs %)) false))) deps-expressions)
        (when (pos? no-license-count) (println (str (fit-width 60 (ansi/fg-bright :red "No licenses found")) " " (fit-width 9 no-license-count false)))))
      (println "  - no dependencies found -"))
    (println)))

(defn- detailed-output!
  "Emit detailed output to stdout."
  [opts proj-expressions-info deps-lib-map-with-info]
  (let [expressions     (sort (keys proj-expressions-info))
        direct-deps     (into {} (remove (fn [[_ v]] (seq (:dependents v))) deps-lib-map-with-info))
        transitive-deps (into {} (filter (fn [[_ v]] (seq (:dependents v))) deps-lib-map-with-info))]
    (println (str "\n" (ansi/bold "This project:")))
    (println (dep-and-license-expressions (str (:lib opts) "@" (:version opts)) expressions))
    (println (ansi/bold "\nDirect dependencies:"))
    (if direct-deps
      (doall (for [[k v] (sort-by key direct-deps)] (println (dep-and-licenses->string [k v]))))
      (println "- no direct dependencies -"))
    (println (ansi/bold "\nTransitive dependencies:"))
    (if transitive-deps
      (doall (for [[k v] (sort-by key transitive-deps)] (println (dep-and-licenses->string [k v]))))
      (println "- no transitive dependencies -"))
    (println)))

(defn- remove-file-prefix
  [s]
  (when s
    (if (s/starts-with? s "file:")
      (subs s (count "file:"))
      s)))

(defn- expression-info->string
  [m expr]
  (when (and m expr)
    (str (ansi/bold (expression-minus-license-refs expr)) " "
      (when-let [info-list (sort-by lcu/expression-info-sort-by-keyfn (seq (get m expr)))]
        (s/join "\n" (map #(str (when-let [md-id (:id %)] (when (not= expr md-id) (ansi/bold (str "  " (expression-minus-license-refs md-id) " "))))
                                (case (:type %)
                                  :declared  (ansi/fg-bright :green  "Declared")
                                  :concluded (ansi/fg-bright :yellow "Concluded"))
                                (when-let [confidence (:confidence %)]   (str (ansi/bold "\n    Confidence: ")
                                                                              (case confidence
                                                                                :low    (ansi/fg-bright :red    "low")
                                                                                :medium (ansi/fg-bright :yellow "medium")
                                                                                :high   (ansi/fg-bright :green  "high"))))
                                (when-let [strategy   (:strategy %)]     (str (ansi/bold "\n    Strategy: ") (get lcu/strategy->string strategy (name strategy))))
                                (when-let [source     (seq (map remove-file-prefix (:source %)))] (str (ansi/bold "\n    Source:") "\n      " (s/join "\n      " source))))
                          info-list))))))

(defn- explain-with-licenses!
  "Emit "
  [[_ dep-info]]
  (let [dep-expr-info (get dep-info :lice-comb/license-info)
        exprs         (sort (keys dep-expr-info))]
    (println (ansi/bold "Licenses:") (s/join ", " (map expression-minus-license-refs exprs)) "\n")
    (println (s/join "\n\n" (map (partial expression-info->string dep-expr-info) exprs)) "\n")))

(defn- explain-without-licenses
  [[_ dep-info :as dep]]
  (println (ansi/bold "Licenses:") (ansi/fg-bright :red "No licenses found"))
  (println (ansi/bold "Locations checked:"))
  (case (:deps/manifest dep-info)
    :mvn  (do (println (remove-file-prefix (str (lcd/dep->pom-uri dep))))
              (println (s/join "\n" (:paths dep-info))))
    :deps (do (print   (str (:deps/root dep-info) ":\n  "))
              (println (s/join "\n  " (lcf/probable-license-files (:deps/root dep-info)))))))

(defn- explain-output!
  "Emit explain output to stdout."
  [[dep-ga dep-info :as dep]]
  (if dep-ga
    (if dep-info
      (let [dep-expr-info (get dep-info :lice-comb/license-info)]
        (println (str "\n" (ansi/bold "Dependency: ") (str dep-ga "@" (get-version dep-info))))
        (if (empty? dep-expr-info)
          (explain-without-licenses dep)
          (explain-with-licenses! dep)))
      (println (str "No dependency info for " dep-ga ". Are you sure it's a dependency of this project?")))
    (println "No dependency provided - please provide one via the :dep option.")))

(defn- edn-output!
  "Emit EDN output to stdout."
  [opts proj-expressions-info deps-lib-map-with-info]
  (pp/pprint (into {(:lib opts) {:this-project true :lice-comb/license-info proj-expressions-info :paths [(.getCanonicalPath (io/file "."))]}}
                   deps-lib-map-with-info)))

(defn licenses
  "Lists all licenses used transitively by the project.

  :lib     -- req: a symbol representing the name of your library / project
                   (e.g. com.github.yourusername/yourproject)
  :output  -- opt: output format, one of :summary, :detailed, :explain, :edn
                   (defaults to :summary)
  :dep     -- opt: a symbol representing the dependency's license to explain
                   Note: required when :output is :explain (and ignored for
                   other outputs)

  Note: has the side effect of 'prepping' your project with its transitive
  dependencies (i.e. downloading them if they haven't already been downloaded)."
  [opts]
  (let [lib-map     (prep-project)
        output-type (get opts :output :summary)]
    (if (= :explain output-type)
      ; Handle :output :explain separately, as it only needs license info for a single dependency, not all of them
      (let [dep-ga        (get opts :dep)
            dep-info      (get lib-map dep-ga)
            dep           [dep-ga dep-info]
            dep-expr-info (lcd/dep->expressions-info dep)
            dep           [dep-ga (assoc dep-info :lice-comb/license-info dep-expr-info)]]
        (explain-output! dep))
      ; Other :output variants need all info for all dependencies
      (let [proj-expressions-info  (lcf/dir->expressions-info ".")
            deps-lib-map-with-info (lcd/deps-expressions lib-map)]
        (case output-type
          :summary  (summary-output! proj-expressions-info deps-lib-map-with-info)
          :detailed (detailed-output! opts proj-expressions-info deps-lib-map-with-info)
          :edn      (edn-output! opts proj-expressions-info deps-lib-map-with-info)))))
  opts)

(defn- asf-category->ansi-string
  "Converts a category keyword into an ANSI-enhanced human-readable String."
  [category]
  (when category
    (let [category-name (:name (get asf/category-info category))]
      (case category
        :category-a         (ansi/fg-bright :green  category-name)
        :category-a-special (ansi/fg        :green  category-name)
        :category-b         (ansi/fg        :green  category-name)
        :creative-commons   (ansi/fg-bright :yellow category-name)
        :category-x         (ansi/fg-bright :red    category-name)
        :uncategorised      (ansi/fg-bright :red    category-name)))))

(defn check-asf-policy
  "Checks your project's dependencies against the ASF's 3rd party license policy
  (https://www.apache.org/legal/resolved.html).

  :output  -- opt: output format, one of :summary, :detailed, :edn (defaults to
                   :summary)

  Note: has the side effect of 'prepping' your project with its transitive
  dependencies (i.e. downloading them if they haven't already been downloaded)."
  [opts]
  (let [lib-map                   (prep-project)
        proj-licenses             (distinct (mapcat #(sexp/extract-ids (sexp/parse %)) (lcf/dir->expressions ".")))
        lib-map-with-license-info (lcd/deps-expressions lib-map)
        dep-licenses-by-category  (group-by #(let [expressions (seq (keys (:lice-comb/license-info (val %))))]
                                               (if expressions
                                                 (asf/expressions-least-category expressions)
                                                 :uncategorised))  ; Don't forget about deps without any license info
                                            lib-map-with-license-info)]
    (when-not (seq (filter (partial = "Apache-2.0") proj-licenses))
      (println (ansi/bold (ansi/fg-bright :red "Your project is not Apache-2.0 licensed; this report is likely meaningless.\n"))))
    (case (get opts :output :summary)
      :summary  (do
                  (println (ansi/bold "\nASF Category                  # of Deps"
                                      "\n----------------------------- ---------"))
                  (run! (fn [category]
                          (println (str (fit-width 30 (asf-category->ansi-string category))
                                        (fit-width 9  (count (get dep-licenses-by-category category)) false))))
                        asf/categories)
                  (println))
      :detailed (do
                  (println)
                  (run! (fn [category]
                          (when-let [deps-in-category (seq (sort (map first (get dep-licenses-by-category category))))]
                            (run! #(println (str % "@" (get-version (get lib-map %)) " [" (asf-category->ansi-string category) "]")) deps-in-category)
                            (println)))
                        asf/categories))
      :edn      (pp/pprint dep-licenses-by-category))))
