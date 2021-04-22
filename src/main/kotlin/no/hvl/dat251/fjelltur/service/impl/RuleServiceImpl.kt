package no.hvl.dat251.fjelltur.service.impl

import no.hvl.dat251.fjelltur.dto.CreateDistanceRuleRequest
import no.hvl.dat251.fjelltur.dto.CreateTimeRuleRequest
import no.hvl.dat251.fjelltur.exception.NotUniqueRuleException
import no.hvl.dat251.fjelltur.entity.DistanceRule
import no.hvl.dat251.fjelltur.entity.Rule
import no.hvl.dat251.fjelltur.entity.TimeRule
import no.hvl.dat251.fjelltur.repository.RuleRepository
import no.hvl.dat251.fjelltur.service.RuleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class RuleServiceImpl(@Autowired val ruleRepository: RuleRepository) : RuleService {

  override fun createTimeRule(request: CreateTimeRuleRequest): TimeRule {
    synchronized(RULE_SYNC) {
      if (ruleRepository.existsRuleByName(request.name)) {
        throw NotUniqueRuleException(request.name)
      }
      val rule = TimeRule()

      rule.name = request.name
      rule.body = request.body
      rule.basicPoints = request.basicPoints
      rule.minimumMinutes = request.minimumMinutes

      return ruleRepository.saveAndFlush(rule)
    }
  }

  private fun findAllRules(query: () -> Page<Rule>): Page<Rule> {
    return query()
  }

  override fun createDistanceRule(request: CreateDistanceRuleRequest): DistanceRule {
    synchronized(RULE_SYNC) {
      if (ruleRepository.existsRuleByName(request.name)) {
        throw NotUniqueRuleException(request.name)
      }
      val rule = DistanceRule()

      rule.name = request.name
      rule.body = request.body
      rule.basicPoints = request.basicPoints
      rule.minKilometers = request.minKilometers

      return ruleRepository.saveAndFlush(rule)
    }
  }

  override fun findAll(pageable: Pageable) = findAllRules { ruleRepository.findAll(pageable) }

  companion object {
    val RULE_SYNC = Any()
  }
}
