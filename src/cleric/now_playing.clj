(ns cleric.now-playing
  (:require [cleric.common :refer :all]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(def props (load-properties "resources/lastfm.properties"))

(defn get-recent-track-url [username]
  (str 
    "http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks"
    "&limit=1"
    "&format=json"
    "&user=" username
    "&api_key=" (:api-key props)))

(defn track-format [{{track :track} :recenttracks}]
  (print track)
  (str (:name track)
       " by "
       (get-in track [:artist :#text])))

(defn users-most-recent-track [username]
  (let [api-url (get-recent-track-url username)
        response @(http/get api-url)
        {:keys [status headers body error]} response]
    (if error
      (str "Error: " error)
      (track-format (json/read-str body :key-fn keyword)))))

(defn now-playing-plugin [bot]
  (-> bot
      (plugin #"!playing (?<id>[0-9a-zA-Z]+)" #(users-most-recent-track %))))
