package io.cequence.pineconescala.demo

import io.cequence.pineconescala.domain.response.DeleteResponse

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object DeleteIndex extends PineconeDemoApp {
  override protected def exec =
    pineconeIndexService.deleteIndex("auto-gpt-test").map(
      _ match {
        case DeleteResponse.Deleted => println("Index successfully deleted.")
        case DeleteResponse.NotFound => println("Index with a given name not found.")
      }
    )
}