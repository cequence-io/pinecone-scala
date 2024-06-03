package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.response.{CreateResponse, PodBasedIndexInfo}
import io.cequence.pineconescala.domain.settings.IndexSettings.CreatePodBasedIndexSettings

import scala.concurrent.Future

trait PineconePodBasedIndexService
    extends PineconeIndexService[CreatePodBasedIndexSettings]
    with PineconePodBasedExtra {

  override def createIndex(
    name: String,
    dimension: Int,
    settings: CreatePodBasedIndexSettings = DefaultSettings.CreatePodBasedIndex
  ): Future[CreateResponse]

  def describeIndexTyped(
    indexName: String
  ): Future[Option[PodBasedIndexInfo]]
}
