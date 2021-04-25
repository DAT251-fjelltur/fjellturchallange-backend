package no.hvl.dat251.fjelltur.dto

import no.hvl.dat251.fjelltur.entity.TimeRule
import javax.validation.constraints.Min

class CreateTimeRuleRequest(
  name: String,
  body: String,
  @Min(1)
  basicPoints: Int,
  @Min(1) // TODO test
  val minimumMinutes: Int
) : CreateRuleRequest(name, body, basicPoints)

data class TimeRuleIdOnlyResponse(val id: String)

data class RegisteredTimeRuleResponse(
  val id: String,
  val name: String,
  val body: String,
  val basicPoints: Int,
  val minimumMinutes: Int,
)

inline class TimeRuleId(val id: String)

fun TimeRule.toTimeRuleOnlyResponse(): TimeRuleIdOnlyResponse {
  return TimeRuleIdOnlyResponse(this.id.id)
}

fun TimeRule.toResponse(): RegisteredTimeRuleResponse = RegisteredTimeRuleResponse(
  id.id,
  name ?: error("Name is null"),
  body ?: error("Body is null"),
  basicPoints ?: error("Basic points is null"),
  minimumMinutes ?: error("Min time is null")
)
