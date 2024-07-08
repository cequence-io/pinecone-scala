package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.response.Assistant
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
   * @param name
   * @param metadata
   * @return
   */
  def createAssistant(name: String, metadata: Map[String, String]): Future[Assistant]

}
