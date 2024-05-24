package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.Metric
import io.cequence.pineconescala.domain.response.CreateResponse
import io.cequence.pineconescala.domain.settings.{CloudProvider, Region}
import io.cequence.pineconescala.domain.settings.IndexSettingsType.CreateServerlessIndexSettings

import scala.concurrent.Future

trait PineconeServerlessIndexService
    extends PineconeIndexService[CreateServerlessIndexSettings] {
  override def createIndex(
    name: String,
    dimension: Int,
    metric: Metric,
    settings: CreateServerlessIndexSettings = defaultSettings
  ): Future[CreateResponse]

  private def defaultSettings: CreateServerlessIndexSettings =
    CreateServerlessIndexSettings(
      CloudProvider.AWS,
      Region.EUWest1
    )
}
