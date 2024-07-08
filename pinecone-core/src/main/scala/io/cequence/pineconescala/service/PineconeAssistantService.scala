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

}
