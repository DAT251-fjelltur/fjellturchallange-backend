package no.hvl.dat251.fjelltur.security

import no.hvl.dat251.fjelltur.service.AccountService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@RunWith(SpringRunner::class)
@SpringBootTest
class SecurityTest {

  @Autowired
  private lateinit var accountService: AccountService

  @Autowired
  private lateinit var passwordEncoder: PasswordEncoder

  @WithMockUser(username = "test", password = "test2testet", authorities = ["test_auth", "ROLE_test"])
  @Test
  fun `account created when using WithMockUser`() {
    val account = assertNotNull(accountService.getCurrentAccountOrNull())
    assertEquals("test", account.username)
    assertTrue(passwordEncoder.matches("test2testet", account.password))
    assertEquals(setOf("test_auth", "ROLE_test"), account.authorities)
  }

  @WithMockUser
  @Test
  fun `same user is returned when getCurrentAccount`() {
    val account1 = accountService.getCurrentAccount()
    val account2 = accountService.getCurrentAccount()
    assertEquals(account1, account2)
  }
}
