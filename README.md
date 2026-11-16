# Pinecone Scala Client 🗂️
[![version](https://img.shields.io/badge/version-2.0.0-green.svg)](https://cequence.io) [![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](https://opensource.org/licenses/MIT) ![GitHub Stars](https://img.shields.io/github/stars/cequence-io/pinecone-scala?style=social) [![Twitter Follow](https://img.shields.io/twitter/follow/0xbnd?style=social)](https://twitter.com/0xbnd)

This is an intuitive async full-fledged Scala client for Pinecone API supporting all the available index, vector, collection, inference and assistant operations/endpoints, provided in two convenient services called [PineconeVectorService](./pinecone-core/src/main/scala/io/cequence/pineconescala/service/PineconeVectorService.scala) and [PineconeIndexService](./pinecone-core/src/main/scala/io/cequence/pineconescala/service/PineconeIndexService.scala). The supported calls are: 

* **Vector Operations**: [describeIndexStats](https://docs.pinecone.io/reference/api/2024-07/data-plane/describeindexstats), [query](https://docs.pinecone.io/reference/api/2024-07/data-plane/query), [delete](https://docs.pinecone.io/reference/api/2024-07/data-plane/delete), [fetch](https://docs.pinecone.io/reference/api/2024-07/data-plane/fetch), [update](https://docs.pinecone.io/reference/api/2024-07/data-plane/update), and [upsert](https://docs.pinecone.io/reference/api/2024-07/data-plane/upsert)
* **Collection Operations**: [listCollections](https://docs.pinecone.io/reference/api/2024-07/control-plane/list_collections), [createCollection](https://docs.pinecone.io/reference/api/2024-07/control-plane/create_collection), [describeCollection](https://docs.pinecone.io/reference/api/2024-07/control-plane/describe_collection), and [deleteCollection](https://docs.pinecone.io/reference/api/2024-07/control-plane/delete_collection)
* **Index Operations**: [listIndexes](https://docs.pinecone.io/reference/api/2024-07/control-plane/list_indexes), [creatIndex](https://docs.pinecone.io/reference/api/2024-07/control-plane/create_index), [describeIndex](https://docs.pinecone.io/reference/api/2024-07/control-plane/describe_index), [deleteIndex](https://docs.pinecone.io/reference/api/2024-07/control-plane/delete_index), and [configureIndex](https://docs.pinecone.io/reference/api/2024-07/control-plane/configure_index)
* **Inference Operations**: [embedData](https://docs.pinecone.io/reference/api/2024-07/inference/generate-embeddings), [rerank](https://docs.pinecone.io/reference/api/2024-10/inference/rerank), and [evaluate](https://docs.pinecone.io/reference/api/assistant/metrics_alignment)
* **Assistant Operations**:  [listAssistants](https://docs.pinecone.io/reference/api/2024-07/assistant/list-assistants), [createAssistant](https://docs.pinecone.io/reference/api/2024-07/assistant/create-assistant), [describeAssistant](https://docs.pinecone.io/reference/api/2024-07/assistant/describe-assistant), [deleteAssistant](https://docs.pinecone.io/reference/api/2024-07/assistant/delete-assistant), [listFiles](https://docs.pinecone.io/reference/api/2024-07/assistant/list-files), [uploadFile](https://docs.pinecone.io/reference/api/2024-07/assistant/create-file), [describeFile](https://docs.pinecone.io/reference/api/2024-07/assistant/describe-file), [deleteFile](https://docs.pinecone.io/reference/api/2024-07/assistant/delete-file), [chatWithAssistant](https://docs.pinecone.io/reference/api/2024-07/assistant/chat-completion-assistant)
  - these operations are provided by two services: `PineconeAssistantService` and `PineconeAssistantFileService`

Note that in order to be consistent with the Pinecone API naming, the service function names match exactly the API endpoint titles/descriptions with camelcase.
Also, we aimed the lib to be self-contained with the fewest dependencies possible: the client depends only on `ws-client-core` (from [ws-client](https://github.com/cequence-io/ws-client), with `play-json` for JSON), and the HTTP backend is pluggable - see [HTTP Backends](#http-backends-engines-).  

**✔️ Important**: this is a "community-maintained" library and, as such, has no relation to Pinecone company.

👉 Check out an article about the lib/client on [Medium](https://medium.com/@0xbnd/pinecone-scala-client-just-landed-53b3638e61b3). Also, **if you want to see hands-on examples right away, go to the [Pinecone Examples](./examples) or [OpenAI + Pinecone Examples](./openai-examples) modules.**

## Installation 🚀

The currently supported Scala versions are **2.12, 2.13**, and **3**.

The library comes in three modules:
- `pinecone-scala-core` - the domain and the service traits
- `pinecone-scala-client` - the services, with no HTTP backend (you add one - see [HTTP Backends](#http-backends-engines-))
- `pinecone-scala-client-default` - the client plus the default HTTP backend (JDK `java.net.http`, no extra dependencies)

To pull the library with the default backend add the following dependency to your *build.sbt*

```
"io.cequence" %% "pinecone-scala-client-default" % "2.0.0"
```

or to *pom.xml* (if you use maven)

```
<dependency>
    <groupId>io.cequence</groupId>
    <artifactId>pinecone-scala-client-default_2.12</artifactId>
    <version>2.0.0</version>
</dependency>
```

**Upgrading from 1.x**: switch to `pinecone-scala-client-default` (or to `pinecone-scala-client` + `ws-client-play-akka` to stay on Play WS + Akka - see [HTTP Backends](#http-backends-engines-)), drop the implicit `Materializer` (the factories take only an implicit `ExecutionContext`), and on Scala 2.13 use 2.13.18+. The 1.x (Akka-based) releases stay available on Maven Central.

## HTTP Backends (Engines) 🔌

The client doesn't talk HTTP itself - it goes through a pluggable [ws-client](https://github.com/cequence-io/ws-client) engine (1.2.1), and doesn't enforce any. `pinecone-scala-client-default` is just `pinecone-scala-client` plus the JDK engine. To use a different backend, depend on `pinecone-scala-client` and add the backend's module next to it, e.g.

```
"io.cequence" %% "pinecone-scala-client" % "2.0.0",
"io.cequence" %% "ws-client-play-pekko" % "1.2.1"
```

The available backends:

| Module | Engine id | Priority | Scala | Backend |
|---|---|---|---|---|
| `ws-client-jdk` | `jdk` | 0 | 2.12, 2.13, 3 | JDK `java.net.http`, no extra dependencies (the one in `pinecone-scala-client-default`) |
| `ws-client-sttp` | `sttp` | 5 | 2.12, 2.13 | sttp |
| `ws-client-zio-http` | `zio-http` | 7 | 2.12, 2.13 | ZIO HTTP |
| `ws-client-play-akka` | `play-akka` | 10 | 2.12, 2.13, 3 | Play WS on Akka (the 1.x backend) |
| `ws-client-pekko-http` | `pekko-http` | 15 | 2.13, 3 (3.3+) | Pekko HTTP |
| `ws-client-play-pekko` | `play-pekko` | 20 | 2.13, 3 (3.3+) | Play WS on Pekko |

`ws-client-play-akka-stream` and `ws-client-play-pekko-stream` are the streaming variants of the Play engines (priorities 11 and 21).

**Which engine is used.** The engine is looked up on the classpath when a service is created:
1. an engine id set via the config key `ws-client.engine` (in `application.conf`, or as `-Dws-client.engine=<id>`) wins - note that it applies to every ws-client-based library in the app;
2. otherwise a single engine on the classpath is used as is, and with several, the one with the highest priority;
3. two engines sharing the top priority fail with an error asking you to pick one by id, and with no engine at all creating a service fails with "No WS client engine found on the classpath".

The JDK engine has the lowest priority - it's the fallback. So with `pinecone-scala-client-default`, if another engine also ends up on the classpath (e.g. `ws-client-play-pekko` pulled in by another library), that one is used instead. To keep the JDK engine, set `ws-client.engine = jdk`, or pass the engine explicitly (below).

**Passing an engine explicitly (and sharing it).** Every service created by a plain factory gets its own engine (with its own connection pool), released by the service's `close()`. To share one engine across services, or to pick it in code, create the engine yourself and pass it to the `withEngine` factories. The engine is then yours: `close()` on the services leaves it open, and you close it once, when done with all of them.

```scala
import com.typesafe.config.ConfigFactory
import io.cequence.pineconescala.service.PineconeIndexServiceFactory.FactoryImplicits
import io.cequence.wsclient.service.spi.{TransportSettings, WSClientEngineRegistry}
import io.cequence.wsclient.service.ws.Timeouts

// a specific engine by id - omit engineId to use the classpath lookup described above
val engine = WSClientEngineRegistry(
  TransportSettings(timeouts = Timeouts(requestTimeout = Some(120000))), // ms
  engineId = Some("jdk")
)

val apiKey = sys.env("PINECONE_SCALA_CLIENT_API_KEY")

val indexService = PineconeIndexServiceFactory.withEngine(engine, apiKey, environment = None).asOne
val inferenceService = PineconeInferenceServiceFactory.withEngine(engine, apiKey)
val assistantService = PineconeAssistantServiceFactory.withEngine(engine, apiKey)

// looks the index up (on the same engine) - Future[Option[PineconeVectorService]]
val vectorService = PineconeVectorServiceFactory.withEngine(
  engine,
  "index-name",
  ConfigFactory.load("pinecone-scala-client.conf")
)

// ... when done with all the services
engine.close()
```

**Engine-specific behavior.** The engine applies the timeouts, so which settings take effect depends on it - e.g. the JDK engine ignores `pooledConnectionIdleTimeout`. The JDK engine also builds multipart uploads (assistant files) in memory, so for very large files prefer one of the Akka/Pekko-based engines.

**Troubleshooting "No WS client engine found on the classpath".** Add a backend module (see the table above). If one is on the classpath and the error persists, your environment likely uses isolated class loaders (e.g. Play dev mode, an app server or a notebook) - the engine lookup goes through the current thread's context class loader. Then create the engine on your app's thread (`WSClientEngineRegistry(...)` as above) and pass it to the `withEngine` factories.

## Config ⚙️

- Env. variables: `PINECONE_SCALA_CLIENT_API_KEY`, and `PINECONE_SCALA_CLIENT_ENV` if pod-based service is used
- File config (default): [pinecone-scala-client.conf](./pinecone-client/src/main/resources/pinecone-scala-client.conf)

## Usage 👨‍🎓

**Ia. Obtaining `PineconeIndexService`**

First you need to provide an implicit execution context, e.g., as

```scala
  implicit val ec = ExecutionContext.global
```

Then you can obtain a service (pod or serverless-based) in one of the following ways.

- Default config (expects env. variable(s) to be set as defined in `Config` section)

```scala
  import io.cequence.pineconescala.service.PineconeIndexServiceFactory.FactoryImplicits

  val service = PineconeIndexServiceFactory().asOne
```

- Custom config
```scala
  val config = ConfigFactory.load("path_to_my_custom_config")
  val service = PineconeIndexServiceFactory(config).asOne
```

- Without config for pod-based service (with env) - creates an instance of `PineconePodBasedIndexService`
```scala
  val service  = PineconeIndexServiceFactory(
    apiKey = "your_api_key",
    environment = "your_env" // e.g. "northamerica-northeast1-gcp
  )
```

- Without config for serverless service - creates an instance of `PineconeServerlessIndexService`
```scala
  val service = PineconeIndexServiceFactory(
    apiKey = "your_api_key"
  )
```

**Ib. Obtaining `PineconeVectorService`**

Same as with `PineconeIndexService`, you need to first provide an implicit execution context. Then you can obtain a service in one of the following ways.

- Default config (expects env. variable(s) to be set as defined in `Config` section). Note that if the index with a given name is not available, the factory will return `None`.
```scala
  PineconeVectorServiceFactory("index_name").map { serviceOption =>
    val service = serviceOption.getOrElse(
      throw new Exception(s"Index with a given name does not exist.")
    )
    // do something with the service
  }
```

**Ic. Obtaining `PineconeInferenceService`**

Same as with `PineconeIndexService`, you need to first provide an implicit execution context. Then you can obtain a service in one of the following ways.

- Default config

```scala
  val service = PineconeInferenceServiceFactory()
```

- Custom config
```scala
  val config = ConfigFactory.load("path_to_my_custom_config")
  val service = PineconeInferenceServiceFactory(config)
```

- Directly with api-key
```scala
  val service = PineconeInferenceServiceFactory(
    apiKey = "your_api_key"
  )
```

**Id. Obtaining `PineconeAssistantService`**


- Default config

```scala
  val service = PineconeAssistantServiceFactory()
```

- Custom config

```scala
  val config = ConfigFactory.load("path_to_my_custom_config")
  val service = PineconeAssistantServiceFactory(config)
```

- Directly with api-key

```scala
  val service = PineconeAssistantServiceFactory(
    apiKey = "your_api"
  )
```

**Ie. Obtaining `PineconeAssistantFileService`**

- Default config

```scala
  val service = PineconeAssistantFileServiceFactory()
```

- Custom config

```scala
  val config = ConfigFactory.load("path_to_my_custom_config")
  val service = PineconeAssistantFileServiceFactory(config)
```

- Directly with api-key

```scala
  val service = PineconeAssistantFileServiceFactory(
    apiKey = "your_api"
  )
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
  ).map {
    case CreateResponse.Created => println("Index successfully created.")
    case CreateResponse.BadRequest => println("Index creation failed. Request exceeds quota or an invalid index name.")
    case CreateResponse.AlreadyExists => println("Index with a given name already exists.")
  }
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

  pineconeIndexService.deleteIndex("index_name").map {
    case DeleteResponse.Deleted => println("Index successfully deleted.")
    case DeleteResponse.NotFound => println("Index with a given name not found.")
  }
```

- Configure index

```scala
  import io.cequence.pineconescala.domain.response.ConfigureIndexResponse

  pineconeIndexService.configureIndex(
    name = "index_name",
    replicas = Some(2),
    pod_type = Some(PodType.p1_x2)
  ).map { 
    case ConfigureIndexResponse.Updated => println("Index successfully updated.")
    case ConfigureIndexResponse.BadRequestNotEnoughQuota => println("Index update failed. Not enough quota.")
    case ConfigureIndexResponse.NotFound => println("Index with a given name not found.")
  }
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
  ).map {
    case CreateResponse.Created => println("Collection successfully created.")
    case CreateResponse.BadRequest => println("Collection creation failed. Request exceeds quota or an invalid collection name.")
    case CreateResponse.AlreadyExists => println("Collection with a given name already exists.")
  }
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

  pineconeIndexService.deleteCollection("collection_name").map {
    case DeleteResponse.Deleted => println("Collection successfully deleted.")
    case DeleteResponse.NotFound => println("Collection with a given name not found.")
  }
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

**Inference Operations**

- Generate embeddings

```scala
  pineconeInferenceService.createEmbeddings(
    Seq("The quick brown fox jumped over the lazy dog")
  ).map { embeddings =>
    println(embeddings.data.mkString("\n"))
  }
```

- Rerank documents / chunks

```scala
  pineconeInferenceService.rerank(
    query = "The tech company Apple is known for its innovative products like the iPhone.",
    documents = Seq(...)  
  ).map(
    _.data.foreach(println)
  )
```

- Evaluate Q&A

```scala
  pineconeInferenceService.evaluate(
    question = "What are the capital cities of France, England and Spain?",
    answer = "Paris is a city of France and Barcelona of Spain",
    groundTruthAnswer = "Paris is the capital city of France, London of England and Madrid of Spain"
  ).map { response =>
    println(response)
  }
```

** Assistant Operations**

- List assistants

```scala
  pineconeAssistantService.listAssistants.map(assistants =>
    println(assistants.mkString(", "))
  )
```

- Create assistant

```scala
  import io.cequence.pineconescala.domain.response.CreateResponse

  pineconeAssistantService.createAssistant(
    name = "assistant_name",
    description = "assistant_description",
    assistantType = "assistant_type"
  ).map {
    case CreateResponse.Created => println("Assistant successfully created.")
    case CreateResponse.BadRequest => println("Assistant creation failed. Request exceeds quota or an invalid assistant name.")
    case CreateResponse.AlreadyExists => println("Assistant with a given name already exists.")
  }
```

- Describe assistant

```scala
  pineconeAssistantService.describeAssistant("assistant_name").map(assistant =>
    // if not found, assistant will be None
    println(assistant)
  )
```

- Delete assistant

```scala
  import io.cequence.pineconescala.domain.response.DeleteResponse

  pineconeAssistantService.deleteAssistant("assistant_name").map {
    case DeleteResponse.Deleted => println("Assistant successfully deleted.")
    case DeleteResponse.NotFound => println("Assistant with a given name not found.")
  }
```

- List assistant files

```scala
  pineconeAssistantService.listFiles("assistant_name").map(files =>
    println(files.mkString(", "))
  )
```

- Upload assistant file

```scala
  import io.cequence.pineconescala.domain.response.CreateResponse

  pineconeAssistantService.uploadFile(
    assistantName = "assistant_name",
    filePath = "path_to_file"
  ).map {
    case CreateResponse.Created => println("File successfully uploaded.")
    case CreateResponse.BadRequest => println("File upload failed. Request exceeds quota or an invalid file path.")
    case CreateResponse.AlreadyExists => println("File with a given name already exists.")
  }
```

- Describe assistant file

```scala
  pineconeAssistantService.describeFile("assistant_name", "file_name").map(file =>
    // if not found, file will be None
    println(file)
  )
```

- Chat with an assistant

```scala
  pineconeAssistantService.chatWithAssistant(
    "assistant_name",
    "What is the maximum height of a red pine?"
  ).map(response =>
    println(response)
  )
```

## Demo

For ready-to-run demos pls. refer to separate modules:
- [Pinecone Scala Demo](./examples) - shows how to use Pinecone vector, index, and collection operations  
- [Pinecone + OpenAI Scala Demo](./openai-examples) - shows how to generate and store OpenAI embeddings into Pinecone and query them afterwards

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

   Which settings take effect depends on the HTTP backend - e.g. the JDK engine (`ws-client-jdk`, the one in `pinecone-scala-client-default`) ignores `pooledConnectionIdleTimeoutSec` - see [HTTP Backends](#http-backends-engines-).

2. _I got an exception like `com.typesafe.config.ConfigException$UnresolvedSubstitution: pinecone-scala-client.conf @ jar:file:.../io/cequence/pinecone-scala-client_2.13/2.0.0/pinecone-scala-client_2.13-2.0.0.jar!/pinecone-scala-client.conf: 4: Could not resolve substitution to a value: ${PINECONE_SCALA_CLIENT_API_KEY}`. What should I do?_

   Set the env. variable `PINECONE_SCALA_CLIENT_API_KEY`. If you don't have one register [here](https://app.pinecone.io/?sessionType=signup).


3. _It all looks cool. I want to chat with you about your research and development?_

   Just shoot us an email at [pinecone-scala-client@cequence.io](mailto:pinecone-scala-client@cequence.io?subject=Research%20andDevelopment).

## License ⚖️

This library is available and published as open source under the terms of the [MIT License](https://opensource.org/licenses/MIT).

## Contributors 🙏

This project is open-source and welcomes any contribution or feedback ([here](https://github.com/cequence-io/pinecone-scala/issues)).

Development of this library has been supported by  [<img src="https://cequence.io/favicon-16x16.png"> - Cequence.io](https://cequence.io) - `The future of contracting` 

Created and maintained by [Peter Banda](https://peterbanda.net).