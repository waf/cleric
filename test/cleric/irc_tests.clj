(ns cleric.irc_tests
  (:require [clojure.test :refer :all]
            [cleric.irc :refer :all]))

(def privmsg-line
  ":nick!~username@domain.example.com PRIVMSG #channel :message content")

(def privmsg-parsed
  {:raw privmsg-line
   :prefix "nick!~username@domain.example.com"
   :command :privmsg
   :params ["#channel"]
   :trailing "message content"})


(deftest test-parser
         (is (= privmsg-parsed
                (parse-message privmsg-line))))

(deftest test-serializer
         (is (= privmsg-line
                (serialize-message privmsg-parsed))))


(deftest test-pongs-a-ping
         (let [ping {:command :ping
                     :prefix "domain.example.com"}]
           (is (= {:command :pong
                   :prefix (:prefix ping)}
                  (pong ping)))))

(deftest test-join
         (is (= {:command :join
                 :params "#channel"}
                (join "#channel"))))


