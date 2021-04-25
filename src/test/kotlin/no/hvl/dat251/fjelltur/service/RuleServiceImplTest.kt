package no.hvl.dat251.fjelltur.service

import no.hvl.dat251.fjelltur.ADMIN_ROLE
import no.hvl.dat251.fjelltur.CREATE_RULE_PERMISSION
import no.hvl.dat251.fjelltur.dto.CreateDistanceRuleRequest
import no.hvl.dat251.fjelltur.dto.CreateTimeRuleRequest
import no.hvl.dat251.fjelltur.entity.DistanceRule
import no.hvl.dat251.fjelltur.entity.GPSLocationTest.Companion.createCoordinate
import no.hvl.dat251.fjelltur.entity.TimeRule
import no.hvl.dat251.fjelltur.entity.Trip
import no.hvl.dat251.fjelltur.exception.NotUniqueRuleException
import no.hvl.dat251.fjelltur.repository.RuleRepository
import org.junit.jupiter.api.Assertions.assertEquals
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

  fun makeNewBasicTimeRule(
    name: String = "Test_time_rule",
    body: String = "This is a testing time rule for testing",
    basicPoints: Int = 3,
    minTime: Int = 10
  ): TimeRule {
    return ruleService.createTimeRule(CreateTimeRuleRequest(name, body, basicPoints, minTime))
  }

  fun makeNewBasicDistanceRule(
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

  @WithMockUser(authorities = [CREATE_RULE_PERMISSION])
  @Test
  fun `disallow different rules with the same name`() {
    val ruleName = "test"
    makeNewBasicDistanceRule(ruleName)
    assertThrows<NotUniqueRuleException> { makeNewBasicTimeRule(ruleName) }
  }

  @WithMockUser(authorities = [CREATE_RULE_PERMISSION])
  @Test
  fun `create distance rule`() {
    val ruleName = "Test_distance_rule"
    val rule = makeNewBasicDistanceRule(ruleName)
    assertEquals(1, ruleRepository.count())
    assertEquals(rule, ruleRepository.findAllByName(ruleName))
  }

  @WithMockUser(authorities = [CREATE_RULE_PERMISSION])
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

  @WithMockUser(authorities = [CREATE_RULE_PERMISSION])
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

  @WithMockUser(authorities = [CREATE_RULE_PERMISSION])
  @Test
  fun `make to rules with same name`() {
    makeNewBasicTimeRule()
    assertThrows<NotUniqueRuleException> { makeNewBasicTimeRule() }
  }

  @WithMockUser(authorities = [CREATE_RULE_PERMISSION])
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
}
