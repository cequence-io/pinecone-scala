package io.cequence.pineconescala.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import io.cequence.pineconescala.domain.response.Assistant.Status.Ready
import io.cequence.pineconescala.domain.response.{Assistant, DeleteResponse, FileResponse}
import org.scalatest.concurrent.Eventually.{PatienceConfig, eventually}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen, OptionValues}

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.concurrent.{ExecutionContext, Future}

class PineconeAssistantFileServiceImplSpec
    extends AsyncWordSpec
    with GivenWhenThen
    with ServerlessFixtures
    with Matchers
    with PineconeServiceConsts
    with BeforeAndAfterEach
    with EventuallyAssert
    with OptionValues {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())
  implicit val patienceConfig: PatienceConfig = PatienceConfig(timeout = 10.seconds, interval = 100.millis)

  val serverlessConfig: Config = ConfigFactory.load("serverless.conf")

  private val assistantName = "testAssistant"
  private val parameters = Map("key" -> "value")

  def assistantServiceBuilder: PineconeAssistantService =
    PineconeAssistantServiceFactory(serverlessConfig)

  def assistantFileServiceBuilder: PineconeAssistantFileService =
    PineconeAssistantFileServiceFactory(serverlessConfig)

  private def tearDown(service: PineconeAssistantService) = {
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
      val assistantFile = createAssistantFile()
      for {
        // TODO: extract creating and tear down to an operator similar to 'in' for ensuring fixtures
        assistantInfoOption <- assistantService.describeAssistant(assistantName)
        _ <- assistantInfoOption.map{_ =>
          println(s"Assistant '${assistantName}' already exists, skipping creation.")
          Future(())
        }.getOrElse(
          assistantService.createAssistant(assistantName, parameters)
        )
        _ <- eventuallyAssistantIs(Ready)(assistantService, assistantName)
        beforeCreateFiles <- assistantFileService.listFiles(assistantName)
        _ <- assistantFileService.uploadFile(assistantName, assistantFile)
        afterCreateFiles <- assistantFileService.listFiles(assistantName)
        _ <- tearDown(assistantService)
      } yield {
        println(afterCreateFiles.map(_.name).mkString("\n"))
        beforeCreateFiles.size should be(0)
        afterCreateFiles.size should be(1)
      }
    }

    "upload file" in {
      val assistantService = assistantServiceBuilder
      val assistantFileService = assistantFileServiceBuilder
      val assistantFile = createAssistantFile()
      for {
        assistantInfoOption <- assistantService.describeAssistant(assistantName)
        _ <- assistantInfoOption.map{_ =>
          println(s"Assistant '${assistantName}' already exists, skipping creation.")
          Future(())
        }.getOrElse(
          assistantService.createAssistant(assistantName, parameters)
        )
        _ <- eventuallyAssistantIs(Ready)(assistantService, assistantName)
        assistantFile <- assistantFileService.uploadFile(assistantName, assistantFile)
        _ <- tearDown(assistantService)
      } yield {
        assistantFile.status shouldBe FileResponse.Status.Processing
      }
    }

/*    "describe file" in {
      val assistantService = assistantServiceBuilder
      val assistantFileService = assistantFileServiceBuilder
      val assistantFile = createAssistantFile()
      for {
        _ <- assistantService.createAssistant(assistantName, parameters)
        file <- assistantFileService.uploadFile(assistantFile, Some("input-file"), assistantName)
        maybeDescribedFile <- assistantFileService.describeFile(assistantName, file.id)
        _ <- tearDown(assistantService)
      } yield {
        val describedFile = maybeDescribedFile.value
        describedFile.name shouldBe file.name
        describedFile.id shouldBe file.id
        describedFile.created_on shouldBe file.created_on
        describedFile.updated_on shouldBe file.updated_on
        describedFile.metadata shouldBe file.metadata
      }
    }

    "delete an existing file" in {
      val assistantService = assistantServiceBuilder
      val assistantFileService = assistantFileServiceBuilder
      val assistantFile = createAssistantFile()
      for {
        _ <- assistantService.createAssistant(assistantName, parameters)
        _ <- eventuallyAssistantIs(Ready)(assistantService, assistantName)
        uploadedFile <- assistantFileService.uploadFile(assistantFile, Some("input-file"), assistantName)
        beforeDelete <- assistantFileService.describeFile(assistantName, uploadedFile.id)
        deleteResponse <- assistantFileService.deleteFile(assistantName, uploadedFile.id)
        afterDelete <- assistantFileService.describeFile(assistantName, uploadedFile.id)
        _ <- tearDown(assistantService)
      } yield {
        beforeDelete should not be empty
        deleteResponse should be(DeleteResponse.Deleted)
        afterDelete.value.status shouldBe FileResponse.Status.Deleting
        assert(true)
      }
    }

    "return NotFound when deleting a non-existent file" in {
      val assistantService = assistantServiceBuilder
      val assistantFileService = assistantFileServiceBuilder

      for {
        _ <- assistantService.createAssistant(assistantName, parameters)
        _ <- eventuallyAssistantIs(Ready)(assistantService, assistantName)
        deleteResponse <- assistantFileService.deleteFile(assistantName, java.util.UUID.randomUUID())
        _ <- tearDown(assistantService)
      } yield {
        deleteResponse should be(DeleteResponse.NotFound)
      }
    }


    "chat with assistant" in {
      val assistantService = assistantServiceBuilder
      val assistantFileService = assistantFileServiceBuilder
      val messages = Seq("What is the maximum height of a red pine?")
      for {
        _ <- assistantService.createAssistant(assistantName, parameters)
        _ <- eventuallyAssert(() => assistantService.listAssistants())(_.size == 1)
        chatResponse <- assistantFileService.chatWithAssistant(assistantName, messages)
        _ <- tearDown(assistantService)
      } yield {
        chatResponse.model shouldBe assistantName
        chatResponse.choices.size should be > 0
      }
    }*/

  }

  private def eventuallyAssistantIs(status: Assistant.Status)(assistantService: PineconeAssistantService, assistantName: String) = {
    eventuallyAssert(() => assistantService.describeAssistant(assistantName))(_.exists(_.hasStatus(status)))
  }

  private def createAssistantFile(): File = {
    val path = "pinecone-assistant.txt"
    Files.write(Paths.get(path), "The quick brown fox jumps over the lazy dog.".getBytes(StandardCharsets.UTF_8))
    val inputFile = Paths.get(path).toFile
    inputFile
  }

}
