package io.cequence.pineconescala.domain.settings

import io.cequence.wsclient.domain.NamedEnumValue

sealed abstract class CloudProvider(name: String) extends NamedEnumValue(name)

object CloudProvider {
  case object AWS extends CloudProvider("aws")
  case object GCP extends CloudProvider("gcp")
  case object Azure extends CloudProvider("azure")

  private val values = Seq(AWS, GCP, Azure)
  private val providerMap = values.map(r => r.toString() -> r).toMap

  def fromString(value: String): Option[CloudProvider] =
    providerMap.get(value)
}
