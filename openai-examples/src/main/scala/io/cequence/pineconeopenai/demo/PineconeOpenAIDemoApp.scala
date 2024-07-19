package io.cequence.pineconeopenai.demo

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.openaiscala.service.OpenAIServiceFactory

import scala.concurrent.{ExecutionContext, Future}
import io.cequence.pineconescala.service.{PineconeIndexServiceFactory, PineconeServiceConsts, PineconeVectorServiceFactory}

trait PineconeOpenAIDemoApp extends App with PineconeServiceConsts {

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

  protected lazy val openAIService = OpenAIServiceFactory()

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
