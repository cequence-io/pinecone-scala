package io.cequence.pineconescala.demo

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV (optional) must be set
object ListIndexes extends PineconeDemoApp {
  override protected def exec =
    pineconeIndexService.listIndexes.map(
      _.foreach(println)
    )
}
