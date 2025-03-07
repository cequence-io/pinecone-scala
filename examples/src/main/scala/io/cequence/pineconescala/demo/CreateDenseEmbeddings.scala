package io.cequence.pineconescala.demo

import io.cequence.pineconescala.domain.EmbeddingModelId
import io.cequence.pineconescala.domain.settings.{EmbeddingsInputType, GenerateEmbeddingsSettings}

// run me - env. variable PINECONE_SCALA_CLIENT_API_KEY must be set
object CreateDenseEmbeddings extends PineconeDemoApp {

  override protected def exec = {
    pineconeInferenceService.createEmbeddings(
      inputs = Seq(
        "What are the capital cities of France, England and Spain?",
        "Paris is the capital city of France and Barcelona of Spain",
        "Paris is the capital city of France, London of England and Madrid of Spain"
      ),
      settings = GenerateEmbeddingsSettings(
        model = EmbeddingModelId.llama_text_embed_v2,
        input_type = Some(EmbeddingsInputType.Query)
      )
    ).map { response =>
      println(response)
    }
  }
}
