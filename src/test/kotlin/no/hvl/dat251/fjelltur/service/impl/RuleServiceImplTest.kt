package no.hvl.dat251.fjelltur.service.impl

import no.hvl.dat251.fjelltur.dto.MakeTimeRuleRequest
import no.hvl.dat251.fjelltur.model.TimeRule
import no.hvl.dat251.fjelltur.repository.RuleRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class RuleServiceImplTest {

  @Mock
  private lateinit var ruleRepository: RuleRepository

  private lateinit var underTest: RuleServiceImpl

  @BeforeEach
  internal fun setUp() {
    this.underTest = RuleServiceImpl(ruleRepository)
  }

  @Test
  @Disabled
  fun makeRule() {
  }

  @Test
  fun `Can make a time rule`() {
    val makeTimeRuleRequest = MakeTimeRuleRequest(
      "Test",
      "This is a test body",
      3,
      50
    )
    underTest.makeTimeRule(makeTimeRuleRequest)

    val ruleArgumentCaptor = ArgumentCaptor.forClass(TimeRule::class.java)
    verify(ruleRepository).saveAndFlush(ruleArgumentCaptor.capture())
    val capturedRule = ruleArgumentCaptor.value

    assertThat(capturedRule.minTime).isEqualTo(50)
    assertThat(capturedRule.name).isEqualTo("Test")
    assertThat(capturedRule.basicPoints).isEqualTo(3)
    assertThat(capturedRule.body).isEqualTo("This is a test body")
  }

  @Test

  fun findAll() {
  }

  @Test
  @Disabled
  fun getRuleRepository() {
  }
}
