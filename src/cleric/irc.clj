(ns cleric.irc
  (:require [clojure.string :as string]))

(def command->keyword
  "IRC command string to keyword, e.g. \"PING\" to :ping"
  (comp keyword string/lower-case))

(def keyword->command
  "keyword to an IRC command string, e.g. :ping to \"PING\""
  (comp string/upper-case name))

; irc line is ":optional-prefix COMMAND optional-parameter-list :optional-trailing"
; tricky bit is the negative-look-ahead (?!:) for when to stop matching the parameter list
; http://www.mybuddymichael.com/writings/a-regular-expression-for-irc-messages.html
(def irc-regex #"^(?:[:](\S+) )?(\S+)(?: (?!:)(.+?))?(?: [:](.+))?$")

(defn parse-message 
  "Parse a string IRC line into a map"
  [line]
  (let [[[all prefix command & parameters]] (re-seq irc-regex line)]
    {:raw all
     :prefix prefix
     :command (command->keyword command)
     :params (drop-last parameters)
     :trailing (last parameters)}))

(defn serialize-message 
  "Serialize a map into a string IRC line"
  [{:keys [prefix command params trailing]}]
  (->> [(if prefix (str ":" prefix))
        (keyword->command command)
        params
        (if trailing (str ":" trailing))]
    (flatten)
    (filter (complement nil?))
    (interpose " ")
    (apply str)))

(defn pong [ping]
  "Given a PING IRC message, PONG it"
  (assoc ping :command :pong))

(defn nick [nick]
  "IRC NICK command"
  {:command :nick
   :params nick})

(defn user [username realname]
  "IRC USER command"
  {:command :user
   :params [username 0 "*"]
   :trailing realname})

(defn join [channel]
  "IRC JOIN command for a single channel"
  {:command :join
   :params channel})

(defn privmsg [request text]
  "IRC PRIVMSG command, to be used in response to another PRIVMSG"
  (assoc request
         :trailing text
         :prefix nil))
