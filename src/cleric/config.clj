(ns cleric.config
  (:require [clojure.java.io :as io]))

;TODO: maybe replace with edn
(defn load-properties [file-name]
  (with-open [reader (io/reader file-name)]
    (let [props (java.util.Properties.)]
      (.load props reader)
      (into {} (for [[k v] props] [(keyword k) (read-string v)])))))
