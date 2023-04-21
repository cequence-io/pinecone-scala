package io.cequence.pineconescala.domain

case class SparseVector(
  indices: Seq[Int],
  values: Seq[Double]
)