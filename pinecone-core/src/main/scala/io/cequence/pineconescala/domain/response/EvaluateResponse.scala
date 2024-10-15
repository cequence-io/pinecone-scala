package io.cequence.pineconescala.domain.response

case class EvaluateResponse(
  metrics: Metrics,
  reasoning: Reasoning,
  usage: EvaluateUsage
)

case class Metrics(
  correctness: Double,
  completeness: Double,
  alignment: Double
)

case class Reasoning(
  evaluated_facts: List[EvaluatedFact]
)

case class EvaluatedFact(
  fact: Fact,
  entailment: String
)

case class Fact(
  content: String
)

case class EvaluateUsage(
  prompt_tokens: Int,
  completion_tokens: Int,
  total_tokens: Int
)
