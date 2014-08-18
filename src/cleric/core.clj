(ns cleric.core
  (:require [clojure.core.async :refer [<!! >!! chan]]
            [cleric.irc :as irc]
            [cleric.responses :as r]
            [cleric.config :as config]
            [cleric.plugins :as plugins]
            [cleric.connection :as connection]))

(defn get-responses [msg]
  "Send the message to the response system. If the response system throws
  an error, return that error as the response"
  (try
    (doall (r/response msg)) ; fully eval the responses for our try/catch
    (catch Exception e 
      (.printStackTrace e)
      [(irc/privmsg msg (str "Error: " (.getMessage e)))])))

(defn run-bot 
  "makes an irc connection and replies to incoming messages.
  this function blocks until the irc connection terminates"
  [{:keys [host port nick username realname channels]}]
  (let [incoming (chan 10) 
        outgoing (chan 10)
        say #(doall (map (partial >!! outgoing) %))
        preamble (flatten [(irc/nick nick)
                           (irc/user username realname)
                           (map irc/join channels)])]
    (plugins/load-from-disk "src/cleric/plugins")
    (connection/sync-channels-to-socket host port 
                                        incoming irc/parse-message 
                                        outgoing irc/serialize-message)
    (say preamble)
    (loop []
      ; read msgs off the channel until the channel closes
      ; a channel closing represents an unexpected tcp disconnect
      (when-let [msg (<!! incoming)] 
        (if-let [responses (seq (get-responses msg))]
          (say responses))
        (recur)))))

(defn -main [& args]
  (let [cfg (config/load-properties "resources/cleric.properties")]
    (loop []
      (run-bot cfg)
      (Thread/sleep 10e3) ; wait 10s and reconnect
      (recur))))
