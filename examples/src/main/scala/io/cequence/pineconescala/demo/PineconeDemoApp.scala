package io.cequence.pineconescala.demo

import io.cequence.pineconescala.service.PineconeIndexServiceFactory.FactoryImplicits
import io.cequence.pineconescala.service._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

trait PineconeDemoApp extends App {

  protected implicit val ec: ExecutionContext = ExecutionContext.global

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

  private val run = {
    for {
      _ <- exec
    } yield {
      closeServices()
      System.exit(0)
    }
  } recover { case e: Exception =>
    closeServices()
    e.printStackTrace()
    System.exit(1)
  }

  // nothing non-daemon (no actor system) keeps the JVM alive until the demo finishes - a
  // waiting thread does (not a blocking wait here: a trait body runs in the object's constructor)
  new Thread(() => Await.ready(run, Duration.Inf)).start()
}
