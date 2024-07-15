package io.cequence.pineconescala.demo

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object ListVectorIds extends PineconeDemoApp {
  override protected def exec =
    createPineconeVectorService("auto-gpt-test").flatMap(
      _.listVectorIDs(
        namespace = "wild-test",
        limit = Some(10),
      )
    ).map { queryResponse =>
      val ids = queryResponse.vectors.map(_.id).mkString(", ")
      println(s"Vector Ids: ${ids}")
      println(s"Pagination: ${queryResponse.pagination}")
      println(s"Namespace : ${queryResponse.namespace}")
    }
}