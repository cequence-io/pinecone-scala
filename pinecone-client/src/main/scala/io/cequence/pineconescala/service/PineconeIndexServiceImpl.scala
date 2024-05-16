package io.cequence.pineconescala.service

import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import io.cequence.pineconescala.ConfigImplicits.ConfigExt
import play.api.libs.ws.StandaloneWSRequest
import io.cequence.pineconescala.JsonUtil.JsonOps
import io.cequence.pineconescala.JsonFormats._
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.pineconescala.domain.settings._
import io.cequence.pineconescala.domain.response._
import io.cequence.pineconescala.domain.PodType
import io.cequence.openaiscala.service.ws.{Timeouts, WSRequestHelper}
import play.api.libs.json.JsObject

import scala.concurrent.{ExecutionContext, Future}

/**
 * Private impl. class of [[PineconeIndexService]].
 *
 * @param apiKey
 * @param environment
 *   (optional)
 * @since Apr
 *   2023
 */
private class PineconeIndexServiceImpl(
  apiKey: String,
  environment: Option[String] = None,
  explTimeouts: Option[Timeouts] = None
)(
  implicit val ec: ExecutionContext,
  val materializer: Materializer
) extends PineconeIndexService
    with WSRequestHelper {

  override protected type PEP = EndPoint
  override protected type PT = Tag
  override protected val coreUrl = environment
    .map(env => s"https://controller.$env.pinecone.io/")
    .getOrElse(
      "https://api.pinecone.io/"
    )

  override protected def timeouts: Timeouts =
    explTimeouts.getOrElse(
      Timeouts(
        requestTimeout = Some(defaultRequestTimeout),
        readTimeout = Some(defaultReadoutTimeout)
      )
    )

  override def listCollections: Future[Seq[String]] =
    execGET(EndPoint.collections).map(
      _.asSafe[Seq[String]]
    )

  override def createCollection(
    name: String,
    source: String
  ): Future[CreateResponse] =
    execPOSTWithStatus(
      EndPoint.collections,
      bodyParams = jsonBodyParams(
        Tag.name -> Some(name),
        Tag.source -> Some(source)
      ),
      acceptableStatusCodes = Nil // don't parse response at all
    ).map { response =>
      val (statusCode, message) = statusCodeAndMessage(response)

      statusCode match {
        case 201 => CreateResponse.Created
        case 400 =>
          CreateResponse.BadRequest // Encountered when request exceeds quota or an invalid collection name.
        case 409 => CreateResponse.AlreadyExists
        case _   => throw new PineconeScalaClientException(s"Code ${statusCode} : ${message}")
      }
    }

  override def describeCollection(
    collectionName: String
  ): Future[Option[CollectionInfo]] =
    execGETWithStatus(
      EndPoint.collections,
      endPointParam = Some(collectionName)
    ).map { response =>
      handleNotFoundAndError(response).map(
        _.asSafe[CollectionInfo]
      )
    }

  override def deleteCollection(
    collectionName: String
  ): Future[DeleteResponse] =
    execDELETEWithStatus(
      EndPoint.collections,
      endPointParam = Some(collectionName),
      acceptableStatusCodes = Nil // don't parse response at all
    ).map { response =>
      val (statusCode, message) = statusCodeAndMessage(response)

      statusCode match {
        case 202 => DeleteResponse.Deleted
        case 404 => DeleteResponse.NotFound
        case _   => throw new PineconeScalaClientException(s"Code ${statusCode} : ${message}")
      }
    }

  override def listIndexes: Future[Seq[String]] =
    execGET(indexesEndpoint).map(response =>
      (response \ "indexes").toOption
        .map(
          _.asSafe[Seq[JsObject]].map(_.toString()) // TODO
        )
        .getOrElse(
          response.asSafe[Seq[String]]
        )
    )

  override def createIndex(
    name: String,
    dimension: Int,
    settings: CreatePodBasedIndexSettings // TODO
  ): Future[CreateResponse] =
    execPOSTWithStatus(
      indexesEndpoint,
      bodyParams = jsonBodyParams(
        Tag.name -> Some(name),
        Tag.dimension -> Some(dimension),
        Tag.metric -> Some(settings.metric.toString),
        Tag.pods -> Some(settings.pods),
        Tag.replicas -> Some(settings.replicas),
        Tag.pod_type -> Some(settings.podType.toString),
        Tag.metadata_config -> (if (settings.metadataConfig.nonEmpty)
                                  Some(settings.metadataConfig)
                                else None),
        Tag.source_collection -> settings.sourceCollection
      ),
      acceptableStatusCodes = Nil // don't parse response at all
    ).map { response =>
      val (statusCode, message) = statusCodeAndMessage(response)

      statusCode match {
        case 201 => CreateResponse.Created
        case 400 =>
          CreateResponse.BadRequest // Encountered when request exceeds quota or an invalid index name.
        case 409 => CreateResponse.AlreadyExists
        case _   => throw new PineconeScalaClientException(s"Code ${statusCode} : ${message}")
      }
    }

  override def describeIndex(
    indexName: String
  ): Future[Option[IndexInfo]] =
    execGETWithStatus(
      indexesEndpoint,
      endPointParam = Some(indexName)
    ).map { response =>
      handleNotFoundAndError(response).map(json =>
        if (environment.isDefined) {
          // pod-based
          json.asSafe[PodBasedIndexInfo]
        } else {
          // serverless-based
          json.asSafe[ServerlessIndexInfo]
        }
      )
    }

  override def deleteIndex(
    indexName: String
  ): Future[DeleteResponse] =
    execDELETEWithStatus(
      indexesEndpoint,
      endPointParam = Some(indexName),
      acceptableStatusCodes = Nil // don't parse response at all
    ).map { response =>
      val (statusCode, message) = statusCodeAndMessage(response)

      statusCode match {
        case 202 => DeleteResponse.Deleted
        case 404 => DeleteResponse.NotFound
        case _   => throw new PineconeScalaClientException(s"Code ${statusCode} : ${message}")
      }
    }

  override def configureIndex(
    indexName: String,
    replicas: Option[Int],
    podType: Option[PodType.Value]
  ): Future[ConfigureIndexResponse] =
    execPATCHWithStatus(
      indexesEndpoint,
      endPointParam = Some(indexName),
      bodyParams = jsonBodyParams(
        Tag.replicas -> replicas,
        Tag.pod_type -> podType.map(_.toString)
      )
    ).map { response =>
      val (statusCode, message) = statusCodeAndMessage(response)

      statusCode match {
        case 202 => ConfigureIndexResponse.Updated
        case 400 => ConfigureIndexResponse.BadRequestNotEnoughQuota
        case 404 => ConfigureIndexResponse.NotFound
        case _   => throw new PineconeScalaClientException(s"Code ${statusCode} : ${message}")
      }
    }

  // aux

  // if environment is specified (pod-based arch) we use databases endpoint,
  // otherwise (serverless arch) we use indexes endpoint
  private def indexesEndpoint =
    environment.map(_ => EndPoint.databases).getOrElse(EndPoint.indexes)

  override protected def getWSRequestOptional(
    endPoint: Option[PEP],
    endPointParam: Option[String],
    params: Seq[(String, Option[Any])] = Nil
  ) =
    addHeaders(super.getWSRequestOptional(endPoint, endPointParam, params))

  override protected def getWSRequest(
    endPoint: Option[PEP],
    endPointParam: Option[String],
    params: Seq[(String, Any)] = Nil
  ) =
    addHeaders(
      super.getWSRequest(
        endPoint,
        endPointParam,
        params
      )
    )

  private def addHeaders(request: StandaloneWSRequest) = {
    val apiKeyHeader = ("Api-Key", apiKey)
    request.addHttpHeaders(apiKeyHeader)
  }

  private def statusCodeAndMessage(
    response: RichJsResponse
  ) =
    response match {
      case Right(statusCodeAndMessage) => statusCodeAndMessage

      // should never happen
      case Left(json) =>
        throw new IllegalArgumentException(
          s"Status code and message expected but got a json value '${json}'."
        )
    }
}

object PineconeIndexServiceFactory extends PineconeServiceFactoryHelper {

  def apply(
    apiKey: String,
    environment: Option[String] = None,
    timeouts: Option[Timeouts] = None
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeIndexService =
    new PineconeIndexServiceImpl(apiKey, environment, timeouts)

  def apply(
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeIndexService =
    apply(ConfigFactory.load(configFileName))

  def apply(
    config: Config
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeIndexService = {
    val timeouts = loadTimeouts(config)

    apply(
      apiKey = config.getString(s"$configPrefix.apiKey"),
      environment = config.optionalString(s"$configPrefix.environment"),
      timeouts = timeoutsToOption(timeouts)
    )
  }
}
