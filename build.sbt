import sbt.Keys.test

ThisBuild / organization := "io.cequence"
ThisBuild / scalaVersion := "2.12.15" // 2.13.10"
ThisBuild / version := "0.0.1"
ThisBuild / isSnapshot := false

lazy val core = (project in file("pinecone-core"))

lazy val client = (project in file("pinecone-client"))
  .dependsOn(core)
  .aggregate(core)

// POM settings for Sonatype
ThisBuild / homepage := Some(url("https://github.com/cequence-io/pinecone-scala"))

ThisBuild / sonatypeProfileName := "io.cequence"

ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/cequence-io/pinecone-scala"), "scm:git@github.com:cequence-io/pinecone-scala.git"))

ThisBuild / developers := List(
  Developer("bnd", "Peter Banda", "peter.banda@protonmail.com", url("https://peterbanda.net"))
)

ThisBuild / licenses += "MIT" -> url("https://opensource.org/licenses/MIT")

ThisBuild / publishMavenStyle := true

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

ThisBuild / publishTo := sonatypePublishToBundle.value
