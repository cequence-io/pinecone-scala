package io.cequence.pineconescala.domain

case class SparseVector(
  indices: Seq[Long],
  values: Seq[Double]
)