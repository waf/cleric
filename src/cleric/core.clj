(ns cleric.core
  (:require [clojure.core.async :refer [<!! >!! chan]]
            [cleric.irc :as irc]
            [cleric.responses :as r]
            [cleric.config :as config]
            [cleric.plugins :as plugins]
            [cleric.connection :as conn]))

(defn- get-responses [msg]
  "Send the message to the response system. If the response system throws
  an error, return that error as the response"
  (try
    (doall (r/response msg)) ; fully eval the responses for our try/catch
    (catch Exception e 
      (.printStackTrace e)
      [(irc/privmsg msg (str "Error: " (.getMessage e)))])))

(defn- send-responses [channel responses]
  "Eagerly put all the responses into the channel"
  (doall 
    (map (partial >!! channel) responses)))

(defn- get-channels [host port]
  (conn/create-socket-channels host port 
                               irc/parse-message 
                               irc/serialize-message))

(defn- run-bot 
  "Makes an irc connection and replies to incoming messages.
  this function blocks until the irc connection terminates"
  [{:keys [host port nick username realname channels]}]

  (plugins/load-from-disk "src/cleric/plugins")
  (let [preamble (flatten [(irc/nick nick)
                           (irc/user username realname)
                           (map irc/join channels)])
        [incoming outgoing] (get-channels host port) 
        say (partial send-responses outgoing)]
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
