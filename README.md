# Immutant Feature Demo

An app showing trivial examples of all the Immutant libraries. Most
log something to stdout, but the `demo.web` examples are available at
<http://localhost:8080/>.

You can view a running example here:
<https://immutant-feature-demo.herokuapp.com/index.html>.

You need at least Java 7 and Leiningen 2.4.0

You can run the app in several ways:

* [With lein run](#with-lein-run)
* [At a repl](#at-a-repl)
* [From a jar](#from-a-jar)
* [In WildFly](#in-wildfly)
    * [In a cluster](#in-a-wildfly-cluster)
* [On Heroku](#on-heroku)
* [On OpenShift](#on-openshift)
    * [In a WildFly cluster](#in-a-wildfly-cluster-on-openshift)

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
    java -jar target/demo-standalone.jar :http2? false

## In WildFly

[WildFly](http://wildfly.org) is installed by downloading and
unpacking an archive. For our purposes, we'll drop it in the project
directory. For a list of available versions, see
<http://wildfly.org/downloads/>

    VERSION=9.0.0.Final

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

Press this button:

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

Or do it manually. [Heroku](http://heroku.com) requires a `Procfile` in the project 
root to bootstrap the app. With this in place, we simply create the heroku repo 
and push:

    heroku create
    git push heroku master

To see the log output:

    heroku logs --tail

And to open the app in the browser:

    heroku open

## On OpenShift

The app includes `start` and `stop` *action hooks* beneath the
`.openshift/` directory that enable it to be deployed on
[OpenShift](http://openshift.com) using the
[DIY cartridge](https://developers.openshift.com/en/diy-overview.html).

We'll call our application `demo`:

    rhc app-create demo diy --from-code https://github.com/immutant/feature-demo

To see the log output:

    cd demo
    rhc tail

Once the app is up, visit
`http://demo-<YOUR_DOMAIN>.rhcloud.com:8000/`. The port, 8000, is
optional for all but the WebSocket example, because OpenShift only
supports WebSockets on port 8000.

### In a WildFly cluster on OpenShift

We can use the
[WildFly cartridge](https://developers.openshift.com/en/wildfly-overview.html)
to create a
[scaled application](https://developers.openshift.com/en/overview-platform-features.html#scaling)
named `wf`. The `pre_deploy_wildfly` action hook will create our war
file in a directory monitored by the app server.

    rhc app-create wf wildfly --scaling --gear-size medium --from-code https://github.com/immutant/feature-demo

Note we set the `--scaling` option and a medium `--gear-size`. It will
take a few minutes for the command to complete. Once it does, monitor
the log output:

    cd wf
    rhc tail

View the web examples at `http://wf-<YOUR_DOMAIN>.rhcloud.com:8000/`

Try scaling the app up to 2 gears:

    rhc cartridge-scale wildfly 2

View the gears for your app to obtain their ssh URL's:

    rhc app-show --gears

Login to a gear to monitor/control it:

    rhc ssh
    help
    gear restart
    tail_all
