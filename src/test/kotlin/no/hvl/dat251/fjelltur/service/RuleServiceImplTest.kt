package no.hvl.dat251.fjelltur.service

import no.hvl.dat251.fjelltur.dto.MakeTimeRuleRequest
import no.hvl.dat251.fjelltur.exception.NotUniqueRuleException
import no.hvl.dat251.fjelltur.model.TimeRule
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
    return ruleService.makeTimeRule(
      MakeTimeRuleRequest(name, body, basicPoints, minTime)
    )
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
  fun `all fields of a rule is set right`() {
    val rule = makeNewBasicTimeRule()
    assertEquals(rule.name, ruleRepository.findAllByName("Test_time_rule")?.name)
    assertEquals(rule.body, ruleRepository.findAllByName("Test_time_rule")?.body)
    assertEquals(rule.basicPoints, ruleRepository.findAllByName("Test_time_rule")?.basicPoints)
    // TODO: Find way to test minTime
  }
}
