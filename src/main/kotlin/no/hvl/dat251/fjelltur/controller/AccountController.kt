package no.hvl.dat251.fjelltur.controller

import no.hvl.dat251.fjelltur.API_VERSION_1
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * @author Elg
 */
@RestController
@RequestMapping("$API_VERSION_1/accounts")
class AccountController {

  @GetMapping("/me")
  fun getMe(@RequestParam(value = "name", defaultValue = "World") name: String): String {
    return "Hello my friend, $name !"
  }
}
