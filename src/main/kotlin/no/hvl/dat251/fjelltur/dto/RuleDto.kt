package no.hvl.dat251.fjelltur.dto

import no.hvl.dat251.fjelltur.entity.Rule
import javax.validation.constraints.Min
import javax.validation.constraints.Size

abstract class CreateRuleRequest(
  @Size(max = 255)
  val name: String,
  @Size(max = 1024)
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

const val TIME_RULE = "TIME RULE"
const val DISTANCE_RULE = "DISTANCE RULE"
const val MOUNTAIN_RULE = "MOUNTAIN RULE"
