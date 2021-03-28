package no.hvl.dat251.fjelltur.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.hvl.dat251.fjelltur.API_VERSION_1
import no.hvl.dat251.fjelltur.controller.AccountController.Companion.ACCOUNTS_PATH
import no.hvl.dat251.fjelltur.controller.AccountController.Companion.LOGIN_PATH
import no.hvl.dat251.fjelltur.dto.LoginRequest
import no.hvl.dat251.fjelltur.security.config.SecurityProperty
import no.hvl.dat251.fjelltur.service.AccountService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.time.Duration
import java.time.Instant
import java.util.ArrayList
import java.util.Date
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(
  private val authenticationManager: AuthenticationManager,
  private val accountService: AccountService,
  private val securityProperty: SecurityProperty
) : UsernamePasswordAuthenticationFilter() {

  @Throws(AuthenticationException::class)
  override fun attemptAuthentication(
    req: HttpServletRequest,
    res: HttpServletResponse
  ): Authentication {
    return try {
      val creds: LoginRequest = mapper.readValue(req.inputStream)
      authenticationManager.authenticate(UsernamePasswordAuthenticationToken(creds.username, creds.password, ArrayList()))
    } catch (e: IOException) {
      throw RuntimeException(e)
    }
  }

  @Throws(IOException::class)
  override fun successfulAuthentication(
    req: HttpServletRequest?,
    res: HttpServletResponse,
    chain: FilterChain?,
    auth: Authentication
  ) {
    val principal = auth.principal
    require(principal is UserDetails) { "Wrong type for principal: $principal (class ${principal::class})" }

    val account = accountService.getAccountByUsername(principal.username)

    val token: String = JWT.create()
      .withSubject(account.id.id)
      .withExpiresAt(Date.from(Instant.now().plus(Duration.ofDays(securityProperty.maxAgeInDays))))
      .sign(Algorithm.HMAC512(securityProperty.secretSigningKey.toByteArray()))

    res.writer.write(mapper.writeValueAsString(mapOf("jwt" to token)))
    res.writer.flush()
  }

  override fun getAuthenticationManager(): AuthenticationManager {
    return authenticationManager
  }

  init {
    setFilterProcessesUrl("$API_VERSION_1/$ACCOUNTS_PATH/$LOGIN_PATH")
  }

  companion object {
    val mapper = jacksonObjectMapper()
  }
}
