(ns cleric.plugins
  (:require [clojure.tools.namespace :as nstools]
            [clojure.java.io :as io]))

; holds our map of regex to command-fn
(def commands (atom {}))

(defn scan-for-plugins [plugins-dir]
  "Find clojure functions in `plugins-dir` that have a :match metadata regex,
  and create a map of that regex to the function"
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
  "Populate our `commands` atom with our command regex/function pairs"
  (reset! commands (scan-for-plugins plugin-dir)))

; given a map of regex->command and an input string
; returns a map from command->[input matches]
(defn run [input]
  "Given an input string, see if it matches any of the regexs.
  If it does, run the corresponding function on the input"
  (flatten ;flatten because multiple plugins can return multiple responses
    (for [[regex command] @commands
          :let [matches (flatten (re-seq regex input))]
          :when (not (empty? matches))] 
      ; TODO: validate arity of plugin-fn, give friendly error
      (apply command (rest matches)))))
