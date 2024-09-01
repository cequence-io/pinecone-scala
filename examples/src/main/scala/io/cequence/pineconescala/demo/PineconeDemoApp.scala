package io.cequence.pineconescala.demo

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.pineconescala.service.PineconeIndexServiceFactory.FactoryImplicits
import io.cequence.pineconescala.service._

import scala.concurrent.{ExecutionContext, Future}

trait PineconeDemoApp extends App {

  protected implicit val ec: ExecutionContext = ExecutionContext.global

  private val actorSystem: ActorSystem = ActorSystem()
  protected implicit val materializer: Materializer = Materializer(actorSystem)

  // impl hook
  protected def exec: Future[_]

  protected lazy val pineconeIndexService = PineconeIndexServiceFactory().asOne
  protected lazy val pineconeAssistantService = PineconeAssistantServiceFactory()
  protected lazy val pineconeAssistantFileService = PineconeAssistantFileServiceFactory()
  protected lazy val pineconeInferenceService = PineconeInferenceServiceFactory()

  protected def pineconePodBasedIndexService: PineconePodBasedIndexService =
    pineconeIndexService match {
      case service: PineconePodBasedIndexService => service
      case _ => throw new Exception("PineconeIndexService is not pod based")
    }

  protected def pineconeServerlessIndexService: PineconeServerlessIndexService =
    pineconeIndexService match {
      case service: PineconeServerlessIndexService => service
      case _ => throw new Exception("PineconeIndexService is not pod based")
    }

  protected def createPineconeVectorService(indexName: String) =
    PineconeVectorServiceFactory(indexName).map(
      _.getOrElse(
        throw new Exception(s"Could not find index '${indexName}'")
      )
    )

  private def closeServices() = {
    pineconeIndexService.close()
    pineconeAssistantService.close()
    pineconeAssistantFileService.close()
    pineconeInferenceService.close()
  }

  {
    for {
      _ <- exec

      _ <- actorSystem.terminate()
    } yield {
      closeServices()
      System.exit(0)
    }
  } recover { case e: Exception =>
    closeServices()
    e.printStackTrace()
    System.exit(1)
  }
}
