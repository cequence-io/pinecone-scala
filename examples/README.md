# Pinecone Scala Client - Demo/Seed Project
[![version](https://img.shields.io/badge/version-0.1.2-green.svg)](https://cequence.io) [![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](https://opensource.org/licenses/MIT) [![Twitter Follow](https://img.shields.io/twitter/follow/0xbnd?style=social)](https://twitter.com/0xbnd)

This is a ready-to-fork, example/demo project demonstrating how to use [Pinecone Scala Client](https://github.com/cequence-io/pinecone-scala).

7 single-function demos are available:

- [List Indexes](./src/main/scala/io/cequence/pineconescala/demo/ListIndexes.scala)
- [Create Index](./src/main/scala/io/cequence/pineconescala/demo/CreateIndex.scala)
- [Describe Index](./src/main/scala/io/cequence/pineconescala/demo/DescribeIndex.scala)
- [Delete Index](./src/main/scala/io/cequence/pineconescala/demo/DeleteIndex.scala)
- [Upsert Vectors](./src/main/scala/io/cequence/pineconescala/demo/UpsertVectors.scala)
- [Query Vectors](./src/main/scala/io/cequence/pineconescala/demo/QueryVectors.scala)
- [Query Vectors By Filter](./src/main/scala/io/cequence/pineconescala/demo/QueryVectorsByFilter.scala)


and two more complex (longer), one for each service:

- [Pinecone Index Long Demo](./src/main/scala/io/cequence/pineconescala/demo/PineconeIndexLongDemo.scala)
- [Pinecone Vector Long Demo](./src/main/scala/io/cequence/pineconescala/demo/PineconeVectorLongDemo.scala)

Before you launch anything, don't forget to set the following env. variables:
- `PINECONE_SCALA_CLIENT_API_KEY`
- `PINECONE_SCALA_CLIENT_ENV` (optional) - needed only for pod-based deployments

That's it folks. Have fun with our Scala client!

## License ⚖️

This library is available and published as open source under the terms of the [MIT License](https://opensource.org/licenses/MIT).

## Contributors 🙏

This project is open-source and welcomes any contribution or feedback ([here](https://github.com/cequence-io/pinecone-scala-demo/issues)).

Development of this library has been supported by  [<img src="https://cequence.io/favicon-16x16.png"> - Cequence.io](https://cequence.io) - `The future of contracting`

Created and maintained by [Peter Banda](https://peterbanda.net).