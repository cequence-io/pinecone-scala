package io.cequence.pineconescala.domain.response

case class ListNamespacesResponse(
  namespaces: Seq[NamespaceDescription],
  pagination: Option[ListNamespacesPagination]
)

case class NamespaceDescription(
  name: String,
  recordCount: Long
)

case class ListNamespacesPagination(
  next: Option[String]
)
