package io.cequence.pineconescala.domain.response

import io.cequence.pineconescala.domain.settings.{Metric, PodType}

case class IndexInfo(
  name: String,
  dimension: Int,
  @Deprecated
  index_type: String,
  metric: Metric.Value,
  pods: Int,
  replicas: Int,
  shards: Int,
  pod_type: PodType.Value,
  index_config: IndexConfig,
  metadata_config: String,
  status: IndexStatus.Value
)

case class IndexConfig(
  k_bits: Int,
  hybrid: Int
)

object IndexStatus extends Enumeration {
  val Initializing, ScalingUp, ScalingDown, Terminating, Ready, InitializationFailed = Value
}