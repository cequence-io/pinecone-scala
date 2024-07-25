package io.cequence.pineconescala.demo

import io.cequence.pineconescala.domain.response.CreateResponse
import io.cequence.pineconescala.service.PineconeServiceConsts

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object CreateServerlessIndex extends PineconeDemoApp with PineconeServiceConsts {
  override protected def exec =
    pineconeServerlessIndexService
      .createIndex(
        name = "auto-gpt-test",
        dimension = 1536,
        DefaultSettings.CreateServerlessIndex
      )
      .map {
        case CreateResponse.Created => println("Index successfully created.")
        case CreateResponse.BadRequest =>
          println("Index creation failed. Request exceeds quota or an invalid index name.")
        case CreateResponse.AlreadyExists => println("Index with a given name already exists.")
      }
}
