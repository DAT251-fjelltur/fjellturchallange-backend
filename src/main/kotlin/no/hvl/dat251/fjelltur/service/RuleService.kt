package no.hvl.dat251.fjelltur.service

import no.hvl.dat251.fjelltur.ADMIN_ROLE
import no.hvl.dat251.fjelltur.CREATE_RULE_PERMISSION
import no.hvl.dat251.fjelltur.dto.CreateDistanceRuleRequest
import no.hvl.dat251.fjelltur.dto.CreateTimeRuleRequest
import no.hvl.dat251.fjelltur.model.DistanceRule
import no.hvl.dat251.fjelltur.model.Rule
import no.hvl.dat251.fjelltur.model.TimeRule
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize

interface RuleService {

  @PreAuthorize("hasAuthority('$CREATE_RULE_PERMISSION') or hasRole('$ADMIN_ROLE')")
  fun createTimeRule(request: CreateTimeRuleRequest): TimeRule

  @PreAuthorize("hasAuthority('$CREATE_RULE_PERMISSION') or hasRole('$ADMIN_ROLE')")
  fun createDistanceRule(request: CreateDistanceRuleRequest): DistanceRule

  fun findAll(pageable: Pageable): Page<Rule>
}
