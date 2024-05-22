package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.{IndexEnv, Metric, PodType}
import io.cequence.pineconescala.domain.response._
import io.cequence.pineconescala.domain.settings.{IndexSettings, IndexSettingsType}
import scala.concurrent.Future

/**
 * Central service to access all Pinecone vector operations/endpoints as defined at <a
 * href="https://docs.pinecone.io/reference">the API ref. page</a>
 *
 * The following services are supported:
 *
 *   - '''Collection Operations''': listCollections, createCollection, describeCollection, and
 *     deleteCollection
 *
 *   - '''Index Operations''': listIndexes, creatIndex, describeIndex, deleteIndex, and
 *     configureIndex
 *
 * @since Apr
 *   2023
 */
// TODO: introduce PineconeServerlessIndexService and PineconePodBasedIndexService (to be returned from PineconeIndexServiceFactory instead of impl classes)
trait PineconeIndexService[S <: IndexSettingsType] extends PineconeServiceConsts {

  /**
   * Get a description of a collection.
   *
   * @param collectionName
   *   The name of the collection
   * @return
   *   Configuration information and deployment status of the collection (if found)
   * @see
   *   <a href="https://docs.pinecone.io/reference/describe_collection">Pinecone Doc</a>
   */
  def describeCollection(
    collectionName: String
  ): Future[Option[CollectionInfo]]

  /**
   * This operation deletes an existing collection.
   *
   * @param collectionName
   *   The name of the collection
   * @return
   *   Whether the collection was deleted successfully or not found.
   * @see
   *   <a href="https://docs.pinecone.io/reference/delete_collection">Pinecone Doc</a>
   */
  def deleteCollection(
    collectionName: String
  ): Future[DeleteResponse]

  /**
   * This operation returns a list of your Pinecone indexes.
   *
   * @return
   *   List of indexes associated with the account (API key)
   * @see
   *   <a href="https://docs.pinecone.io/reference/list_indexes">Pinecone Doc</a>
   */
  def listIndexes: Future[Seq[String]]

  /**
   * This operation creates a Pinecone index. You can use it to specify the measure of
   * similarity, the dimension of vectors to be stored in the index, the numbers of replicas to
   * use, and more.
   *
   * @param name
   *   The name of the index to be created. The maximum length is 45 characters.
   * @param dimension
   *   The dimensions of the vectors to be inserted in the index
   * @param settings
   *   The settings for the index
   * @return
   *   Whether the index was created successfully or not.
   * @see
   *   <a href="https://docs.pinecone.io/reference/create_index">Pinecone Doc</a>
   */
  def createIndex(
    name: String,
    dimension: Int,
    metric: Metric.Value,
    settings: S // CreatePodBasedIndexSettings = DefaultSettings.CreateIndex
  ): Future[CreateResponse]

  /**
   * Get a description of an index.
   *
   * @param indexName
   *   The name of the index
   * @return
   *   Configuration information and deployment status of the index (if found)
   * @see
   *   <a href="https://docs.pinecone.io/reference/describe_index">Pinecone Doc</a>
   */
  def describeIndex(
    indexName: String
  ): Future[Option[IndexInfo]]

  /**
   * This operation deletes an existing index.
   *
   * @param indexName
   *   The name of the index
   * @return
   *   Whether the index was deleted successfully or not found.
   * @see
   *   <a href="https://docs.pinecone.io/reference/delete_index">Pinecone Doc</a>
   */
  def deleteIndex(
    indexName: String
  ): Future[DeleteResponse]

  /**
   * Closes the underlying ws client, and releases all its resources.
   */
  def close(): Unit
}
