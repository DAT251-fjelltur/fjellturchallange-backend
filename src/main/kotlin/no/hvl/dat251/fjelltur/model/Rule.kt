package no.hvl.dat251.fjelltur.model

import no.hvl.dat251.fjelltur.dto.RuleId
import org.hibernate.annotations.GenericGenerator
import javax.persistence.Column
import javax.persistence.DiscriminatorColumn
import javax.persistence.DiscriminatorType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "rule_type", discriminatorType = DiscriminatorType.STRING)
class Rule {

  @field: Id
  @field: GeneratedValue(generator = "system-uuid")
  @field:GenericGenerator(name = "system-uuid", strategy = "uuid")
  @field:Column(unique = true)
  var id: RuleId = RuleId("")

  @field:Column
  var name: String? = null

  @field:Column
  var body: String? = null

  @field: Column
  var basicPoints: Int? = 0
}
