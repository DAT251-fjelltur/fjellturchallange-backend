package no.hvl.dat251.fjelltur.entity.rule

import no.hvl.dat251.fjelltur.entity.GPSLocationTest
import no.hvl.dat251.fjelltur.entity.Trip
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MountainRuleTest {

  @Test
  fun `get point for walking up and down `() {
    val coordinateOne = GPSLocationTest.createCoordinate(60.3605958614197, 5.31952347862378) // løvstakken
    val coordinateTwo = GPSLocationTest.createCoordinate(60.0, 5.0) // langt unna løvtakken
    val coordinateThree = GPSLocationTest.createCoordinate(60.0, 5.0)
    val trip = Trip()
    trip.locations = mutableListOf(coordinateTwo, coordinateOne, coordinateThree)

    val rule = MountainRule()
    rule.minMetersTraveled = 2200 // meters
    rule.summitRadiusMeters = 150
    rule.summit = coordinateOne
    rule.basicPoints = 2

    assertEquals(rule.basicPoints, rule.calculatePoints(trip))
  }

  @Test
  fun `does not get point for walking just up`() {
    val coordinateOne = GPSLocationTest.createCoordinate(60.3605958614197, 5.31952347862378) // løvstakken
    val coordinateTwo = GPSLocationTest.createCoordinate(60.0, 5.0) // langt unna løvtakken
    val trip = Trip()
    trip.locations = mutableListOf(coordinateTwo, coordinateOne)

    val rule = MountainRule()
    rule.minMetersTraveled = 2200 // meters
    rule.summitRadiusMeters = 150
    rule.summit = coordinateOne
    rule.basicPoints = 2

    assertEquals(0, rule.calculatePoints(trip))
  }

  @Test
  fun `does not get point for walking just down`() {
    val coordinateOne = GPSLocationTest.createCoordinate(60.3605958614197, 5.31952347862378) // løvstakken
    val coordinateTwo = GPSLocationTest.createCoordinate(60.0, 5.0) // langt unna løvtakken
    val trip = Trip()
    trip.locations = mutableListOf(coordinateOne, coordinateTwo)

    val rule = MountainRule()
    rule.minMetersTraveled = 2200 // meters
    rule.summitRadiusMeters = 150
    rule.summit = coordinateOne
    rule.basicPoints = 2

    assertEquals(0, rule.calculatePoints(trip))
  }

  @Test
  fun `does not get point with not within summit`() {
    val coordinateOne = GPSLocationTest.createCoordinate(59.3605958614197, 5.31952347862378) // ikke løvstakken
    val coordinateTwo = GPSLocationTest.createCoordinate(60.0, 5.0) // langt unna løvtakken
    val trip = Trip()
    trip.locations = mutableListOf(coordinateOne, coordinateTwo)

    val rule = MountainRule()
    rule.minMetersTraveled = 2200 // meters
    rule.summitRadiusMeters = 150
    rule.summit = coordinateOne
    rule.basicPoints = 2

    assertEquals(0, rule.calculatePoints(trip))
  }
}
