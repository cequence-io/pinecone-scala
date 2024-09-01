package io.cequence.pineconescala.domain.response

case class RerankResponse(
  data: Seq[RerankedDocument],
  usage: RerankUsage
)

case class RerankedDocument(
  // The index of the document in the original list
  index: Int,
  document: Option[Map[String, Any]],
  score: Double
)

case class RerankUsage(
  total_tokens: Option[Int],
  rerank_units: Int
)
