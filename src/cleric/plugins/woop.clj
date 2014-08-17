(ns cleric.plugins.woop
  (require [clojure.string :refer (join)]))

(defn- woop [n]
  (let [integer (float (int n))
        bounded (->> integer (max 1) (min 20))
        fraction (-> n (- integer) (* 4) (Math/round))]
    (str 
      (join " " (repeat bounded "WOOP"))
      " "
      (.substring "WOOP" 0 fraction))))

(defn woop-10
  "WOOPs 10 times"
  {:match #"^!woop$"} 
  []
  (woop 10))

(defn woooop
  "WOOPs once for each 'o'"
  {:match #"^!w(o{3,})p$"} 
  [os]
  (woop (count os)))

(defn woop-n
  "WOOPs n times"
  {:match #"^!woo+p ([0-9]+\.?[0-9]*)$"} 
  [n]
  (woop (Float/parseFloat n)))
