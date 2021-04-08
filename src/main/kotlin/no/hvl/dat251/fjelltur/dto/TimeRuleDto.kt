package no.hvl.dat251.fjelltur.dto

import no.hvl.dat251.fjelltur.model.TimeRule
import javax.validation.constraints.Min

data class MakeTimeRuleRequest(
  val name: String,
  val body: String,
  @Min(1)
  val basicPoints: Int,
  @Min(0)
  val minTime: Int
)

data class TimeRuleIdOnlyResponse(val id: String)
data class RegisteredTimeRuleResponse(
  val id: String,
  val name: String,
  val body: String,
  val basicPoints: Int,
  val minTime: Int,
)

inline class TimeRuleId(val id: String)

fun TimeRule.toTimeRuleOnlyResponse(): TimeRuleIdOnlyResponse {
  return TimeRuleIdOnlyResponse(this.id.id)
}
fun TimeRule.toResponse(): RegisteredTimeRuleResponse = RegisteredTimeRuleResponse(
  id.id,
  name ?: kotlin.error("Name is null"),
  body ?: kotlin.error("Body is null"),
  basicPoints ?: kotlin.error("Basic points is null"),
  minTime?: kotlin.error("Min time is null")
)
