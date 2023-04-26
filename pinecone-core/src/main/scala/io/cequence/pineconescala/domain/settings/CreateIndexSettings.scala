package io.cequence.pineconescala.domain.settings

import io.cequence.pineconescala.domain.{Metric, PodType}

case class CreateIndexSettings(
  // The distance metric to be used for similarity search.
  // You can use 'euclidean', 'cosine', or 'dotproduct'.
  metric: Metric.Value,

  // The number of pods for the index to use, including replicas.
  pods: Int,

  // The number of replicas. Replicas duplicate your index. They provide higher availability and throughput
  replicas: Int,

  // The type of pod to use. One of s1, p1, or p2 appended with . and one of x1, x2, x4, or x8.
  pod_type: PodType.Value,

  // Configuration for the behavior of Pinecone's internal metadata index.
  // By default, all metadata is indexed; when metadata_config is present, only specified metadata fields are indexed.
  metadata_config: Map[String, String] = Map(),

  // The name of the collection to create an index from
  source_collection: Option[String] = None
)