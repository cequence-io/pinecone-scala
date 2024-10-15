package io.cequence.pineconescala.demo

// run me - env. variable PINECONE_SCALA_CLIENT_API_KEY must be set
object Evaluate extends PineconeDemoApp {

  override protected def exec = {
    pineconeInferenceService.evaluate(
      question = "What are the capital cities of France, England and Spain?",
      answer = "Paris is a city of France and Barcelona of Spain",
      groundTruthAnswer = "Paris is the capital city of France, London of England and Madrid of Spain"
    ).map { response =>
      println(response)
    }
  }
}
