(ns cleric.response_tests
  (:require [clojure.test :refer :all]
            [cleric.plugins :as plugins]
            [cleric.responses :refer :all]))

(deftest 
  test-pong
  (is (= [:pong]
         (map :command
              (response {:command :ping
                         :prefix "domain.example.com"})))))

(deftest 
  test-unknown
  (is (= []
         (response {:command nil}))))

(defn create-privmsg [text]
  {:command :privmsg
   :trailing text
   :params ["#channel"]
   :prefix nil})

(deftest 
  test-single-messages
  (with-redefs-fn {#'plugins/run (fn [text] ["response text"])}
                  #(is (= [(create-privmsg "response text")]
                          (response (create-privmsg "request"))))))

(deftest 
  test-multiple-messages
  (with-redefs-fn {#'plugins/run (fn [text] ["response text" 
                                             "another response"])}
                  #(is (= [(create-privmsg "response text")
                           (create-privmsg "another response")]
                          (response (create-privmsg "request"))))))

(deftest 
  test-multiple-messages-with-newlines
  (with-redefs-fn {#'plugins/run (fn [text] ["response text" 
                                             "with \n newline"
                                             "and \r\n another \r\n one"])}
                  #(is (= [(create-privmsg "response text")
                           (create-privmsg "with ")
                           (create-privmsg " newline")
                           (create-privmsg "and ")
                           (create-privmsg " another ")
                           (create-privmsg " one")]
                          (response (create-privmsg "request"))))))
