package io.cequence.pineconescala

import io.cequence.pineconescala.domain.response.Choice.ChatCompletionMessage
import io.cequence.pineconescala.domain.response._
import io.cequence.pineconescala.domain.settings.{EmbeddingsInputType, EmbeddingsTruncate}
import io.cequence.pineconescala.domain.settings.EmbeddingsInputType.{Passage, Query}
import io.cequence.pineconescala.domain.{Metric, PVector, PodType, SparseVector, response}
import io.cequence.wsclient.JsonUtil.enumFormat
import play.api.libs.json._
import play.api.libs.functional.syntax._

import java.time.OffsetDateTime
import java.util.UUID

object JsonFormats {
  // vector-stuff formats
  implicit lazy val namespaceStatsFormat: Format[NamespaceStats] = Json.format[NamespaceStats]
  implicit lazy val indexStatsFormat: Format[IndexStats] = Json.format[IndexStats]
  implicit lazy val sparseVectorFormat: Format[SparseVector] = Json.format[SparseVector]
  implicit lazy val vectorFormat: Format[PVector] = Json.format[PVector]
  implicit lazy val matchFormat: Format[Match] = Json.format[Match]
  implicit lazy val queryResultFormat: Format[QueryResponse] = Json.format[QueryResponse]
  implicit lazy val fetchResponseFormat: Format[FetchResponse] = Json.format[FetchResponse]
  implicit lazy val vectorIdFormat: Format[VectorId] = Json.format[VectorId]
  implicit lazy val listVectorIdsPaginationFormat: Format[ListVectorIdsPagination] =
    Json.format[ListVectorIdsPagination]
  implicit lazy val listVectorIdsResponseFormat: Format[ListVectorIdsResponse] =
    Json.format[ListVectorIdsResponse]

  // index/collection formats
  implicit lazy val collectionInfoFormat: Format[CollectionInfo] = Json.format[CollectionInfo]
  implicit lazy val indexStatusFormat: Format[IndexStatus] = {
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
  implicit lazy val podTypeFormat: Format[PodType] = {
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
  implicit lazy val metricFormat: Format[Metric] = {
    import Metric._
    enumFormat[Metric](
      euclidean,
      cosine,
      dotproduct
    )
  }

  // pod-based
  implicit lazy val indexConfigFormat: Format[PodBasedIndexConfig] =
    Json.format[PodBasedIndexConfig]
  implicit lazy val indexDatabaseInfoFormat: Format[PodBasedIndexDatabaseInfo] =
    Json.format[PodBasedIndexDatabaseInfo]
  implicit lazy val indexStatusInfoFormat: Format[PodBasedIndexStatusInfo] =
    Json.format[PodBasedIndexStatusInfo]
  implicit lazy val indexInfoFormat: Format[PodBasedIndexInfo] = Json.format[PodBasedIndexInfo]

  // serverless
  implicit lazy val serverlessIndexStatusFormat: Format[ServerlessIndexStatus] =
    Json.format[ServerlessIndexStatus]
  implicit lazy val serverlessIndexSpecAuxFormat: Format[ServerlessIndexSpecAux] =
    Json.format[ServerlessIndexSpecAux]
  implicit lazy val serverlessIndexSpecFormat: Format[ServerlessIndexSpec] =
    Json.format[ServerlessIndexSpec]
  implicit lazy val serverlessIndexInfoFormat: Format[ServerlessIndexInfo] =
    Json.format[ServerlessIndexInfo]

  // embeddings
  implicit lazy val embeddingUsageInfoReads: Reads[EmbeddingsUsageInfo] =
    Json.reads[EmbeddingsUsageInfo]
  implicit lazy val embeddingInfoReads: Reads[EmbeddingsInfo] = Json.reads[EmbeddingsInfo]
  implicit lazy val embeddingValuesReads: Reads[EmbeddingsValues] = Json.reads[EmbeddingsValues]
  implicit lazy val embeddingResponseReads: Reads[GenerateEmbeddingsResponse] =
    Json.reads[GenerateEmbeddingsResponse]

  implicit lazy val embeddingsInputTypeWrites: Writes[EmbeddingsInputType] = enumFormat(
    Query,
    Passage
  )

  implicit lazy val embeddingsTruncateWrites: Writes[EmbeddingsTruncate] = enumFormat(
    EmbeddingsTruncate.None,
    EmbeddingsTruncate.End
  )

  // assistants
  implicit lazy val assistantStatusFormat: Format[Assistant.Status] = enumFormat(
    Assistant.Status.Initializing,
    Assistant.Status.Failed,
    Assistant.Status.Ready,
    Assistant.Status.Terminating
  )
  implicit lazy val assistantFormat: Format[Assistant] = {
    val reads: Reads[Assistant] = (
      (__ \ "name").read[String] and
        (__ \ "metadata").readWithDefault[Map[String, String]](Map.empty[String, String]) and
        (__ \ "status").read[Assistant.Status] and
        (__ \ "created_on").readNullable[OffsetDateTime] and
        (__ \ "updated_on").readNullable[OffsetDateTime]
    )(Assistant.apply _)

    val writes: Writes[Assistant] = Json.writes[Assistant]
    Format(reads, writes)
  }

  implicit lazy val listAssistantsResponseFormat: Format[ListAssistantsResponse] =
    Json.format[ListAssistantsResponse]

  // files
  implicit lazy val fileStatusFormat: Format[FileResponse.Status] = enumFormat(
    FileResponse.Status.Deleting,
    FileResponse.Status.Available,
    FileResponse.Status.Processing,
    FileResponse.Status.ProcessingFailed
  )
  implicit lazy val fileFormat: Format[FileResponse] = {
    val reads: Reads[FileResponse] = (
      (__ \ "name").read[String] and
        (__ \ "id").read[UUID] and
        (__ \ "metadata").readWithDefault[Map[String, String]](Map.empty[String, String]) and
        (__ \ "created_on").readNullable[OffsetDateTime] and
        (__ \ "updated_on").readNullable[OffsetDateTime] and
        (__ \ "status").read[response.FileResponse.Status]
    )(FileResponse.apply _)

    val writes: Writes[FileResponse] = Json.writes[FileResponse]
    Format(reads, writes)
  }

  implicit lazy val listFilesResponseFormat: Format[ListFilesResponse] =
    Json.format[ListFilesResponse]

  // chat
  implicit lazy val userMessagesFormat: Writes[UserMessage] =
    Writes[UserMessage] { userMessage =>
      Json.obj(
        "role" -> "user",
        "content" -> userMessage.content
      )
    }

  implicit lazy val chatCompletionMessageFormat: Format[ChatCompletionMessage] =
    Json.format[ChatCompletionMessage]
  implicit lazy val chatCompletionChoiceRoleFormat: Format[Choice.Role] = enumFormat(
    Choice.Role.user,
    Choice.Role.assistant
  )
  implicit lazy val chatCompletionChoiceFinishReasonFormat: Format[Choice.FinishReason] =
    enumFormat(
      Choice.FinishReason.Stop,
      Choice.FinishReason.Length,
      Choice.FinishReason.ToolCalls,
      Choice.FinishReason.ContentFilter,
      Choice.FinishReason.FunctionCall
    )
  implicit lazy val chatCompletionChoiceFormat: Format[Choice] = Json.format[Choice]
  implicit lazy val chatCompletionModelFormat: Format[ChatCompletionResponse] =
    Json.format[ChatCompletionResponse]
}
