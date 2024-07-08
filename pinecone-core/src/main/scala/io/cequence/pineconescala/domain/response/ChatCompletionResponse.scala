package io.cequence.pineconescala.domain.response

import io.cequence.pineconescala.domain.response.Choice.{ChatCompletionMessage, FinishReason}

/**
 * @param id
 * @param model
 *   The name of the assistant returning the response.
 */
final case class ChatCompletionResponse(
  id: String,
  model: String,
  messages: Seq[Choice]
)

final case class Choice(
  finish_reason: FinishReason,
  index: Int,
  message: ChatCompletionMessage
)

object Choice {
  final case class ChatCompletionMessage(role: Role, content: String)

  sealed trait FinishReason
  object FinishReason {
    case object Stop extends FinishReason
    case object Length extends FinishReason
    case object ToolCalls extends FinishReason
    case object ContentFilter extends FinishReason
    case object FunctionCall extends FinishReason
  }

  sealed trait Role
  object Role {
    case object user extends Role
    case object assistant extends Role
  }
}
