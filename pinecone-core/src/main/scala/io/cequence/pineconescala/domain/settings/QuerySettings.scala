package io.cequence.pineconescala.domain.settings

import io.cequence.pineconescala.domain.SparseVector

case class QuerySettings(
  // The number of results to return for each query.
  topK: Int,

  // The filter to apply. You can use vector metadata to limit your search.
  // See https://www.pinecone.io/docs/metadata-filtering/.
  filter: Map[String, String] = Map(),

  // Indicates whether vector values are included in the response.
  includeValues: Boolean,

  // Indicates whether metadata is included in the response as well as the ids.
  includeMetadata: Boolean,

  // Vector sparse data.
  // Represented as a list of indices and a list of corresponded values, which must be the same length.
  sparseVector: Option[SparseVector] = None,
)