package io.cequence.pineconescala.domain

import io.cequence.pineconescala.domain.settings.{CloudProvider, Region}

sealed trait IndexEnv

object IndexEnv {
  case class PodEnv(environment: String) extends IndexEnv
  case class ServerlessEnv(
    cloud: CloudProvider,
    region: Region
  ) extends IndexEnv
}
