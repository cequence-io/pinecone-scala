package io.cequence.pineconescala.demo

import io.cequence.pineconescala.domain.response.CreateResponse

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object CreateIndex extends PineconeDemoApp {
  override protected def exec =
    pineconeIndexService.createIndex(
      name = "auto-gpt-test",
      dimension = 1536
    ).map(
      _ match {
        case CreateResponse.Created => println("Index successfully created.")
        case CreateResponse.BadRequest => println("Index creation failed. Request exceeds quota or an invalid index name.")
        case CreateResponse.AlreadyExists => println("Index with a given name already exists.")
      }
    )
}