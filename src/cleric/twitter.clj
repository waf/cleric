(ns cleric.twitter
  (require [cleric.common :refer :all]
           [clojure.string :refer [join]]
           [cleric.store :as store]
           [clojure.tools.logging :as log]
           [twitter.api.restful :as api]
           [twitter.oauth :as oauth]))

(def props (load-properties "resources/twitter.properties"))

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
  (rand-nth (@store/tweets username)))

; add the twitter user (source) to our sources store
; if mode is random, download the user's tweets into our tweets store
(defn register [command mode source]
  (do
    (if (= "random" mode) 
      (store/add-tweets source (get-tweets-for-user source 200)))
    (store/add-source command mode source)
    (str "new command registered: +" command)))

(defn deregister [command]
  (store/remove-source command)
  (str "deleted command +" command))

(defn run [command]
  (when-let [handler (@store/sources command)]
    (let [mode (first handler)
          source (second handler)]
      (case mode
        "latest" (get-latest-tweet source)
        "random" (get-random-tweet source)))))

(def register-plugin (plugin #"!register (\w+) (\w+) (\w+)" register))
(def deregister-plugin (plugin #"!deregister (.+)" deregister))
(def run-plugin (plugin #"\+(\w+)" run))
