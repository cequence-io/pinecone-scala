package io.cequence.pineconescala.service

import io.cequence.pineconescala.domain.settings.IndexSettings.{CreatePodBasedIndexSettings, CreateServerlessIndexSettings}
import io.cequence.wsclient.domain.NamedEnumValue

sealed abstract class EndPoint(value: String = "") extends NamedEnumValue(value)

object EndPoint {
  case object describe_index_stats extends EndPoint
  case object embed extends EndPoint
  case object query extends EndPoint
  case object vectors_delete extends EndPoint("vectors/delete")
  case object vectors_fetch extends EndPoint("vectors/fetch")
  case object vectors_list extends EndPoint("vectors/list")
  case object vectors_update extends EndPoint("vectors/update")
  case object vectors_upsert extends EndPoint("vectors/upsert")
  case object collections extends EndPoint
  case object databases extends EndPoint
  case object indexes extends EndPoint
}

// TODO: rename to Param
sealed abstract class Tag(value: String = "") extends NamedEnumValue(value)

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
  case object limit extends Tag
  case object paginationToken extends Tag
  case object prefix extends Tag
  case object cloud extends Tag
  case object region extends Tag
  case object spec extends Tag
  case object shards extends Tag
  case object inputs extends Tag
  case object model extends Tag
  case object parameters extends Tag

  // TODO: move elsewhere
  def fromCreatePodBasedIndexSettings(
    name: String,
    dimension: Int,
    settings: CreatePodBasedIndexSettings
  ): Seq[(Tag, Option[Any])] = {
    Seq(
      Tag.name -> Some(name),
      Tag.dimension -> Some(dimension),
      Tag.metric -> Some(settings.metric.toString),
      Tag.spec -> Some(
        Map(
          "pod" -> Map(
            Tag.pods.toString -> Some(settings.pods),
            Tag.replicas.toString -> Some(settings.replicas),
            Tag.pod_type.toString -> Some(settings.podType.toString),
            Tag.shards.toString -> Some(settings.shards),
            Tag.metadata_config.toString ->
              (if (settings.metadataConfig.nonEmpty) Some(settings.metadataConfig) else None),
            Tag.source_collection.toString -> settings.sourceCollection
          )
        )
      )
    )
  }

  def fromCreateServerlessIndexSettings(
    name: String,
    dimension: Int,
    settings: CreateServerlessIndexSettings
  ): Seq[(Tag, Option[Any])] = {
    Seq(
      Tag.name -> Some(name),
      Tag.dimension -> Some(dimension),
      Tag.metric -> Some(settings.metric.toString),
      Tag.spec -> Some(
        Map(
          "serverless" -> Map(
            Tag.cloud.toString -> settings.cloud.toString,
            Tag.region.toString -> settings.region.toString
          )
        )
      )
    )
  }
}
