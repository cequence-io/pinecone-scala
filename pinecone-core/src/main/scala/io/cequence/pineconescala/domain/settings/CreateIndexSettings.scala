package io.cequence.pineconescala.domain.settings

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

object Metric extends Enumeration {
  val euclidean, cosine, dotproduct = Value
}

object PodType extends Enumeration {
  val s1_x1 = Value("s1.x1")
  val s1_x2 = Value("s1.x2")
  val s1_x4 = Value("s1.x4")
  val s1_x8 = Value("s1.x8")

  val p1_x1 = Value("p1.x1")
  val p1_x2 = Value("p1.x2")
  val p1_x4 = Value("p1.x4")
  val p1_x8 = Value("p1.x8")

  val p2_x1 = Value("p2.x1")
  val p2_x2 = Value("p2.x2")
  val p2_x4 = Value("p2.x4")
  val p2_x8 = Value("p2.x8")
}