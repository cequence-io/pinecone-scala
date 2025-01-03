package io.cequence.pineconescala.domain.settings

case class QuerySettings(
  // The number of results to return for each query.
  topK: Int,

  // The filter to apply. You can use vector metadata to limit your search.
  // See https://www.pinecone.io/docs/metadata-filtering/.
  filter: Map[String, Any] = Map(),

  // Indicates whether vector values are included in the response.
  includeValues: Boolean,

  // Indicates whether metadata is included in the response as well as the ids.
  includeMetadata: Boolean
)