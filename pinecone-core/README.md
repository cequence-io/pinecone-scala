# Pinecone Scala - Core [![version](https://img.shields.io/badge/version-0.1.0-green.svg)](https://cequence.io) [![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](https://opensource.org/licenses/MIT)

This is the core module, which contains mostly domain classes and the definition of services: [PineconeVectorService](./src/main/scala/io/cequence/pineconescala/service/PineconeVectorService.scala) and [PineconeIndexService](./src/main/scala/io/cequence/pineconescala/service/PineconeIndexService.scala).
Note that the full project documentation can be found [here](../README.md).

## Installation 🚀

The currently supported Scala versions are **2.12, 2.13**, and **3**.

To pull the library you have to add the following dependency to your *build.sbt*

```
"io.cequence" %% "pinecone-scala-core" % "0.1.0"
```

or to *pom.xml* (if you use maven)

```
<dependency>
    <groupId>io.cequence</groupId>
    <artifactId>pinecone-scala-core_2.12</artifactId>
    <version>0.1.0</version>
</dependency>
```
