package no.hvl.dat251.fjelltur.service.impl

import no.hvl.dat251.fjelltur.dto.AccountId
import no.hvl.dat251.fjelltur.dto.TripId
import no.hvl.dat251.fjelltur.dto.TripStartRequest
import no.hvl.dat251.fjelltur.exception.AccountAlreadyOnTripException
import no.hvl.dat251.fjelltur.exception.TripNotFoundException
import no.hvl.dat251.fjelltur.exception.TripNotOngoingException
import no.hvl.dat251.fjelltur.model.Account
import no.hvl.dat251.fjelltur.model.GPSLocation
import no.hvl.dat251.fjelltur.model.Trip
import no.hvl.dat251.fjelltur.repository.TripRepository
import no.hvl.dat251.fjelltur.service.AccountService
import no.hvl.dat251.fjelltur.service.TripService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.OffsetDateTime

@Service
class TripServiceImpl(
  @Autowired val tripRepository: TripRepository,
  @Autowired val accountService: AccountService
) : TripService {

  override fun startTrip(request: TripStartRequest): Trip {
    val trip = Trip()

    // TODO make sure either the user in on the participant map or has the permission to start a trip without themself
    val list = request.participants.map { accountService.getAccountByUid(AccountId(it)) }
    for (account in list) {
      if (tripRepository.existsTripByParticipantsContainsAndOngoingTrue(account)) {
        throw AccountAlreadyOnTripException(account)
      }
    }
    trip.participants.addAll(list)

    trip.locations.add(request.startLocation.toGPSLocation())
    return tripRepository.saveAndFlush(trip)
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

  override fun currentTrip(): Trip? {
    return currentTrip(accountService.getCurrentAccount())
  }

  override fun currentTrip(account: Account): Trip? {
    val trips = tripRepository.findAllByParticipantsContainsAndOngoingTrue(account)
    if (trips.size > 1) {
      TODO("more than one trip should be handled")
    }
    return trips.firstOrNull()
  }
}
