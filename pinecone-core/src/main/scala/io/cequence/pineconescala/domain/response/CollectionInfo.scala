package io.cequence.pineconescala.domain.response

case class CollectionInfo(
  name: String,
  // The size of the collection in bytes. Not available when initializing
  size: Option[Int],
  dimension: Int,
  status: String // TODO: this can be potentially an enum (== IndexStatus?)
)
