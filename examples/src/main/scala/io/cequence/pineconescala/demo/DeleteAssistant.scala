package io.cequence.pineconescala.demo

import io.cequence.pineconescala.domain.response.DeleteResponse

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object DeleteAssistant extends PineconeDemoApp {

  override protected def exec =
    pineconeAssistantService.deleteAssistant("my-assistant").map {
      case DeleteResponse.Deleted  => println("Assistant successfully deleted.")
      case DeleteResponse.NotFound => println("Assistant with a given name not found.")
    }
}
