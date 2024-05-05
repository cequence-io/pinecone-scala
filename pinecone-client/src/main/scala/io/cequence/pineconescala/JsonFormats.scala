package  io.cequence.pineconescala

import io.cequence.pineconescala.JsonUtil.EnumFormat
import io.cequence.pineconescala.domain.{Metric, PVector, PodType, SparseVector}
import io.cequence.pineconescala.domain.response._
import io.cequence.pineconescala.domain.settings._
import play.api.libs.json.{Format, Json, _}

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
  implicit val listVectorIdsPaginationFormat: Format[ListVectorIdsPagination] = Json.format[ListVectorIdsPagination]
  implicit val listVectorIdsResponseFormat: Format[ListVectorIdsResponse] = Json.format[ListVectorIdsResponse]


  // index/collection formats
  implicit val collectionInfoFormat: Format[CollectionInfo] = Json.format[CollectionInfo]
  implicit val indexStatusFormat: Format[IndexStatus.Value] = EnumFormat(IndexStatus.values)
  implicit val podTypeFormat: Format[PodType.Value] = EnumFormat(PodType.values)
  implicit val metricFormat: Format[Metric.Value] = EnumFormat(Metric.values)
  implicit val indexConfigFormat: Format[IndexConfig] = Json.format[IndexConfig]
  implicit val indexDatabaseInfoFormat: Format[IndexDatabaseInfo] = Json.format[IndexDatabaseInfo]
  implicit val indexStatusInfoFormat: Format[IndexStatusInfo] = Json.format[IndexStatusInfo]
  implicit val indexInfoFormat: Format[IndexInfo] = Json.format[IndexInfo]
}