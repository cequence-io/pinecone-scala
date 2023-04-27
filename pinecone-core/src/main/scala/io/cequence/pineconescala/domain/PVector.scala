package io.cequence.pineconescala.domain

case class PVector(
  id: String,
  values: Seq[Double],
  sparseValues: Option[SparseVector] = None,
  metadata: Map[String, String]
)
