package io.cequence.pineconescala.demo

import akka.actor.ActorSystem
import akka.stream.Materializer

import scala.concurrent.{ExecutionContext, Future}
import io.cequence.pineconescala.service.{PineconeIndexService, PineconeIndexServiceFactory, PineconeVectorService, PineconeVectorServiceFactory}

trait PineconeDemoApp extends App {

  protected implicit val ec: ExecutionContext = ExecutionContext.global

  private val actorSystem: ActorSystem = ActorSystem()
  protected implicit val materializer: Materializer = Materializer(actorSystem)

  // impl hook
  protected def exec: Future[_]

  protected lazy val pineconeIndexService = PineconeIndexServiceFactory().left.get

  protected def createPineconeVectorService(indexName: String) =
    PineconeVectorServiceFactory(indexName).map(
      _.getOrElse(
        throw new Exception(s"Could not find index '${indexName}'")
      )
    )

  {
    for {
      _ <- exec

      _ <- actorSystem.terminate()
    } yield
      System.exit(0)
  } recover {
    case e: Exception =>
      e.printStackTrace()
      System.exit(1)
  }
}
