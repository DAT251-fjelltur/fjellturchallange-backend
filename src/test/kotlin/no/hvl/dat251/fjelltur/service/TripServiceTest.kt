package no.hvl.dat251.fjelltur.service

import no.hvl.dat251.fjelltur.dto.GPSLocationRequest
import no.hvl.dat251.fjelltur.dto.TripId
import no.hvl.dat251.fjelltur.dto.TripStartRequest
import no.hvl.dat251.fjelltur.exception.AccountAlreadyOnTripException
import no.hvl.dat251.fjelltur.exception.TooManyOngoingTripsException
import no.hvl.dat251.fjelltur.exception.TripNotFoundException
import no.hvl.dat251.fjelltur.model.GPSLocation
import no.hvl.dat251.fjelltur.model.Trip
import no.hvl.dat251.fjelltur.repository.TripRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@SpringBootTest
class TripServiceTest {

  @Autowired
  private lateinit var tripService: TripService

  @Autowired
  private lateinit var tripRepository: TripRepository

  @Autowired
  private lateinit var accountService: AccountService

  @WithMockUser(username = "TripServiceTest_startTrip")
  private fun startTrip(
    lat: Double = 0.0,
    long: Double = 0.0,
    acc: Double = 0.0
  ): Trip {
    return tripService.startTrip(
      TripStartRequest(
        GPSLocationRequest(
          lat.toBigDecimal(),
          long.toBigDecimal(),
          acc.toBigDecimal()
        )
      )
    )
  }

  @WithMockUser(username = "TripServiceTest_createTrip")
  @Test
  fun `create trip for user without an ongoing trip`() {
    val trip = startTrip()

    assertEquals(1, trip.locations.size)
    assertEquals(accountService.getCurrentAccount(), trip.account)
  }

  @WithMockUser(username = "TripServiceTest_createTriptwice")
  @Test
  fun `create trip for user when they are on an ongoing trip throws`() {
    startTrip()
    assertThrows<AccountAlreadyOnTripException> { startTrip() }
  }

  @WithMockUser(username = "TripServiceTest_stopTrip")
  @Test
  fun `stop trip stops it`() {
    val trip = startTrip()
    assertTrue(trip.ongoing)
    val stoppedTrip = tripService.endTrip(trip)
    assertEquals(trip.id, stoppedTrip.id)
    assertFalse(stoppedTrip.ongoing)
  }

  @WithMockUser(username = "TripServiceTest_stopTrip")
  @Test
  fun `can start another trip after stopping it`() {
    val trip = startTrip()
    assertTrue(trip.ongoing)
    val stoppedTrip = tripService.endTrip(trip)
    assertEquals(trip.id, stoppedTrip.id)
    assertFalse(stoppedTrip.ongoing)

    val newTrip = startTrip()
    assertNotEquals(trip.id, newTrip.id)
    val oldTrip = assertNotNull(tripService.findTripOrNull(trip.id))
    assertEquals(trip.id, oldTrip.id)
  }

  @WithMockUser(username = "TripServiceTest_currTrip")
  @Test
  fun `Get trip of current user`() {
    assertNull(tripService.currentTrip())
    val trip = startTrip()
    val currTrip = assertNotNull(tripService.currentTrip())
    assertEquals(trip.id, currTrip.id)
  }

  @WithMockUser(username = "TripServiceTest_currentTripTooManyTrips")
  @Test
  fun `current trip when multiple trips ongoing throws`() {
    startTrip()

    val anotherTrip = Trip().also {
      it.account = accountService.getCurrentAccount()
    }
    tripRepository.saveAndFlush(anotherTrip)

    assertThrows<TooManyOngoingTripsException> { tripService.currentTrip() }
  }

  @WithMockUser(username = "TripServiceTest_findExistingTrip")
  @Test
  fun `find trip`() {
    val trip = startTrip()
    val foundTrip = assertNotNull(tripService.findTripOrNull(trip.id))
    assertEquals(trip.id, foundTrip.id)
  }

  @WithMockUser(username = "TripServiceTest_findOrNullNonExistingTrip")
  @Test
  fun `find or null non existing trip is null`() {
    assertNull(tripService.findTripOrNull(TripId("ugfygesyfi")))
  }

  @WithMockUser(username = "TripServiceTest_findNonExistingTrip")
  @Test
  fun `find non existing trip throws`() {
    assertThrows<TripNotFoundException> { tripService.findTrip(TripId("ugfygesyfi")) }
  }

  @WithMockUser(username = "TripServiceTest_tripDurationEnded")
  @Test
  fun `calculate trip duration Ended`() {
    val trip = startTrip()
    val startTime = trip.locations.first().recordedAt
    trip.locations.add(
      GPSLocation().also {
        it.recordedAt = startTime.plusSeconds(1L)
      }
    )
    trip.locations.add(
      GPSLocation().also {
        it.recordedAt = startTime.plusSeconds(2L)
      }
    )
    trip.ongoing = false
    assertEquals(Duration.of(2L, ChronoUnit.SECONDS), tripService.calculateTripDuration(trip))
  }

  @WithMockUser(username = "TripServiceTest_tripDurationOngoing")
  @Test
  fun `calculate trip duration ongoing`() {
    val trip = startTrip()
    assertNotEquals(Duration.ZERO, tripService.calculateTripDuration(trip))
  }
}
