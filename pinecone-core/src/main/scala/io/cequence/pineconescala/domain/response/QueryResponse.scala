package io.cequence.pineconescala.domain.response

import io.cequence.pineconescala.domain.SparseVector

case class QueryResponse(
  matches: Seq[Match],
  namespace: String,
  @Deprecated
  results: Seq[String], // TODO: is it even used?
)

case class Match(
  id: String,
  score: Double,
  values: Seq[Double],                   // not returned when includeValues = false
  sparseValues: Option[SparseVector],
  metadata: Option[Map[String, String]]  // not returned when includeMetadata = false
) {
  val metadataUnwrapped : Map[String, String] = metadata.getOrElse(Map[String, String]())
  def getMetadata(key: String): Option[String] = metadataUnwrapped.get(key)
}