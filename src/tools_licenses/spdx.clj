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

(def ^:private spdx-license-list-uri "https://spdx.org/licenses/licenses.json")
(def ^:private spdx-license-list     (try
                                       (json/parse-string (slurp spdx-license-list-uri) u/clojurise-json-key)
                                       (catch Exception e
                                         (throw (ex-info (str "Unexpected " (cr/typename (type e)) " while reading " spdx-license-list-uri ". Please check your internet connection and try again.") {})))))

; Alternative indexes into the SPDX list
(def ^:private spdx-name-to-id (apply merge (map #(hash-map (s/lower-case (:name %)) (:license-id %)) (:licenses spdx-license-list))))
(def ^:private spdx-url-to-id  (into {} (for [lic (:licenses spdx-license-list) url (:see-also lic)] (hash-map (s/lower-case url) (:license-id lic)))))


(def license-list-version
  "The version of the license list in use."
  (:license-list-version spdx-license-list))

(def license-list
  "The SPDX license list."
  (:licenses spdx-license-list))

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
  (merge ; Start with all of the official license names
         (into {}
          (for [[k v] spdx-name-to-id]
            [(re-pattern (u/escape-re (s/lower-case k))) [v]]))

         ; Then add some common variants
         {
          #"apache(\s+software)?(\s+license(s)?(,)?)?(\s+version)? 2(\.0)?"               ["Apache-2.0"]
          #"apache(\s+software)?(\s+license(s)?(,)?)?(\s+version)? 1\.1"                  ["Apache-1.1"]
          #"apache(\s+software)?(\s+license(s)?(,)?)?(\s+version)? 1\.0"                  ["Apache-1.0"]
          #"apache(\s+software)?(\s+license(s)?)?"                                        ["Apache-1.0"]   ; Assume earliest version
          #"copyright(\s+\(c\)|©)?\s+2011\s+matthew\s+lee\s+hinman"                       ["MIT"]
          #"cup\s+parser\s+generator\s+copyright\s+notice,\s+license,\s+and\s+disclaimer" ["BSD-3-Clause"] ; See https://www.apache.org/legal/resolved.html#category-a
          #"eclipse\s+distribution\s+license\s+-\s+v\s+1\.0"                              ["BSD-3-Clause"] ; See https://wiki.spdx.org/view/Legal_Team/License_List/Licenses_Under_Consideration#Processed_License_Requests
          #"eclipse\s+public\s+license\s*-\s*v\s*2(\.0)?"                                 ["EPL-2.0"]
          #"eclipse\s+public\s+license\s*-\s*v\s*1\.1"                                    ["EPL-1.1"]
          #"eclipse\s+public\s+license\s*-\s*v\s*1(\.0)?"                                 ["EPL-1.0"]
          #"eclipse\s+public\s+license"                                                   ["EPL-1.0"]      ; Assume earliest version
          #"gnu\s+affero\s+general\s+public\s+license"                                    ["AGPL-3.0"]     ; Assume earliest version
          #"gnu\s+affero\s+general\s+public\s+license\s+version\s+3"                      ["AGPL-3.0"]
          #"gnu\s+general\s+public\s+license\s+version"                                   ["GPL-1.0"]      ; Assume earliest version
          #"gnu\s+general\s+public\s+license\s+version\s+1"                               ["GPL-1.0"]
          #"gnu\s+general\s+public\s+license\s+version\s+2"                               ["GPL-2.0"]
          #"gnu\s+general\s+public\s+license\s+version\s+3"                               ["GPL-3.0"]
          #"gnu\s+lesser\s+general\s+public\s+license\s+version"                          ["LGPL-2.0"]     ; Assume earliest version
          #"gnu\s+lesser\s+general\s+public\s+license\s+version\s+2"                      ["LGPL-2.0"]
          #"gnu\s+lesser\s+general\s+public\s+license\s+version\s+2\.1"                   ["LGPL-2.1"]
          #"gnu\s+lesser\s+general\s+public\s+license\s+version\s+3"                      ["LGPL-3.0"]
          #"the\s+mx4j\s+license,\s+version\s+1\.0"                                       ["Apache-1.1"]   ; See https://wiki.spdx.org/view/Legal_Team/License_List/Licenses_Under_Consideration#Processed_License_Requests
          #"cddl\+gpl\s+license"                                                          ["CDDL-1.0" "GPL-2.0"]
          #"cddl/gplv2\+ce"                                                               ["CDDL-1.0" "GPL-2.0-with-classpath-exception"]
          #"cddl\s+\+\s+gpl\s*v2\s+with\s+classpath\s+exception"                          ["CDDL-1.0" "GPL-2.0-with-classpath-exception"]
          #"cddl\s+1\.1\+gpl\s+license"                                                   ["CDDL-1.1" "GPL-2.0"]
          #"dual\s+license\s+consisting\s+of\s+the\s+cddl\s+v1\.1\s+and\s+gpl\s+v2"       ["CDDL-1.1" "GPL-2.0"]
          #"lesser\s+general\s+public\s+license,\s+version\s+3\s+or\s+greater"            ["LGPL-3.0+"]}))

; Store regexes in reverse size order, on the naive assumption that longer regexes are more specific and should be processed first
(def ^:private alias-regexes (reverse (sort-by #(count (str %)) (keys aliases))))

; Note: this should be updated to use the methods described here: https://spdx.dev/license-list/matching-guidelines/
(defn guess
  "Attempts to guess the SPDX license identifier(s) (a sequence), from the given license text fragment. Returns nil if unable to do so."
  [text]
  (when text
    (if-let [exact-match (license-name->spdx-id text)]
      [exact-match]
      (let [ltext (s/lower-case text)]
        (loop [f (first alias-regexes)
               r (rest  alias-regexes)]
          (when f
            (if (re-find f ltext)
              (get aliases f)
              (recur (first r) (rest r)))))))))
