package io.cequence.pineconescala.domain

object EmbeddingModelId {
  // 2048 input tokens
  val llama_text_embed_v2 = "llama-text-embed-v2"
  // 507 input tokens, dim 1024 (dense)
  val multilingual_e5_large = "multilingual-e5-large"
  // 512 input tokens
  val pinecone_sparse_english_v0 = "pinecone-sparse-english-v0"
}
