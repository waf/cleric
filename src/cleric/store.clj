(ns cleric.store
  (require [clojure.data.json :as json]
           [clojure.java.io :as io]))

(defonce store (atom {}))
(defonce filename "store/data")

(defn- persist []
  (spit filename (json/write-str @store)))

(defn- hydrate-if-needed []
  (if (and (empty? @store) 
           (.exists (io/as-file filename)))
    (swap! store merge (json/read-str (slurp filename)))))

(defn- sync-to-disk [op]
  (hydrate-if-needed)
  (op)
  (persist))

(defn get-val [skey]
  (hydrate-if-needed)
  (@store skey))

(defn put-val [skey sval]
  (sync-to-disk #(swap! store assoc skey sval)))

(defn rm-val [skey]
  (sync-to-disk #(swap! store dissoc skey)))
