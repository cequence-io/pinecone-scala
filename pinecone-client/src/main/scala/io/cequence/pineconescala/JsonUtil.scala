package io.cequence.pineconescala

import play.api.libs.json._

@deprecated
object JsonUtil {

  // TODO: replace with JsonUtil.enumFormat (in wsclient)
  private class EnumFormat[E](values: Iterable[E]) extends Format[E] {

    private val stringValueMap = values.map(v => (v.toString, v)).toMap

    def reads(json: JsValue): JsResult[E] = json match {
      case JsString(s) =>
        stringValueMap.get(s).map(
          JsSuccess(_)
        ).getOrElse(
          JsError(s"Enumeration values do not contain a string: '$s'")
        )

      case _ => JsError("String value expected")
    }

    def writes(v: E): JsValue = JsString(v.toString)
  }

  object EnumFormat {
    def apply[E](values: Iterable[E]): Format[E] = new EnumFormat[E](values: Iterable[E])
  }
}