package io.cequence.pineconescala.domain

sealed trait IndexEnv

// TODO: do we need this?
object IndexEnv {
  case class PodEnv(environment: String) extends IndexEnv
}
