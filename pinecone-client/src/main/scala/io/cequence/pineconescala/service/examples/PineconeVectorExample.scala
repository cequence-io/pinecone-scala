package io.cequence.pineconescala.service.examples

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.pineconescala.domain.{PVector, SparseVector}
import io.cequence.pineconescala.domain.settings.QuerySettings
import io.cequence.pineconescala.service.PineconeVectorServiceFactory

import scala.concurrent.ExecutionContext
import scala.util.Random

// TODO: turn this into a test
object PineconeVectorExample extends App {

  implicit val ec = ExecutionContext.global
  implicit val materializer = Materializer(ActorSystem())

  private val testIds = Seq("666", "667")

  {
    for {
      service <- PineconeVectorServiceFactory(indexName = "auto-gpt-test").map(
        _.getOrElse(throw new IllegalArgumentException(s"index 'auto-gpt-test' not found"))
      )

      stats <- service.describeIndexStats

      vectorUpsertedCount <- service.upsert(
        vectors = Seq(
          PVector(
            id = testIds(0),
            values = Seq.fill(stats. dimension)(Random.nextDouble),
            sparseValues = Some(SparseVector(
              indices = Seq(1, 2, 3),
              values = Seq(8.8, 7.7, 2.2)
            )),
            metadata = Map(
              "is_relevant" -> "not really but for testing it's ok, you know",
              "food_quality" -> "brunches are perfect but don't go there before closing time"
            )
          ),
          PVector(
            id = testIds(1),
            values = Seq.fill(stats.dimension)(Random.nextDouble),
            sparseValues = Some(SparseVector(
              indices = Seq(4, 5, 6),
              values = Seq(-0.12, 0.57, 0.69)
            )),
            metadata = Map(
              "is_relevant" -> "very much so",
              "food_quality" -> "burritos are the best!"
            )
          )
        ),
        namespace = "",
      )

      fetchResponse <- service.fetch(ids = testIds, namespace = "")

      queryResponse <- service.query(
        vector = fetchResponse.vectors(testIds(0)).values,
        namespace = "",
        settings = QuerySettings(
          topK = 5,
          includeValues = true,
          includeMetadata = true
        )
      )

      queryResponse2 <- service.query(
        id = testIds(0),
        namespace = "",
        settings = QuerySettings(
          topK = 5,
          includeValues = true,
          includeMetadata = true
        )
      )

      _ <- service.update(
        id = testIds(0),
        namespace = "",
        values = fetchResponse.vectors(testIds(0)).values.map(_ / 100),
        sparseValues = Some(SparseVector(
          indices = Seq(1, 2, 3),
          values = Seq(8.8, 7.7, 2.2)
        )),
        setMetaData = Map(
          "solid_info" -> "this is the source of the truth"
        )
      )

      fetchResponse2 <- service.fetch(
        ids = Seq(testIds(0)), namespace = ""
      )

      _ <- service.delete(
        ids = testIds,
        namespace = ""
      )

      fetchResponse3 <- service.fetch(ids = testIds, namespace = "")
    } yield {
      println(stats)
      println()
      println(vectorUpsertedCount)
      println()
      println("Before update:")
      println(fetchResponse)
      println()
      println("After update:")
      println(fetchResponse2)
      println()
      println("After delete:")
      println(fetchResponse3)
      println()
      println()
      System.exit(0)
    }
  } recover {
    case e: Throwable =>
      println(e)
      System.exit(1)
  }
}
