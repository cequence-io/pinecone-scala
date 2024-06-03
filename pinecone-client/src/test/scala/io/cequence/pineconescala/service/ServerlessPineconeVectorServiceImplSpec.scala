package io.cequence.pineconescala.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import io.cequence.pineconescala.domain.response.{FetchResponse, QueryResponse}
import io.cequence.pineconescala.domain.settings.QuerySettings
import org.scalatest.matchers.must.Matchers.contain
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.{Assertion, GivenWhenThen}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class ServerlessPineconeVectorServiceImplSpec
    extends AsyncWordSpec
    with GivenWhenThen
    with ServerlessFixtures {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())

  val serverlessConfig: Config = ConfigFactory.load("serverless.conf")

  def vectorServiceBuilder: Future[PineconeVectorService] =
    PineconeVectorServiceFactory(indexName, serverlessConfig).map(
      _.getOrElse(throw new IllegalArgumentException(s"index '${indexName}' not found"))
    )

  def withVectorStore(testFun: PineconeVectorService => Future[Assertion])
    : Future[Assertion] = {
    vectorServiceBuilder.flatMap { vectorService =>
      val result = testFun(vectorService)
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

    s"describeIndexStats should not contain the namespace 'serverless-pinecone-test'" in {
      for {
        service <- vectorServiceBuilder
        _ <- service.deleteAll(namespace).recover({ case _ => () })
        stats <- service.describeIndexStats
      } yield stats.namespaces.keys.toSet shouldNot contain(namespace)
    }

    "upsert should insert a vector" in withVectorStore { service =>
      for {
        upserted <- service.upsert(
          vectors = Seq(vector1, vector2),
          namespace = namespace
        )
        _ = Thread.sleep(1000)
        _ = Thread.sleep(1000)
        _ = Thread.sleep(1000)
        fetchedVector <- service.fetch(
          ids = testIds,
          namespace = namespace
        )
      } yield {
        upserted shouldEqual 2
        fetchedVector.namespace shouldEqual namespace
        fetchedVector.vectors(vector1.id).id shouldEqual vector1.id
        fetchedVector.vectors(vector1.id).metadata shouldEqual vector1.metadata
        fetchedVector.vectors(vector2.id).id shouldEqual vector2.id
        fetchedVector.vectors(vector2.id).metadata shouldEqual vector2.metadata
      }
    }

    "listVectorIDs should return all vector IDs" ignore withVectorStore { service =>
      for {
        _ <- service.upsert(
          vectors = Seq(vector1, vector2),
          namespace = namespace
        )
        allVectors <- service.listVectorIDs(namespace)
      } yield {
        allVectors.vectors should contain theSameElementsAs testIds
      }
    }

    "query should return k vectors with the highest similarity" in withVectorStore { service =>
      for {
        upserted <- service.upsert(
          vectors = Seq(vector1, vector2),
          namespace = namespace
        )
        _ = Thread.sleep(10)
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
        upserted shouldEqual 2
        queryResponse.matches.size shouldEqual 2
        queryResponse.matches.head.id shouldEqual vector1.id
        queryResponse.matches(1).id shouldEqual vector2.id
      }
    }

    "queryById should return k vectors with the highest similarity" in withVectorStore {
      service =>
        for {
          upserted <- service.upsert(
            vectors = Seq(vector1, vector2),
            namespace = namespace
          )
          _ = Thread.sleep(10)
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
          upserted shouldEqual 2
          queryResponse.matches.size shouldEqual 2
          queryResponse.matches.head.id shouldEqual vector2.id
          queryResponse.matches(1).id shouldEqual vector1.id
        }
    }

    "update should update a vector" in withVectorStore { service =>
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

    "delete should remove a vector" in withVectorStore { service =>
      for {
        _ <- service.upsert(
          vectors = Seq(vector1, vector2),
          namespace = namespace
        )
        _ = Thread.sleep(1000)
        _ <- service.delete(
          ids = Seq(vector1.id),
          namespace = namespace
        )
        _ = Thread.sleep(1000)
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
