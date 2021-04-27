package no.hvl.dat251.fjelltur.service.impl

import no.hvl.dat251.fjelltur.dto.CreateDistanceRuleRequest
import no.hvl.dat251.fjelltur.dto.CreateTimeRuleRequest
import no.hvl.dat251.fjelltur.dto.UpdateDistanceRuleRequest
import no.hvl.dat251.fjelltur.dto.UpdateTimeRuleRequest
import no.hvl.dat251.fjelltur.entity.DistanceRule
import no.hvl.dat251.fjelltur.entity.Rule
import no.hvl.dat251.fjelltur.entity.TimeRule
import no.hvl.dat251.fjelltur.exception.NotUniqueRuleException
import no.hvl.dat251.fjelltur.exception.UnknownRuleNameException
import no.hvl.dat251.fjelltur.repository.RuleRepository
import no.hvl.dat251.fjelltur.service.RuleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import javax.transaction.Transactional

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

  override fun findByName(name: String): Rule {
    return ruleRepository.findAllByName(name) ?: throw UnknownRuleNameException(name)
  }

  @Transactional
  override fun deleteRule(name: String) {
    synchronized(RULE_SYNC) {
      if (!ruleRepository.existsRuleByName(name)) {
        throw UnknownRuleNameException(name)
      }
      ruleRepository.deleteRuleByName(name)
    }
  }

  override fun updateDistanceRule(request: UpdateDistanceRuleRequest): DistanceRule {
    synchronized(RULE_SYNC) {
      val name = request.name
      val ruleToBeUpdated: DistanceRule =
        (ruleRepository.findAllByName(name) ?: throw UnknownRuleNameException(name)) as DistanceRule
      if (request.body != null) {
        ruleToBeUpdated.body = request.body
      }
      if (request.basicPoints != null) {
        ruleToBeUpdated.basicPoints = request.basicPoints
      }
      if (request.minKilometers != null) {
        ruleToBeUpdated.minKilometers = request.minKilometers
      }

      return ruleRepository.saveAndFlush(ruleToBeUpdated)
    }
  }

  override fun updateTimeRule(request: UpdateTimeRuleRequest): TimeRule {
    synchronized(RULE_SYNC) {
      val name = request.name
      val ruleToBeUpdated = (ruleRepository.findAllByName(name) ?: throw UnknownRuleNameException(name)) as TimeRule
      if (request.body != null) {
        ruleToBeUpdated.body = request.body
      }
      if (request.basicPoints != null) {
        ruleToBeUpdated.basicPoints = request.basicPoints
      }
      if (request.minimumMinutes != null) {
        ruleToBeUpdated.minimumMinutes = request.minimumMinutes
      }
      return ruleRepository.saveAndFlush(ruleToBeUpdated)
    }
  }

  companion object {
    val RULE_SYNC = Any()
  }
}
