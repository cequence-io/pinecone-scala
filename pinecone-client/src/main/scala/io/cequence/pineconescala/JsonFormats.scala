package io.cequence.pineconescala

import io.cequence.pineconescala.domain.response._
import io.cequence.pineconescala.domain.{Metric, PVector, PodType, SparseVector}
import io.cequence.wsclient.JsonUtil.enumFormat
import play.api.libs.json.{Format, Json}

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
      `s1.x1`,
      `s1.x2`,
      `s1.x4`,
      `s1.x8`,
      `p1.x1`,
      `p1.x2`,
      `p1.x4`,
      `p1.x8`,
      `p2.x1`,
      `p2.x2`,
      `p2.x4`,
      `p2.x8`
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
}
