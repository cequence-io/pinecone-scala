package io.cequence.pineconescala.service

import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import play.api.libs.ws.StandaloneWSRequest
import play.api.libs.json.Json
import io.cequence.pineconescala.JsonUtil.JsonOps
import io.cequence.pineconescala.JsonFormats._
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.pineconescala.domain.response._
import io.cequence.pineconescala.domain.{PVector, SparseVector}
import io.cequence.wsclient.service.ws.{Timeouts, WSRequestHelper}
import io.cequence.pineconescala.domain.response.IndexStats
import io.cequence.pineconescala.domain.settings.QuerySettings

import scala.concurrent.{ExecutionContext, Future}

/**
 * Private impl. class of [[PineconeVectorService]].
 *
 * @param apiKey
 * @param coreUrl
 * @param explTimeouts
 * @since Apr
 *   2023
 */
private class PineconeVectorServiceImpl(
  apiKey: String,
  override val coreUrl: String,
  explTimeouts: Option[Timeouts] = None
)(
  implicit val ec: ExecutionContext,
  val materializer: Materializer
) extends PineconeVectorService
    with WSRequestHelper {

  override protected type PEP = EndPoint
  override protected type PT = Tag

  override protected def timeouts: Timeouts =
    explTimeouts.getOrElse(
      Timeouts(
        requestTimeout = Some(defaultRequestTimeout),
        readTimeout = Some(defaultReadoutTimeout)
      )
    )

  override def describeIndexStats: Future[IndexStats] =
    execGET(EndPoint.describe_index_stats).map(
      _.asSafe[IndexStats]
    )

  override def query(
    vector: Seq[Double],
    namespace: String,
    settings: QuerySettings
  ): Future[QueryResponse] =
    execPOST(
      EndPoint.query,
      bodyParams = jsonBodyParams(
        Tag.vector -> Some(vector),
        Tag.namespace -> Some(namespace),
        Tag.topK -> Some(settings.topK),
        Tag.filter -> (if (settings.filter.nonEmpty) Some(settings.filter) else None),
        Tag.includeValues -> Some(settings.includeValues),
        Tag.includeMetadata -> Some(settings.includeMetadata),
        Tag.sparseVector -> settings.sparseVector.map(Json.toJson(_)(sparseVectorFormat))
      )
    ).map(
      _.asSafe[QueryResponse]
    )

  override def queryById(
    id: String,
    namespace: String,
    settings: QuerySettings
  ): Future[QueryResponse] =
    execPOST(
      EndPoint.query,
      bodyParams = jsonBodyParams(
        Tag.id -> Some(id),
        Tag.namespace -> Some(namespace),
        Tag.topK -> Some(settings.topK),
        Tag.filter -> (if (settings.filter.nonEmpty) Some(settings.filter) else None),
        Tag.includeValues -> Some(settings.includeValues),
        Tag.includeMetadata -> Some(settings.includeMetadata),
        Tag.sparseVector -> settings.sparseVector.map(Json.toJson(_)(sparseVectorFormat))
      )
    ).map(
      _.asSafe[QueryResponse]
    )

  override def listVectorIDs(
    namespace: String,
    limit: Option[Int],
    paginationToken: Option[String],
    prefix: Option[String]
  ): Future[ListVectorIdsResponse] =
    execGET(
      EndPoint.vectors_list,
      params = Seq(
        Tag.namespace -> Some(namespace),
        Tag.limit -> limit,
        Tag.paginationToken -> paginationToken,
        Tag.prefix -> prefix
      )
    ).map(
      _.asSafe[ListVectorIdsResponse]
    )

  override def delete(
    ids: Seq[String],
    namespace: String
  ): Future[Unit] =
    execPOST(
      EndPoint.vectors_delete,
      bodyParams = jsonBodyParams(
        Tag.ids -> Some(ids),
        Tag.namespace -> Some(namespace)
      )
    ).map(_ => ())

  override def delete(
    filter: Map[String, String],
    namespace: String
  ): Future[Unit] = {
    assert(filter.nonEmpty, "Filter must be defined.")

    execPOST(
      EndPoint.vectors_delete,
      bodyParams = jsonBodyParams(
        Tag.filter -> Some(filter),
        Tag.namespace -> Some(namespace)
      )
    ).map(_ => ())
  }

  override def deleteAll(
    namespace: String
  ): Future[Unit] =
    execPOST(
      EndPoint.vectors_delete,
      bodyParams = jsonBodyParams(
        Tag.deleteAll -> Some(true),
        Tag.namespace -> Some(namespace)
      )
    ).map(_ => ())

  override def fetch(
    ids: Seq[String],
    namespace: String
  ): Future[FetchResponse] =
    execGET(
      EndPoint.vectors_fetch,
      params = Seq(
        Tag.namespace -> Some(namespace)
      ) ++ ids.map(Tag.ids -> Some(_))
    ).map(
      _.asSafe[FetchResponse]
    )

  override def update(
    id: String,
    namespace: String,
    values: Seq[Double],
    sparseValues: Option[SparseVector],
    setMetaData: Map[String, String]
  ): Future[Unit] =
    execPOST(
      EndPoint.vectors_update,
      bodyParams = jsonBodyParams(
        Tag.id -> Some(id),
        Tag.namespace -> Some(namespace),
        Tag.values -> Some(values),
        Tag.sparseValues -> sparseValues.map(Json.toJson(_)),
        Tag.setMetadata -> (if (setMetaData.nonEmpty) Some(setMetaData) else None)
      )
    ).map(_ => ())

  override def upsert(
    vectors: Seq[PVector],
    namespace: String
  ): Future[Int] =
    execPOST(
      EndPoint.vectors_upsert,
      bodyParams = jsonBodyParams(
        Tag.vectors -> Some(vectors.map(Json.toJson(_))),
        Tag.namespace -> Some(namespace)
      )
    ).map(json =>
      (json \ "upsertedCount").toOption match {
        case Some(upsertedCountJson) => upsertedCountJson.asSafe[Int]
        case None =>
          throw new PineconeScalaClientException(
            s"Upsert should return 'upsertedCount' but got: ${Json.prettyPrint(json)}."
          )
      }
    )

  // aux

  private def addHeaders(request: StandaloneWSRequest) = {
    val apiKeyHeader = ("Api-Key", apiKey)
    request.addHttpHeaders(apiKeyHeader)
  }
}

