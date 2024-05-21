package io.cequence.pineconescala.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import io.cequence.pineconescala.domain.response.CreateResponse.Created
import io.cequence.pineconescala.domain.response.{CollectionInfo, CreateResponse}
import io.cequence.pineconescala.service.PineconeIndexServiceFactory.{Pod, Serverless}
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.must.Matchers.contain
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.ExecutionContext

class PineconeServerlessIndexServiceImplSpec
    extends AsyncWordSpec
    with GivenWhenThen
    with ServerlessFixtures {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())

  val serverlessConfig: Config = ConfigFactory.load("serverless.conf")
  def pineconeIndexService: Serverless =
    PineconeIndexServiceFactory(serverlessConfig).right.get

  Vector(
    """{"name":"openai-embeddings-1536","metric":"cosine","dimension":1536,"status":{"ready":true,"state":"Ready"},"host":"openai-embeddings-1536-826d112.svc.eu-west1-gcp.pinecone.io","spec":{"pod":{"replicas":1,"shards":1,"pods":1,"pod_type":"p1.x1","environment":"eu-west1-gcp","source_collection":""}}}", "{"name":"openai-embeddings-1536-sl","metric":"cosine","dimension":1536,"status":{"ready":true,"state":"Ready"},"host":"openai-embeddings-1536-sl-826d112.svc.apu-57e2-42f6.pinecone.io","spec":{"serverless":{"region":"eu-west-1","cloud":"aws"}}}"""
  )

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
        println(s"collections = ${collections}")
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
        println(s"maybeCollectionInfo = ${maybeCollectionInfo}")
        maybeCollectionInfo.map { collectionInfo =>
          collectionInfo.name shouldBe collectionName
          collectionInfo.dimension shouldBe 1536
          collectionInfo.status shouldBe "Ready"
        }.getOrElse(fail("Collection not found"))
      }
    }

    "createCollection creates a new collection" in {
      for {
        response: CreateResponse <- pineconeIndexService.createCollection(
          name = "test-collection",
          source = indexName
        )
        maybeCreated <- pineconeIndexService.describeCollection("test-collection")
      } yield {
        response shouldBe Created
        maybeCreated.isDefined shouldBe true
        maybeCreated.map(_.dimension) shouldBe Some(1536)
      }
    }

  }
}
