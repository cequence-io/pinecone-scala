package io.cequence.pineconescala.demo

import io.cequence.pineconescala.service.{PineconeIndexServiceFactory, PineconeServiceConsts}

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object ListVectorIds extends PineconeDemoApp with PineconeServiceConsts {
  override protected def exec = {
    for {
      _ <-  PineconeIndexServiceFactory().right.get.createIndex(
        name = "auto-gpt-test",
        dimension = 1536,
        DefaultSettings.CreateServerlessIndex
      )
      vectorService <- createPineconeVectorService("auto-gpt-test")
      queryResponse <- vectorService.listVectorIDs(
        namespace = "wild-test",
        limit = Some(10)
      )
    } yield {
      val ids = queryResponse.vectors.map(_.id).mkString(", ")
      println(s"Vector Ids: ${ids}")
      println(s"Pagination: ${queryResponse.pagination}")
      println(s"Namespace : ${queryResponse.namespace}")
    }
  }

}
