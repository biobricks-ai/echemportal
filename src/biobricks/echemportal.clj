(ns biobricks.echemportal
  (:require [babashka.fs :as fs]
            [clojure-csv.core :as csv]
            [clojure.java.shell :as sh]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [etaoin.api :as ea]
            [lambdaisland.uri :as uri]))

(def retry-recur-val ::retry-recur)

(defmacro retry
  "Retries body up to n times, doubling interval-ms each time
   and adding jitter.

   If throw-pred is provided, it will be called on the exception. If
   throw-pred returns true, the exception is re-thrown and the body is
   not retried."
  [opts & body]
  `(let [opts# ~opts
         throw-pred# (or (:throw-pred opts#) (constantly false))]
     (loop [interval-ms# (:interval-ms opts#)
            n# (:n opts#)]
     ;; Can't recur from inside the catch, so we use a special return
     ;; value to signal the need to recur.
       (let [ret#
             (try
               ~@body
               (catch Exception e#
                 (if (and (pos? n#) (not (throw-pred# e#)))
                   (do
                     (log/info e# "Retrying after" interval-ms# "ms due to Exception")
                     retry-recur-val)
                   (throw e#))))]
         (if (= ret# retry-recur-val)
           (do
             (Thread/sleep interval-ms#)
             (recur (+ interval-ms# interval-ms# (.longValue ^Integer (rand-int 100))) (dec n#)))
           ret#)))))

;; Crawl

(def substance-search-uri "https://www.echemportal.org/echemportal/substance-search")

(defn click-csv-button? [driver]
  (let [csv-query [{:tag :echem-export-buttons}
                   {:tag :button}
                   {:fn/text "CSV"}]
        no-matches-query {:fn/text "Your search criteria didn't match any substance."}]
    (loop [[_ & more] (range 60)]
      (cond
        (ea/visible? driver csv-query)
        (do (ea/click-visible driver csv-query) true)

        (ea/visible? driver no-matches-query)
        false

        more
        (do (Thread/sleep 500) (recur more))

        :else
        (throw (RuntimeException. "Failed to get search results"))))))

(defn substance-search-results [download-dir search-term]
  (let [uri (str substance-search-uri "?"
                 (uri/map->query-string {:query_term search-term}))
        opts {:download-dir download-dir
              :headless true
              :user-agent "biobricks.ai brick builder"}]
    (ea/with-wait-timeout 30
      (ea/with-driver :chrome opts driver
        (ea/go driver uri)
        (when (click-csv-button? driver)
          (loop [[_ & more] (range 60)]
            (let [results-file (->> (fs/list-dir download-dir)
                                    (filter #(= "result.csv" (fs/file-name %)))
                                    first)]
              (or
               results-file
               (when more
                 (Thread/sleep 500)
                 (recur more))))))))))

(defn crawl-substances [_opts]
  (let [target-dir (fs/path "target" "echemportal")]
    (fs/create-dirs target-dir)
    (doseq [i (range 10 999)]
      (retry
       {:interval-ms 1000
        :n 10
        :throw-pred (partial instance? InterruptedException)}
       (fs/with-temp-dir [dir {:prefix "brick-builder"}]
         (if-let [results (substance-search-results
                           dir
                           (str i (when (<= 100 i) "*") "-*-*"))]
           (do
             (fs/move results (fs/path target-dir (str i ".csv")))
             (log/info "echemportal: Saved results for" i))
           (log/info "echemportal: No results for" i)))))))

(comment
  ;; Download search results
  (fs/with-temp-dir [dir {:prefix "brick-builder"}]
    (-> (substance-search-results (str dir) "108-*-*")
        fs/file slurp))

  ;; Empty results return nil
  (fs/with-temp-dir [dir {:prefix "brick-builder"}]
    (substance-search-results (str dir) "05-*-*"))

  ;; Run crawler
  (def crawler (future (crawl-substances)))
  (future-cancel crawler))

;; Convert search results to parquet

(def csv-header
  ["Substance Name"
   "Name type"
   "Substance Number"
   "Number type"
   "Remark"
   "Level"
   "Result link"
   "Source"
   "GHS data"
   "Property data"
   ""])

(defn fixup-row [row]
  (cond
    (= 10 (count row)) row

    ; Fix unquoted urls with commas in them
    (-> (nth row 6) uri/uri :scheme)
    (let [i (-> row count (- 10) (+ 7))]
      (-> (subvec row 0 6)
          (into [(str/join "," (subvec row 6 i))])
          (into (subvec row i))))

    :else row))

;; Known bad lines and their legal replacements
(def replacement-lines
  {"\"Aldicarb degradate \"Aldicarb sulfone\"\",,1646-88-4,CAS Number,\"\",2,https://www.regulations.gov/document?D=EPA-HQ-OPP-2012-0161-0021,EPA OPPALB,,false"
   "\"Aldicarb degradate \"\"Aldicarb sulfone\"\"\",,1646-88-4,CAS Number,\"\",2,https://www.regulations.gov/document?D=EPA-HQ-OPP-2012-0161-0021,EPA OPPALB,,false"})

(defn load-crawl-csv [text]
  (try
    (-> text csv/parse-csv doall)
    (catch Exception e
      (->> (str/split text #"\n")
           (map #(replacement-lines % %))
           (str/join \newline)
           csv/parse-csv
           doall))))

(defn collect-results [dir]
  (loop [[file & more] (fs/list-dir dir)
         results (transient #{})]
    (if-not file
      (persistent! results)
      (let [text (-> file fs/file slurp)
            [header & rows] (try
                              (load-crawl-csv text)
                              (catch Exception e
                                (throw (ex-info (str "Error parsing " file ": " (ex-message e))
                                                {:cause e
                                                 :file file
                                                 :text text}))))
            rows (map fixup-row rows)]
        (when-not (= header csv-header)
          (throw (ex-info "Unexpected CSV header"
                          {:actual header :expected csv-header})))
        (doseq [row rows]
          (when (not= 10 (count row))
            (throw (ex-info (str "Wrong number of columns (" (count row) "), expected 10.")
                            {:actual (count row)
                             :expected 10
                             :file file
                             :row row}))))
        (recur more (reduce conj! results rows))))))

(defn write-csv [file results]
  (->> results
       (sort-by
        (fn [[_ _ num]]
          (or (some-> num (str/replace "-" "") parse-long)
              Long/MAX_VALUE)))
       (cons (pop csv-header))
       csv/write-csv
       (spit (fs/file file)))
  file)

(defn write-parquet [file results]
  (fs/with-temp-dir [dir {:prefix "brick-builder"}]
    (let [csv (fs/path dir "echemportal.csv")
          parquet (fs/path dir "echemportal.parquet")]
      (write-csv csv results)
      (sh/sh "csv2parquet" (str csv) (str parquet))
      (fs/move parquet file {:replace-existing true})))
  file)

(defn crawl-to-parquet [{:keys [crawl-results output]}]
  (->> (fs/path crawl-results)
       collect-results
       (write-parquet (fs/path output))))

(comment
  (do
    (def results (collect-results (fs/path "target" "echemportal")))
    (count results))
  (write-csv (fs/path "target" "echemportal.csv") results)
  (write-parquet (fs/path "target" "echemportal.parquet") results)
  (crawl-to-parquet {:crawl-results "target/echemportal"
                     :output "target/echemportal.parquet"}))
