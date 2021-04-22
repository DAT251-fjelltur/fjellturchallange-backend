package no.hvl.dat251.fjelltur.entity

import no.hvl.dat251.fjelltur.dto.AccountId
import org.hibernate.annotations.GenericGenerator
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Account {

  @field:Id
  @field:GeneratedValue(generator = "system-uuid")
  @field:GenericGenerator(name = "system-uuid", strategy = "uuid")
  @field:Column(unique = true)
  var id: AccountId = AccountId("")

  @field:Column(unique = true, nullable = false)
  lateinit var username: String

  @field:Column(nullable = false)
  lateinit var password: String

  /**
   * What this user is allowed to do
   */
  @field:ElementCollection(fetch = EAGER)
  var authorities: MutableSet<String> = mutableSetOf()

  var photoUrl: String? = null

  var disabled: Boolean = false

  var score: Float = 0f

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Account

    if (id != other.id) return false
    if (authorities != other.authorities) return false
    if (username != other.username) return false
    if (photoUrl != other.photoUrl) return false
    if (disabled != other.disabled) return false
    if (score != other.score) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + authorities.hashCode()
    result = 31 * result + username.hashCode()
    result = 31 * result + (photoUrl?.hashCode() ?: 0)
    result = 31 * result + disabled.hashCode()
    result = 31 * result + score.hashCode()
    return result
  }
}
