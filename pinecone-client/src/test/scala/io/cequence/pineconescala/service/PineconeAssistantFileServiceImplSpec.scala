package io.cequence.pineconescala.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.concurrent.Eventually.{PatienceConfig, eventually}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen, OptionValues}

import scala.concurrent.ExecutionContext

class PineconeAssistantFileServiceImplSpec
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

  def assistantFileServiceBuilder: PineconeAssistantFileService =
    PineconeAssistantFileServiceFactory(serverlessConfig)

  private def tearDown(service: PineconeAssistantService) = {
    implicit val patienceConfig: PatienceConfig =
      PatienceConfig(timeout = 10.seconds, interval = 100.millis)
    service.deleteAssistant(assistantName).flatMap { _ =>
      eventually {
        val deletedF = service.describeAssistant(assistantName)
        deletedF.map(_ should be(None))
      }
    }
  }

  "Pinecone Assistant File Service" when {

    "list files" in {
      val assistantService = assistantServiceBuilder
      val assistantFileService = assistantFileServiceBuilder
      for {
        _ <- assistantService.createAssistant(assistantName, parameters)
        beforeCreateFiles <- assistantFileService.listFiles(assistantName)
        _ <- assistantFileService.uploadFile(assistantName)
        afterCreateFiles <- assistantFileService.listFiles(assistantName)
        _ <- tearDown(assistantService)
      } yield {
        //assert(true)
        beforeCreateFiles.size should be(0)
        afterCreateFiles.size should be(1)
      }
    }
//
//    "upload file" in {
//      val service = assistantServiceBuilder
//      for {
//        _ <- service.createAssistant(assistantName, parameters)
//        file <- service.uploadFile(assistantName)
//        _ <- tearDown(service)
//      } yield {
//        // TODO: assert file contents
//        assert(true)
//      }
//    }
//
//    "describe file" in {
//      val service = assistantServiceBuilder
//      for {
//        _ <- service.createAssistant(assistantName, parameters)
//        file <- service.uploadFile(assistantName)
//        maybeDescribedFile <- service.describeFile(assistantName, file.id)
//        _ <- tearDown(service)
//      } yield {
//        val describedFile = maybeDescribedFile.value
//        describedFile.name shouldBe file.name
//        describedFile.id shouldBe file.id
//        describedFile.created_on shouldBe file.created_on
//        describedFile.updated_on shouldBe file.updated_on
//        describedFile.metadata shouldBe file.metadata
//      }
//    }

//    "delete an existing file" in {
//      val service = assistantServiceBuilder
//      for {
//        _ <- service.createAssistant(assistantName, parameters)
//        //file <- service.uploadFile(assistantName)
//        //beforeDelete <- service.describeFile(assistantName, file.id)
//        deleteResponse <- service.deleteFile(assistantName, file.id)
//        afterDelete <- service.describeFile(assistantName, file.id)
//        _ <- tearDown(service)
//      } yield {
//        beforeDelete should not be empty
//        deleteResponse should be(DeleteResponse.Deleted)
//        afterDelete shouldBe empty
//      }
//    }

//    "return NotFound when deleting a non-existent file" in {
//      val service = assistantServiceBuilder
//      for {
//        deleteResponse <- service.deleteFile(assistantName, java.util.UUID.randomUUID())
//      } yield {
//        deleteResponse should be(DeleteResponse.NotFound)
//      }
//    }

  }

}
