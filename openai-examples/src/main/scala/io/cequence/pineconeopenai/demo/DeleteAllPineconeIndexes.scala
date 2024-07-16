package io.cequence.pineconeopenai.demo

import scala.concurrent.Future

/**
 * This demo deletes all indexes. Be careful!
 *
 * The following env. variables are expected:
 *  - PINECONE_SCALA_CLIENT_API_KEY
 *  - PINECONE_SCALA_CLIENT_ENV
 */
object DeleteAllPineconeIndexes extends PineconeOpenAIDemoApp {

  override protected def exec =
    pineconeIndexService.listIndexes.flatMap { indexes =>
      Future.sequence(
        indexes.map(pineconeIndexService.deleteIndex)
      )
    }.map(_ => ())
}