[![Build Status](https://travis-ci.org/Spedge/hangar.svg?branch=master)](https://travis-ci.org/Spedge/hangar) [![Documentation Status](https://readthedocs.org/projects/hangar/badge/?version=latest)](http://hangar.readthedocs.io/en/latest/?badge=latest) 
[![](https://images.microbadger.com/badges/image/spedge/hangar-api.svg)](https://microbadger.com/images/spedge/hangar-api "* Get your own image badge on microbadger.com")



# hangar
After many, many years of dealing with expensive, monolithic artifact management solutions - I thought I'd give it a go myself. Can't be that hard, right?

https://xkcd.com/927/

# Linked Repositories

[Spedge/hangar-index](https://github.com/Spedge/hangar-index)
[Spedge/hangar-gui](https://github.com/Spedge/hangar-gui)

# Architecture

The idea is that this system is as stateless and distributed as possible, so super-fault tolerant. The artifacts are stored in S3 and the index in Zookeeper. Hangar API deals with the RESTful requests from maven/gradle and Hangar-GUI gives you a search/browse functionality feeding from the index.

Below is a typical installation of Hangar...

![Image of Yaktocat](./docs/images/hangar.PNG)

# Features

So far...

* NEW (Python) Basic pip proxy (no storage / cache yet)
* (Java) Allow S3-backed Storage
* (Java) Create zookeeper-based index
* (Java) Allow downloading of latest snapshot
* (Java) Allow upload of releases
* (Java) Will proxy artifacts from remote repositories, saving copy to the storage layer as it passes them on.
* (Java) In-memory index for quick identification of artifacts
* (Java) Will re-index from local storage using streams (super quick)
* (Java) Will accept snapshot artifacts to upload. 
* (Java) Allows separate storage for snapshots, releases and proxy data.

To go...

* (Java) Retention policies on a repo-by-repo basis.

# Run Me

You can run a very basic instance of Hangar by simply pulling the container from dockerhub and running it.

1. Run `docker pull spedge/hangar-api`
2. Run `docker run -p 8080:8080 spedge/hangar-api`

You should see the following...

    $ docker run --rm -p 8080:8080 spedge/hangar-api
    INFO  [2016-07-20 18:12:44,379] org.eclipse.jetty.util.log: Logging initialized @1374ms
    INFO  [2016-07-20 18:12:44,613] com.spedge.hangar.storage.Storage: 0 Artifacts Indexed under /data/java-snapshots in 97ms
    INFO  [2016-07-20 18:12:44,617] com.spedge.hangar.storage.Storage: 0 Artifacts Indexed under /data/java-releases in 0ms
    INFO  [2016-07-20 18:12:44,623] com.spedge.hangar.storage.Storage: 0 Artifacts Indexed under /data/java-proxy in 1ms
    INFO  [2016-07-20 18:12:44,625] io.dropwizard.server.ServerFactory: Starting hangar
    INFO  [2016-07-20 18:12:44,633] io.dropwizard.server.DefaultServerFactory: Registering jersey handler with root path prefix: /
    INFO  [2016-07-20 18:12:44,654] io.dropwizard.server.DefaultServerFactory: Registering admin handler with root path prefix: /
    INFO  [2016-07-20 18:12:44,719] org.eclipse.jetty.setuid.SetUIDListener: Opened application@338494fa{HTTP/1.1}{0.0.0.0:8080}
    INFO  [2016-07-20 18:12:44,733] org.eclipse.jetty.setuid.SetUIDListener: Opened admin@505a9d7c{HTTP/1.1}{0.0.0.0:8081}
    INFO  [2016-07-20 18:12:44,735] org.eclipse.jetty.server.Server: jetty-9.2.z-SNAPSHOT
    INFO  [2016-07-20 18:12:45,590] io.dropwizard.jersey.DropwizardResourceConfig: The following paths were found for the configured resources:

    GET     /java/releases/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\.)?(\w)*} (com.spedge.hangar.repo.java.api.JavaReleaseAPI)
    PUT     /java/releases/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\.)?(\w)*} (com.spedge.hangar.repo.java.api.JavaReleaseAPI)
    GET     /java/releases/{group : .+}/{artifact : .+}/{version : (?i)[\d\.]+}/{filename : [^/]+} (com.spedge.hangar.repo.java.api.JavaReleaseAPI)
    PUT     /java/releases/{group : .+}/{artifact : .+}/{version : (?i)[\d\.]+}/{filename : [^/]+} (com.spedge.hangar.repo.java.api.JavaReleaseAPI)
    GET     /java/snapshots/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\.)?(\w)*} (com.spedge.hangar.repo.java.api.JavaSnapshotAPI)
    PUT     /java/snapshots/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\.)?(\w)*} (com.spedge.hangar.repo.java.api.JavaSnapshotAPI)
    GET     /java/snapshots/{group : .+}/{artifact : .+}/{version : (?i)[\d\.]+-SNAPSHOT}/{filename: [^/]+} (com.spedge.hangar.repo.java.api.JavaSnapshotAPI)
    PUT     /java/snapshots/{group : .+}/{artifact : .+}/{version : (?i)[\d\.]+-SNAPSHOT}/{filename: [^/]+} (com.spedge.hangar.repo.java.api.JavaSnapshotAPI)
    GET     /java/snapshots/{group : .+}/{artifact : .+}/{version : ([\d\.]*\-SNAPSHOT)+}/maven-metadata.xml{type : (\.)?(\w)*} (com.spedge.hangar.repo.java.api.JavaSnapshotAPI)
    PUT     /java/snapshots/{group : .+}/{artifact : .+}/{version : ([\d\.]*\-SNAPSHOT)+}/maven-metadata.xml{type : (\.)?(\w)*} (com.spedge.hangar.repo.java.api.JavaSnapshotAPI)
    GET     /java/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\.)?(\w)*} (com.spedge.hangar.repo.java.api.JavaDownloadAPI)
    GET     /java/{group : .+}/{artifact : .+}/{version : .+}/{filename : [^/]+} (com.spedge.hangar.repo.java.api.JavaDownloadAPI)
    PUT     /test/headers (com.spedge.hangar.requests.TestRequest)

    INFO  [2016-07-20 18:12:45,604] org.eclipse.jetty.server.handler.ContextHandler: Started i.d.j.MutableServletContextHandler@60e5272{/,null,AVAILABLE}
    INFO  [2016-07-20 18:12:45,615] io.dropwizard.setup.AdminEnvironment: tasks =

    POST    /tasks/log-level (io.dropwizard.servlets.tasks.LogConfigurationTask)
    POST    /tasks/gc (io.dropwizard.servlets.tasks.GarbageCollectionTask)

    INFO  [2016-07-20 18:12:45,622] org.eclipse.jetty.server.handler.ContextHandler: Started i.d.j.MutableServletContextHandler@47d7bfb3{/,null,AVAILABLE}
    INFO  [2016-07-20 18:12:45,635] org.eclipse.jetty.server.ServerConnector: Started application@338494fa{HTTP/1.1}{0.0.0.0:8080}
    INFO  [2016-07-20 18:12:45,636] org.eclipse.jetty.server.ServerConnector: Started admin@505a9d7c{HTTP/1.1}{0.0.0.0:8081}
    INFO  [2016-07-20 18:12:45,637] org.eclipse.jetty.server.Server: Started @2632ms
