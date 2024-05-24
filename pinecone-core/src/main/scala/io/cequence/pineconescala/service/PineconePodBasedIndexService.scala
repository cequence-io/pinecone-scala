package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.{Metric, PodType}
import io.cequence.pineconescala.domain.response.CreateResponse
import io.cequence.pineconescala.domain.settings.IndexSettingsType.CreatePodBasedIndexSettings

import scala.concurrent.Future

trait PineconePodBasedIndexService
    extends PineconeIndexService[CreatePodBasedIndexSettings]
    with PineconePodBasedSpecifics {

  override def createIndex(
    name: String,
    dimension: Int,
    metric: Metric,
    settings: CreatePodBasedIndexSettings
  ): Future[CreateResponse]

  def defaultSettings: CreatePodBasedIndexSettings =
    CreatePodBasedIndexSettings(
      pods = 1,
      replicas = 1,
      podType = PodType.`s1.x1`
    )
}
