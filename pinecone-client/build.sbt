import Dependencies.Versions._

name := "pinecone-scala-client"

description := "Scala client for Pinecone API implemented with Play WS lib."

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.18"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test
libraryDependencies += "org.scalatestplus" %% "mockito-4-11" % "3.2.18.0" % Test

libraryDependencies += "io.cequence" %% "ws-client-core" % wsClient
libraryDependencies += "io.cequence" %% "ws-client-play" % wsClient

lazy val playWsVersion = settingKey[String]("Play WS version")
inThisBuild(
  playWsVersion := {
    scalaVersion.value match {
      case "2.12.18" => "2.1.10"
      case "2.13.11" => "2.2.0-M3"
      case "3.2.2" =>
        "2.2.0-M2" // Version "2.2.0-M3" was produced by an unstable release: Scala 3.3.0-RC3
      case _ => "2.1.10"
    }
  }
)

libraryDependencies += "com.typesafe.play" %% "play-ws-standalone-json" % playWsVersion.value
