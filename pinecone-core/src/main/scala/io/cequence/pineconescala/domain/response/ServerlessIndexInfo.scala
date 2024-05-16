package io.cequence.pineconescala.domain.response

import io.cequence.pineconescala.domain.Metric

case class ServerlessIndexInfo(
  name: String,
  metric: Metric.Value,
  dimension: Int,
  status: ServerlessIndexStatus,
  host: String,
  spec: ServerlessIndexSpec
) extends IndexInfo {
  override def state = status.state
}

case class ServerlessIndexStatus(
  ready: Boolean,
  state: IndexStatus.Value
)

case class ServerlessIndexSpecAux(
  region: String,
  cloud: String
)

case class ServerlessIndexSpec(
  serverless: ServerlessIndexSpecAux
)