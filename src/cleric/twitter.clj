(ns cleric.twitter
  (require [cleric.common :refer :all]
           [qbits.ash.store :as store]
           [clojure.tools.logging :as log]
           [twitter.api.restful :as api]
           [twitter.oauth :as oauth]))

(def props (load-properties "resources/twitter.properties"))
(defonce sources (.getTreeMap store/db "sources"))
(defonce tweets (.getTreeMap store/db "tweets"))

(def creds (oauth/make-oauth-creds (:app-consumer-key props)
                                   (:app-consumer-secret props)
                                   (:user-access-token props)
                                   (:user-access-token-secret props)))

(defn get-tweets-for-user [user n]
  (let [request {:screen-name user
                 :count n
                 :trim-user "true"
                 :exclude-replies "true"
                 :include-rts "false"}
        response (api/statuses-user-timeline :oauth-creds creds
                                             :params request)]
    (map :text (:body response))))

(defn get-latest-tweet [username]
  (first (get-tweets-for-user username 1)))

(defn get-random-tweet [username]
  (rand-nth (get tweets username)))

; add the twitter user (source) to our sources store
; if mode is random, download the user's tweets into our tweets store
(defn register [command mode source]
  (do 
    (if (= "random" mode) 
      (store/put! tweets source (get-tweets-for-user source 200)))
    (store/put! sources command [mode source])
    (str "new command registered: +" command)))

(defn deregister [command]
  (store/del! sources command)
  (str "deleted command +" command))

(defn run [command]
  (when-let [handler (get sources command)]
    (let [mode (first handler)
          source (second handler)]
      (case mode
        "latest" (get-latest-tweet source)
        "random" (get-random-tweet source)))))

(def register-plugin (plugin #"!register (\w+) (\w+) (\w+)" register))
(def deregister-plugin (plugin #"!deregister (.+)" deregister))
(def run-plugin (plugin #"\+(\w+)" run))
