(ns cleric.plugins.help
  (:require [cleric.plugins :as plug]
            [clojure.string :as string]))

(defn plugin-list
  "Display all currently active plugins"
  {:match #"^!help$"}
  []
  (let [fn-name (comp :name meta)]
    (string/join "," (map fn-name (vals @plug/plugins)))))

(defn plugin-help
  "Display help about a specific plugin"
  {:match #"^!help (.*)$"}
  [plugin-name]
  (let [matching-plugins #(= (:name %) (symbol plugin-name))]
    (->> @plug/plugins
      (vals)
      (map meta)
      (filter matching-plugins)
      (map :doc)
      (string/join ","))))
