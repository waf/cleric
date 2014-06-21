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

(defn track-format [track]
  (let [title (:name track)
        artist (get-in track [:artist :#text])]
    (if (get-in track [(keyword "@attr") :nowplaying])
      (str "Currently playing " title " by " artist)
      (str "Nothing playing. Last played " title " by " artist 
           " on " (get-in track [:date :#text])))))

; last.fm returns either a vector if there are multiple 
; songs, or a single element if there is only one song. 
(defn unwrap-vector [element]
  (if (vector? element) 
    (first element)
    element))

(defn users-most-recent-track [username]
  (let [api-url (get-recent-track-url username)
        response @(http/get api-url)
        {:keys [status headers body error]} response]
    (if error ; handle http errors
      (str "Error: " error)
      (let [api-response (json/read-str body :key-fn keyword)]
        (if (:error api-response) ; handle api errors
          (str "Error: " (:message api-response))
          (track-format (-> api-response
                            :recenttracks
                            :track
                            unwrap-vector)))))))

(defn now-playing-plugin [bot]
  (-> bot
      (plugin #"!playing (?<id>[0-9a-zA-Z]+)" #(users-most-recent-track %))))
