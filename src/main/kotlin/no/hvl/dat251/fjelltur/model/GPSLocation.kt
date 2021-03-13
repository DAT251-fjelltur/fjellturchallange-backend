package no.hvl.dat251.fjelltur.model

import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class GPSLocation() {

  @field:Id
  @field:GeneratedValue
  var id: Long = -1

  @field:Column(nullable = false)
  var latitude: Double = Double.NaN

  @field:Column(nullable = false)
  var longitude: Double = Double.NaN

  @field:Column(nullable = false)
  var accuracy: Double = Double.NaN

  @field:Column(nullable = false)
  lateinit var recordedAt: OffsetDateTime

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as GPSLocation

    if (id != other.id) return false
    if (latitude != other.latitude) return false
    if (longitude != other.longitude) return false
    if (accuracy != other.accuracy) return false
    if (recordedAt != other.recordedAt) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + latitude.hashCode()
    result = 31 * result + longitude.hashCode()
    result = 31 * result + accuracy.hashCode()
    result = 31 * result + recordedAt.hashCode()
    return result
  }
}
