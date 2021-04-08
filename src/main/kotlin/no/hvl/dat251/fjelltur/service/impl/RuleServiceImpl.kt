package no.hvl.dat251.fjelltur.service.impl

import no.hvl.dat251.fjelltur.dto.MakeTimeRuleRequest
import no.hvl.dat251.fjelltur.exception.NotUniqueRuleException
import no.hvl.dat251.fjelltur.model.Rule
import no.hvl.dat251.fjelltur.model.TimeRule
import no.hvl.dat251.fjelltur.repository.RuleRepository
import no.hvl.dat251.fjelltur.service.RuleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class RuleServiceImpl(
  @Autowired val ruleRepository: RuleRepository,
) : RuleService {

  override fun makeTimeRule(request: MakeTimeRuleRequest): TimeRule {
    if (ruleRepository.findAllByName(request.name) != null) {
      throw NotUniqueRuleException()
    }
    val rule = TimeRule()

    rule.name = request.name
    rule.body = request.body
    rule.basicPoints = request.basicPoints
    rule.minTime = request.minTime

    return ruleRepository.saveAndFlush(rule)
  }

  private fun findAllRules(query: () -> Page<Rule>): Page<Rule> {
    return query()
  }

  override fun findAll(pageable: Pageable) = findAllRules { ruleRepository.findAll(pageable) }
}
