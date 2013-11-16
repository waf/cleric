(ns cleric.common
  (require [qbits.ash :as ash]
           [clojure.tools.logging :as log]
           [clojure.java.io :as io]))

(defn plugin [bot pattern action]
    (ash/listen bot :on-message
                (fn [event] 
                  (when-let [match (re-find pattern (:content event))]
                    ;regex api is awkward, is there a better way?
                    (let [params (if (vector? match) (rest match) [])] 
                      (log/info str "calling " action)
                      (log/info str "with args " params)
                      (ash/reply bot event (apply action (vec params))))))))

(defn load-properties [file-name]
  (with-open [^java.io.Reader reader (io/reader file-name)]
    (let [props (java.util.Properties.)]
      (.load props reader)
      (into {} (for [[k v] props] [(keyword k) (read-string v)])))))

