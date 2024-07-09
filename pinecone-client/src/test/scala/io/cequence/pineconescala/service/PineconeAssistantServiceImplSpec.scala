package io.cequence.pineconescala.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import io.cequence.pineconescala.domain.response.DeleteResponse
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.concurrent.Eventually.PatienceConfig
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.ExecutionContext

class PineconeAssistantServiceImplSpec
    extends AsyncWordSpec
    with GivenWhenThen
    with ServerlessFixtures
    with Matchers
    with PineconeServiceConsts with BeforeAndAfterEach {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())

  val serverlessConfig: Config = ConfigFactory.load("serverless.conf")

  private val assistantName = "test-assistant"
  private val parameters = Map("key" -> "value")

  def assistantServiceBuilder: PineconeAssistantService =
    PineconeAssistantServiceFactory(serverlessConfig)

  private def tearDown(service: PineconeAssistantService) = {
    implicit val patienceConfig: PatienceConfig = PatienceConfig(timeout = 10.seconds, interval = 100.millis)
    service.deleteAssistant(assistantName).flatMap { _ =>
      eventually {
        val deletedF = service.describeAssistant(assistantName)
        deletedF.map(_ should be (None))
      }
    }
  }

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
        assistant <- service.createAssistant(assistantName, parameters)
        _ <- tearDown(service)
      } yield {
        assistant.name should be(assistantName)
        assistant.metadata should be(parameters)
      }
    }

    "return None when describing a non-existent assistant" in {
      val service = assistantServiceBuilder
      for {
        assistant <- service.describeAssistant("non-existent-assistant")
      } yield {
        assistant should be(None)
      }
    }

    "return assistant when describing an existing assistant" in {
      val service = assistantServiceBuilder
      for {
        _ <- service.createAssistant(assistantName, parameters)
        assistant <- service.describeAssistant(assistantName)
        _ <- tearDown(service)
      } yield {
        assistant.get.name should be(assistantName)
        assistant.get.metadata should be(parameters)
      }
    }

    "delete an existing assistant" in {
      val service = assistantServiceBuilder
      for {
        _ <- service.createAssistant(assistantName, parameters)
        deleteResponse <- service.deleteAssistant(assistantName)
        _ <- tearDown(service)
      } yield
        deleteResponse should be(DeleteResponse.Deleted)
    }

    "return NotFound when deleting a non-existent assistant" in {
      val service = assistantServiceBuilder
      for {
        deleteResponse <- service.deleteAssistant("non-existent-assistant")
      } yield {
        deleteResponse should be(DeleteResponse.NotFound)
      }
    }

  }

}
