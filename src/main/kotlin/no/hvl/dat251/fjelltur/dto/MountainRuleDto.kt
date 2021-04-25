package no.hvl.dat251.fjelltur.dto

import no.hvl.dat251.fjelltur.entity.rule.MountainRule
import javax.validation.constraints.Min

class CreateMountainRuleRequest(
  name: String,
  body: String,
  @Min(1)
  basicPoints: Int,
  @Min(0)
  val radiusMeters: Int,
  val summit: GPSLocationRequest
) : CreateRuleRequest(name, body, basicPoints)

data class MountainRuleIdOnlyResponse(val id: String)

data class RegisteredMountainRuleResponse(
  val id: String,
  val name: String,
  val body: String,
  val basicPoints: Int,
  val radiusMeters: Int,
  val summit: GPSLocationResponse
)

inline class MountainRuleId(val id: String)

fun MountainRule.toMountainRuleOnlyResponse(): MountainRuleIdOnlyResponse {
  return MountainRuleIdOnlyResponse(this.id.id)
}

fun MountainRule.toResponse() = RegisteredMountainRuleResponse(
  id.id,
  name ?: error("Name is null"),
  body ?: error("Body is null"),
  basicPoints ?: error("Basic points is null"),
  radiusMeters ?: error("radius is null"),
  summit?.toResponse() ?: error("radius is null")
)
