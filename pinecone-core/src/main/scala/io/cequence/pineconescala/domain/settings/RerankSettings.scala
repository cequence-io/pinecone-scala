package io.cequence.pineconescala.domain.settings

case class RerankSettings(
  // Model to use for reranking (required)
  model: String,

  // The number of results to return sorted by relevance (optional)
  top_n: Option[Int] = None,

  // Whether to return the documents in the response (default: true)
  return_documents: Boolean = true,

  // The fields to rank the documents by (optional). If not provided the default is 'text'.
  rank_fields: Seq[String] = Nil,

  // Additional model-specific parameters for the reranker (optional)
  parameters: Map[String, Any] = Map.empty
)
