(ns cleric.woop
  (require [cleric.common :refer :all]
           [clojure.string :refer (join)]))

(defn woop [n]
  (let [integer (float (int n))
        bounded (->> integer (max 1) (min 20))
        fraction (-> n (- integer) (* 4) (Math/round))]
    (str 
      (join " " (repeat bounded "WOOP"))
      " "
      (.substring "WOOP" 0 fraction))))

(defn woop-plugin [bot]
  (-> bot
      (plugin #"^!woop$" #(woop 10))
      (plugin #"^!woop (?<num>[0-9]+\.?[0-9]*)$" #(woop (Float/parseFloat %)))
      (plugin #"^!woo(?<num>o+)p$" #(woop (count %)))
      (plugin #"^!wooo+p (?<num>[0-9]+\.?[0-9]*)$" #(woop (Float/parseFloat %)))))

