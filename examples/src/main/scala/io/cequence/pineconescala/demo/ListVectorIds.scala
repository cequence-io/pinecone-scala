package io.cequence.pineconescala.demo

import io.cequence.pineconescala.service.PineconeServiceConsts

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object ListVectorIds extends PineconeDemoApp with PineconeServiceConsts {

  private lazy val indexName = "auto-gpt-test"
  private lazy val namespace = "wild-test"

  override protected def exec = {
    for {
      vectorService <- createPineconeVectorService(indexName)

      queryResponse <- vectorService.listVectorIDs(
        namespace = namespace,
        limit = Some(10)
      )
    } yield {
      val ids = queryResponse.vectors.map(_.id).mkString(", ")
      println(s"Namespace : ${queryResponse.namespace}")
      println(s"Vector Ids: ${ids}")
      println(s"Pagination: ${queryResponse.pagination}")
    }
  }

}
