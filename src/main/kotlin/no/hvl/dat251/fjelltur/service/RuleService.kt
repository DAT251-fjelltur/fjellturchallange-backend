package no.hvl.dat251.fjelltur.service

import no.hvl.dat251.fjelltur.dto.MakeRuleRequest
import no.hvl.dat251.fjelltur.dto.MakeTimeRuleRequest
import no.hvl.dat251.fjelltur.model.Rule
import no.hvl.dat251.fjelltur.model.TimeRule
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RuleService {
  @Deprecated("Do not use")
  fun makeRule(request: MakeRuleRequest): Rule

  fun makeTimeRule(request: MakeTimeRuleRequest): TimeRule

  fun findAll(pageable: Pageable): Page<Rule>
}
