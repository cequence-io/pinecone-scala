import sbt.Keys.test

// Supported versions
val scala212 = "2.12.18"
val scala213 = "2.13.18"
val scala32 = "3.2.2"
val scala33 = "3.3.1"

ThisBuild / organization := "io.cequence"
ThisBuild / scalaVersion := scala213
// isSnapshot follows the version (a -SNAPSHOT suffix)
ThisBuild / version := "2.0.0-SNAPSHOT"

// the published modules cross-build (`sbt ++2.12.18 client/test`, `+clientDefault/publishSigned`)
val crossBuilt = Seq(crossScalaVersions := Seq(scala212, scala213, scala32))

lazy val core = (project in file("pinecone-core")).settings(crossBuilt)

// no HTTP engine enforced - the user adds a ws-client backend
lazy val client =
  (project in file("pinecone-client")).settings(crossBuilt).dependsOn(core).aggregate(core)

// client + the default HTTP engine (JDK)
lazy val clientDefault = (project in file("pinecone-client-default"))
  .settings(crossBuilt)
  .dependsOn(client)
  .aggregate(client)

lazy val examples = (project in file("examples"))
  .dependsOn(clientDefault)
  .aggregate(clientDefault)

lazy val openAiExamples = (project in file("openai-examples"))
  .dependsOn(clientDefault)
  .aggregate(clientDefault)

// POM settings for Sonatype
ThisBuild / homepage := Some(url("https://github.com/cequence-io/pinecone-scala"))

ThisBuild / sonatypeProfileName := "io.cequence"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/cequence-io/pinecone-scala"),
    "scm:git@github.com:cequence-io/pinecone-scala.git"
  )
)

ThisBuild / developers := List(
  Developer(
    "bnd",
    "Peter Banda",
    "peter.banda@protonmail.com",
    url("https://peterbanda.net")
  ),
  Developer(
    "bburdiliak",
    "Boris Burdiliak",
    "boris.burdiliak@cequence.io",
    url("https://cequence.io")
  ),
  Developer(
    "branislav-burdiliak",
    "Branislav Burdiliak",
    "brano.burdiliak@cequence.io",
    url("https://cequence.io")
  )
)

ThisBuild / licenses += "MIT" -> url("https://opensource.org/licenses/MIT")

ThisBuild / publishMavenStyle := true

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

ThisBuild / publishTo := sonatypePublishToBundle.value

inThisBuild(
  List(
    scalacOptions += "-Ywarn-unused",
  )
)
