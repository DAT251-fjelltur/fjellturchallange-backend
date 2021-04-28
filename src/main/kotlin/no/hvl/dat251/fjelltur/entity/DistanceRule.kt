package no.hvl.dat251.fjelltur.entity

import no.hvl.dat251.fjelltur.dto.DISTANCE_RULE
import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue(DISTANCE_RULE)
class DistanceRule : Rule() {

  @field:Column
  var minKilometers: Int? = 0

  override fun calculatePoints(trip: Trip): Int {
    val minKilometers = requireNotNull(minKilometers) { "No minimum distance in kilometers found" }
    val pointsPerTime = requireNotNull(basicPoints) { "No basicPoints found" }

    val tripDistanceKm = trip.calculateDistance() / 1000

    return tripDistanceKm / minKilometers * pointsPerTime
  }
}
