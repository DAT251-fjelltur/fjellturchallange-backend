package no.hvl.dat251.fjelltur.model

import no.hvl.dat251.fjelltur.dto.TIMERULE
import java.time.Duration
import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue(TIMERULE)
class TimeRule : Rule() {
  @field:Column
  var minimumMinutes: Int? = null

  override fun calculatePoints(trip: Trip): Int {
    // invariant: trip is closed
    require(!trip.ongoing) { "Cannot calculate the points of an ongoing trip" }

    val minMinutes = requireNotNull(minimumMinutes) { "No minimum time in minutes found" }
    val pointsPerTime = requireNotNull(basicPoints) { "No basicPoints found" }

    val begin = trip.locations.first().recordedAt
    val end = trip.locations.last().recordedAt

    require(begin !== end)

    val duration = Duration.between(begin, end)

    return (duration.toMinutes().toInt() / minMinutes) * pointsPerTime
  }
}
