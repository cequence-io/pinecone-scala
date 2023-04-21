package io.cequence.pineconescala.domain.response

case class CollectionInfo(
  name: String,
  // The size of the collection in bytes.
  size: Int,
  status: String
)
