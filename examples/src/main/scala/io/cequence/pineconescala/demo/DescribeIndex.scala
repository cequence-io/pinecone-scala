package io.cequence.pineconescala.demo

// run me - env. variables PINECONE_SCALA_CLIENT_API_KEY and PINECONE_SCALA_CLIENT_ENV must be set
object DescribeIndex extends PineconeDemoApp {
  override protected def exec =
    pineconeIndexService.describeIndex("auto-gpt-test").map(
      _.foreach(println)
    )
}