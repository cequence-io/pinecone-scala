package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.response.{ChatCompletionResponse, DeleteResponse, FileResponse}

import java.io.File
import java.util.UUID
import scala.concurrent.Future

trait PineconeAssistantFileService {

  /**
   * This operation returns a list of all files in an assistant.
   *
   * @param assistantName The name of the assistant to get files of.
   * @return
   */
  def listFiles(assistantName: String): Future[Seq[FileResponse]]

  /**
   * This operation uploads a file to a specified assistant.
   *
   * @param file A file to upload.
   * @param displayFileName The name of the file to be displayed.
   * @param name The name of the assistant to upload file to.
   * @return
   */
  def uploadFile(file: File, displayFileName: Option[String], assistantName: String): Future[FileResponse]

  /**
   *
   * @param assistantName The name of the assistant to get file from.
   * @param fileId The UUID of the file to be described.
   * @return
   */
  def describeFile(assistantName: String, fileId: UUID): Future[Option[FileResponse]]

  /**
   *
   * @param assistantName
   * @param fileId The UUID of the file to be described.
   * @return
   */
  def deleteFile(assistantName: String, fileId: UUID): Future[DeleteResponse]


  /**
   * This operation queries the completions endpoint of a Pinecone Assistant.
   * For guidance and examples, see the chat with assistant guide.
   *
   * @param assistantName The name of the assistant to be described.
   * @param messages An array of objects that represent the messages in a conversation.
   * @return The ChatCompletionModel describes the response format of a chat request
   */
  def chatWithAssistant(assistantName: String, messages: Seq[String]): Future[ChatCompletionResponse]

}
