package no.hvl.dat251.fjelltur.service.impl

import no.hvl.dat251.fjelltur.dto.CreateDistanceRuleRequest
import no.hvl.dat251.fjelltur.dto.CreateMountainRuleRequest
import no.hvl.dat251.fjelltur.dto.CreateTimeRuleRequest
import no.hvl.dat251.fjelltur.entity.DistanceRule
import no.hvl.dat251.fjelltur.entity.Rule
import no.hvl.dat251.fjelltur.entity.TimeRule
import no.hvl.dat251.fjelltur.entity.rule.MountainRule
import no.hvl.dat251.fjelltur.exception.NotUniqueRuleException
import no.hvl.dat251.fjelltur.repository.GPSLocationRepository
import no.hvl.dat251.fjelltur.repository.RuleRepository
import no.hvl.dat251.fjelltur.service.RuleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class RuleServiceImpl(
  @Autowired val ruleRepository: RuleRepository,
  @Autowired val locationRepository: GPSLocationRepository
) : RuleService {

  @Transactional
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

  @Transactional
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

  @Transactional
  override fun createMountainRule(request: CreateMountainRuleRequest): MountainRule {
    synchronized(RULE_SYNC) {
      if (ruleRepository.existsRuleByName(request.name)) {
        throw NotUniqueRuleException(request.name)
      }
      val rule = MountainRule()

      rule.name = request.name
      rule.body = request.body
      rule.basicPoints = request.basicPoints
      rule.radiusMeters = request.radiusMeters
      rule.summit = request.summit.toGPSLocation().let { locationRepository.saveAndFlush(it) }

      return ruleRepository.saveAndFlush(rule)
    }
  }

  override fun findAll(pageable: Pageable) = findAllRules { ruleRepository.findAll(pageable) }

  companion object {
    val RULE_SYNC = Any()
  }
}
