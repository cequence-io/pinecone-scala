package io.cequence.pineconescala.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import io.cequence.pineconescala.domain.Metric
import io.cequence.pineconescala.domain.settings.{CloudProvider, Region}
import io.cequence.pineconescala.domain.settings.IndexSettingsType.CreateServerlessIndexSettings
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.must.Matchers.contain
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.ExecutionContext

class ServerlessPineconeIndexServiceImplSpec
    extends AsyncWordSpec
    with GivenWhenThen
    with ServerlessFixtures {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())

  val serverlessConfig: Config = ConfigFactory.load("serverless.conf")

  def pineconeIndexService: ServerlessIndexServiceImpl =
    PineconeIndexServiceFactory(serverlessConfig).right.get

  "Pinecone Index Service" when {

    "listIndexes lists all available indexes" in {
      for {
        indexes <- pineconeIndexService.listIndexes
      } yield {
        indexes should contain(indexName)
      }
    }

    "describeIndex shows correct index metadata" in {
      for {
        maybeIndexInfo <- pineconeIndexService.describeIndex(indexName)
      } yield {
        maybeIndexInfo.map { indexInfo =>
          indexInfo.name shouldBe indexName
          indexInfo.dimension shouldBe 1536
        }.getOrElse(fail("Index not found"))
      }
    }

    "createIndex creates a new index, deleteIndex deletes the index" in {
      val newIndexName = "openai-embeddings-1536-sl-2"
      for {
        _ <- pineconeIndexService.createIndex(
          newIndexName,
          dimensions,
          Metric.cosine,
          CreateServerlessIndexSettings(CloudProvider.AWS, Region.EUWest1)
        )
        indexes <- pineconeIndexService.listIndexes
        _ <- pineconeIndexService.deleteIndex(newIndexName)
        _ = Thread.sleep(1000)
        indexesAfterDeletion <- pineconeIndexService.listIndexes
      } yield {
        indexes should contain(newIndexName)
        indexesAfterDeletion shouldNot contain(newIndexName)
      }
    }

  }
}
