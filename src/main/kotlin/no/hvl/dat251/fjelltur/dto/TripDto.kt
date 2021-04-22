package no.hvl.dat251.fjelltur.dto

import no.hvl.dat251.fjelltur.entity.GPSLocation
import no.hvl.dat251.fjelltur.entity.Trip
import java.math.BigDecimal
import java.time.Duration
import java.time.OffsetDateTime
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Digits
import javax.validation.constraints.Min

/**
 *
 */
data class GPSLocationRequest(
  @DecimalMin("-90")
  @DecimalMax("90")
  @Digits(integer = 2, fraction = 6)
  val latitude: BigDecimal,

  @DecimalMin("-180")
  @DecimalMax("180")
  @Digits(integer = 3, fraction = 6)
  val longitude: BigDecimal,
  /**
   * The estimated horizontal accuracy of this location, radial, in meters.
   */
  @DecimalMin("0")
  val accuracy: BigDecimal
) {

  fun toGPSLocation(): GPSLocation {
    return GPSLocation().also {
      it.accuracy = accuracy.toDouble()
      it.latitude = latitude.toDouble()
      it.longitude = longitude.toDouble()
      it.recordedAt = OffsetDateTime.now()
    }
  }
}

data class GPSLocationResponse(
  val latitude: Double,
  val longitude: Double,
  val accuracy: Double,
  val recordedAt: OffsetDateTime
)

inline class TripId(val id: String)

data class TripStartRequest(val startLocation: GPSLocationRequest)

data class TripEndRequest(val id: TripId, val endLocation: GPSLocationRequest)

data class TripResponse(
  val tripId: String,
  val locations: List<GPSLocationResponse>,
  val ongoing: Boolean
)

fun GPSLocation.toResponse(): GPSLocationResponse {
  return GPSLocationResponse(latitude, longitude, accuracy, recordedAt)
}

fun Trip.toResponse(): TripResponse {
  return TripResponse(
    id.id,
    locations.map { it.toResponse() },
    ongoing
  )
}

data class TripIdOnlyResponse(val id: String?)

fun Trip?.toTripIdOnlyResponse(): TripIdOnlyResponse {
  return TripIdOnlyResponse(this?.id?.id)
}

data class TripDurationResponse(@Min(0) val seconds: Long)

data class TripDistanceResponse(@Min(0) val meters: Int)

fun Duration.toTripDuration() = TripDurationResponse(toSeconds())
