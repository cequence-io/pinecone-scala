package io.cequence.pineconescala.domain

abstract class EnumValue(value: String = "") {
  override def toString = if (value.nonEmpty) value else getClass.getSimpleName.stripSuffix("$")
}
