(ns cleric.plugins.spotify
  (:require [clj-http.client :as http]
            [clojure.data.json :as json]))

(defn track-format [track]
  (str (:name track)
       " by "
       (get-in track [:artists 0 :name])))

(defn track-lookup 
  {:match #"http://open.spotify.com/track/(?<id>[0-9a-zA-Z]+)"}
  [id]
  (let [api-url (str "https://api.spotify.com/v1/tracks/" id)
        response (http/get api-url)
        track-info (json/read-str (:body response)
                                  :key-fn keyword)]
    (track-format track-info)))
