(ns cleric.core
  (require [qbits.ash :as ash]
           [cleric.common :refer [load-properties]]
           [cleric.twitter :refer [register-plugin
                                   deregister-plugin
                                   run-plugin]]))

(def config (load-properties "resources/cleric.properties"))

(defn -main
  [& args]
  (-> (apply ash/make-bot (mapcat seq config))
      register-plugin
      deregister-plugin
      run-plugin))

