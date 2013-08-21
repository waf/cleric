(ns cleric.core
  (require [qbits.ash :as ash]
           [cleric.store :as store]
           [cleric.common :refer [load-properties]]
           [cleric.twitter :refer [register-plugin
                                   deregister-plugin
                                   list-plugin
                                   run-plugin]]))

(def config (load-properties "resources/cleric.properties"))

(defn -main
  [& args]
  (store/hydrate)
  (.addShutdownHook (Runtime/getRuntime) (Thread. store/persist))
  (-> (apply ash/make-bot (mapcat seq config))
      register-plugin
      deregister-plugin
      list-plugin
      run-plugin))

