(ns cleric.store
  (require [clojure.data.json :as json]
           [clojure.java.io :as io]))

(defonce sources (atom {}))
(defonce tweets (atom {}))

(defn add-source [command mode source]
  (swap! sources assoc command [mode source]))

(defn remove-source [command]
  (swap! sources dissoc command))

(defn add-tweets [source lst]
  (swap! tweets assoc source lst))

(defn persist []
  (let [save-store (fn [store file]
                     (spit file (json/write-str store)))]
    (save-store @sources "store/sources")
    (save-store @tweets "store/tweets")))

(defn hydrate []
  (let [load-store (fn [store file] 
                     (if (.exists (io/as-file file))
                       (swap! store merge (json/read-str (slurp file)))))]
    (load-store sources "store/sources")
    (load-store tweets "store/tweets")))
