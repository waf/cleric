# cleric

IRC bot that can dynamically add modules based on twitter sources

## Running

If you want to run your own instance, register with [the Twitter API](https://dev.twitter.com/apps/new)
to get API keys. Put these API keys in resources/twitter.properties.

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

Edit core.clj to set bot information. Once the bot joins your channel, use

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
