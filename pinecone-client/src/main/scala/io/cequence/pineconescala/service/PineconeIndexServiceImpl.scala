package io.cequence.pineconescala.service

import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import io.cequence.pineconescala.JsonFormats._
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.pineconescala.domain.IndexEnv.PodEnv
import io.cequence.pineconescala.domain.response._
import io.cequence.pineconescala.domain.settings.IndexSettingsType.{
  CreatePodBasedIndexSettings,
  CreateServerlessIndexSettings
}
import io.cequence.pineconescala.domain.settings._
import io.cequence.pineconescala.domain.{Metric, PodType}
import io.cequence.wsclient.JsonUtil.JsonOps
import io.cequence.wsclient.service.ws.{Timeouts, WSRequestHelper}
import play.api.libs.json.JsValue
import play.api.libs.ws.StandaloneWSRequest

import scala.concurrent.{ExecutionContext, Future}

class ServerlessIndexServiceImpl(
  apiKey: String,
  explTimeouts: Option[Timeouts] = None
)(
  override implicit val ec: ExecutionContext,
  override val materializer: Materializer
) extends PineconeIndexServiceImpl[CreateServerlessIndexSettings](
      apiKey,
      None,
      explTimeouts
    )(ec, materializer)
    with PineconeServerlessIndexService {

  override protected val coreUrl: String =
    "https://api.pinecone.io/"

  override def indexesEndpoint: EndPoint = EndPoint.indexes

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
  override def createIndex(
    name: String,
    dimension: Int,
    metric: Metric,
    settings: CreateServerlessIndexSettings
  ): Future[CreateResponse] = {
    execPOSTWithStatus(
      indexesEndpoint,
      bodyParams = {
        val params = jsonBodyParams(
          Tag.fromCreateServerlessIndexSettings(name, dimension, metric, settings): _*
        )

        println(s"params = $params")

        params
      },
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
  }

  override def describeIndexResponse(json: JsValue): IndexInfo =
    json.asSafe[ServerlessIndexInfo]

  def describeIndexServerless(json: JsValue): ServerlessIndexInfo =
    json.asSafe[ServerlessIndexInfo]
}

class PineconePodPineconeBasedImpl(
  apiKey: String,
  environment: PodEnv,
  explTimeouts: Option[Timeouts] = None
)(
  override implicit val ec: ExecutionContext,
  override val materializer: Materializer
) extends PineconeIndexServiceImpl[CreatePodBasedIndexSettings](
      apiKey,
      Some(environment),
      explTimeouts
    )(ec, materializer)
    with PineconePodBasedIndexService {

  override protected val coreUrl =
    s"https://controller.${environment.environment}.pinecone.io/"

  override def indexesEndpoint: EndPoint = EndPoint.databases

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
  override def createIndex(
    name: String,
    dimension: Int,
    metric: Metric,
    settings: CreatePodBasedIndexSettings
  ): Future[CreateResponse] = {
    execPOSTWithStatus(
      indexesEndpoint,
      bodyParams = jsonBodyParams(
        Tag.fromCreatePodBasedIndexSettings(name, dimension, metric, settings): _*
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
  }

  override def configureIndex(
    indexName: String,
    replicas: Option[Int],
    podType: Option[PodType]
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

  override def describeIndexResponse(json: JsValue): IndexInfo =
    json.asSafe[PodBasedIndexInfo]

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
      println(s"response = $response")
      val (statusCode, message) = statusCodeAndMessage(response)

      statusCode match {
        case 201 => CreateResponse.Created
        case 400 =>
          CreateResponse.BadRequest // Encountered when request exceeds quota or an invalid collection name.
        case 409 => CreateResponse.AlreadyExists
        case _   => throw new PineconeScalaClientException(s"Code ${statusCode} : ${message}")
      }
    }

  override def listCollections: Future[Seq[String]] =
    execGET(EndPoint.collections).map(response => response.asSafe[Seq[String]]
//      response
//        .asSafe[Seq[String]](response \ "collections")
//        .asOpt[Seq[JsValue]]
//        .map(x => x.map(_ \ "name").map(_.as[String]))
//        .getOrElse(response.asSafe[Seq[String]])
    )
}

/**
 * Private impl. class of [[PineconeIndexService]].
 *
 * @param apiKey
 * @param environment
 *   (optional)
 * @since Apr
 *   2023
 */
abstract class PineconeIndexServiceImpl[S <: IndexSettingsType](
  apiKey: String,
  environment: Option[PodEnv],
  explicitTimeouts: Option[Timeouts] = None
)(
  implicit val ec: ExecutionContext,
  val materializer: Materializer
) extends PineconeIndexService[S]
    with WSRequestHelper {

  override protected type PEP = EndPoint
  override protected type PT = Tag

  override val defaultRequestTimeout = 120 * 1000 // two minutes
  override val defaultReadoutTimeout = 120 * 1000 // two minutes

  override protected val explTimeouts: Option[Timeouts] = explicitTimeouts

  def isPodBasedIndex: Boolean = environment.isDefined
  def isServerlessIndex: Boolean = !isPodBasedIndex

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
      (response \ "indexes")
        .asOpt[Seq[JsValue]]
        .map(indexes => {
          indexes.flatMap(index => (index \ "name").asOpt[String])
        })
        .getOrElse(
          response.asSafe[Seq[String]]
        )
    )

  def describeIndexResponse(json: JsValue): IndexInfo

  override def describeIndex(
    indexName: String
  ): Future[Option[IndexInfo]] =
    execGETWithStatus(
      indexesEndpoint,
      endPointParam = Some(indexName)
    ).map { response: RichJsResponse =>
      handleNotFoundAndError(response).map(describeIndexResponse)
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

  // aux

  // if environment is specified (pod-based arch) we use databases endpoint,
  // otherwise (serverless arch) we use indexes endpoint
  def indexesEndpoint: PEP // Either[EndPoint.databases.type, EndPoint.indexes.type]

  override def addHeaders(request: StandaloneWSRequest): StandaloneWSRequest = {
    val apiKeyHeader = ("Api-Key", apiKey)
    request.addHttpHeaders(apiKeyHeader)
  }

  protected def statusCodeAndMessage(
    response: RichJsResponse
  ): (Int, String) =
    response match {
      case Right(statusCodeAndMessage) => statusCodeAndMessage

      // should never happen
      case Left(json) =>
        throw new IllegalArgumentException(
          s"Status code and message expected but got a json value '${json}'."
        )
    }

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
  override def createIndex(
    name: String,
    dimension: Int,
    metric: Metric,
    settings: S
  ): Future[CreateResponse]

  override protected def handleErrorCodes(
    httpCode: Int,
    message: String
  ): Nothing =
    throw new PineconeScalaClientException(s"Code ${httpCode} : ${message}")
}

object PineconeIndexServiceFactory extends PineconeServiceFactoryHelper {

  def apply(
    apiKey: String,
    environment: PodEnv,
    timeouts: Option[Timeouts]
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconePodPineconeBasedImpl =
    new PineconePodPineconeBasedImpl(apiKey, environment, timeouts)

  def apply(
    apiKey: String,
    timeouts: Option[Timeouts]
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeServerlessIndexService =
    new ServerlessIndexServiceImpl(apiKey, timeouts)

  def apply(
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): Either[PineconePodBasedIndexService, PineconeServerlessIndexService] =
    apply(ConfigFactory.load(configFileName))

  def apply(
    config: Config
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): Either[PineconePodBasedIndexService, PineconeServerlessIndexService] = {
    val timeouts = loadTimeouts(config)

    apply(
      apiKey = config.getString(s"$configPrefix.apiKey"),
      environment = loadPodEnv(config),
      timeouts = timeouts.toOption
    )
  }

  def apply(
    apiKey: String,
    environment: Option[PodEnv],
    timeouts: Option[Timeouts]
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): Either[PineconePodBasedIndexService, PineconeServerlessIndexService] =
    environment match {
      case Some(podEnv) =>
        Left(new PineconePodPineconeBasedImpl(apiKey, podEnv, timeouts))
      case None =>
        Right(new ServerlessIndexServiceImpl(apiKey, timeouts))
    }

}
