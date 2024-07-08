package io.cequence.pineconescala.service

import akka.stream.Materializer
import com.typesafe.config.Config
import io.cequence.pineconescala.domain.response.Assistant
import io.cequence.wsclient.domain.WsRequestContext
import io.cequence.wsclient.service.ws.{Timeouts, WSRequestHelper}
import play.api.libs.ws.StandaloneWSRequest
import io.cequence.pineconescala.JsonFormats._
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.pineconescala.service.PineconeInferenceServiceFactory.{
  configPrefix,
  loadTimeouts
}
import io.cequence.wsclient.ResponseImplicits.JsonSafeOps

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
      ("X-Pinecone-API-Version", "2024-07")
    ),
    explTimeouts = explicitTimeouts
  )

  override def listAssistants(): Future[Seq[Assistant]] = {
    execGET(EndPoint.assistants).map(_.asSafeJsonArray[Assistant])
  }

  override protected def handleErrorCodes(
    httpCode: Int,
    message: String
  ): Nothing =
    throw new PineconeScalaClientException(s"Code ${httpCode} : ${message}")

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
