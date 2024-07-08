package io.cequence.pineconescala.domain.response

import io.cequence.wsclient.domain.EnumValue

import java.time.OffsetDateTime

final case class Assistant(name: String, metadata: Map[String, String] = Map.empty, status: Assistant.Status,
                           created_on: Option[OffsetDateTime], updated_on: Option[OffsetDateTime])

object Assistant {

  sealed trait Status extends EnumValue

  object Status {
    case object Initializing extends Status
    case object Failed extends Status
    case object Ready extends Status
    case object Terminating extends Status
  }

}
