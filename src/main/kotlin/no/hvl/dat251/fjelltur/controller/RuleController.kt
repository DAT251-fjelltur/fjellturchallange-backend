package no.hvl.dat251.fjelltur.controller

import no.hvl.dat251.fjelltur.API_VERSION_1
import no.hvl.dat251.fjelltur.dto.MakeTimeRuleRequest
import no.hvl.dat251.fjelltur.dto.RegisteredRuleResponse
import no.hvl.dat251.fjelltur.dto.TimeRuleIdOnlyResponse
import no.hvl.dat251.fjelltur.dto.toResponse
import no.hvl.dat251.fjelltur.dto.toTimeRuleOnlyResponse
import no.hvl.dat251.fjelltur.service.RuleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping(
  "$API_VERSION_1/rule",
  produces = [MediaType.APPLICATION_JSON_VALUE]
)
class RuleController(@Autowired val ruleService: RuleService) {

  @PostMapping("/make/time", consumes = [MediaType.APPLICATION_JSON_VALUE])
  fun makeTime(@Valid @RequestBody request: MakeTimeRuleRequest): TimeRuleIdOnlyResponse {
    return ruleService.makeTimeRule(request).toTimeRuleOnlyResponse()
  }

  @GetMapping("/getAll")
  fun getAll(page: Pageable): Page<RegisteredRuleResponse> {
    return ruleService.findAll(page).map { it.toResponse() }
  }
}
