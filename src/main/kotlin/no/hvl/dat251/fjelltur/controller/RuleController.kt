package no.hvl.dat251.fjelltur.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import no.hvl.dat251.fjelltur.API_VERSION_1
import no.hvl.dat251.fjelltur.dto.CreateDistanceRuleRequest
import no.hvl.dat251.fjelltur.dto.CreateMountainRuleRequest
import no.hvl.dat251.fjelltur.dto.CreateTimeRuleRequest
import no.hvl.dat251.fjelltur.dto.DistanceRuleIdOnlyResponse
import no.hvl.dat251.fjelltur.dto.MountainRuleIdOnlyResponse
import no.hvl.dat251.fjelltur.dto.RegisteredRuleResponse
import no.hvl.dat251.fjelltur.dto.TimeRuleIdOnlyResponse
import no.hvl.dat251.fjelltur.dto.UpdateDistanceRuleRequest
import no.hvl.dat251.fjelltur.dto.UpdateTimeRuleRequest
import no.hvl.dat251.fjelltur.dto.toDistanceRuleOnlyResponse
import no.hvl.dat251.fjelltur.dto.toMountainRuleOnlyResponse
import no.hvl.dat251.fjelltur.dto.toResponse
import no.hvl.dat251.fjelltur.dto.toTimeRuleOnlyResponse
import no.hvl.dat251.fjelltur.service.RuleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody

@RestController
@RequestMapping(
  "$API_VERSION_1/rule",
  produces = [MediaType.APPLICATION_JSON_VALUE]
)
class RuleController(@Autowired val ruleService: RuleService) {

  @PostMapping("/create/time", consumes = [MediaType.APPLICATION_JSON_VALUE])
  fun makeTime(@Valid @RequestBody request: CreateTimeRuleRequest): TimeRuleIdOnlyResponse {
    return ruleService.createTimeRule(request).toTimeRuleOnlyResponse()
  }

  @GetMapping("/getAll")
  fun getAll(page: Pageable): Page<RegisteredRuleResponse> {
    return ruleService.findAll(page).map { it.toResponse() }
  }

  @Operation(
    summary = "Find a rule",
    parameters = [Parameter(name = "name", description = "Name of the rule to get")],
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(
        responseCode = "404",
        description = "No rule with the given name in the database",
        content = [Content()]
      )
    ]
  )
  @GetMapping("/find")
  fun getAll(@RequestParam("name") name: String): RegisteredRuleResponse {
    return ruleService.findByName(name).toResponse()
  }

  @PostMapping("/create/distance")
  fun createDistance(@Valid @RequestBody request: CreateDistanceRuleRequest): DistanceRuleIdOnlyResponse {
    return ruleService.createDistanceRule(request).toDistanceRuleOnlyResponse()
  }

  @PostMapping("/create/mountain")
  fun createMountain(@Valid @RequestBody request: CreateMountainRuleRequest): MountainRuleIdOnlyResponse {
    return ruleService.createMountainRule(request).toMountainRuleOnlyResponse()
  }

  @Operation(
    summary = "Delete a rule",
    parameters = [Parameter(name = "name", description = "The name of the rule")],
    responses = [
      ApiResponse(
        responseCode = "404",
        description = "No rule with the given name in the database",
        content = [Content()]
      ),
      ApiResponse(responseCode = "200")
    ]
  )
  @DeleteMapping("/delete")
  fun deleteRule(@RequestParam("name") name: String) {
    return ruleService.deleteRule(name)
  }

  @Operation(
    summary = "Update fields of a distance rule",
    requestBody = SwaggerRequestBody(description = "The fields that is suppose to be updated, the fields that ase suppose to be the same can be null"),
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(
        responseCode = "404",
        description = "No rule with the given name in the database",
        content = [Content()]
      )
    ]
  )
  @PutMapping("/update/distance")
  fun updateDistance(@Valid @RequestBody request: UpdateDistanceRuleRequest): DistanceRuleIdOnlyResponse {
    return ruleService.updateDistanceRule(request).toDistanceRuleOnlyResponse()
  }

  @Operation(
    summary = "Update fields of a time rule",
    requestBody = SwaggerRequestBody(description = "The fields that is suppose to be updated, the fields that ase suppose to be the same can be null"),
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(
        responseCode = "404",
        description = "No rule with the given name in the database",
        content = [Content()]
      )
    ]
  )
  @PutMapping("/update/time")
  fun updateTime(@Valid @RequestBody request: UpdateTimeRuleRequest): TimeRuleIdOnlyResponse {
    return ruleService.updateTimeRule(request).toTimeRuleOnlyResponse()
  }
}
