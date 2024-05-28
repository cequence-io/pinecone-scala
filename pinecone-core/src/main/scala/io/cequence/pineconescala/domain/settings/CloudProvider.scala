package io.cequence.pineconescala.domain.settings

import io.cequence.wsclient.domain.NamedEnumValue

sealed abstract class CloudProvider(name: String) extends NamedEnumValue(name)
object CloudProvider {
  case object AWS extends CloudProvider("aws")
  case object GCP extends CloudProvider("gcp")
  case object Azure extends CloudProvider("azure")

  def fromString(value: String): Option[CloudProvider] = value match {
    case "aws"   => Some(AWS)
    case "gcp"   => Some(GCP)
    case "azure" => Some(Azure)
    case _       => None
  }
}
