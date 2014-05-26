(ns lastfmfetch.core
  "Fetches a chart from Last.fm for a given user, parsing the HTML, and pulling out artist info."
  (:require [clj-http.client :as client])
  (:import [java.io PrintStream]
           [org.htmlcleaner HtmlCleaner])
  (:gen-class))

(defn get-artist-and-playcount
  "Extract the artist name and play count from the HTML cell."
  [cell]
  (let [title (.getAttributeByName cell "title")
        [match artist playcount] (re-matches #"^(.+), played ([\w\d\,]+)\s*\S*$" title)
        playcountStr (if (= playcount "once") "1" playcount)]
    [artist playcountStr]))

(defn get-url
  "Pulls the href from the artist cell, appending it to the last.fm domain, to create a full URL."
  [cell]
  (let [links (.getElementsByName cell "a" true)
        a (first links)
        href (.getAttributeByName a "href")]
    (str "http://last.fm" href)))

(defn output-data
  "Take the data scraped from last.fm and write it out as an HTML list, with the
  artist name as a link."
  [data]
  (println "<html><head><meta charset=\"UTF-8\"/></head><body><ol>")
  (doseq [chunk data]
    (let [[artist playcount url] chunk]
      (println (str "<li><a href='" url "'>" artist "</a>, Plays: " playcount "</li>"))))
  (println "</ol></body></html>"))

(defn create-url
  "Creates the appropriate last.fm URL for the desired user, range, and chart type."
  [username rangetype subtype]
  (format "http://last.fm/user/%s/charts?rangetype=%s&subtype=%s" username rangetype subtype))

(defn fetch-data
  "Gets the HTML page from last.fm, cleans it, parses it, and extracts the five artist
  cells from the top of the list. These cells are then split up, and a list of
  [artist play-count url] vectors are returned."
  [url]
  (let [response (client/get url)
        status (:status response)
        cleaner (HtmlCleaner.)]
    (doto (.getProperties cleaner)
      (.setOmitComments true)
      (.setPruneTags "script,style"))
    (if (= status 200)
      (when-let [node (.clean cleaner (:body response))]
        (let [subject-cells (take 5 (.getElementsByAttValue node "class" "subjectCell" true true))]
          (for [cell subject-cells
                :let [[artist play-count] (get-artist-and-playcount cell)
                      artist-url (get-url cell)]]
            [artist play-count artist-url]))))))

;; Main
(defn -main [& args]
  (if (< (count args) 4)
    (println "Usage: lastfmfetch username rangetype subtype outputfile")
    (let [[username rangetype subtype output-file] args
          url (create-url username rangetype subtype)
          data (fetch-data url)]
      (binding [*out* (java.io.PrintWriter. output-file)]
        (output-data data)))))

