name := "pinecone-scala-client"

description := "Scala client for Pinecone API implemented using Play WS lib."

val playWsVersion = "2.1.10" // "2.2.0-M2"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ahc-ws-standalone" % playWsVersion,
  "com.typesafe.play" %% "play-ws-standalone-json" % playWsVersion
)

// we need this for Scala 2.13
//dependencyOverrides ++= Seq(
//  "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2"
//)
