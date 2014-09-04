(ns cleric.connection
  "Provides bidirectional TCP socket communication via core.async channels"
  (:require [clojure.core.async :refer [go go-loop <! >! close! chan]]
            [clojure.tools.logging :refer [spyf]]
            [clojure.java.io :as io])
  (:import java.net.Socket
           java.io.IOException))

(defn- create-connection [host port]
  (let [socket (Socket. host port)]
    {:socket socket
     :in (io/reader socket)
     :out (io/writer socket)
     :command (chan)}))

(defn- destroy-connection [conn incoming outgoing]
  (.close (:in conn))
  (.close (:out conn))
  (close! (:command conn))
  (close! incoming)
  (close! outgoing))

(defn- socket->channel [socket channel deserializer command]
  "Any data received from the socket is written to the core.async channel, after
  being deserialized with the provided function."
  (go (loop [lines (try (line-seq socket) (catch IOException _ nil))]
        (when (seq lines)
          (->> lines
            (first)
            (spyf "recv: %s")
            (deserializer)
            (>! channel))
          (recur (rest lines))))
      (>! command :connection-close)))

(defn- channel->socket [channel socket serializer command]
  "Any messages received on the core.async channel are written to the socket, after
  being serialized with the provided function."
  (go (loop []
        (when-let [msg-to-send (<! channel)]
          (doto socket
            (.write (spyf "send: %s" 
                          (serializer msg-to-send)))
            (.newLine)
            (.flush))
          (recur)))))

(defn- monitor-for-cleanup [conn incoming outgoing]
  (go 
    (case (<! (:command conn))
      :connection-close (destroy-connection conn incoming outgoing))))

; TODO: seems like a great use case for transducers
(defn create-socket-channels [host port deserializer serializer]
  "Entry point for the module. Kicks off both the socket->channel and channel->socket async goroutines."
  (let [conn (create-connection host port)
        incoming (chan 10)
        outgoing (chan 10)]
    (socket->channel (:in conn) incoming deserializer (:command conn))
    (channel->socket outgoing (:out conn) serializer (:command conn))
    (monitor-for-cleanup conn incoming outgoing)
    [incoming outgoing]))
