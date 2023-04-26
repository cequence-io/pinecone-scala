package io.cequence.pineconescala.service

import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import play.api.libs.ws.StandaloneWSRequest
import io.cequence.pineconescala.JsonUtil.JsonOps
import io.cequence.pineconescala.JsonFormats._
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.pineconescala.domain.settings._
import io.cequence.pineconescala.domain.response._
import io.cequence.pineconescala.ConfigImplicits._
import io.cequence.pineconescala.domain.{PVector, PodType}
import io.cequence.pineconescala.service.ws.{Timeouts, WSRequestHelper}

import scala.concurrent.{ExecutionContext, Future}

/**
 * Private impl. class of [[PineconeIndexService]].
 *
 * @param apiKey
 * @param environment
 * @since Apr 2023
 */
private class PineconeIndexServiceImpl(
  apiKey: String,
  environment: String,
  explTimeouts: Option[Timeouts] = None)(
  implicit val ec: ExecutionContext, val materializer: Materializer
) extends PineconeIndexService with WSRequestHelper {

  override protected type PEP = Command
  override protected type PT = Tag
  override protected val coreUrl = s"https://controller.${environment}.pinecone.io/"

  override protected def timeouts: Timeouts =
    explTimeouts.getOrElse(
      Timeouts(
        requestTimeout = Some(defaultRequestTimeout),
        readTimeout = Some(defaultReadoutTimeout)
      )
    )

  override def listCollections: Future[Seq[String]] =
    execGET(Command.collections).map(
      _.asSafe[Seq[String]]
    )

  override def createCollection(
    name: String,
    source: String
  ): Future[CreateResponse] =
    execPOSTWithStatus(
      Command.collections,
      bodyParams = jsonBodyParams(
        Tag.name -> Some(name),
        Tag.source -> Some(source)
      ),
      acceptableStatusCodes = Nil // don't parse response at all
    ).map { response =>
      val (statusCode, message) = statusCodeAndMessage(response)

      statusCode match {
        case 201 => CreateResponse.Created
        case 400 => CreateResponse.BadRequest // Encountered when request exceeds quota or an invalid collection name.
        case 409 => CreateResponse.AlreadyExists
        case _ => throw new PineconeScalaClientException(s"Code ${statusCode} : ${message}")
      }
    }

  override def describeCollection(
    collectionName: String
  ): Future[Option[CollectionInfo]] =
    execGETWithStatus(
      Command.collections,
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
      Command.collections,
      endPointParam = Some(collectionName),
      acceptableStatusCodes = Nil // don't parse response at all
    ).map { response =>
      val (statusCode, message) = statusCodeAndMessage(response)

      statusCode match {
        case 202 => DeleteResponse.Deleted
        case 404 => DeleteResponse.NotFound
        case _ => throw new PineconeScalaClientException(s"Code ${statusCode} : ${message}")
      }
    }

  override def listIndexes: Future[Seq[String]] =
    execGET(Command.databases).map(
      _.asSafe[Seq[String]]
    )

  override def createIndex(
    name: String,
    dimension: Int,
    settings: CreateIndexSettings
  ): Future[CreateResponse] =
    execPOSTWithStatus(
      Command.databases,
      bodyParams = jsonBodyParams(
        Tag.name -> Some(name),
        Tag.dimension -> Some(dimension),
        Tag.metric -> Some(settings.metric.toString),
        Tag.pods -> Some(settings.pods),
        Tag.replicas -> Some(settings.replicas),
        Tag.pod_type -> Some(settings.pod_type.toString),
        Tag.metadata_config -> (if (settings.metadata_config.nonEmpty) Some(settings.metadata_config) else None),
        Tag.source_collection -> settings.source_collection
      ),
      acceptableStatusCodes = Nil // don't parse response at all
    ).map { response =>
      val (statusCode, message) = statusCodeAndMessage(response)

      statusCode match {
        case 201 => CreateResponse.Created
        case 400 => CreateResponse.BadRequest // Encountered when request exceeds quota or an invalid index name.
        case 409 => CreateResponse.AlreadyExists
        case _ => throw new PineconeScalaClientException(s"Code ${statusCode} : ${message}")
      }
    }

  override def describeIndex(
    indexName: String
  ): Future[Option[IndexInfo]] =
    execGETWithStatus(
      Command.databases,
      endPointParam = Some(indexName)
    ).map { response =>
      handleNotFoundAndError(response).map(
        _.asSafe[IndexInfo]
      )
    }

  override def deleteIndex(
    indexName: String
  ): Future[DeleteResponse] =
    execDELETEWithStatus(
      Command.databases,
      endPointParam = Some(indexName),
      acceptableStatusCodes = Nil // don't parse response at all
    ).map { response =>
      val (statusCode, message) = statusCodeAndMessage(response)

      statusCode match {
        case 202 => DeleteResponse.Deleted
        case 404 => DeleteResponse.NotFound
        case _ => throw new PineconeScalaClientException(s"Code ${statusCode} : ${message}")
      }
    }

  override def configureIndex(
    indexName: String,
    replicas: Option[Int],
    pod_type: Option[PodType.Value]
  ): Future[ConfigureIndexResponse] =
    execPATCHWithStatus(
      Command.databases,
      endPointParam = Some(indexName),
      bodyParams = jsonBodyParams(
        Tag.replicas -> replicas,
        Tag.pod_type -> pod_type.map(_.toString)
      )
    ).map { response =>
      val (statusCode, message) = statusCodeAndMessage(response)

      statusCode match {
        case 202 => ConfigureIndexResponse.Updated
        case 400 => ConfigureIndexResponse.BadRequestNotEnoughQuota
        case 404 => ConfigureIndexResponse.NotFound
        case _ => throw new PineconeScalaClientException(s"Code ${statusCode} : ${message}")
      }
    }

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

  private def statusCodeAndMessage(
    response: RichJsResponse
  ) =
    response match {
      case Right(statusCodeAndMessage) => statusCodeAndMessage

      // should never happen
      case Left(json) =>
        throw new IllegalArgumentException(s"Status code and message expected but got a json value '${json}'.")
    }
}

object PineconeIndexServiceFactory extends PineconeServiceFactoryHelper {

  def apply(
    apiKey: String,
    environment: String,
    timeouts: Option[Timeouts] = None)(
    implicit ec: ExecutionContext, materializer: Materializer
  ): PineconeIndexService =
    new PineconeIndexServiceImpl(apiKey, environment, timeouts)

  def apply()(
    implicit ec: ExecutionContext, materializer: Materializer
  ): PineconeIndexService =
    apply(ConfigFactory.load(configFileName))

  def apply(
    config: Config)(
    implicit ec: ExecutionContext, materializer: Materializer
  ): PineconeIndexService = {
    val timeouts = loadTimeouts(config)

    apply(
      apiKey = config.getString(s"$configPrefix.apiKey"),
      environment = config.getString(s"$configPrefix.environment"),
      timeouts = timeoutsToOption(timeouts)
    )
  }
}