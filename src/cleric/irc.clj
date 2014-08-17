(ns cleric.irc
  (:require [clojure.string :as string]))

; "PING" to :ping
(def command->keyword
  (comp keyword string/lower-case))

; :ping to "PING"
(def keyword->command
  (comp string/upper-case name))

; irc line is ":optional-prefix COMMAND optional-parameter-list :optional-trailing"
; tricky bit is the negative-look-ahead (?!:) for when to stop matching the parameter list
; http://www.mybuddymichael.com/writings/a-regular-expression-for-irc-messages.html
(def irc-regex #"^(?:[:](\S+) )?(\S+)(?: (?!:)(.+?))?(?: [:](.+))?$")

(defn parse-message [line]
  (let [[[all prefix command & parameters]] (re-seq irc-regex line)]
    {:raw all
     :prefix prefix
     :command (command->keyword command)
     :params (drop-last parameters)
     :trailing (last parameters)}))

(defn serialize-message [{:keys [prefix command params trailing]}]
  (->> [(if prefix (str ":" prefix))
        (keyword->command command)
        params
        (if trailing (str ":" trailing))]
    (flatten)
    (filter (complement nil?))
    (interpose " ")
    (apply str)))

(defn pong [ping]
  (assoc ping :command :pong))

(defn nick [nick]
  {:command :nick
   :params nick})

(defn user [username realname]
  {:command :user
   :params [username 0 "*"]
   :trailing realname})

(defn join [channel]
  {:command :join
   :params channel})

(defn privmsg [request text]
  (assoc request
         :trailing text
         :prefix nil))
