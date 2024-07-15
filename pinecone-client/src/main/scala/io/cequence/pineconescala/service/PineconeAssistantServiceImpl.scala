package io.cequence.pineconescala.service

import akka.stream.Materializer
import com.typesafe.config.Config
import io.cequence.pineconescala.domain.response.{
  Assistant,
  ChatCompletionResponse,
  DeleteResponse,
  ListAssistantsResponse,
  ListFilesResponse,
  UserMessage
}
import io.cequence.wsclient.domain.{RichResponse, WsRequestContext}
import io.cequence.wsclient.service.ws.{PlayWSClientEngine, Timeouts}
import io.cequence.pineconescala.JsonFormats._
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.wsclient.ResponseImplicits.JsonSafeOps
import io.cequence.wsclient.service.WSClientEngine
import io.cequence.wsclient.service.WSClientWithEngineTypes.WSClientWithEngine

import scala.concurrent.{ExecutionContext, Future}

class PineconeAssistantServiceImpl(
  apiKey: String,
  explicitTimeouts: Option[Timeouts] = None
)(
  implicit val ec: ExecutionContext,
  val materializer: Materializer
) extends PineconeAssistantService
    with WSClientWithEngine {

  override protected type PEP = EndPoint
  override protected type PT = Tag

  // we use play-ws backend
  override protected val engine: WSClientEngine = PlayWSClientEngine(
    coreUrl = "https://api.pinecone.io/",
    requestContext = WsRequestContext(
      authHeaders = Seq(
        ("Api-Key", apiKey),
        ("X-Pinecone-API-Version", "2024-07")
      ),
      explTimeouts = explicitTimeouts
    )
  )

  override def listAssistants(): Future[Seq[Assistant]] =
    execGET(EndPoint.assistants).map(_.asSafeJson[ListAssistantsResponse]).map(_.assistants)

  override def createAssistant(
    name: String,
    metadata: Map[String, String]
  ): Future[Assistant] = {
    execPOST(
      EndPoint.assistants,
      bodyParams = jsonBodyParams(
        Tag.name -> Some(name),
        Tag.metadata -> Some(metadata)
      )
    ).map(_.asSafeJson[Assistant])
  }

  override def describeAssistant(name: String): Future[Option[Assistant]] =
    execGETRich(
      EndPoint.assistants,
      endPointParam = Some(name)
    ).map { response =>
      handleNotFoundAndError(response).map(
        _.asSafeJson[Assistant]
      )
    }

  override def deleteAssistant(name: String): Future[DeleteResponse] =
    execDELETERich(
      EndPoint.assistants,
      endPointParam = Some(name)
    ).map(handleDeleteResponse)

  // TODO: we need more granular exceptions here
  override protected def handleErrorCodes(
    httpCode: Int,
    message: String
  ): Nothing =
    throw new PineconeScalaClientException(s"Code ${httpCode} : ${message}")

  protected def handleDeleteResponse(response: RichResponse): DeleteResponse =
    response.status.code match {
      case 200 => DeleteResponse.Deleted
      case 404 => DeleteResponse.NotFound
      case _ =>
        throw new PineconeScalaClientException(
          s"Code ${response.status.code} : ${response.status.message}"
        )
    }

}

object PineconeAssistantServiceFactory extends PineconeServiceFactoryHelper {

  def apply(
    apiKey: String,
    timeouts: Option[Timeouts] = None
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeAssistantService = {
    new PineconeAssistantServiceImpl(apiKey, timeouts)
  }

  def apply(
    config: Config
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeAssistantService = {
    val timeouts = loadTimeouts(config)

    apply(
      apiKey = config.getString(s"$configPrefix.apiKey"),
      timeouts = timeouts.toOption
    )
  }

}
