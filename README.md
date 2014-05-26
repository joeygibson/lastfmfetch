# lastfmfetch

Scrapes a chart from [Last.fm](http://last.fm) for a given user, for a given timespan.
It pulls out the top five results, HTML-ifies them, and writes it out to a file.

I wrote this because, at the time, the Last.fm API was not returning the same results as I was
seeing on the chart pages on their site. I decided to just scrape the HTML, and this is the result.

**Note**: There is no error checking, so be aware of that.
## Usage

lastfmfetch username rangetype subtype output-file

## License

Copyright (C) 2011-2014 [Joey Gibson](mailto:joey@joeygibson.com)

Distributed under the Eclipse Public License, the same as Clojure.
