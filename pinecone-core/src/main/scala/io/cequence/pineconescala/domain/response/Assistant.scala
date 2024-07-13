package io.cequence.pineconescala.domain.response

import io.cequence.wsclient.domain.EnumValue

import java.time.OffsetDateTime

final case class Assistant(name: String, metadata: Map[String, String] = Map.empty, status: Assistant.Status,
                           created_on: Option[OffsetDateTime], updated_on: Option[OffsetDateTime]) {
  def hasFailed: Boolean = hasStatus(Assistant.Status.Failed)
  def isInitializing: Boolean = hasStatus(Assistant.Status.Initializing)
  def isReady: Boolean = hasStatus(Assistant.Status.Ready)
  def isTerminating: Boolean = hasStatus(Assistant.Status.Terminating)
  def hasStatus(status: Assistant.Status): Boolean = this.status == status
}

object Assistant {

  sealed trait Status extends EnumValue

  object Status {
    case object Initializing extends Status
    case object Failed extends Status
    case object Ready extends Status
    case object Terminating extends Status
  }

}

final case class UserMessage(content: String)
