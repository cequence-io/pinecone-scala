package io.cequence.pineconescala.demo

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.pineconescala.domain.PodType
import io.cequence.pineconescala.service.PineconeIndexServiceFactory
import io.cequence.pineconescala.service.PineconeIndexServiceFactory.FactoryImplicits

import scala.concurrent.{ExecutionContext, Future}

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object PineconeIndexLongDemo extends App {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())

  private val indexName = "auto-gpt-test"
  private val collectionName = s"${indexName}-collection"

  {
    for {
      // we wrap it in a Future just because of the recover block
      pineconePodBasedIndexService <- Future(
        PineconeIndexServiceFactory().left.getOrElse(
          throw new Exception("PineconePodBasedIndexService expected")
        )
      )

      // create index
      _ <- pineconePodBasedIndexService.createIndex(
        name = indexName,
        dimension = 1536
      ).map { response =>
        println(s"Create index response: ${response}")
      }

      // list indexes
      _ <- pineconePodBasedIndexService.listIndexes.map { indexes =>
        println(s"The following indexes exist: ${indexes.mkString(", ")}")
      }

      // describe index (option is returned)
      _ <- pineconePodBasedIndexService.describeIndex(indexName).map { indexInfo =>
        println(s"Index '${indexName}'info: ${indexInfo}")
      }

      // configure index
      _ <- pineconePodBasedIndexService.configureIndex(
        "auto-gpt-test",
        replicas = Some(0),
        podType = Some(PodType.p1_x1)
      ).map { response =>
        println(s"Configure index response: ${response}")
      }

      // describe index after re-configuring (update)
      _ <- pineconePodBasedIndexService.describeIndex(indexName).map { indexInfo =>
        println(s"Index '${indexName}' info after reconfiguring: ${indexInfo}")
      }

      // wait a bit
      _ = {
        println("Waiting 20 seconds for index to be ready")
        Thread.sleep(20000)
      }

      // describe index after waiting
      _ <- pineconePodBasedIndexService.describeIndex(indexName).map { indexInfo =>
        println(s"Index '${indexName}' info after waiting 20 seconds: ${indexInfo}")
      }

      // delete index
      _ <- pineconePodBasedIndexService.deleteIndex(indexName).map { response =>
        println(s"Delete index response: ${response}")
      }

      // describe index after deletion
      _ <- pineconePodBasedIndexService.describeIndex(indexName).map { indexInfo =>
        println(s"Index '${indexName}' info after deletion: ${indexInfo}")
      }

      // re-create index
      _ <- pineconePodBasedIndexService.createIndex(
        name = indexName,
        dimension = 1536
      ).map { response =>
        println(s"Create index response: ${response}")
      }

      // wait a bit
      _ = {
        println("Waiting 40 seconds for index to be ready before creating a collection")
        Thread.sleep(40000)
      }

      // describe index after waiting
      _ <- pineconePodBasedIndexService.describeIndex(indexName).map { indexInfo =>
        println(s"Index '${indexName}' info after waiting 40 seconds: ${indexInfo}")
      }

      // create collection
      _ <- pineconePodBasedIndexService.createCollection(collectionName, indexName).map { response =>
        println(s"Create collection response: ${response}")
      }

      // list collections (at least one should be available)
      _ <- pineconePodBasedIndexService.listCollections.map(collectionNames =>
        println(s"Available collections: ${collectionNames.mkString(", ")}")
      )

      // describe collection (option is returned)
      _ <- pineconePodBasedIndexService.describeCollection(collectionName).map { collectionInfo =>
        println(s"Collection info: ${collectionInfo}")
      }

      // delete collection
      _ <- pineconePodBasedIndexService.deleteCollection(collectionName).map(response =>
        println(s"Delete collection response: ${response}")
      )

      // wait a bit
      _ = {
        println("Waiting 20 seconds for deletion to complete")
        Thread.sleep(20000)
      }

      // list collections after delete
      _ <- pineconePodBasedIndexService.listCollections.map(collectionNames =>
        println(s"Available collections (after delete): ${collectionNames.mkString(", ")}")
      )
    } yield {
      System.exit(0)
    }
  } recover {
    case e: Throwable =>
      println(e)
      System.exit(1)
  }
}