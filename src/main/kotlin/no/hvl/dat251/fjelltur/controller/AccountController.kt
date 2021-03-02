package no.hvl.dat251.fjelltur.controller

import no.hvl.dat251.fjelltur.API_VERSION_1
import no.hvl.dat251.fjelltur.controller.AccountController.Companion.ACCOUNTS_PATH
import no.hvl.dat251.fjelltur.dto.AccountCreationRequest
import no.hvl.dat251.fjelltur.dto.LoginRequest
import no.hvl.dat251.fjelltur.dto.RegisteredAccountResponse
import no.hvl.dat251.fjelltur.dto.toResponse
import no.hvl.dat251.fjelltur.service.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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

    val obj = SecurityContextHolder.getContext().authentication.principal

    if (obj is UserDetails) {
      return accountService.getAccountByUsername(obj.username).toResponse()
    }

    throw error("principal is not UserDetails. but ${obj.javaClass}")
  }

  @PostMapping("/$REGISTER_PATH")
  fun register(@Valid @RequestBody request: AccountCreationRequest): RegisteredAccountResponse {
    return accountService.createAccount(request).toResponse()
  }

  @PostMapping("/$LOGIN_PATH")
  fun login(@Valid @RequestBody request: LoginRequest): String {
    val account = accountService.getAccountByUsernameOrNull(request.username)
      ?: return "Account '${request.username}' not found"
    return "Found account ${account.username}: id ${account.id}"
  }

  companion object {
    const val ACCOUNTS_PATH = "accounts"
    const val REGISTER_PATH = "register"
    const val LOGIN_PATH = "login"
  }
}
