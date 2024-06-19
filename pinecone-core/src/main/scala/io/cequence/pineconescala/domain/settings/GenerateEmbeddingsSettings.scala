package io.cequence.pineconescala.domain.settings

import io.cequence.wsclient.domain.EnumValue

case class GenerateEmbeddingsSettings(
  // ID of the model to use.
  model: String,

  // Common property used to distinguish between types of data.
  input_type: Option[String] = None,

  // The number of dimensions the resulting output embeddings should have. Only supported in text-embedding-3 and later models.
  truncate: String = "END"
)

sealed trait EmbeddingsEncodingFormat extends EnumValue

object EmbeddingsEncodingFormat {
  case object float extends EmbeddingsEncodingFormat
  case object base64 extends EmbeddingsEncodingFormat
}