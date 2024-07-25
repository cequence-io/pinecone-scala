package io.cequence.pineconescala.demo

import io.cequence.pineconescala.domain.PVector

import scala.util.Random

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object UpsertVectors extends PineconeDemoApp {
  override protected def exec =
    createPineconeVectorService("auto-gpt-test")
      .flatMap(
        _.upsert(
          vectors = Seq(
            PVector(
              id = "666",
              values = Seq.fill(1536)(Random.nextDouble),
              metadata = Map(
                "is_relevant" -> "not really but for testing it's ok, you know",
                "food_quality" -> "brunches are perfect but don't go there before closing time"
              ),
              sparseValues = None
            ),
            PVector(
              id = "777",
              values = Seq.fill(1536)(Random.nextDouble),
              metadata = Map(
                "is_relevant" -> "very much so",
                "food_quality" -> "burritos are the best!"
              ),
              sparseValues = None
            )
          ),
          namespace = "my_namespace"
        )
      )
      .map(vectorUpsertedCount => println(s"Upserted $vectorUpsertedCount vectors."))
}
