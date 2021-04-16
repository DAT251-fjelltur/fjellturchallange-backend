package no.hvl.dat251.fjelltur.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import no.hvl.dat251.fjelltur.API_VERSION_1
import no.hvl.dat251.fjelltur.controller.AccountController.Companion.ACCOUNTS_PATH
import no.hvl.dat251.fjelltur.dto.AccountCreationRequest
import no.hvl.dat251.fjelltur.dto.AccountId
import no.hvl.dat251.fjelltur.dto.RegisteredAccountResponse
import no.hvl.dat251.fjelltur.dto.UpdatePasswordRequest
import no.hvl.dat251.fjelltur.dto.toResponse
import no.hvl.dat251.fjelltur.exception.MissingFieldException
import no.hvl.dat251.fjelltur.service.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotNull
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody

/**
 * @author Elg
 */
@RestController
@RequestMapping(
  "$API_VERSION_1/$ACCOUNTS_PATH",
  produces = [MediaType.APPLICATION_JSON_VALUE]
)
class AccountController(@Autowired val accountService: AccountService) {

  @GetMapping("/me")
  fun getMe(): RegisteredAccountResponse {
    return accountService.getCurrentAccount().toResponse()
  }

  @GetMapping("/list")
  fun getAll(page: Pageable): Page<RegisteredAccountResponse> {
    return accountService.findAll(page).map { it.toResponse() }
  }

  @Operation(
    summary = "Find an account either by internal id or username",
    description = "If both username and id are given id is prioritized over the username",
    parameters = [
      Parameter(name = "username", description = "Username of account"),
      Parameter(name = "id", description = "Internal id of account"),
    ],
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(responseCode = "400", description = "If all parameters are missing", content = [Content()]),
      ApiResponse(responseCode = "404", description = "An account with the id/username is not found", content = [Content()]),
    ]
  )
  @GetMapping
  fun getOtherByUsername(
    @RequestParam("id") id: AccountId?,
    @RequestParam("username") username: String?
  ): RegisteredAccountResponse {
    if (id != null) {
      return accountService.getAccountByUid(id).toResponse()
    } else if (username != null) {
      return accountService.getAccountByUsername(username).toResponse()
    }
    throw MissingFieldException("id and username")
  }

  @Operation(
    summary = "set a users permissions to the given list of permissions",
    parameters = [Parameter(name = "id", description = "Internal id of account")],
    requestBody = SwaggerRequestBody(description = "List of permissions the account should have"),
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(responseCode = "403", description = "You are not allowed to edit permissions", content = [Content()]),
      ApiResponse(responseCode = "404", description = "An account with the id is not found", content = [Content()]),
    ]
  )
  @PutMapping("/permissions", consumes = [MediaType.APPLICATION_JSON_VALUE])
  fun setPermissions(@RequestParam("id") id: AccountId, @Valid @RequestBody permissions: List<@NotNull String>): MutableSet<String> {
    val account = accountService.getAccountByUid(id)
    account.authorities.clear()
    account.authorities.addAll(permissions)
    val updatedAccount = accountService.updateUser(account)
    return updatedAccount.authorities
  }

  @Operation(
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(responseCode = "400", description = "Failed to create account, see attached message", content = [Content()]),
      ApiResponse(responseCode = "418", description = "Password is not secure", content = [Content()]),
    ]
  )
  @PostMapping("/$REGISTER_PATH", consumes = [MediaType.APPLICATION_JSON_VALUE])
  fun register(@Valid @RequestBody request: AccountCreationRequest): RegisteredAccountResponse {
    return accountService.createAccount(request).toResponse()
  }

  @Operation(
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(responseCode = "403", description = "Old password is not correct", content = [Content()]),
      ApiResponse(responseCode = "418", description = "Password is not secure", content = [Content()]),
    ]
  )
  @PutMapping("/update_password", consumes = [MediaType.APPLICATION_JSON_VALUE])
  fun updatePassword(@Valid @RequestBody request: UpdatePasswordRequest) {
    accountService.changePassword(request.oldPassword, request.newPassword)
  }

  companion object {
    const val ACCOUNTS_PATH = "accounts"
    const val REGISTER_PATH = "register"
    const val LOGIN_PATH = "login"
  }
}
