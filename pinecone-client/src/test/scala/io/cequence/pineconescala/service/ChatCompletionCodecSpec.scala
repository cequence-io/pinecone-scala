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
    |  "choices" : [ {
    |    "finish_reason" : "stop",
    |    "index" : 0,
    |    "message" : {
    |      "role" : "assistant",
    |    }
    |      "content" : "Based on the provided search results, there is no information available regarding the maximum height of a red pine. The search result only contains a sentence about a fox and a dog, which is unrelated to the question about red pines  [1].\n\nReferences:\n1. [pinecone-assistant.txt](https://storage.googleapis.com/knowledge-prod-files/549946b9-cc33-45e2-97bd-bf9e5d9ba17b%2Fdefa42cb-7409-4ecc-89b9-367045b4e6e8%2F60a9e195-1025-427b-8520-5f834b5614fc.txt?X-Goog-Algorithm=GOOG4-RSA-SHA256&X-Goog-Credential=ke-prod-1%40pc-knowledge-prod.iam.gserviceaccount.com%2F20240713%2Fauto%2Fstorage%2Fgoog4_request&X-Goog-Date=20240713T131700Z&X-Goog-Expires=3600&X-Goog-SignedHeaders=host&response-content-disposition=inline&response-content-type=text%2Fplain&X-Goog-Signature=3205693609e40c6389efe27d111ca7cae3a327e3344152fd4e96d1e70d11bdc328b4d4add6af4a67517595474ccaee9b5a555f0be6b8583043ddff8961902ff894c7fa6a54c74471d3b55e5c19680f3fdc71ebd62721c9a7fff08eeeb1491a733094ee3642adf90537ce37c7664a9c84559342ade3d9e59e1b3fb6a7e7b740308d439da5f7f0715e877a3f75075bef77aa0106ad708479105fc6f0530474a50cf0aaa8c661c7672513d40a2b015076a200dd08ce9383a1d8de21cfa144debe8af1f720f10cdbc8c177345c9e35fec8a715850832ddcde235d90f628fe9fb08ac5b0da1fea201be9058631eec46e089c0e9652ca85584c3d63248c5daf5d08780) \n"
    |  } ],
    |  "model" : "testAssistant"
    |""".stripMargin

  "decoder for chat completions" should "decode a chat completion response" in {
    val chatCompletionChoices = ChatCompletionResponse(
      id = "00000000000000006f3994cbaacfe3fa",
      model = "test-assistant",
      choices = Seq(
        Choice(
          FinishReason.Stop,
          index = 0,
          message = ChatCompletionMessage(role = Choice.Role.assistant, content = "Based on the provided search results, there is no information available regarding the maximum height of a red pine. The search result only contains a sentence about a fox and a dog, which is unrelated to the question about red pines  [1].\n\nReferences:\n1. [pinecone-assistant.txt](https://storage.googleapis.com/knowledge-prod-files/549946b9-cc33-45e2-97bd-bf9e5d9ba17b%2Fdefa42cb-7409-4ecc-89b9-367045b4e6e8%2F60a9e195-1025-427b-8520-5f834b5614fc.txt?X-Goog-Algorithm=GOOG4-RSA-SHA256&X-Goog-Credential=ke-prod-1%40pc-knowledge-prod.iam.gserviceaccount.com%2F20240713%2Fauto%2Fstorage%2Fgoog4_request&X-Goog-Date=20240713T131700Z&X-Goog-Expires=3600&X-Goog-SignedHeaders=host&response-content-disposition=inline&response-content-type=text%2Fplain&X-Goog-Signature=3205693609e40c6389efe27d111ca7cae3a327e3344152fd4e96d1e70d11bdc328b4d4add6af4a67517595474ccaee9b5a555f0be6b8583043ddff8961902ff894c7fa6a54c74471d3b55e5c19680f3fdc71ebd62721c9a7fff08eeeb1491a733094ee3642adf90537ce37c7664a9c84559342ade3d9e59e1b3fb6a7e7b740308d439da5f7f0715e877a3f75075bef77aa0106ad708479105fc6f0530474a50cf0aaa8c661c7672513d40a2b015076a200dd08ce9383a1d8de21cfa144debe8af1f720f10cdbc8c177345c9e35fec8a715850832ddcde235d90f628fe9fb08ac5b0da1fea201be9058631eec46e089c0e9652ca85584c3d63248c5daf5d08780) \n")
        )
      )
    )

    Json.parse(chatCompletionResponseJson).as[ChatCompletionResponse] shouldBe chatCompletionChoices
  }

}
