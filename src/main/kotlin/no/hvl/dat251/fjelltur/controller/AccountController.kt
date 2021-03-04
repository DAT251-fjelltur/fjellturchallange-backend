package no.hvl.dat251.fjelltur.controller

import no.hvl.dat251.fjelltur.ADMIN_ROLE
import no.hvl.dat251.fjelltur.API_VERSION_1
import no.hvl.dat251.fjelltur.GET_OTHER_PERMISSION
import no.hvl.dat251.fjelltur.controller.AccountController.Companion.ACCOUNTS_PATH
import no.hvl.dat251.fjelltur.dto.AccountCreationRequest
import no.hvl.dat251.fjelltur.dto.RegisteredAccountResponse
import no.hvl.dat251.fjelltur.dto.toResponse
import no.hvl.dat251.fjelltur.exception.AccountCreationFailedException
import no.hvl.dat251.fjelltur.exception.MissingFieldException
import no.hvl.dat251.fjelltur.service.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

/**
 * @author Elg
 */
@RestController
@RequestMapping("$API_VERSION_1/$ACCOUNTS_PATH")
class AccountController(@Autowired val accountService: AccountService) {

  @GetMapping("/me")
  fun getMe(): RegisteredAccountResponse {
    return accountService.getCurrentAccount().toResponse()
  }

  @GetMapping("/list")
  fun getAll(page: Pageable): Page<RegisteredAccountResponse> {
    return accountService.findAll(page).map { it.toResponse() }
  }

  @PreAuthorize("hasAuthority('$GET_OTHER_PERMISSION') or hasRole('$ADMIN_ROLE')")
  @GetMapping
  fun getOtherByUsername(
    @RequestParam("id") id: String?,
    @RequestParam("username") username: String?
  ): RegisteredAccountResponse {
    if (id != null) {
      return accountService.getAccountByUid(id).toResponse()
    } else if (username != null) {
      return accountService.getAccountByUsername(username).toResponse()
    }
    throw MissingFieldException("id and username")
  }

  @GetMapping("/username")
  fun getOtherById(@RequestParam("username") username: String): RegisteredAccountResponse {
    return accountService.getAccountByUsername(username).toResponse()
  }

  @PutMapping("/permissions")
  fun setPermissions(@RequestParam("id") id: String, @RequestBody permissions: List<String>): MutableSet<String> {
    val account = accountService.getAccountByUid(id)
    account.authorities.clear()
    account.authorities.addAll(permissions)
    val updatedAccount = accountService.updateUser(account)
    return updatedAccount.authorities
  }

  @PostMapping("/$REGISTER_PATH")
  fun register(@Valid @RequestBody request: AccountCreationRequest): RegisteredAccountResponse {
    if (accountService.getAccountByUsernameOrNull(request.username) != null) {
      throw AccountCreationFailedException("Account name already taken")
    }

    return accountService.createAccount(request).toResponse()
  }

  companion object {
    const val ACCOUNTS_PATH = "accounts"
    const val REGISTER_PATH = "register"
    const val LOGIN_PATH = "login"
  }
}
