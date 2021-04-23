package no.hvl.dat251.fjelltur.dto

import no.hvl.dat251.fjelltur.entity.DistanceRule
import javax.validation.constraints.Min

class CreateDistanceRuleRequest(
  name: String,
  body: String,
  @Min(1)
  basicPoints: Int,
  @Min(0)
  val minKilometers: Int
) : CreateRuleRequest(name, body, basicPoints)

/**
 * @param name can not be null as this is is used to find the distance rule to be updated
 *
 * @author Mathias Skallerud Jacobsen
 */
class UpdateDistanceRule(
  val name: String,
  val body: String?,
  @Min(1)
  val basicPoints: Int?,
  @Min(0)
  val minKilometers: Int?
)

data class DistanceRuleIdOnlyResponse(val id: String)

data class RegisteredDistanceRuleResponse(
  val id: String,
  val name: String,
  val body: String,
  val basicPoints: Int,
  val minKilometers: Int,
)

inline class DistanceRuleId(val id: String)

fun DistanceRule.toDistanceRuleOnlyResponse(): DistanceRuleIdOnlyResponse {
  return DistanceRuleIdOnlyResponse(this.id.id)
}

fun DistanceRule.toResponse() = RegisteredDistanceRuleResponse(
  id.id,
  name ?: error("Name is null"),
  body ?: error("Body is null"),
  basicPoints ?: error("Basic points is null"),
  minKilometers ?: error("Kilometers is null")
)
