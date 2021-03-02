package no.hvl.dat251.fjelltur.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import no.hvl.dat251.fjelltur.security.JWTAuthenticationFilter.Companion.SECRET
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(authManager: AuthenticationManager) : BasicAuthenticationFilter(authManager) {

  @Override
  override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
    val header = req.getHeader(HEADER_STRING)

    if (header == null || !header.startsWith(TOKEN_PREFIX)) {
      chain.doFilter(req, res)
      return
    }

    val authentication = getAuthentication(req)

    SecurityContextHolder.getContext().authentication = authentication
    chain.doFilter(req, res)
  }

  // Reads the JWT from the Authorization header, and then uses JWT to validate the token
  private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
    val token = request.getHeader(HEADER_STRING)

    if (token != null) {
      // parse the token.
      val user = JWT.require(Algorithm.HMAC512(SECRET.toByteArray()))
        .build().verify(token.replace(TOKEN_PREFIX, "")).subject

      if (user != null) {
        // new arraylist means authorities
        return UsernamePasswordAuthenticationToken(user, null, ArrayList())
      }

      return null
    }

    return null
  }

  companion object {
    const val TOKEN_PREFIX = "Bearer "
    const val HEADER_STRING = "Authorization"
  }
}
