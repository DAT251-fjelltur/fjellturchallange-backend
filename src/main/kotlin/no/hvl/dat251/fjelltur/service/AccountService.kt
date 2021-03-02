package no.hvl.dat251.fjelltur.service

import no.hvl.dat251.fjelltur.dto.AccountCreationRequest
import no.hvl.dat251.fjelltur.exception.AccountCreationFailedException
import no.hvl.dat251.fjelltur.exception.AccountNotFoundException
import no.hvl.dat251.fjelltur.exception.InsufficientAccessException
import no.hvl.dat251.fjelltur.exception.NotLoggedInException
import no.hvl.dat251.fjelltur.model.Account
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ExceptionHandler

interface AccountService {

  @ExceptionHandler(AccountCreationFailedException::class)
  fun createAccount(request: AccountCreationRequest): Account

  @ExceptionHandler(NotLoggedInException::class)
  fun getCurrentAccount(): Account

  fun getCurrentAccountOrNull(): Account?

  @get:ExceptionHandler(AccountNotFoundException::class)
  val loggedInUid: String

  val loggedInUidOrNull: String?

  val isLoggedIn: Boolean

  val isNotLoggedIn: Boolean

  @ExceptionHandler(UsernameNotFoundException::class)
  fun getAccountByUsername(username: String): Account

  fun getAccountByUsernameOrNull(username: String): Account?

  @ExceptionHandler(AccountNotFoundException::class)
  fun getAccountByUid(uid: String): Account

  fun getAccountByUidOrNull(uid: String): Account?

  @ExceptionHandler(NotLoggedInException::class, InsufficientAccessException::class)
  fun findAllByDisabled(disabled: Boolean, pageable: Pageable): Page<Account>

  @ExceptionHandler(NotLoggedInException::class, InsufficientAccessException::class)
  fun findAll(pageable: Pageable): Page<Account>

  /**
   * Update the specified user.
   */
  @ExceptionHandler(NotLoggedInException::class, InsufficientAccessException::class)
  fun updateUser(user: Account): Account

  /**
   * Remove the user with the given login name from the system.
   */
  @ExceptionHandler(NotLoggedInException::class, InsufficientAccessException::class)
  fun deleteUser(username: String)

  /**
   * Modify the current user's password. This should change the user's password in the
   * persistent user repository (database, LDAP etc).
   *
   * @param oldPassword current password (for re-authentication)
   * @param newPassword the password to change to
   */
  @ExceptionHandler(NotLoggedInException::class, InsufficientAccessException::class)
  fun changePassword(oldPassword: String, newPassword: String): Account

  /**
   * Check if a user with the supplied login name exists in the system.
   */
  @ExceptionHandler(NotLoggedInException::class, InsufficientAccessException::class)
  fun userExists(username: String): Boolean
}
