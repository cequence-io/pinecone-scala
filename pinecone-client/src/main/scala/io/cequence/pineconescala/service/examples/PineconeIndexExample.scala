package io.cequence.pineconescala.service.examples

import akka.actor.ActorSystem
import akka.stream.Materializer
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

      createResponse <- service.createIndex(
        name = "auto-gpt-test",
        dimension = 1536
      )

      _ = println(createResponse)

      createResponse2 <- service.createIndex(
        name = "auto-gpt-test",
        dimension = 1536
      )

      _ = println(createResponse2)

      indexes2 <- service.listIndexes

      _ = println(indexes2.mkString(" "))
    } yield {
      System.exit(0)
    }
  } recover {
    case e: Throwable =>
      println(e)
      System.exit(1)
  }
}
