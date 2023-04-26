package io.cequence.pineconescala.domain.response

import io.cequence.pineconescala.domain.{Metric, PodType}

case class IndexInfo(
  database: IndexDatabaseInfo,
  status: IndexStatusInfo
//  index_config: IndexConfig,
//  metadata_config: String,
)

case class IndexDatabaseInfo(
  name: String,
  metric: Metric.Value,
  dimension: Int,
  pods: Int,
  replicas: Int,
  shards: Int,
  pod_type: PodType.Value
)

case class IndexStatusInfo(
  waiting: Seq[String],
  crashed: Seq[String],
  host: String,
  port: Int,
  state: IndexStatus.Value,
  ready: Boolean
)

@Deprecated // TODO: remove?
case class IndexConfig(
  k_bits: Int,
  hybrid: Int
)

object IndexStatus extends Enumeration {
  val Initializing, ScalingUp, ScalingDown, Terminating, Ready, InitializationFailed = Value
}