package io.cequence.pineconescala.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.pineconescala.domain.RerankModelId
import io.cequence.pineconescala.domain.settings.RerankSettings
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.ExecutionContext

class ServerlessPineconeInferenceServiceImplSpec
    extends AsyncWordSpec
    with GivenWhenThen
    with ServerlessFixtures
    with Matchers
    with PineconeServiceConsts {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())

  val serverlessConfig: Config = ConfigFactory.load("serverless.conf")

  def inferenceServiceBuilder: PineconeInferenceService =
    PineconeInferenceServiceFactory(serverlessConfig)

  "Pinecone Inference Service" when {

    "create embeddings should provide embeddings for input data" in {
      val service = inferenceServiceBuilder
      for {
        embeddings <- service.createEmbeddings(
          Seq("The quick brown fox jumped over the lazy dog"),
          settings = DefaultSettings.GenerateEmbeddings.withPassageInputType.withEndTruncate
        )
      } yield {
        embeddings.data.size should be(1)
        embeddings.data(0).values should not be empty
        embeddings.usage.total_tokens should be(16)
      }
    }

    "rerank documents" in {
      val service = inferenceServiceBuilder

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

      for {
        rerankResponse <- service.rerank(
          query =
            "The tech company Apple is known for its innovative products like the iPhone.",
          documents = documents,
          settings = RerankSettings(
            model = RerankModelId.bge_reranker_v2_m3,
            top_n = Some(4),
            return_documents = true,
            rank_fields = Seq("my_field")
          )
        )
      } yield {
        rerankResponse.data.size should be(4)
        rerankResponse.usage.rerank_units should be(1)
        rerankResponse.data.map(_.index) should be(Seq(2, 0, 3, 1))

        def docEq(origIndex: Int, responseIndex: Int) =
          rerankResponse.data(origIndex).document.getOrElse(
            throw new PineconeScalaClientException("Document missing")
          ) should be(documents(responseIndex))

        docEq(0, 2)
        docEq(1, 0)
        docEq(2, 3)
        docEq(3, 1)
      }
    }
  }
}
