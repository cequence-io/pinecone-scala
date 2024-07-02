import sbt.Keys.test

// Supported versions
val scala212 = "2.12.19"
val scala213 = "2.13.10"
val scala32 = "3.2.2"
val scala33 = "3.3.1"

ThisBuild / organization := "io.cequence"
ThisBuild / scalaVersion := scala212
ThisBuild / version := "0.1.3"
ThisBuild / isSnapshot := false

lazy val core = (project in file("pinecone-core"))

lazy val client = (project in file("pinecone-client")).dependsOn(core).aggregate(core)

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
  )
)

ThisBuild / licenses += "MIT" -> url("https://opensource.org/licenses/MIT")

ThisBuild / publishMavenStyle := true

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

ThisBuild / publishTo := sonatypePublishToBundle.value
