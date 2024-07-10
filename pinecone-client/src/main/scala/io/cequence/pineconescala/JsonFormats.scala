package io.cequence.pineconescala

import io.cequence.pineconescala.domain.response.Choice.ChatCompletionMessage
import io.cequence.pineconescala.domain.response._
import io.cequence.pineconescala.domain.settings.{EmbeddingsInputType, EmbeddingsTruncate}
import io.cequence.pineconescala.domain.settings.EmbeddingsInputType.{Passage, Query}
import io.cequence.pineconescala.domain.{Metric, PVector, PodType, SparseVector}
import io.cequence.wsclient.JsonUtil.enumFormat
import play.api.libs.json.{Format, JsString, Json, Reads, Writes}

object JsonFormats {
  // vector-stuff formats
  implicit val namespaceStatsFormat: Format[NamespaceStats] = Json.format[NamespaceStats]
  implicit val indexStatsFormat: Format[IndexStats] = Json.format[IndexStats]
  implicit val sparseVectorFormat: Format[SparseVector] = Json.format[SparseVector]
  implicit val vectorFormat: Format[PVector] = Json.format[PVector]
  implicit val matchFormat: Format[Match] = Json.format[Match]
  implicit val queryResultFormat: Format[QueryResponse] = Json.format[QueryResponse]
  implicit val fetchResponseFormat: Format[FetchResponse] = Json.format[FetchResponse]
  implicit val vectorIdFormat: Format[VectorId] = Json.format[VectorId]
  implicit val listVectorIdsPaginationFormat: Format[ListVectorIdsPagination] =
    Json.format[ListVectorIdsPagination]
  implicit val listVectorIdsResponseFormat: Format[ListVectorIdsResponse] =
    Json.format[ListVectorIdsResponse]

  // index/collection formats
  implicit val collectionInfoFormat: Format[CollectionInfo] = Json.format[CollectionInfo]
  implicit val indexStatusFormat: Format[IndexStatus] = {
    import IndexStatus._
    enumFormat[IndexStatus](
      Initializing,
      ScalingUp,
      ScalingDown,
      Terminating,
      Ready,
      InitializationFailed
    )
  }
  implicit val podTypeFormat: Format[PodType] = {
    import PodType._
    enumFormat[PodType](
      s1_x1,
      s1_x2,
      s1_x4,
      s1_x8,
      p1_x1,
      p1_x2,
      p1_x4,
      p1_x8,
      p2_x1,
      p2_x2,
      p2_x4,
      p2_x8
    )
  }
  implicit val metricFormat: Format[Metric] = {
    import Metric._
    enumFormat[Metric](
      euclidean,
      cosine,
      dotproduct
    )
  }

  // pod-based
  implicit val indexConfigFormat: Format[PodBasedIndexConfig] =
    Json.format[PodBasedIndexConfig]
  implicit val indexDatabaseInfoFormat: Format[PodBasedIndexDatabaseInfo] =
    Json.format[PodBasedIndexDatabaseInfo]
  implicit val indexStatusInfoFormat: Format[PodBasedIndexStatusInfo] =
    Json.format[PodBasedIndexStatusInfo]
  implicit val indexInfoFormat: Format[PodBasedIndexInfo] = Json.format[PodBasedIndexInfo]

  // serverless
  implicit val serverlessIndexStatusFormat: Format[ServerlessIndexStatus] =
    Json.format[ServerlessIndexStatus]
  implicit val serverlessIndexSpecAuxFormat: Format[ServerlessIndexSpecAux] =
    Json.format[ServerlessIndexSpecAux]
  implicit val serverlessIndexSpecFormat: Format[ServerlessIndexSpec] =
    Json.format[ServerlessIndexSpec]
  implicit val serverlessIndexInfoFormat: Format[ServerlessIndexInfo] =
    Json.format[ServerlessIndexInfo]

  // embeddings
  implicit val embeddingUsageInfoReads: Reads[EmbeddingsUsageInfo] =
    Json.reads[EmbeddingsUsageInfo]
  implicit val embeddingInfoReads: Reads[EmbeddingsInfo] = Json.reads[EmbeddingsInfo]
  implicit val embeddingValuesReads: Reads[EmbeddingsValues] = Json.reads[EmbeddingsValues]
  implicit val embeddingResponseReads: Reads[GenerateEmbeddingsResponse] = Json.reads[GenerateEmbeddingsResponse]

  implicit val embeddingsInputTypeWrites: Writes[EmbeddingsInputType] = enumFormat(
    Query,
    Passage
  )

  implicit val embeddingsTruncateWrites: Writes[EmbeddingsTruncate] = enumFormat(
    EmbeddingsTruncate.None,
    EmbeddingsTruncate.End
  )

  // assistants
  implicit val assistantStatusFormat: Format[Assistant.Status] = enumFormat(
    Assistant.Status.Initializing,
    Assistant.Status.Failed,
    Assistant.Status.Ready,
    Assistant.Status.Terminating,
  )
  implicit val assistantFormat: Format[Assistant] = Json.format[Assistant]
  implicit val listAssistantsResponseFormat: Format[ListAssistantsResponse] = Json.format[ListAssistantsResponse]

  // files
  implicit val fileStatusFormat: Format[FileResponse.Status] = enumFormat(
    FileResponse.Status.Deleting,
    FileResponse.Status.Available,
    FileResponse.Status.Processing,
    FileResponse.Status.ProcessingFailed,
  )
  implicit val fileFormat: Format[FileResponse] = Json.format[FileResponse]
  implicit val listFilesResponseFormat: Format[ListFilesResponse] = Json.format[ListFilesResponse]

  // chat
  implicit val chatCompletionMessageFormat: Format[ChatCompletionMessage] = Json.format[ChatCompletionMessage]
  implicit val chatCompletionChoiceRoleFormat: Format[Choice.Role] = enumFormat(
    Choice.Role.user,
    Choice.Role.assistant
  )
  implicit val chatCompletionChoiceFinishReasonFormat: Format[Choice.FinishReason] = enumFormat(
    Choice.FinishReason.Stop,
    Choice.FinishReason.Length,
    Choice.FinishReason.ToolCalls,
    Choice.FinishReason.ContentFilter,
    Choice.FinishReason.FunctionCall
  )
  implicit val chatCompletionChoiceFormat: Format[Choice] = Json.format[Choice]
  implicit val chatCompletionModelFormat: Format[ChatCompletionResponse] = Json.format[ChatCompletionResponse]
}
