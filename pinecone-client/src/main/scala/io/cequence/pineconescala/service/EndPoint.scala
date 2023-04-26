package  io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.EnumValue

sealed abstract class EndPoint(value: String = "") extends EnumValue(value)

object EndPoint {
  case object describe_index_stats extends EndPoint
  case object query extends EndPoint
  case object vectors_delete extends EndPoint("vectors/delete")
  case object vectors_fetch extends EndPoint("vectors/fetch")
  case object vectors_update extends EndPoint("vectors/update")
  case object vectors_upsert extends EndPoint("vectors/upsert")
  case object collections extends EndPoint
  case object databases extends EndPoint
}

sealed abstract class Tag(value: String = "") extends EnumValue(value)

object Tag {
  case object filter extends Tag
  case object namespace extends Tag
  case object topK extends Tag
  case object includeValues extends Tag
  case object includeMetadata extends Tag
  case object vector extends Tag
  case object sparseVector extends Tag
  case object id extends Tag
  case object ids extends Tag
  case object deleteAll extends Tag
  case object sparseValues extends Tag
  case object setMetadata extends Tag
  case object values extends Tag
  case object vectors extends Tag
  case object name extends Tag
  case object source extends Tag
  case object dimension extends Tag
  case object metric extends Tag
  case object pods extends Tag
  case object replicas extends Tag
  case object pod_type extends Tag
  case object metadata_config extends Tag
  case object source_collection extends Tag
  case object indexName extends Tag
}
