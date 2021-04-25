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

  var radiusMeters: Int? = 0

  override fun calculatePoints(trip: Trip): Int {

    val summit = requireNotNull(summit) { "No summit found" }
    val radius = requireNotNull(radiusMeters) { "No radius found" }
    val basicPoints = requireNotNull(basicPoints) { "No basicPoints found" }

    for (location in trip.locations) {
      if (summit.distanceTo(location) <= radius) {
        return basicPoints
      }
    }
    return 0
  }
}
