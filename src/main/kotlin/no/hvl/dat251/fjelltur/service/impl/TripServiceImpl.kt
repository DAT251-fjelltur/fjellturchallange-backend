package no.hvl.dat251.fjelltur.service.impl

import no.hvl.dat251.fjelltur.dto.GPSLocationRequest
import no.hvl.dat251.fjelltur.dto.TripId
import no.hvl.dat251.fjelltur.entity.Account
import no.hvl.dat251.fjelltur.entity.GPSLocation
import no.hvl.dat251.fjelltur.entity.Rule
import no.hvl.dat251.fjelltur.entity.Trip
import no.hvl.dat251.fjelltur.exception.AccountAlreadyOnTripException
import no.hvl.dat251.fjelltur.exception.NoCurrentTripException
import no.hvl.dat251.fjelltur.exception.NoRulesDefinedException
import no.hvl.dat251.fjelltur.exception.TripNotFoundException
import no.hvl.dat251.fjelltur.exception.TripNotOngoingException
import no.hvl.dat251.fjelltur.repository.TripRepository
import no.hvl.dat251.fjelltur.service.AccountService
import no.hvl.dat251.fjelltur.service.RuleService
import no.hvl.dat251.fjelltur.service.TripService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.OffsetDateTime

@Service
class TripServiceImpl(
  @Autowired val tripRepository: TripRepository,
  @Autowired val accountService: AccountService,
  @Autowired val ruleService: RuleService,
) : TripService {

  override fun startTrip(request: GPSLocationRequest): Trip {
    val account = accountService.getCurrentAccount()

    synchronized(SYNC_OBJECT) {
      if (tripRepository.existsTripByAccountIsAndOngoingTrue(account)) {
        throw AccountAlreadyOnTripException(account)
      }

      val trip = Trip()
      trip.account = account
      trip.locations.add(request.toGPSLocation())
      return tripRepository.saveAndFlush(trip)
    }
  }

  override fun endTrip(trip: Trip): Trip {
    if (!trip.ongoing) {
      throw TripNotOngoingException(trip.id)
    }
    trip.ongoing = false
    return tripRepository.saveAndFlush(trip)
  }

  override fun addGPSLocation(trip: Trip, location: GPSLocation): Trip {
    if (!trip.ongoing) {
      throw TripNotOngoingException(trip.id)
    }
    trip.locations.add(location)
    return tripRepository.saveAndFlush(trip)
  }

  override fun findTrip(id: TripId): Trip {
    return findTripOrNull(id) ?: throw TripNotFoundException(id)
  }

  override fun findTripOrNull(id: TripId): Trip? {
    return tripRepository.findByIdOrNull(id.id)
  }

  override fun calculateTripDuration(trip: Trip): Duration {
    val locations = trip.locations
    val first = locations.first().recordedAt
    val last = if (trip.ongoing) OffsetDateTime.now() else locations.last().recordedAt
    return Duration.between(first, last).abs()
  }

  override fun calculateTripDistance(trip: Trip): Int {
    return trip.calculateDistance()
  }

  override fun currentTripOrNull(): Trip? {
    return currentTripOrNull(accountService.getCurrentAccount())
  }

  override fun currentTripOrNull(account: Account): Trip? {
    return tripRepository.findAllByAccountIsAndOngoingTrue(account).firstOrNull()
  }

  override fun currentTrip(): Trip {
    return currentTrip(accountService.getCurrentAccount())
  }

  override fun currentTrip(account: Account): Trip {
    return currentTripOrNull(account) ?: throw NoCurrentTripException(account)
  }

  override fun tripScore(trip: Trip): Pair<Rule, Int> {

    val rules = ruleService.findAll(Pageable.unpaged())
    if (rules.isEmpty) {
      throw NoRulesDefinedException()
    }
    val optional = rules.stream().map { it to it.calculatePoints(trip) }.max { (_, i), (_, j) -> i.compareTo(j) }
    if (optional.isEmpty) {
      throw IllegalStateException("Failed to find any applicable rules")
    }
    return optional.get()
  }

  companion object {
    val SYNC_OBJECT = Any()
  }
}
