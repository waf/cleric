(ns cleric.connection
  (:require [clojure.core.async :refer [<!! >!! close! go <! >! chan go-loop pub sub]]
            [cleric.irc :as irc]
            [clojure.java.io :as io])
  (:import java.net.Socket
           javax.net.ssl.SSLSocketFactory
           java.io.IOException))

(defn create-connection [host port]
  (let [socket (Socket. host port)]
    {:socket socket
     :in (io/reader socket)
     :out (io/writer socket)}))

(defn socket->channel [socket channel]
  (go
    (loop [lines (line-seq socket)]
           (when (seq lines)
             (let [line (first lines)]
               (println "received: " line)
               (>! channel (irc/parse-message (first lines))))
             (recur (rest lines))))
    (println "socket connection interrupted")
    (close! channel)))

(defn channel->socket [channel socket]
  (go-loop []
           (let [msg-to-send (<! channel)
                 serialized (irc/serialize-message msg-to-send)]
             (println "sending: " serialized)
             (doto socket
               (.write serialized)
               (.newLine)
               (.flush))
             (recur))))

(defn sync-channels-to-socket [host port incoming outgoing]
  (let [conn (create-connection host port)]
    ; async goroutine that reads tcp messages from a socket and writes them to a channel
    (socket->channel (:in conn) incoming)
    ; async goroutine that reads messages from a channel and writes them to a socket
    (channel->socket outgoing (:out conn))))
