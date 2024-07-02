package io.cequence.pineconescala.domain.settings

import io.cequence.wsclient.domain.{EnumValue, NamedEnumValue}

case class GenerateEmbeddingsSettings(
  // ID of the model to use.
  model: String,

  // Common property used to distinguish between types of data.
  input_type: Option[EmbeddingsInputType] = None,

  // The number of dimensions the resulting output embeddings should have. Only supported in text-embedding-3 and later models.
  truncate: EmbeddingsTruncate = EmbeddingsTruncate.End
) {
  def withPassageInputType = copy(input_type = Some(EmbeddingsInputType.Passage))
  def withQueryInputType = copy(input_type = Some(EmbeddingsInputType.Query))
  def withoutTruncate = copy(truncate = EmbeddingsTruncate.None)
  def withEndTruncate = copy(truncate = EmbeddingsTruncate.End)
}

sealed abstract class EmbeddingsInputType(name: String) extends NamedEnumValue(name)

object EmbeddingsInputType {
  case object Passage extends EmbeddingsInputType(name = "passage")
  case object Query extends EmbeddingsInputType(name = "query")
}

sealed abstract class EmbeddingsTruncate(name: String) extends NamedEnumValue(name)

object EmbeddingsTruncate {
  case object None extends EmbeddingsTruncate(name = "NONE")
  case object End extends EmbeddingsTruncate(name = "END")
}

// TODO: do we need this?
sealed trait EmbeddingsEncodingFormat extends EnumValue

object EmbeddingsEncodingFormat {
  case object float extends EmbeddingsEncodingFormat
  case object base64 extends EmbeddingsEncodingFormat
}
