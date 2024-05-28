package io.cequence.pineconescala.domain

import io.cequence.wsclient.domain.EnumValue

sealed trait Metric extends EnumValue

object Metric {
  case object euclidean extends Metric
  case object cosine extends Metric
  case object dotproduct extends Metric
}
