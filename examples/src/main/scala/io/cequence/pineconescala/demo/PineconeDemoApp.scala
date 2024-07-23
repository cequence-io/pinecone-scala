package io.cequence.pineconescala.demo

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.pineconescala.service.PineconeIndexServiceFactory.FactoryImplicits

import scala.concurrent.{ExecutionContext, Future}
import io.cequence.pineconescala.service.{PineconeIndexServiceFactory, PineconePodBasedIndexService, PineconeServerlessIndexService, PineconeVectorServiceFactory}

trait PineconeDemoApp extends App {

  protected implicit val ec: ExecutionContext = ExecutionContext.global

  private val actorSystem: ActorSystem = ActorSystem()
  protected implicit val materializer: Materializer = Materializer(actorSystem)

  // impl hook
  protected def exec: Future[_]

  protected lazy val pineconeIndexService = PineconeIndexServiceFactory().asOne

  protected def pineconePodBasedIndexService: PineconePodBasedIndexService = pineconeIndexService match {
    case service: PineconePodBasedIndexService => service
    case _ => throw new Exception("PineconeIndexService is not pod based")
  }

  protected def pineconeServerlessIndexService: PineconeServerlessIndexService = pineconeIndexService match {
    case service: PineconeServerlessIndexService => service
    case _ => throw new Exception("PineconeIndexService is not pod based")
  }

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
