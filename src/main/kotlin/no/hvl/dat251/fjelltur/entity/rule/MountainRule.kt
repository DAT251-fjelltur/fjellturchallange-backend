package no.hvl.dat251.fjelltur.entity.rule

import no.hvl.dat251.fjelltur.dto.MOUNTAIN_RULE
import no.hvl.dat251.fjelltur.entity.GPSLocation
import no.hvl.dat251.fjelltur.entity.Rule
import no.hvl.dat251.fjelltur.entity.Trip
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue(MOUNTAIN_RULE)
class MountainRule : Rule() {

  @field:javax.persistence.ManyToOne
  var summit: GPSLocation? = null

  var summitRadiusMeters: Int = 0

  var minMetersTraveled: Int = 0

  override fun calculatePoints(trip: Trip): Int {

    val summit = requireNotNull(summit) { "No summit found" }
    val basicPoints = requireNotNull(basicPoints) { "No basicPoints found" }

    val summitIndex = trip.locations.indexOfFirst { summit.distanceTo(it) <= summitRadiusMeters }
    if (summitIndex == -1) {
      // Did not walk within the summit of this mountain
      return 0
    }

    val distanceUp = Trip.accumulatedDistance(trip.locations.take(summitIndex + 1))
    if (distanceUp < minMetersTraveled) {
      // travelled less than the trip requires
      return 0
    }

    val distanceDown = Trip.accumulatedDistance(trip.locations.drop(summitIndex))
    if (distanceDown < minMetersTraveled) {
      // travelled less than the trip requires
      return 0
    }

    return basicPoints
  }
}
