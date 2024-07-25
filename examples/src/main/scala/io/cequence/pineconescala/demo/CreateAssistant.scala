package io.cequence.pineconescala.demo

import scala.concurrent.Future

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object CreateAssistant extends PineconeDemoApp {

  override protected def exec: Future[_] = {
    val assistantName = s"assistant-${System.currentTimeMillis()}"
    pineconeAssistantService.createAssistant(assistantName).map(println)
  }
}
