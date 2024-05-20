package io.cequence.pineconescala.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.pineconescala.domain.{PVector, SparseVector}
import io.cequence.pineconescala.domain.response.{FetchResponse, IndexStats, QueryResponse}
import io.cequence.pineconescala.domain.settings.QuerySettings
import org.scalatest.Pending.isSucceeded.&&
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.funspec.{AnyFunSpec, AsyncFunSpec}
import org.scalatest.{Assertion, GivenWhenThen}
import org.scalatest.matchers.must.Matchers.{contain, not}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AsyncWordSpec

import scala.collection.mutable.Stack
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Random, Success}

class PineconeVectorServiceImplSpec
    extends AsyncWordSpec
    with GivenWhenThen
    with TextFixtures {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())

  def vectorServiceBuilder: Future[PineconeVectorService] =
    PineconeVectorServiceFactory(indexName).map(
      _.getOrElse(throw new IllegalArgumentException(s"index '${indexName}' not found"))
    )

  def withTearingDownStore(testFun: PineconeVectorService => Future[Assertion])
    : Future[Assertion] = {
    vectorServiceBuilder.flatMap { vectorService =>
      val result = testFun(vectorService)
      vectorService.deleteAll(namespace)
      result
    }
  }

  "Pinecone Vector Service" when {

    "describeIndexStats should confirm usage of 1536 dimensional vectors" in {
      for {
        service <- vectorServiceBuilder
        stats <- service.describeIndexStats
      } yield stats.dimension shouldEqual dimensions
    }

    s"describeIndexStats should not contain the namespace 'pinecone-test'" in {
      for {
        service <- vectorServiceBuilder
        stats <- service.describeIndexStats
      } yield stats.namespaces.keys.toSet shouldNot contain("pinecone-test")
    }

    "upsert should insert a vector" in withTearingDownStore { service =>
      for {
        _ <- service.upsert(
          vectors = Seq(vector1, vector2),
          namespace = namespace
        )
        fetchedVector: FetchResponse <- service.fetch(
          ids = testIds,
          namespace = namespace
        )
      } yield {
        fetchedVector.namespace shouldEqual namespace
        fetchedVector.vectors(testIds.head).id shouldEqual vector1.id
        fetchedVector.vectors(testIds.head).metadata shouldEqual vector1.metadata
      }
    }

    "query should return k vectors with the highest similarity" in withTearingDownStore {
      service =>
        for {
          _ <- service.upsert(
            vectors = Seq(vector1, vector2),
            namespace = namespace
          )
          queryResponse: QueryResponse <- service.query(
            vector = vector1.values,
            namespace = namespace,
            settings = QuerySettings(
              topK = 2,
              includeValues = true,
              includeMetadata = true
            )
          )
        } yield {
          queryResponse.matches.size shouldEqual 2
          queryResponse.matches.head.id shouldEqual vector1.id
          queryResponse.matches(1).id shouldEqual vector2.id
        }
    }

    "queryById should return k vectors with the highest similarity" in withTearingDownStore {
      service =>
        for {
          _ <- service.upsert(
            vectors = Seq(vector1, vector2),
            namespace = namespace
          )
          queryResponse: QueryResponse <- service.queryById(
            id = vector2.id,
            namespace = namespace,
            settings = QuerySettings(
              topK = 2,
              includeValues = true,
              includeMetadata = true
            )
          )
        } yield {
          queryResponse.matches.size shouldEqual 2
          queryResponse.matches.head.id shouldEqual vector2.id
          queryResponse.matches(1).id shouldEqual vector1.id
        }
    }

    "update should update a vector" in withTearingDownStore { service =>
      for {
        _ <- service.upsert(
          vectors = Seq(vector1, vector2),
          namespace = namespace
        )
        _ <- service.update(
          id = vector1.id,
          namespace = namespace,
          values = Seq.fill(dimensions)(Random.nextDouble)
        )
        fetchedVector: FetchResponse <- service.fetch(
          ids = testIds,
          namespace = namespace
        )
      } yield {
        fetchedVector.namespace shouldEqual namespace
        val vector = fetchedVector.vectors(testIds.head)
        vector.id shouldEqual vector1.id
        vector.metadata shouldEqual vector1.metadata
        vector.values.zip(vector1.values).count(i => i._1 == i._2) shouldEqual 0
      }
    }

    "delete should remove a vector" in withTearingDownStore { service =>
      for {
        _ <- service.upsert(
          vectors = Seq(vector1, vector2),
          namespace = namespace
        )
        _ <- service.delete(
          ids = Seq(vector1.id),
          namespace = namespace
        )
        fetchedVector: FetchResponse <- service.fetch(
          ids = testIds,
          namespace = namespace
        )
      } yield {
        fetchedVector.namespace shouldEqual namespace
        fetchedVector.vectors.size shouldEqual 1
        fetchedVector.vectors(testIds(1)).id shouldEqual vector2.id
      }
    }

  }
}
