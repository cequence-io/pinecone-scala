package io.cequence.pineconescala.demo

import io.cequence.pineconescala.demo.CreateAssistant.pineconeAssistantService

import scala.concurrent.Future

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object DescribeAssistant extends PineconeDemoApp{

  override protected def exec: Future[_] = {
    val assistantName = s"assistant-${System.currentTimeMillis()}"
    for {
      _ <- pineconeAssistantService.createAssistant(assistantName)
      assistantInfo <- pineconeAssistantService.describeAssistant(assistantName)
    } yield println(assistantInfo)
  }

}
