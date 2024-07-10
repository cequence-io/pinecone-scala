package io.cequence.pineconescala.domain.response

import io.cequence.wsclient.domain.EnumValue

import java.time.OffsetDateTime
import java.util.UUID

final case class FileResponse(name: String, id: UUID, metadata: Map[String, String] = Map.empty, created_on: Option[OffsetDateTime], updated_on: Option[OffsetDateTime])

object FileResponse {

  sealed trait Status extends EnumValue

  object Status {
    case object Processing extends Status
    case object Available extends Status
    case object Deleting extends Status
    case object ProcessingFailed extends Status
  }

}