package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.{PVector, SparseVector}
import io.cequence.pineconescala.domain.response._
import io.cequence.pineconescala.domain.response.IndexStats
import io.cequence.pineconescala.domain.settings.QuerySettings

import scala.concurrent.Future

/**
 * Central service to access all Pinecone vector operations/endpoints as defined at <a
 * href="https://docs.pinecone.io/reference">the API ref. page</a>
 *
 * The following services are supported:
 *
 *   - describeIndexStats
 *   - query - by vector or by id (queryById)
 *   - delete - by filter or ids, or delete all
 *   - fetch
 *   - update
 *   - upsert
 *   - listVectorIDs
 *
 * @since Apr
 *   2023
 */
trait PineconeVectorService extends PineconeServiceConsts {

  /**
   * The DescribeIndexStats operation returns statistics about the index's contents, including
   * the vector count per namespace and the number of dimensions.
   *
   * @return
   *   IndexStats
   * @see
   *   <a href="https://docs.pinecone.io/reference/describe_index_stats_post">Pinecone Doc</a>
   */
  def describeIndexStats: Future[IndexStats]

  /**
   * The Query operation searches a namespace, using a query vector. It retrieves the ids of
   * the most similar items in a namespace, along with their similarity scores.
   *
   * @param vector
   *   The query vector. This should be the same length as the dimension of the index being
   *   queried.
   * @param sparseVector
   *   Represented as a list of indices and a list of corresponded values, which must be the
   *   same length.
   * @param namespace
   *   The namespace to query.
   * @return
   *   model or None if not found
   * @see
   *   <a href="https://docs.pinecone.io/reference/query">Pinecone Doc</a>
   */
  def query(
    vector: Seq[Double],
    namespace: String,
    sparseVector: Option[SparseVector] = None,
    settings: QuerySettings = DefaultSettings.Query
  ): Future[QueryResponse]

  /**
   * The Query operation searches a namespace, using an unique id of the vector. It retrieves
   * the ids of the most similar items in a namespace, along with their similarity scores.
   *
   * @param id
   *   The unique ID of the vector to be used as a query vector.
   * @param namespace
   * @param settings
   * @return
   *   QueryResult
   * @see
   *   <a href="https://docs.pinecone.io/reference/query">Pinecone Doc</a>
   */
  def queryById(
    id: String,
    namespace: String,
    settings: QuerySettings = DefaultSettings.Query
  ): Future[QueryResponse]

  /**
   * The list operation lists the IDs of vectors in a single namespace. An optional prefix can
   * be passed to limit the results to IDs with a common prefix.
   *
   * Note: This operation seems to be working only for serverless indexes.
   *
   * It returns up to 100 IDs at a time by default in sorted order (bitwise/"C" collation). If
   * the limit parameter is set, list returns up to that number of IDs instead. Whenever there
   * are additional IDs to return, the response also includes a pagination_token that you can
   * use to get the next batch of IDs. When the response does not includes a pagination_token,
   * there are no more IDs to return.
   *
   * @param namespace
   * @param limit
   * @param paginationToken
   * @param prefix
   *
   * @return
   *   List of vector IDs wrapped in a ListVectorIdsResponse
   * @see
   *   <a href="https://docs.pinecone.io/reference/list">Pinecone Doc</a>
   */
  def listVectorIDs(
    namespace: String,
    limit: Option[Int] = None,
    paginationToken: Option[String] = None,
    prefix: Option[String] = None
  ): Future[ListVectorIdsResponse]

  /**
   * Same as [[listVectorIDs]] but returns all the IDs in the namespace.
   *
   * @param namespace
   * @param batchLimit
   * @param prefix
   * @return
   */
  def listAllVectorsIDs(
    namespace: String,
    batchLimit: Option[Int] = None,
    prefix: Option[String] = None
  ): Future[Seq[VectorId]]

  /**
   * The Delete operation deletes vectors, by id, from a single namespace.
   *
   * @param ids
   *   Vectors to delete.
   * @param namespace
   *   The namespace to delete vectors from, if applicable.
   * @return
   *   N/A
   * @see
   *   <a href="https://docs.pinecone.io/reference/delete_post">Pinecone Doc</a>
   */
  def delete(
    ids: Seq[String],
    namespace: String
  ): Future[Unit]

  /**
   * The Delete operation deletes vectors, by the metadata filter, from a single namespace.
   *
   * @param filter
   *   The metadata filter here will be used to select the vectors to delete. See
   *   https://www.pinecone.io/docs/metadata-filtering/.
   * @param namespace
   * @return
   * @see
   *   <a href="https://docs.pinecone.io/reference/delete_post">Pinecone Doc</a>
   */
  def delete(
    filter: Map[String, String],
    namespace: String
  ): Future[Unit]

  /**
   * The Delete operation deletes ALL the vectors from a single namespace.
   *
   * @param namespace
   * @return
   * @see
   *   <a href="https://docs.pinecone.io/reference/delete_post">Pinecone Doc</a>
   */
  def deleteAll(
    namespace: String
  ): Future[Unit]

  /**
   * The Fetch operation looks up and returns vectors, by ID, from a single namespace. The
   * returned vectors include the vector data and/or metadata.
   *
   * @param id
   *   The vector IDs to fetch. Does not accept values containing spaces.
   * @param namespace
   * @return
   * @see
   *   <a href="https://docs.pinecone.io/reference/fetch">Pinecone Doc</a>
   */
  def fetch(
    ids: Seq[String],
    namespace: String
  ): Future[FetchResponse]

  /**
   * The Update operation updates vector in a namespace. If a value is included, it will
   * overwrite the previous value. If a set_metadata is included, the values of the fields
   * specified in it will be added or overwrite the previous value.
   *
   * @param id
   *   Vector's unique id.
   * @param namespace
   *   The namespace containing the vector to update.
   * @param values
   *   Vector data.
   * @param sparseValues
   *   Vector sparse data. Represented as a list of indices and a list of corresponded values,
   *   which must be the same length.
   * @param setMetaData
   *   Metadata to set for the vector.
   * @return
   *   N/A
   * @see
   *   <a href="https://docs.pinecone.io/reference/update">Pinecone Doc</a>
   */
  def update(
    id: String,
    namespace: String,
    values: Seq[Double],
    sparseValues: Option[SparseVector] = None,
    setMetaData: Map[String, String] = Map()
  ): Future[Unit]

  /**
   * The Upsert operation writes vectors into a namespace. If a new value is upserted for an
   * existing vector id, it will overwrite the previous value.
   *
   * @param vectors
   *   An array containing the vectors to upsert. Recommended batch limit is 100 vectors.
   * @param namespace
   *   This is the namespace name where you upsert vectors.
   * @return
   *   The number of vectors upserted.
   * @see
   *   <a href="https://docs.pinecone.io/reference/upsert">Pinecone Doc</a>
   */
  def upsert(
    vectors: Seq[PVector],
    namespace: String
  ): Future[Int]

  /**
   * Closes the underlying ws client, and releases all its resources.
   */
  def close(): Unit
}
