package io.cequence.pineconescala.demo

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.pineconescala.domain.settings.QuerySettings
import io.cequence.pineconescala.domain.{PVector, SparseVector}
import io.cequence.pineconescala.service.PineconeVectorServiceFactory

import scala.concurrent.ExecutionContext
import scala.util.Random

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object PineconeVectorLongDemo extends App {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())

  private val indexName = "auto-gpt-test"
  private val testIds = Seq("666", "667")

  {
    for {
      pineconeVectorService <- PineconeVectorServiceFactory(indexName).map(
        _.getOrElse(throw new IllegalArgumentException(s"index '${indexName}' not found"))
      )

      stats <- pineconeVectorService.describeIndexStats

      _ = println(s"Index '${indexName}' stats: ${stats}")

      vectorUpsertedCount <- pineconeVectorService.upsert(
        vectors = Seq(
          PVector(
            id = testIds(0),
            values = Seq.fill(stats.dimension)(Random.nextDouble),
            sparseValues = Some(
              SparseVector(
                indices = Seq(1, 2, 3),
                values = Seq(8.8, 7.7, 2.2)
              )
            ),
            metadata = Map(
              "is_relevant" -> "not really but for testing it's ok, you know",
              "food_quality" -> "brunches are perfect but don't go there before closing time"
            )
          ),
          PVector(
            id = testIds(1),
            values = Seq.fill(stats.dimension)(Random.nextDouble),
            sparseValues = Some(
              SparseVector(
                indices = Seq(4, 5, 6),
                values = Seq(-0.12, 0.57, 0.69)
              )
            ),
            metadata = Map(
              "is_relevant" -> "very much so",
              "food_quality" -> "burritos are the best!"
            )
          )
        ),
        namespace = "my_namespace"
      )

      _ = println(s"Upserted ${vectorUpsertedCount} vectors.")

      fetchResponse <- pineconeVectorService.fetch(
        ids = testIds,
        namespace = "my_namespace"
      )

      _ = println(s"Fetched ${fetchResponse.vectors.keySet.size} vectors.")

      queryResponse <- pineconeVectorService.query(
        vector = fetchResponse.vectors(testIds(0)).values,
        namespace = "my_namespace",
        settings = QuerySettings(
          topK = 5,
          includeValues = true,
          includeMetadata = true
        )
      )

      _ = println(s"Query matched ${queryResponse.matches.size} vectors.")

      queryResponse2 <- pineconeVectorService.queryById(
        id = testIds(0),
        namespace = "my_namespace",
        settings = QuerySettings(
          topK = 5,
          includeValues = true,
          includeMetadata = true
        )
      )

      _ = println(s"Query by id matched ${queryResponse2.matches.size} vectors.")

      _ <- pineconeVectorService.update(
        id = testIds(0),
        namespace = "my_namespace",
        values = fetchResponse.vectors(testIds(0)).values.map(_ / 100),
        sparseValues = Some(
          SparseVector(
            indices = Seq(1, 2, 3),
            values = Seq(8.8, 7.7, 2.2)
          )
        ),
        setMetaData = Map(
          "solid_info" -> "this is the source of the truth"
        )
      )

      _ = println(s"Update finished.")

      fetchResponse2 <- pineconeVectorService.fetch(
        ids = Seq(testIds(0)),
        namespace = "my_namespace"
      )

      _ = println(s"Fetched ${fetchResponse2.vectors.keySet.size} vectors.")

      _ <- pineconeVectorService.delete(
        ids = testIds,
        namespace = "my_namespace"
      )

      _ = println(s"Delete finished.")

      fetchResponse3 <- pineconeVectorService.fetch(
        ids = testIds,
        namespace = "my_namespace"
      )

      _ = println(s"Fetched ${fetchResponse3.vectors.keySet.size} vectors after delete.")
    } yield {
      System.exit(0)
    }
  } recover { case e: Throwable =>
    println(e)
    System.exit(1)
  }
}
