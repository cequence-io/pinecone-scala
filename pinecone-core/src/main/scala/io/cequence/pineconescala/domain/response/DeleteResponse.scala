package io.cequence.pineconescala.domain.response

sealed trait DeleteResponse

object DeleteResponse {
  case object Deleted extends DeleteResponse
  case object NotFound extends DeleteResponse
}