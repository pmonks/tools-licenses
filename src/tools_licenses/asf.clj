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

(ns tools-licenses.asf
  "Logic related to the ASF's 3rd Party License Policy."
  (:require [clojure.string      :as s]
            [tools-licenses.spdx :as spdx]))


; See https://www.apache.org/legal/resolved.html#category-a
(def ^:private asf-cat-a
  ["Apache-1.1"
   "Apache-2.0"
   "PHP-3.01"
   "MX4J"            ; Non-SPDX identifier in fallbacks
   "BSD-2-Clause"
   "BSD-3-Clause"
   "DOM4J"           ; Non-SPDX identifier in fallbacks
   "PostgreSQL"
;   "EDL-1.0"        ; Legally equivalent to BSD-3-Clause according to SPDX, and detected as such
   "MIT"
   "ISC"
   "SMLNJ"
;   "CUP"            ; Legally equivalent to BSD-3-Clause according to ASF, and detected as such
   "ICU"
   "NCSA"
   "W3C"
;   "W3C Community Contributor License Agreement"    ; Not in SPDX; not yet supported
   "Xnet"
   "Zlib"
   "Libpng"
;   "FSF autoconf license"                           ; Not in SPDX; not yet supported
;   "DejaVu Fonts (Bitstream Vera/Arev licenses)"    ; Not in SPDX; not yet supported
   "AFL-3.0"
;   "Service+Component+Architecture+Specifications"  ; Not in SPDX; not yet supported
;   "OOXML XSD ECMA License"                         ; Not in SPDX; not yet supported
   "MS-PL"
;   "Creative Commons Copyright-Only Dedication"     ; Not in SPDX; not yet supported
   "PSF-2.0"
;   "Python Imaging Library Software License"        ; Not in SPDX; not yet supported
   "APAFML"
   "BSL-1.0"
;   "License for CERN packages in COLT"              ; Not in SPDX; not yet supported
   "WTFPL"
;   "The Romantic WTF public license"                ; Not in SPDX; not yet supported
   "Unicode-DFS-2015"
   "Unicode-DFS-2016"
   "ZPL-2.0"
;   "ACE license"                                    ; Not in SPDX; not yet supported; license text is no longer available online
   "UPL-1.0"
;   "Open Grid Forum License"                        ; Not in SPDX; not yet supported
;   "Google \"Additional IP Rights Grant (Patents)\" file" ; Not in SPDX; not yet supported
   "Unlicense"
   "HPND"
   "MulanPSL-2.0"
   ])

(def ^:private asf-cat-a-special
  ["OGL-UK-3.0"    ; Note: provided it does not have a custom attribution notice, otherwise it becomes category-b
   "Public domain" ; Not in SPDX; see https://www.apache.org/legal/resolved.html#handling-public-domain-licensed-works
  ])


; See https://www.apache.org/legal/resolved.html#category-b
(def ^:private asf-cat-b
  ["CDDL-1.0"
   "CDDL-1.1"
   "CPL-1.0"
   "EPL-1.0"
   "EPL-2.0"
   "IPL-1.0"
   "MPL-1.0"
   "MPL-1.1"
   "MPL-2.0"
   "SPL-1.0"
   "OSL-3.0"
   "ErlPL-1.1"
;   "UnRAR License (only for unarchiving)"           ; Not in SPDX; not yet supported
   "OFL-1.0"
   "OFL-1.1"
;   "Ubuntu Font License Version 1.0"                ; Not in SPDX; not yet supported
   "IPA"
   "Ruby"
   ])

