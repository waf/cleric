(ns cleric.core
  (require [qbits.ash :as ash]
           [cleric.store :as store]
           [cleric.common :refer [load-properties]]
           [cleric.woop :as woop]
           [cleric.twitter :as twitter]))

(def config (load-properties "resources/cleric.properties"))

(defn -main
  [& args]
  (store/hydrate)
  (-> (apply ash/make-bot (mapcat seq config))
      woop/woop-plugin
      twitter/twitter-plugin))

