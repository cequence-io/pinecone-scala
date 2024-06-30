package io.cequence.pineconescala.service

import akka.stream.Materializer
import io.cequence.pineconescala.domain.response.GenerateEmbeddingsResponse
import io.cequence.pineconescala.domain.settings.GenerateEmbeddingsSettings
import io.cequence.wsclient.JsonUtil.{JsonOps, toJson}
import io.cequence.wsclient.service.ws.{Timeouts, WSRequestHelper}
import play.api.libs.json.{JsObject, JsValue}
import io.cequence.pineconescala.JsonFormats._
import io.cequence.pineconescala.PineconeScalaClientException
import play.api.libs.ws.StandaloneWSRequest

import scala.concurrent.{ExecutionContext, Future}

private class PineconeInferenceServiceImpl(
  apiKey: String,
  override val coreUrl: String,
  explicitTimeouts: Option[Timeouts] = None
)(
  implicit val ec: ExecutionContext,
  val materializer: Materializer
) extends PineconeInferenceService
    with WSRequestHelper {

  override protected type PEP = EndPoint
  override protected type PT = Tag

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
      Tag.inputs -> Some(inputs),
      Tag.model -> Some(settings.model)
    )
    val otherParams: (Tag, Option[JsValue]) = {
      Tag.parameters -> Some(
        JsObject(
          Seq(
            Tag.input_type.toString() -> toJson(settings.input_type),
            Tag.truncate.toString() -> toJson(settings.truncate)
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
    request.addHttpHeaders(apiKeyHeader)
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

  def apply(
    apiKey: String,
    indexHostURL: String,
    timeouts: Option[Timeouts]
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeInferenceService = {
    val indexHostURLWithEndingSlash =
      if (indexHostURL.endsWith("/")) indexHostURL else s"$indexHostURL/"
    new PineconeInferenceServiceImpl(apiKey, indexHostURLWithEndingSlash, timeouts)
  }

}
