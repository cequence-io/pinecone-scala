package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.response.{GenerateEmbeddingsResponse, RerankResponse}
import io.cequence.pineconescala.domain.settings.{GenerateEmbeddingsSettings, RerankSettings}
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
   *
   * @see <a href="https://docs.pinecone.io/reference/api/2024-10/inference/generate-embeddings">Pinecone Doc</a>
   */
  // TODO: rename to embedData to be consistent with the API
  def createEmbeddings(
    inputs: Seq[String],
    settings: GenerateEmbeddingsSettings = DefaultSettings.GenerateEmbeddings
  ): Future[GenerateEmbeddingsResponse]

  /**
   * Using a reranker to rerank a list of items for a query.
   *
   * @param query The query to rerank documents against (required)
   * @param documents The documents to rerank (required)
   * @param settings
   * @return
   *
   * @see <a href="https://docs.pinecone.io/reference/api/2024-10/inference/rerank">Pinecone Doc</a>
   */
  def rerank(
    query: String,
    documents: Seq[Map[String, Any]],
    settings: RerankSettings = DefaultSettings.Rerank
  ): Future[RerankResponse]
}
