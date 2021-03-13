package no.hvl.dat251.fjelltur.service

import no.hvl.dat251.fjelltur.dto.TripId
import no.hvl.dat251.fjelltur.dto.TripStartRequest
import no.hvl.dat251.fjelltur.exception.AccountNotFoundException
import no.hvl.dat251.fjelltur.exception.TripNotFoundException
import no.hvl.dat251.fjelltur.exception.TripNotOngoingException
import no.hvl.dat251.fjelltur.model.Account
import no.hvl.dat251.fjelltur.model.GPSLocation
import no.hvl.dat251.fjelltur.model.Trip
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Duration

interface TripService {

  @ExceptionHandler(AccountNotFoundException::class)
  fun startTrip(request: TripStartRequest): Trip

  @ExceptionHandler(TripNotOngoingException::class)
  fun endTrip(trip: Trip): Trip

  @ExceptionHandler(TripNotOngoingException::class)
  fun addGPSLocation(trip: Trip, location: GPSLocation): Trip

  @ExceptionHandler(TripNotFoundException::class)
  fun findTrip(id: TripId): Trip

  fun findTripOrNull(id: TripId): Trip?

  /**
   * Find the current trip the logged in user is on, if any
   */
  fun currentTrip(): Trip?

  /**
   * Find the current trip the given user is on, if any
   */
  fun currentTrip(id: Account): Trip?

  /**
   * Calculate the current length of the trip
   */
  fun calculateTripDuration(trip: Trip): Duration
}
