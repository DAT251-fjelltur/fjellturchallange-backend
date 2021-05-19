package no.hvl.dat251.fjelltur.entity

import no.hvl.dat251.fjelltur.entity.GPSLocationTest.Companion.createCoordinate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TripTest {

  @Test
  fun `distance of trip is zero when it contains no locations`() {
    assertEquals(0, Trip().calculateDistance())
  }

  @Test
  fun `distance of trip is zero when it contains a single location`() {
    val coordinateOne = createCoordinate(60.3741927, 5.3350323)
    val trip = Trip()
    trip.locations.add(coordinateOne)

    assertEquals(0, trip.calculateDistance())
  }

  @Test
  fun `distance of trip is equal to distance from first to last when trip contains only two locations`() {

    val coordinateOne = createCoordinate(60.3741927, 5.3350323)
    val coordinateTwo = createCoordinate(60.3757513, 5.3344057)

    val trip = Trip()
    trip.locations = mutableListOf(coordinateOne, coordinateTwo)

    assertEquals(coordinateOne.distanceTo(coordinateTwo).toInt(), trip.calculateDistance())
  }

  @Test
  fun `distance of trip is equal to the from first to second plus second to third, assuming trips has three locations`() {

    val coordinateOne = createCoordinate(60.3741927, 5.3350323)
    val coordinateTwo = createCoordinate(60.3757513, 5.3344057)

    val trip = Trip()
    trip.locations = mutableListOf(coordinateOne, coordinateTwo, coordinateOne)

    assertEquals((coordinateOne.distanceTo(coordinateTwo) * 2).toInt(), trip.calculateDistance())
  }
}
