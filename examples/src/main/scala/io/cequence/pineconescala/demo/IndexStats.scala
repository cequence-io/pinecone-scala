package io.cequence.pineconescala.demo

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object IndexStats extends PineconeDemoApp {
  override protected def exec =
    createPineconeVectorService("auto-gpt-test").flatMap(
      _.describeIndexStats
    ).map { queryResponse =>
      val namespaces = queryResponse.namespaces.keys.mkString("\n")
      println(s"Namespaces \n${namespaces}")
    }
}