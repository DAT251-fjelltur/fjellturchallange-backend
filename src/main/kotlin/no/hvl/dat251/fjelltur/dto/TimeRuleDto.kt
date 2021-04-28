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

class UpdateTimeRuleRequest(
  name: String,
  body: String?,
  @Min(1)
  basicPoints: Int?,
  @Min(0)
  val minimumMinutes: Int?
) : UpdateRuleRequest(name, body, basicPoints)

data class TimeRuleIdOnlyResponse(val id: String)

class RegisteredTimeRuleResponse(
  id: String,
  name: String,
  body: String,
  basicPoints: Int,
  val minimumMinutes: Int,
) : RegisteredRuleResponse(id, name, body, basicPoints)

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
