(ns cleric.plugins.reload
  (:require [cleric.plugins :as plug]))

(defn reload-plugins
  "Returns a hyperlink to the command's source on github.com"
  {:match #"^!reload$"}
  []
  (plug/load-from-disk "src/cleric/plugins")
  "plugins reloaded")
