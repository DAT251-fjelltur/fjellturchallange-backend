package no.hvl.dat251.fjelltur.service.impl

import no.hvl.dat251.fjelltur.dto.AccountCreationRequest
import no.hvl.dat251.fjelltur.dto.AccountId
import no.hvl.dat251.fjelltur.exception.AccountCreationFailedException
import no.hvl.dat251.fjelltur.exception.AccountNotFoundException
import no.hvl.dat251.fjelltur.exception.AccountUpdateFailedException
import no.hvl.dat251.fjelltur.exception.InvalidCredentialsException
import no.hvl.dat251.fjelltur.exception.NotLoggedInException
import no.hvl.dat251.fjelltur.exception.PasswordNotSecureException
import no.hvl.dat251.fjelltur.model.Account
import no.hvl.dat251.fjelltur.repository.AccountRepository
import no.hvl.dat251.fjelltur.service.AccountService
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

  override fun createAccount(request: AccountCreationRequest): Account {
    if (getAccountByUsernameOrNull(request.username) != null) {
      throw AccountCreationFailedException("Account name already taken")
    }
    if (!USERNAME_REGEX.matches(request.username)) {
      throw AccountCreationFailedException("Account username must match the regex: ${USERNAME_REGEX.pattern}")
    }

    if (request.password.length < MIN_PASSWORD_LENGTH) {
      throw PasswordNotSecureException("${request.username} your password is too short")
    }

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
          check("test" in environment.activeProfiles) { "Authentication principal cannot be a UserDetails when the 'test' profile is not active" }
          val existingAccount = getAccountByUsernameOrNull(principal.username)
          if (existingAccount != null) {
            // This persist between tests
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

  override fun updateUser(user: Account): Account {
    val uid = user.id

    if (user.password != getAccountByUid(uid).password) {
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
    if (newPassword.length < MIN_PASSWORD_LENGTH) {
      throw PasswordNotSecureException("${acc.username} your password is too short")
    }

    if (!passwordEncoder.matches(oldPassword, acc.password)) throw InvalidCredentialsException("${acc.username} your password do not match current password")
    acc.password = passwordEncoder.encode(newPassword)
    return accountRepository.saveAndFlush(acc)
  }

  override fun userExists(username: String): Boolean {
    return getAccountByUsernameOrNull(username) != null
  }

  companion object {
    /** Account username must match this regex */
    val USERNAME_REGEX = "^[a-zA-Z0-9ÆØÅæøå_-]+$".toRegex()
    val MIN_PASSWORD_LENGTH = 8
  }
}
