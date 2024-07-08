package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.response.{Assistant, DeleteResponse}
import io.cequence.wsclient.service.CloseableService

import scala.concurrent.Future

trait PineconeAssistantService extends CloseableService with PineconeServiceConsts {

  /**
   * This operation returns a list of all assistants in a project.
   *
   * @return
   */
  def listAssistants(): Future[Seq[Assistant]]

  /**
   * This operation deploys a Pinecone Assistant. This is where you specify the underlying training model,
   * which cloud provider you would like to deploy with, and more.
   *
   * @param name The name of the assistant. Resource name must be 1-45 characters long, start and end with
   *             an alphanumeric character, and consist only of lower case alphanumeric characters or '-'.
   * @param metadata A dictionary containing metadata for the assistant.
   * @return
   */
  def createAssistant(name: String, metadata: Map[String, String]): Future[Assistant]

  /**
   * This operation describes an assistant and its metadata.
   *
   * @param name The name of the assistant to poll.
   * @return
   */
  def describeAssistant(name: String): Future[Option[Assistant]]

  /**
   * This operation deletes an existing assistant.
   *
   * @param name The name of the base to delete.
   * @return
   */
  def deleteAssistant(name: String): Future[DeleteResponse]

}
