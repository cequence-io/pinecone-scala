package io.cequence.pineconescala.service.examples

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.pineconescala.domain.PodType
import io.cequence.pineconescala.service.PineconeIndexServiceFactory

import scala.concurrent.ExecutionContext

// TODO: turn this into a test
object PineconeIndexExample extends App {

  implicit val ec = ExecutionContext.global
  implicit val materializer = Materializer(ActorSystem())

  private val service = PineconeIndexServiceFactory()

  {
    for {
      indexes <- service.listIndexes

      _ = println(indexes.mkString(", "))

      indexInfo <- service.describeIndex(indexes(0))

      _ = println(indexInfo)

      deleteResponse <- service.deleteIndex(indexes(0))

      _ = println(deleteResponse)

      indexInfo <- service.describeIndex(indexes(0))

      _ = println(indexInfo)

      createResponse <- service.createIndex(
        name = "auto-gpt-test",
        dimension = 1536
      )

      _ = println(createResponse)

      configureIndexResponse <- service.configureIndex(
        "auto-gpt-test",
        replicas = Some(0),
        podType = Some(PodType.p1_x1)
      )

      _ = println(configureIndexResponse)

      _ = Thread.sleep(5000)

      indexInfo <- service.describeIndex("auto-gpt-test")

      _ = println(indexInfo)

      createResponse2 <- service.createIndex(
        name = "auto-gpt-test",
        dimension = 1536
      )

      _ = println(createResponse2)

      indexes2 <- service.listIndexes

      _ = println(indexes2.mkString(", "))

      createResponse3 <- service.createCollection("auto-gpt-test-collection", "auto-gpt-test")

      _ = println(createResponse3)

      collections <- service.listCollections

      _ = println(collections.mkString(", "))

      collectionInfo <- service.describeCollection("auto-gpt-test-collection")

      _ = println(collectionInfo)

      deleteResponse2 <- service.deleteCollection("auto-gpt-test-collection")

      _ = println(deleteResponse2)

      collections2 <- service.listCollections

      _ = println(collections2.mkString(", "))
    } yield {
      System.exit(0)
    }
  } recover {
    case e: Throwable =>
      println(e)
      System.exit(1)
  }
}