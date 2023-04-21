package  io.cequence.pineconescala.service

import com.typesafe.config.Config
import io.cequence.pineconescala.service.ws.Timeouts
import io.cequence.pineconescala.ConfigImplicits._

trait PineconeServiceFactoryHelper extends PineconeServiceConsts {

  protected def loadTimeouts(
    config: Config
  ) = {
    def intTimeoutAux(fieldName: String) =
      config.optionalInt(s"$configPrefix.timeouts.${fieldName}Sec").map(_ * 1000)

    Timeouts(
      requestTimeout = intTimeoutAux("requestTimeout"),
      readTimeout = intTimeoutAux("readTimeout"),
      connectTimeout = intTimeoutAux("connectTimeout"),
      pooledConnectionIdleTimeout = intTimeoutAux("pooledConnectionIdleTimeout")
    )
  }

  protected def timeoutsToOption(timeouts: Timeouts) =
    if (timeouts.requestTimeout.isDefined
      || timeouts.readTimeout.isDefined
      || timeouts.connectTimeout.isDefined
      || timeouts.pooledConnectionIdleTimeout.isDefined
    )
      Some(timeouts)
    else
      None
}