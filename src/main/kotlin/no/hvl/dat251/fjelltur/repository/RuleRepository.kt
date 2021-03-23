package no.hvl.dat251.fjelltur.repository

import no.hvl.dat251.fjelltur.model.Rule
import org.springframework.data.jpa.repository.JpaRepository

interface RuleRepository : JpaRepository<Rule, String> {
  fun findAllByName(name: String): Rule?
}
