(ns cleric.responses
  "IRC response system. To make the bot respond to a 
  specific IRC command, implement the corresponding multimethod"
  (:require [cleric.irc :as irc] 
            [cleric.plugins :as plugins]))

(defmulti response :command)

(defmethod response :default [msg] [])

(defmethod response :ping [msg] [(irc/pong msg)])

(defmethod response 
  ; send PRIVMSGs to the plugin system
  :privmsg [{[target] :params 
             text :trailing
             :as request}]
  (if (not= target "clerical") ;don't response to PMs
    (let [responses (plugins/run text)
          create-privmsg (partial irc/privmsg request)]
      (map create-privmsg responses))))

