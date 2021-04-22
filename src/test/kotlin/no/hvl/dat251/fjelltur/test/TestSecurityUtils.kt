package no.hvl.dat251.fjelltur.test

import no.hvl.dat251.fjelltur.dto.AccountCreationRequest
import no.hvl.dat251.fjelltur.entity.Account
import no.hvl.dat251.fjelltur.service.AccountService

object TestSecurityUtils {

  /**
   * Shortcut for [AccountService.createAccount]
   */
  fun AccountService.createNewTestAccount(username: String, password: String): Account {
    return createAccount(AccountCreationRequest(username, password, null))
  }
}
