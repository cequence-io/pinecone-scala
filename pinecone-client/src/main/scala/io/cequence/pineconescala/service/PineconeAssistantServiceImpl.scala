package io.cequence.pineconescala.service

import akka.stream.Materializer
import com.typesafe.config.Config
import io.cequence.pineconescala.domain.response.{Assistant, ChatCompletionResponse, DeleteResponse, File, ListAssistantsResponse, ListFilesResponse}
import io.cequence.wsclient.domain.{RichResponse, WsRequestContext}
import io.cequence.wsclient.service.ws.{Timeouts, WSRequestHelper}
import io.cequence.pineconescala.JsonFormats._
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.wsclient.ResponseImplicits.JsonSafeOps
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

class PineconeAssistantServiceImpl(
  apiKey: String,
  explicitTimeouts: Option[Timeouts] = None
)(
  implicit val ec: ExecutionContext,
  val materializer: Materializer
) extends PineconeAssistantService
    with WSRequestHelper {

  override protected type PEP = EndPoint
  override protected type PT = Tag
  override val coreUrl: String = "https://api.pinecone.io/"
  override protected val requestContext = WsRequestContext(
    authHeaders = Seq(
      ("Api-Key", apiKey),
     // ("X-Pinecone-API-Version", "2024-07")
    ),
    explTimeouts = explicitTimeouts
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

  override def chatWithAssistant(assistantName: String, messages: Seq[String]): Future[ChatCompletionResponse] =
    execPOST(
      EndPoint.chat,
      // FIXME: provide support for end point param followed by URL suffix
      endPointParam = Some(s"$assistantName/chat/completions"),
      bodyParams = jsonBodyParams(
        Tag.messages -> Some(messages.map(Json.toJson(_))))
    ).map(_.asSafeJson[ChatCompletionResponse])

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
