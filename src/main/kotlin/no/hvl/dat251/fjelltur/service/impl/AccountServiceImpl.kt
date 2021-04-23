package no.hvl.dat251.fjelltur.service.impl

import no.hvl.dat251.fjelltur.ADMIN_ROLE
import no.hvl.dat251.fjelltur.UPDATE_OTHER_USER_BASIC
import no.hvl.dat251.fjelltur.UPDATE_OTHER_USER_PERMISSION
import no.hvl.dat251.fjelltur.dto.AccountCreationRequest
import no.hvl.dat251.fjelltur.dto.AccountId
import no.hvl.dat251.fjelltur.entity.Account
import no.hvl.dat251.fjelltur.exception.AccountCreationFailedException
import no.hvl.dat251.fjelltur.exception.AccountNotFoundException
import no.hvl.dat251.fjelltur.exception.AccountUpdateFailedException
import no.hvl.dat251.fjelltur.exception.InsufficientAccessException
import no.hvl.dat251.fjelltur.exception.InvalidCredentialsException
import no.hvl.dat251.fjelltur.exception.NotLoggedInException
import no.hvl.dat251.fjelltur.exception.PasswordNotSecureException
import no.hvl.dat251.fjelltur.repository.AccountRepository
import no.hvl.dat251.fjelltur.service.AccountService
import no.hvl.dat251.fjelltur.service.AccountService.Companion.MIN_PASSWORD_LENGTH
import no.hvl.dat251.fjelltur.service.AccountService.Companion.USERNAME_REGEX
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AccountServiceImpl(
  @Autowired
  private val accountRepository: AccountRepository,
  @Autowired
  private val passwordEncoder: PasswordEncoder,
  @Autowired
  private val environment: Environment
) : AccountService {

  private fun testUsername(username: String): String? {
    if (getAccountByUsernameOrNull(username) != null) {
      return ("Account name already taken")
    }
    if (!USERNAME_REGEX.matches(username)) {
      return ("Account username must match the regex: ${USERNAME_REGEX.pattern}")
    }
    return null
  }

  private fun testPassword(password: String) {
    if (password.length < MIN_PASSWORD_LENGTH) {
      throw PasswordNotSecureException("Given password is too short. It must be at least $MIN_PASSWORD_LENGTH characters long")
    }
  }

  override fun createAccount(request: AccountCreationRequest): Account {
    testUsername(request.username)?.also {
      throw AccountCreationFailedException(it)
    }

    testPassword(request.password)

    val account = Account()
    account.password = passwordEncoder.encode(request.password)
    account.username = request.username
    account.photoUrl = request.photoUrl
    return accountRepository.saveAndFlush(account)
  }

  override fun getCurrentAccount(): Account {
    return getCurrentAccountOrNull() ?: throw NotLoggedInException()
  }

  override fun getCurrentAccountOrNull(): Account? {
    val principal = loggedInUid
    return getAccountByUidOrNull(principal)
  }

  override val loggedInUid: AccountId get() = loggedInUidOrNull ?: throw NotLoggedInException()

  override val loggedInUidOrNull: AccountId?
    get() {
      return when (val principal = SecurityContextHolder.getContext().authentication.principal) {
        is String -> AccountId(principal)
        is UserDetails -> {
          check("test" in environment.activeProfiles) {
            "Authentication principal cannot be a UserDetails when the 'test' profile is not active"
          }
          val existingAccount = getAccountByUsernameOrNull(principal.username)
          if (existingAccount != null) {
            // Accounts persist between tests
            // one fix is to add `@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)` to the test class
            return existingAccount.id
          }
          val account = createAccount(AccountCreationRequest(principal.username, principal.password, null))
          account.authorities.addAll(principal.authorities.map { it.authority })
          return accountRepository.saveAndFlush(account).id
        }
        else -> error("Authentication principal is not a account id or a UserDetails. It is $principal")
      }
    }

  override val isLoggedIn: Boolean get() = loggedInUidOrNull != null
  override val isNotLoggedIn: Boolean get() = loggedInUidOrNull == null

  override fun getAccountByUsername(username: String): Account {
    return getAccountByUsernameOrNull(username)
      ?: throw UsernameNotFoundException("No user with the username $username")
  }

  override fun getAccountByUsernameOrNull(username: String): Account? {
    return accountRepository.findAccountByUsername(username)
  }

  override fun getAccountByUid(uid: AccountId): Account {
    return getAccountByUidOrNull(uid) ?: throw AccountNotFoundException(uid)
  }

  override fun getAccountByUidOrNull(uid: AccountId): Account? {
    return accountRepository.findByIdOrNull(uid.id)
  }

  private fun findAllAccounts(query: () -> Page<Account>): Page<Account> {
    return query()
  }

  override fun findAllByDisabled(disabled: Boolean, pageable: Pageable) =
    findAllAccounts { accountRepository.findAllByDisabled(disabled, pageable) }

  override fun findAll(pageable: Pageable) = findAllAccounts { accountRepository.findAll(pageable) }

  // TODO make sure user only updates permitted fields
  // TODO test
  override fun updateUser(user: Account): Account {
    val uid = user.id
    val loggedIn = getCurrentAccount()
    val isNotAdmin = ADMIN_ROLE !in loggedIn.authorities
    if (uid != loggedInUid && isNotAdmin && UPDATE_OTHER_USER_BASIC !in loggedIn.authorities) {
      throw InsufficientAccessException("update others accounts")
    }

    val updatingUser = getAccountByUid(uid)
    if (user.authorities != updatingUser.authorities && isNotAdmin && UPDATE_OTHER_USER_PERMISSION !in loggedIn.authorities) {
      throw InsufficientAccessException("update permissions of accounts")
    }

    if (user.username != updatingUser.username) {
      testUsername(user.username)?.also {
        throw AccountUpdateFailedException(it)
      }
    }

    if (user.password != updatingUser.password) {
      throw AccountUpdateFailedException("Use dedicated method to update password")
    }
    return accountRepository.saveAndFlush(user)
  }

  // TODO Test
  override fun deleteUser(username: String) {
    val account = getAccountByUsername(username)
    accountRepository.deleteById(account.id.id)
  }

  // TODO Test
  override fun changePassword(oldPassword: String, newPassword: String): Account {
    val acc = getCurrentAccount()

    if (!passwordEncoder.matches(oldPassword, acc.password)) {
      throw InvalidCredentialsException("${acc.username} your password do not match current password")
    }
    testPassword(newPassword)

    acc.password = passwordEncoder.encode(newPassword)
    return accountRepository.saveAndFlush(acc)
  }

  override fun userExists(username: String): Boolean {
    return getAccountByUsernameOrNull(username) != null
  }
}
