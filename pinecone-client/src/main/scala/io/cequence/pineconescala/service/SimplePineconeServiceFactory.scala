package io.cequence.pineconescala.service

import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import io.cequence.wsclient.service.ws.Timeouts

import scala.concurrent.ExecutionContext

trait SimplePineconeServiceFactory[T] extends PineconeServiceFactoryHelper {

  def apply(
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): T =
    apply(ConfigFactory.load(configFileName))

  def apply(
    config: Config
  )(
    implicit ec: ExecutionContext,
    materializer: Materializer
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
    implicit ec: ExecutionContext,
    materializer: Materializer
  ): T
}
