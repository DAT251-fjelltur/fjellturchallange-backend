package no.hvl.dat251.fjelltur.service

import no.hvl.dat251.fjelltur.dto.MakeTimeRuleRequest
import no.hvl.dat251.fjelltur.exception.NotUniqueRuleException
import no.hvl.dat251.fjelltur.model.Rule
import no.hvl.dat251.fjelltur.model.TimeRule
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.ExceptionHandler

interface RuleService {

    @ExceptionHandler(NotUniqueRuleException::class)
    fun makeTimeRule(request: MakeTimeRuleRequest): TimeRule

    fun findAll(pageable: Pageable): Page<Rule>
}
