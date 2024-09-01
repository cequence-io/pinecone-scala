package io.cequence.pineconescala.demo

import io.cequence.pineconescala.domain.RerankModelId
import io.cequence.pineconescala.domain.settings.RerankSettings

// run me - env. variable PINECONE_SCALA_CLIENT_API_KEY must be set
object Rerank extends PineconeDemoApp {

  override protected def exec = {
    val documents = Seq(
      Map(
        "id" -> "vec1",
        "my_field" -> "Apple is a popular fruit known for its sweetness and crisp texture."
      ),
      Map(
        "id" -> "vec2",
        "my_field" -> "Many people enjoy eating apples as a healthy snack."
      ),
      Map(
        "id" -> "vec3",
        "my_field" -> "Apple Inc. has revolutionized the tech industry with its sleek designs and user-friendly interfaces."
      ),
      Map(
        "id" -> "vec4",
        "my_field" -> "An apple a day keeps the doctor away, as the saying goes."
      )
    )

    pineconeInferenceService.rerank(
      query =
        "The tech company Apple is known for its innovative products like the iPhone.",
      documents = documents,
      settings = RerankSettings(
        model = RerankModelId.bge_reranker_v2_m3,
        top_n = Some(4),
        return_documents = true,
        rank_fields = Seq("my_field")
      )
    ).map { response =>
      response.data.foreach(println)
    }
  }
}
