(ns cleric.common
  (require [qbits.ash :as ash]
           [clojure.java.io :as io]))

(defn plugin [pattern action]
  (fn [bot] 
    (ash/listen bot :on-message
                (fn [event] (when-let [matches (->> event
                                                    :content
                                                    (re-find pattern)
                                                    rest )]
                              (ash/reply bot event (apply action matches)))))))

(defn load-properties [file-name]
  (with-open [^java.io.Reader reader (io/reader file-name)]
    (let [props (java.util.Properties.)]
      (.load props reader)
      (into {} (for [[k v] props] [(keyword k) (read-string v)])))))

