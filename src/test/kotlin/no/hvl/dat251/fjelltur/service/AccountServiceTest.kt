package no.hvl.dat251.fjelltur.service

import no.hvl.dat251.fjelltur.ADMIN_ROLE
import no.hvl.dat251.fjelltur.UPDATE_ACCOUNT_AUTHORITIES_PERMISSION
import no.hvl.dat251.fjelltur.UPDATE_OTHER_ACCOUNT_PERMISSION
import no.hvl.dat251.fjelltur.entity.Account
import no.hvl.dat251.fjelltur.exception.AccountUpdateFailedException
import no.hvl.dat251.fjelltur.exception.InsufficientAccessException
import no.hvl.dat251.fjelltur.test.TestSecurityUtils.createNewTestAccount
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@SpringBootTest
class AccountServiceTest {

  @Autowired
  private lateinit var accountService: AccountService

  private fun testCanUpdateBasic(account: Account) {

    val newPhotoUrl = "wow"
    assertNotEquals(newPhotoUrl, account.photoUrl)
    account.photoUrl = newPhotoUrl
    assertDoesNotThrow { accountService.updateUser(account) }
    assertEquals(newPhotoUrl, accountService.getAccountByUid(account.id).photoUrl)
  }

  private fun testCanUpdateAuthority(account: Account) {

    val newAuthority = "White coat with clipboard"
    assertFalse(newAuthority in account.authorities)

    account.authorities.add(newAuthority)
    assertDoesNotThrow { accountService.updateUser(account) }

    assertTrue(newAuthority in accountService.getAccountByUid(account.id).authorities)
  }

  private fun testCanNotUpdateAuthority(account: Account) {

    val newAuthority = "Beggar"
    assertFalse(newAuthority in account.authorities)

    account.authorities.add(newAuthority)
    assertThrows<InsufficientAccessException> { accountService.updateUser(account) }

    assertFalse(newAuthority in accountService.getAccountByUid(account.id).authorities)
  }

  // UPDATE BASIC SELF //

  @WithMockUser(username = "AccountServiceTest_updateOwnAccount")
  @Test
  fun `can update own account`() {
    val curr = accountService.getCurrentAccount()
    testCanUpdateBasic(curr)
  }

  // UPDATE BASIC OTHER //

  @WithMockUser(username = "AccountServiceTest_updateOtherAccount_noAuthority")
  @Test
  fun `can not update other accounts without authority`() {
    val other = accountService.createNewTestAccount("AccountServiceTest_updateOtherAccount_test")
    assertThrows<InsufficientAccessException> { accountService.updateUser(other) }
  }

  @WithMockUser(username = "AccountServiceTest_updateOtherAccount_asAdmin", authorities = [ADMIN_ROLE])
  @Test
  fun `can update other accounts as admin`() {
    val other = accountService.createNewTestAccount("AccountServiceTest_updateOtherAccount_test_by_admin")
    testCanUpdateBasic(other)
  }

  @WithMockUser(username = "AccountServiceTest_updateOtherAccount_withPerms", authorities = [UPDATE_OTHER_ACCOUNT_PERMISSION])
  @Test
  fun `can update other accounts basic with authority`() {
    val other = accountService.createNewTestAccount("AccountServiceTest_updateOtherAccount_test_with_authorities")
    testCanUpdateBasic(other)
  }

  // UPDATE OWN PERMISSIONS //

  @WithMockUser(username = "AccountServiceTest_updateAuthority_asRegularUser")
  @Test
  fun `can not update authorities without having correct authority`() {
    val curr = accountService.getCurrentAccount()
    testCanNotUpdateAuthority(curr)
  }

  @WithMockUser(username = "AccountServiceTest_updateAuthority_asAdmin", authorities = [ADMIN_ROLE])
  @Test
  fun `can update authorities when admin`() {
    val curr = accountService.getCurrentAccount()
    testCanUpdateAuthority(curr)
  }

