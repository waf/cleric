(ns cleric.plugins.woop
  (require [clojure.string :refer (join blank?)]
           [cleric.color :refer (color)]))

(defn- woop [n]
  (let [integer (int n)
        bounded (->> integer (max 0) (min 20))
        fraction (-> n (- integer) (* 4.0) (Math/round))
        woops (conj (vec (repeat bounded "WOOP"))
                    (.substring "WOOP" 0 fraction))]
    (str (color :fg :red :bg :blue)
         (join (color) woops))))

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
