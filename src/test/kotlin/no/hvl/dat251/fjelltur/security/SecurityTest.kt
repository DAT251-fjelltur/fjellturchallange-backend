package no.hvl.dat251.fjelltur.security

import no.hvl.dat251.fjelltur.service.AccountService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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

  @WithMockUser(username = "test")
  @Test
  fun `User with the same name does not exist in multiple test methods`() {
    // Yeah its kinda meta
    val account = assertNotNull(accountService.getCurrentAccountOrNull())
    assertFalse(passwordEncoder.matches("test2testet", account.password))
  }
}
