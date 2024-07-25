package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.response.GenerateEmbeddingsResponse
import io.cequence.pineconescala.domain.settings.GenerateEmbeddingsSettings
import io.cequence.wsclient.service.CloseableService

import scala.concurrent.Future

/**
 * Pinecone inference operations as defined at <a
 * href="https://docs.pinecone.io/reference/api/2024-07/inference">the API ref. page</a>
 *
 * The following services are supported:
 *
 *   - createEmbeddings
 *
 * @since May
 *   2024
 */
trait PineconeInferenceService extends CloseableService with PineconeServiceConsts {

  /**
   * Uses the specified model to generate embeddings for the input sequence.
   *
   * @param inputs
   *   Input sequence for which to generate embeddings.
   * @param settings
   * @return
   *   list of embeddings inside an envelope
   */
  // TODO: rename to embedData to be consistent with the API
  def createEmbeddings(
    inputs: Seq[String],
    settings: GenerateEmbeddingsSettings = DefaultSettings.GenerateEmbeddings
  ): Future[GenerateEmbeddingsResponse]
}
