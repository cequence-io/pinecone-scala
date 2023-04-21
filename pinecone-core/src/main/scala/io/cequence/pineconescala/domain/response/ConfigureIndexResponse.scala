package io.cequence.pineconescala.domain.response

sealed trait ConfigureIndexResponse

object ConfigureIndexResponse {
  case object Updated extends ConfigureIndexResponse
  case object BadRequestNotEnoughQuota extends ConfigureIndexResponse
  case object NotFound extends ConfigureIndexResponse
}