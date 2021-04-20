package no.hvl.dat251.fjelltur.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class GPSLocationTest {

  @Test
  fun `calculate correct distance of coordinates`() {
    val coordinateOne = createCoordinate(60.3741927, 5.3350323) // kiwi :,)
    val coordinateTwo = createCoordinate(60.3757513, 5.3344057) // u-matkroken :,)
    assertEquals(176.68, coordinateOne.distanceTo(coordinateTwo), 0.1)
  }

  @Test
  fun `calculate correct distance of coordinates transitivity`() {
    val coordinateOne = createCoordinate(60.3741927, 5.3350323) // kiwi :,)
    val coordinateTwo = createCoordinate(60.3757513, 5.3344057) // u-matkroken :,)
    assertEquals(coordinateTwo.distanceTo(coordinateOne), coordinateOne.distanceTo(coordinateTwo), 0.0)
  }

  @Test
  fun `calculate correct distance of same coordinates`() {
    val coordinateOne = createCoordinate(60.3741927, 5.3350323) // kiwi :,)
    assertEquals(0.0, coordinateOne.distanceTo(coordinateOne), 0.0)
  }

  companion object {

    fun createCoordinate(
      latitude: Double = 0.0,
      longitude: Double = 0.0,
      accuracy: Double = 0.0,
      recordedAt: OffsetDateTime = OffsetDateTime.now()
    ): GPSLocation {
      return GPSLocation().also {
        it.longitude = longitude
        it.latitude = latitude
        it.accuracy = accuracy
        it.recordedAt = recordedAt
      }
    }
  }
}
