package io.cequence.pineconescala.service

import com.typesafe.config.Config
import io.cequence.wsclient.service.ws.Timeouts

import scala.concurrent.ExecutionContext

trait SimplePineconeServiceFactory[T] extends PineconeServiceFactoryHelper {

  def apply(
  )(
    implicit ec: ExecutionContext
  ): T =
    apply(loadConfig())

  def apply(
    config: Config
  )(
    implicit ec: ExecutionContext
  ): T = {
    val timeouts = loadTimeouts(config)

    apply(
      apiKey = config.getString(s"$configPrefix.apiKey"),
      timeouts = timeouts.toOption
    )
  }

  def apply(
    apiKey: String,
    timeouts: Option[Timeouts] = None
  )(
    implicit ec: ExecutionContext
  ): T
}
