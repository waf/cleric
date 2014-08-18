(ns cleric.connection
  (:require [clojure.core.async :refer [<!! >!! close! go <! >! chan go-loop pub sub]]
            [clojure.java.io :as io])
  (:import java.net.Socket))

(defn- create-connection [host port]
  (let [socket (Socket. host port)]
    {:socket socket
     :in (io/reader socket)
     :out (io/writer socket)}))

(defn- socket->channel [socket channel deserializer]
  "Any data received from the socket is written to the core.async channel, after
  being deserialized with the provided function."
  (go
    (loop [lines (line-seq socket)]
      (when (seq lines)
        (let [line (first lines)]
          (println "received: " line)
          (>! channel (deserializer (first lines))))
        (recur (rest lines))))
    (println "socket connection interrupted")
    (close! channel)))

(defn- channel->socket [channel socket serializer]
  "Any messages received on the core.async channel are written to the socket, after
  being serialized with the provided function."
  (go-loop []
           (let [msg-to-send (<! channel)
                 serialized (serializer msg-to-send)]
             (println "sending: " serialized)
             (doto socket
               (.write serialized)
               (.newLine)
               (.flush))
             (recur))))

; TODO: seems like a great use case for transducers
(defn sync-channels-to-socket [host port incoming deserializer outgoing serializer]
  "runs both the socket->channel and channel->socket async goroutines to 
  provide bidirectional TCP socket communication via core.async channels."
  (let [conn (create-connection host port)]
    (socket->channel (:in conn) incoming deserializer)
    (channel->socket outgoing (:out conn) serializer)))
