# Immutant feature demo

An app showing trivial examples of all the Immutant libraries.

You can run the app in several ways:

### run

The value of `:main` in `project.clj` is `demo.core`, which runs
`-main` for all of the demo namespaces.

    $ lein run

You can use the -m option to run specific namespaces, e.g.

    $ lein run -m demo.web

### repl

You can fire up a repl and invoke each namespace directly

    $ lein repl

Once at a prompt, try `(demo.core/-main)`

### jar

Create an uberjar and run it

    $ lein uberjar
    $ java -jar target/demo-0.2.0-SNAPSHOT-standalone.jar 

### wildfly

    $ wget http://download.jboss.org/wildfly/8.1.0.Final/wildfly-8.1.0.Final.zip
    $ unzip wildfly-8.1.0.Final.zip
    $ lein immutant war -o wildfly-8.1.0.Final
    $ wildfly-8.1.0.Final/bin/standalone.sh -c standalone-full.xml

Normally, you would unzip WildFly to a more "official" directory,
say `/srv/wildfly`.

Note the web examples will be deployed with a context path of `/demo`.
