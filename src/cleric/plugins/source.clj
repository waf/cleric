(ns cleric.plugins.source
  (:require [cleric.plugins :as plug]
            [clojure.string :as string]))

(defn- githubbify [{:keys [file line]}]
  (format "https://github.com/waf/cleric/blob/master/src/%s#L%d"
          file 
          line))

; wheeee metadata
(defn github-source
  "Returns a hyperlink to the command's source on github.com"
  {:match #"^!source (.*)$"}
  [command-name]
  (let [matching-commands #(= (:name %) (symbol command-name))]
    (->> @plug/commands
      (vals)
      (map meta)
      (filter matching-commands)
      (map githubbify))))
