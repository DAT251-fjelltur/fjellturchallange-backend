package no.hvl.dat251.fjelltur.service

import no.hvl.dat251.fjelltur.ADMIN_ROLE
import no.hvl.dat251.fjelltur.CRUD_RULE_PERMISSION
import no.hvl.dat251.fjelltur.dto.CreateDistanceRuleRequest
import no.hvl.dat251.fjelltur.dto.CreateTimeRuleRequest
import no.hvl.dat251.fjelltur.dto.UpdateDistanceRuleRequest
import no.hvl.dat251.fjelltur.dto.UpdateTimeRuleRequest
import no.hvl.dat251.fjelltur.entity.DistanceRule
import no.hvl.dat251.fjelltur.entity.GPSLocationTest.Companion.createCoordinate
import no.hvl.dat251.fjelltur.entity.TimeRule
import no.hvl.dat251.fjelltur.entity.Trip
import no.hvl.dat251.fjelltur.exception.NotUniqueRuleException
import no.hvl.dat251.fjelltur.exception.UnknownRuleNameException
import no.hvl.dat251.fjelltur.repository.RuleRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@SpringBootTest
internal class RuleServiceImplTest {

  @Autowired
  lateinit var ruleRepository: RuleRepository

  @Autowired
  lateinit var ruleService: RuleService

  @BeforeEach
  fun setUp() {
    ruleRepository.deleteAll()
  }

  private fun makeNewBasicTimeRule(
    name: String = "Test_time_rule",
    body: String = "This is a testing time rule for testing",
    basicPoints: Int = 3,
    minTime: Int = 10
  ): TimeRule {
    return ruleService.createTimeRule(CreateTimeRuleRequest(name, body, basicPoints, minTime))
  }

  private fun makeNewBasicDistanceRule(
    name: String = "Test_distance_rule",
    body: String = "This is a test distance rule",
    basicPoints: Int = 3,
    minKm: Int = 10
  ): DistanceRule {
    return ruleService.createDistanceRule(CreateDistanceRuleRequest(name, body, basicPoints, minKm))
  }

  @Test
  fun `calculate correct time points`() {
    val coordinateOne = createCoordinate(
      recordedAt =
      OffsetDateTime.of(1945, 5, 8, 9, 0, 0, 0, ZoneOffset.UTC)
    )
    val coordinateTwo = createCoordinate(
      recordedAt =
      OffsetDateTime.of(1945, 5, 8, 10, 0, 0, 0, ZoneOffset.UTC)
    )
    val trip = Trip()

    trip.locations = mutableListOf(coordinateOne, coordinateTwo)
    val timeRule = TimeRule()

    trip.ongoing = true
    assertThrows<IllegalArgumentException> { timeRule.calculatePoints(trip) }

    trip.ongoing = false

    timeRule.basicPoints = 1
    timeRule.minimumMinutes = 30
    assertEquals(2, timeRule.calculatePoints(trip))

    timeRule.basicPoints = 2
    timeRule.minimumMinutes = 30
    assertEquals(4, timeRule.calculatePoints(trip))

    timeRule.basicPoints = 2
    timeRule.minimumMinutes = 31
    assertEquals(2, timeRule.calculatePoints(trip))

    timeRule.basicPoints = 1
    timeRule.minimumMinutes = 60
    assertEquals(1, timeRule.calculatePoints(trip))

    timeRule.basicPoints = 2
    timeRule.minimumMinutes = 15
    assertEquals(8, timeRule.calculatePoints(trip))
  }

