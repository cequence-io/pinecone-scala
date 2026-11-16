import Dependencies.Versions._

name := "pinecone-scala-client-default"

description := "Pinecone Scala client with the default HTTP engine (JDK java.net.http)"

// no code - the client plus a default ws-client backend; to pick another backend depend on
// pinecone-scala-client and add that one instead
libraryDependencies += "io.cequence" %% "ws-client-jdk" % wsClient
