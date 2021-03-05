package no.hvl.dat251.fjelltur

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

const val API_VERSION_1 = "/api/v1"

@SpringBootApplication
class Application {

  @Bean
  fun passwordEncoder(): BCryptPasswordEncoder {
    return BCryptPasswordEncoder()
  }
}

fun main(args: Array<String>) {
  runApplication<Application>(*args)
}