object PineconeVectorServiceFactory extends PineconeServiceFactoryHelper {

  def apply(
    indexName: String
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): Future[Option[PineconeVectorService]] =
    apply(indexName, ConfigFactory.load(configFileName))

  def apply(
    indexName: String,
    config: Config
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): Future[Option[PineconeVectorService]] = {
    val timeouts = loadTimeouts(config)

    apply(
      apiKey = config.getString(s"$configPrefix.apiKey"),
      indexName = indexName,
      timeouts = timeoutsToOption(timeouts),
      pineconeIndexService = PineconeIndexServiceFactory(config)
    )
  }

  def apply(
    apiKey: String,
    indexName: String,
    timeouts: Option[Timeouts] = None,
    pineconeIndexService: PineconeIndexService
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): Future[Option[PineconeVectorService]] =
    pineconeIndexService
      .describeIndex(indexName)
      .map(_.map(indexInfo => apply(apiKey, s"https://${indexInfo.host}", timeouts)))

  def apply(
    apiKey: String,
    indexHostURL: String,
    timeouts: Option[Timeouts]
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeVectorService = {
    val indexHostURLWithEndingSlash =
      if (indexHostURL.endsWith("/")) indexHostURL else s"$indexHostURL/"
    new PineconeVectorServiceImpl(apiKey, indexHostURLWithEndingSlash, timeouts)
  }
}
