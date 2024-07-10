package io.cequence.pineconescala.service

import akka.stream.Materializer
import com.typesafe.config.Config
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.pineconescala.domain.response.{DeleteResponse, File, ListFilesResponse}
import io.cequence.wsclient.ResponseImplicits.JsonSafeOps
import io.cequence.wsclient.domain.{RichResponse, WsRequestContext}
import io.cequence.wsclient.service.ws.{Timeouts, WSRequestHelper}
import io.cequence.pineconescala.JsonFormats._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class PineconeAssistantFileServiceImpl(
  apiKey: String,
  explicitTimeouts: Option[Timeouts] = None
)(
  implicit val ec: ExecutionContext,
  val materializer: Materializer
) extends PineconeAssistantFileService
    with WSRequestHelper {

  override protected type PEP = EndPoint
  override protected type PT = Tag
  override val coreUrl: String = "https://prod-1-data.ke.pinecone.io/"
  override protected val requestContext = WsRequestContext(
    authHeaders = Seq(
      ("Api-Key", apiKey)
      // ("X-Pinecone-API-Version", "2024-07")
    ),
    explTimeouts = explicitTimeouts
  )

  override def listFiles(assistantName: String): Future[Seq[File]] =
    execGET(EndPoint.files, endPointParam = Some(assistantName))
      .map(_.asSafeJson[ListFilesResponse])
      .map(_.files)

  override def uploadFile(assistantName: String): Future[File] = {
    // TODO: file contents
    execPOST(
      EndPoint.files,
      endPointParam = Some(assistantName)
    ).map(_.asSafeJson[File])
  }

  override def describeFile(
    assistantName: String,
    fileId: UUID
  ): Future[Option[File]] =
    execGETRich(
      EndPoint.files,
      // FIXME: provide support for multiple end point params
      endPointParam = Some(s"$assistantName/${fileId.toString}")
    ).map { response =>
      handleNotFoundAndError(response).map(
        _.asSafeJson[File]
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

object PineconeAssistantFileServiceFactory extends PineconeServiceFactoryHelper {

  def apply(
    apiKey: String,
    timeouts: Option[Timeouts] = None
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeAssistantFileService = {
    new PineconeAssistantFileServiceImpl(apiKey, timeouts)
  }

  def apply(
    config: Config
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeAssistantFileService = {
    val timeouts = loadTimeouts(config)

    apply(
      apiKey = config.getString(s"$configPrefix.apiKey"),
      timeouts = timeouts.toOption
    )
  }

}
