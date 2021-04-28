package no.hvl.dat251.fjelltur.entity

import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

@Entity
class GPSLocation {

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

  /**
   * @return Distance from this location to the [other] locations in meters
   */
  fun distanceTo(other: GPSLocation): Double {
    return if (latitude == other.latitude && longitude == other.longitude) {
      0.0
    } else {
      val theta = longitude - other.longitude
      val thislatRan = Math.toRadians(latitude)
      val otherLatRan = Math.toRadians(other.latitude)
      var dist = sin(thislatRan) * sin(otherLatRan) + cos(thislatRan) * cos(otherLatRan) * cos(Math.toRadians(theta))
      dist = acos(dist)
      dist = Math.toDegrees(dist)
      dist *= 60 * 1.1515 * 1.609344 * 1000
      dist
    }
  }

  fun equalsIgnoreTime(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as GPSLocation

    if (id != other.id) return false
    if (latitude != other.latitude) return false
    if (longitude != other.longitude) return false
    if (accuracy != other.accuracy) return false

    return true
  }

  override fun equals(other: Any?): Boolean {
    if (equalsIgnoreTime(other)) {
      other as GPSLocation
      return recordedAt == other.recordedAt
    }
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

  override fun toString(): String {
    return "GPSLocation(id=$id, latitude=$latitude, longitude=$longitude, accuracy=$accuracy, recordedAt=$recordedAt)"
  }
}
