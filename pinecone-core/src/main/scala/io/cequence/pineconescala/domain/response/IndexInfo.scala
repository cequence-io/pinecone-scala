package io.cequence.pineconescala.domain.response

import io.cequence.pineconescala.domain.Metric

trait IndexInfo {
  def name: String
  def metric: Metric
  def dimension: Int
  def host: String
  def state: IndexStatus
}
