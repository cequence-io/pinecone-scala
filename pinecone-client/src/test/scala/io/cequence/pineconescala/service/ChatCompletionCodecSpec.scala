package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.response.Choice.{ChatCompletionMessage, FinishReason}
import io.cequence.pineconescala.domain.response.{ChatCompletionResponse, Choice}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.Json

class ChatCompletionCodecSpec extends AnyFlatSpec with Matchers {

  import io.cequence.pineconescala.JsonFormats._

  val chatCompletionResponseJson = """
    |{
    |  "id" : "00000000000000006f3994cbaacfe3fa",
    |  "choices" : [
    |    {
    |      "finish_reason" : "stop",
    |      "index" : 0,
    |      "message" : {
    |        "role" : "assistant",
    |        "content" : "Some response from the assistant"
    |      }
    |   }
    |  ],
    |  "model" : "test-assistant"
    |}
    |""".stripMargin

  "decoder for chat completions" should "decode a chat completion response" in {
    val chatCompletionChoices = ChatCompletionResponse(
      id = "00000000000000006f3994cbaacfe3fa",
      model = "test-assistant",
      choices = Seq(
        Choice(
          FinishReason.Stop,
          index = 0,
          message = ChatCompletionMessage(role = Choice.Role.assistant, content = "Some response from the assistant")
        )
      )
    )

    Json.parse(chatCompletionResponseJson).as[ChatCompletionResponse] shouldBe chatCompletionChoices
  }

}
