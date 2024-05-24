package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.PodType
import io.cequence.pineconescala.domain.response.{ConfigureIndexResponse, CreateResponse}

import scala.concurrent.Future

trait PineconePodBasedSpecifics {

  /**
   * This operation returns a list of your Pinecone collections.
   *
   * @return
   *   List of collections
   * @see
   *   <a href="https://docs.pinecone.io/reference/list_collections">Pinecone Doc</a>
   */
  def listCollections: Future[Seq[String]]

  /**
   * This operation creates a Pinecone collection.
   *
   * @param name
   *   The name of the collection to be created.
   * @param source
   *   The name of the source index to be used as the source for the collection.
   * @return
   *   Whether the collection was created successfully or not.
   * @see
   *   <a href="https://docs.pinecone.io/reference/create_collection">Pinecone Doc</a>
   */
  def createCollection(
    name: String,
    source: String
  ): Future[CreateResponse]

  /**
   * This operation specifies the pod type and number of replicas for an index.
   *
   * @param indexName
   *   The name of the index
   * @param replicas
   *   The desired number of replicas for the index.
   * @param podType
   *   The new pod type for the index.
   * @return
   *   Whether the index was updated successfully or not found.
   * @see
   *   <a href="https://docs.pinecone.io/reference/configure_index">Pinecone Doc</a>
   */
  def configureIndex(
    indexName: String,
    replicas: Option[Int],
    podType: Option[PodType]
  ): Future[ConfigureIndexResponse]

}
