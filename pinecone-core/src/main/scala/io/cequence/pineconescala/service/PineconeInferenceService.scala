package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.response.GenerateEmbeddingsResponse
import io.cequence.pineconescala.domain.settings.GenerateEmbeddingsSettings
import io.cequence.wsclient.service.CloseableService

import scala.concurrent.Future

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
  def createEmbeddings(
    inputs: Seq[String],
    settings: GenerateEmbeddingsSettings = DefaultSettings.GenerateEmbeddings
  ): Future[GenerateEmbeddingsResponse]

}
