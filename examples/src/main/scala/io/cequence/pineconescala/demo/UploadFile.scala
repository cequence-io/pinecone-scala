package io.cequence.pineconescala.demo

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object UploadFile extends PineconeDemoApp {

  override protected def exec = {
    val assistantName = s"assistant-${System.currentTimeMillis()}"

    val file = createAssistantFile()

    for {
      _ <- pineconeAssistantService.createAssistant(assistantName)
      _ = waitForAssistantToInitialize()
      fileResponse <- pineconeAssistantFileService.uploadFile(assistantName, file)
    } yield println(fileResponse)
  }

  private def waitForAssistantToInitialize(): Unit = Thread.sleep(5000)

  private def createAssistantFile(): File = {
    val path = "pinecone-assistant.txt"
    Files.write(
      Paths.get(path),
      "The quick brown fox jumps over the lazy dog.".getBytes(StandardCharsets.UTF_8)
    )
    val inputFile = Paths.get(path).toFile
    inputFile
  }

}
