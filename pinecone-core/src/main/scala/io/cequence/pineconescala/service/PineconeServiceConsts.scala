package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.settings.IndexSettings.{
  CreatePodBasedIndexSettings,
  CreateServerlessIndexSettings
}
import io.cequence.pineconescala.domain.{Metric, PodType}
import io.cequence.pineconescala.domain.settings._

/**
 * Constants of [[PineconeVectorService]], mostly defaults
 */
trait PineconeServiceConsts {

  protected val configPrefix = "pinecone-scala-client"

  protected val configFileName = "pinecone-scala-client.conf"

  object DefaultSettings {

    val Query = QuerySettings(
      topK = 10,
      includeValues = false,
      includeMetadata = true // by default include metadata
    )

    val CreatePodBasedIndex = CreatePodBasedIndexSettings(
      metric = Metric.cosine,
      pods = 1,
      replicas = 1,
      podType = PodType.p1_x1,
      metadataConfig = Map(),
      sourceCollection = None
    )

    val CreateServerlessIndex = CreateServerlessIndexSettings(
      Metric.cosine,
      CloudProvider.AWS,
      Region.EUWest1
    )
  }
}
