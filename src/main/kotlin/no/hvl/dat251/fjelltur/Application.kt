package no.hvl.dat251.fjelltur

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

const val API_VERSION_1 = "/api/v1"

@SpringBootApplication
class Application

fun main(args: Array<String>) {
  runApplication<Application>(*args)
}
