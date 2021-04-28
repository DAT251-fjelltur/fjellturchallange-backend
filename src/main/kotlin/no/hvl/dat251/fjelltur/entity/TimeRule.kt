package no.hvl.dat251.fjelltur.entity

import no.hvl.dat251.fjelltur.dto.TIME_RULE
import java.time.Duration
import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue(TIME_RULE)
class TimeRule : Rule() {
  @field:Column
  var minimumMinutes: Int? = null

  override fun calculatePoints(trip: Trip): Int {

    val minMinutes = requireNotNull(minimumMinutes) { "No minimum time in minutes found" }
    val pointsPerTime = requireNotNull(basicPoints) { "No basicPoints found" }

    val begin = trip.locations.first().recordedAt
    val end = trip.locations.last().recordedAt

    val duration = Duration.between(begin, end)

    return (duration.toMinutes().toInt() / minMinutes) * pointsPerTime
  }
}
