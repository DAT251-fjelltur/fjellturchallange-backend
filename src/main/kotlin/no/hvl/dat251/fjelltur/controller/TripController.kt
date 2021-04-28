package no.hvl.dat251.fjelltur.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import no.hvl.dat251.fjelltur.ADMIN_ROLE
import no.hvl.dat251.fjelltur.API_VERSION_1
import no.hvl.dat251.fjelltur.GET_TRIP_OF_OTHER_USER
import no.hvl.dat251.fjelltur.dto.AccountId
import no.hvl.dat251.fjelltur.dto.GPSLocationRequest
import no.hvl.dat251.fjelltur.dto.TripDistanceResponse
import no.hvl.dat251.fjelltur.dto.TripDurationResponse
import no.hvl.dat251.fjelltur.dto.TripId
import no.hvl.dat251.fjelltur.dto.TripIdOnlyResponse
import no.hvl.dat251.fjelltur.dto.TripResponse
import no.hvl.dat251.fjelltur.dto.TripScoreResponse
import no.hvl.dat251.fjelltur.dto.toResponse
import no.hvl.dat251.fjelltur.dto.toTripDuration
import no.hvl.dat251.fjelltur.dto.toTripIdOnlyResponse
import no.hvl.dat251.fjelltur.service.AccountService
import no.hvl.dat251.fjelltur.service.TripService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
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
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody

@RestController
@RequestMapping(
  "$API_VERSION_1/trip",
  produces = [APPLICATION_JSON_VALUE]
)
class TripController(
  @Autowired val tripService: TripService,
  @Autowired val accountService: AccountService
) {

  @Operation(
    requestBody = SwaggerRequestBody(description = "Start location of the user"),
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(responseCode = "422", description = "User is already on a trip", content = [Content()]),
    ]
  )
  @PostMapping("/start", consumes = [APPLICATION_JSON_VALUE])
  fun startTrip(@Valid @RequestBody request: GPSLocationRequest): TripIdOnlyResponse {
    return tripService.startTrip(request).toTripIdOnlyResponse()
  }

  @Operation(
    summary = "Update your position",
    requestBody = SwaggerRequestBody(description = "Current location of the user"),
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(responseCode = "404", description = "No current trip", content = [Content()]),
    ]
  )
  @PutMapping("/update", consumes = [APPLICATION_JSON_VALUE])
  fun updateTripCoordinate(@Valid @RequestBody request: GPSLocationRequest) {
    val trip = tripService.currentTrip()
    tripService.addGPSLocation(trip, request.toGPSLocation())
  }

  @Operation(
    summary = "Stop your currently ongoing trip, if any",
    requestBody = SwaggerRequestBody(description = "The final location of the trip"),
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(responseCode = "404", description = "No current trip", content = [Content()]),
    ]
  )
  @PutMapping("/stop", consumes = [APPLICATION_JSON_VALUE])
  fun endTrip(@Valid @RequestBody request: GPSLocationRequest): TripResponse {
    val trip = tripService.currentTrip()
    val updatedTrip = tripService.addGPSLocation(trip, request.toGPSLocation())
    return tripService.endTrip(updatedTrip).toResponse()
  }

  @Operation(
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(responseCode = "404", description = "No trip with the given id", content = [Content()]),
    ]
  )
  @GetMapping("/{id}/info")
  fun getTrip(@PathVariable id: TripId): TripResponse {
    return tripService.findTrip(id).toResponse()
  }

  @Operation(
    summary = "How many points this trip is worth",
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(responseCode = "404", description = "No trip with the given id", content = [Content()]),
    ]
  )
  @GetMapping("/{id}/score")
  fun tripScore(@PathVariable id: TripId): TripScoreResponse {
    val (rule, score) = tripService.tripScore(tripService.findTrip(id))
    return TripScoreResponse(rule?.name, score.toFloat())
  }

  @Operation(
    summary = "How long the trip has lasted, live updated if trip is still ongoing",
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(responseCode = "404", description = "No trip with the given id", content = [Content()]),
    ]
  )
  @GetMapping("/{id}/duration")
  fun lengthOfTrip(@PathVariable id: TripId): TripDurationResponse {
    val trip = tripService.findTrip(id)
    return tripService.calculateTripDuration(trip).toTripDuration()
  }

  @Operation(
    summary = "How far the account have traveled, live updated if trip is still ongoing",
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(responseCode = "404", description = "No trip with the given id", content = [Content()]),
    ]
  )
  @GetMapping("/{id}/distance")
  fun distanceOfTrip(@PathVariable id: TripId): TripDistanceResponse {
    val trip = tripService.findTrip(id)
    return TripDistanceResponse(tripService.calculateTripDistance(trip))
  }

  @Operation(
    summary = "Currently ongoing trip of a user",
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(responseCode = "404", description = "No account found with the given id or current trip", content = [Content()]),
      ApiResponse(responseCode = "403", description = "You are not allowed to current trip of other", content = [Content()]),
    ]
  )
  @PreAuthorize("hasAuthority('$GET_TRIP_OF_OTHER_USER') or hasRole('$ADMIN_ROLE')")
  @GetMapping("/current_other")
  fun getCurrentTrip(@RequestParam("account_id") id: AccountId): TripResponse {
    val account = accountService.getAccountByUid(id)
    return tripService.currentTrip(account).toResponse()
  }

  @Operation(
    summary = "Your currently ongoing trip",
    responses = [
      ApiResponse(responseCode = "200"),
      ApiResponse(responseCode = "404", description = "No current trip", content = [Content()]),
    ]
  )
  @GetMapping("/current")
  fun getCurrentTrip(): TripResponse {
    return tripService.currentTrip(accountService.getCurrentAccount()).toResponse()
  }

  @GetMapping("/previous_trips")
  fun getPreviousTrips(@RequestParam("id") id: AccountId): List<TripResponse> {
    return tripService.findPreviousTrips(accountService.getCurrentAccount()).map { it.toResponse() }
  }
}
