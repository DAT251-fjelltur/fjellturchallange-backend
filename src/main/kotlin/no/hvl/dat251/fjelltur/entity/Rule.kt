package no.hvl.dat251.fjelltur.entity

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
abstract class Rule {

  @field: Id
  @field: GeneratedValue(generator = "system-uuid")
  @field:GenericGenerator(name = "system-uuid", strategy = "uuid")
  @field:Column(unique = true)
  var id: RuleId = RuleId("")

  @field:Column(updatable = false, insertable = false)
  lateinit var rule_type: String

  @field:Column
  var name: String? = null

  @field:Column(length = 1024)
  var body: String? = null

  @field: Column
  var basicPoints: Int? = 0

  abstract fun calculatePoints(trip: Trip): Int

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Rule) return false

    if (id != other.id) return false
    if (name != other.name) return false
    if (body != other.body) return false
    if (basicPoints != other.basicPoints) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + (name?.hashCode() ?: 0)
    result = 31 * result + (body?.hashCode() ?: 0)
    result = 31 * result + (basicPoints ?: 0)
    return result
  }
}
