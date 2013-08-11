(ns cleric.core
  (require [qbits.ash :as ash]
           [cleric.twitter :refer [register-plugin
                                   deregister-plugin
                                   run-plugin]]))

(defn -main
  [& args]
  (-> (ash/make-bot :nick "cleric"
                    :name "cleric"
                    :host "irc.freenode.net"
                    :port 6667
                    :channels ["#rhnoise"]
                    :auto-reconnect true)
      register-plugin
      deregister-plugin
      run-plugin))

