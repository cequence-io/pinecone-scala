package io.cequence.pineconescala.service

import com.typesafe.config.Config
import io.cequence.wsclient.ConfigImplicits._
import io.cequence.pineconescala.domain.IndexEnv.PodEnv
import io.cequence.wsclient.service.ws.Timeouts

trait PineconeServiceFactoryHelper extends PineconeServiceConsts {

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

  def loadPodEnv(config: Config): Option[PodEnv] =
    config.optionalString(s"$configPrefix.environment").map(PodEnv)

}
