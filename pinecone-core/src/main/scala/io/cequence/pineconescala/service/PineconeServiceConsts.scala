package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.settings.IndexSettings.{CreatePodBasedIndexSettings, CreateServerlessIndexSettings}
import io.cequence.pineconescala.domain.{EmbeddingModelId, Metric, PodType, RerankModelId}
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

    val GenerateEmbeddings = GenerateEmbeddingsSettings(
      model = EmbeddingModelId.multilingual_e5_large,
      input_type = Some(EmbeddingsInputType.Query)
    )

    val Rerank = RerankSettings(
      model = RerankModelId.bge_reranker_v2_m3
    )
  }
}
