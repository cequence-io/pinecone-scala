package io.cequence.pineconescala.service

import akka.stream.Materializer
import com.typesafe.config.Config
import io.cequence.pineconescala.domain.response.GenerateEmbeddingsResponse
import io.cequence.pineconescala.domain.settings.{GenerateEmbeddingsSettings, IndexSettings}
import io.cequence.wsclient.JsonUtil.{JsonOps, toJson}
import io.cequence.wsclient.service.ws.{Timeouts, WSRequestHelper}
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import io.cequence.pineconescala.JsonFormats._
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.wsclient.domain.WsRequestContext
import play.api.libs.ws.StandaloneWSRequest

import scala.concurrent.{ExecutionContext, Future}

private class PineconeInferenceServiceImpl(
  apiKey: String,
  explicitTimeouts: Option[Timeouts] = None
)(
  implicit val ec: ExecutionContext,
  val materializer: Materializer
) extends PineconeInferenceService
    with WSRequestHelper {

  override protected type PEP = EndPoint
  override protected type PT = Tag
  override val coreUrl: String = "https://api.pinecone.io/"
  override protected val requestContext = WsRequestContext(explTimeouts = explicitTimeouts)

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
  ): Future[GenerateEmbeddingsResponse] = {
    val basicParams: Seq[(Tag, Option[JsValue])] = jsonBodyParams(
      Tag.inputs -> Some(JsArray(inputs.map(input => JsObject(Seq("text" -> toJson(input)))))),
      Tag.model -> Some(settings.model)
    )
    val otherParams: (Tag, Option[JsValue]) = {
      Tag.parameters -> Some(
        JsObject(
          Seq(
            Tag.input_type.toString() -> Json.toJson(settings.input_type),
            Tag.truncate.toString() -> Json.toJson(settings.truncate)
          )
        )
      )
    }
    execPOST(
      EndPoint.embed,
      bodyParams = basicParams :+ otherParams
    ).map(
      _.asSafe[GenerateEmbeddingsResponse]
    )

  }

  override def addHeaders(request: StandaloneWSRequest) = {
    val apiKeyHeader = ("Api-Key", apiKey)
    val versionHeader = ("X-Pinecone-API-Version", "2024-07")
    request
      .addHttpHeaders(apiKeyHeader)
      .addHttpHeaders(versionHeader)
  }

  override protected def handleErrorCodes(
    httpCode: Int,
    message: String
  ): Nothing =
    throw new PineconeScalaClientException(s"Code ${httpCode} : ${message}")

  override def close(): Unit =
    client.close()

}

object PineconeInferenceServiceFactory extends PineconeServiceFactoryHelper {

  def apply[S <: IndexSettings](
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
