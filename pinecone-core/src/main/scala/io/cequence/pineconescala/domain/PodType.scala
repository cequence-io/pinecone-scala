package io.cequence.pineconescala.domain

import io.cequence.wsclient.domain.EnumValue

sealed trait PodType extends EnumValue

object PodType {
  case object `s1.x1` extends PodType
  case object `s1.x2` extends PodType
  case object `s1.x4` extends PodType
  case object `s1.x8` extends PodType

  case object `p1.x1` extends PodType
  case object `p1.x2` extends PodType
  case object `p1.x4` extends PodType
  case object `p1.x8` extends PodType

  case object `p2.x1` extends PodType
  case object `p2.x2` extends PodType
  case object `p2.x4` extends PodType
  case object `p2.x8` extends PodType

}
