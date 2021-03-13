package no.hvl.dat251.fjelltur.service.impl

import no.hvl.dat251.fjelltur.dto.AccountId
import no.hvl.dat251.fjelltur.dto.TripId
import no.hvl.dat251.fjelltur.dto.TripStartRequest
import no.hvl.dat251.fjelltur.exception.TripNotFoundException
import no.hvl.dat251.fjelltur.exception.TripNotOngoingException
import no.hvl.dat251.fjelltur.model.GPSLocation
import no.hvl.dat251.fjelltur.model.Trip
import no.hvl.dat251.fjelltur.repository.TripRepository
import no.hvl.dat251.fjelltur.service.AccountService
import no.hvl.dat251.fjelltur.service.TripService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TripServiceImpl(
  @Autowired val tripRepository: TripRepository,
  @Autowired val accountService: AccountService
) : TripService {

  override fun startTrip(request: TripStartRequest): Trip {
    val trip = Trip()
    trip.participants.addAll(request.participants.map { accountService.getAccountByUid(AccountId(it)) })
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
}
