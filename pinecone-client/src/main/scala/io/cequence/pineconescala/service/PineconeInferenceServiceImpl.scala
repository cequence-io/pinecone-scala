package io.cequence.pineconescala.service

import akka.stream.Materializer
import com.typesafe.config.Config
import io.cequence.pineconescala.domain.response.GenerateEmbeddingsResponse
import io.cequence.pineconescala.domain.settings.GenerateEmbeddingsSettings
import io.cequence.wsclient.ResponseImplicits._
import io.cequence.wsclient.service.ws.{PlayWSClientEngine, Timeouts}
import io.cequence.pineconescala.JsonFormats._
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.wsclient.domain.WsRequestContext
import io.cequence.wsclient.service.WSClientEngine
import io.cequence.wsclient.service.WSClientWithEngineTypes.WSClientWithEngine

import scala.concurrent.{ExecutionContext, Future}

private class PineconeInferenceServiceImpl(
  apiKey: String,
  explicitTimeouts: Option[Timeouts] = None
)(
  implicit val ec: ExecutionContext,
  val materializer: Materializer
) extends PineconeInferenceService
    with WSClientWithEngine {

  override protected type PEP = EndPoint
  override protected type PT = Tag

  // we use play-ws backend
  override protected val engine: WSClientEngine = PlayWSClientEngine(
    coreUrl = "https://api.pinecone.io/",
    requestContext =  WsRequestContext(
      authHeaders = Seq(
        ("Api-Key", apiKey),
        ("X-Pinecone-API-Version", "2024-07")
      ),
      explTimeouts = explicitTimeouts
    )
  )

  /**
   * Uses the specified model to generate embeddings for the input sequence.
   *
   * @param inputs
   *   Input sequence for which to generate embeddings.
   * @param settings
   * @return
   *   list of embeddings inside an envelope
   */
  override def createEmbeddings(
    inputs: Seq[String],
    settings: GenerateEmbeddingsSettings
  ): Future[GenerateEmbeddingsResponse] =
    execPOST(
      EndPoint.embed,
      bodyParams = jsonBodyParams(
        Tag.inputs -> Some(
          inputs.map(input => Map("text" -> input))
        ),
        Tag.model -> Some(settings.model),
        Tag.parameters -> Some(
          Map(
            "input_type" -> settings.input_type.map(_.toString),
            "truncate" -> settings.truncate.toString
          )
        )
      )
    ).map(
      _.asSafeJson[GenerateEmbeddingsResponse]
    )

  override protected def handleErrorCodes(
    httpCode: Int,
    message: String
  ): Nothing =
    throw new PineconeScalaClientException(s"Code ${httpCode} : ${message}")
}

object PineconeInferenceServiceFactory extends PineconeServiceFactoryHelper {

  def apply(
    apiKey: String,
    timeouts: Option[Timeouts] = None
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeInferenceService = {
    new PineconeInferenceServiceImpl(apiKey, timeouts)
  }

  def apply(
    config: Config
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeInferenceService = {
    val timeouts = loadTimeouts(config)

    apply(
      apiKey = config.getString(s"$configPrefix.apiKey"),
      timeouts = timeouts.toOption
    )
  }
}