  @Test
  fun `calculate correct distance points`() {
    val coordinateOne = createCoordinate(60.3741927, 5.3350323) // kiwi :,)
    val coordinateTwo = createCoordinate(60.41136525054337, 5.33242996996982) // stoltze
    val trip = Trip()
    trip.locations = mutableListOf(coordinateOne, coordinateTwo)

    val distanceRule = DistanceRule()

    trip.ongoing = true
    assertThrows<IllegalArgumentException> { distanceRule.calculatePoints(trip) }

    trip.ongoing = false

    distanceRule.basicPoints = 1
    distanceRule.minKilometers = 1
    assertEquals(4, distanceRule.calculatePoints(trip))

    distanceRule.basicPoints = 2
    distanceRule.minKilometers = 1
    assertEquals(8, distanceRule.calculatePoints(trip))

    distanceRule.basicPoints = 1
    distanceRule.minKilometers = 2
    assertEquals(2, distanceRule.calculatePoints(trip))

    distanceRule.basicPoints = 2
    distanceRule.minKilometers = 3
    assertEquals(2, distanceRule.calculatePoints(trip))
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `disallow different rules with the same name`() {
    val ruleName = "test"
    makeNewBasicDistanceRule(ruleName)
    assertThrows<NotUniqueRuleException> { makeNewBasicTimeRule(ruleName) }
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `create distance rule`() {
    val ruleName = "Test_distance_rule"
    val rule = makeNewBasicDistanceRule(ruleName)
    assertEquals(1, ruleRepository.count())
    assertEquals(rule, ruleRepository.findAllByName(ruleName))
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `all fields of a distance rule are set right`() {
    val ruleName = "Test_dist"
    val body = "Body"
    val points = 8
    val minKm = 20
    val rule = makeNewBasicDistanceRule(ruleName, body, points, minKm)
    val foundRule = assertNotNull(ruleRepository.findAllByName(ruleName))
    assertTrue(foundRule is DistanceRule)
    assertEquals(rule.name, ruleName)
    assertEquals(rule.body, body)
    assertEquals(rule.basicPoints, points)
    assertEquals(rule.minKilometers, minKm)
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `make new time rule`() {
    val ruleName = "Test_time_rule"
    val rule = makeNewBasicTimeRule(ruleName)
    assertEquals(1, ruleRepository.count())
    assertEquals(rule, ruleRepository.findAllByName(ruleName))
  }

  @WithMockUser(authorities = [])
  @Test
  fun `cannot make time without CREATE_RULE_PERMISSION`() {
    assertThrows<AccessDeniedException> { makeNewBasicTimeRule() }
  }

  @WithMockUser(authorities = [ADMIN_ROLE])
  @Test
  fun `can create time rule as admin`() {
    assertDoesNotThrow { makeNewBasicTimeRule() }
  }

  @WithMockUser(authorities = [])
  @Test
  fun `cannot make distance without CREATE_RULE_PERMISSION`() {
    assertThrows<AccessDeniedException> { makeNewBasicDistanceRule() }
  }

  @WithMockUser(authorities = [ADMIN_ROLE])
  @Test
  fun `can create distance rule as admin`() {
    assertDoesNotThrow { makeNewBasicDistanceRule() }
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `make to rules with same name`() {
    makeNewBasicTimeRule()
    assertThrows<NotUniqueRuleException> { makeNewBasicTimeRule() }
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `all fields of a time rule are set right`() {
    val ruleName = "Test_time_rule"
    val rule = makeNewBasicTimeRule(ruleName)
    val foundRule = assertNotNull(ruleRepository.findAllByName(ruleName))

    assertTrue(foundRule is TimeRule)

    assertEquals(rule.name, foundRule.name)
    assertEquals(rule.body, foundRule.body)
    assertEquals(rule.basicPoints, foundRule.basicPoints)
    assertEquals(rule.minimumMinutes, foundRule.minimumMinutes)
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `throws when rule do not exists`() {
    val ruleName = "Test_time_rule"

    assertThrows<UnknownRuleNameException> {
      ruleService.deleteRule(ruleName)
    }
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `delete rule existing rule works`() {
    val ruleName = "Test_time_rule"
    makeNewBasicTimeRule(ruleName)
    assertDoesNotThrow {
      assertTrue(ruleRepository.existsRuleByName(ruleName))
      ruleService.deleteRule(ruleName)
      assertFalse(ruleRepository.existsRuleByName(ruleName))
    }
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `update distance rule that dont exists throws`() {
    val ruleName = "Test_distance_rule"
    val request = UpdateDistanceRuleRequest(ruleName, null, null, null)

    assertThrows<UnknownRuleNameException> { ruleService.updateDistanceRule(request) }
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `update distance rule with new body`() {
    val ruleName = "Test_distance_rule"
    val ruleBody = "This is the body"
    val createRequest = CreateDistanceRuleRequest(ruleName, ruleBody, 1, 10)
    ruleService.createDistanceRule(createRequest)
    val updatedBody = "This is a new body"
    val updateDistanceRuleRequest = UpdateDistanceRuleRequest(ruleName, updatedBody, null, null)
    ruleService.updateDistanceRule(updateDistanceRuleRequest)
    val updatedDistanceRule = ruleRepository.findAllByName(ruleName) as DistanceRule
    assertEquals(updatedBody, updatedDistanceRule.body)
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `update distance rule with new basicPoints`() {
    val ruleName = "Test_distance_rule"
    val ruleBody = "This is the body"
    val createRequest = CreateDistanceRuleRequest(ruleName, ruleBody, 1, 10)
    ruleService.createDistanceRule(createRequest)
    val updatedBasicPoints = 20
    val updateDistanceRuleRequest = UpdateDistanceRuleRequest(ruleName, null, updatedBasicPoints, null)
    ruleService.updateDistanceRule(updateDistanceRuleRequest)
    val updatedDistanceRule = ruleRepository.findAllByName(ruleName) as DistanceRule
    assertEquals(updatedBasicPoints, updatedDistanceRule.basicPoints)
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `update distance rule with new min kilometers`() {
    val ruleName = "Test_distance_rule"
    val ruleBody = "This is the body"
    val createRequest = CreateDistanceRuleRequest(ruleName, ruleBody, 1, 10)
    ruleService.createDistanceRule(createRequest)
    val updatedMinKm = 2
    val updateDistanceRuleRequest = UpdateDistanceRuleRequest(ruleName, null, null, updatedMinKm)
    ruleService.updateDistanceRule(updateDistanceRuleRequest)
    val updatedDistanceRule = ruleRepository.findAllByName(ruleName) as DistanceRule
    assertEquals(updatedMinKm, updatedDistanceRule.minKilometers)
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `update distance rule with no updated values doesn't do anything`() {
    val ruleName = "Test_distance_rule"
    val ruleBody = "This is the body"
    val createRequest = CreateDistanceRuleRequest(ruleName, ruleBody, 1, 10)
    val distanceRule = ruleService.createDistanceRule(createRequest)

    val updateDistanceRuleRequest = UpdateDistanceRuleRequest(ruleName, null, null, null)

    ruleService.updateDistanceRule(updateDistanceRuleRequest)
    val updatedDistanceRule = ruleRepository.findAllByName(ruleName) as DistanceRule

    assertEquals(distanceRule, updatedDistanceRule)
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `update time rule that doesn't exist should throw`() {
    val ruleName = "Test time rule"
    val updateTimeRuleRequest = UpdateTimeRuleRequest(ruleName, null, null, null)

    assertThrows<UnknownRuleNameException> { ruleService.updateTimeRule(updateTimeRuleRequest) }
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `update time rule with new body`() {
    val ruleName = "Test time rule"
    makeNewBasicTimeRule(name = ruleName)

    val newBody = "This is the new updated body"
    val updateTimeRuleRequest = UpdateTimeRuleRequest(ruleName, newBody, null, null)

    val newTimeRule = ruleService.updateTimeRule(updateTimeRuleRequest)

    assertEquals(newBody, newTimeRule.body)
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `update time rule with new basic points`() {
    val ruleName = "Test time rule"
    makeNewBasicTimeRule(name = ruleName)

    val basicPoints = 1000
    val updateTimeRuleRequest = UpdateTimeRuleRequest(ruleName, null, basicPoints, null)

    val newTimeRule = ruleService.updateTimeRule(updateTimeRuleRequest)

    assertEquals(basicPoints, newTimeRule.basicPoints)
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `update time rule with new basic minMin`() {
    val ruleName = "Test time rule"
    makeNewBasicTimeRule(name = ruleName)

    val newMinMin = 1000
    val updateTimeRuleRequest = UpdateTimeRuleRequest(ruleName, null, null, newMinMin)

    val newTimeRule = ruleService.updateTimeRule(updateTimeRuleRequest)

    assertEquals(newMinMin, newTimeRule.minimumMinutes)
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `update time rule with no updated values doesn't do anything`() {
    val ruleName = "Test_time_rule"
    val ruleBody = "This is the body"
    val createRequest = CreateTimeRuleRequest(ruleName, ruleBody, 1, 10)
    val timeRule = ruleService.createTimeRule(createRequest)

    val updateTimeRuleRequest = UpdateTimeRuleRequest(ruleName, null, null, null)

    ruleService.updateTimeRule(updateTimeRuleRequest)
    val updatedDistanceRule = ruleRepository.findAllByName(ruleName) as TimeRule

    assertEquals(timeRule, updatedDistanceRule)
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `get rule throws if no rule exists`() {
    val ruleName = "Test rule"
    assertThrows<UnknownRuleNameException> { ruleService.findRuleByName(ruleName) }
  
  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `throws when account not found`() {
    assertThrows<UnknownRuleNameException> { ruleService.findByName("non-existing") }
  }

  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
  fun `get rule gets the time rule`() {
    val ruleName = "Test time rule"
    val body: String = "This is a testing time rule for testing"
    val basicPoints: Int = 3
    val minTime: Int = 10
    makeNewBasicTimeRule(name = ruleName)
    val getRule = ruleService.findRuleByName(ruleName) as TimeRule
    assertEquals(ruleName, getRule.name)
    assertEquals(body, getRule.body)
    assertEquals(basicPoints, getRule.basicPoints)
    assertEquals(minTime, getRule.minimumMinutes)
  
  @WithMockUser(authorities = [CRUD_RULE_PERMISSION])
  @Test
fun `Find correct rule by name`() {
    val ruleName = "exists"
    val createdRule = makeNewBasicTimeRule(name = ruleName)
    val foundRule = assertDoesNotThrow { ruleService.findByName(ruleName) }
    assertEquals(createdRule, foundRule)
  }
}
