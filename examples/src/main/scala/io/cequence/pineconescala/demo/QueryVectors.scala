package io.cequence.pineconescala.demo

import scala.util.Random

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object QueryVectors extends PineconeDemoApp {

  private val indexName = "auto-gpt-test"
  private val namespace = "my_namespace"

  override protected def exec =
    createPineconeVectorService(indexName)
      .flatMap(
        _.query(
          vector = Seq.fill(1536)(Random.nextDouble), // some values/embeddings
          namespace
        )
      )
      .map { queryResponse =>
        queryResponse.matches.foreach { matchInfo =>
          println(s"Matched vector id: ${matchInfo.id}")
          println(s"Matched vector values: ${matchInfo.values.take(20).mkString(", ")}..") // by default values are not included
          println(s"Matched vector score: ${matchInfo.score}")
          println(s"Matched vector metadata: ${matchInfo.metadataUnwrapped}") // by default metadata is included
        }
      }
}
