(defproject lastfmfetch "1.0.0-SNAPSHOT"
  :description "Fetch chart data from Last.fm"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [net.sourceforge.htmlcleaner/htmlcleaner "2.2"]
                 [org.apache.commons/commons-lang3 "3.1"]
                 [clj-http "0.9.1"]]
  ;; :shell-wrapper true
  :main lastfmfetch.core)
