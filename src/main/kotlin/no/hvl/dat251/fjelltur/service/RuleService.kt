package no.hvl.dat251.fjelltur.service

import no.hvl.dat251.fjelltur.ADMIN_ROLE
import no.hvl.dat251.fjelltur.CRUD_RULE_PERMISSION
import no.hvl.dat251.fjelltur.dto.CreateDistanceRuleRequest
import no.hvl.dat251.fjelltur.dto.CreateMountainRuleRequest
import no.hvl.dat251.fjelltur.dto.CreateTimeRuleRequest
import no.hvl.dat251.fjelltur.dto.UpdateDistanceRuleRequest
import no.hvl.dat251.fjelltur.dto.UpdateTimeRuleRequest
import no.hvl.dat251.fjelltur.entity.DistanceRule
import no.hvl.dat251.fjelltur.entity.Rule
import no.hvl.dat251.fjelltur.entity.TimeRule
import no.hvl.dat251.fjelltur.entity.rule.MountainRule
import no.hvl.dat251.fjelltur.exception.UnknownRuleNameException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.ExceptionHandler

interface RuleService {

  @PreAuthorize("hasAuthority('$CRUD_RULE_PERMISSION') or hasRole('$ADMIN_ROLE')")
  fun createTimeRule(request: CreateTimeRuleRequest): TimeRule

  @PreAuthorize("hasAuthority('$CRUD_RULE_PERMISSION') or hasRole('$ADMIN_ROLE')")
  fun createDistanceRule(request: CreateDistanceRuleRequest): DistanceRule

  @PreAuthorize("hasAuthority('$CRUD_RULE_PERMISSION') or hasRole('$ADMIN_ROLE')")
  fun createMountainRule(request: CreateMountainRuleRequest): MountainRule

  fun findAll(pageable: Pageable): Page<Rule>

  @ExceptionHandler(UnknownRuleNameException::class)
  fun findByName(name: String): Rule

  @ExceptionHandler(UnknownRuleNameException::class)
  @PreAuthorize("hasAuthority('$CRUD_RULE_PERMISSION') or hasRole('$ADMIN_ROLE')")
  fun deleteRule(name: String)

  @PreAuthorize("hasAuthority('$CRUD_RULE_PERMISSION') or hasRole('$ADMIN_ROLE')")
  fun updateDistanceRule(request: UpdateDistanceRuleRequest): DistanceRule

  @PreAuthorize("hasAuthority('$CRUD_RULE_PERMISSION') or hasRole('$ADMIN_ROLE')")
  fun updateTimeRule(request: UpdateTimeRuleRequest): TimeRule
}
