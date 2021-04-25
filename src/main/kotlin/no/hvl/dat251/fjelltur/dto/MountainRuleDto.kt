package no.hvl.dat251.fjelltur.dto

import io.swagger.v3.oas.annotations.media.Schema
import no.hvl.dat251.fjelltur.entity.rule.MountainRule
import javax.validation.constraints.Min

class CreateMountainRuleRequest(
  name: String,
  body: String,
  @Min(1)
  basicPoints: Int,

  @Schema(
    description = "How far you must travel, in meters, before and after reaching the summit. " +
      "Only the first location within `summitRadiusMeters` of `summit` is considered"
  )
  @Min(0)
  val minMetersTraveled: Int,
  @Schema(description = "Radius, in meters, a trip location must be within to count it as being on the summit")
  @Min(0)
  val summitRadiusMeters: Int,
  @Schema(description = "The summit of the mountain")
  val summit: GPSLocationRequest
) : CreateRuleRequest(name, body, basicPoints)

data class MountainRuleIdOnlyResponse(val id: String)

data class RegisteredMountainRuleResponse(
  val id: String,
  val name: String,
  val body: String,
  val basicPoints: Int,
  val minMetersTraveled: Int,
  val summitRadiusMeters: Int,
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
  minMetersTraveled,
  summitRadiusMeters,
  summit?.toResponse() ?: error("radius is null")
)
