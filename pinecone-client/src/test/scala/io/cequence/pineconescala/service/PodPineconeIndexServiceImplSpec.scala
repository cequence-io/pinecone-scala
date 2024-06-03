package io.cequence.pineconescala.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.pineconescala.domain.{Metric, PodType}
import io.cequence.pineconescala.domain.response.CollectionInfo
import io.cequence.pineconescala.domain.settings.IndexSettings.{
  CreatePodBasedIndexSettings,
  CreateServerlessIndexSettings
}
import io.cequence.pineconescala.domain.settings.{CloudProvider, Region}
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.must.Matchers.contain
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.ExecutionContext

class PodPineconeIndexServiceImplSpec
    extends AsyncWordSpec
    with GivenWhenThen
    with PodFixtures {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())

  def pineconeIndexService: PineconePodBasedIndexService =
    PineconeIndexServiceFactory().left.get

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

    "listCollections lists all available collections" in {
      for {
        collections <- pineconeIndexService.listCollections
      } yield {
        collections.head.startsWith(s"$indexName-snapshot") shouldBe true
      }
    }

    "describeCollection shows correct collection metadata" in {
      for {
        collectionName <- pineconeIndexService.listCollections.map(_.head)
        maybeCollectionInfo: Option[CollectionInfo] <- pineconeIndexService.describeCollection(
          collectionName
        )
      } yield {
        maybeCollectionInfo.map { collectionInfo =>
          collectionInfo.name shouldBe collectionName
          collectionInfo.dimension shouldBe 1536
          collectionInfo.status shouldBe "Ready"
        }.getOrElse(fail("Collection not found"))
      }
    }

    "createIndex creates a new index, deleteIndex deletes the index" in {
      val newIndexName = "create-index-test"
      for {
        _ <- pineconeIndexService.createIndex(
          newIndexName,
          dimensions,
          CreatePodBasedIndexSettings(
            metric = Metric.cosine,
            pods = 2,
            replicas = 2,
            podType = PodType.p1_x1,
            shards = 2,
            metadataConfig = Map("metadata1" -> "string", "metadata2" -> "int"),
            sourceCollection = Some("openai-embeddings-1536")
          )
        )
        indexes <- pineconeIndexService.listIndexes
        _ <- pineconeIndexService.deleteIndex(newIndexName)
        _ = Thread.sleep(5000)
        indexesAfterDeletion <- pineconeIndexService.listIndexes
      } yield {
        indexes should contain(newIndexName)
        indexesAfterDeletion shouldNot contain(newIndexName)
      }
    }

  }
}
