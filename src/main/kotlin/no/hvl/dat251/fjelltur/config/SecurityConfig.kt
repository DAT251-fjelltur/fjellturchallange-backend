package no.hvl.dat251.fjelltur.config

import no.hvl.dat251.fjelltur.API_VERSION_1
import no.hvl.dat251.fjelltur.controller.AccountController.Companion.ACCOUNTS_PATH
import no.hvl.dat251.fjelltur.controller.AccountController.Companion.LOGIN_PATH
import no.hvl.dat251.fjelltur.controller.AccountController.Companion.REGISTER_PATH
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.POST
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = false, jsr250Enabled = true, prePostEnabled = false)
class SecurityConfig(
  @Qualifier("userDetailsServiceImpl")
  @Autowired
  private val userDetailsService: UserDetailsService,
  @Autowired
  private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {

  override fun configure(auth: AuthenticationManagerBuilder) {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
  }

  override fun configure(http: HttpSecurity) {
    http.authorizeRequests()
      .antMatchers(
        POST,
        "$API_VERSION_1/$ACCOUNTS_PATH/$REGISTER_PATH",
        "$API_VERSION_1/$ACCOUNTS_PATH/$LOGIN_PATH"
      ).permitAll()
      .anyRequest().authenticated().and()
      .logout().permitAll().and()
      .csrf().disable()
      .formLogin().disable()
      .httpBasic()
  }
}
