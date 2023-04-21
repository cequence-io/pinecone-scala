package  io.cequence.pineconescala.service

import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import play.api.libs.ws.StandaloneWSRequest
import play.api.libs.json.Json
import io.cequence.pineconescala.JsonUtil.JsonOps
import io.cequence.pineconescala.JsonFormats._
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.pineconescala.domain.settings._
import io.cequence.pineconescala.domain.response._
import io.cequence.pineconescala.ConfigImplicits._
import io.cequence.pineconescala.domain.{PVector, SparseVector}
import io.cequence.pineconescala.service.ws.{Timeouts, WSRequestHelper}
import io.cequence.pineconescala.domain.response.IndexStats
import io.cequence.pineconescala.domain.settings.QuerySettings

import java.io.File
import scala.concurrent.{ExecutionContext, Future}

/**
 * Private impl. class of [[PineconeVectorService]].
 *
 * @param apiKey
 * @param environment
 * @param indexName
 * @since Apr 2023
 */
private class PineconeVectorServiceImpl(
  apiKey: String,
  environment: String,
  indexName: String,
  explTimeouts: Option[Timeouts] = None)(
  implicit val ec: ExecutionContext, val materializer: Materializer
) extends PineconeVectorService with WSRequestHelper {

  override protected type PEP = Command.type#Value
  override protected type PT = Tag.type#Value
  override protected val coreUrl = s"https://${indexName}.svc.${environment}.pinecone.io/"

//  private val logger = LoggerFactory.getLogger("PineconeVectorService")

  override protected def timeouts: Timeouts =
    explTimeouts.getOrElse(
      Timeouts(
        requestTimeout = Some(defaultRequestTimeout),
        readTimeout = Some(defaultReadoutTimeout)
      )
    )

  override def describeIndexStats: Future[IndexStats] =
    execGET(Command.describe_index_stats).map(
      _.asSafe[IndexStats]
    )

  override def query(
    vector: Seq[Double],
    namespace: String,
    settings: QuerySettings
  ): Future[QueryResponse] =
    execPOST(
      Command.query,
      bodyParams = jsonBodyParams(
        Tag.vector -> Some(vector),
        Tag.namespace -> Some(namespace),
        Tag.topK -> Some(settings.topK),
        Tag.filter -> (if (settings.filter.nonEmpty) Some(settings.filter) else None),
        Tag.includeValues -> Some(settings.includeValues),
        Tag.includeMetadata -> Some(settings.includeMetadata),
        Tag.sparseVector -> settings.sparseVector.map(Json.toJson(_)(sparseVectorFormat))
      )
    ).map { json =>
//      println(Json.prettyPrint(json))
      json.asSafe[QueryResponse]
    }

  override def query(
    id: String,
    namespace: String,
    settings: QuerySettings
  ): Future[QueryResponse] =
    execPOST(
      Command.query,
      bodyParams = jsonBodyParams(
        Tag.id -> Some(id),
        Tag.namespace -> Some(namespace),
        Tag.topK -> Some(settings.topK),
        Tag.filter -> (if (settings.filter.nonEmpty) Some(settings.filter) else None),
        Tag.includeValues -> Some(settings.includeValues),
        Tag.includeMetadata -> Some(settings.includeMetadata),
        Tag.sparseVector -> settings.sparseVector.map(Json.toJson(_)(sparseVectorFormat)),
      )
    ).map { json =>
//      println(Json.prettyPrint(json))
      json.asSafe[QueryResponse]
    }

  override def delete(
    ids: Seq[String],
    namespace: String
  ): Future[Unit] =
    execPOST(
      Command.vectors_delete,
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
      Command.vectors_delete,
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
      Command.vectors_delete,
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
      Command.vectors_fetch,
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
      Command.vectors_update,
      bodyParams = jsonBodyParams(
        Tag.id -> Some(id),
        Tag.namespace -> Some(namespace),
        Tag.values_ -> Some(values),
        Tag.sparseValues -> sparseValues.map(Json.toJson(_)),
        Tag.setMetadata -> (if (setMetaData.nonEmpty) Some(setMetaData) else None)
      )
    ).map(_ => ())

  override def upsert(
    vectors: Seq[PVector],
    namespace: String
  ): Future[Int] =
    execPOST(
      Command.vectors_upsert,
      bodyParams = jsonBodyParams(
        Tag.vectors -> Some(vectors.map(Json.toJson(_))),
        Tag.namespace -> Some(namespace)
      )
    ).map(json =>
      (json \ "upsertedCount").toOption match {
        case Some(upsertedCountJson) => upsertedCountJson.asSafe[Int]
        case None => throw new PineconeScalaClientException(s"Upsert should return 'upsertedCount' but got: ${Json.prettyPrint(json)}.")
      }
    )

  // aux

  override protected def getWSRequestOptional(
    endPoint: Option[PEP],
    endPointParam: Option[String],
    params: Seq[(PT, Option[Any])] = Nil
  ) =
    addHeaders(super.getWSRequestOptional(endPoint, endPointParam, params))

  override protected def getWSRequest(
    endPoint: Option[PEP],
    endPointParam: Option[String],
    params: Seq[(PT, Any)] = Nil
  ) =
    addHeaders(super.getWSRequest(endPoint, endPointParam, params))

  private def addHeaders(request: StandaloneWSRequest) = {
    val apiKeyHeader = ("Api-Key", apiKey)
    request.addHttpHeaders(apiKeyHeader)
  }
}

object PineconeServiceFactory extends PineconeServiceFactoryHelper {

  def apply(
    apiKey: String,
    environment: String,
    indexName: String,
    timeouts: Option[Timeouts] = None)(
    implicit ec: ExecutionContext, materializer: Materializer
  ): PineconeVectorService =
    new PineconeVectorServiceImpl(apiKey, environment, indexName, timeouts)

  def apply(
    indexName: String)(
    implicit ec: ExecutionContext, materializer: Materializer
  ): PineconeVectorService =
    apply(indexName, ConfigFactory.load(configFileName))

  def apply(
    indexName: String,
    config: Config)(
    implicit ec: ExecutionContext, materializer: Materializer
  ): PineconeVectorService = {
    val timeouts = loadTimeouts(config)

    apply(
      apiKey = config.getString(s"$configPrefix.apiKey"),
      environment = config.getString(s"$configPrefix.environment"),
      indexName = indexName,
      timeouts = timeoutsToOption(timeouts)
    )
  }
}