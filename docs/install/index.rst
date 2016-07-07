Installation
============

Each team, squad, mob will have a different way of wanting to approach artifact management - and so they should! It's a wonderful diverse world after all! 

With this ethos in mind, there are a variety of ways of running Hangar. 

* All Local

In this format, we have a single instance with local storage for artifacts and an in-memory index. Handy for debugging or just giving it a try.

* S3 Local

In this format the index is still held in memory, but the storage of artifacts is in S3.

* S3 Fully Distributed

In this format, we have multiple API hosts with a shared zookeeper based index and S3 storage.