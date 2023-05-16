# Pinecone Scala Client 🗂️
[![version](https://img.shields.io/badge/version-0.1.0-green.svg)](https://cequence.io) [![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](https://opensource.org/licenses/MIT) ![GitHub Stars](https://img.shields.io/github/stars/cequence-io/pinecone-scala?style=social) [![Twitter Follow](https://img.shields.io/twitter/follow/0xbnd?style=social)](https://twitter.com/0xbnd)

This is an intuitive async Scala client for Pinecone API supporting all the available vector and index/collection operations/endpoints, provided in two convenient services called [PineconeVectorService](./pinecone-core/src/main/scala/io/cequence/pineconescala/service/PineconeVectorService.scala) and [PineconeIndexService](./pinecone-core/src/main/scala/io/cequence/pineconescala/service/PineconeIndexService.scala). The supported calls are: 

* **Vector Operations**: [describeIndexStats](https://docs.pinecone.io/reference/describe_index_stats_post), [query](https://docs.pinecone.io/reference/query), [delete](https://docs.pinecone.io/reference/delete_post), [fetch](https://docs.pinecone.io/reference/fetch), [update](https://docs.pinecone.io/reference/update), and [upsert](https://docs.pinecone.io/reference/upsert)
* **Collection Operations**: [listCollections](https://docs.pinecone.io/reference/list_collections), [createCollection](https://docs.pinecone.io/reference/create_collection), [describeCollection](https://docs.pinecone.io/reference/describe_collection), and [deleteCollection](https://docs.pinecone.io/reference/delete_collection)
* **Index Operations**: [listIndexes](https://docs.pinecone.io/reference/list_indexes), [creatIndex](https://docs.pinecone.io/reference/create_index), [describeIndex](https://docs.pinecone.io/reference/describe_index), [deleteIndex](https://docs.pinecone.io/reference/delete_index), and [configureIndex](https://docs.pinecone.io/reference/configure_index)

Note that in order to be consistent with the Pinecone API naming, the service function names match exactly the API endpoint titles/descriptions with camelcase.
Also, we aimed the lib to be self-contained with the fewest dependencies possible therefore we ended up using only two libs `play-ahc-ws-standalone` and `play-ws-standalone-json` (at the top level).  

**✔️ Important**: this is a "community-maintained" library and, as such, has no relation to Pinecone company.

👉 Check out an article about the lib/client on [Medium](https://medium.com/@0xbnd/pinecone-scala-client-just-landed-53b3638e61b3).

## Installation 🚀

The currently supported Scala versions are **2.12, 2.13**, and **3**.  

To pull the library you have to add the following dependency to your *build.sbt*

```
"io.cequence" %% "pinecone-scala-client" % "0.1.0"
```

or to *pom.xml* (if you use maven)

```
<dependency>
    <groupId>io.cequence</groupId>
    <artifactId>pinecone-scala-client_2.12</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Config ⚙️

- Env. variables: `PINECONE_SCALA_CLIENT_API_KEY`, and `PINECONE_SCALA_CLIENT_ENV`
- File config (default): [pinecone-scala-client.conf](./pinecone-client/src/main/resources/pinecone-scala-client.conf)

## Usage 👨‍🎓

**Ia. Obtaining `PineconeIndexService`**

First you need to provide an implicit execution context as well as akka materializer, e.g., as

```scala
  implicit val ec = ExecutionContext.global
  implicit val materializer = Materializer(ActorSystem())
```

Then you can obtain a service in one of the following ways.

- Default config (expects env. variable(s) to be set as defined in `Config` section)

```scala
  val service = PineconeIndexServiceFactory()
```

- Custom config
```scala
  val config = ConfigFactory.load("path_to_my_custom_config")
  val service = PineconeIndexServiceFactory(config)
```

- Without config
```scala
  val service = PineconeIndexServiceFactory(
    apiKey = "your_api_key",
    environment = "your_env" // e.g. "northamerica-northeast1-gcp
  )
```

**Ib. Obtaining `PineconeVectorService`**

Same as with `PineconeIndexService`, you need to first provide implicit execution context and akka materializer. Then you can obtain a service in one of the following ways.

- Default config (expects env. variable(s) to be set as defined in `Config` section). Note that if the index with a given name is not available, the factory will return `None`.
```scala
  PineconeVectorServiceFactory("index_name").map { serviceOption =>
    val service = serviceOption.getOrElse(
      throw new Exception(s"Index with a given name does not exist.")
    )
    // do something with the service
  }
```

- Custom config
```scala
  val config = ConfigFactory.load("path_to_my_custom_config")
  PineconeVectorServiceFactory("index_name", config).map { service =>
    val service = serviceOption.getOrElse(
      throw new Exception(s"Index with a given name does not exist.")
    )
    // do something with the service
  }
```

- Without config

```scala
  PineconeVectorServiceFactory(
    apiKey = "your_api_key",
    indexName = "index_name", // e.g. "auto-gpt"
    pineconeIndexService = pineconeIndexService // index service to be used to find the index host URL 
  ).map { serviceOption =>
    val service = serviceOption.getOrElse(
      throw new Exception(s"Index with a given name does not exist.")
    )
    // do something with the service
  }
```

**II. Calling functions**

Full documentation of each call with its respective inputs and settings is provided in [PineconeVectorService](./pinecone-core/src/main/scala/io/cequence/pineconescala/service/PineconeVectorService.scala) and [PineconeIndexService](./pinecone-core/src/main/scala/io/cequence/pineconescala/service/PineconeIndexService.scala). Since all the calls are async they return responses wrapped in `Future`.

Examples:

**Index Operations**

- List indexes

```scala
  pineconeIndexService.listIndexes.map(indexes =>
    indexes.foreach(println)
  )
```

- Create index (with default settings)

```scala
  import io.cequence.pineconescala.domain.response.CreateResponse

  pineconeIndexService.createIndex(
    name = "auto-gpt-test",
    dimension = 1536
  ).map(
    _ match {
      case CreateResponse.Created => println("Index successfully created.")
      case CreateResponse.BadRequest => println("Index creation failed. Request exceeds quota or an invalid index name.")
      case CreateResponse.AlreadyExists => println("Index with a given name already exists.")
    }
  )
```

- Describe index

```scala
  pineconeIndexService.describeIndex("index_name").map(indexInfo =>
    // if not found, indexInfo will be None    
    println(indexInfo)
  )
```

- Delete index

```scala
  import io.cequence.pineconescala.domain.response.DeleteResponse

  pineconeIndexService.deleteIndex("index_name").map(
    _ match {
      case DeleteResponse.Deleted => println("Index successfully deleted.")
      case DeleteResponse.NotFound => println("Index with a given name not found.")
    }
  )
```

- Configure index

```scala
  import io.cequence.pineconescala.domain.response.ConfigureIndexResponse

  pineconeIndexService.configureIndex(
    name = "index_name",
    replicas = Some(2),
    pod_type = Some(PodType.p1_x2)
  ).map(deleteResponse =>
    deleteResponse match {
      case ConfigureIndexResponse.Updated => println("Index successfully updated.")
      case ConfigureIndexResponse.BadRequestNotEnoughQuota => println("Index update failed. Not enough quota.")
      case ConfigureIndexResponse.NotFound => println("Index with a given name not found.")
    }
  )
```

**Collection Operations**

- List collections

```scala
  pineconeIndexService.listCollections.map(collectionNames =>
    println(collectionNames.mkString(", "))
  )
```

- Create collection

```scala
  import io.cequence.pineconescala.domain.response.CreateResponse

  pineconeIndexService.createCollection(
    name = "collection_name",
    source = "index_name"
  ).map(
    _ match {
      case CreateResponse.Created => println("Collection successfully created.")
      case CreateResponse.BadRequest => println("Collection creation failed. Request exceeds quota or an invalid collection name.")
      case CreateResponse.AlreadyExists => println("Collection with a given name already exists.")
    }
  )
```

- Describe collection

```scala
  pineconeIndexService.describeCollection("collection_name").map(collectionInfo =>
    // if not found, collectionInfo will be None
    println(collectionInfo)
  )
```

- Delete collection

```scala
  import io.cequence.pineconescala.domain.response.DeleteResponse

  pineconeIndexService.deleteCollection("collection_name").map(
    _ match {
      case DeleteResponse.Deleted => println("Collection successfully deleted.")
      case DeleteResponse.NotFound => println("Collection with a given name not found.")
    }
  )
```

**Vector Operations**

- Upsert

```scala
  val dimension = 1536

  pineconeVectorService.upsert(
    vectors = Seq(
      PVector(
        id = "666",
        values = Seq.fill(dimension)(Random.nextDouble),
        metadata = Map(
          "is_relevant" -> "not really but for testing it's ok, you know",
          "food_quality" -> "brunches are perfect but don't go there before closing time"
        )
      ),
      PVector(
        id = "777",
        values = Seq.fill(dimension)(Random.nextDouble),
        metadata = Map(
          "is_relevant" -> "very much so",
          "food_quality" -> "burritos are the best!"
        )
      )
    ),
    namespace = "my_namespace",
  ).map(vectorUpsertedCount =>
    println(s"Upserted $vectorUpsertedCount vectors.")
  )
```

- Update

```scala
  val fetchedValues = ... // vectors fetched from somewhere 

  pineconeVectorService.update(
    id = "777",
    namespace = "my_namespace",
    values = fetchedValues.map(_ / 100), // divide fetched values by 100
    sparseValues = Some(SparseVector(
      indices = Seq(1, 2, 3),
      values = Seq(8.8, 7.7, 2.2)
    )),
    setMetaData = Map(
      "solid_info" -> "this is the source of the truth"
    )
  ).map(_ =>
    println(s"Vectors updated.")
  )
```

- Query with default settings

```scala
  pineconeVectorService.query(
    vector = Seq.fill(1536)(Random.nextDouble), // some values/embeddings
    namespace = "my_namespace"
  ).map { queryResponse =>
    queryResponse.matches.foreach { matchInfo =>
      println(s"Matched vector id: ${matchInfo.id}")
      println(s"Matched vector values: ${matchInfo.values.take(20).mkString(", ")}..")
      println(s"Matched vector score: ${matchInfo.score}")
      println(s"Matched vector metadata: ${matchInfo.metadata}")
    }
  }
```

- Query with custom settings

```scala
  pineconeVectorService.query(
    vector = Seq.fill(1536)(Random.nextDouble), // some values/embeddings
    namespace = "my_namespace",
    settings = QuerySettings(
      topK = 5,
      includeValues = true,
      includeMetadata = true
    )
  ).map { queryResponse =>
    queryResponse.matches.foreach { matchInfo =>
      println(s"Matched vector id: ${matchInfo.id}")
      println(s"Matched vector values: ${matchInfo.values.take(20).mkString(", ")}..")
      println(s"Matched vector score: ${matchInfo.score}")
      println(s"Matched vector metadata: ${matchInfo.metadata}")
    }
  }
```

- Fetch

```scala
  pineconeVectorService.fetch(
    ids = Seq("666", "777"),
    namespace = "my_namespace"
  ).map { fetchResponse =>
    fetchResponse.vectors.values.map { pVector =>
      println(s"Fetched vector id: ${pVector.id}")
      println(s"Fetched vector values: ${pVector.values.take(20).mkString(", ")}..")
      println(s"Fetched vector metadata: ${pVector.metadata}")
   }
}
```

- Delete by id(s)

```scala
  pineconeVectorService.delete(
    ids = Seq("666", "777"),
    namespace = "my_namespace"
  ).map(_ =>
    println("Vectors deleted")
  )
```

- Delete all

```scala
  pineconeVectorService.deleteAll(
    namespace = "my_namespace"
  ).map(_ =>
    println("All vectors deleted")
  )
```

- Describe index stats

```scala
  pineconeVectorService.describeIndexStats.map(stats =>
    println(stats)      
  )
```
## Demo

For ready-to-run demos pls. refer to separate seed projects:
- [Pinecone Scala Demo](https://github.com/cequence-io/pinecone-scala-demo) - shows how to use Pinecone vector, index, and collection operations  
- [Pinecone + OpenAI Scala Demo](https://github.com/cequence-io/pinecone-openai-scala-demo) - shows how to generate and store OpenAI embeddings into Pinecone and query them afterwards

## FAQ 🤔

1. _I got a timeout exception. How can I change the timeout setting?_

   You can do it either by passing the `timeouts` param to `Pinecone{Vector,Index}ServiceFactory` or, if you use your own configuration file, then you can simply add it there as: 

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

2. _I got an exception like `com.typesafe.config.ConfigException$UnresolvedSubstitution: pinecone-scala-client.conf @ jar:file:.../io/cequence/pinecone-scala-client_2.13/0.1.0/pinecone-scala-client_2.13-0.1.0.jar!/pinecone-scala-client.conf: 4: Could not resolve substitution to a value: ${PINECONE_SCALA_CLIENT_API_KEY}`. What should I do?_

   Set the env. variable `PINECONE_SCALA_CLIENT_API_KEY`. If you don't have one register [here](https://app.pinecone.io/?sessionType=signup).


3. _It all looks cool. I want to chat with you about your research and development?_

   Just shoot us an email at [pinecone-scala-client@cequence.io](mailto:pinecone-scala-client@cequence.io?subject=Research%20andDevelopment).

## License ⚖️

This library is available and published as open source under the terms of the [MIT License](https://opensource.org/licenses/MIT).

## Contributors 🙏

This project is open-source and welcomes any contribution or feedback ([here](https://github.com/cequence-io/pinecone-scala/issues)).

Development of this library has been supported by  [<img src="https://cequence.io/favicon-16x16.png"> - Cequence.io](https://cequence.io) - `The future of contracting` 

Created and maintained by [Peter Banda](https://peterbanda.net).