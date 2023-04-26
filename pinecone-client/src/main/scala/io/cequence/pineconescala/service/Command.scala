package  io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.EnumValue

sealed abstract class Command(value: String = "") extends EnumValue(value)

object Command {
  case object describe_index_stats extends Command
  case object query extends Command
  case object vectors_delete extends Command("vectors/delete")
  case object vectors_fetch extends Command("vectors/fetch")
  case object vectors_update extends Command("vectors/update")
  case object vectors_upsert extends Command("vectors/upsert")
  case object collections extends Command
  case object databases extends Command
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
  case object queries extends Tag
  case object ids extends Tag
  case object deleteAll extends Tag
  case object sparseValues extends Tag
  case object setMetadata extends Tag
  case object values extends Tag
  case object vectors extends Tag
  case object name extends Tag
  case object source extends Tag
  case object collectionName extends Tag
  case object dimension extends Tag
  case object metric extends Tag
  case object pods extends Tag
  case object replicas extends Tag
  case object pod_type extends Tag
  case object metadata_config extends Tag
  case object source_collection extends Tag
  case object index_type extends Tag
  case object index_config extends Tag
  case object indexName extends Tag
}
