package io.cequence.pineconescala.domain.response

import io.cequence.pineconescala.domain
import io.cequence.pineconescala.domain.Metric
import io.cequence.wsclient.domain.EnumValue

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
  metric: Metric,
  dimension: Int,
  pods: Int,
  replicas: Int,
  shards: Int,
  pod_type: Option[domain.PodType] // undefined for indexes running on "free" envs
)

case class PodBasedIndexStatusInfo(
  waiting: Seq[String],
  crashed: Seq[String],
  host: String,
  port: Int,
  state: IndexStatus,
  ready: Boolean
)

@Deprecated // TODO: remove?
case class PodBasedIndexConfig(
  k_bits: Int,
  hybrid: Int
)

sealed trait IndexStatus extends EnumValue

object IndexStatus {
  case object Initializing extends IndexStatus
  case object ScalingUp extends IndexStatus
  case object ScalingDown extends IndexStatus
  case object Terminating extends IndexStatus
  case object Ready extends IndexStatus
  case object InitializationFailed extends IndexStatus
}
