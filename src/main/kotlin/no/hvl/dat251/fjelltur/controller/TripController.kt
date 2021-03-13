package no.hvl.dat251.fjelltur.controller

import no.hvl.dat251.fjelltur.API_VERSION_1
import no.hvl.dat251.fjelltur.dto.GPSLocationRequest
import no.hvl.dat251.fjelltur.dto.TripId
import no.hvl.dat251.fjelltur.dto.TripIdOnlyResponse
import no.hvl.dat251.fjelltur.dto.TripResponse
import no.hvl.dat251.fjelltur.dto.TripStartRequest
import no.hvl.dat251.fjelltur.dto.toResponse
import no.hvl.dat251.fjelltur.dto.toTripIdOnlyResponse
import no.hvl.dat251.fjelltur.service.TripService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("$API_VERSION_1/trip")
class TripController(@Autowired val tripService: TripService) {

  @PostMapping("/start")
  fun startTrip(@Valid @RequestBody request: TripStartRequest): TripIdOnlyResponse {
    return tripService.startTrip(request).toTripIdOnlyResponse()
  }

  @PutMapping("/{id}/update")
  fun updateTripCoordinate(@PathVariable id: TripId, @Valid @RequestBody request: GPSLocationRequest) {
    val trip = tripService.findTrip(id)
    tripService.addGPSLocation(trip, request.toGPSLocation())
  }

  @PostMapping("/{id}/stop")
  fun endTrip(@PathVariable id: TripId): TripResponse {
    val trip = tripService.findTrip(id)
    return tripService.endTrip(trip).toResponse()
  }

  @GetMapping("/{id}")
  fun getTrip(@PathVariable id: TripId): TripResponse {
    val trip = tripService.findTrip(id)
    return trip.toResponse()
  }
}
