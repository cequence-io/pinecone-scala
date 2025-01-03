package io.cequence.pineconescala.demo

import io.cequence.pineconescala.domain.EmbeddingModelId
import io.cequence.pineconescala.domain.settings.{EmbeddingsInputType, GenerateEmbeddingsSettings}

// run me - env. variable PINECONE_SCALA_CLIENT_API_KEY must be set
object CreateSparseEmbeddings extends PineconeDemoApp {

  override protected def exec = {
    pineconeInferenceService.createSparseEmbeddings(
      inputs = Seq(
        "What are the capital cities of France, England and Spain?",
        "Paris is the capital city of France and Barcelona of Spain",
        "Paris is the capital city of France, London of England and Madrid of Spain"
      ),
      settings = GenerateEmbeddingsSettings(
        model = EmbeddingModelId.pinecone_sparse_english_v0,
        input_type = Some(EmbeddingsInputType.Passage),
        return_tokens = true
      )
    ).map { response =>
      response.data.foreach { data =>
        println(data.sparse_indices.mkString(", "))
      }
      response.data.foreach { data =>
        println(data.toSparseVector.indices.mkString(", "))
      }
    }
  }
}
