package  io.cequence.pineconescala

class PineconeScalaClientException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
  def this(message: String) = this(message, null)
}

class PineconeScalaClientTimeoutException(message: String, cause: Throwable) extends PineconeScalaClientException(message, cause) {
  def this(message: String) = this(message, null)
}

class PineconeScalaClientUnknownHostException(message: String, cause: Throwable) extends PineconeScalaClientException(message, cause) {
  def this(message: String) = this(message, null)
}