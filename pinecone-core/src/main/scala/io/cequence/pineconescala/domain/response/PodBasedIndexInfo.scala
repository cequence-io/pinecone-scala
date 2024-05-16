package io.cequence.pineconescala.domain.response

import io.cequence.pineconescala.domain.{Metric, PodType}

case class PodBasedIndexInfo(
  database: PodBasedIndexDatabaseInfo,
  status: PodBasedIndexStatusInfo
  //  index_config: IndexConfig,
  //  metadata_config: String,
) extends IndexInfo {

  override def name = database.name

  override def metric = database.metric

  override def dimension = database.dimension

  override def host = status.host

  override def state = status.state
}

case class PodBasedIndexDatabaseInfo(
  name: String,
  metric: Metric.Value,
  dimension: Int,
  pods: Int,
  replicas: Int,
  shards: Int,
  pod_type: Option[PodType.Value] // undefined for indexes running on "free" envs
)

case class PodBasedIndexStatusInfo(
  waiting: Seq[String],
  crashed: Seq[String],
  host: String,
  port: Int,
  state: IndexStatus.Value,
  ready: Boolean
)

@Deprecated // TODO: remove?
case class PodBasedIndexConfig(
  k_bits: Int,
  hybrid: Int
)

// TODO: turn this into a sealed trait
object IndexStatus extends Enumeration {
  val Initializing, ScalingUp, ScalingDown, Terminating, Ready, InitializationFailed = Value
}