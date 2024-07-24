package io.cequence.pineconescala.domain.response

case class ListVectorIdsResponse(
  vectors: Seq[VectorId],
  pagination: Option[ListVectorIdsPagination],
  namespace: String
)

case class VectorId(id: String)

case class ListVectorIdsPagination(
  next: Option[String],
  previous: Option[String]
)