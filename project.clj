(defproject cleric "0.1.0-SNAPSHOT"
  :description "IRC bot that can dynamically add modules based on twitter sources"
  :url "https://github.com/waf/cleric"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main cleric.core
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.logging "0.2.6"]
                 [org.slf4j/slf4j-log4j12 "1.7.5"]
                 [cc.qbits/ash "0.2.7"]
                 [twitter-api "0.7.5"]])
