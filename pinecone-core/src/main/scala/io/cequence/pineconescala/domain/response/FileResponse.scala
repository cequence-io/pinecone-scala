package io.cequence.pineconescala.domain.response

import io.cequence.wsclient.domain.EnumValue

import java.time.OffsetDateTime
import java.util.UUID

final case class FileResponse(name: String, id: UUID, metadata: Map[String, String] = Map.empty, created_on: Option[OffsetDateTime], updated_on: Option[OffsetDateTime], status: FileResponse.Status) {
  def isProcessing: Boolean = hasStatus(FileResponse.Status.Processing)
  def isAvailable: Boolean = hasStatus(FileResponse.Status.Available)
  def isDeleting: Boolean = hasStatus(FileResponse.Status.Deleting)
  def hasProcessingFailed: Boolean = hasStatus(FileResponse.Status.ProcessingFailed)
  def hasStatus(status: FileResponse.Status): Boolean = this.status == status
}

object FileResponse {

  sealed trait Status extends EnumValue

  object Status {
    case object Processing extends Status
    case object Available extends Status
    case object Deleting extends Status
    case object ProcessingFailed extends Status
  }

}