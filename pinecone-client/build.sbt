name := "pinecone-scala-client"

description := "Scala client for Pinecone API implemented with Play WS lib."

lazy val playWsVersion = settingKey[String]("Play WS version to use")

playWsVersion := {
  scalaVersion.value match {
    case "2.12.15" => "2.1.10"
    case "2.13.10" => "2.2.0-M3"
    case "3.2.2" => "2.2.0-M2" // Version "2.2.0-M3" was produced by an unstable release: Scala 3.3.0-RC3
    case _ => "2.1.10"
  }
}

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ahc-ws-standalone" % playWsVersion.value,
  "com.typesafe.play" %% "play-ws-standalone-json" % playWsVersion.value
)