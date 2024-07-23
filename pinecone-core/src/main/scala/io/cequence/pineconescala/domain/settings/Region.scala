package io.cequence.pineconescala.domain.settings

import io.cequence.wsclient.domain.NamedEnumValue

sealed abstract class Region(name: String) extends NamedEnumValue(name)
object Region {
  case object USEast1 extends Region("us-east-1") // Virginia
  case object USWest2 extends Region("us-west-2") // Oregon
  case object EUWest1 extends Region("eu-west-1") // Ireland
  case object USCentral1 extends Region("us-central1") // Iowa
  case object EeastUS2 extends Region("eastus2") // Virginia
  case class Other(name: String) extends Region(name)

  private val knowRegions = Seq(
    USEast1,
    USWest2,
    EUWest1,
    USCentral1,
    EeastUS2
  )

  private val knowRegionMap = knowRegions.map(r => r.toString() -> r).toMap

  def fromString(value: String): Region =
    knowRegionMap.getOrElse(value, Other(value))
}
