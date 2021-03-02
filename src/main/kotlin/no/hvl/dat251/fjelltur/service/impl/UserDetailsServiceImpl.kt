package no.hvl.dat251.fjelltur.service.impl

import no.hvl.dat251.fjelltur.model.Account
import no.hvl.dat251.fjelltur.service.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
  @Autowired
  val accountService: AccountService
) : UserDetailsService {

  override fun loadUserByUsername(username: String?): UserDetails {

    if (username == null) {
      throw UsernameNotFoundException("Username must be specified")
    }

    val user: Account = accountService.getAccountByUsername(username)

    val builder = User.builder()
    builder.username(username)
    builder.password(user.password)
    builder.disabled(user.disabled)
    builder.authorities(*user.authorities.toTypedArray())

    return builder.build()
  }
}
