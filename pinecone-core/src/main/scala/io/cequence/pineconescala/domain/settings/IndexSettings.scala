package io.cequence.pineconescala.domain.settings

import io.cequence.pineconescala.domain.{Metric, PodType}

sealed trait IndexSettings {
  def metric: Metric
}

object IndexSettings {
  case class CreatePodBasedIndexSettings(
    metric: Metric,
    // The number of pods for the index to use, including replicas.
    pods: Int,

    // The number of replicas. Replicas duplicate your index. They provide higher availability and throughput
    replicas: Int,

    // The type of pod to use. One of s1, p1, or p2 appended with . and one of x1, x2, x4, or x8.
    podType: PodType,

    // The number of shards. Shards split your data across multiple pods so you can fit more data into an index.
    shards: Int = 1,

    // Configuration for the behavior of Pinecone's internal metadata index.
    // By default, all metadata is indexed; when metadata_config is present, only specified metadata fields are indexed.
    metadataConfig: Map[String, String] = Map(),

    // The name of the collection to create an index from
    sourceCollection: Option[String] = None
  ) extends IndexSettings

  case class CreateServerlessIndexSettings(
    metric: Metric,
    cloud: CloudProvider,
    region: Region
  ) extends IndexSettings
}
