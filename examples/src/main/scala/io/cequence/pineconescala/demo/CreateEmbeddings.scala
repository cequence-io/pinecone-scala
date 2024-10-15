package io.cequence.pineconescala.demo

// run me - env. variable PINECONE_SCALA_CLIENT_API_KEY must be set
object CreateEmbeddings extends PineconeDemoApp {

  override protected def exec = {
    pineconeInferenceService.createEmbeddings(
      inputs = Seq(
        "What are the capital cities of France, England and Spain?",
        "Paris is the capital city of France and Barcelona of Spain",
        "Paris is the capital city of France, London of England and Madrid of Spain"
      )
    ).map { response =>
      println(response)
    }
  }
}
