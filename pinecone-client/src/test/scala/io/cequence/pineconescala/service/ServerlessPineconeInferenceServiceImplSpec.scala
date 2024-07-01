package io.cequence.pineconescala.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.GivenWhenThen

import scala.concurrent.{ExecutionContext, Future}

class ServerlessPineconeInferenceServiceImplSpec
    extends AsyncWordSpec
    with GivenWhenThen
    with ServerlessFixtures with Matchers with PineconeServiceConsts{

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())

  val serverlessConfig: Config = ConfigFactory.load("serverless.conf")

  def inferenceServiceBuilder: PineconeInferenceService =
    PineconeInferenceServiceFactory(serverlessConfig)

  "Pinecone Inference Service" when {

    "create embeddings should provide embeddings for input data" in {
      val service = inferenceServiceBuilder
      for {
        embeddings <- service.createEmbeddings(Seq("The quick brown fox jumped over the lazy dog"),
          settings = DefaultSettings.GenerateEmbeddings.withPassageInputType.withEndTruncate)
      } yield {
        println(embeddings)
        embeddings.data should not be empty
      }
    }


  }
}
