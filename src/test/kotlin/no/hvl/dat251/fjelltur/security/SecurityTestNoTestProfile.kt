package no.hvl.dat251.fjelltur.security

import no.hvl.dat251.fjelltur.service.AccountService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("faketest")
@ExtendWith(SpringExtension::class)
@SpringBootTest
class SecurityTestNoTestProfile {

  @Autowired
  private lateinit var accountService: AccountService

  @WithMockUser(username = "test")
  @Test
  fun `Throw before test when having @WithMockUser, getting current profile and not using test profile`() {
    assertThrows<IllegalStateException> { accountService.loggedInUidOrNull }
  }
}
