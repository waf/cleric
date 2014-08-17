(ns cleric.responses
  (:require [cleric.irc :as irc] 
            [cleric.plugins :as plugins]))

(defmulti response :command)

(defmethod response :default [msg] [])

(defmethod response :ping [msg] [(irc/pong msg)])

(defmethod response :privmsg [{[target] :params 
                              text :trailing
                              :as request}]
  (if (not= target "clerical") ;don't response to PMs
    (let [responses (plugins/run text)
          create-privmsg (partial irc/privmsg request)]
      (map create-privmsg responses))))

