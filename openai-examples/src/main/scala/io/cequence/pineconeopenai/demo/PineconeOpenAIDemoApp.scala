package io.cequence.pineconeopenai.demo

import io.cequence.openaiscala.service.OpenAIServiceFactory
import io.cequence.pineconescala.service.PineconeIndexServiceFactory.FactoryImplicits

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import io.cequence.pineconescala.service.{PineconeIndexServiceFactory, PineconeServiceConsts, PineconeVectorServiceFactory}

trait PineconeOpenAIDemoApp extends App with PineconeServiceConsts {

  protected implicit val ec: ExecutionContext = ExecutionContext.global

  // impl hook
  protected def exec: Future[_]

  protected lazy val pineconeIndexService = PineconeIndexServiceFactory().asOne

  protected def createPineconeVectorService(indexName: String) =
    PineconeVectorServiceFactory(indexName).map(
      _.getOrElse(
        throw new Exception(s"Could not find index '${indexName}'")
      )
    )

  protected lazy val openAIService = OpenAIServiceFactory()

  private val run = {
    for {
      _ <- exec
    } yield
      System.exit(0)
  } recover {
    case e: Exception =>
      e.printStackTrace()
      System.exit(1)
  }

  // nothing non-daemon (no actor system) keeps the JVM alive until the demo finishes - a
  // waiting thread does (not a blocking wait here: a trait body runs in the object's constructor)
  new Thread(() => Await.ready(run, Duration.Inf)).start()
}
