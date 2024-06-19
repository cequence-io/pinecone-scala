package io.cequence.pineconescala.domain.response

case class GenerateEmbeddingsResponse(
  data: Seq[Double],
  model: String,
  usage: EmbeddingsUsageInfo
)

case class EmbeddingsInfo(
  embedding: Seq[Double],
  index: Int
)

case class EmbeddingsUsageInfo(
  total_tokens: Int
)
