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
class UpdateDistanceRuleRequest(
  name: String,
  body: String?,
  @Min(1)
  basicPoints: Int?,
  @Min(0)
  val minKilometers: Int?
) : UpdateRuleRequest(name, body, basicPoints)

data class DistanceRuleIdOnlyResponse(val id: String)

class RegisteredDistanceRuleResponse(
  id: String,
  name: String,
  body: String,
  basicPoints: Int,
  val minKilometers: Int,
) : RegisteredRuleResponse(id, name, body, basicPoints)

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
