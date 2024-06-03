package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.response.{CreateResponse, ServerlessIndexInfo}
import io.cequence.pineconescala.domain.settings.IndexSettings.CreateServerlessIndexSettings

import scala.concurrent.Future

trait PineconeServerlessIndexService
    extends PineconeIndexService[CreateServerlessIndexSettings] {
  override def createIndex(
    name: String,
    dimension: Int,
    settings: CreateServerlessIndexSettings = DefaultSettings.CreateServerlessIndex
  ): Future[CreateResponse]

  def describeIndexTyped(
    indexName: String
  ): Future[Option[ServerlessIndexInfo]]
}
