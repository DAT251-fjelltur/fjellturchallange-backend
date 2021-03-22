package no.hvl.dat251.fjelltur.dto

import no.hvl.dat251.fjelltur.model.Rule

data class MakeRuleRequest(
  val poeng: Int,
  val name: String,

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
  basicPoints!!,
  name!!,
)
const val TIMERULE = "TIME RULE"
