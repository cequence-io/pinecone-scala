package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.{PVector, SparseVector}

import scala.util.Random

trait TextFixtures {

  val dimensions = 1536
  val indexName = "openai-embeddings-1536"
  val namespace = "pinecone-test"
  val testIds = Seq("666", "667")

  val vector1: PVector = PVector(
    id = testIds(0),
    values = Seq.fill(dimensions)(Random.nextDouble),
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
  )
  val vector2: PVector = PVector(
    id = testIds(1),
    values = Seq.fill(dimensions)(Random.nextDouble),
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

}
