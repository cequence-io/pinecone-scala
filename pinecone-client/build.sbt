import Dependencies.Versions._

name := "pinecone-scala-client"

libraryDependencies ++= Seq(
  "io.cequence" %% "ws-client-core" % wsClient,
  // no HTTP engine is enforced - the user adds a ws-client backend (ws-client-jdk,
  // ws-client-play-akka, ws-client-play-pekko, ws-client-pekko-http, ws-client-zio-http, ...);
  // the JDK one (java.net.http) is used only to run the tests
  "io.cequence" %% "ws-client-jdk" % wsClient % Test,

  // 3.2.16: the last ScalaTest built with Scala 3.2 (newer ones break the 3.2.2 cross-build)
  "org.scalatest" %% "scalatest" % "3.2.16" % Test,
  "org.scalatestplus" %% "mockito-4-11" % "3.2.16.0" % Test
)
