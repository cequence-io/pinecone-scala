package io.cequence.pineconescala.service

import akka.stream.Materializer
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.pineconescala.domain.response.{
  ChatCompletionResponse,
  DeleteResponse,
  FileResponse,
  ListFilesResponse,
  UserMessage
}
import io.cequence.wsclient.ResponseImplicits.JsonSafeOps
import io.cequence.wsclient.domain.{RichResponse, WsRequestContext}
import io.cequence.wsclient.service.ws.{PlayWSClientEngine, Timeouts}
import io.cequence.pineconescala.JsonFormats._
import io.cequence.wsclient.service.WSClientEngine
import io.cequence.wsclient.service.WSClientWithEngineTypes.WSClientWithEngine
import play.api.libs.json.Json

import java.io.File
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class PineconeAssistantFileServiceImpl(
  apiKey: String,
  explicitTimeouts: Option[Timeouts] = None
)(
  implicit val ec: ExecutionContext,
  val materializer: Materializer
) extends PineconeAssistantFileService
    with WSClientWithEngine {

  override protected type PEP = EndPoint
  override protected type PT = Tag

  // we use play-ws backend
  override protected val engine: WSClientEngine = PlayWSClientEngine(
    coreUrl = "https://prod-1-data.ke.pinecone.io/",
    requestContext = WsRequestContext(
      authHeaders = Seq(
        ("Api-Key", apiKey)
        // ("X-Pinecone-API-Version", apiVersion)
      ),
      explTimeouts = explicitTimeouts
    )
  )

  override def listFiles(assistantName: String): Future[Seq[FileResponse]] =
    execGET(EndPoint.files, endPointParam = Some(assistantName))
      .map(_.asSafeJson[ListFilesResponse])
      .map(_.files)

  override def uploadFile(
    assistantName: String,
    file: File,
    displayFileName: Option[String] = None
  ): Future[FileResponse] = {
    execPOSTMultipart(
      EndPoint.files,
      endPointParam = Some(assistantName),
      fileParams = Seq((Tag.file, file, displayFileName))
    ).map(_.asSafeJson[FileResponse])
  }

  override def describeFile(
    assistantName: String,
    fileId: UUID
  ): Future[Option[FileResponse]] =
    execGETRich(
      EndPoint.files,
      // FIXME: provide support for multiple end point params
      endPointParam = Some(s"$assistantName/${fileId.toString}")
    ).map { response =>
      handleNotFoundAndError(response).map(
        _.asSafeJson[FileResponse]
      )
    }

  override def deleteFile(
    assistantName: String,
    fileId: UUID
  ): Future[DeleteResponse] =
    execDELETERich(
      EndPoint.files,
      endPointParam = Some(s"$assistantName/${fileId.toString}")
    ).map(handleDeleteResponse)

  override def chatWithAssistant(
    assistantName: String,
    messages: Seq[String]
  ): Future[ChatCompletionResponse] =
    execPOST(
      EndPoint.chat,
      // FIXME: provide support for end point param followed by URL suffix
      endPointParam = Some(s"$assistantName/chat/completions"),
      bodyParams = jsonBodyParams(
        Tag.messages -> Some(Json.toJson(messages.map(UserMessage)))
      )
    ).map(_.asSafeJson[ChatCompletionResponse])

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

object PineconeAssistantFileServiceFactory
    extends SimplePineconeServiceFactory[PineconeAssistantFileService] {

  override def apply(
    apiKey: String,
    timeouts: Option[Timeouts] = None
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeAssistantFileService =
    new PineconeAssistantFileServiceImpl(apiKey, timeouts)
}
