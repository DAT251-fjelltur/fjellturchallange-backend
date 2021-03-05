package no.hvl.dat251.fjelltur.repository

import no.hvl.dat251.fjelltur.model.Account
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<Account, String> {

  fun findAllByDisabled(disabled: Boolean, pageable: Pageable): Page<Account>

  fun findAccountByUsername(username: String): Account?
}
