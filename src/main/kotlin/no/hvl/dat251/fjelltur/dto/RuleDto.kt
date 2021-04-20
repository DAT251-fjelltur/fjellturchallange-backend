package no.hvl.dat251.fjelltur.dto

import no.hvl.dat251.fjelltur.model.Rule
import javax.validation.constraints.Min

abstract class CreateRuleRequest(
  val name: String,
  val body: String,
  @Min(1)
  val basicPoints: Int,
)

inline class RuleId(val id: String)

data class RuleIdOnlyResponse(val id: String?)

data class RegisteredRuleResponse(
  val id: String,
  val poeng: Int,
  val name: String,

)

fun Rule.toRuleIdOnlyResponse(): RuleIdOnlyResponse {
  return RuleIdOnlyResponse(this.id.id)
}

fun Rule.toResponse(): RegisteredRuleResponse = RegisteredRuleResponse(
  id.id,
  basicPoints ?: kotlin.error("Basic points is null"),
  name ?: kotlin.error("Name is null"),
)
const val TIMERULE = "TIME RULE"
const val DISTANCERULE = "DISTANCE RULE"
