package io.cequence.pineconescala.service

import com.typesafe.config.{Config, ConfigFactory}
import io.cequence.wsclient.ClassLoaders
import io.cequence.wsclient.ConfigImplicits._
import io.cequence.wsclient.service.ws.Timeouts

trait PineconeServiceFactoryHelper extends PineconeServiceConsts {

  /**
   * The client config (`pinecone-scala-client.conf`), read through the thread's context class
   * loader when that loader sees this library, else through the library's own class loader
   * (ws-client's `ClassLoaders.contextOr`). A pool thread of an in-process sbt `run` / `test`
   * carries a loader that sees neither the library nor its config file, and a thread may have
   * none at all.
   */
  protected def loadConfig(): Config =
    ConfigFactory.load(
      ClassLoaders.contextOr(classOf[PineconeServiceFactoryHelper]),
      configFileName
    )

  protected def loadTimeouts(
    config: Config
  ): Timeouts = {
    def intTimeoutAux(fieldName: String) =
      config.optionalInt(s"$configPrefix.timeouts.${fieldName}Sec").map(_ * 1000)

    Timeouts(
      requestTimeout = intTimeoutAux("requestTimeout"),
      readTimeout = intTimeoutAux("readTimeout"),
      connectTimeout = intTimeoutAux("connectTimeout"),
      pooledConnectionIdleTimeout = intTimeoutAux("pooledConnectionIdleTimeout")
    )
  }

  def loadPodEnv(config: Config): Option[String] =
    config.optionalString(s"$configPrefix.environment")

}
