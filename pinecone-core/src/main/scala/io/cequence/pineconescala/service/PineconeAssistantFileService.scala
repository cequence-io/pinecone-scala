package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.response.{DeleteResponse, File}

import java.util.UUID
import scala.concurrent.Future

trait PineconeAssistantFileService {

  /**
   * This operation returns a list of all files in an assistant.
   *
   * @param assistantName The name of the assistant to get files of.
   * @return
   */
  def listFiles(assistantName: String): Future[Seq[File]]

  /**
   * This operation uploads a file to a specified assistant.
   *
   * @param name The name of the base to upload files to.
   * @return
   */
  def uploadFile(assistantName: String): Future[File]

  /**
   *
   * @param assistantName The name of the base to get file from.
   * @param fileId The UUID of the file to be described.
   * @return
   */
  def describeFile(assistantName: String, fileId: UUID): Future[Option[File]]

  /**
   *
   * @param assistantName
   * @param fileId The UUID of the file to be described.
   * @return
   */
  def deleteFile(assistantName: String, fileId: UUID): Future[DeleteResponse]

}
