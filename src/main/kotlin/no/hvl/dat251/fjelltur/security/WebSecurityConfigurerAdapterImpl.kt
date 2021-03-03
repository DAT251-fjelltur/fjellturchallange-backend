package no.hvl.dat251.fjelltur.security

import no.hvl.dat251.fjelltur.API_VERSION_1
import no.hvl.dat251.fjelltur.controller.AccountController.Companion.ACCOUNTS_PATH
import no.hvl.dat251.fjelltur.controller.AccountController.Companion.LOGIN_PATH
import no.hvl.dat251.fjelltur.controller.AccountController.Companion.REGISTER_PATH
import no.hvl.dat251.fjelltur.security.config.SecurityProperty
import no.hvl.dat251.fjelltur.service.AccountService
import no.hvl.dat251.fjelltur.service.impl.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.POST
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = false)
class WebSecurityConfigurerAdapterImpl(
  @Autowired
  private val userDetailsService: UserDetailsServiceImpl,
  @Autowired
  private val passwordEncoder: PasswordEncoder,
  @Autowired
  private val accountService: AccountService,
  @Autowired
  private val securityProperty: SecurityProperty,
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
      .httpBasic().disable()
      .addFilter(JWTAuthenticationFilter(authenticationManager(), accountService, securityProperty))
      .addFilter(JWTAuthorizationFilter(authenticationManager(), securityProperty))
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
  }

  @Bean
  fun corsConfigurationSource(): CorsConfigurationSource {
    val source = UrlBasedCorsConfigurationSource()
    val corsConfiguration = CorsConfiguration().applyPermitDefaultValues()
    source.registerCorsConfiguration("/**", corsConfiguration)
    return source
  }
}
