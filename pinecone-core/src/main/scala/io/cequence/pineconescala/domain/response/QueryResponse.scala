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
  values: Seq[Double],
  sparseValues: Option[SparseVector],
  metadata: Map[String, String]
)