package io.cequence.pineconescala.domain.response

import io.cequence.pineconescala.domain.PVector

case class FetchResponse(
  vectors: Map[String, PVector],
  namespace: String
)
