(ns cleric.plugins.twitter
  (:require 
    [clojure.string :refer [join]]
    [cleric.store :as store]
    [cleric.config :as cfg]
    [twitter.api.restful :as api]
    [twitter.oauth :as oauth]))

(def props (cfg/load-properties "resources/twitter.properties"))

(def creds (oauth/make-oauth-creds (:app-consumer-key props)
                                   (:app-consumer-secret props)
                                   (:user-access-token props)
                                   (:user-access-token-secret props)))

(defn- get-tweets-for-user [user n]
  (let [request {:screen-name user
                 :count n
                 :trim-user "true"
                 :exclude-replies "true"
                 :include-rts "false"}
        response (api/statuses-user-timeline :oauth-creds creds
                                             :params request)]
    (map :text (:body response))))

(defn- get-latest-tweet [username]
  (first (get-tweets-for-user username 1)))

(defn- get-random-tweet [username]
  (rand-nth ((store/get-val "tweets") username)))

(defn- refresh-tweet-store [source]
  (store/put-val 
    "tweets" 
    (assoc-in (store/get-val "tweets")
              [source]
              (get-tweets-for-user source 200))))

; add the twitter user (source) to our sources store
; if mode is random, download the user's tweets into our tweets store
(defn register-command
  "Add a twitter command. e.g. !register <cmd> [latest|random] <account>"
  {:match #"^!register (\w+) (\w+) (\w+)$"}
  [command mode source]
  (do
    (if (= "random" mode) (refresh-tweet-store source))
    (store/put-val 
      "commands"
      (assoc-in (store/get-val "commands")
                [command]
                [mode source]))
    (str "new command registered: +" command)))

(defn deregister-command
  "Remove a twitter command"
  {:match #"!deregister (.+)"}
  [command]
  (store/put-val
    "commands"
    (dissoc (store/get-val "commands") command))
  (str "deleted command +" command))

(defn run-command
  "Run a twitter command"
  {:match #"^\+(\w+)$"}
  [command]
  (when-let [handler ((store/get-val "commands") command)]
    (let [mode (first handler)
          source (second handler)]
      (case mode
        "latest" (get-latest-tweet source)
        "random" (do
                    (if (< (rand) 0.2) (future (refresh-tweet-store source)))
                    (get-random-tweet source))))))

(defn list-commands
  "Lists all registered twitter commands"
  {:match #"!list"}
  []
  (let [command-format (fn [cmd] (str "+" (first cmd) ":" (second (second cmd))))
        commands (seq (store/get-val "commands"))]
  (join " " (map command-format commands))))
