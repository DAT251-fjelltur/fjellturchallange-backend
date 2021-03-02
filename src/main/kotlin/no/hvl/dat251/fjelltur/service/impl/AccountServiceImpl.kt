package no.hvl.dat251.fjelltur.service.impl

import no.hvl.dat251.fjelltur.dto.AccountCreationRequest
import no.hvl.dat251.fjelltur.exception.InsufficientAccessException
import no.hvl.dat251.fjelltur.exception.NotLoggedInException
import no.hvl.dat251.fjelltur.model.Account
import no.hvl.dat251.fjelltur.repository.AccountRepository
import no.hvl.dat251.fjelltur.service.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AccountServiceImpl(
//  @Autowired
//  private val securityService: SecurityService,
  @Autowired
  private val accountRepository: AccountRepository,
  @Autowired
  private val passwordEncoder: PasswordEncoder
) : AccountService {

  override fun createAccount(request: AccountCreationRequest): Account {
    val account = Account()
    account.password = passwordEncoder.encode(request.password)
    account.username = request.username
    account.photoUrl = request.photoUrl
    return accountRepository.saveAndFlush(account)
  }

  private fun Account.canNotUpdate(uid: String): Boolean {
    return disabled // || uid != this.id && this.role != ADMIN
  }

  private fun Account.isNotAdmin(): Boolean {
    return disabled // || this.role != ADMIN
  }

  override fun getCurrentAccount(): Account {
    return getCurrentAccountOrNull() ?: throw NotLoggedInException()
  }

  override fun getCurrentAccountOrNull(): Account? {
    TODO()
  }

  override val loggedInUid: String get() = loggedInUidOrNull ?: throw NotLoggedInException()
  override val loggedInUidOrNull: String? get() = TODO() // securityService.loggedInUid

  override val isLoggedIn: Boolean get() = loggedInUidOrNull != null
  override val isNotLoggedIn: Boolean get() = loggedInUidOrNull == null

  override fun getAccountByUsername(username: String): Account {
    return getAccountByUsernameOrNull(username) ?: throw UsernameNotFoundException("No user with the name $username")
  }

  override fun getAccountByUsernameOrNull(username: String): Account? {
    return accountRepository.findAccountByUsername(username)
  }

  override fun getAccountByUid(uid: String): Account {
    return accountRepository.findByIdOrNull(uid) ?: TODO() // return refreshAccount(uid)
  }

  override fun getAccountByUidOrNull(uid: String): Account? {
    return try {
      getAccountByUid(uid)
    } catch (e: Exception) {
      null
    }
  }

  private fun findAllAccounts(query: () -> Page<Account>): Page<Account> {
    if (getCurrentAccount().isNotAdmin()) {
      throw InsufficientAccessException("list accounts")
    }
    return query()
  }

  override fun findAllByDisabled(disabled: Boolean, pageable: Pageable) =
    findAllAccounts { accountRepository.findAllByDisabled(disabled, pageable) }

  override fun findAll(pageable: Pageable) =
    findAllAccounts { accountRepository.findAll(pageable) }

  override fun updateUser(user: Account): Account {
    if (user != getCurrentAccountOrNull()) throw InsufficientAccessException("update user")
    // TODO check that password, id, roles etc can be updated
    return accountRepository.saveAndFlush(user)
  }

  override fun deleteUser(username: String) {
    val account = getAccountByUsername(username)
    accountRepository.deleteById(account.id.toString())
  }

  override fun changePassword(oldPassword: String, newPassword: String): Account {
    val acc = getCurrentAccount()
    if (oldPassword != acc.password) throw error("not eql pass lol")
    acc.password = newPassword
    return accountRepository.saveAndFlush(acc)
  }

  override fun userExists(username: String): Boolean {
    return getAccountByUsernameOrNull(username) != null
  }
}
