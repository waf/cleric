(defproject cleric "0.1.0-SNAPSHOT"
            :description "IRC bot that can dynamically add modules based on twitter sources"
            :url "https://github.com/waf/cleric"
            :main cleric.core
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/core.async "0.1.319.0-6b1aca-alpha"]
                           [org.clojure/data.json "0.2.5"]
                           [org.clojure/tools.namespace "0.2.5"]

                           ; logging setup :(
                           [org.clojure/tools.logging "0.3.0"]
                           [ch.qos.logback/logback-classic "1.1.2" :exclusions [org.slf4j/slf4j-api]]
                           [org.slf4j/jcl-over-slf4j "1.7.7"] ; redirects commons-logging to slf4j

                           [clj-http "1.0.0" :exclusions [crouton ; disable html parsing support
                                                          cheshire ; disable edn support
                                                          commons-logging ; we'll use slf4j/logback
                                                          com.cognitect/transit-clj]] ; disable transit serialization
                           [twitter-api "0.7.5" :exclusions [clj-http]] ; twitter-api relies on a very old clj-http
                           ])
