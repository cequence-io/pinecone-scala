package  io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.{Metric, PodType}
import io.cequence.pineconescala.domain.settings._

/**
 * Constants of [[PineconeVectorService]], mostly defaults
 */
trait PineconeServiceConsts {

  protected val defaultRequestTimeout = 120 * 1000 // two minute

  protected val defaultReadoutTimeout = 120 * 1000 // two minute

  protected val configPrefix = "pinecone-scala-client"

  protected val configFileName = "pinecone-scala-client.conf"

  object DefaultSettings {

    val Query = QuerySettings(
      topK = 10,
      includeValues = false,
      includeMetadata = false
    )

    val CreateIndex = CreateIndexSettings(
      metric = Metric.cosine,
      pods = 1,
      replicas = 1,
      pod_type = PodType.p1_x1,
      metadata_config = Map(),
      source_collection = None
    )
  }
}