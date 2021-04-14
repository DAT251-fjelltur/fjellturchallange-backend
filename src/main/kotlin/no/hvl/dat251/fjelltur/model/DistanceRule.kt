package no.hvl.dat251.fjelltur.model

import no.hvl.dat251.fjelltur.dto.DISTANCERULE
import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue(DISTANCERULE)
class DistanceRule : Rule() {
  @field:Column
  var minKilometers: Int? = 0

  override fun calculatePoints(trip: Trip): Int {
    require(!trip.ongoing) { "Cannot calculate the points of an ongoing trip" }

    val locations = trip.locations
    val minKilometers = requireNotNull(minKilometers) { "No minimum distance in kilometers found" }
    val pointsPerTime = requireNotNull(basicPoints) { "No basicPoints found" }

    val totalDistance = locations.dropLast(1).withIndex().sumByDouble { (i, location) ->
      val next = locations[i + 1]
      return@sumByDouble location.distanceTo(next)
    }

    return (totalDistance / 1000).toInt() / minKilometers * pointsPerTime
  }
}
