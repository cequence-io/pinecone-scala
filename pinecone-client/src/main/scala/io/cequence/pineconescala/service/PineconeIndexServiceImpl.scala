package io.cequence.pineconescala.service

import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import io.cequence.pineconescala.JsonFormats._
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.pineconescala.domain.response._
import io.cequence.pineconescala.domain.settings.IndexSettings.{
  CreatePodBasedIndexSettings,
  CreateServerlessIndexSettings
}
import io.cequence.pineconescala.domain.settings._
import io.cequence.pineconescala.domain.PodType
import io.cequence.wsclient.JsonUtil.JsonOps
import io.cequence.wsclient.ResponseImplicits._
import io.cequence.wsclient.domain.{RichResponse, WsRequestContext}
import io.cequence.wsclient.service.WSClientEngine
import io.cequence.wsclient.service.WSClientWithEngineTypes.WSClientWithEngine
import io.cequence.wsclient.service.ws.{PlayWSClientEngine, Timeouts}
import play.api.libs.json.JsValue

import scala.concurrent.{ExecutionContext, Future}

private final class ServerlessIndexServiceImpl(
  apiKey: String,
  explTimeouts: Option[Timeouts] = None
)(
  override implicit val ec: ExecutionContext,
  override val materializer: Materializer
) extends PineconeIndexServiceImpl[CreateServerlessIndexSettings](
      apiKey,
      None,
      coreUrl = "https://api.pinecone.io/",
      explTimeouts
    )(ec, materializer)
    with PineconeServerlessIndexService {

  override protected def indexesEndpoint: EndPoint = EndPoint.indexes

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
    settings: CreateServerlessIndexSettings
  ): Future[CreateResponse] =
    execPOSTRich(
      indexesEndpoint,
      bodyParams = {
        jsonBodyParams(
          fromCreateServerlessIndexSettings(name, dimension, settings): _*
        )
      },
      acceptableStatusCodes = Nil // don't parse response at all
    ).map(handleCreateResponse)

  private def fromCreateServerlessIndexSettings(
    name: String,
    dimension: Int,
    settings: CreateServerlessIndexSettings
  ): Seq[(Tag, Option[Any])] = {
    Seq(
      Tag.name -> Some(name),
      Tag.dimension -> Some(dimension),
      Tag.metric -> Some(settings.metric.toString),
      Tag.spec -> Some(
        Map(
          "serverless" -> Map(
            Tag.cloud.toString -> settings.cloud.toString,
            Tag.region.toString -> settings.region.toString
          )
        )
      )
    )
  }

  override def describeIndexResponse(json: JsValue): IndexInfo =
    json.asSafe[ServerlessIndexInfo]

  override def describeIndexTyped(
    indexName: String
  ): Future[Option[ServerlessIndexInfo]] =
    describeIndex(indexName).map(
      _.map(_.asInstanceOf[ServerlessIndexInfo])
    )
}

