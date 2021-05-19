package no.hvl.dat251.fjelltur.entity

import no.hvl.dat251.fjelltur.dto.TripId
import org.hibernate.annotations.GenericGenerator
import javax.persistence.CascadeType
import javax.persistence.CascadeType.ALL
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
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

  @field:ManyToOne(cascade = [ALL], optional = false)
  lateinit var account: Account

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Trip

    if (id != other.id) return false
    if (locations != other.locations) return false
    if (ongoing != other.ongoing) return false

    return true
  }

  /**
   * @return Current distance of this trip in meters
   */
  fun calculateDistance(): Int {
    return accumulatedDistance(locations)
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  override fun toString(): String {
    return "Trip(id=$id, locations=$locations, ongoing=$ongoing)"
  }

  companion object {
    fun accumulatedDistance(locations: List<GPSLocation>): Int {
      return locations.dropLast(1).withIndex().sumByDouble { (i, location) ->
        val next = locations[i + 1]
        return@sumByDouble location.distanceTo(next)
      }.toInt()
    }
  }
}
