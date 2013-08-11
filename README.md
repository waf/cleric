# cleric

An IRC bot that can dynamically add modules based on twitter sources.

## Configuration

`resources/cleric.properties` is the main bot configuration file.

As this bot interacts with Twitter, you'll need Twitter API credentials. 
Register with [the Twitter API](https://dev.twitter.com/apps/new) and put the 
provided credentials in `resources/twitter.properties`.

## Running

This bot is written in clojure and uses [leiningen](https://github.com/technomancy/leiningen). 
To start the bot, install leiningen and run:

```
$ lein run
```

or

```
$ lein uberjar
$ java -jar target/cleric-*-STANDALONE.jar
```

## Usage

Once the bot joins your channel, use

```
!register <command> <mode> <twitter_username>
```

Where `<mode>` is either 'random' or 'latest'. This will create a command 
of the form `+<command>` that will retrieve either the latest tweet or a 
random tweet from the twitter account `<twitter_username>`

To delete a command, run `!deregister <command>`

Example:

```
!register carrot random RealCarrotFacts
+carrot
!deregister carrot
```

## License

Distributed under the Eclipse Public License, the same as Clojure.
