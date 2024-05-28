package io.cequence.pineconescala.domain

import io.cequence.wsclient.domain.NamedEnumValue

sealed abstract class PodType(name: String) extends NamedEnumValue(name)

object PodType {
  case object s1_x1 extends PodType("s1.x1")
  case object s1_x2 extends PodType("s1.x2")
  case object s1_x4 extends PodType("s1.x4")
  case object s1_x8 extends PodType("s1.x8")

  case object p1_x1 extends PodType("p1.x1")
  case object p1_x2 extends PodType("p1.x2")
  case object p1_x4 extends PodType("p1.x4")
  case object p1_x8 extends PodType("p1.x8")

  case object p2_x1 extends PodType("p2.x1")
  case object p2_x2 extends PodType("p2.x2")
  case object p2_x4 extends PodType("p2.x4")
  case object p2_x8 extends PodType("p2.x8")
}