  @WithMockUser(username = "AccountServiceTest_updateAuthority_withAuthPerm", authorities = [UPDATE_ACCOUNT_AUTHORITIES_PERMISSION])
  @Test
  fun `can update own authorities by having only update authority`() {
    val curr = accountService.getCurrentAccount()
    testCanUpdateAuthority(curr)
  }

  @WithMockUser(
    username = "AccountServiceTest_updateOwnAuthority_withBothPerms",
    authorities = [UPDATE_OTHER_ACCOUNT_PERMISSION, UPDATE_ACCOUNT_AUTHORITIES_PERMISSION]
  )
  @Test
  fun `can update authorities by having update authority and update basic`() {
    val curr = accountService.getCurrentAccount()
    testCanUpdateAuthority(curr)
  }

  // UPDATE OTHERS PERMISSIONS //

  @WithMockUser(username = "AccountServiceTest_updateOthersAuthority_withAuthPermOnly", authorities = [UPDATE_ACCOUNT_AUTHORITIES_PERMISSION])
  @Test
  fun `can not update others authorities by having only update authority`() {
    val other = accountService.createNewTestAccount("AccountServiceTest_updateOthersAuthority_withAuthPermOnly_test")
    testCanNotUpdateAuthority(other)
  }

  @WithMockUser(username = "AccountServiceTest_updateOthersAuthority_basicPermOnly", authorities = [UPDATE_OTHER_ACCOUNT_PERMISSION])
  @Test
  fun `can not update others authorities with basic authority`() {
    val other = accountService.createNewTestAccount("AccountServiceTest_updateOthersAuthority_basicPermOnly_test")
    testCanNotUpdateAuthority(other)
  }

  @WithMockUser(
    username = "AccountServiceTest_updateOthersAuthority_bothPerms",
    authorities = [UPDATE_OTHER_ACCOUNT_PERMISSION, UPDATE_ACCOUNT_AUTHORITIES_PERMISSION]
  )
  @Test
  fun `can update authorities of others by having update authority and update basic`() {
    val other = accountService.createNewTestAccount("AccountServiceTest_updateOthersAuthority_bothPerms_test")
    testCanUpdateAuthority(other)
  }

  @WithMockUser(
    username = "AccountServiceTest_updateOthersAuthority_admin",
    authorities = [UPDATE_OTHER_ACCOUNT_PERMISSION, UPDATE_ACCOUNT_AUTHORITIES_PERMISSION]
  )
  @Test
  fun `can update authorities of others by being admin`() {
    val other = accountService.createNewTestAccount("AccountServiceTest_updateOthersAuthority_admin_test")
    testCanUpdateAuthority(other)
  }

  // USERNAME CHECKS //

  @WithMockUser(username = "AccountServiceTest_updateUserName_taken")
  @Test
  fun `can not update username to a taken username`() {

    val curr = accountService.getCurrentAccount()
    val takenName = "blablabla"
    accountService.createNewTestAccount(takenName)
    curr.username = takenName
    assertThrows<AccountUpdateFailedException> { accountService.updateUser(curr) }
    assertNotEquals(takenName, accountService.getCurrentAccount().username)
  }

  @WithMockUser(username = "AccountServiceTest_updateUserName_illegal")
  @Test
  fun `can not update username to an illegal username`() {

    val curr = accountService.getCurrentAccount()
    val illegalName = "  blablabla"

    assertFalse(AccountService.USERNAME_REGEX.matches(illegalName))

    curr.username = illegalName
    assertThrows<AccountUpdateFailedException> { accountService.updateUser(curr) }
    assertNotEquals(illegalName, accountService.getCurrentAccount().username)
  }

  // PASSWORD //

  @WithMockUser(username = "AccountServiceTest_tryUpdatePassword")
  @Test
  fun `can not update password in updateAccount`() {
    val curr = accountService.getCurrentAccount()
    val illegal = "blablabla"
    curr.password = illegal
    assertThrows<AccountUpdateFailedException> { accountService.updateUser(curr) }
    assertNotEquals(illegal, accountService.getCurrentAccount().password)
  }
}
