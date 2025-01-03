package io.cequence.pineconescala.domain.response

import io.cequence.pineconescala.domain.SparseVector

sealed trait EmbeddingsResponse

object EmbeddingsResponse {

  case class Dense(
    model: String,
    data: Seq[DenseEmbeddingsValues],
    usage: EmbeddingsUsageInfo
  )

  case class Sparse(
    model: String,
    data: Seq[SparseEmbeddingsValues],
    usage: EmbeddingsUsageInfo
  )
}

case class DenseEmbeddingsValues(values: Seq[Double])

case class SparseEmbeddingsValues(
  sparse_values: Seq[Double],
  sparse_indices: Seq[Long],
  // TODO: is it even supported?
  sparse_tokens: Seq[String]
) {
  def toSparseVector =
    SparseVector(
      indices = sparse_indices,
      values = sparse_values
    )
}

case class EmbeddingsUsageInfo(
  total_tokens: Int
)

@Deprecated
case class EmbeddingsInfo(
  embedding: Seq[Double],
  index: Int
)
