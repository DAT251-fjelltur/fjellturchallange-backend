package no.hvl.dat251.fjelltur.controller

import no.hvl.dat251.fjelltur.API_VERSION_1
import no.hvl.dat251.fjelltur.dto.MakeRuleRequest
import no.hvl.dat251.fjelltur.dto.MakeTimeRuleRequest
import no.hvl.dat251.fjelltur.dto.RegisteredRuleResponse
import no.hvl.dat251.fjelltur.dto.RuleIdOnlyResponse
import no.hvl.dat251.fjelltur.dto.TimeRuleIdOnlyResponse
import no.hvl.dat251.fjelltur.dto.toResponse
import no.hvl.dat251.fjelltur.dto.toRuleIdOnlyResponse
import no.hvl.dat251.fjelltur.dto.toTimeRuleOnlyResponse
import no.hvl.dat251.fjelltur.service.RuleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("$API_VERSION_1/rule")
class RuleController(
  @Autowired val ruleService: RuleService,

) {

  @Deprecated("nolonger in use")
  @PostMapping("/make")
  fun makeRule(@Valid @RequestBody request: MakeRuleRequest): RuleIdOnlyResponse {
    return ruleService.makeRule(request).toRuleIdOnlyResponse()
  }

  @PostMapping("/make/time")
  fun makeTime(@Valid @RequestBody request: MakeTimeRuleRequest): TimeRuleIdOnlyResponse {
    return ruleService.makeTimeRule(request).toTimeRuleOnlyResponse()
  }

  @GetMapping("/getAll")
  fun getAll(page: Pageable): Page<RegisteredRuleResponse> {
    return ruleService.findAll(page).map { it.toResponse() }
  }
}