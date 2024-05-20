package io.cequence.pineconescala.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.pineconescala.domain.response.{FetchResponse, QueryResponse}
import io.cequence.pineconescala.domain.settings.QuerySettings
import org.scalatest.matchers.must.Matchers.contain
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.{Assertion, GivenWhenThen}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class PineconeIndexServiceImplSpec
    extends AsyncWordSpec
    with GivenWhenThen
    with TextFixtures {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val materializer: Materializer = Materializer(ActorSystem())

  def pineconeIndexService: PineconeIndexService =
    PineconeIndexServiceFactory(indexName)

  "Pinecone Index Service" when {

    "listIndexes lists all available indexes" in {
      for {
        indexes <- pineconeIndexService.listIndexes
      } yield {
        indexes should contain("auto-gpt-test")
      }
    }

  }
}
