package no.hvl.dat251.fjelltur.dto

import no.hvl.dat251.fjelltur.entity.DistanceRule
import no.hvl.dat251.fjelltur.entity.Rule
import no.hvl.dat251.fjelltur.entity.TimeRule
import javax.validation.constraints.Min

abstract class CreateRuleRequest(
  val name: String,
  val body: String,
  @Min(1)
  val basicPoints: Int,
)

abstract class UpdateRuleRequest(
  val name: String,
  val body: String?,
  @Min(1)
  val basicPoints: Int?,
)

inline class RuleId(val id: String)

data class RuleIdOnlyResponse(val id: String?)

open class RegisteredRuleResponse(
  val id: String,
  val name: String,
  val body: String,
  val basicPoints: Int,
)

fun Rule.toRuleIdOnlyResponse(): RuleIdOnlyResponse {
  return RuleIdOnlyResponse(this.id.id)
}

fun Rule.toResponse(): RegisteredRuleResponse {
  return when (this) {
    is DistanceRule -> this.toResponse()
    is TimeRule -> this.toResponse()
    else -> RegisteredRuleResponse(
      id.id,
      name ?: error("Rule name is null"),
      body ?: error("Rule body is null"),
      basicPoints ?: error("Rule basic points is null"),
    )
  }
}

const val TIMERULE = "TIME RULE"
const val DISTANCERULE = "DISTANCE RULE"
