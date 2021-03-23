package no.hvl.dat251.fjelltur.service

import no.hvl.dat251.fjelltur.dto.MakeRuleRequest
import no.hvl.dat251.fjelltur.dto.MakeTimeRuleRequest
<<<<<<< HEAD
import no.hvl.dat251.fjelltur.exception.NotUniqueRuleException
=======
>>>>>>> 8cc1a93f19208f5a3610130adee7ce6ee7c5edb0
import no.hvl.dat251.fjelltur.model.Rule
import no.hvl.dat251.fjelltur.model.TimeRule
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.ExceptionHandler

interface RuleService {
  @Deprecated("Do not use")
  fun makeRule(request: MakeRuleRequest): Rule

  @ExceptionHandler(NotUniqueRuleException::class)
  fun makeTimeRule(request: MakeTimeRuleRequest): TimeRule

  fun findAll(pageable: Pageable): Page<Rule>
}