; See https://www.apache.org/legal/resolved.html#category-x
(def ^:private asf-cat-x
  [;"Binary Code License (BCL)"                      ; Not in SPDX; not yet supported
;   "Intel Simplified Software License"              ; Not in SPDX; not yet supported
;   "JSR-275 License"                                ; Not in SPDX; not yet supported - TODO: ADD THIS TO FALLBACKS WITH A NON-SPDX ID
;   "Microsoft Limited Public License"               ; Not in SPDX; not yet supported
;   "Amazon Software License (ASL)"                  ; Not in SPDX; not yet supported
;   "Java SDK for Satori RTM license"                ; Not in SPDX; not yet supported
;   "Redis Source Available License (RSAL)"          ; Not in SPDX; not yet supported
;   "Booz Allen Public License"                      ; Not in SPDX; not yet supported
;   "Confluent Community License Version 1.0"        ; Not in SPDX; not yet supported
;   "Any license including the Commons Clause License Condition v1.0" ; Not in SPDX; not yet supported
;   "Creative Commons Non-Commercial variants"       ; Handled via a special case
;   "Sun Community Source License 3.0"               ; Not in SPDX; not yet supported
   "GPL-1.0"
   "GPL-1.0-only"
   "GPL-2.0"
   "GPL-2.0+"
   "GPL-2.0-only"
   "GPL-2.0-or-later"
   "GPL-2.0-with-autoconf-exception"
   "GPL-2.0-with-bison-exception"
   "GPL-2.0-with-classpath-exception"
   "GPL-2.0-with-font-exception"
   "GPL-2.0-with-GCC-exception"
   "GPL-3.0"
   "GPL-3.0+"
   "GPL-3.0-only"
   "GPL-3.0-or-later"
   "GPL-3.0-with-autoconf-exception"
   "GPL-3.0-with-GCC-exception"
   "AGPL-1.0"                                        ; Note this license is not related to the GNU AGPL
   "AGPL-1.0-only"                                   ; Note this license is not related to the GNU AGPL
   "AGPL-1.0-or-later"                               ; Note this license is not related to the GNU AGPL
   "AGPL-3.0"
   "AGPL-3.0-only"
   "AGPL-3.0-or-later"
   "LGPL-2.0"
   "LGPL-2.0+"
   "LGPL-2.0-only"
   "LGPL-2.0-or-later"
   "LGPL-2.1"
   "LGPL-2.1+"
   "LGPL-2.1-only"
   "LGPL-2.1-or-later"
   "LGPL-3.0"
   "LGPL-3.0+"
   "LGPL-3.0-only"
   "LGPL-3.0-or-later"
   "QPL-1.0"
   "Sleepycat"
   "SSPL-1.0"
   "CPOL-1.02"
   "BSD-4-Clause"
   "BSD-4-Clause-Shortened"
   "BSD-4-Clause-UC"
;   "Facebook BSD+Patents license"                   ; Not in SPDX; not yet supported
   "NPL-1.0"
   "NPL-1.1"
;   "The Solipsistic Eclipse Public License"         ; Not in SPDX; not yet supported
;   "The \"Don't Be A Dick\" Public License"         ; Not in SPDX; not yet supported
   "JSON"
  ])

; Unless these licenses appear in cat A or B, these should be considered cat X (see https://www.apache.org/legal/resolved.html#criteria))
(def ^:private non-osi-approved
  (vec (map :license-id (filter :is-osi-approved spdx/license-list))))

(defn category
  "Returns the ASF 'category' for the given SPDX license id, which will be one of:

  :category-a         - see https://www.apache.org/legal/resolved.html#category-a
  :category-a-special
  :category-b         - see https://www.apache.org/legal/resolved.html#category-b
  :creative-commons   - see https://www.apache.org/legal/resolved.html#cc-by (may be any category...)
  :category-x         - see https://www.apache.org/legal/resolved.html#category-x
  :non-osi-approved   - see https://www.apache.org/legal/resolved.html#criteria (effectively the same as :category-x)
  :unknown            - the ASF category could not be determined"
  [spdx-id]
  (if (seq (filter #(= spdx-id %) asf-cat-a))
    :category-a
    (if (seq (filter #(= spdx-id %) asf-cat-a-special))
      :category-a-special
      (if (seq (filter #(= spdx-id %) asf-cat-b))
        :category-b
        (if (s/starts-with? spdx-id "CC-BY-")
          :creative-commons
          (if (seq (filter #(= spdx-id %) asf-cat-x))
            :category-x
            (if (seq (filter #(= spdx-id %) non-osi-approved))
              :non-osi-approved
              :unknown)))))))

(def ^:private category-order
  {:category-a         0
   :category-a-special 1
   :category-b         2
   :creative-commons   4
   :category-x         5
   :non-osi-approved   6
   :unknown            7})

(def third-party-license-uri "https://www.apache.org/legal/resolved.html")

(def category-info
  "Information on each category."
  {:category-a         {:name "Category A"                :url "https://www.apache.org/legal/resolved.html#category-a"}
   :category-a-special {:name "Category A (with caveats)" :url "https://www.apache.org/legal/resolved.html#category-a"}
   :category-b         {:name "Category B"                :url "https://www.apache.org/legal/resolved.html#category-b"}
   :creative-commons   {:name "Creative Commons Licenses" :url "https://www.apache.org/legal/resolved.html#cc-by"}
   :category-x         {:name "Category X"                :url "https://www.apache.org/legal/resolved.html#category-x"}
   :non-osi-approved   {:name "Non-OSI Approved Licenses" :url "https://www.apache.org/legal/resolved.html#criteria"}
   :unknown            {:name "Unknown"                   :url "https://www.apache.org/legal/resolved.html#criteria"}})

(def category-compare
  "A comparator for ASF category keywords (see category fn for that list)."
  (comparator
    (fn [l r]
      (compare (get category-order l 99) (get category-order r 99)))))

(def least-to-most-problematic-categories
  "A sequence of categories in least to most problematic."
  (sort-by category-order (keys category-order)))

(defn least-problematic-category
  "Returns the least problematic category for the given sequence of SPDX license identifiers."
  [licenses]
  (first (sort-by category-order (map category licenses))))
