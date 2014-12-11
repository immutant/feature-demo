# Immutant Feature Demo

An app showing trivial examples of all the Immutant libraries. Most
log something to stdout, but the `demo.web` examples are available at
<http://localhost:8080/>.

You need at least Java 7 and Leiningen 2.4.0

You can run the app in several ways:

* [With lein run](#with-lein-run)
* [At a repl](#at-a-repl)
* [From a jar](#from-a-jar)
* [In WildFly](#in-wildfly)
    * [In a WildFly cluster](#in-a-wildfly-cluster)
* [On Heroku](#on-heroku)

## With lein run

The value of `:main` in `project.clj` is `demo.core`, which runs
`-main` for all of the demo namespaces.

    lein run

You can use the -m option to run specific namespaces, e.g.

    lein run -m demo.web

## At a repl

You can fire up a repl and invoke each namespace directly

    lein repl

Once at a prompt, try `(demo.web/-main)`

## From a jar

Create an uberjar and run it

    lein uberjar
    java -jar target/demo-0.2.0-SNAPSHOT-standalone.jar 

## In WildFly

[WildFly](http://wildfly.org) is installed by downloading and
unpacking an archive. For our purposes, we'll drop it in the project
directory. For a list of available versions, see
<http://wildfly.org/downloads/>

    VERSION=8.2.0.Final

    # Install WildFly
    wget http://download.jboss.org/wildfly/$VERSION/wildfly-$VERSION.zip
    unzip wildfly-$VERSION.zip

    # Create the war file and deploy it to WildFly
    lein immutant war -o wildfly-$VERSION

    # Fire up WildFly
    wildfly-$VERSION/bin/standalone.sh -c standalone-full.xml

Note the web examples will be deployed with a context path of `/demo`
on WildFly so go to <http://localhost:8080/demo/> to see the web
examples. Alternatively, to mount the app at the root context,
<http://localhost:8080/>, rename the war file beneath
`wildfly-$VERSION/standalone/deployments/` to `ROOT.war`.

### In a WildFly cluster

We'll simulate a cluster by "installing" another WildFly instance:

    cp -R wildfly-$VERSION wildfly-too
    rm -rf wildfly-too/standalone/data/

Because we already deployed the war file, it gets copied over, too.
And to avoid spurious errors, we remove the `standalone/data`
directory where WildFly keeps some runtime state.

Now we'll start our first instance:

    wildfly-$VERSION/bin/standalone.sh -c standalone-full-ha.xml -Djboss.node.name=one -Djboss.messaging.cluster.password=demo

Note the following:

* We use the `standalone-full-ha.xml` file in which clustering is
  configured
* Since both of our peers will be on the same host, we need to
  give each node a unique name
* The cluster requires a password

In another shell, we fire up the second instance with similar options
plus a system property to avoid port conflicts, since we're on the
same host.

    wildfly-too/bin/standalone.sh -c standalone-full-ha.xml -Djboss.node.name=two -Djboss.messaging.cluster.password=demo -Djboss.socket.binding.port-offset=100

You can correlate the output from both peers to the code beneath
`src/demo` to observe HA singleton jobs, load-balanced messaging, and
distributed caching. And you can observe session replication by
reloading the following pages in your browser:

* <http://localhost:8080/demo/counter>
* <http://localhost:8180/demo/counter>

## On Heroku

[Heroku](http://heroku.com) requires two extra files in the project
root: a `Procfile` to bootstrap the app, and a `system.properties` to
specify Java version 1.7. With those in place, we simply create the
heroku repo and push:

    heroku create
    git push heroku thedeuce:master

To see the log output:

    heroku logs --tail

And to open the app in the browser:

    heroku open
