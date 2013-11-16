(ns cleric.store
  (require [clojure.data.json :as json]
           [clojure.java.io :as io]))

(defonce store (atom {}))
(defonce filename "store/data")

(defn persist []
  (spit filename (json/write-str @store)))

(defn hydrate []
  (if (.exists (io/as-file filename))
    (swap! store merge (json/read-str (slurp filename)))))

(defn get-val [skey]
  (@store skey))

(defn put-val [skey sval]
  (swap! store assoc skey sval)
  (persist))

(defn rm-val [skey]
  (swap! store dissoc skey)
  (persist))
