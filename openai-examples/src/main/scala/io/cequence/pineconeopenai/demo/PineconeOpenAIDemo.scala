package io.cequence.pineconeopenai.demo

import akka.stream.scaladsl.{Flow, Sink, Source}
import io.cequence.openaiscala.domain.ModelId
import io.cequence.openaiscala.domain.settings.CreateEmbeddingsSettings
import io.cequence.pineconescala.domain.PVector
import io.cequence.pineconescala.domain.response.CreateResponse
import io.cequence.pineconescala.domain.settings.QuerySettings
import io.cequence.pineconescala.service.PineconeVectorService

import scala.concurrent.Future
import play.api.libs.json.Json

/**
 * Run me!
 *
 * Based on <a href="https://docs.pinecone.io/docs/openai">Pinecone OpenAI Tutorial</a>.
 *
 * The following env. variables are expected:
 *  - PINECONE_SCALA_CLIENT_API_KEY
 *  - PINECONE_SCALA_CLIENT_ENV
 *  - OPENAI_SCALA_CLIENT_API_KEY
 *  - OPENAI_SCALA_CLIENT_ORG_ID (optional)
 *
 *  Note: If you can't create a new index in Pinecone because it exceeds your quota (free account can have only one index),
 *  you can run [[DeleteAllPineconeIndexes]] to start from a clean slate.
 */
object PineconeOpenAIDemo extends PineconeOpenAIDemoApp {

  private val indexName = "openai"
  private val namespace = "default"
  private val batchSize = 32                              // process everything in batches of 32
  private val parallelism = 1                             // no rush, do it in sequence
  private val indexSettings = DefaultSettings.CreateIndex // metric = cosine, pods = 1, podType = p1.x1

  override protected def exec = {
    for {
      embedResponse <- openAIService.createEmbeddings(
        input = Seq(
          "Sample document text goes here",
          "there will be several phrases in each batch"
        ),
        settings = CreateEmbeddingsSettings(ModelId.text_embedding_ada_002)
      )

      // extract embeddings to a list
      embeds = embedResponse.data.map(_.embedding)

      indexNames <- pineconeIndexService.listIndexes

      // check if 'openai' index already exists (only create index if not)
      _ <- if (!indexNames.contains(indexName)) {
        pineconeIndexService.createIndex(
          indexName,
          dimension = embeds(0).size,
          settings = indexSettings
        ).map(
          _ match {
            case CreateResponse.Created =>
              println(s"Index '${indexName}' successfully created.")
              println("Waiting 30 seconds for the index initialization to finish.")
              Thread.sleep(30000)

            case CreateResponse.BadRequest =>
              println(s"Index '${indexName}' creation failed. Request exceeds quota or an invalid index name.")

            case CreateResponse.AlreadyExists =>
              println(s"Index '${indexName}' with a given name already exists.")
          }
        )
      } else
        Future(())

      // create a service for a given index name
      pineconeVectorService <- createPineconeVectorService(indexName)

      // take the first 1000 entries and extract the text. the result is an iterator
      trecTextsIterator = scala.io.Source.fromFile("src/main/resources/trec-train.json")
        .getLines()
        .take(1000)
        .map(jsonString => (Json.parse(jsonString) \ "text").as[String].trim) // extract text

      // add ids to each text/line, chunk into batches of 32, and create a source
      textsWithIdsSource = Source.fromIterator(() => trecTextsIterator.zipWithIndex)
        .grouped(batchSize)
//        .buffer(2, OverflowStrategy.backpressure) // process in a sequence (no rush)

      // define an Akka-based processing flow
      processingFlow = Flow[Seq[(String, Int)]].mapAsyncUnordered(parallelism) { textsWithIds =>
        val texts = textsWithIds.map(_._1)

        println(s"Creating and upserting embeddings for batch: ${textsWithIds.head._2 / batchSize}")

        for {
          // create embeddings
          embedResponse <- openAIService.createEmbeddings(
            input = texts,
            settings = CreateEmbeddingsSettings(ModelId.text_embedding_ada_002)
          )

          embeds = embedResponse.data.map(_.embedding)

          // prep vectors with metadata
          vectors = embeds.zip(textsWithIds).map { case (embed, (text, id)) =>
            PVector(
              id = id.toString,
              values = embed,
              metadata = Map("text" -> text)
            )
          }

          // upsert the batch to Pinecone
          _ <- pineconeVectorService.upsert(
            vectors = vectors,
            namespace = namespace
          )
        } yield
          ()
      }

      // execute the source with the processing flow
      _ <- textsWithIdsSource.via(processingFlow).runWith(Sink.ignore)

      //////////////
      // Querying //
      //////////////

      // first query
      _ <- execQuery(
        "What caused the 1929 Great Depression?",
        pineconeVectorService
      )

      // second query
      _ <- execQuery(
        "What was the cause of the major recession in the early 20th century?",
        pineconeVectorService
      )

      // third query
      _ <- execQuery(
        "Why was there a long-term economic downturn in the early 20th century?",
        pineconeVectorService
      )
    } yield
      ()
  }

  private def execQuery(
    query: String,
    pineconeVectorService: PineconeVectorService
  ) = {
    for {
      // create embeddings for the query (only one in this case)
      embed <- openAIService.createEmbeddings(
        input = Seq(query),
        settings = CreateEmbeddingsSettings(ModelId.text_embedding_ada_002)
      ).map(_.data(0).embedding)

      // query the index
      queryResponse <- pineconeVectorService.query(
        vector = embed,
        namespace = namespace,
          settings = QuerySettings(
            topK = 5,
            includeValues = false,
            includeMetadata = true
          )
        )

      // let's print out the top_k most similar questions and their respective similarity scores.
      _ = {
          println(s"\nQuery: ${query}\n")
          queryResponse.matches.foreach { match_ =>
            println(s"${"%1.2f".format(match_.score)}: ${match_.metadata.get("text")}")
          }
        }
    } yield
      ()
  }
}
