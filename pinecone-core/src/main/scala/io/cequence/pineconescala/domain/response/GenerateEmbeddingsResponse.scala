package io.cequence.pineconescala.domain.response

case class GenerateEmbeddingsResponse(
  data: Seq[EmbeddingsValues],
  model: String,
  usage: EmbeddingsUsageInfo
)

case class EmbeddingsValues(values: Seq[Double])

case class EmbeddingsInfo(
  embedding: Seq[Double],
  index: Int
)

case class EmbeddingsUsageInfo(
  total_tokens: Int
)
