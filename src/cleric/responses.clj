(ns cleric.responses
  "IRC response system. To make the bot respond to a 
  specific IRC command, implement the corresponding multimethod"
  (:require [clojure.string :refer [split-lines blank?]]
            [cleric.irc :as irc] 
            [cleric.plugins :as plugins]))

(defmulti response :command)

(defmethod response :default [msg] [])

(defmethod response :ping [msg] [(irc/pong msg)])

(defmethod response 
  ; send PRIVMSGs to the plugin system
  :privmsg [{[target] :params 
             text :trailing
             :as request}]
  (if (not= target "cleric") ;don't respond to PMs
    (let [responses (plugins/run text)
          message-text (filter (complement blank?) 
                               (mapcat split-lines responses))
          create-privmsg (partial irc/privmsg request)]
      (map create-privmsg message-text))))

