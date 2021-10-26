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

(ns tools-licenses.spdx
  "SPDX license information handling logic."
  (:require [clojure.string       :as s]
            [clojure.set          :as set]
            [clojure.reflect      :as cr]
            [cheshire.core        :as json]
            [tools-licenses.utils :as u]))

(def ^:private spdx-license-list-uri "https://cdn.jsdelivr.net/gh/spdx/license-list-data/json/licenses.json")
(def ^:private spdx-license-list     (try
                                       (json/parse-string (slurp spdx-license-list-uri) u/clojurise-json-key)
                                       (catch Exception e
                                         (throw (ex-info (str "Unexpected " (cr/typename (type e)) " while reading " spdx-license-list-uri ". Please check your internet connection and try again.") {})))))

; Alternative indexes into the SPDX list
(def ^:private spdx-name-to-id       (apply merge (map #(hash-map (s/lower-case (:name %)) (:license-id %)) (:licenses spdx-license-list))))
(def ^:private spdx-url-to-id        (into {} (for [lic (:licenses spdx-license-list) url (:see-also lic)] (hash-map (s/lower-case url) (:license-id lic)))))


(def license-list-version
  "The version of the license list in use."
  (:license-list-version spdx-license-list))

(def ids
  "All SPDX license identifiers in the list."
  (map :license-id (:licenses spdx-license-list)))

(defn license-name->spdx-id
  "Returns the SPDX license identifier equivalent of the given license name, or nil if unable to do so."
  [name]
  (when name
    (get spdx-name-to-id (s/lower-case name))))

(defn license-url->spdx-id
  "Returns the SPDX license identifier equivalent for the given URL, or nil if unable to do so."
  [url]
  (when url
    (let [lcase-url (s/lower-case (str url))
          url-match (first (filter (partial s/starts-with? lcase-url) (keys spdx-url-to-id)))]
      (get spdx-url-to-id url-match))))

(def spdx-id->license-name
  "Returns the official license name for the given SPDX id, or nil if unable to do so."
  (set/map-invert spdx-name-to-id))

(def ^:private aliases
  (merge (u/mapfonk #(re-pattern (u/escape-re (s/lower-case (s/replace % " only" "")))) spdx-name-to-id)   ; Start with all of the official license names
         {; Identifiers:
          #"apache( software)? license(s)?(,)? version 2\.0"      "Apache-2.0"
          #"apache 2(\.0)?"                                       "Apache-2.0"
          #"apache( software)? license(s)?"                       "Apache-1.0"   ; Assume earliest version
          #"eclipse public license - v 1\.1"                      "EPL-1.1"
          #"eclipse public license - v 1\.0"                      "EPL-1.0"
          #"eclipse public license"                               "EPL-1.0"      ; Assume earliest version
          #"copyright( \(c\)|©)? 2011 matthew lee hinman"         "MIT"
          #"gnu lesser general public license"                    "LGPL-2.0"     ; Assume earliest version
          ; Expressions:
          #"cddl\+gpl license"                                    "CDDL-1.0 OR GPL-2.0"
          #"cddl/gplv2\+ce"                                       "CDDL-1.0 OR (GPL-2.0 WITH Classpath-exception-2.0)"
          #"cddl \+ gplv2 with classpath exception"               "CDDL-1.0 OR (GPL-2.0 WITH Classpath-exception-2.0)"
          #"cddl 1\.1\+gpl license"                               "CDDL-1.1 OR GPL-2.0"
          #"dual license consisting of the cddl v1\.1 and gpl v2" "CDDL-1.1 OR GPL-2.0"
          #"lesser general public license, version 3 or greater"  "LGPL-3.0+"}))

; Store regexes in reverse size order, on the naive assumption that longer regexes are more specific and should be processed first
(def ^:private alias-regexes (reverse (sort-by #(count (str %)) (keys aliases))))

(defn guess
  "Attempts to guess an SPDX license identifier or expression, from the given license text fragment. Returns nil if unable to do so."
  [text]
  (when text
    (if-let [exact-match (license-name->spdx-id text)]
      exact-match
      (let [ltext (s/lower-case text)]
        (loop [f (first alias-regexes)
               r (rest  alias-regexes)]
          (when f
            (if (re-find f ltext)
              (get aliases f)
              (recur (first r) (rest r)))))))))
