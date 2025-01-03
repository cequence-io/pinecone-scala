package io.cequence.pineconescala.demo

import io.cequence.pineconescala.service.PineconeServiceConsts

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object ListAllVectorIds extends PineconeDemoApp with PineconeServiceConsts {

  private lazy val indexName = "auto-gpt-test"
  private lazy val namespace = "auto-gpt"

  override protected def exec = {
    for {
      vectorService <- createPineconeVectorService(indexName)

      queryResponse <- vectorService.listAllVectorsIDs(
        namespace = namespace,
        batchLimit = Some(20)
      )
    } yield {
      val ids = queryResponse.map(_.id)
      println(s"Vector Ids: ${ids.size}")
    }
  }

}