private final class PineconePodPineconeBasedImpl(
  apiKey: String,
  environment: String,
  explTimeouts: Option[Timeouts] = None
)(
  override implicit val ec: ExecutionContext,
  override val materializer: Materializer
) extends PineconeIndexServiceImpl[CreatePodBasedIndexSettings](
      apiKey,
      Some(environment),
      coreUrl = s"https://controller.${environment}.pinecone.io/",
      explTimeouts
    )(ec, materializer)
    with PineconePodBasedIndexService {

  override protected def indexesEndpoint: EndPoint = EndPoint.databases

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
    settings: CreatePodBasedIndexSettings
  ): Future[CreateResponse] =
    execPOSTRich(
      indexesEndpoint,
      bodyParams = jsonBodyParams(
        fromCreatePodBasedIndexSettings(name, dimension, settings): _*
      ),
      acceptableStatusCodes = Nil // don't parse response at all
    ).map(handleCreateResponse)

  private def fromCreatePodBasedIndexSettings(
    name: String,
    dimension: Int,
    settings: CreatePodBasedIndexSettings
  ): Seq[(Tag, Option[Any])] = {
    Seq(
      Tag.name -> Some(name),
      Tag.dimension -> Some(dimension),
      Tag.metric -> Some(settings.metric.toString),
      Tag.spec -> Some(
        Map(
          "pod" -> Map(
            Tag.pods.toString -> Some(settings.pods),
            Tag.replicas.toString -> Some(settings.replicas),
            Tag.pod_type.toString -> Some(settings.podType.toString),
            Tag.shards.toString -> Some(settings.shards),
            Tag.metadata_config.toString ->
              (if (settings.metadataConfig.nonEmpty) Some(settings.metadataConfig) else None),
            Tag.source_collection.toString -> settings.sourceCollection
          )
        )
      )
    )
  }

  override def configureIndex(
    indexName: String,
    replicas: Option[Int],
    podType: Option[PodType]
  ): Future[ConfigureIndexResponse] =
    execPATCRich(
      indexesEndpoint,
      endPointParam = Some(indexName),
      bodyParams = jsonBodyParams(
        Tag.replicas -> replicas,
        Tag.pod_type -> podType.map(_.toString)
      )
    ).map { richResponse =>
      val status = richResponse.status

      status.code match {
        case 202 => ConfigureIndexResponse.Updated
        case 400 => ConfigureIndexResponse.BadRequestNotEnoughQuota
        case 404 => ConfigureIndexResponse.NotFound
        case _ =>
          throw new PineconeScalaClientException(s"Code ${status.code} : ${status.message}")
      }
    }

  override protected def describeIndexResponse(json: JsValue): IndexInfo =
    json.asSafe[PodBasedIndexInfo]

  override def describeIndexTyped(
    indexName: String
  ): Future[Option[PodBasedIndexInfo]] =
    describeIndex(indexName).map(
      _.map(_.asInstanceOf[PodBasedIndexInfo])
    )

  override def createCollection(
    name: String,
    source: String
  ): Future[CreateResponse] =
    execPOSTRich(
      EndPoint.collections,
      bodyParams = jsonBodyParams(
        Tag.name -> Some(name),
        Tag.source -> Some(source)
      ),
      acceptableStatusCodes = Nil // don't parse response at all
    ).map(handleCreateResponse)

  override def listCollections: Future[Seq[String]] =
    execGET(EndPoint.collections).map(
      _.asSafeJson[Seq[String]]
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
abstract class PineconeIndexServiceImpl[S <: IndexSettings](
  apiKey: String,
  environment: Option[String],
  coreUrl: String,
  explicitTimeouts: Option[Timeouts] = None
)(
  implicit val ec: ExecutionContext,
  val materializer: Materializer
) extends PineconeIndexService[S]
    with WSClientWithEngine {

  override protected type PEP = EndPoint
  override protected type PT = Tag

  // we use play-ws backend
  override protected val engine: WSClientEngine = PlayWSClientEngine(
    coreUrl,
    requestContext = WsRequestContext(
      authHeaders = Seq(
        "Api-Key" -> apiKey,
        "X-Pinecone-API-Version" -> apiVersion
      ),
      explTimeouts = explicitTimeouts
    )
  )

  def isPodBasedIndex: Boolean = environment.isDefined
  def isServerlessIndex: Boolean = !isPodBasedIndex

  override def describeCollection(
    collectionName: String
  ): Future[Option[CollectionInfo]] =
    execGETRich(
      EndPoint.collections,
      endPointParam = Some(collectionName)
    ).map { response =>
      handleNotFoundAndError(response).map(
        _.asSafeJson[CollectionInfo]
      )
    }

  override def deleteCollection(
    collectionName: String
  ): Future[DeleteResponse] =
    execDELETERich(
      EndPoint.collections,
      endPointParam = Some(collectionName),
      acceptableStatusCodes = Nil // don't parse response at all
    ).map(handleDeleteResponse)

  override def listIndexes: Future[Seq[String]] =
    execGET(indexesEndpoint).map(response =>
      (response.json \ "indexes")
        .asOpt[Seq[JsValue]]
        .map(indexes => {
          indexes.flatMap(index => (index \ "name").asOpt[String])
        })
        .getOrElse(
          response.asSafeJson[Seq[String]]
        )
    )

  protected def describeIndexResponse(json: JsValue): IndexInfo

  override def describeIndex(
    indexName: String
  ): Future[Option[IndexInfo]] =
    execGETRich(
      indexesEndpoint,
      endPointParam = Some(indexName)
    ).map { richResponse =>
      handleNotFoundAndError(richResponse).map(response =>
        describeIndexResponse(response.json)
      )
    }

  override def deleteIndex(
    indexName: String
  ): Future[DeleteResponse] =
    execDELETERich(
      indexesEndpoint,
      endPointParam = Some(indexName),
      acceptableStatusCodes = Nil // don't parse response at all
    ).map(handleDeleteResponse)

  // aux

  // if environment is specified (pod-based arch) we use databases endpoint,
  // otherwise (serverless arch) we use indexes endpoint
  protected def indexesEndpoint: PEP // Either[EndPoint.databases.type, EndPoint.indexes.type]

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
    settings: S
  ): Future[CreateResponse]

  override protected def handleErrorCodes(
    httpCode: Int,
    message: String
  ): Nothing =
    throw new PineconeScalaClientException(s"Code ${httpCode} : ${message}")

  protected def handleCreateResponse(response: RichResponse): CreateResponse =
    response.status.code match {
      case 201 => CreateResponse.Created
      // Encountered when request exceeds quota or an invalid index name.
      case 400 => CreateResponse.BadRequest
      case 409 => CreateResponse.AlreadyExists
      case _ =>
        throw new PineconeScalaClientException(
          s"Code ${response.status.code} : ${response.status.message}"
        )
    }

  protected def handleDeleteResponse(response: RichResponse): DeleteResponse =
    response.status.code match {
      case 202 => DeleteResponse.Deleted
      case 404 => DeleteResponse.NotFound
      case _ =>
        throw new PineconeScalaClientException(
          s"Code ${response.status.code} : ${response.status.message}"
        )
    }
}

object PineconeIndexServiceFactory extends PineconeServiceFactoryHelper {

  def apply(
    apiKey: String,
    environment: String,
    timeouts: Option[Timeouts]
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconePodBasedIndexService =
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
    environment: Option[String],
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

  // if we don't care whether it's pod-based or serverless
  implicit class FactoryImplicits(
    either: Either[PineconePodBasedIndexService, PineconeServerlessIndexService]
  ) {
    def asOne: PineconeIndexService[_] =
      either match {
        case Left(service)  => service
        case Right(service) => service
      }
  }
}
