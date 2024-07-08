package io.cequence.pineconescala.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.ExecutionContext

class PineconeAssistantServiceImplSpec
  extends AsyncWordSpec
  with GivenWhenThen
  with ServerlessFixtures with Matchers with PineconeServiceConsts {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())

  val serverlessConfig: Config = ConfigFactory.load("serverless.conf")

  def assistantServiceBuilder: PineconeAssistantService =
    PineconeAssistantServiceFactory(serverlessConfig)

  "Pinecone Assistant Service" when {

    "list assistants" in {
      val service = assistantServiceBuilder
      for {
        assistants <- service.listAssistants()
      } yield {
        assistants.size should be(0)
      }
    }

    "create assistant" in {
      val service = assistantServiceBuilder
      for {
        assistant <- service.createAssistant("test-assistant", Map("key" -> "value"))
      } yield {
        assistant.name should be("test-assistant")
        assistant.metadata should be(Map("key" -> "value"))
      }
    }

  }

}