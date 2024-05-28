package io.cequence.pineconescala.domain.settings

import io.cequence.wsclient.domain.NamedEnumValue

sealed abstract class Region(name: String) extends NamedEnumValue(name)
object Region {
  case object USEast1 extends Region("us-east-1")
  case object USWest2 extends Region("us-west-2")
  case object EUWest1 extends Region("eu-west-1")
  case class Other(name: String) extends Region(name)

  def fromString(value: String): Option[Region] = value match {
    case "us-east-1" => Some(USEast1)
    case "us-west-2" => Some(USWest2)
    case "eu-west-1" => Some(EUWest1)
    case other       => Some(Other(other))
  }
}
