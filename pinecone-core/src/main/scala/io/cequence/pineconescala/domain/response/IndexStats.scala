package io.cequence.pineconescala.domain.response

case class IndexStats(
  dimension: Int,
  indexFullness: Int,
  totalVectorCount: Int,
  namespaces: Map[String, NamespaceStats]
)

case class NamespaceStats(
  vectorCount: Int
)