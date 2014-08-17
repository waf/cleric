(ns cleric.plugins
  (:require [clojure.tools.namespace :as nstools]
            [clojure.java.io :as io]))

(def plugins (atom {}))

(defn scan-for-plugins [plugins-dir]
  (let [get-publics-in-ns #(do (require %) (ns-publics %))
        get-publics-in-dir #(->> %
                              (io/file) 
                              (nstools/find-namespaces-in-dir)
                              (mapcat (comp vals get-publics-in-ns)))]
    (into {}
          (for [public (get-publics-in-dir plugins-dir)
                :let [pattern (:match (meta public))]
                :when pattern]
            [(re-pattern pattern) public]))))

(defn load-from-disk [plugin-dir]
  (reset! plugins (scan-for-plugins plugin-dir)))

; given a map of regex->plugin and an input string
; returns a map from plugin->[input matches]
(defn run [input]
  (for [[regex plugin-fn] @plugins
        :let [matches (flatten (re-seq regex input))]
        :when (not (empty? matches))] 
    ; TODO: validate arity of plugin-fn, give friendly error
    (apply plugin-fn (rest matches))))
