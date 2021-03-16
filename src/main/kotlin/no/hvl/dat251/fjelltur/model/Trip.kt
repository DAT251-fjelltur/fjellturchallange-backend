package no.hvl.dat251.fjelltur.model

import no.hvl.dat251.fjelltur.dto.TripId
import org.hibernate.annotations.GenericGenerator
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
import javax.persistence.OrderBy

@Entity
class Trip {

  @field:Id
  @field:GeneratedValue(generator = "system-uuid")
  @field:GenericGenerator(name = "system-uuid", strategy = "uuid")
  @field:Column(unique = true)
  var id: TripId = TripId("")

  @field:OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = EAGER)
  @field:JoinColumn(name = "trip_id")
  @field:OrderBy("recordedAt ASC")
  var locations: MutableList<GPSLocation> = mutableListOf()

  var ongoing: Boolean = true

  @field:ManyToMany(cascade = [CascadeType.ALL])
  var participants: MutableSet<Account> = mutableSetOf()

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Trip

    if (id != other.id) return false
    if (locations != other.locations) return false
    if (ongoing != other.ongoing) return false

    return true
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  override fun toString(): String {
    return "Trip(id=$id, locations=$locations, ongoing=$ongoing)"
  }
}
