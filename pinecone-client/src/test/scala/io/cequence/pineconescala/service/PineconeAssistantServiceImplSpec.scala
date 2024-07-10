package io.cequence.pineconescala.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import io.cequence.pineconescala.domain.response.Assistant.Status.Terminating
import io.cequence.pineconescala.domain.response.DeleteResponse
import org.scalatest.{Assertion, BeforeAndAfterEach, GivenWhenThen, OptionValues}
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.concurrent.Eventually.PatienceConfig
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.wordspec.AsyncWordSpec

import scala.Console.println
import scala.concurrent.{ExecutionContext, Future}

class PineconeAssistantServiceImplSpec
    extends AsyncWordSpec
    with GivenWhenThen
    with ServerlessFixtures
    with Matchers
    with PineconeServiceConsts
    with BeforeAndAfterEach
    with OptionValues {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())

  val serverlessConfig: Config = ConfigFactory.load("serverless.conf")

  private val assistantName = "testAssistant"
  private val parameters = Map("key" -> "value")

  def assistantServiceBuilder: PineconeAssistantService =
    PineconeAssistantServiceFactory(serverlessConfig)

  private def tearDown(service: PineconeAssistantService) = {
    implicit val patienceConfig: PatienceConfig =
      PatienceConfig(timeout = 10.seconds, interval = 100.millis)
    service.deleteAssistant(assistantName).flatMap { _ =>
      eventuallyAssert(() => service.describeAssistant(assistantName))(_.isEmpty)
    }
  }

  private def eventuallyAssert[A](f: () => Future[A])(check: A => Boolean)
                                 (implicit config: PatienceConfig): Future[Assertion] =
    eventually {
      f().map(a => assert(check(a)))
    }

  "Pinecone Assistant Service" when {

    implicit val patienceConfig: PatienceConfig =
      PatienceConfig(timeout = 10.seconds, interval = 100.millis)

    "list assistants" in {
      val service = assistantServiceBuilder
      for {
        beforeCreateAssistants <- service.listAssistants()
        _ <- service.createAssistant(assistantName, parameters)
        _ <- eventuallyAssert(() => service.listAssistants())(_.size == 1)
        _ <- tearDown(service)
      } yield {
        beforeCreateAssistants.size should be(0)
        // afterCreateAssistants.size should be(1)
      }
    }

    "create assistant" in {
      val service = assistantServiceBuilder
      for {
        assistant <- service.createAssistant(assistantName, parameters)
        createdAssistant <- service.describeAssistant(assistantName)
        _ <- tearDown(service)
      } yield {
        assistant.name should be(assistantName)
        assistant.metadata should be(parameters)
        createdAssistant.value.name should be(assistantName)
        createdAssistant.value.metadata should be(parameters)
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

    "delete an existing assistant eventually, marking it firstly as Terminating when listing assistants" in {
      val service = assistantServiceBuilder
      for {
        _ <- service.createAssistant(assistantName, parameters)
        deleteResponse <- service.deleteAssistant(assistantName)
        afterDelete <- service.listAssistants()
        _ <- eventuallyAssert(() => service.listAssistants())(_.isEmpty)
        _ <- tearDown(service)
      } yield {
        deleteResponse shouldBe DeleteResponse.Deleted
        afterDelete(0).status shouldBe Terminating
      }
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
