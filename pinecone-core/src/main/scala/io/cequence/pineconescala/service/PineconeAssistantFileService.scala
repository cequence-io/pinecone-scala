package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.response.{DeleteResponse, FileResponse}

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

}
