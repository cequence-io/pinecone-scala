package io.cequence.pineconescala.domain

case class PVector(
  id: String,
  values: Seq[Double],
  sparseValues: Option[SparseVector],
  metadata: Map[String, String]
)
