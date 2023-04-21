package io.cequence.pineconescala.domain.response

sealed trait CreateResponse

object CreateResponse {
  case object Created extends CreateResponse
  // Bad request. Encountered when request exceeds quota or an invalid index/collection name.
  case object BadRequest extends CreateResponse
  case object AlreadyExists extends CreateResponse
}