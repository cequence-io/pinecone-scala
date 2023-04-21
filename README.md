# Pinecone Scala Client 🗄️
[![version](https://img.shields.io/badge/version-0.0.1-green.svg)](https://cequence.io) [![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](https://opensource.org/licenses/MIT) ![GitHub Stars](https://img.shields.io/github/stars/cequence-io/pinecone-scala?style=social) [![Twitter Follow](https://img.shields.io/twitter/follow/0xbnd?style=social)](https://twitter.com/0xbnd)

This is a no-nonsense async Scala client for Pinecone API supporting all the available vector and index/collection operations/endpoints, provided in two convenient services called [PineconeVectorService](./pinecone-core/src/main/scala/io/cequence/pineconescala/service/PineconeVectorService.scala) and [PineconeIndexService](./pinecone-core/src/main/scala/io/cequence/pineconescala/service/PineconeIndexService.scala). The supported calls are: 

* **Vector Operations**: [describeIndexStats](https://docs.pinecone.io/reference/describe_index_stats_post), [query](https://docs.pinecone.io/reference/query), [delete](https://docs.pinecone.io/reference/delete_post), [fetch](https://docs.pinecone.io/reference/fetch), [update](https://docs.pinecone.io/reference/update), and [upsert](https://docs.pinecone.io/reference/upsert)
* **Collection Operations**: [listCollections](https://docs.pinecone.io/reference/list_collections), [createCollection](https://docs.pinecone.io/reference/create_collection), [describeCollection](https://docs.pinecone.io/reference/describe_collection), and [delete_collection](https://docs.pinecone.io/reference/delete_collection)
* **Index Operations**: [listIndexes](https://docs.pinecone.io/reference/list_indexes), [creatIndex](https://docs.pinecone.io/reference/create_index), [describeIndex](https://docs.pinecone.io/reference/describe_index), [deleteIndex](https://docs.pinecone.io/reference/delete_index), and [configureIndex](https://docs.pinecone.io/reference/configure_index)

Note that in order to be consistent with the Pinecone API naming, the service function names match exactly the API endpoint titles/descriptions with camelcase.
Also, we aimed the lib to be self-contained with the fewest dependencies possible therefore we ended up using only two libs `play-ahc-ws-standalone` and `play-ws-standalone-json` (at the top level). Additionally, if dependency injection is required we use `scala-guice` lib as well.  

**✔️ Important**: this is a "community-maintained" library and, as such, has no relation to Pinecone company.

## Installation 🚀

The currently supported Scala versions are **2.12, 2.13**, and **3**. Note that an optional module `pinecone-scala-guice` is available only for Scala 2.12 and 2.13.  

To pull the library you have to add the following dependency to your *build.sbt*

```
"io.cequence" %% "pinecone-scala-client" % "0.0.1"
```

or to *pom.xml* (if you use maven)

```
<dependency>
    <groupId>io.cequence</groupId>
    <artifactId>pinecone-scala-client_2.12</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Config ⚙️

- Env. variables: `PINECONE_SCALA_CLIENT_API_KEY`, and `PINECONE_SCALA_CLIENT_ENV`
- File config (default):  [pinecone-scala-client.conf](./pinecone-client/src/main/resources/pinecone-scala-client.conf)

## Usage 👨‍🎓

**I. Obtaining `PineconeVectorService` and `PineconeIndexService`**

First you need to provide an implicit execution context as well as akka materializer, e.g., as

```scala
  implicit val ec = ExecutionContext.global
  implicit val materializer = Materializer(ActorSystem())
```

Then you can obtain a service in one of the following ways.

- Default config (expects env. variable(s) to be set as defined in `Config` section)
```scala
  val service = PineconeVectorServiceFactory("index_name")
```

or

```scala
  val service = PineconeIndexServiceFactory()
```

- Custom config
```scala
  val config = ConfigFactory.load("path_to_my_custom_config")
  val service = PineconeVectorServiceFactory("index_name", config)
```

or

```scala
  val config = ConfigFactory.load("path_to_my_custom_config")
  val service = PineconeIndexServiceFactory(config)
```

- Without config

```scala
  val service = PineconeVectorServiceFactory(
     apiKey = "your_api_key",
     environment = "your_env", // e.g. "northamerica-northeast1-gcp
     indexName = "index_name", // e.g. "auto-gpt-xxxxxxx"
  )
```

or 

```scala
  val service = PineconeIndexServiceFactory(
     apiKey = "your_api_key",
     environment = "your_env" // e.g. "northamerica-northeast1-gcp
  )
```

**II. Calling functions**

Full documentation of each call with its respective inputs and settings is provided in [PineconeVectorService](./pinecone-core/src/main/scala/io/cequence/pineconescala/service/PineconeVectorService.scala) and [PineconeIndexService](./pinecone-core/src/main/scala/io/cequence/pineconescala/service/PineconeIndexService.scala). Since all the calls are async they return responses wrapped in `Future`.

Examples:

- List collections

```scala
  service.listCollections.map(models =>
    models.foreach(println)
  )
```

TODO

## FAQ 🤔

1. I got a timeout exception. How can I change the timeout setting?_

   You can do it either by passing the `timeouts` param to `PineconeServiceFactory` or, if you use your own configuration file, then you can simply add it there as: 

```
pinecone-scala-client {
    timeouts {
        requestTimeoutSec = 200
        readTimeoutSec = 200
        connectTimeoutSec = 5
        pooledConnectionIdleTimeoutSec = 60
    }
}
```

2. I got an exception like `com.typesafe.config.ConfigException$UnresolvedSubstitution: pinecone-scala-client.conf @ jar:file:.../io/cequence/pinecone-scala-client_2.13/0.0.1/pinecone-scala-client_2.13-0.0.1.jar!/pinecone-scala-client.conf: 4: Could not resolve substitution to a value: ${PINECONE_SCALA_CLIENT_API_KEY}`. What should I do?_

   Set the env. variable `PINECONE_SCALA_CLIENT_API_KEY`. If you don't have one register [here](https://beta.pinecone.com/signup).


3_It all looks cool. I want to chat with you about your research and development?_

   Just shoot us an email at [pinecone-scala-client@cequence.io](mailto:pinecone-scala-client@cequence.io?subject=Research%20andDevelopment).

## License ⚖️

This library is available and published as open source under the terms of the [MIT License](https://opensource.org/licenses/MIT).

## Contributors 🙏

This project is open-source and welcomes any contribution or feedback ([here](https://github.com/cequence-io/pinecone-scala-client/issues)).

Development of this library has been supported by  [<img src="https://cequence.io/favicon-16x16.png"> - Cequence.io](https://cequence.io) - `The future of contracting` 

Created and maintained by [Peter Banda](https://peterbanda.net).