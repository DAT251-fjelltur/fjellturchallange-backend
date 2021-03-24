package no.hvl.dat251.fjelltur.controller

import no.hvl.dat251.fjelltur.ADMIN_ROLE
import no.hvl.dat251.fjelltur.API_VERSION_1
import no.hvl.dat251.fjelltur.GET_TRIP_OF_OTHER_USER
import no.hvl.dat251.fjelltur.dto.AccountId
import no.hvl.dat251.fjelltur.dto.GPSLocationRequest
import no.hvl.dat251.fjelltur.dto.TripDurationResponse
import no.hvl.dat251.fjelltur.dto.TripId
import no.hvl.dat251.fjelltur.dto.TripIdOnlyResponse
import no.hvl.dat251.fjelltur.dto.TripResponse
import no.hvl.dat251.fjelltur.dto.TripStartRequest
import no.hvl.dat251.fjelltur.dto.toResponse
import no.hvl.dat251.fjelltur.dto.toTripDuration
import no.hvl.dat251.fjelltur.dto.toTripIdOnlyResponse
import no.hvl.dat251.fjelltur.service.AccountService
import no.hvl.dat251.fjelltur.service.TripService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("$API_VERSION_1/trip")
class TripController(
  @Autowired val tripService: TripService,
  @Autowired val accountService: AccountService
) {

  @PostMapping("/start")
  fun startTrip(@Valid @RequestBody request: TripStartRequest): TripIdOnlyResponse {
    return tripService.startTrip(request).toTripIdOnlyResponse()
  }

  @PutMapping("/update")
  fun updateTripCoordinate(@Valid @RequestBody request: GPSLocationRequest) {
    val trip = tripService.currentTrip(accountService.getCurrentAccount())
    tripService.addGPSLocation(trip, request.toGPSLocation())
  }

  @PutMapping("/stop")
  fun endTrip(@Valid @RequestBody request: GPSLocationRequest): TripResponse {
    val trip = tripService.currentTrip(accountService.getCurrentAccount())
    val updatedTrip = tripService.addGPSLocation(trip, request.toGPSLocation())
    return tripService.endTrip(updatedTrip).toResponse()
  }

  @GetMapping("/{id}/info")
  fun getTrip(@PathVariable id: TripId): TripResponse {
    return tripService.findTrip(id).toResponse()
  }

  @GetMapping("/{id}/duration")
  fun lengthOfTrip(@PathVariable id: TripId): TripDurationResponse {
    val trip = tripService.findTrip(id)
    return tripService.calculateTripDuration(trip).toTripDuration()
  }

  @PreAuthorize("hasAuthority('$GET_TRIP_OF_OTHER_USER') or hasRole('$ADMIN_ROLE')")
  @GetMapping("/current_other")
  fun getCurrentTrip(@RequestParam("id") id: AccountId): TripResponse {
    val account = accountService.getAccountByUid(id)
    return tripService.currentTrip(account).toResponse()
  }

  @GetMapping("/current")
  fun getCurrentTrip(): TripResponse {
    return tripService.currentTrip(accountService.getCurrentAccount()).toResponse()
  }
}
