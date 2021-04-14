package no.hvl.dat251.fjelltur.service

import no.hvl.dat251.fjelltur.dto.CreateTimeRuleRequest
import no.hvl.dat251.fjelltur.exception.NotUniqueRuleException
import no.hvl.dat251.fjelltur.model.DistanceRule
import no.hvl.dat251.fjelltur.model.GPSLocation
import no.hvl.dat251.fjelltur.model.TimeRule
import no.hvl.dat251.fjelltur.model.Trip
import no.hvl.dat251.fjelltur.repository.RuleRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.IllegalArgumentException

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
    minTime: Int = 10
  ): TimeRule {
    return ruleService.createTimeRule(CreateTimeRuleRequest(name, body, basicPoints, minTime))
  }

  private fun createCoordinate(latitude: Double, longitude: Double, accuracy: Double = 0.0): GPSLocation {
    return GPSLocation().also {
      it.longitude = longitude
      it.latitude = latitude
      it.accuracy = accuracy
    }
  }

  @Test
  fun `calculate correct distance of coordinates`() {
    val coordinateOne = createCoordinate(60.3741927, 5.3350323,) // kiwi :,)
    val coordinateTwo = createCoordinate(60.3757513, 5.3344057,) // u-matkroken :,)
    assertEquals(176.68, coordinateOne.distanceTo(coordinateTwo), 0.1)
  }

  @Test
  fun `calculate correct distance of coordinates transitivity`() {
    val coordinateOne = createCoordinate(60.3741927, 5.3350323,) // kiwi :,)
    val coordinateTwo = createCoordinate(60.3757513, 5.3344057,) // u-matkroken :,)
    assertEquals(coordinateTwo.distanceTo(coordinateOne), coordinateOne.distanceTo(coordinateTwo), 0.0)
  }

  @Test
  fun `calculate correct distance of same coordinates`() {
    val coordinateOne = createCoordinate(60.3741927, 5.3350323,) // kiwi :,)
    assertEquals(0.0, coordinateOne.distanceTo(coordinateOne), 0.0)
  }

  @Test
  fun `calculate correct points`() {
    val coordinateOne = createCoordinate(60.3741927, 5.3350323) // kiwi :,)
    val coordinateTwo = createCoordinate(60.41136525054337, 5.33242996996982) // stoltzen

    // distance = 4135.87 meters

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

  @Test
  fun `all fields of a distance rule are set right`() {

  }

  @Test
  fun `make new time rule`() {
    val rule = makeNewBasicTimeRule()
    assertEquals(1, ruleRepository.count())
    assertEquals(rule, ruleRepository.findAllByName("Test_time_rule"))
  }

  @Test
  fun `make to rules with same name`() {
    assertThrows<NotUniqueRuleException> {
      makeNewBasicTimeRule()
      makeNewBasicTimeRule()
    }
  }
  @Test
  fun `all fields of a time rule are set right`() {
    val rule = makeNewBasicTimeRule()
    assertEquals(rule.name, ruleRepository.findAllByName("Test_time_rule")?.name)
    assertEquals(rule.body, ruleRepository.findAllByName("Test_time_rule")?.body)
    assertEquals(rule.basicPoints, ruleRepository.findAllByName("Test_time_rule")?.basicPoints)
    // TODO: Find way to test minTime
  }
}
