(ns cleric.plugins.help
  (:require [cleric.plugins :as plug]
            [clojure.string :as string]))

(defn- command-to-plugin-name [cmd]
  "Given a command function, get the plugin (clojure module) in which it resides"
  (-> cmd
    (meta)
    (:ns)
    (ns-name)
    (str)
    (string/split #"\.")
    (last)))

(defn- format-command [cmd]
  "Given a command function, print its metadata"
  (let [{:keys [name doc match]} (meta cmd)]
    (str name ": " doc " - " match)))

(defn list-plugins
  "Display all currently active plugins"
  {:match #"^!help$"}
  []
  (->> @plug/commands
    (vals)
    (map command-to-plugin-name)
    (distinct)
    (sort)
    (string/join ", ")))

(defn list-plugin-commands
  "Display help about a specific plugin"
  {:match #"^!help (.*)$"}
  [plugin-name]
  (let [commands-in-plugin #(= plugin-name (command-to-plugin-name %))]
    (->> @plug/commands
      (vals)
      (filter commands-in-plugin)
      (map format-command))))
