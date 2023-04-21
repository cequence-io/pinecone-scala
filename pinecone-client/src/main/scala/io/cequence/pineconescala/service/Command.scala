package  io.cequence.pineconescala.service

object Command extends Enumeration {
  val describe_index_stats = Value
  val query = Value
  val vectors_delete = Value("vectors/delete")
  val vectors_fetch = Value("vectors/fetch")
  val vectors_update = Value("vectors/update")
  val vectors_upsert = Value("vectors/upsert")
  val collections = Value
  val databases = Value
}

object Tag extends Enumeration {
  val filter = Value
  val namespace = Value
  val topK = Value 
  val includeValues = Value
  val includeMetadata = Value 
  val vector = Value 
  val sparseVector = Value 
  val id = Value
  val queries = Value
  val ids = Value
  val deleteAll = Value
  val sparseValues = Value
  val setMetadata = Value
  val values_ = Value("values") // values is a keyword in Scala Enum
  val vectors = Value
  val name = Value
  val source = Value
  val collectionName = Value
  val dimension = Value
  val metric = Value
  val pods = Value
  val replicas = Value
  val pod_type = Value
  val metadata_config = Value
  val source_collection = Value
  val index_type = Value
  val index_config = Value
  val indexName = Value
}
