(ns cleric.spotify
  (:require [cleric.common :refer :all]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(defn track-format [track]
  (str (:name track)
       " by "
       (get-in track [:artists 0 :name])))

(defn track-lookup [id]
  (let [api-url (str "https://api.spotify.com/v1/tracks/" id)
        response @(http/get api-url)
        {:keys [status headers body error]} response]
    (if error
      (str "Error: " error)
      (track-format (json/read-str body :key-fn keyword)))))

(defn spotify-plugin [bot]
  (-> bot
      (plugin #"http://open.spotify.com/track/(?<id>[0-9a-zA-Z]+)" #(track-lookup %))))
