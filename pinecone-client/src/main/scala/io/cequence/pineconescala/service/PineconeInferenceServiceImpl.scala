package io.cequence.pineconescala.service

import akka.stream.Materializer
import io.cequence.pineconescala.domain.response.{EmbeddingsResponse, EvaluateResponse, RerankResponse}
import io.cequence.pineconescala.domain.settings.{GenerateEmbeddingsSettings, RerankSettings}
import io.cequence.wsclient.ResponseImplicits._
import io.cequence.wsclient.service.ws.{PlayWSClientEngine, Timeouts}
import io.cequence.pineconescala.JsonFormats._
import io.cequence.pineconescala.PineconeScalaClientException
import io.cequence.wsclient.domain.{Response, WsRequestContext}
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

  private val regularURL = "api.pinecone.io/"
  private val prodURL = "prod-1-data.ke.pinecone.io/"

  // we use play-ws backend
  override protected val engine: WSClientEngine = PlayWSClientEngine(
    coreUrl = "https://", // TODO: change to regularURL eventually
    requestContext = WsRequestContext(
      authHeaders = Seq(
        "Api-Key" -> apiKey,
        "X-Pinecone-API-Version" -> "2024-10"
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
  ): Future[EmbeddingsResponse.Dense] =
    createSparseEmbeddingsAux(inputs, settings).map(
      _.asSafeJson[EmbeddingsResponse.Dense]
    )

  override def createSparseEmbeddings(
    inputs: Seq[String],
    settings: GenerateEmbeddingsSettings
  ): Future[EmbeddingsResponse.Sparse] =
    createSparseEmbeddingsAux(inputs, settings).map(
      _.asSafeJson[EmbeddingsResponse.Sparse]
    )

  private def createSparseEmbeddingsAux(
    inputs: Seq[String],
    settings: GenerateEmbeddingsSettings
  ): Future[Response] =
    execPOST(
      EndPoint.embed(regularURL),
      bodyParams = jsonBodyParams(
        Tag.inputs -> Some(
          inputs.map(input => Map("text" -> input))
        ),
        Tag.model -> Some(settings.model),
        Tag.parameters -> Some(
          Map(
            "input_type" -> settings.input_type.map(_.toString),
            "truncate" -> settings.truncate.toString,
            "return_tokens" -> settings.return_tokens
          )
        )
      )
    )

  /**
   * Using a reranker to rerank a list of items for a query.
   *
   * @param query
   *   The query to rerank documents against (required)
   * @param documents
   *   The documents to rerank (required)
   * @param settings
   * @return
   *
   * @see
   *   <a href="https://docs.pinecone.io/reference/api/2024-10/inference/rerank">Pinecone
   *   Doc</a>
   */
  override def rerank(
    query: String,
    documents: Seq[Map[String, Any]],
    settings: RerankSettings
  ): Future[RerankResponse] =
    execPOST(
      EndPoint.rerank(regularURL),
      bodyParams = jsonBodyParams(
        Tag.query -> Some(query),
        Tag.documents -> Some(documents),
        Tag.model -> Some(settings.model),
        Tag.top_n -> settings.top_n,
        Tag.return_documents -> Some(settings.return_documents),
        Tag.rank_fields -> (
          if (settings.rank_fields.nonEmpty) Some(settings.rank_fields) else None
        ),
        Tag.parameters -> (
          if (settings.parameters.nonEmpty) Some(settings.parameters) else None
        )
      )
    ).map(
      _.asSafeJson[RerankResponse]
    )

  override def evaluate(
    question: String,
    answer: String,
    groundTruthAnswer: String
  ): Future[EvaluateResponse] =
    execPOST(
      EndPoint.evaluate(prodURL),
      bodyParams = jsonBodyParams(
        Tag.question -> Some(question),
        Tag.answer -> Some(answer),
        Tag.ground_truth_answer -> Some(groundTruthAnswer)
      )
    ).map(
      _.asSafeJson[EvaluateResponse]
    )

  override protected def handleErrorCodes(
    httpCode: Int,
    message: String
  ): Nothing =
    throw new PineconeScalaClientException(s"Code ${httpCode} : ${message}")
}

object PineconeInferenceServiceFactory
    extends SimplePineconeServiceFactory[PineconeInferenceService] {

  override def apply(
    apiKey: String,
    timeouts: Option[Timeouts] = None
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): PineconeInferenceService =
    new PineconeInferenceServiceImpl(apiKey, timeouts)
}
