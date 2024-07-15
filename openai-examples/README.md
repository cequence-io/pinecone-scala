# Pinecone + OpenAI Scala Client - Demo/Seed Project
[![version](https://img.shields.io/badge/version-0.1.0-green.svg)](https://cequence.io) [![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](https://opensource.org/licenses/MIT) [![Twitter Follow](https://img.shields.io/twitter/follow/0xbnd?style=social)](https://twitter.com/0xbnd)

This is a ready-to-fork, example/demo project demonstrating how to use [Pinecone](https://pinecone.io) vector database with [OpenAI](https://openai.com) embeddings in Scala using [Pinecone Scala Client](https://github.com/cequence-io/pinecone-scala) and [OpenAI Scala Client](https://github.com/cequence-io/openai-scala-client).

The demo app can be found in [PineconeOpenAIDemo](./src/main/scala/io/cequence/pineconeopenai/demo/PineconeOpenAIDemo.scala).
The following env. variables are expected:
- `PINECONE_SCALA_CLIENT_API_KEY`
- `PINECONE_SCALA_CLIENT_ENV`
- `OPENAI_SCALA_CLIENT_API_KEY`
- `OPENAI_SCALA_CLIENT_ORG_ID` (optional)

**✔️ Important**: The demo uses data from [Trec dataset](https://cogcomp.seas.upenn.edu/Data/QA/QC/) created by Cognitive Computation Group at University of Pennsylvania, which we hereby acknowledge. Its training part is included in this project at [trec-train.json](./src/main/resources/trec-train.json) (downloaded using [this script](./src/main/resources/trec-dump.py)). 

## License ⚖️

This library is available and published as open source under the terms of the [MIT License](https://opensource.org/licenses/MIT).

## Contributors 🙏

This project is open-source and welcomes any contribution or feedback ([here](https://github.com/cequence-io/pinecone-openai-scala-demo/issues)).

Development of this library has been supported by  [<img src="https://cequence.io/favicon-16x16.png"> - Cequence.io](https://cequence.io) - `The future of contracting`

Created and maintained by [Peter Banda](https://peterbanda.net).
